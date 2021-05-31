package net.minecraft.world.level.material;

import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.DoorBlock;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Iterator;
import net.minecraft.core.Vec3i;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.StateDefinition;
import com.google.common.collect.Maps;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.Map;
import net.minecraft.world.level.block.Block;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public abstract class FlowingFluid extends Fluid {
    public static final BooleanProperty FALLING;
    public static final IntegerProperty LEVEL;
    private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey>> OCCLUSION_CACHE;
    private final Map<FluidState, VoxelShape> shapes;
    
    public FlowingFluid() {
        this.shapes = (Map<FluidState, VoxelShape>)Maps.newIdentityHashMap();
    }
    
    @Override
    protected void createFluidStateDefinition(final StateDefinition.Builder<Fluid, FluidState> a) {
        a.add(FlowingFluid.FALLING);
    }
    
    public Vec3 getFlow(final BlockGetter bhb, final BlockPos ew, final FluidState clk) {
        double double5 = 0.0;
        double double6 = 0.0;
        try (final BlockPos.PooledMutableBlockPos b9 = BlockPos.PooledMutableBlockPos.acquire()) {
            for (final Direction fb12 : Direction.Plane.HORIZONTAL) {
                b9.set(ew).move(fb12);
                final FluidState clk2 = bhb.getFluidState(b9);
                if (!this.affectsFlow(clk2)) {
                    continue;
                }
                float float14 = clk2.getOwnHeight();
                float float15 = 0.0f;
                if (float14 == 0.0f) {
                    if (!bhb.getBlockState(b9).getMaterial().blocksMotion()) {
                        final BlockPos ew2 = b9.below();
                        final FluidState clk3 = bhb.getFluidState(ew2);
                        if (this.affectsFlow(clk3)) {
                            float14 = clk3.getOwnHeight();
                            if (float14 > 0.0f) {
                                float15 = clk.getOwnHeight() - (float14 - 0.8888889f);
                            }
                        }
                    }
                }
                else if (float14 > 0.0f) {
                    float15 = clk.getOwnHeight() - float14;
                }
                if (float15 == 0.0f) {
                    continue;
                }
                double5 += fb12.getStepX() * float15;
                double6 += fb12.getStepZ() * float15;
            }
            Vec3 csi11 = new Vec3(double5, 0.0, double6);
            if (clk.<Boolean>getValue((Property<Boolean>)FlowingFluid.FALLING)) {
                for (final Direction fb13 : Direction.Plane.HORIZONTAL) {
                    b9.set(ew).move(fb13);
                    if (this.isSolidFace(bhb, b9, fb13) || this.isSolidFace(bhb, b9.above(), fb13)) {
                        csi11 = csi11.normalize().add(0.0, -6.0, 0.0);
                        break;
                    }
                }
            }
            return csi11.normalize();
        }
    }
    
    private boolean affectsFlow(final FluidState clk) {
        return clk.isEmpty() || clk.getType().isSame(this);
    }
    
    protected boolean isSolidFace(final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        final BlockState bvt5 = bhb.getBlockState(ew);
        final FluidState clk6 = bhb.getFluidState(ew);
        return !clk6.getType().isSame(this) && (fb == Direction.UP || (bvt5.getMaterial() != Material.ICE && bvt5.isFaceSturdy(bhb, ew, fb)));
    }
    
    protected void spread(final LevelAccessor bhs, final BlockPos ew, final FluidState clk) {
        if (clk.isEmpty()) {
            return;
        }
        final BlockState bvt5 = bhs.getBlockState(ew);
        final BlockPos ew2 = ew.below();
        final BlockState bvt6 = bhs.getBlockState(ew2);
        final FluidState clk2 = this.getNewLiquid(bhs, ew2, bvt6);
        if (this.canSpreadTo(bhs, ew, bvt5, Direction.DOWN, ew2, bvt6, bhs.getFluidState(ew2), clk2.getType())) {
            this.spreadTo(bhs, ew2, bvt6, Direction.DOWN, clk2);
            if (this.sourceNeighborCount(bhs, ew) >= 3) {
                this.spreadToSides(bhs, ew, clk, bvt5);
            }
        }
        else if (clk.isSource() || !this.isWaterHole(bhs, clk2.getType(), ew, bvt5, ew2, bvt6)) {
            this.spreadToSides(bhs, ew, clk, bvt5);
        }
    }
    
    private void spreadToSides(final LevelAccessor bhs, final BlockPos ew, final FluidState clk, final BlockState bvt) {
        int integer6 = clk.getAmount() - this.getDropOff(bhs);
        if (clk.<Boolean>getValue((Property<Boolean>)FlowingFluid.FALLING)) {
            integer6 = 7;
        }
        if (integer6 <= 0) {
            return;
        }
        final Map<Direction, FluidState> map7 = this.getSpread(bhs, ew, bvt);
        for (final Map.Entry<Direction, FluidState> entry9 : map7.entrySet()) {
            final Direction fb10 = (Direction)entry9.getKey();
            final FluidState clk2 = (FluidState)entry9.getValue();
            final BlockPos ew2 = ew.relative(fb10);
            final BlockState bvt2 = bhs.getBlockState(ew2);
            if (this.canSpreadTo(bhs, ew, bvt, fb10, ew2, bvt2, bhs.getFluidState(ew2), clk2.getType())) {
                this.spreadTo(bhs, ew2, bvt2, fb10, clk2);
            }
        }
    }
    
    protected FluidState getNewLiquid(final LevelReader bhu, final BlockPos ew, final BlockState bvt) {
        int integer5 = 0;
        int integer6 = 0;
        for (final Direction fb8 : Direction.Plane.HORIZONTAL) {
            final BlockPos ew2 = ew.relative(fb8);
            final BlockState bvt2 = bhu.getBlockState(ew2);
            final FluidState clk11 = bvt2.getFluidState();
            if (clk11.getType().isSame(this) && this.canPassThroughWall(fb8, bhu, ew, bvt, ew2, bvt2)) {
                if (clk11.isSource()) {
                    ++integer6;
                }
                integer5 = Math.max(integer5, clk11.getAmount());
            }
        }
        if (this.canConvertToSource() && integer6 >= 2) {
            final BlockState bvt3 = bhu.getBlockState(ew.below());
            final FluidState clk12 = bvt3.getFluidState();
            if (bvt3.getMaterial().isSolid() || this.isSourceBlockOfThisType(clk12)) {
                return this.getSource(false);
            }
        }
        final BlockPos ew3 = ew.above();
        final BlockState bvt4 = bhu.getBlockState(ew3);
        final FluidState clk13 = bvt4.getFluidState();
        if (!clk13.isEmpty() && clk13.getType().isSame(this) && this.canPassThroughWall(Direction.UP, bhu, ew, bvt, ew3, bvt4)) {
            return this.getFlowing(8, true);
        }
        final int integer7 = integer5 - this.getDropOff(bhu);
        if (integer7 <= 0) {
            return Fluids.EMPTY.defaultFluidState();
        }
        return this.getFlowing(integer7, false);
    }
    
    private boolean canPassThroughWall(final Direction fb, final BlockGetter bhb, final BlockPos ew3, final BlockState bvt4, final BlockPos ew5, final BlockState bvt6) {
        Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey> object2ByteLinkedOpenHashMap8;
        if (bvt4.getBlock().hasDynamicShape() || bvt6.getBlock().hasDynamicShape()) {
            object2ByteLinkedOpenHashMap8 = null;
        }
        else {
            object2ByteLinkedOpenHashMap8 = (Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey>)FlowingFluid.OCCLUSION_CACHE.get();
        }
        Block.BlockStatePairKey a9;
        if (object2ByteLinkedOpenHashMap8 != null) {
            a9 = new Block.BlockStatePairKey(bvt4, bvt6, fb);
            final byte byte10 = object2ByteLinkedOpenHashMap8.getAndMoveToFirst(a9);
            if (byte10 != 127) {
                return byte10 != 0;
            }
        }
        else {
            a9 = null;
        }
        final VoxelShape ctc10 = bvt4.getCollisionShape(bhb, ew3);
        final VoxelShape ctc11 = bvt6.getCollisionShape(bhb, ew5);
        final boolean boolean12 = !Shapes.mergedFaceOccludes(ctc10, ctc11, fb);
        if (object2ByteLinkedOpenHashMap8 != null) {
            if (object2ByteLinkedOpenHashMap8.size() == 200) {
                object2ByteLinkedOpenHashMap8.removeLastByte();
            }
            object2ByteLinkedOpenHashMap8.putAndMoveToFirst(a9, (byte)(byte)(boolean12 ? 1 : 0));
        }
        return boolean12;
    }
    
    public abstract Fluid getFlowing();
    
    public FluidState getFlowing(final int integer, final boolean boolean2) {
        return this.getFlowing().defaultFluidState().<Comparable, Integer>setValue((Property<Comparable>)FlowingFluid.LEVEL, integer).<Comparable, Boolean>setValue((Property<Comparable>)FlowingFluid.FALLING, boolean2);
    }
    
    public abstract Fluid getSource();
    
    public FluidState getSource(final boolean boolean1) {
        return this.getSource().defaultFluidState().<Comparable, Boolean>setValue((Property<Comparable>)FlowingFluid.FALLING, boolean1);
    }
    
    protected abstract boolean canConvertToSource();
    
    protected void spreadTo(final LevelAccessor bhs, final BlockPos ew, final BlockState bvt, final Direction fb, final FluidState clk) {
        if (bvt.getBlock() instanceof LiquidBlockContainer) {
            ((LiquidBlockContainer)bvt.getBlock()).placeLiquid(bhs, ew, bvt, clk);
        }
        else {
            if (!bvt.isAir()) {
                this.beforeDestroyingBlock(bhs, ew, bvt);
            }
            bhs.setBlock(ew, clk.createLegacyBlock(), 3);
        }
    }
    
    protected abstract void beforeDestroyingBlock(final LevelAccessor bhs, final BlockPos ew, final BlockState bvt);
    
    private static short getCacheKey(final BlockPos ew1, final BlockPos ew2) {
        final int integer3 = ew2.getX() - ew1.getX();
        final int integer4 = ew2.getZ() - ew1.getZ();
        return (short)((integer3 + 128 & 0xFF) << 8 | (integer4 + 128 & 0xFF));
    }
    
    protected int getSlopeDistance(final LevelReader bhu, final BlockPos ew2, final int integer, final Direction fb, final BlockState bvt, final BlockPos ew6, final Short2ObjectMap<Pair<BlockState, FluidState>> short2ObjectMap, final Short2BooleanMap short2BooleanMap) {
        int integer2 = 1000;
        for (final Direction fb2 : Direction.Plane.HORIZONTAL) {
            if (fb2 == fb) {
                continue;
            }
            final BlockPos ew7 = ew2.relative(fb2);
            final short short14 = getCacheKey(ew6, ew7);
            final Pair<BlockState, FluidState> pair15 = (Pair<BlockState, FluidState>)short2ObjectMap.computeIfAbsent(short14, integer -> {
                final BlockState bvt4 = bhu.getBlockState(ew7);
                return Pair.of(bvt4, bvt4.getFluidState());
            });
            final BlockState bvt2 = (BlockState)pair15.getFirst();
            final FluidState clk17 = (FluidState)pair15.getSecond();
            if (!this.canPassThrough(bhu, this.getFlowing(), ew2, bvt, fb2, ew7, bvt2, clk17)) {
                continue;
            }
            final boolean boolean18 = short2BooleanMap.computeIfAbsent(short14, integer -> {
                final BlockPos ew2 = ew7.below();
                final BlockState bvt2 = bhu.getBlockState(ew2);
                return this.isWaterHole(bhu, this.getFlowing(), ew7, bvt2, ew2, bvt2);
            });
            if (boolean18) {
                return integer;
            }
            if (integer >= this.getSlopeFindDistance(bhu)) {
                continue;
            }
            final int integer3 = this.getSlopeDistance(bhu, ew7, integer + 1, fb2.getOpposite(), bvt2, ew6, short2ObjectMap, short2BooleanMap);
            if (integer3 >= integer2) {
                continue;
            }
            integer2 = integer3;
        }
        return integer2;
    }
    
    private boolean isWaterHole(final BlockGetter bhb, final Fluid clj, final BlockPos ew3, final BlockState bvt4, final BlockPos ew5, final BlockState bvt6) {
        return this.canPassThroughWall(Direction.DOWN, bhb, ew3, bvt4, ew5, bvt6) && (bvt6.getFluidState().getType().isSame(this) || this.canHoldFluid(bhb, ew5, bvt6, clj));
    }
    
    private boolean canPassThrough(final BlockGetter bhb, final Fluid clj, final BlockPos ew3, final BlockState bvt4, final Direction fb, final BlockPos ew6, final BlockState bvt7, final FluidState clk) {
        return !this.isSourceBlockOfThisType(clk) && this.canPassThroughWall(fb, bhb, ew3, bvt4, ew6, bvt7) && this.canHoldFluid(bhb, ew6, bvt7, clj);
    }
    
    private boolean isSourceBlockOfThisType(final FluidState clk) {
        return clk.getType().isSame(this) && clk.isSource();
    }
    
    protected abstract int getSlopeFindDistance(final LevelReader bhu);
    
    private int sourceNeighborCount(final LevelReader bhu, final BlockPos ew) {
        int integer4 = 0;
        for (final Direction fb6 : Direction.Plane.HORIZONTAL) {
            final BlockPos ew2 = ew.relative(fb6);
            final FluidState clk8 = bhu.getFluidState(ew2);
            if (this.isSourceBlockOfThisType(clk8)) {
                ++integer4;
            }
        }
        return integer4;
    }
    
    protected Map<Direction, FluidState> getSpread(final LevelReader bhu, final BlockPos ew, final BlockState bvt) {
        int integer5 = 1000;
        final Map<Direction, FluidState> map6 = (Map<Direction, FluidState>)Maps.newEnumMap((Class)Direction.class);
        final Short2ObjectMap<Pair<BlockState, FluidState>> short2ObjectMap7 = (Short2ObjectMap<Pair<BlockState, FluidState>>)new Short2ObjectOpenHashMap();
        final Short2BooleanMap short2BooleanMap8 = (Short2BooleanMap)new Short2BooleanOpenHashMap();
        for (final Direction fb10 : Direction.Plane.HORIZONTAL) {
            final BlockPos ew2 = ew.relative(fb10);
            final short short12 = getCacheKey(ew, ew2);
            final Pair<BlockState, FluidState> pair13 = (Pair<BlockState, FluidState>)short2ObjectMap7.computeIfAbsent(short12, integer -> {
                final BlockState bvt4 = bhu.getBlockState(ew2);
                return Pair.of(bvt4, bvt4.getFluidState());
            });
            final BlockState bvt2 = (BlockState)pair13.getFirst();
            final FluidState clk15 = (FluidState)pair13.getSecond();
            final FluidState clk16 = this.getNewLiquid(bhu, ew2, bvt2);
            if (this.canPassThrough(bhu, clk16.getType(), ew, bvt, fb10, ew2, bvt2, clk15)) {
                final BlockPos ew3 = ew2.below();
                final boolean boolean19 = short2BooleanMap8.computeIfAbsent(short12, integer -> {
                    final BlockState bvt2 = bhu.getBlockState(ew3);
                    return this.isWaterHole(bhu, this.getFlowing(), ew2, bvt2, ew3, bvt2);
                });
                int integer6;
                if (boolean19) {
                    integer6 = 0;
                }
                else {
                    integer6 = this.getSlopeDistance(bhu, ew2, 1, fb10.getOpposite(), bvt2, ew, short2ObjectMap7, short2BooleanMap8);
                }
                if (integer6 < integer5) {
                    map6.clear();
                }
                if (integer6 > integer5) {
                    continue;
                }
                map6.put(fb10, clk16);
                integer5 = integer6;
            }
        }
        return map6;
    }
    
    private boolean canHoldFluid(final BlockGetter bhb, final BlockPos ew, final BlockState bvt, final Fluid clj) {
        final Block bmv6 = bvt.getBlock();
        if (bmv6 instanceof LiquidBlockContainer) {
            return ((LiquidBlockContainer)bmv6).canPlaceLiquid(bhb, ew, bvt, clj);
        }
        if (bmv6 instanceof DoorBlock || bmv6.is(BlockTags.SIGNS) || bmv6 == Blocks.LADDER || bmv6 == Blocks.SUGAR_CANE || bmv6 == Blocks.BUBBLE_COLUMN) {
            return false;
        }
        final Material clo7 = bvt.getMaterial();
        return clo7 != Material.PORTAL && clo7 != Material.STRUCTURAL_AIR && clo7 != Material.WATER_PLANT && clo7 != Material.REPLACEABLE_WATER_PLANT && !clo7.blocksMotion();
    }
    
    protected boolean canSpreadTo(final BlockGetter bhb, final BlockPos ew2, final BlockState bvt3, final Direction fb, final BlockPos ew5, final BlockState bvt6, final FluidState clk, final Fluid clj) {
        return clk.canBeReplacedWith(bhb, ew5, clj, fb) && this.canPassThroughWall(fb, bhb, ew2, bvt3, ew5, bvt6) && this.canHoldFluid(bhb, ew5, bvt6, clj);
    }
    
    protected abstract int getDropOff(final LevelReader bhu);
    
    protected int getSpreadDelay(final Level bhr, final BlockPos ew, final FluidState clk3, final FluidState clk4) {
        return this.getTickDelay(bhr);
    }
    
    public void tick(final Level bhr, final BlockPos ew, FluidState clk) {
        if (!clk.isSource()) {
            final FluidState clk2 = this.getNewLiquid(bhr, ew, bhr.getBlockState(ew));
            final int integer6 = this.getSpreadDelay(bhr, ew, clk, clk2);
            if (clk2.isEmpty()) {
                clk = clk2;
                bhr.setBlock(ew, Blocks.AIR.defaultBlockState(), 3);
            }
            else if (!clk2.equals(clk)) {
                clk = clk2;
                final BlockState bvt7 = clk.createLegacyBlock();
                bhr.setBlock(ew, bvt7, 2);
                bhr.getLiquidTicks().scheduleTick(ew, clk.getType(), integer6);
                bhr.updateNeighborsAt(ew, bvt7.getBlock());
            }
        }
        this.spread(bhr, ew, clk);
    }
    
    protected static int getLegacyLevel(final FluidState clk) {
        if (clk.isSource()) {
            return 0;
        }
        return 8 - Math.min(clk.getAmount(), 8) + (clk.<Boolean>getValue((Property<Boolean>)FlowingFluid.FALLING) ? 8 : 0);
    }
    
    private static boolean hasSameAbove(final FluidState clk, final BlockGetter bhb, final BlockPos ew) {
        return clk.getType().isSame(bhb.getFluidState(ew.above()).getType());
    }
    
    @Override
    public float getHeight(final FluidState clk, final BlockGetter bhb, final BlockPos ew) {
        if (hasSameAbove(clk, bhb, ew)) {
            return 1.0f;
        }
        return clk.getOwnHeight();
    }
    
    @Override
    public float getOwnHeight(final FluidState clk) {
        return clk.getAmount() / 9.0f;
    }
    
    @Override
    public VoxelShape getShape(final FluidState clk, final BlockGetter bhb, final BlockPos ew) {
        if (clk.getAmount() == 9 && hasSameAbove(clk, bhb, ew)) {
            return Shapes.block();
        }
        return (VoxelShape)this.shapes.computeIfAbsent(clk, clk -> Shapes.box(0.0, 0.0, 0.0, 1.0, clk.getHeight(bhb, ew), 1.0));
    }
    
    static {
        FALLING = BlockStateProperties.FALLING;
        LEVEL = BlockStateProperties.LEVEL_FLOWING;
        OCCLUSION_CACHE = ThreadLocal.withInitial(() -> {
            final Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey> object2ByteLinkedOpenHashMap1 = new Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey>(200) {
                protected void rehash(final int integer) {
                }
            };
            object2ByteLinkedOpenHashMap1.defaultReturnValue((byte)127);
            return object2ByteLinkedOpenHashMap1;
        });
    }
}
