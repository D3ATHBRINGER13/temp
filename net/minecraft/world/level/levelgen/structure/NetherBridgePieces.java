package net.minecraft.world.level.levelgen.structure;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.core.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import java.util.Iterator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.core.Direction;
import java.util.Random;
import java.util.List;

public class NetherBridgePieces {
    private static final PieceWeight[] BRIDGE_PIECE_WEIGHTS;
    private static final PieceWeight[] CASTLE_PIECE_WEIGHTS;
    
    private static NetherBridgePiece findAndCreateBridgePieceFactory(final PieceWeight n, final List<StructurePiece> list, final Random random, final int integer4, final int integer5, final int integer6, final Direction fb, final int integer8) {
        final Class<? extends NetherBridgePiece> class9 = n.pieceClass;
        NetherBridgePiece m10 = null;
        if (class9 == BridgeStraight.class) {
            m10 = BridgeStraight.createPiece(list, random, integer4, integer5, integer6, fb, integer8);
        }
        else if (class9 == BridgeCrossing.class) {
            m10 = BridgeCrossing.createPiece(list, integer4, integer5, integer6, fb, integer8);
        }
        else if (class9 == RoomCrossing.class) {
            m10 = RoomCrossing.createPiece(list, integer4, integer5, integer6, fb, integer8);
        }
        else if (class9 == StairsRoom.class) {
            m10 = StairsRoom.createPiece(list, integer4, integer5, integer6, integer8, fb);
        }
        else if (class9 == MonsterThrone.class) {
            m10 = MonsterThrone.createPiece(list, integer4, integer5, integer6, integer8, fb);
        }
        else if (class9 == CastleEntrance.class) {
            m10 = CastleEntrance.createPiece(list, random, integer4, integer5, integer6, fb, integer8);
        }
        else if (class9 == CastleSmallCorridorPiece.class) {
            m10 = CastleSmallCorridorPiece.createPiece(list, integer4, integer5, integer6, fb, integer8);
        }
        else if (class9 == CastleSmallCorridorRightTurnPiece.class) {
            m10 = CastleSmallCorridorRightTurnPiece.createPiece(list, random, integer4, integer5, integer6, fb, integer8);
        }
        else if (class9 == CastleSmallCorridorLeftTurnPiece.class) {
            m10 = CastleSmallCorridorLeftTurnPiece.createPiece(list, random, integer4, integer5, integer6, fb, integer8);
        }
        else if (class9 == CastleCorridorStairsPiece.class) {
            m10 = CastleCorridorStairsPiece.createPiece(list, integer4, integer5, integer6, fb, integer8);
        }
        else if (class9 == CastleCorridorTBalconyPiece.class) {
            m10 = CastleCorridorTBalconyPiece.createPiece(list, integer4, integer5, integer6, fb, integer8);
        }
        else if (class9 == CastleSmallCorridorCrossingPiece.class) {
            m10 = CastleSmallCorridorCrossingPiece.createPiece(list, integer4, integer5, integer6, fb, integer8);
        }
        else if (class9 == CastleStalkRoom.class) {
            m10 = CastleStalkRoom.createPiece(list, integer4, integer5, integer6, fb, integer8);
        }
        return m10;
    }
    
    static {
        BRIDGE_PIECE_WEIGHTS = new PieceWeight[] { new PieceWeight(BridgeStraight.class, 30, 0, true), new PieceWeight(BridgeCrossing.class, 10, 4), new PieceWeight(RoomCrossing.class, 10, 4), new PieceWeight(StairsRoom.class, 10, 3), new PieceWeight(MonsterThrone.class, 5, 2), new PieceWeight(CastleEntrance.class, 5, 1) };
        CASTLE_PIECE_WEIGHTS = new PieceWeight[] { new PieceWeight(CastleSmallCorridorPiece.class, 25, 0, true), new PieceWeight(CastleSmallCorridorCrossingPiece.class, 15, 5), new PieceWeight(CastleSmallCorridorRightTurnPiece.class, 5, 10), new PieceWeight(CastleSmallCorridorLeftTurnPiece.class, 5, 10), new PieceWeight(CastleCorridorStairsPiece.class, 10, 3, true), new PieceWeight(CastleCorridorTBalconyPiece.class, 7, 2), new PieceWeight(CastleStalkRoom.class, 5, 2) };
    }
    
    static class PieceWeight {
        public final Class<? extends NetherBridgePiece> pieceClass;
        public final int weight;
        public int placeCount;
        public final int maxPlaceCount;
        public final boolean allowInRow;
        
        public PieceWeight(final Class<? extends NetherBridgePiece> class1, final int integer2, final int integer3, final boolean boolean4) {
            this.pieceClass = class1;
            this.weight = integer2;
            this.maxPlaceCount = integer3;
            this.allowInRow = boolean4;
        }
        
        public PieceWeight(final Class<? extends NetherBridgePiece> class1, final int integer2, final int integer3) {
            this(class1, integer2, integer3, false);
        }
        
        public boolean doPlace(final int integer) {
            return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
        }
        
        public boolean isValid() {
            return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
        }
    }
    
    abstract static class NetherBridgePiece extends StructurePiece {
        protected NetherBridgePiece(final StructurePieceType cev, final int integer) {
            super(cev, integer);
        }
        
        public NetherBridgePiece(final StructurePieceType cev, final CompoundTag id) {
            super(cev, id);
        }
        
        @Override
        protected void addAdditionalSaveData(final CompoundTag id) {
        }
        
        private int updatePieceWeight(final List<PieceWeight> list) {
            boolean boolean3 = false;
            int integer4 = 0;
            for (final PieceWeight n6 : list) {
                if (n6.maxPlaceCount > 0 && n6.placeCount < n6.maxPlaceCount) {
                    boolean3 = true;
                }
                integer4 += n6.weight;
            }
            return boolean3 ? integer4 : -1;
        }
        
        private NetherBridgePiece generatePiece(final StartPiece q, final List<PieceWeight> list2, final List<StructurePiece> list3, final Random random, final int integer5, final int integer6, final int integer7, final Direction fb, final int integer9) {
            final int integer10 = this.updatePieceWeight(list2);
            final boolean boolean12 = integer10 > 0 && integer9 <= 30;
            int integer11 = 0;
            while (integer11 < 5 && boolean12) {
                ++integer11;
                int integer12 = random.nextInt(integer10);
                for (final PieceWeight n16 : list2) {
                    integer12 -= n16.weight;
                    if (integer12 < 0) {
                        if (!n16.doPlace(integer9)) {
                            break;
                        }
                        if (n16 == q.previousPiece && !n16.allowInRow) {
                            break;
                        }
                        final NetherBridgePiece m17 = findAndCreateBridgePieceFactory(n16, list3, random, integer5, integer6, integer7, fb, integer9);
                        if (m17 != null) {
                            final PieceWeight pieceWeight = n16;
                            ++pieceWeight.placeCount;
                            q.previousPiece = n16;
                            if (!n16.isValid()) {
                                list2.remove(n16);
                            }
                            return m17;
                        }
                        continue;
                    }
                }
            }
            return BridgeEndFiller.createPiece(list3, random, integer5, integer6, integer7, fb, integer9);
        }
        
        private StructurePiece generateAndAddPiece(final StartPiece q, final List<StructurePiece> list, final Random random, final int integer4, final int integer5, final int integer6, @Nullable final Direction fb, final int integer8, final boolean boolean9) {
            if (Math.abs(integer4 - q.getBoundingBox().x0) > 112 || Math.abs(integer6 - q.getBoundingBox().z0) > 112) {
                return BridgeEndFiller.createPiece(list, random, integer4, integer5, integer6, fb, integer8);
            }
            List<PieceWeight> list2 = q.availableBridgePieces;
            if (boolean9) {
                list2 = q.availableCastlePieces;
            }
            final StructurePiece civ12 = this.generatePiece(q, list2, list, random, integer4, integer5, integer6, fb, integer8 + 1);
            if (civ12 != null) {
                list.add(civ12);
                q.pendingChildren.add(civ12);
            }
            return civ12;
        }
        
        @Nullable
        protected StructurePiece generateChildForward(final StartPiece q, final List<StructurePiece> list, final Random random, final int integer4, final int integer5, final boolean boolean6) {
            final Direction fb8 = this.getOrientation();
            if (fb8 != null) {
                switch (fb8) {
                    case NORTH: {
                        return this.generateAndAddPiece(q, list, random, this.boundingBox.x0 + integer4, this.boundingBox.y0 + integer5, this.boundingBox.z0 - 1, fb8, this.getGenDepth(), boolean6);
                    }
                    case SOUTH: {
                        return this.generateAndAddPiece(q, list, random, this.boundingBox.x0 + integer4, this.boundingBox.y0 + integer5, this.boundingBox.z1 + 1, fb8, this.getGenDepth(), boolean6);
                    }
                    case WEST: {
                        return this.generateAndAddPiece(q, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 + integer5, this.boundingBox.z0 + integer4, fb8, this.getGenDepth(), boolean6);
                    }
                    case EAST: {
                        return this.generateAndAddPiece(q, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 + integer5, this.boundingBox.z0 + integer4, fb8, this.getGenDepth(), boolean6);
                    }
                }
            }
            return null;
        }
        
        @Nullable
        protected StructurePiece generateChildLeft(final StartPiece q, final List<StructurePiece> list, final Random random, final int integer4, final int integer5, final boolean boolean6) {
            final Direction fb8 = this.getOrientation();
            if (fb8 != null) {
                switch (fb8) {
                    case NORTH: {
                        return this.generateAndAddPiece(q, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 + integer4, this.boundingBox.z0 + integer5, Direction.WEST, this.getGenDepth(), boolean6);
                    }
                    case SOUTH: {
                        return this.generateAndAddPiece(q, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 + integer4, this.boundingBox.z0 + integer5, Direction.WEST, this.getGenDepth(), boolean6);
                    }
                    case WEST: {
                        return this.generateAndAddPiece(q, list, random, this.boundingBox.x0 + integer5, this.boundingBox.y0 + integer4, this.boundingBox.z0 - 1, Direction.NORTH, this.getGenDepth(), boolean6);
                    }
                    case EAST: {
                        return this.generateAndAddPiece(q, list, random, this.boundingBox.x0 + integer5, this.boundingBox.y0 + integer4, this.boundingBox.z0 - 1, Direction.NORTH, this.getGenDepth(), boolean6);
                    }
                }
            }
            return null;
        }
        
        @Nullable
        protected StructurePiece generateChildRight(final StartPiece q, final List<StructurePiece> list, final Random random, final int integer4, final int integer5, final boolean boolean6) {
            final Direction fb8 = this.getOrientation();
            if (fb8 != null) {
                switch (fb8) {
                    case NORTH: {
                        return this.generateAndAddPiece(q, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 + integer4, this.boundingBox.z0 + integer5, Direction.EAST, this.getGenDepth(), boolean6);
                    }
                    case SOUTH: {
                        return this.generateAndAddPiece(q, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 + integer4, this.boundingBox.z0 + integer5, Direction.EAST, this.getGenDepth(), boolean6);
                    }
                    case WEST: {
                        return this.generateAndAddPiece(q, list, random, this.boundingBox.x0 + integer5, this.boundingBox.y0 + integer4, this.boundingBox.z1 + 1, Direction.SOUTH, this.getGenDepth(), boolean6);
                    }
                    case EAST: {
                        return this.generateAndAddPiece(q, list, random, this.boundingBox.x0 + integer5, this.boundingBox.y0 + integer4, this.boundingBox.z1 + 1, Direction.SOUTH, this.getGenDepth(), boolean6);
                    }
                }
            }
            return null;
        }
        
        protected static boolean isOkBox(final BoundingBox cic) {
            return cic != null && cic.y0 > 10;
        }
    }
    
    public static class StartPiece extends BridgeCrossing {
        public PieceWeight previousPiece;
        public List<PieceWeight> availableBridgePieces;
        public List<PieceWeight> availableCastlePieces;
        public final List<StructurePiece> pendingChildren;
        
        public StartPiece(final Random random, final int integer2, final int integer3) {
            super(random, integer2, integer3);
            this.pendingChildren = (List<StructurePiece>)Lists.newArrayList();
            this.availableBridgePieces = (List<PieceWeight>)Lists.newArrayList();
            for (final PieceWeight n8 : NetherBridgePieces.BRIDGE_PIECE_WEIGHTS) {
                n8.placeCount = 0;
                this.availableBridgePieces.add(n8);
            }
            this.availableCastlePieces = (List<PieceWeight>)Lists.newArrayList();
            for (final PieceWeight n8 : NetherBridgePieces.CASTLE_PIECE_WEIGHTS) {
                n8.placeCount = 0;
                this.availableCastlePieces.add(n8);
            }
        }
        
        public StartPiece(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.NETHER_FORTRESS_START, id);
            this.pendingChildren = (List<StructurePiece>)Lists.newArrayList();
        }
    }
    
    public static class BridgeStraight extends NetherBridgePiece {
        public BridgeStraight(final int integer, final Random random, final BoundingBox cic, final Direction fb) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_STRAIGHT, integer);
            this.setOrientation(fb);
            this.boundingBox = cic;
        }
        
        public BridgeStraight(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_STRAIGHT, id);
        }
        
        @Override
        public void addChildren(final StructurePiece civ, final List<StructurePiece> list, final Random random) {
            this.generateChildForward((StartPiece)civ, list, random, 1, 3, false);
        }
        
        public static BridgeStraight createPiece(final List<StructurePiece> list, final Random random, final int integer3, final int integer4, final int integer5, final Direction fb, final int integer7) {
            final BoundingBox cic8 = BoundingBox.orientBox(integer3, integer4, integer5, -1, -3, 0, 5, 10, 19, fb);
            if (!NetherBridgePiece.isOkBox(cic8) || StructurePiece.findCollisionPiece(list, cic8) != null) {
                return null;
            }
            return new BridgeStraight(integer7, random, cic8, fb);
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            this.generateBox(bhs, cic, 0, 3, 0, 4, 4, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 1, 5, 0, 3, 7, 18, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 5, 0, 0, 5, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 4, 5, 0, 4, 5, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 2, 0, 4, 2, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 2, 13, 4, 2, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 0, 0, 4, 1, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 0, 15, 4, 1, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            for (int integer6 = 0; integer6 <= 4; ++integer6) {
                for (int integer7 = 0; integer7 <= 2; ++integer7) {
                    this.fillColumnDown(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), integer6, -1, integer7, cic);
                    this.fillColumnDown(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), integer6, -1, 18 - integer7, cic);
                }
            }
            final BlockState bvt6 = (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.SOUTH, true);
            final BlockState bvt7 = ((AbstractStateHolder<O, BlockState>)bvt6).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.EAST, true);
            final BlockState bvt8 = ((AbstractStateHolder<O, BlockState>)bvt6).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.WEST, true);
            this.generateBox(bhs, cic, 0, 1, 1, 0, 4, 1, bvt7, bvt7, false);
            this.generateBox(bhs, cic, 0, 3, 4, 0, 4, 4, bvt7, bvt7, false);
            this.generateBox(bhs, cic, 0, 3, 14, 0, 4, 14, bvt7, bvt7, false);
            this.generateBox(bhs, cic, 0, 1, 17, 0, 4, 17, bvt7, bvt7, false);
            this.generateBox(bhs, cic, 4, 1, 1, 4, 4, 1, bvt8, bvt8, false);
            this.generateBox(bhs, cic, 4, 3, 4, 4, 4, 4, bvt8, bvt8, false);
            this.generateBox(bhs, cic, 4, 3, 14, 4, 4, 14, bvt8, bvt8, false);
            this.generateBox(bhs, cic, 4, 1, 17, 4, 4, 17, bvt8, bvt8, false);
            return true;
        }
    }
    
    public static class BridgeEndFiller extends NetherBridgePiece {
        private final int selfSeed;
        
        public BridgeEndFiller(final int integer, final Random random, final BoundingBox cic, final Direction fb) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_END_FILLER, integer);
            this.setOrientation(fb);
            this.boundingBox = cic;
            this.selfSeed = random.nextInt();
        }
        
        public BridgeEndFiller(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_END_FILLER, id);
            this.selfSeed = id.getInt("Seed");
        }
        
        public static BridgeEndFiller createPiece(final List<StructurePiece> list, final Random random, final int integer3, final int integer4, final int integer5, final Direction fb, final int integer7) {
            final BoundingBox cic8 = BoundingBox.orientBox(integer3, integer4, integer5, -1, -3, 0, 5, 10, 8, fb);
            if (!NetherBridgePiece.isOkBox(cic8) || StructurePiece.findCollisionPiece(list, cic8) != null) {
                return null;
            }
            return new BridgeEndFiller(integer7, random, cic8, fb);
        }
        
        @Override
        protected void addAdditionalSaveData(final CompoundTag id) {
            super.addAdditionalSaveData(id);
            id.putInt("Seed", this.selfSeed);
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            final Random random2 = new Random((long)this.selfSeed);
            for (int integer7 = 0; integer7 <= 4; ++integer7) {
                for (int integer8 = 3; integer8 <= 4; ++integer8) {
                    final int integer9 = random2.nextInt(8);
                    this.generateBox(bhs, cic, integer7, integer8, 0, integer7, integer8, integer9, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
                }
            }
            int integer7 = random2.nextInt(8);
            this.generateBox(bhs, cic, 0, 5, 0, 0, 5, integer7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            integer7 = random2.nextInt(8);
            this.generateBox(bhs, cic, 4, 5, 0, 4, 5, integer7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            for (integer7 = 0; integer7 <= 4; ++integer7) {
                final int integer8 = random2.nextInt(5);
                this.generateBox(bhs, cic, integer7, 2, 0, integer7, 2, integer8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            }
            for (integer7 = 0; integer7 <= 4; ++integer7) {
                for (int integer8 = 0; integer8 <= 1; ++integer8) {
                    final int integer9 = random2.nextInt(3);
                    this.generateBox(bhs, cic, integer7, integer8, 0, integer7, integer8, integer9, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
                }
            }
            return true;
        }
    }
    
    public static class BridgeCrossing extends NetherBridgePiece {
        public BridgeCrossing(final int integer, final BoundingBox cic, final Direction fb) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, integer);
            this.setOrientation(fb);
            this.boundingBox = cic;
        }
        
        protected BridgeCrossing(final Random random, final int integer2, final int integer3) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, 0);
            this.setOrientation(Direction.Plane.HORIZONTAL.getRandomDirection(random));
            if (this.getOrientation().getAxis() == Direction.Axis.Z) {
                this.boundingBox = new BoundingBox(integer2, 64, integer3, integer2 + 19 - 1, 73, integer3 + 19 - 1);
            }
            else {
                this.boundingBox = new BoundingBox(integer2, 64, integer3, integer2 + 19 - 1, 73, integer3 + 19 - 1);
            }
        }
        
        protected BridgeCrossing(final StructurePieceType cev, final CompoundTag id) {
            super(cev, id);
        }
        
        public BridgeCrossing(final StructureManager cjp, final CompoundTag id) {
            this(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, id);
        }
        
        @Override
        public void addChildren(final StructurePiece civ, final List<StructurePiece> list, final Random random) {
            this.generateChildForward((StartPiece)civ, list, random, 8, 3, false);
            this.generateChildLeft((StartPiece)civ, list, random, 3, 8, false);
            this.generateChildRight((StartPiece)civ, list, random, 3, 8, false);
        }
        
        public static BridgeCrossing createPiece(final List<StructurePiece> list, final int integer2, final int integer3, final int integer4, final Direction fb, final int integer6) {
            final BoundingBox cic7 = BoundingBox.orientBox(integer2, integer3, integer4, -8, -3, 0, 19, 10, 19, fb);
            if (!NetherBridgePiece.isOkBox(cic7) || StructurePiece.findCollisionPiece(list, cic7) != null) {
                return null;
            }
            return new BridgeCrossing(integer6, cic7, fb);
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            this.generateBox(bhs, cic, 7, 3, 0, 11, 4, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 3, 7, 18, 4, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 8, 5, 0, 10, 7, 18, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 5, 8, 18, 7, 10, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(bhs, cic, 7, 5, 0, 7, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 7, 5, 11, 7, 5, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 11, 5, 0, 11, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 11, 5, 11, 11, 5, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 5, 7, 7, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 11, 5, 7, 18, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 5, 11, 7, 5, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 11, 5, 11, 18, 5, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 7, 2, 0, 11, 2, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 7, 2, 13, 11, 2, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 7, 0, 0, 11, 1, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 7, 0, 15, 11, 1, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            for (int integer6 = 7; integer6 <= 11; ++integer6) {
                for (int integer7 = 0; integer7 <= 2; ++integer7) {
                    this.fillColumnDown(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), integer6, -1, integer7, cic);
                    this.fillColumnDown(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), integer6, -1, 18 - integer7, cic);
                }
            }
            this.generateBox(bhs, cic, 0, 2, 7, 5, 2, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 13, 2, 7, 18, 2, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 0, 7, 3, 1, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 15, 0, 7, 18, 1, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            for (int integer6 = 0; integer6 <= 2; ++integer6) {
                for (int integer7 = 7; integer7 <= 11; ++integer7) {
                    this.fillColumnDown(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), integer6, -1, integer7, cic);
                    this.fillColumnDown(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), 18 - integer6, -1, integer7, cic);
                }
            }
            return true;
        }
    }
    
    public static class RoomCrossing extends NetherBridgePiece {
        public RoomCrossing(final int integer, final BoundingBox cic, final Direction fb) {
            super(StructurePieceType.NETHER_FORTRESS_ROOM_CROSSING, integer);
            this.setOrientation(fb);
            this.boundingBox = cic;
        }
        
        public RoomCrossing(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.NETHER_FORTRESS_ROOM_CROSSING, id);
        }
        
        @Override
        public void addChildren(final StructurePiece civ, final List<StructurePiece> list, final Random random) {
            this.generateChildForward((StartPiece)civ, list, random, 2, 0, false);
            this.generateChildLeft((StartPiece)civ, list, random, 0, 2, false);
            this.generateChildRight((StartPiece)civ, list, random, 0, 2, false);
        }
        
        public static RoomCrossing createPiece(final List<StructurePiece> list, final int integer2, final int integer3, final int integer4, final Direction fb, final int integer6) {
            final BoundingBox cic7 = BoundingBox.orientBox(integer2, integer3, integer4, -2, 0, 0, 7, 9, 7, fb);
            if (!NetherBridgePiece.isOkBox(cic7) || StructurePiece.findCollisionPiece(list, cic7) != null) {
                return null;
            }
            return new RoomCrossing(integer6, cic7, fb);
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            this.generateBox(bhs, cic, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 2, 0, 6, 7, 6, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 2, 0, 1, 6, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 2, 6, 1, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 5, 2, 0, 6, 6, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 5, 2, 6, 6, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 2, 0, 0, 6, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 2, 5, 0, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 6, 2, 0, 6, 6, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 6, 2, 5, 6, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            final BlockState bvt6 = (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.WEST, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.EAST, true);
            final BlockState bvt7 = (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.SOUTH, true);
            this.generateBox(bhs, cic, 2, 6, 0, 4, 6, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 2, 5, 0, 4, 5, 0, bvt6, bvt6, false);
            this.generateBox(bhs, cic, 2, 6, 6, 4, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 2, 5, 6, 4, 5, 6, bvt6, bvt6, false);
            this.generateBox(bhs, cic, 0, 6, 2, 0, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 5, 2, 0, 5, 4, bvt7, bvt7, false);
            this.generateBox(bhs, cic, 6, 6, 2, 6, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 6, 5, 2, 6, 5, 4, bvt7, bvt7, false);
            for (int integer8 = 0; integer8 <= 6; ++integer8) {
                for (int integer9 = 0; integer9 <= 6; ++integer9) {
                    this.fillColumnDown(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), integer8, -1, integer9, cic);
                }
            }
            return true;
        }
    }
    
    public static class StairsRoom extends NetherBridgePiece {
        public StairsRoom(final int integer, final BoundingBox cic, final Direction fb) {
            super(StructurePieceType.NETHER_FORTRESS_STAIRS_ROOM, integer);
            this.setOrientation(fb);
            this.boundingBox = cic;
        }
        
        public StairsRoom(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.NETHER_FORTRESS_STAIRS_ROOM, id);
        }
        
        @Override
        public void addChildren(final StructurePiece civ, final List<StructurePiece> list, final Random random) {
            this.generateChildRight((StartPiece)civ, list, random, 6, 2, false);
        }
        
        public static StairsRoom createPiece(final List<StructurePiece> list, final int integer2, final int integer3, final int integer4, final int integer5, final Direction fb) {
            final BoundingBox cic7 = BoundingBox.orientBox(integer2, integer3, integer4, -2, 0, 0, 7, 11, 7, fb);
            if (!NetherBridgePiece.isOkBox(cic7) || StructurePiece.findCollisionPiece(list, cic7) != null) {
                return null;
            }
            return new StairsRoom(integer5, cic7, fb);
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            this.generateBox(bhs, cic, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 2, 0, 6, 10, 6, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 2, 0, 1, 8, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 5, 2, 0, 6, 8, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 2, 1, 0, 8, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 6, 2, 1, 6, 8, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 1, 2, 6, 5, 8, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            final BlockState bvt6 = (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.WEST, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.EAST, true);
            final BlockState bvt7 = (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.SOUTH, true);
            this.generateBox(bhs, cic, 0, 3, 2, 0, 5, 4, bvt7, bvt7, false);
            this.generateBox(bhs, cic, 6, 3, 2, 6, 5, 2, bvt7, bvt7, false);
            this.generateBox(bhs, cic, 6, 3, 4, 6, 5, 4, bvt7, bvt7, false);
            this.placeBlock(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), 5, 2, 5, cic);
            this.generateBox(bhs, cic, 4, 2, 5, 4, 3, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 3, 2, 5, 3, 4, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 2, 2, 5, 2, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 1, 2, 5, 1, 6, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 1, 7, 1, 5, 7, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 6, 8, 2, 6, 8, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(bhs, cic, 2, 6, 0, 4, 8, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 2, 5, 0, 4, 5, 0, bvt6, bvt6, false);
            for (int integer8 = 0; integer8 <= 6; ++integer8) {
                for (int integer9 = 0; integer9 <= 6; ++integer9) {
                    this.fillColumnDown(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), integer8, -1, integer9, cic);
                }
            }
            return true;
        }
    }
    
    public static class MonsterThrone extends NetherBridgePiece {
        private boolean hasPlacedSpawner;
        
        public MonsterThrone(final int integer, final BoundingBox cic, final Direction fb) {
            super(StructurePieceType.NETHER_FORTRESS_MONSTER_THRONE, integer);
            this.setOrientation(fb);
            this.boundingBox = cic;
        }
        
        public MonsterThrone(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.NETHER_FORTRESS_MONSTER_THRONE, id);
            this.hasPlacedSpawner = id.getBoolean("Mob");
        }
        
        @Override
        protected void addAdditionalSaveData(final CompoundTag id) {
            super.addAdditionalSaveData(id);
            id.putBoolean("Mob", this.hasPlacedSpawner);
        }
        
        public static MonsterThrone createPiece(final List<StructurePiece> list, final int integer2, final int integer3, final int integer4, final int integer5, final Direction fb) {
            final BoundingBox cic7 = BoundingBox.orientBox(integer2, integer3, integer4, -2, 0, 0, 7, 8, 9, fb);
            if (!NetherBridgePiece.isOkBox(cic7) || StructurePiece.findCollisionPiece(list, cic7) != null) {
                return null;
            }
            return new MonsterThrone(integer5, cic7, fb);
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            this.generateBox(bhs, cic, 0, 2, 0, 6, 7, 7, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(bhs, cic, 1, 0, 0, 5, 1, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 1, 2, 1, 5, 2, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 1, 3, 2, 5, 3, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 1, 4, 3, 5, 4, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 1, 2, 0, 1, 4, 2, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 5, 2, 0, 5, 4, 2, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 1, 5, 2, 1, 5, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 5, 5, 2, 5, 5, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 5, 3, 0, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 6, 5, 3, 6, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 1, 5, 8, 5, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            final BlockState bvt6 = (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.WEST, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.EAST, true);
            final BlockState bvt7 = (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.SOUTH, true);
            this.placeBlock(bhs, ((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.WEST, true), 1, 6, 3, cic);
            this.placeBlock(bhs, ((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.EAST, true), 5, 6, 3, cic);
            this.placeBlock(bhs, (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.EAST, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.NORTH, true), 0, 6, 3, cic);
            this.placeBlock(bhs, (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.WEST, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.NORTH, true), 6, 6, 3, cic);
            this.generateBox(bhs, cic, 0, 6, 4, 0, 6, 7, bvt7, bvt7, false);
            this.generateBox(bhs, cic, 6, 6, 4, 6, 6, 7, bvt7, bvt7, false);
            this.placeBlock(bhs, (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.EAST, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.SOUTH, true), 0, 6, 8, cic);
            this.placeBlock(bhs, (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.WEST, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.SOUTH, true), 6, 6, 8, cic);
            this.generateBox(bhs, cic, 1, 6, 8, 5, 6, 8, bvt6, bvt6, false);
            this.placeBlock(bhs, ((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.EAST, true), 1, 7, 8, cic);
            this.generateBox(bhs, cic, 2, 7, 8, 4, 7, 8, bvt6, bvt6, false);
            this.placeBlock(bhs, ((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.WEST, true), 5, 7, 8, cic);
            this.placeBlock(bhs, ((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.EAST, true), 2, 8, 8, cic);
            this.placeBlock(bhs, bvt6, 3, 8, 8, cic);
            this.placeBlock(bhs, ((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.WEST, true), 4, 8, 8, cic);
            if (!this.hasPlacedSpawner) {
                final BlockPos ew8 = new BlockPos(this.getWorldX(3, 5), this.getWorldY(5), this.getWorldZ(3, 5));
                if (cic.isInside(ew8)) {
                    this.hasPlacedSpawner = true;
                    bhs.setBlock(ew8, Blocks.SPAWNER.defaultBlockState(), 2);
                    final BlockEntity btw9 = bhs.getBlockEntity(ew8);
                    if (btw9 instanceof SpawnerBlockEntity) {
                        ((SpawnerBlockEntity)btw9).getSpawner().setEntityId(EntityType.BLAZE);
                    }
                }
            }
            for (int integer8 = 0; integer8 <= 6; ++integer8) {
                for (int integer9 = 0; integer9 <= 6; ++integer9) {
                    this.fillColumnDown(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), integer8, -1, integer9, cic);
                }
            }
            return true;
        }
    }
    
    public static class CastleEntrance extends NetherBridgePiece {
        public CastleEntrance(final int integer, final Random random, final BoundingBox cic, final Direction fb) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_ENTRANCE, integer);
            this.setOrientation(fb);
            this.boundingBox = cic;
        }
        
        public CastleEntrance(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_ENTRANCE, id);
        }
        
        @Override
        public void addChildren(final StructurePiece civ, final List<StructurePiece> list, final Random random) {
            this.generateChildForward((StartPiece)civ, list, random, 5, 3, true);
        }
        
        public static CastleEntrance createPiece(final List<StructurePiece> list, final Random random, final int integer3, final int integer4, final int integer5, final Direction fb, final int integer7) {
            final BoundingBox cic8 = BoundingBox.orientBox(integer3, integer4, integer5, -5, -3, 0, 13, 14, 13, fb);
            if (!NetherBridgePiece.isOkBox(cic8) || StructurePiece.findCollisionPiece(list, cic8) != null) {
                return null;
            }
            return new CastleEntrance(integer7, random, cic8, fb);
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            this.generateBox(bhs, cic, 0, 3, 0, 12, 4, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 5, 0, 12, 13, 12, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 5, 0, 1, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 11, 5, 0, 12, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 2, 5, 11, 4, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 8, 5, 11, 10, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 5, 9, 11, 7, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 2, 5, 0, 4, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 8, 5, 0, 10, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 5, 9, 0, 7, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 2, 11, 2, 10, 12, 10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 5, 8, 0, 7, 8, 0, Blocks.NETHER_BRICK_FENCE.defaultBlockState(), Blocks.NETHER_BRICK_FENCE.defaultBlockState(), false);
            final BlockState bvt6 = (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.WEST, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.EAST, true);
            final BlockState bvt7 = (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.SOUTH, true);
            for (int integer8 = 1; integer8 <= 11; integer8 += 2) {
                this.generateBox(bhs, cic, integer8, 10, 0, integer8, 11, 0, bvt6, bvt6, false);
                this.generateBox(bhs, cic, integer8, 10, 12, integer8, 11, 12, bvt6, bvt6, false);
                this.generateBox(bhs, cic, 0, 10, integer8, 0, 11, integer8, bvt7, bvt7, false);
                this.generateBox(bhs, cic, 12, 10, integer8, 12, 11, integer8, bvt7, bvt7, false);
                this.placeBlock(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), integer8, 13, 0, cic);
                this.placeBlock(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), integer8, 13, 12, cic);
                this.placeBlock(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), 0, 13, integer8, cic);
                this.placeBlock(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), 12, 13, integer8, cic);
                if (integer8 != 11) {
                    this.placeBlock(bhs, bvt6, integer8 + 1, 13, 0, cic);
                    this.placeBlock(bhs, bvt6, integer8 + 1, 13, 12, cic);
                    this.placeBlock(bhs, bvt7, 0, 13, integer8 + 1, cic);
                    this.placeBlock(bhs, bvt7, 12, 13, integer8 + 1, cic);
                }
            }
            this.placeBlock(bhs, (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.EAST, true), 0, 13, 0, cic);
            this.placeBlock(bhs, (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.SOUTH, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.EAST, true), 0, 13, 12, cic);
            this.placeBlock(bhs, (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.SOUTH, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.WEST, true), 12, 13, 12, cic);
            this.placeBlock(bhs, (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.WEST, true), 12, 13, 0, cic);
            for (int integer8 = 3; integer8 <= 9; integer8 += 2) {
                this.generateBox(bhs, cic, 1, 7, integer8, 1, 8, integer8, ((AbstractStateHolder<O, BlockState>)bvt7).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.WEST, true), ((AbstractStateHolder<O, BlockState>)bvt7).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.WEST, true), false);
                this.generateBox(bhs, cic, 11, 7, integer8, 11, 8, integer8, ((AbstractStateHolder<O, BlockState>)bvt7).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.EAST, true), ((AbstractStateHolder<O, BlockState>)bvt7).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.EAST, true), false);
            }
            this.generateBox(bhs, cic, 4, 2, 0, 8, 2, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 2, 4, 12, 2, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 4, 0, 0, 8, 1, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 4, 0, 9, 8, 1, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 0, 4, 3, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 9, 0, 4, 12, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            for (int integer8 = 4; integer8 <= 8; ++integer8) {
                for (int integer9 = 0; integer9 <= 2; ++integer9) {
                    this.fillColumnDown(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), integer8, -1, integer9, cic);
                    this.fillColumnDown(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), integer8, -1, 12 - integer9, cic);
                }
            }
            for (int integer8 = 0; integer8 <= 2; ++integer8) {
                for (int integer9 = 4; integer9 <= 8; ++integer9) {
                    this.fillColumnDown(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), integer8, -1, integer9, cic);
                    this.fillColumnDown(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), 12 - integer8, -1, integer9, cic);
                }
            }
            this.generateBox(bhs, cic, 5, 5, 5, 7, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 6, 1, 6, 6, 4, 6, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.placeBlock(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), 6, 0, 6, cic);
            this.placeBlock(bhs, Blocks.LAVA.defaultBlockState(), 6, 5, 6, cic);
            final BlockPos ew8 = new BlockPos(this.getWorldX(6, 6), this.getWorldY(5), this.getWorldZ(6, 6));
            if (cic.isInside(ew8)) {
                bhs.getLiquidTicks().scheduleTick(ew8, Fluids.LAVA, 0);
            }
            return true;
        }
    }
    
    public static class CastleStalkRoom extends NetherBridgePiece {
        public CastleStalkRoom(final int integer, final BoundingBox cic, final Direction fb) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_STALK_ROOM, integer);
            this.setOrientation(fb);
            this.boundingBox = cic;
        }
        
        public CastleStalkRoom(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_STALK_ROOM, id);
        }
        
        @Override
        public void addChildren(final StructurePiece civ, final List<StructurePiece> list, final Random random) {
            this.generateChildForward((StartPiece)civ, list, random, 5, 3, true);
            this.generateChildForward((StartPiece)civ, list, random, 5, 11, true);
        }
        
        public static CastleStalkRoom createPiece(final List<StructurePiece> list, final int integer2, final int integer3, final int integer4, final Direction fb, final int integer6) {
            final BoundingBox cic7 = BoundingBox.orientBox(integer2, integer3, integer4, -5, -3, 0, 13, 14, 13, fb);
            if (!NetherBridgePiece.isOkBox(cic7) || StructurePiece.findCollisionPiece(list, cic7) != null) {
                return null;
            }
            return new CastleStalkRoom(integer6, cic7, fb);
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            this.generateBox(bhs, cic, 0, 3, 0, 12, 4, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 5, 0, 12, 13, 12, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 5, 0, 1, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 11, 5, 0, 12, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 2, 5, 11, 4, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 8, 5, 11, 10, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 5, 9, 11, 7, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 2, 5, 0, 4, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 8, 5, 0, 10, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 5, 9, 0, 7, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 2, 11, 2, 10, 12, 10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            final BlockState bvt6 = (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.WEST, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.EAST, true);
            final BlockState bvt7 = (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.SOUTH, true);
            final BlockState bvt8 = ((AbstractStateHolder<O, BlockState>)bvt7).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.WEST, true);
            final BlockState bvt9 = ((AbstractStateHolder<O, BlockState>)bvt7).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.EAST, true);
            for (int integer10 = 1; integer10 <= 11; integer10 += 2) {
                this.generateBox(bhs, cic, integer10, 10, 0, integer10, 11, 0, bvt6, bvt6, false);
                this.generateBox(bhs, cic, integer10, 10, 12, integer10, 11, 12, bvt6, bvt6, false);
                this.generateBox(bhs, cic, 0, 10, integer10, 0, 11, integer10, bvt7, bvt7, false);
                this.generateBox(bhs, cic, 12, 10, integer10, 12, 11, integer10, bvt7, bvt7, false);
                this.placeBlock(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), integer10, 13, 0, cic);
                this.placeBlock(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), integer10, 13, 12, cic);
                this.placeBlock(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), 0, 13, integer10, cic);
                this.placeBlock(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), 12, 13, integer10, cic);
                if (integer10 != 11) {
                    this.placeBlock(bhs, bvt6, integer10 + 1, 13, 0, cic);
                    this.placeBlock(bhs, bvt6, integer10 + 1, 13, 12, cic);
                    this.placeBlock(bhs, bvt7, 0, 13, integer10 + 1, cic);
                    this.placeBlock(bhs, bvt7, 12, 13, integer10 + 1, cic);
                }
            }
            this.placeBlock(bhs, (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.EAST, true), 0, 13, 0, cic);
            this.placeBlock(bhs, (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.SOUTH, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.EAST, true), 0, 13, 12, cic);
            this.placeBlock(bhs, (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.SOUTH, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.WEST, true), 12, 13, 12, cic);
            this.placeBlock(bhs, (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.WEST, true), 12, 13, 0, cic);
            for (int integer10 = 3; integer10 <= 9; integer10 += 2) {
                this.generateBox(bhs, cic, 1, 7, integer10, 1, 8, integer10, bvt8, bvt8, false);
                this.generateBox(bhs, cic, 11, 7, integer10, 11, 8, integer10, bvt9, bvt9, false);
            }
            final BlockState bvt10 = ((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_STAIRS.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)StairBlock.FACING, Direction.NORTH);
            for (int integer11 = 0; integer11 <= 6; ++integer11) {
                final int integer12 = integer11 + 4;
                for (int integer13 = 5; integer13 <= 7; ++integer13) {
                    this.placeBlock(bhs, bvt10, integer13, 5 + integer11, integer12, cic);
                }
                if (integer12 >= 5 && integer12 <= 8) {
                    this.generateBox(bhs, cic, 5, 5, integer12, 7, integer11 + 4, integer12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
                }
                else if (integer12 >= 9 && integer12 <= 10) {
                    this.generateBox(bhs, cic, 5, 8, integer12, 7, integer11 + 4, integer12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
                }
                if (integer11 >= 1) {
                    this.generateBox(bhs, cic, 5, 6 + integer11, integer12, 7, 9 + integer11, integer12, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
                }
            }
            for (int integer11 = 5; integer11 <= 7; ++integer11) {
                this.placeBlock(bhs, bvt10, integer11, 12, 11, cic);
            }
            this.generateBox(bhs, cic, 5, 6, 7, 5, 7, 7, bvt9, bvt9, false);
            this.generateBox(bhs, cic, 7, 6, 7, 7, 7, 7, bvt8, bvt8, false);
            this.generateBox(bhs, cic, 5, 13, 12, 7, 13, 12, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(bhs, cic, 2, 5, 2, 3, 5, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 2, 5, 9, 3, 5, 10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 2, 5, 4, 2, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 9, 5, 2, 10, 5, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 9, 5, 9, 10, 5, 10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 10, 5, 4, 10, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            final BlockState bvt11 = ((AbstractStateHolder<O, BlockState>)bvt10).<Comparable, Direction>setValue((Property<Comparable>)StairBlock.FACING, Direction.EAST);
            final BlockState bvt12 = ((AbstractStateHolder<O, BlockState>)bvt10).<Comparable, Direction>setValue((Property<Comparable>)StairBlock.FACING, Direction.WEST);
            this.placeBlock(bhs, bvt12, 4, 5, 2, cic);
            this.placeBlock(bhs, bvt12, 4, 5, 3, cic);
            this.placeBlock(bhs, bvt12, 4, 5, 9, cic);
            this.placeBlock(bhs, bvt12, 4, 5, 10, cic);
            this.placeBlock(bhs, bvt11, 8, 5, 2, cic);
            this.placeBlock(bhs, bvt11, 8, 5, 3, cic);
            this.placeBlock(bhs, bvt11, 8, 5, 9, cic);
            this.placeBlock(bhs, bvt11, 8, 5, 10, cic);
            this.generateBox(bhs, cic, 3, 4, 4, 4, 4, 8, Blocks.SOUL_SAND.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState(), false);
            this.generateBox(bhs, cic, 8, 4, 4, 9, 4, 8, Blocks.SOUL_SAND.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState(), false);
            this.generateBox(bhs, cic, 3, 5, 4, 4, 5, 8, Blocks.NETHER_WART.defaultBlockState(), Blocks.NETHER_WART.defaultBlockState(), false);
            this.generateBox(bhs, cic, 8, 5, 4, 9, 5, 8, Blocks.NETHER_WART.defaultBlockState(), Blocks.NETHER_WART.defaultBlockState(), false);
            this.generateBox(bhs, cic, 4, 2, 0, 8, 2, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 2, 4, 12, 2, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 4, 0, 0, 8, 1, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 4, 0, 9, 8, 1, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 0, 4, 3, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 9, 0, 4, 12, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            for (int integer13 = 4; integer13 <= 8; ++integer13) {
                for (int integer14 = 0; integer14 <= 2; ++integer14) {
                    this.fillColumnDown(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), integer13, -1, integer14, cic);
                    this.fillColumnDown(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), integer13, -1, 12 - integer14, cic);
                }
            }
            for (int integer13 = 0; integer13 <= 2; ++integer13) {
                for (int integer14 = 4; integer14 <= 8; ++integer14) {
                    this.fillColumnDown(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), integer13, -1, integer14, cic);
                    this.fillColumnDown(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), 12 - integer13, -1, integer14, cic);
                }
            }
            return true;
        }
    }
    
    public static class CastleSmallCorridorPiece extends NetherBridgePiece {
        public CastleSmallCorridorPiece(final int integer, final BoundingBox cic, final Direction fb) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR, integer);
            this.setOrientation(fb);
            this.boundingBox = cic;
        }
        
        public CastleSmallCorridorPiece(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR, id);
        }
        
        @Override
        public void addChildren(final StructurePiece civ, final List<StructurePiece> list, final Random random) {
            this.generateChildForward((StartPiece)civ, list, random, 1, 0, true);
        }
        
        public static CastleSmallCorridorPiece createPiece(final List<StructurePiece> list, final int integer2, final int integer3, final int integer4, final Direction fb, final int integer6) {
            final BoundingBox cic7 = BoundingBox.orientBox(integer2, integer3, integer4, -1, 0, 0, 5, 7, 5, fb);
            if (!NetherBridgePiece.isOkBox(cic7) || StructurePiece.findCollisionPiece(list, cic7) != null) {
                return null;
            }
            return new CastleSmallCorridorPiece(integer6, cic7, fb);
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            this.generateBox(bhs, cic, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            final BlockState bvt6 = (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.SOUTH, true);
            this.generateBox(bhs, cic, 0, 2, 0, 0, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 3, 1, 0, 4, 1, bvt6, bvt6, false);
            this.generateBox(bhs, cic, 0, 3, 3, 0, 4, 3, bvt6, bvt6, false);
            this.generateBox(bhs, cic, 4, 3, 1, 4, 4, 1, bvt6, bvt6, false);
            this.generateBox(bhs, cic, 4, 3, 3, 4, 4, 3, bvt6, bvt6, false);
            this.generateBox(bhs, cic, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            for (int integer7 = 0; integer7 <= 4; ++integer7) {
                for (int integer8 = 0; integer8 <= 4; ++integer8) {
                    this.fillColumnDown(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), integer7, -1, integer8, cic);
                }
            }
            return true;
        }
    }
    
    public static class CastleSmallCorridorCrossingPiece extends NetherBridgePiece {
        public CastleSmallCorridorCrossingPiece(final int integer, final BoundingBox cic, final Direction fb) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_CROSSING, integer);
            this.setOrientation(fb);
            this.boundingBox = cic;
        }
        
        public CastleSmallCorridorCrossingPiece(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_CROSSING, id);
        }
        
        @Override
        public void addChildren(final StructurePiece civ, final List<StructurePiece> list, final Random random) {
            this.generateChildForward((StartPiece)civ, list, random, 1, 0, true);
            this.generateChildLeft((StartPiece)civ, list, random, 0, 1, true);
            this.generateChildRight((StartPiece)civ, list, random, 0, 1, true);
        }
        
        public static CastleSmallCorridorCrossingPiece createPiece(final List<StructurePiece> list, final int integer2, final int integer3, final int integer4, final Direction fb, final int integer6) {
            final BoundingBox cic7 = BoundingBox.orientBox(integer2, integer3, integer4, -1, 0, 0, 5, 7, 5, fb);
            if (!NetherBridgePiece.isOkBox(cic7) || StructurePiece.findCollisionPiece(list, cic7) != null) {
                return null;
            }
            return new CastleSmallCorridorCrossingPiece(integer6, cic7, fb);
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            this.generateBox(bhs, cic, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 2, 0, 0, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 4, 2, 0, 4, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 2, 4, 0, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 4, 2, 4, 4, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            for (int integer6 = 0; integer6 <= 4; ++integer6) {
                for (int integer7 = 0; integer7 <= 4; ++integer7) {
                    this.fillColumnDown(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), integer6, -1, integer7, cic);
                }
            }
            return true;
        }
    }
    
    public static class CastleSmallCorridorRightTurnPiece extends NetherBridgePiece {
        private boolean isNeedingChest;
        
        public CastleSmallCorridorRightTurnPiece(final int integer, final Random random, final BoundingBox cic, final Direction fb) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_RIGHT_TURN, integer);
            this.setOrientation(fb);
            this.boundingBox = cic;
            this.isNeedingChest = (random.nextInt(3) == 0);
        }
        
        public CastleSmallCorridorRightTurnPiece(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_RIGHT_TURN, id);
            this.isNeedingChest = id.getBoolean("Chest");
        }
        
        @Override
        protected void addAdditionalSaveData(final CompoundTag id) {
            super.addAdditionalSaveData(id);
            id.putBoolean("Chest", this.isNeedingChest);
        }
        
        @Override
        public void addChildren(final StructurePiece civ, final List<StructurePiece> list, final Random random) {
            this.generateChildRight((StartPiece)civ, list, random, 0, 1, true);
        }
        
        public static CastleSmallCorridorRightTurnPiece createPiece(final List<StructurePiece> list, final Random random, final int integer3, final int integer4, final int integer5, final Direction fb, final int integer7) {
            final BoundingBox cic8 = BoundingBox.orientBox(integer3, integer4, integer5, -1, 0, 0, 5, 7, 5, fb);
            if (!NetherBridgePiece.isOkBox(cic8) || StructurePiece.findCollisionPiece(list, cic8) != null) {
                return null;
            }
            return new CastleSmallCorridorRightTurnPiece(integer7, random, cic8, fb);
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            this.generateBox(bhs, cic, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            final BlockState bvt6 = (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.WEST, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.EAST, true);
            final BlockState bvt7 = (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.SOUTH, true);
            this.generateBox(bhs, cic, 0, 2, 0, 0, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 3, 1, 0, 4, 1, bvt7, bvt7, false);
            this.generateBox(bhs, cic, 0, 3, 3, 0, 4, 3, bvt7, bvt7, false);
            this.generateBox(bhs, cic, 4, 2, 0, 4, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 1, 2, 4, 4, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 1, 3, 4, 1, 4, 4, bvt6, bvt6, false);
            this.generateBox(bhs, cic, 3, 3, 4, 3, 4, 4, bvt6, bvt6, false);
            if (this.isNeedingChest && cic.isInside(new BlockPos(this.getWorldX(1, 3), this.getWorldY(2), this.getWorldZ(1, 3)))) {
                this.isNeedingChest = false;
                this.createChest(bhs, cic, random, 1, 2, 3, BuiltInLootTables.NETHER_BRIDGE);
            }
            this.generateBox(bhs, cic, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            for (int integer8 = 0; integer8 <= 4; ++integer8) {
                for (int integer9 = 0; integer9 <= 4; ++integer9) {
                    this.fillColumnDown(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), integer8, -1, integer9, cic);
                }
            }
            return true;
        }
    }
    
    public static class CastleSmallCorridorLeftTurnPiece extends NetherBridgePiece {
        private boolean isNeedingChest;
        
        public CastleSmallCorridorLeftTurnPiece(final int integer, final Random random, final BoundingBox cic, final Direction fb) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_LEFT_TURN, integer);
            this.setOrientation(fb);
            this.boundingBox = cic;
            this.isNeedingChest = (random.nextInt(3) == 0);
        }
        
        public CastleSmallCorridorLeftTurnPiece(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_LEFT_TURN, id);
            this.isNeedingChest = id.getBoolean("Chest");
        }
        
        @Override
        protected void addAdditionalSaveData(final CompoundTag id) {
            super.addAdditionalSaveData(id);
            id.putBoolean("Chest", this.isNeedingChest);
        }
        
        @Override
        public void addChildren(final StructurePiece civ, final List<StructurePiece> list, final Random random) {
            this.generateChildLeft((StartPiece)civ, list, random, 0, 1, true);
        }
        
        public static CastleSmallCorridorLeftTurnPiece createPiece(final List<StructurePiece> list, final Random random, final int integer3, final int integer4, final int integer5, final Direction fb, final int integer7) {
            final BoundingBox cic8 = BoundingBox.orientBox(integer3, integer4, integer5, -1, 0, 0, 5, 7, 5, fb);
            if (!NetherBridgePiece.isOkBox(cic8) || StructurePiece.findCollisionPiece(list, cic8) != null) {
                return null;
            }
            return new CastleSmallCorridorLeftTurnPiece(integer7, random, cic8, fb);
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            this.generateBox(bhs, cic, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            final BlockState bvt6 = (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.WEST, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.EAST, true);
            final BlockState bvt7 = (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.SOUTH, true);
            this.generateBox(bhs, cic, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 4, 3, 1, 4, 4, 1, bvt7, bvt7, false);
            this.generateBox(bhs, cic, 4, 3, 3, 4, 4, 3, bvt7, bvt7, false);
            this.generateBox(bhs, cic, 0, 2, 0, 0, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 2, 4, 3, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 1, 3, 4, 1, 4, 4, bvt6, bvt6, false);
            this.generateBox(bhs, cic, 3, 3, 4, 3, 4, 4, bvt6, bvt6, false);
            if (this.isNeedingChest && cic.isInside(new BlockPos(this.getWorldX(3, 3), this.getWorldY(2), this.getWorldZ(3, 3)))) {
                this.isNeedingChest = false;
                this.createChest(bhs, cic, random, 3, 2, 3, BuiltInLootTables.NETHER_BRIDGE);
            }
            this.generateBox(bhs, cic, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            for (int integer8 = 0; integer8 <= 4; ++integer8) {
                for (int integer9 = 0; integer9 <= 4; ++integer9) {
                    this.fillColumnDown(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), integer8, -1, integer9, cic);
                }
            }
            return true;
        }
    }
    
    public static class CastleCorridorStairsPiece extends NetherBridgePiece {
        public CastleCorridorStairsPiece(final int integer, final BoundingBox cic, final Direction fb) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_CORRIDOR_STAIRS, integer);
            this.setOrientation(fb);
            this.boundingBox = cic;
        }
        
        public CastleCorridorStairsPiece(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_CORRIDOR_STAIRS, id);
        }
        
        @Override
        public void addChildren(final StructurePiece civ, final List<StructurePiece> list, final Random random) {
            this.generateChildForward((StartPiece)civ, list, random, 1, 0, true);
        }
        
        public static CastleCorridorStairsPiece createPiece(final List<StructurePiece> list, final int integer2, final int integer3, final int integer4, final Direction fb, final int integer6) {
            final BoundingBox cic7 = BoundingBox.orientBox(integer2, integer3, integer4, -1, -7, 0, 5, 14, 10, fb);
            if (!NetherBridgePiece.isOkBox(cic7) || StructurePiece.findCollisionPiece(list, cic7) != null) {
                return null;
            }
            return new CastleCorridorStairsPiece(integer6, cic7, fb);
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            final BlockState bvt6 = ((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_STAIRS.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)StairBlock.FACING, Direction.SOUTH);
            final BlockState bvt7 = (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.SOUTH, true);
            for (int integer8 = 0; integer8 <= 9; ++integer8) {
                final int integer9 = Math.max(1, 7 - integer8);
                final int integer10 = Math.min(Math.max(integer9 + 5, 14 - integer8), 13);
                final int integer11 = integer8;
                this.generateBox(bhs, cic, 0, 0, integer11, 4, integer9, integer11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
                this.generateBox(bhs, cic, 1, integer9 + 1, integer11, 3, integer10 - 1, integer11, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
                if (integer8 <= 6) {
                    this.placeBlock(bhs, bvt6, 1, integer9 + 1, integer11, cic);
                    this.placeBlock(bhs, bvt6, 2, integer9 + 1, integer11, cic);
                    this.placeBlock(bhs, bvt6, 3, integer9 + 1, integer11, cic);
                }
                this.generateBox(bhs, cic, 0, integer10, integer11, 4, integer10, integer11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
                this.generateBox(bhs, cic, 0, integer9 + 1, integer11, 0, integer10 - 1, integer11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
                this.generateBox(bhs, cic, 4, integer9 + 1, integer11, 4, integer10 - 1, integer11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
                if ((integer8 & 0x1) == 0x0) {
                    this.generateBox(bhs, cic, 0, integer9 + 2, integer11, 0, integer9 + 3, integer11, bvt7, bvt7, false);
                    this.generateBox(bhs, cic, 4, integer9 + 2, integer11, 4, integer9 + 3, integer11, bvt7, bvt7, false);
                }
                for (int integer12 = 0; integer12 <= 4; ++integer12) {
                    this.fillColumnDown(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), integer12, -1, integer11, cic);
                }
            }
            return true;
        }
    }
    
    public static class CastleCorridorTBalconyPiece extends NetherBridgePiece {
        public CastleCorridorTBalconyPiece(final int integer, final BoundingBox cic, final Direction fb) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_CORRIDOR_T_BALCONY, integer);
            this.setOrientation(fb);
            this.boundingBox = cic;
        }
        
        public CastleCorridorTBalconyPiece(final StructureManager cjp, final CompoundTag id) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_CORRIDOR_T_BALCONY, id);
        }
        
        @Override
        public void addChildren(final StructurePiece civ, final List<StructurePiece> list, final Random random) {
            int integer5 = 1;
            final Direction fb6 = this.getOrientation();
            if (fb6 == Direction.WEST || fb6 == Direction.NORTH) {
                integer5 = 5;
            }
            this.generateChildLeft((StartPiece)civ, list, random, 0, integer5, random.nextInt(8) > 0);
            this.generateChildRight((StartPiece)civ, list, random, 0, integer5, random.nextInt(8) > 0);
        }
        
        public static CastleCorridorTBalconyPiece createPiece(final List<StructurePiece> list, final int integer2, final int integer3, final int integer4, final Direction fb, final int integer6) {
            final BoundingBox cic7 = BoundingBox.orientBox(integer2, integer3, integer4, -3, 0, 0, 9, 7, 9, fb);
            if (!NetherBridgePiece.isOkBox(cic7) || StructurePiece.findCollisionPiece(list, cic7) != null) {
                return null;
            }
            return new CastleCorridorTBalconyPiece(integer6, cic7, fb);
        }
        
        @Override
        public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            final BlockState bvt6 = (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.NORTH, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.SOUTH, true);
            final BlockState bvt7 = (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.WEST, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.EAST, true);
            this.generateBox(bhs, cic, 0, 0, 0, 8, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 2, 0, 8, 5, 8, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 6, 0, 8, 6, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 0, 2, 0, 2, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 6, 2, 0, 8, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 1, 3, 0, 1, 4, 0, bvt7, bvt7, false);
            this.generateBox(bhs, cic, 7, 3, 0, 7, 4, 0, bvt7, bvt7, false);
            this.generateBox(bhs, cic, 0, 2, 4, 8, 2, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 1, 1, 4, 2, 2, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(bhs, cic, 6, 1, 4, 7, 2, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(bhs, cic, 1, 3, 8, 7, 3, 8, bvt7, bvt7, false);
            this.placeBlock(bhs, (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.EAST, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.SOUTH, true), 0, 3, 8, cic);
            this.placeBlock(bhs, (((AbstractStateHolder<O, BlockState>)Blocks.NETHER_BRICK_FENCE.defaultBlockState()).setValue((Property<Comparable>)FenceBlock.WEST, true)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.SOUTH, true), 8, 3, 8, cic);
            this.generateBox(bhs, cic, 0, 3, 6, 0, 3, 7, bvt6, bvt6, false);
            this.generateBox(bhs, cic, 8, 3, 6, 8, 3, 7, bvt6, bvt6, false);
            this.generateBox(bhs, cic, 0, 3, 4, 0, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 8, 3, 4, 8, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 1, 3, 5, 2, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 6, 3, 5, 7, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(bhs, cic, 1, 4, 5, 1, 5, 5, bvt7, bvt7, false);
            this.generateBox(bhs, cic, 7, 4, 5, 7, 5, 5, bvt7, bvt7, false);
            for (int integer8 = 0; integer8 <= 5; ++integer8) {
                for (int integer9 = 0; integer9 <= 8; ++integer9) {
                    this.fillColumnDown(bhs, Blocks.NETHER_BRICKS.defaultBlockState(), integer9, -1, integer8, cic);
                }
            }
            return true;
        }
    }
}
