package net.minecraft.world.level.levelgen.feature.structures;

import java.util.Iterator;
import java.util.Collection;
import com.google.common.collect.Lists;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.block.Rotation;
import com.google.common.collect.Queues;
import java.util.Deque;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import org.apache.logging.log4j.LogManager;
import net.minecraft.world.level.levelgen.structure.StructureFeatureIO;
import java.util.Random;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Logger;

public class JigsawPlacement {
    private static final Logger LOGGER;
    public static final StructureTemplatePools POOLS;
    
    public static void addPieces(final ResourceLocation qv, final int integer, final PieceFactory a, final ChunkGenerator<?> bxi, final StructureManager cjp, final BlockPos ew, final List<StructurePiece> list, final Random random) {
        StructureFeatureIO.bootstrap();
        new Placer(qv, integer, a, bxi, cjp, ew, list, random);
    }
    
    static {
        LOGGER = LogManager.getLogger();
        (POOLS = new StructureTemplatePools()).register(StructureTemplatePool.EMPTY);
    }
    
    static final class PieceState {
        private final PoolElementStructurePiece piece;
        private final AtomicReference<VoxelShape> free;
        private final int boundsTop;
        private final int depth;
        
        private PieceState(final PoolElementStructurePiece cip, final AtomicReference<VoxelShape> atomicReference, final int integer3, final int integer4) {
            this.piece = cip;
            this.free = atomicReference;
            this.boundsTop = integer3;
            this.depth = integer4;
        }
    }
    
    static final class Placer {
        private final int maxDepth;
        private final PieceFactory factory;
        private final ChunkGenerator<?> chunkGenerator;
        private final StructureManager structureManager;
        private final List<StructurePiece> pieces;
        private final Random random;
        private final Deque<PieceState> placing;
        
        public Placer(final ResourceLocation qv, final int integer, final PieceFactory a, final ChunkGenerator<?> bxi, final StructureManager cjp, final BlockPos ew, final List<StructurePiece> list, final Random random) {
            this.placing = (Deque<PieceState>)Queues.newArrayDeque();
            this.maxDepth = integer;
            this.factory = a;
            this.chunkGenerator = bxi;
            this.structureManager = cjp;
            this.pieces = list;
            this.random = random;
            final Rotation brg10 = Rotation.getRandom(random);
            final StructureTemplatePool cft11 = JigsawPlacement.POOLS.getPool(qv);
            final StructurePoolElement cfr12 = cft11.getRandomTemplate(random);
            final PoolElementStructurePiece cip13 = a.create(cjp, cfr12, ew, cfr12.getGroundLevelDelta(), brg10, cfr12.getBoundingBox(cjp, ew, brg10));
            final BoundingBox cic14 = cip13.getBoundingBox();
            final int integer2 = (cic14.x1 + cic14.x0) / 2;
            final int integer3 = (cic14.z1 + cic14.z0) / 2;
            final int integer4 = bxi.getFirstFreeHeight(integer2, integer3, Heightmap.Types.WORLD_SURFACE_WG);
            cip13.move(0, integer4 - (cic14.y0 + cip13.getGroundLevelDelta()), 0);
            list.add(cip13);
            if (integer <= 0) {
                return;
            }
            final int integer5 = 80;
            final AABB csc19 = new AABB(integer2 - 80, integer4 - 80, integer3 - 80, integer2 + 80 + 1, integer4 + 80 + 1, integer3 + 80 + 1);
            this.placing.addLast(new PieceState(cip13, new AtomicReference((Object)Shapes.join(Shapes.create(csc19), Shapes.create(AABB.of(cic14)), BooleanOp.ONLY_FIRST)), integer4 + 80, 0));
            while (!this.placing.isEmpty()) {
                final PieceState b20 = (PieceState)this.placing.removeFirst();
                this.tryPlacingChildren(b20.piece, b20.free, b20.boundsTop, b20.depth);
            }
        }
        
        private void tryPlacingChildren(final PoolElementStructurePiece cip, final AtomicReference<VoxelShape> atomicReference, final int integer3, final int integer4) {
            final StructurePoolElement cfr6 = cip.getElement();
            final BlockPos ew7 = cip.getPosition();
            final Rotation brg8 = cip.getRotation();
            final StructureTemplatePool.Projection a9 = cfr6.getProjection();
            final boolean boolean10 = a9 == StructureTemplatePool.Projection.RIGID;
            final AtomicReference<VoxelShape> atomicReference2 = (AtomicReference<VoxelShape>)new AtomicReference();
            final BoundingBox cic12 = cip.getBoundingBox();
            final int integer5 = cic12.y0;
            for (final StructureTemplate.StructureBlockInfo b15 : cfr6.getShuffledJigsawBlocks(this.structureManager, ew7, brg8, this.random)) {
                final Direction fb16 = b15.state.<Direction>getValue((Property<Direction>)JigsawBlock.FACING);
                final BlockPos ew8 = b15.pos;
                final BlockPos ew9 = ew8.relative(fb16);
                final int integer6 = ew8.getY() - integer5;
                int integer7 = -1;
                final StructureTemplatePool cft21 = JigsawPlacement.POOLS.getPool(new ResourceLocation(b15.nbt.getString("target_pool")));
                final StructureTemplatePool cft22 = JigsawPlacement.POOLS.getPool(cft21.getFallback());
                if (cft21 == StructureTemplatePool.INVALID || (cft21.size() == 0 && cft21 != StructureTemplatePool.EMPTY)) {
                    JigsawPlacement.LOGGER.warn("Empty or none existent pool: {}", b15.nbt.getString("target_pool"));
                }
                else {
                    final boolean boolean11 = cic12.isInside(ew9);
                    AtomicReference<VoxelShape> atomicReference3;
                    int integer8;
                    if (boolean11) {
                        atomicReference3 = atomicReference2;
                        integer8 = integer5;
                        if (atomicReference2.get() == null) {
                            atomicReference2.set(Shapes.create(AABB.of(cic12)));
                        }
                    }
                    else {
                        atomicReference3 = atomicReference;
                        integer8 = integer3;
                    }
                    final List<StructurePoolElement> list26 = (List<StructurePoolElement>)Lists.newArrayList();
                    if (integer4 != this.maxDepth) {
                        list26.addAll((Collection)cft21.getShuffledTemplates(this.random));
                    }
                    list26.addAll((Collection)cft22.getShuffledTemplates(this.random));
                Label_1101:
                    for (final StructurePoolElement cfr7 : list26) {
                        if (cfr7 == EmptyPoolElement.INSTANCE) {
                            break;
                        }
                        for (final Rotation brg9 : Rotation.getShuffled(this.random)) {
                            final List<StructureTemplate.StructureBlockInfo> list27 = cfr7.getShuffledJigsawBlocks(this.structureManager, BlockPos.ZERO, brg9, this.random);
                            final BoundingBox cic13 = cfr7.getBoundingBox(this.structureManager, BlockPos.ZERO, brg9);
                            int integer9;
                            if (cic13.getYSpan() > 16) {
                                integer9 = 0;
                            }
                            else {
                                integer9 = list27.stream().mapToInt(b -> {
                                    if (!cic13.isInside(b.pos.relative(b.state.<Direction>getValue((Property<Direction>)JigsawBlock.FACING)))) {
                                        return 0;
                                    }
                                    final ResourceLocation qv4 = new ResourceLocation(b.nbt.getString("target_pool"));
                                    final StructureTemplatePool cft5 = JigsawPlacement.POOLS.getPool(qv4);
                                    final StructureTemplatePool cft6 = JigsawPlacement.POOLS.getPool(cft5.getFallback());
                                    return Math.max(cft5.getMaxSize(this.structureManager), cft6.getMaxSize(this.structureManager));
                                }).max().orElse(0);
                            }
                            for (final StructureTemplate.StructureBlockInfo b16 : list27) {
                                if (!JigsawBlock.canAttach(b15, b16)) {
                                    continue;
                                }
                                final BlockPos ew10 = b16.pos;
                                final BlockPos ew11 = new BlockPos(ew9.getX() - ew10.getX(), ew9.getY() - ew10.getY(), ew9.getZ() - ew10.getZ());
                                final BoundingBox cic14 = cfr7.getBoundingBox(this.structureManager, ew11, brg9);
                                final int integer10 = cic14.y0;
                                final StructureTemplatePool.Projection a10 = cfr7.getProjection();
                                final boolean boolean12 = a10 == StructureTemplatePool.Projection.RIGID;
                                final int integer11 = ew10.getY();
                                final int integer12 = integer6 - integer11 + b15.state.<Direction>getValue((Property<Direction>)JigsawBlock.FACING).getStepY();
                                int integer13;
                                if (boolean10 && boolean12) {
                                    integer13 = integer5 + integer12;
                                }
                                else {
                                    if (integer7 == -1) {
                                        integer7 = this.chunkGenerator.getFirstFreeHeight(ew8.getX(), ew8.getZ(), Heightmap.Types.WORLD_SURFACE_WG);
                                    }
                                    integer13 = integer7 - integer11;
                                }
                                final int integer14 = integer13 - integer10;
                                final BoundingBox cic15 = cic14.moved(0, integer14, 0);
                                final BlockPos ew12 = ew11.offset(0, integer14, 0);
                                if (integer9 > 0) {
                                    final int integer15 = Math.max(integer9 + 1, cic15.y1 - cic15.y0);
                                    cic15.y1 = cic15.y0 + integer15;
                                }
                                if (Shapes.joinIsNotEmpty((VoxelShape)atomicReference3.get(), Shapes.create(AABB.of(cic15).deflate(0.25)), BooleanOp.ONLY_SECOND)) {
                                    continue;
                                }
                                atomicReference3.set(Shapes.joinUnoptimized((VoxelShape)atomicReference3.get(), Shapes.create(AABB.of(cic15)), BooleanOp.ONLY_FIRST));
                                final int integer15 = cip.getGroundLevelDelta();
                                int integer16;
                                if (boolean12) {
                                    integer16 = integer15 - integer12;
                                }
                                else {
                                    integer16 = cfr7.getGroundLevelDelta();
                                }
                                final PoolElementStructurePiece cip2 = this.factory.create(this.structureManager, cfr7, ew12, integer16, brg9, cic15);
                                int integer17;
                                if (boolean10) {
                                    integer17 = integer5 + integer6;
                                }
                                else if (boolean12) {
                                    integer17 = integer13 + integer11;
                                }
                                else {
                                    if (integer7 == -1) {
                                        integer7 = this.chunkGenerator.getFirstFreeHeight(ew8.getX(), ew8.getZ(), Heightmap.Types.WORLD_SURFACE_WG);
                                    }
                                    integer17 = integer7 + integer12 / 2;
                                }
                                cip.addJunction(new JigsawJunction(ew9.getX(), integer17 - integer6 + integer15, ew9.getZ(), integer12, a10));
                                cip2.addJunction(new JigsawJunction(ew8.getX(), integer17 - integer11 + integer16, ew8.getZ(), -integer12, a9));
                                this.pieces.add(cip2);
                                if (integer4 + 1 <= this.maxDepth) {
                                    this.placing.addLast(new PieceState(cip2, (AtomicReference)atomicReference3, integer8, integer4 + 1));
                                    break Label_1101;
                                }
                                break Label_1101;
                            }
                        }
                    }
                }
            }
        }
    }
    
    public interface PieceFactory {
        PoolElementStructurePiece create(final StructureManager cjp, final StructurePoolElement cfr, final BlockPos ew, final int integer, final Rotation brg, final BoundingBox cic);
    }
}
