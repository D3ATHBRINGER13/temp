package net.minecraft.world.level.levelgen.structure;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.core.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import java.util.Iterator;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import java.util.Random;
import java.util.List;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import java.util.Set;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public abstract class StructurePiece {
    protected static final BlockState CAVE_AIR;
    protected BoundingBox boundingBox;
    @Nullable
    private Direction orientation;
    private Mirror mirror;
    private Rotation rotation;
    protected int genDepth;
    private final StructurePieceType type;
    private static final Set<Block> SHAPE_CHECK_BLOCKS;
    
    protected StructurePiece(final StructurePieceType cev, final int integer) {
        this.type = cev;
        this.genDepth = integer;
    }
    
    public StructurePiece(final StructurePieceType cev, final CompoundTag id) {
        this(cev, id.getInt("GD"));
        if (id.contains("BB")) {
            this.boundingBox = new BoundingBox(id.getIntArray("BB"));
        }
        final int integer4 = id.getInt("O");
        this.setOrientation((integer4 == -1) ? null : Direction.from2DDataValue(integer4));
    }
    
    public final CompoundTag createTag() {
        final CompoundTag id2 = new CompoundTag();
        id2.putString("id", Registry.STRUCTURE_PIECE.getKey(this.getType()).toString());
        id2.put("BB", (Tag)this.boundingBox.createTag());
        final Direction fb3 = this.getOrientation();
        id2.putInt("O", (fb3 == null) ? -1 : fb3.get2DDataValue());
        id2.putInt("GD", this.genDepth);
        this.addAdditionalSaveData(id2);
        return id2;
    }
    
    protected abstract void addAdditionalSaveData(final CompoundTag id);
    
    public void addChildren(final StructurePiece civ, final List<StructurePiece> list, final Random random) {
    }
    
    public abstract boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd);
    
    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }
    
    public int getGenDepth() {
        return this.genDepth;
    }
    
    public boolean isCloseToChunk(final ChunkPos bhd, final int integer) {
        final int integer2 = bhd.x << 4;
        final int integer3 = bhd.z << 4;
        return this.boundingBox.intersects(integer2 - integer, integer3 - integer, integer2 + 15 + integer, integer3 + 15 + integer);
    }
    
    public static StructurePiece findCollisionPiece(final List<StructurePiece> list, final BoundingBox cic) {
        for (final StructurePiece civ4 : list) {
            if (civ4.getBoundingBox() != null && civ4.getBoundingBox().intersects(cic)) {
                return civ4;
            }
        }
        return null;
    }
    
    protected boolean edgesLiquid(final BlockGetter bhb, final BoundingBox cic) {
        final int integer4 = Math.max(this.boundingBox.x0 - 1, cic.x0);
        final int integer5 = Math.max(this.boundingBox.y0 - 1, cic.y0);
        final int integer6 = Math.max(this.boundingBox.z0 - 1, cic.z0);
        final int integer7 = Math.min(this.boundingBox.x1 + 1, cic.x1);
        final int integer8 = Math.min(this.boundingBox.y1 + 1, cic.y1);
        final int integer9 = Math.min(this.boundingBox.z1 + 1, cic.z1);
        final BlockPos.MutableBlockPos a10 = new BlockPos.MutableBlockPos();
        for (int integer10 = integer4; integer10 <= integer7; ++integer10) {
            for (int integer11 = integer6; integer11 <= integer9; ++integer11) {
                if (bhb.getBlockState(a10.set(integer10, integer5, integer11)).getMaterial().isLiquid()) {
                    return true;
                }
                if (bhb.getBlockState(a10.set(integer10, integer8, integer11)).getMaterial().isLiquid()) {
                    return true;
                }
            }
        }
        for (int integer10 = integer4; integer10 <= integer7; ++integer10) {
            for (int integer11 = integer5; integer11 <= integer8; ++integer11) {
                if (bhb.getBlockState(a10.set(integer10, integer11, integer6)).getMaterial().isLiquid()) {
                    return true;
                }
                if (bhb.getBlockState(a10.set(integer10, integer11, integer9)).getMaterial().isLiquid()) {
                    return true;
                }
            }
        }
        for (int integer10 = integer6; integer10 <= integer9; ++integer10) {
            for (int integer11 = integer5; integer11 <= integer8; ++integer11) {
                if (bhb.getBlockState(a10.set(integer4, integer11, integer10)).getMaterial().isLiquid()) {
                    return true;
                }
                if (bhb.getBlockState(a10.set(integer7, integer11, integer10)).getMaterial().isLiquid()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    protected int getWorldX(final int integer1, final int integer2) {
        final Direction fb4 = this.getOrientation();
        if (fb4 == null) {
            return integer1;
        }
        switch (fb4) {
            case NORTH:
            case SOUTH: {
                return this.boundingBox.x0 + integer1;
            }
            case WEST: {
                return this.boundingBox.x1 - integer2;
            }
            case EAST: {
                return this.boundingBox.x0 + integer2;
            }
            default: {
                return integer1;
            }
        }
    }
    
    protected int getWorldY(final int integer) {
        if (this.getOrientation() == null) {
            return integer;
        }
        return integer + this.boundingBox.y0;
    }
    
    protected int getWorldZ(final int integer1, final int integer2) {
        final Direction fb4 = this.getOrientation();
        if (fb4 == null) {
            return integer2;
        }
        switch (fb4) {
            case NORTH: {
                return this.boundingBox.z1 - integer2;
            }
            case SOUTH: {
                return this.boundingBox.z0 + integer2;
            }
            case WEST:
            case EAST: {
                return this.boundingBox.z0 + integer1;
            }
            default: {
                return integer2;
            }
        }
    }
    
    protected void placeBlock(final LevelAccessor bhs, BlockState bvt, final int integer3, final int integer4, final int integer5, final BoundingBox cic) {
        final BlockPos ew8 = new BlockPos(this.getWorldX(integer3, integer5), this.getWorldY(integer4), this.getWorldZ(integer3, integer5));
        if (!cic.isInside(ew8)) {
            return;
        }
        if (this.mirror != Mirror.NONE) {
            bvt = bvt.mirror(this.mirror);
        }
        if (this.rotation != Rotation.NONE) {
            bvt = bvt.rotate(this.rotation);
        }
        bhs.setBlock(ew8, bvt, 2);
        final FluidState clk9 = bhs.getFluidState(ew8);
        if (!clk9.isEmpty()) {
            bhs.getLiquidTicks().scheduleTick(ew8, clk9.getType(), 0);
        }
        if (StructurePiece.SHAPE_CHECK_BLOCKS.contains(bvt.getBlock())) {
            bhs.getChunk(ew8).markPosForPostprocessing(ew8);
        }
    }
    
    protected BlockState getBlock(final BlockGetter bhb, final int integer2, final int integer3, final int integer4, final BoundingBox cic) {
        final int integer5 = this.getWorldX(integer2, integer4);
        final int integer6 = this.getWorldY(integer3);
        final int integer7 = this.getWorldZ(integer2, integer4);
        final BlockPos ew10 = new BlockPos(integer5, integer6, integer7);
        if (!cic.isInside(ew10)) {
            return Blocks.AIR.defaultBlockState();
        }
        return bhb.getBlockState(ew10);
    }
    
    protected boolean isInterior(final LevelReader bhu, final int integer2, final int integer3, final int integer4, final BoundingBox cic) {
        final int integer5 = this.getWorldX(integer2, integer4);
        final int integer6 = this.getWorldY(integer3 + 1);
        final int integer7 = this.getWorldZ(integer2, integer4);
        final BlockPos ew10 = new BlockPos(integer5, integer6, integer7);
        return cic.isInside(ew10) && integer6 < bhu.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, integer5, integer7);
    }
    
    protected void generateAirBox(final LevelAccessor bhs, final BoundingBox cic, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final int integer8) {
        for (int integer9 = integer4; integer9 <= integer7; ++integer9) {
            for (int integer10 = integer3; integer10 <= integer6; ++integer10) {
                for (int integer11 = integer5; integer11 <= integer8; ++integer11) {
                    this.placeBlock(bhs, Blocks.AIR.defaultBlockState(), integer10, integer9, integer11, cic);
                }
            }
        }
    }
    
    protected void generateBox(final LevelAccessor bhs, final BoundingBox cic, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final int integer8, final BlockState bvt9, final BlockState bvt10, final boolean boolean11) {
        for (int integer9 = integer4; integer9 <= integer7; ++integer9) {
            for (int integer10 = integer3; integer10 <= integer6; ++integer10) {
                for (int integer11 = integer5; integer11 <= integer8; ++integer11) {
                    if (!boolean11 || !this.getBlock(bhs, integer10, integer9, integer11, cic).isAir()) {
                        if (integer9 == integer4 || integer9 == integer7 || integer10 == integer3 || integer10 == integer6 || integer11 == integer5 || integer11 == integer8) {
                            this.placeBlock(bhs, bvt9, integer10, integer9, integer11, cic);
                        }
                        else {
                            this.placeBlock(bhs, bvt10, integer10, integer9, integer11, cic);
                        }
                    }
                }
            }
        }
    }
    
    protected void generateBox(final LevelAccessor bhs, final BoundingBox cic, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final int integer8, final boolean boolean9, final Random random, final BlockSelector a) {
        for (int integer9 = integer4; integer9 <= integer7; ++integer9) {
            for (int integer10 = integer3; integer10 <= integer6; ++integer10) {
                for (int integer11 = integer5; integer11 <= integer8; ++integer11) {
                    if (!boolean9 || !this.getBlock(bhs, integer10, integer9, integer11, cic).isAir()) {
                        a.next(random, integer10, integer9, integer11, integer9 == integer4 || integer9 == integer7 || integer10 == integer3 || integer10 == integer6 || integer11 == integer5 || integer11 == integer8);
                        this.placeBlock(bhs, a.getNext(), integer10, integer9, integer11, cic);
                    }
                }
            }
        }
    }
    
    protected void generateMaybeBox(final LevelAccessor bhs, final BoundingBox cic, final Random random, final float float4, final int integer5, final int integer6, final int integer7, final int integer8, final int integer9, final int integer10, final BlockState bvt11, final BlockState bvt12, final boolean boolean13, final boolean boolean14) {
        for (int integer11 = integer6; integer11 <= integer9; ++integer11) {
            for (int integer12 = integer5; integer12 <= integer8; ++integer12) {
                for (int integer13 = integer7; integer13 <= integer10; ++integer13) {
                    if (random.nextFloat() <= float4) {
                        if (!boolean13 || !this.getBlock(bhs, integer12, integer11, integer13, cic).isAir()) {
                            if (!boolean14 || this.isInterior(bhs, integer12, integer11, integer13, cic)) {
                                if (integer11 == integer6 || integer11 == integer9 || integer12 == integer5 || integer12 == integer8 || integer13 == integer7 || integer13 == integer10) {
                                    this.placeBlock(bhs, bvt11, integer12, integer11, integer13, cic);
                                }
                                else {
                                    this.placeBlock(bhs, bvt12, integer12, integer11, integer13, cic);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    protected void maybeGenerateBlock(final LevelAccessor bhs, final BoundingBox cic, final Random random, final float float4, final int integer5, final int integer6, final int integer7, final BlockState bvt) {
        if (random.nextFloat() < float4) {
            this.placeBlock(bhs, bvt, integer5, integer6, integer7, cic);
        }
    }
    
    protected void generateUpperHalfSphere(final LevelAccessor bhs, final BoundingBox cic, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final int integer8, final BlockState bvt, final boolean boolean10) {
        final float float12 = (float)(integer6 - integer3 + 1);
        final float float13 = (float)(integer7 - integer4 + 1);
        final float float14 = (float)(integer8 - integer5 + 1);
        final float float15 = integer3 + float12 / 2.0f;
        final float float16 = integer5 + float14 / 2.0f;
        for (int integer9 = integer4; integer9 <= integer7; ++integer9) {
            final float float17 = (integer9 - integer4) / float13;
            for (int integer10 = integer3; integer10 <= integer6; ++integer10) {
                final float float18 = (integer10 - float15) / (float12 * 0.5f);
                for (int integer11 = integer5; integer11 <= integer8; ++integer11) {
                    final float float19 = (integer11 - float16) / (float14 * 0.5f);
                    if (!boolean10 || !this.getBlock(bhs, integer10, integer9, integer11, cic).isAir()) {
                        final float float20 = float18 * float18 + float17 * float17 + float19 * float19;
                        if (float20 <= 1.05f) {
                            this.placeBlock(bhs, bvt, integer10, integer9, integer11, cic);
                        }
                    }
                }
            }
        }
    }
    
    protected void fillColumnDown(final LevelAccessor bhs, final BlockState bvt, final int integer3, final int integer4, final int integer5, final BoundingBox cic) {
        final int integer6 = this.getWorldX(integer3, integer5);
        int integer7 = this.getWorldY(integer4);
        final int integer8 = this.getWorldZ(integer3, integer5);
        if (!cic.isInside(new BlockPos(integer6, integer7, integer8))) {
            return;
        }
        while ((bhs.isEmptyBlock(new BlockPos(integer6, integer7, integer8)) || bhs.getBlockState(new BlockPos(integer6, integer7, integer8)).getMaterial().isLiquid()) && integer7 > 1) {
            bhs.setBlock(new BlockPos(integer6, integer7, integer8), bvt, 2);
            --integer7;
        }
    }
    
    protected boolean createChest(final LevelAccessor bhs, final BoundingBox cic, final Random random, final int integer4, final int integer5, final int integer6, final ResourceLocation qv) {
        final BlockPos ew9 = new BlockPos(this.getWorldX(integer4, integer6), this.getWorldY(integer5), this.getWorldZ(integer4, integer6));
        return this.createChest(bhs, cic, random, ew9, qv, null);
    }
    
    public static BlockState reorient(final BlockGetter bhb, final BlockPos ew, final BlockState bvt) {
        Direction fb4 = null;
        for (final Direction fb5 : Direction.Plane.HORIZONTAL) {
            final BlockPos ew2 = ew.relative(fb5);
            final BlockState bvt2 = bhb.getBlockState(ew2);
            if (bvt2.getBlock() == Blocks.CHEST) {
                return bvt;
            }
            if (!bvt2.isSolidRender(bhb, ew2)) {
                continue;
            }
            if (fb4 != null) {
                fb4 = null;
                break;
            }
            fb4 = fb5;
        }
        if (fb4 != null) {
            return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)HorizontalDirectionalBlock.FACING, fb4.getOpposite());
        }
        Direction fb6 = bvt.<Direction>getValue((Property<Direction>)HorizontalDirectionalBlock.FACING);
        BlockPos ew3 = ew.relative(fb6);
        if (bhb.getBlockState(ew3).isSolidRender(bhb, ew3)) {
            fb6 = fb6.getOpposite();
            ew3 = ew.relative(fb6);
        }
        if (bhb.getBlockState(ew3).isSolidRender(bhb, ew3)) {
            fb6 = fb6.getClockWise();
            ew3 = ew.relative(fb6);
        }
        if (bhb.getBlockState(ew3).isSolidRender(bhb, ew3)) {
            fb6 = fb6.getOpposite();
            ew3 = ew.relative(fb6);
        }
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)HorizontalDirectionalBlock.FACING, fb6);
    }
    
    protected boolean createChest(final LevelAccessor bhs, final BoundingBox cic, final Random random, final BlockPos ew, final ResourceLocation qv, @Nullable BlockState bvt) {
        if (!cic.isInside(ew) || bhs.getBlockState(ew).getBlock() == Blocks.CHEST) {
            return false;
        }
        if (bvt == null) {
            bvt = reorient(bhs, ew, Blocks.CHEST.defaultBlockState());
        }
        bhs.setBlock(ew, bvt, 2);
        final BlockEntity btw8 = bhs.getBlockEntity(ew);
        if (btw8 instanceof ChestBlockEntity) {
            ((ChestBlockEntity)btw8).setLootTable(qv, random.nextLong());
        }
        return true;
    }
    
    protected boolean createDispenser(final LevelAccessor bhs, final BoundingBox cic, final Random random, final int integer4, final int integer5, final int integer6, final Direction fb, final ResourceLocation qv) {
        final BlockPos ew10 = new BlockPos(this.getWorldX(integer4, integer6), this.getWorldY(integer5), this.getWorldZ(integer4, integer6));
        if (cic.isInside(ew10) && bhs.getBlockState(ew10).getBlock() != Blocks.DISPENSER) {
            this.placeBlock(bhs, ((AbstractStateHolder<O, BlockState>)Blocks.DISPENSER.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)DispenserBlock.FACING, fb), integer4, integer5, integer6, cic);
            final BlockEntity btw11 = bhs.getBlockEntity(ew10);
            if (btw11 instanceof DispenserBlockEntity) {
                ((DispenserBlockEntity)btw11).setLootTable(qv, random.nextLong());
            }
            return true;
        }
        return false;
    }
    
    public void move(final int integer1, final int integer2, final int integer3) {
        this.boundingBox.move(integer1, integer2, integer3);
    }
    
    @Nullable
    public Direction getOrientation() {
        return this.orientation;
    }
    
    public void setOrientation(@Nullable final Direction fb) {
        this.orientation = fb;
        if (fb == null) {
            this.rotation = Rotation.NONE;
            this.mirror = Mirror.NONE;
        }
        else {
            switch (fb) {
                case SOUTH: {
                    this.mirror = Mirror.LEFT_RIGHT;
                    this.rotation = Rotation.NONE;
                    break;
                }
                case WEST: {
                    this.mirror = Mirror.LEFT_RIGHT;
                    this.rotation = Rotation.CLOCKWISE_90;
                    break;
                }
                case EAST: {
                    this.mirror = Mirror.NONE;
                    this.rotation = Rotation.CLOCKWISE_90;
                    break;
                }
                default: {
                    this.mirror = Mirror.NONE;
                    this.rotation = Rotation.NONE;
                    break;
                }
            }
        }
    }
    
    public Rotation getRotation() {
        return this.rotation;
    }
    
    public StructurePieceType getType() {
        return this.type;
    }
    
    static {
        CAVE_AIR = Blocks.CAVE_AIR.defaultBlockState();
        SHAPE_CHECK_BLOCKS = (Set)ImmutableSet.builder().add(Blocks.NETHER_BRICK_FENCE).add(Blocks.TORCH).add(Blocks.WALL_TORCH).add(Blocks.OAK_FENCE).add(Blocks.SPRUCE_FENCE).add(Blocks.DARK_OAK_FENCE).add(Blocks.ACACIA_FENCE).add(Blocks.BIRCH_FENCE).add(Blocks.JUNGLE_FENCE).add(Blocks.LADDER).add(Blocks.IRON_BARS).build();
    }
    
    public abstract static class BlockSelector {
        protected BlockState next;
        
        protected BlockSelector() {
            this.next = Blocks.AIR.defaultBlockState();
        }
        
        public abstract void next(final Random random, final int integer2, final int integer3, final int integer4, final boolean boolean5);
        
        public BlockState getNext() {
            return this.next;
        }
    }
}
