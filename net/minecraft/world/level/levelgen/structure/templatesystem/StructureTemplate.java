package net.minecraft.world.level.levelgen.structure.templatesystem;

import net.minecraft.core.IdMapper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.IntTag;
import java.util.Comparator;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.EntityType;
import java.util.Optional;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.LiquidBlockContainer;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.phys.Vec3;
import java.util.function.Predicate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Iterator;
import java.util.Collection;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Vec3i;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import java.util.List;

public class StructureTemplate {
    private final List<List<StructureBlockInfo>> palettes;
    private final List<StructureEntityInfo> entityInfoList;
    private BlockPos size;
    private String author;
    
    public StructureTemplate() {
        this.palettes = (List<List<StructureBlockInfo>>)Lists.newArrayList();
        this.entityInfoList = (List<StructureEntityInfo>)Lists.newArrayList();
        this.size = BlockPos.ZERO;
        this.author = "?";
    }
    
    public BlockPos getSize() {
        return this.size;
    }
    
    public void setAuthor(final String string) {
        this.author = string;
    }
    
    public String getAuthor() {
        return this.author;
    }
    
    public void fillFromWorld(final Level bhr, final BlockPos ew2, final BlockPos ew3, final boolean boolean4, @Nullable final Block bmv) {
        if (ew3.getX() < 1 || ew3.getY() < 1 || ew3.getZ() < 1) {
            return;
        }
        final BlockPos ew4 = ew2.offset(ew3).offset(-1, -1, -1);
        final List<StructureBlockInfo> list8 = (List<StructureBlockInfo>)Lists.newArrayList();
        final List<StructureBlockInfo> list9 = (List<StructureBlockInfo>)Lists.newArrayList();
        final List<StructureBlockInfo> list10 = (List<StructureBlockInfo>)Lists.newArrayList();
        final BlockPos ew5 = new BlockPos(Math.min(ew2.getX(), ew4.getX()), Math.min(ew2.getY(), ew4.getY()), Math.min(ew2.getZ(), ew4.getZ()));
        final BlockPos ew6 = new BlockPos(Math.max(ew2.getX(), ew4.getX()), Math.max(ew2.getY(), ew4.getY()), Math.max(ew2.getZ(), ew4.getZ()));
        this.size = ew3;
        for (final BlockPos ew7 : BlockPos.betweenClosed(ew5, ew6)) {
            final BlockPos ew8 = ew7.subtract(ew5);
            final BlockState bvt16 = bhr.getBlockState(ew7);
            if (bmv != null && bmv == bvt16.getBlock()) {
                continue;
            }
            final BlockEntity btw17 = bhr.getBlockEntity(ew7);
            if (btw17 != null) {
                final CompoundTag id18 = btw17.save(new CompoundTag());
                id18.remove("x");
                id18.remove("y");
                id18.remove("z");
                list9.add(new StructureBlockInfo(ew8, bvt16, id18));
            }
            else if (bvt16.isSolidRender(bhr, ew7) || bvt16.isCollisionShapeFullBlock(bhr, ew7)) {
                list8.add(new StructureBlockInfo(ew8, bvt16, null));
            }
            else {
                list10.add(new StructureBlockInfo(ew8, bvt16, null));
            }
        }
        final List<StructureBlockInfo> list11 = (List<StructureBlockInfo>)Lists.newArrayList();
        list11.addAll((Collection)list8);
        list11.addAll((Collection)list9);
        list11.addAll((Collection)list10);
        this.palettes.clear();
        this.palettes.add(list11);
        if (boolean4) {
            this.fillEntityList(bhr, ew5, ew6.offset(1, 1, 1));
        }
        else {
            this.entityInfoList.clear();
        }
    }
    
    private void fillEntityList(final Level bhr, final BlockPos ew2, final BlockPos ew3) {
        final List<Entity> list5 = bhr.<Entity>getEntitiesOfClass((java.lang.Class<? extends Entity>)Entity.class, new AABB(ew2, ew3), (java.util.function.Predicate<? super Entity>)(aio -> !(aio instanceof Player)));
        this.entityInfoList.clear();
        for (final Entity aio7 : list5) {
            final Vec3 csi8 = new Vec3(aio7.x - ew2.getX(), aio7.y - ew2.getY(), aio7.z - ew2.getZ());
            final CompoundTag id9 = new CompoundTag();
            aio7.save(id9);
            BlockPos ew4;
            if (aio7 instanceof Painting) {
                ew4 = ((Painting)aio7).getPos().subtract(ew2);
            }
            else {
                ew4 = new BlockPos(csi8);
            }
            this.entityInfoList.add(new StructureEntityInfo(csi8, ew4, id9));
        }
    }
    
    public List<StructureBlockInfo> filterBlocks(final BlockPos ew, final StructurePlaceSettings cjq, final Block bmv) {
        return this.filterBlocks(ew, cjq, bmv, true);
    }
    
    public List<StructureBlockInfo> filterBlocks(final BlockPos ew, final StructurePlaceSettings cjq, final Block bmv, final boolean boolean4) {
        final List<StructureBlockInfo> list6 = (List<StructureBlockInfo>)Lists.newArrayList();
        final BoundingBox cic7 = cjq.getBoundingBox();
        for (final StructureBlockInfo b9 : cjq.getPalette(this.palettes, ew)) {
            final BlockPos ew2 = boolean4 ? calculateRelativePosition(cjq, b9.pos).offset(ew) : b9.pos;
            if (cic7 != null && !cic7.isInside(ew2)) {
                continue;
            }
            final BlockState bvt11 = b9.state;
            if (bvt11.getBlock() != bmv) {
                continue;
            }
            list6.add(new StructureBlockInfo(ew2, bvt11.rotate(cjq.getRotation()), b9.nbt));
        }
        return list6;
    }
    
    public BlockPos calculateConnectedPosition(final StructurePlaceSettings cjq1, final BlockPos ew2, final StructurePlaceSettings cjq3, final BlockPos ew4) {
        final BlockPos ew5 = calculateRelativePosition(cjq1, ew2);
        final BlockPos ew6 = calculateRelativePosition(cjq3, ew4);
        return ew5.subtract(ew6);
    }
    
    public static BlockPos calculateRelativePosition(final StructurePlaceSettings cjq, final BlockPos ew) {
        return transform(ew, cjq.getMirror(), cjq.getRotation(), cjq.getRotationPivot());
    }
    
    public void placeInWorldChunk(final LevelAccessor bhs, final BlockPos ew, final StructurePlaceSettings cjq) {
        cjq.updateBoundingBoxFromChunkPos();
        this.placeInWorld(bhs, ew, cjq);
    }
    
    public void placeInWorld(final LevelAccessor bhs, final BlockPos ew, final StructurePlaceSettings cjq) {
        this.placeInWorld(bhs, ew, cjq, 2);
    }
    
    public boolean placeInWorld(final LevelAccessor bhs, final BlockPos ew, final StructurePlaceSettings cjq, final int integer) {
        if (this.palettes.isEmpty()) {
            return false;
        }
        final List<StructureBlockInfo> list6 = cjq.getPalette(this.palettes, ew);
        if ((list6.isEmpty() && (cjq.isIgnoreEntities() || this.entityInfoList.isEmpty())) || this.size.getX() < 1 || this.size.getY() < 1 || this.size.getZ() < 1) {
            return false;
        }
        final BoundingBox cic7 = cjq.getBoundingBox();
        final List<BlockPos> list7 = (List<BlockPos>)Lists.newArrayListWithCapacity(cjq.shouldKeepLiquids() ? list6.size() : 0);
        final List<Pair<BlockPos, CompoundTag>> list8 = (List<Pair<BlockPos, CompoundTag>>)Lists.newArrayListWithCapacity(list6.size());
        int integer2 = Integer.MAX_VALUE;
        int integer3 = Integer.MAX_VALUE;
        int integer4 = Integer.MAX_VALUE;
        int integer5 = Integer.MIN_VALUE;
        int integer6 = Integer.MIN_VALUE;
        int integer7 = Integer.MIN_VALUE;
        final List<StructureBlockInfo> list9 = processBlockInfos(bhs, ew, cjq, list6);
        for (final StructureBlockInfo b18 : list9) {
            final BlockPos ew2 = b18.pos;
            if (cic7 != null && !cic7.isInside(ew2)) {
                continue;
            }
            final FluidState clk20 = cjq.shouldKeepLiquids() ? bhs.getFluidState(ew2) : null;
            final BlockState bvt21 = b18.state.mirror(cjq.getMirror()).rotate(cjq.getRotation());
            if (b18.nbt != null) {
                final BlockEntity btw22 = bhs.getBlockEntity(ew2);
                Clearable.tryClear(btw22);
                bhs.setBlock(ew2, Blocks.BARRIER.defaultBlockState(), 20);
            }
            if (!bhs.setBlock(ew2, bvt21, integer)) {
                continue;
            }
            integer2 = Math.min(integer2, ew2.getX());
            integer3 = Math.min(integer3, ew2.getY());
            integer4 = Math.min(integer4, ew2.getZ());
            integer5 = Math.max(integer5, ew2.getX());
            integer6 = Math.max(integer6, ew2.getY());
            integer7 = Math.max(integer7, ew2.getZ());
            list8.add(Pair.of((Object)ew2, (Object)b18.nbt));
            if (b18.nbt != null) {
                final BlockEntity btw22 = bhs.getBlockEntity(ew2);
                if (btw22 != null) {
                    b18.nbt.putInt("x", ew2.getX());
                    b18.nbt.putInt("y", ew2.getY());
                    b18.nbt.putInt("z", ew2.getZ());
                    btw22.load(b18.nbt);
                    btw22.mirror(cjq.getMirror());
                    btw22.rotate(cjq.getRotation());
                }
            }
            if (clk20 == null || !(bvt21.getBlock() instanceof LiquidBlockContainer)) {
                continue;
            }
            ((LiquidBlockContainer)bvt21.getBlock()).placeLiquid(bhs, ew2, bvt21, clk20);
            if (clk20.isSource()) {
                continue;
            }
            list7.add(ew2);
        }
        boolean boolean17 = true;
        final Direction[] arr18 = { Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };
        while (boolean17 && !list7.isEmpty()) {
            boolean17 = false;
            final Iterator<BlockPos> iterator19 = (Iterator<BlockPos>)list7.iterator();
            while (iterator19.hasNext()) {
                BlockPos ew4;
                final BlockPos ew3 = ew4 = (BlockPos)iterator19.next();
                FluidState clk21 = bhs.getFluidState(ew4);
                for (int integer8 = 0; integer8 < arr18.length && !clk21.isSource(); ++integer8) {
                    final BlockPos ew5 = ew4.relative(arr18[integer8]);
                    final FluidState clk22 = bhs.getFluidState(ew5);
                    if (clk22.getHeight(bhs, ew5) > clk21.getHeight(bhs, ew4) || (clk22.isSource() && !clk21.isSource())) {
                        clk21 = clk22;
                        ew4 = ew5;
                    }
                }
                if (clk21.isSource()) {
                    final BlockState bvt22 = bhs.getBlockState(ew3);
                    final Block bmv24 = bvt22.getBlock();
                    if (!(bmv24 instanceof LiquidBlockContainer)) {
                        continue;
                    }
                    ((LiquidBlockContainer)bmv24).placeLiquid(bhs, ew3, bvt22, clk21);
                    boolean17 = true;
                    iterator19.remove();
                }
            }
        }
        if (integer2 <= integer5) {
            if (!cjq.getKnownShape()) {
                final DiscreteVoxelShape csr19 = new BitSetDiscreteVoxelShape(integer5 - integer2 + 1, integer6 - integer3 + 1, integer7 - integer4 + 1);
                final int integer9 = integer2;
                final int integer10 = integer3;
                final int integer11 = integer4;
                for (final Pair<BlockPos, CompoundTag> pair24 : list8) {
                    final BlockPos ew6 = (BlockPos)pair24.getFirst();
                    csr19.setFull(ew6.getX() - integer9, ew6.getY() - integer10, ew6.getZ() - integer11, true, true);
                }
                updateShapeAtEdge(bhs, integer, csr19, integer9, integer10, integer11);
            }
            for (final Pair<BlockPos, CompoundTag> pair25 : list8) {
                final BlockPos ew4 = (BlockPos)pair25.getFirst();
                if (!cjq.getKnownShape()) {
                    final BlockState bvt23 = bhs.getBlockState(ew4);
                    final BlockState bvt22 = Block.updateFromNeighbourShapes(bvt23, bhs, ew4);
                    if (bvt23 != bvt22) {
                        bhs.setBlock(ew4, bvt22, (integer & 0xFFFFFFFE) | 0x10);
                    }
                    bhs.blockUpdated(ew4, bvt22.getBlock());
                }
                if (pair25.getSecond() != null) {
                    final BlockEntity btw22 = bhs.getBlockEntity(ew4);
                    if (btw22 == null) {
                        continue;
                    }
                    btw22.setChanged();
                }
            }
        }
        if (!cjq.isIgnoreEntities()) {
            this.placeEntities(bhs, ew, cjq.getMirror(), cjq.getRotation(), cjq.getRotationPivot(), cic7);
        }
        return true;
    }
    
    public static void updateShapeAtEdge(final LevelAccessor bhs, final int integer2, final DiscreteVoxelShape csr, final int integer4, final int integer5, final int integer6) {
        final BlockPos ew10;
        final BlockPos ew11;
        final BlockState bvt12;
        final BlockState bvt13;
        final BlockState bvt14;
        final BlockState bvt15;
        csr.forAllFaces((fb, integer7, integer8, integer9) -> {
            ew10 = new BlockPos(integer4 + integer7, integer5 + integer8, integer6 + integer9);
            ew11 = ew10.relative(fb);
            bvt12 = bhs.getBlockState(ew10);
            bvt13 = bhs.getBlockState(ew11);
            bvt14 = bvt12.updateShape(fb, bvt13, bhs, ew10, ew11);
            if (bvt12 != bvt14) {
                bhs.setBlock(ew10, bvt14, (integer2 & 0xFFFFFFFE) | 0x10);
            }
            bvt15 = bvt13.updateShape(fb.getOpposite(), bvt14, bhs, ew11, ew10);
            if (bvt13 != bvt15) {
                bhs.setBlock(ew11, bvt15, (integer2 & 0xFFFFFFFE) | 0x10);
            }
        });
    }
    
    public static List<StructureBlockInfo> processBlockInfos(final LevelAccessor bhs, final BlockPos ew, final StructurePlaceSettings cjq, final List<StructureBlockInfo> list) {
        final List<StructureBlockInfo> list2 = (List<StructureBlockInfo>)Lists.newArrayList();
        for (final StructureBlockInfo b7 : list) {
            final BlockPos ew2 = calculateRelativePosition(cjq, b7.pos).offset(ew);
            StructureBlockInfo b8 = new StructureBlockInfo(ew2, b7.state, b7.nbt);
            for (Iterator<StructureProcessor> iterator10 = (Iterator<StructureProcessor>)cjq.getProcessors().iterator(); b8 != null && iterator10.hasNext(); b8 = ((StructureProcessor)iterator10.next()).processBlock(bhs, ew, b7, b8, cjq)) {}
            if (b8 != null) {
                list2.add(b8);
            }
        }
        return list2;
    }
    
    private void placeEntities(final LevelAccessor bhs, final BlockPos ew2, final Mirror bqg, final Rotation brg, final BlockPos ew5, @Nullable final BoundingBox cic) {
        for (final StructureEntityInfo c9 : this.entityInfoList) {
            final BlockPos ew6 = transform(c9.blockPos, bqg, brg, ew5).offset(ew2);
            if (cic != null && !cic.isInside(ew6)) {
                continue;
            }
            final CompoundTag id11 = c9.nbt;
            final Vec3 csi12 = transform(c9.pos, bqg, brg, ew5);
            final Vec3 csi13 = csi12.add(ew2.getX(), ew2.getY(), ew2.getZ());
            final ListTag ik14 = new ListTag();
            ik14.add(new DoubleTag(csi13.x));
            ik14.add(new DoubleTag(csi13.y));
            ik14.add(new DoubleTag(csi13.z));
            id11.put("Pos", (Tag)ik14);
            id11.remove("UUIDMost");
            id11.remove("UUIDLeast");
            createEntityIgnoreException(bhs, id11).ifPresent(aio -> {
                float float6 = aio.mirror(bqg);
                float6 += aio.yRot - aio.rotate(brg);
                aio.moveTo(csi13.x, csi13.y, csi13.z, float6, aio.xRot);
                bhs.addFreshEntity(aio);
            });
        }
    }
    
    private static Optional<Entity> createEntityIgnoreException(final LevelAccessor bhs, final CompoundTag id) {
        try {
            return EntityType.create(id, bhs.getLevel());
        }
        catch (Exception exception3) {
            return (Optional<Entity>)Optional.empty();
        }
    }
    
    public BlockPos getSize(final Rotation brg) {
        switch (brg) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90: {
                return new BlockPos(this.size.getZ(), this.size.getY(), this.size.getX());
            }
            default: {
                return this.size;
            }
        }
    }
    
    public static BlockPos transform(final BlockPos ew1, final Mirror bqg, final Rotation brg, final BlockPos ew4) {
        int integer5 = ew1.getX();
        final int integer6 = ew1.getY();
        int integer7 = ew1.getZ();
        boolean boolean8 = true;
        switch (bqg) {
            case LEFT_RIGHT: {
                integer7 = -integer7;
                break;
            }
            case FRONT_BACK: {
                integer5 = -integer5;
                break;
            }
            default: {
                boolean8 = false;
                break;
            }
        }
        final int integer8 = ew4.getX();
        final int integer9 = ew4.getZ();
        switch (brg) {
            case CLOCKWISE_180: {
                return new BlockPos(integer8 + integer8 - integer5, integer6, integer9 + integer9 - integer7);
            }
            case COUNTERCLOCKWISE_90: {
                return new BlockPos(integer8 - integer9 + integer7, integer6, integer8 + integer9 - integer5);
            }
            case CLOCKWISE_90: {
                return new BlockPos(integer8 + integer9 - integer7, integer6, integer9 - integer8 + integer5);
            }
            default: {
                return boolean8 ? new BlockPos(integer5, integer6, integer7) : ew1;
            }
        }
    }
    
    private static Vec3 transform(final Vec3 csi, final Mirror bqg, final Rotation brg, final BlockPos ew) {
        double double5 = csi.x;
        final double double6 = csi.y;
        double double7 = csi.z;
        boolean boolean11 = true;
        switch (bqg) {
            case LEFT_RIGHT: {
                double7 = 1.0 - double7;
                break;
            }
            case FRONT_BACK: {
                double5 = 1.0 - double5;
                break;
            }
            default: {
                boolean11 = false;
                break;
            }
        }
        final int integer12 = ew.getX();
        final int integer13 = ew.getZ();
        switch (brg) {
            case CLOCKWISE_180: {
                return new Vec3(integer12 + integer12 + 1 - double5, double6, integer13 + integer13 + 1 - double7);
            }
            case COUNTERCLOCKWISE_90: {
                return new Vec3(integer12 - integer13 + double7, double6, integer12 + integer13 + 1 - double5);
            }
            case CLOCKWISE_90: {
                return new Vec3(integer12 + integer13 + 1 - double7, double6, integer13 - integer12 + double5);
            }
            default: {
                return boolean11 ? new Vec3(double5, double6, double7) : csi;
            }
        }
    }
    
    public BlockPos getZeroPositionWithTransform(final BlockPos ew, final Mirror bqg, final Rotation brg) {
        return getZeroPositionWithTransform(ew, bqg, brg, this.getSize().getX(), this.getSize().getZ());
    }
    
    public static BlockPos getZeroPositionWithTransform(final BlockPos ew, final Mirror bqg, final Rotation brg, int integer4, int integer5) {
        --integer4;
        --integer5;
        final int integer6 = (bqg == Mirror.FRONT_BACK) ? integer4 : 0;
        final int integer7 = (bqg == Mirror.LEFT_RIGHT) ? integer5 : 0;
        BlockPos ew2 = ew;
        switch (brg) {
            case NONE: {
                ew2 = ew.offset(integer6, 0, integer7);
                break;
            }
            case CLOCKWISE_90: {
                ew2 = ew.offset(integer5 - integer7, 0, integer6);
                break;
            }
            case CLOCKWISE_180: {
                ew2 = ew.offset(integer4 - integer6, 0, integer5 - integer7);
                break;
            }
            case COUNTERCLOCKWISE_90: {
                ew2 = ew.offset(integer7, 0, integer4 - integer6);
                break;
            }
        }
        return ew2;
    }
    
    public BoundingBox getBoundingBox(final StructurePlaceSettings cjq, final BlockPos ew) {
        final Rotation brg4 = cjq.getRotation();
        final BlockPos ew2 = cjq.getRotationPivot();
        final BlockPos ew3 = this.getSize(brg4);
        final Mirror bqg7 = cjq.getMirror();
        final int integer8 = ew2.getX();
        final int integer9 = ew2.getZ();
        final int integer10 = ew3.getX() - 1;
        final int integer11 = ew3.getY() - 1;
        final int integer12 = ew3.getZ() - 1;
        BoundingBox cic13 = new BoundingBox(0, 0, 0, 0, 0, 0);
        switch (brg4) {
            case NONE: {
                cic13 = new BoundingBox(0, 0, 0, integer10, integer11, integer12);
                break;
            }
            case CLOCKWISE_180: {
                cic13 = new BoundingBox(integer8 + integer8 - integer10, 0, integer9 + integer9 - integer12, integer8 + integer8, integer11, integer9 + integer9);
                break;
            }
            case COUNTERCLOCKWISE_90: {
                cic13 = new BoundingBox(integer8 - integer9, 0, integer8 + integer9 - integer12, integer8 - integer9 + integer10, integer11, integer8 + integer9);
                break;
            }
            case CLOCKWISE_90: {
                cic13 = new BoundingBox(integer8 + integer9 - integer10, 0, integer9 - integer8, integer8 + integer9, integer11, integer9 - integer8 + integer12);
                break;
            }
        }
        switch (bqg7) {
            case FRONT_BACK: {
                this.mirrorAABB(brg4, integer10, integer12, cic13, Direction.WEST, Direction.EAST);
                break;
            }
            case LEFT_RIGHT: {
                this.mirrorAABB(brg4, integer12, integer10, cic13, Direction.NORTH, Direction.SOUTH);
                break;
            }
        }
        cic13.move(ew.getX(), ew.getY(), ew.getZ());
        return cic13;
    }
    
    private void mirrorAABB(final Rotation brg, final int integer2, final int integer3, final BoundingBox cic, final Direction fb5, final Direction fb6) {
        BlockPos ew8 = BlockPos.ZERO;
        if (brg == Rotation.CLOCKWISE_90 || brg == Rotation.COUNTERCLOCKWISE_90) {
            ew8 = ew8.relative(brg.rotate(fb5), integer3);
        }
        else if (brg == Rotation.CLOCKWISE_180) {
            ew8 = ew8.relative(fb6, integer2);
        }
        else {
            ew8 = ew8.relative(fb5, integer2);
        }
        cic.move(ew8.getX(), 0, ew8.getZ());
    }
    
    public CompoundTag save(final CompoundTag id) {
        if (this.palettes.isEmpty()) {
            id.put("blocks", (Tag)new ListTag());
            id.put("palette", (Tag)new ListTag());
        }
        else {
            final List<SimplePalette> list3 = (List<SimplePalette>)Lists.newArrayList();
            final SimplePalette a4 = new SimplePalette();
            list3.add(a4);
            for (int integer5 = 1; integer5 < this.palettes.size(); ++integer5) {
                list3.add(new SimplePalette());
            }
            final ListTag ik5 = new ListTag();
            final List<StructureBlockInfo> list4 = (List<StructureBlockInfo>)this.palettes.get(0);
            for (int integer6 = 0; integer6 < list4.size(); ++integer6) {
                final StructureBlockInfo b8 = (StructureBlockInfo)list4.get(integer6);
                final CompoundTag id2 = new CompoundTag();
                id2.put("pos", (Tag)this.newIntegerList(b8.pos.getX(), b8.pos.getY(), b8.pos.getZ()));
                final int integer7 = a4.idFor(b8.state);
                id2.putInt("state", integer7);
                if (b8.nbt != null) {
                    id2.put("nbt", (Tag)b8.nbt);
                }
                ik5.add(id2);
                for (int integer8 = 1; integer8 < this.palettes.size(); ++integer8) {
                    final SimplePalette a5 = (SimplePalette)list3.get(integer8);
                    a5.addMapping(((StructureBlockInfo)((List)this.palettes.get(integer8)).get(integer6)).state, integer7);
                }
            }
            id.put("blocks", (Tag)ik5);
            if (list3.size() == 1) {
                final ListTag ik6 = new ListTag();
                for (final BlockState bvt9 : a4) {
                    ik6.add(NbtUtils.writeBlockState(bvt9));
                }
                id.put("palette", (Tag)ik6);
            }
            else {
                final ListTag ik6 = new ListTag();
                for (final SimplePalette a6 : list3) {
                    final ListTag ik7 = new ListTag();
                    for (final BlockState bvt10 : a6) {
                        ik7.add(NbtUtils.writeBlockState(bvt10));
                    }
                    ik6.add(ik7);
                }
                id.put("palettes", (Tag)ik6);
            }
        }
        final ListTag ik8 = new ListTag();
        for (final StructureEntityInfo c5 : this.entityInfoList) {
            final CompoundTag id3 = new CompoundTag();
            id3.put("pos", (Tag)this.newDoubleList(c5.pos.x, c5.pos.y, c5.pos.z));
            id3.put("blockPos", (Tag)this.newIntegerList(c5.blockPos.getX(), c5.blockPos.getY(), c5.blockPos.getZ()));
            if (c5.nbt != null) {
                id3.put("nbt", (Tag)c5.nbt);
            }
            ik8.add(id3);
        }
        id.put("entities", (Tag)ik8);
        id.put("size", (Tag)this.newIntegerList(this.size.getX(), this.size.getY(), this.size.getZ()));
        id.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
        return id;
    }
    
    public void load(final CompoundTag id) {
        this.palettes.clear();
        this.entityInfoList.clear();
        final ListTag ik3 = id.getList("size", 3);
        this.size = new BlockPos(ik3.getInt(0), ik3.getInt(1), ik3.getInt(2));
        final ListTag ik4 = id.getList("blocks", 10);
        if (id.contains("palettes", 9)) {
            final ListTag ik5 = id.getList("palettes", 9);
            for (int integer6 = 0; integer6 < ik5.size(); ++integer6) {
                this.loadPalette(ik5.getList(integer6), ik4);
            }
        }
        else {
            this.loadPalette(id.getList("palette", 10), ik4);
        }
        final ListTag ik5 = id.getList("entities", 10);
        for (int integer6 = 0; integer6 < ik5.size(); ++integer6) {
            final CompoundTag id2 = ik5.getCompound(integer6);
            final ListTag ik6 = id2.getList("pos", 6);
            final Vec3 csi9 = new Vec3(ik6.getDouble(0), ik6.getDouble(1), ik6.getDouble(2));
            final ListTag ik7 = id2.getList("blockPos", 3);
            final BlockPos ew11 = new BlockPos(ik7.getInt(0), ik7.getInt(1), ik7.getInt(2));
            if (id2.contains("nbt")) {
                final CompoundTag id3 = id2.getCompound("nbt");
                this.entityInfoList.add(new StructureEntityInfo(csi9, ew11, id3));
            }
        }
    }
    
    private void loadPalette(final ListTag ik1, final ListTag ik2) {
        final SimplePalette a4 = new SimplePalette();
        final List<StructureBlockInfo> list5 = (List<StructureBlockInfo>)Lists.newArrayList();
        for (int integer6 = 0; integer6 < ik1.size(); ++integer6) {
            a4.addMapping(NbtUtils.readBlockState(ik1.getCompound(integer6)), integer6);
        }
        for (int integer6 = 0; integer6 < ik2.size(); ++integer6) {
            final CompoundTag id7 = ik2.getCompound(integer6);
            final ListTag ik3 = id7.getList("pos", 3);
            final BlockPos ew9 = new BlockPos(ik3.getInt(0), ik3.getInt(1), ik3.getInt(2));
            final BlockState bvt10 = a4.stateFor(id7.getInt("state"));
            CompoundTag id8;
            if (id7.contains("nbt")) {
                id8 = id7.getCompound("nbt");
            }
            else {
                id8 = null;
            }
            list5.add(new StructureBlockInfo(ew9, bvt10, id8));
        }
        list5.sort(Comparator.comparingInt(b -> b.pos.getY()));
        this.palettes.add(list5);
    }
    
    private ListTag newIntegerList(final int... arr) {
        final ListTag ik3 = new ListTag();
        for (final int integer7 : arr) {
            ik3.add(new IntTag(integer7));
        }
        return ik3;
    }
    
    private ListTag newDoubleList(final double... arr) {
        final ListTag ik3 = new ListTag();
        for (final double double7 : arr) {
            ik3.add(new DoubleTag(double7));
        }
        return ik3;
    }
    
    static class SimplePalette implements Iterable<BlockState> {
        public static final BlockState DEFAULT_BLOCK_STATE;
        private final IdMapper<BlockState> ids;
        private int lastId;
        
        private SimplePalette() {
            this.ids = new IdMapper<BlockState>(16);
        }
        
        public int idFor(final BlockState bvt) {
            int integer3 = this.ids.getId(bvt);
            if (integer3 == -1) {
                integer3 = this.lastId++;
                this.ids.addMapping(bvt, integer3);
            }
            return integer3;
        }
        
        @Nullable
        public BlockState stateFor(final int integer) {
            final BlockState bvt3 = this.ids.byId(integer);
            return (bvt3 == null) ? SimplePalette.DEFAULT_BLOCK_STATE : bvt3;
        }
        
        public Iterator<BlockState> iterator() {
            return this.ids.iterator();
        }
        
        public void addMapping(final BlockState bvt, final int integer) {
            this.ids.addMapping(bvt, integer);
        }
        
        static {
            DEFAULT_BLOCK_STATE = Blocks.AIR.defaultBlockState();
        }
    }
    
    public static class StructureBlockInfo {
        public final BlockPos pos;
        public final BlockState state;
        public final CompoundTag nbt;
        
        public StructureBlockInfo(final BlockPos ew, final BlockState bvt, @Nullable final CompoundTag id) {
            this.pos = ew;
            this.state = bvt;
            this.nbt = id;
        }
        
        public String toString() {
            return String.format("<StructureBlockInfo | %s | %s | %s>", new Object[] { this.pos, this.state, this.nbt });
        }
    }
    
    public static class StructureEntityInfo {
        public final Vec3 pos;
        public final BlockPos blockPos;
        public final CompoundTag nbt;
        
        public StructureEntityInfo(final Vec3 csi, final BlockPos ew, final CompoundTag id) {
            this.pos = csi;
            this.blockPos = ew;
            this.nbt = id;
        }
    }
}
