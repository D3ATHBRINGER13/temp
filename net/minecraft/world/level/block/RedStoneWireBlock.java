package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.DustParticleOptions;
import java.util.Random;
import net.minecraft.util.Mth;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import com.google.common.collect.Lists;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import java.util.Iterator;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import java.util.Set;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.Direction;
import java.util.Map;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class RedStoneWireBlock extends Block {
    public static final EnumProperty<RedstoneSide> NORTH;
    public static final EnumProperty<RedstoneSide> EAST;
    public static final EnumProperty<RedstoneSide> SOUTH;
    public static final EnumProperty<RedstoneSide> WEST;
    public static final IntegerProperty POWER;
    public static final Map<Direction, EnumProperty<RedstoneSide>> PROPERTY_BY_DIRECTION;
    protected static final VoxelShape[] SHAPE_BY_INDEX;
    private boolean shouldSignal;
    private final Set<BlockPos> toUpdate;
    
    public RedStoneWireBlock(final Properties c) {
        super(c);
        this.shouldSignal = true;
        this.toUpdate = (Set<BlockPos>)Sets.newHashSet();
        this.registerDefaultState(((((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue(RedStoneWireBlock.NORTH, RedstoneSide.NONE)).setValue(RedStoneWireBlock.EAST, RedstoneSide.NONE)).setValue(RedStoneWireBlock.SOUTH, RedstoneSide.NONE)).setValue(RedStoneWireBlock.WEST, RedstoneSide.NONE)).<Comparable, Integer>setValue((Property<Comparable>)RedStoneWireBlock.POWER, 0));
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return RedStoneWireBlock.SHAPE_BY_INDEX[getAABBIndex(bvt)];
    }
    
    private static int getAABBIndex(final BlockState bvt) {
        int integer2 = 0;
        final boolean boolean3 = bvt.<RedstoneSide>getValue(RedStoneWireBlock.NORTH) != RedstoneSide.NONE;
        final boolean boolean4 = bvt.<RedstoneSide>getValue(RedStoneWireBlock.EAST) != RedstoneSide.NONE;
        final boolean boolean5 = bvt.<RedstoneSide>getValue(RedStoneWireBlock.SOUTH) != RedstoneSide.NONE;
        final boolean boolean6 = bvt.<RedstoneSide>getValue(RedStoneWireBlock.WEST) != RedstoneSide.NONE;
        if (boolean3 || (boolean5 && !boolean3 && !boolean4 && !boolean6)) {
            integer2 |= 1 << Direction.NORTH.get2DDataValue();
        }
        if (boolean4 || (boolean6 && !boolean3 && !boolean4 && !boolean5)) {
            integer2 |= 1 << Direction.EAST.get2DDataValue();
        }
        if (boolean5 || (boolean3 && !boolean4 && !boolean5 && !boolean6)) {
            integer2 |= 1 << Direction.SOUTH.get2DDataValue();
        }
        if (boolean6 || (boolean4 && !boolean3 && !boolean5 && !boolean6)) {
            integer2 |= 1 << Direction.WEST.get2DDataValue();
        }
        return integer2;
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final BlockGetter bhb3 = ban.getLevel();
        final BlockPos ew4 = ban.getClickedPos();
        return (((((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).setValue(RedStoneWireBlock.WEST, this.getConnectingSide(bhb3, ew4, Direction.WEST))).setValue(RedStoneWireBlock.EAST, this.getConnectingSide(bhb3, ew4, Direction.EAST))).setValue(RedStoneWireBlock.NORTH, this.getConnectingSide(bhb3, ew4, Direction.NORTH))).<RedstoneSide, RedstoneSide>setValue(RedStoneWireBlock.SOUTH, this.getConnectingSide(bhb3, ew4, Direction.SOUTH));
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (fb == Direction.DOWN) {
            return bvt1;
        }
        if (fb == Direction.UP) {
            return (((((AbstractStateHolder<O, BlockState>)bvt1).setValue(RedStoneWireBlock.WEST, this.getConnectingSide(bhs, ew5, Direction.WEST))).setValue(RedStoneWireBlock.EAST, this.getConnectingSide(bhs, ew5, Direction.EAST))).setValue(RedStoneWireBlock.NORTH, this.getConnectingSide(bhs, ew5, Direction.NORTH))).<RedstoneSide, RedstoneSide>setValue(RedStoneWireBlock.SOUTH, this.getConnectingSide(bhs, ew5, Direction.SOUTH));
        }
        return ((AbstractStateHolder<O, BlockState>)bvt1).<Comparable, RedstoneSide>setValue((Property<Comparable>)RedStoneWireBlock.PROPERTY_BY_DIRECTION.get(fb), this.getConnectingSide(bhs, ew5, fb));
    }
    
    @Override
    public void updateIndirectNeighbourShapes(final BlockState bvt, final LevelAccessor bhs, final BlockPos ew, final int integer) {
        try (final BlockPos.PooledMutableBlockPos b6 = BlockPos.PooledMutableBlockPos.acquire()) {
            for (final Direction fb9 : Direction.Plane.HORIZONTAL) {
                final RedstoneSide bwy10 = bvt.<RedstoneSide>getValue((Property<RedstoneSide>)RedStoneWireBlock.PROPERTY_BY_DIRECTION.get(fb9));
                if (bwy10 != RedstoneSide.NONE && bhs.getBlockState(b6.set(ew).move(fb9)).getBlock() != this) {
                    b6.move(Direction.DOWN);
                    final BlockState bvt2 = bhs.getBlockState(b6);
                    if (bvt2.getBlock() != Blocks.OBSERVER) {
                        final BlockPos ew2 = b6.relative(fb9.getOpposite());
                        final BlockState bvt3 = bvt2.updateShape(fb9.getOpposite(), bhs.getBlockState(ew2), bhs, b6, ew2);
                        Block.updateOrDestroy(bvt2, bvt3, bhs, b6, integer);
                    }
                    b6.set(ew).move(fb9).move(Direction.UP);
                    final BlockState bvt4 = bhs.getBlockState(b6);
                    if (bvt4.getBlock() == Blocks.OBSERVER) {
                        continue;
                    }
                    final BlockPos ew3 = b6.relative(fb9.getOpposite());
                    final BlockState bvt5 = bvt4.updateShape(fb9.getOpposite(), bhs.getBlockState(ew3), bhs, b6, ew3);
                    Block.updateOrDestroy(bvt4, bvt5, bhs, b6, integer);
                }
            }
        }
    }
    
    private RedstoneSide getConnectingSide(final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        final BlockPos ew2 = ew.relative(fb);
        final BlockState bvt6 = bhb.getBlockState(ew2);
        final BlockPos ew3 = ew.above();
        final BlockState bvt7 = bhb.getBlockState(ew3);
        if (!bvt7.isRedstoneConductor(bhb, ew3)) {
            final boolean boolean9 = bvt6.isFaceSturdy(bhb, ew2, Direction.UP) || bvt6.getBlock() == Blocks.HOPPER;
            if (boolean9 && shouldConnectTo(bhb.getBlockState(ew2.above()))) {
                if (bvt6.isCollisionShapeFullBlock(bhb, ew2)) {
                    return RedstoneSide.UP;
                }
                return RedstoneSide.SIDE;
            }
        }
        if (shouldConnectTo(bvt6, fb) || (!bvt6.isRedstoneConductor(bhb, ew2) && shouldConnectTo(bhb.getBlockState(ew2.below())))) {
            return RedstoneSide.SIDE;
        }
        return RedstoneSide.NONE;
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        final BlockPos ew2 = ew.below();
        final BlockState bvt2 = bhu.getBlockState(ew2);
        return bvt2.isFaceSturdy(bhu, ew2, Direction.UP) || bvt2.getBlock() == Blocks.HOPPER;
    }
    
    private BlockState updatePowerStrength(final Level bhr, final BlockPos ew, BlockState bvt) {
        bvt = this.updatePowerStrengthImpl(bhr, ew, bvt);
        final List<BlockPos> list5 = (List<BlockPos>)Lists.newArrayList((Iterable)this.toUpdate);
        this.toUpdate.clear();
        for (final BlockPos ew2 : list5) {
            bhr.updateNeighborsAt(ew2, this);
        }
        return bvt;
    }
    
    private BlockState updatePowerStrengthImpl(final Level bhr, final BlockPos ew, BlockState bvt) {
        final BlockState bvt2 = bvt;
        final int integer6 = bvt2.<Integer>getValue((Property<Integer>)RedStoneWireBlock.POWER);
        this.shouldSignal = false;
        final int integer7 = bhr.getBestNeighborSignal(ew);
        this.shouldSignal = true;
        int integer8 = 0;
        if (integer7 < 15) {
            for (final Direction fb10 : Direction.Plane.HORIZONTAL) {
                final BlockPos ew2 = ew.relative(fb10);
                final BlockState bvt3 = bhr.getBlockState(ew2);
                integer8 = this.checkTarget(integer8, bvt3);
                final BlockPos ew3 = ew.above();
                if (bvt3.isRedstoneConductor(bhr, ew2) && !bhr.getBlockState(ew3).isRedstoneConductor(bhr, ew3)) {
                    integer8 = this.checkTarget(integer8, bhr.getBlockState(ew2.above()));
                }
                else {
                    if (bvt3.isRedstoneConductor(bhr, ew2)) {
                        continue;
                    }
                    integer8 = this.checkTarget(integer8, bhr.getBlockState(ew2.below()));
                }
            }
        }
        int integer9 = integer8 - 1;
        if (integer7 > integer9) {
            integer9 = integer7;
        }
        if (integer6 != integer9) {
            bvt = ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)RedStoneWireBlock.POWER, integer9);
            if (bhr.getBlockState(ew) == bvt2) {
                bhr.setBlock(ew, bvt, 2);
            }
            this.toUpdate.add(ew);
            for (final Direction fb11 : Direction.values()) {
                this.toUpdate.add(ew.relative(fb11));
            }
        }
        return bvt;
    }
    
    private void checkCornerChangeAt(final Level bhr, final BlockPos ew) {
        if (bhr.getBlockState(ew).getBlock() != this) {
            return;
        }
        bhr.updateNeighborsAt(ew, this);
        for (final Direction fb7 : Direction.values()) {
            bhr.updateNeighborsAt(ew.relative(fb7), this);
        }
    }
    
    @Override
    public void onPlace(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (bvt4.getBlock() == bvt1.getBlock() || bhr.isClientSide) {
            return;
        }
        this.updatePowerStrength(bhr, ew, bvt1);
        for (final Direction fb8 : Direction.Plane.VERTICAL) {
            bhr.updateNeighborsAt(ew.relative(fb8), this);
        }
        for (final Direction fb8 : Direction.Plane.HORIZONTAL) {
            this.checkCornerChangeAt(bhr, ew.relative(fb8));
        }
        for (final Direction fb8 : Direction.Plane.HORIZONTAL) {
            final BlockPos ew2 = ew.relative(fb8);
            if (bhr.getBlockState(ew2).isRedstoneConductor(bhr, ew2)) {
                this.checkCornerChangeAt(bhr, ew2.above());
            }
            else {
                this.checkCornerChangeAt(bhr, ew2.below());
            }
        }
    }
    
    @Override
    public void onRemove(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (boolean5 || bvt1.getBlock() == bvt4.getBlock()) {
            return;
        }
        super.onRemove(bvt1, bhr, ew, bvt4, boolean5);
        if (bhr.isClientSide) {
            return;
        }
        for (final Direction fb10 : Direction.values()) {
            bhr.updateNeighborsAt(ew.relative(fb10), this);
        }
        this.updatePowerStrength(bhr, ew, bvt1);
        for (final Direction fb11 : Direction.Plane.HORIZONTAL) {
            this.checkCornerChangeAt(bhr, ew.relative(fb11));
        }
        for (final Direction fb11 : Direction.Plane.HORIZONTAL) {
            final BlockPos ew2 = ew.relative(fb11);
            if (bhr.getBlockState(ew2).isRedstoneConductor(bhr, ew2)) {
                this.checkCornerChangeAt(bhr, ew2.above());
            }
            else {
                this.checkCornerChangeAt(bhr, ew2.below());
            }
        }
    }
    
    private int checkTarget(final int integer, final BlockState bvt) {
        if (bvt.getBlock() != this) {
            return integer;
        }
        final int integer2 = bvt.<Integer>getValue((Property<Integer>)RedStoneWireBlock.POWER);
        if (integer2 > integer) {
            return integer2;
        }
        return integer;
    }
    
    @Override
    public void neighborChanged(final BlockState bvt, final Level bhr, final BlockPos ew3, final Block bmv, final BlockPos ew5, final boolean boolean6) {
        if (bhr.isClientSide) {
            return;
        }
        if (bvt.canSurvive(bhr, ew3)) {
            this.updatePowerStrength(bhr, ew3, bvt);
        }
        else {
            Block.dropResources(bvt, bhr, ew3);
            bhr.removeBlock(ew3, false);
        }
    }
    
    @Override
    public int getDirectSignal(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        if (!this.shouldSignal) {
            return 0;
        }
        return bvt.getSignal(bhb, ew, fb);
    }
    
    @Override
    public int getSignal(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        if (!this.shouldSignal) {
            return 0;
        }
        final int integer6 = bvt.<Integer>getValue((Property<Integer>)RedStoneWireBlock.POWER);
        if (integer6 == 0) {
            return 0;
        }
        if (fb == Direction.UP) {
            return integer6;
        }
        final EnumSet<Direction> enumSet7 = (EnumSet<Direction>)EnumSet.noneOf((Class)Direction.class);
        for (final Direction fb2 : Direction.Plane.HORIZONTAL) {
            if (this.isPowerSourceAt(bhb, ew, fb2)) {
                enumSet7.add(fb2);
            }
        }
        if (fb.getAxis().isHorizontal() && enumSet7.isEmpty()) {
            return integer6;
        }
        if (enumSet7.contains(fb) && !enumSet7.contains(fb.getCounterClockWise()) && !enumSet7.contains(fb.getClockWise())) {
            return integer6;
        }
        return 0;
    }
    
    private boolean isPowerSourceAt(final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        final BlockPos ew2 = ew.relative(fb);
        final BlockState bvt6 = bhb.getBlockState(ew2);
        final boolean boolean7 = bvt6.isRedstoneConductor(bhb, ew2);
        final BlockPos ew3 = ew.above();
        final boolean boolean8 = bhb.getBlockState(ew3).isRedstoneConductor(bhb, ew3);
        return (!boolean8 && boolean7 && shouldConnectTo(bhb, ew2.above())) || shouldConnectTo(bvt6, fb) || (bvt6.getBlock() == Blocks.REPEATER && bvt6.<Boolean>getValue((Property<Boolean>)DiodeBlock.POWERED) && bvt6.<Comparable>getValue((Property<Comparable>)DiodeBlock.FACING) == fb) || (!boolean7 && shouldConnectTo(bhb, ew2.below()));
    }
    
    protected static boolean shouldConnectTo(final BlockGetter bhb, final BlockPos ew) {
        return shouldConnectTo(bhb.getBlockState(ew));
    }
    
    protected static boolean shouldConnectTo(final BlockState bvt) {
        return shouldConnectTo(bvt, null);
    }
    
    protected static boolean shouldConnectTo(final BlockState bvt, @Nullable final Direction fb) {
        final Block bmv3 = bvt.getBlock();
        if (bmv3 == Blocks.REDSTONE_WIRE) {
            return true;
        }
        if (bvt.getBlock() == Blocks.REPEATER) {
            final Direction fb2 = bvt.<Direction>getValue((Property<Direction>)RepeaterBlock.FACING);
            return fb2 == fb || fb2.getOpposite() == fb;
        }
        if (Blocks.OBSERVER == bvt.getBlock()) {
            return fb == bvt.<Comparable>getValue((Property<Comparable>)ObserverBlock.FACING);
        }
        return bvt.isSignalSource() && fb != null;
    }
    
    @Override
    public boolean isSignalSource(final BlockState bvt) {
        return this.shouldSignal;
    }
    
    public static int getColorForData(final int integer) {
        final float float2 = integer / 15.0f;
        float float3 = float2 * 0.6f + 0.4f;
        if (integer == 0) {
            float3 = 0.3f;
        }
        float float4 = float2 * float2 * 0.7f - 0.5f;
        float float5 = float2 * float2 * 0.6f - 0.7f;
        if (float4 < 0.0f) {
            float4 = 0.0f;
        }
        if (float5 < 0.0f) {
            float5 = 0.0f;
        }
        final int integer2 = Mth.clamp((int)(float3 * 255.0f), 0, 255);
        final int integer3 = Mth.clamp((int)(float4 * 255.0f), 0, 255);
        final int integer4 = Mth.clamp((int)(float5 * 255.0f), 0, 255);
        return 0xFF000000 | integer2 << 16 | integer3 << 8 | integer4;
    }
    
    @Override
    public void animateTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        final int integer6 = bvt.<Integer>getValue((Property<Integer>)RedStoneWireBlock.POWER);
        if (integer6 == 0) {
            return;
        }
        final double double7 = ew.getX() + 0.5 + (random.nextFloat() - 0.5) * 0.2;
        final double double8 = ew.getY() + 0.0625f;
        final double double9 = ew.getZ() + 0.5 + (random.nextFloat() - 0.5) * 0.2;
        final float float13 = integer6 / 15.0f;
        final float float14 = float13 * 0.6f + 0.4f;
        final float float15 = Math.max(0.0f, float13 * float13 * 0.7f - 0.5f);
        final float float16 = Math.max(0.0f, float13 * float13 * 0.6f - 0.7f);
        bhr.addParticle(new DustParticleOptions(float14, float15, float16, 1.0f), double7, double8, double9, 0.0, 0.0, 0.0);
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        switch (brg) {
            case CLOCKWISE_180: {
                return (((((AbstractStateHolder<O, BlockState>)bvt).setValue(RedStoneWireBlock.NORTH, (Comparable)bvt.<V>getValue((Property<V>)RedStoneWireBlock.SOUTH))).setValue(RedStoneWireBlock.EAST, (Comparable)bvt.<V>getValue((Property<V>)RedStoneWireBlock.WEST))).setValue(RedStoneWireBlock.SOUTH, (Comparable)bvt.<V>getValue((Property<V>)RedStoneWireBlock.NORTH))).<RedstoneSide, Comparable>setValue(RedStoneWireBlock.WEST, (Comparable)bvt.<V>getValue((Property<V>)RedStoneWireBlock.EAST));
            }
            case COUNTERCLOCKWISE_90: {
                return (((((AbstractStateHolder<O, BlockState>)bvt).setValue(RedStoneWireBlock.NORTH, (Comparable)bvt.<V>getValue((Property<V>)RedStoneWireBlock.EAST))).setValue(RedStoneWireBlock.EAST, (Comparable)bvt.<V>getValue((Property<V>)RedStoneWireBlock.SOUTH))).setValue(RedStoneWireBlock.SOUTH, (Comparable)bvt.<V>getValue((Property<V>)RedStoneWireBlock.WEST))).<RedstoneSide, Comparable>setValue(RedStoneWireBlock.WEST, (Comparable)bvt.<V>getValue((Property<V>)RedStoneWireBlock.NORTH));
            }
            case CLOCKWISE_90: {
                return (((((AbstractStateHolder<O, BlockState>)bvt).setValue(RedStoneWireBlock.NORTH, (Comparable)bvt.<V>getValue((Property<V>)RedStoneWireBlock.WEST))).setValue(RedStoneWireBlock.EAST, (Comparable)bvt.<V>getValue((Property<V>)RedStoneWireBlock.NORTH))).setValue(RedStoneWireBlock.SOUTH, (Comparable)bvt.<V>getValue((Property<V>)RedStoneWireBlock.EAST))).<RedstoneSide, Comparable>setValue(RedStoneWireBlock.WEST, (Comparable)bvt.<V>getValue((Property<V>)RedStoneWireBlock.SOUTH));
            }
            default: {
                return bvt;
            }
        }
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        switch (bqg) {
            case LEFT_RIGHT: {
                return (((AbstractStateHolder<O, BlockState>)bvt).setValue(RedStoneWireBlock.NORTH, (Comparable)bvt.<V>getValue((Property<V>)RedStoneWireBlock.SOUTH))).<RedstoneSide, Comparable>setValue(RedStoneWireBlock.SOUTH, (Comparable)bvt.<V>getValue((Property<V>)RedStoneWireBlock.NORTH));
            }
            case FRONT_BACK: {
                return (((AbstractStateHolder<O, BlockState>)bvt).setValue(RedStoneWireBlock.EAST, (Comparable)bvt.<V>getValue((Property<V>)RedStoneWireBlock.WEST))).<RedstoneSide, Comparable>setValue(RedStoneWireBlock.WEST, (Comparable)bvt.<V>getValue((Property<V>)RedStoneWireBlock.EAST));
            }
            default: {
                return super.mirror(bvt, bqg);
            }
        }
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(RedStoneWireBlock.NORTH, RedStoneWireBlock.EAST, RedStoneWireBlock.SOUTH, RedStoneWireBlock.WEST, RedStoneWireBlock.POWER);
    }
    
    static {
        NORTH = BlockStateProperties.NORTH_REDSTONE;
        EAST = BlockStateProperties.EAST_REDSTONE;
        SOUTH = BlockStateProperties.SOUTH_REDSTONE;
        WEST = BlockStateProperties.WEST_REDSTONE;
        POWER = BlockStateProperties.POWER;
        PROPERTY_BY_DIRECTION = (Map)Maps.newEnumMap((Map)ImmutableMap.of(Direction.NORTH, RedStoneWireBlock.NORTH, Direction.EAST, RedStoneWireBlock.EAST, Direction.SOUTH, RedStoneWireBlock.SOUTH, Direction.WEST, RedStoneWireBlock.WEST));
        SHAPE_BY_INDEX = new VoxelShape[] { Block.box(3.0, 0.0, 3.0, 13.0, 1.0, 13.0), Block.box(3.0, 0.0, 3.0, 13.0, 1.0, 16.0), Block.box(0.0, 0.0, 3.0, 13.0, 1.0, 13.0), Block.box(0.0, 0.0, 3.0, 13.0, 1.0, 16.0), Block.box(3.0, 0.0, 0.0, 13.0, 1.0, 13.0), Block.box(3.0, 0.0, 0.0, 13.0, 1.0, 16.0), Block.box(0.0, 0.0, 0.0, 13.0, 1.0, 13.0), Block.box(0.0, 0.0, 0.0, 13.0, 1.0, 16.0), Block.box(3.0, 0.0, 3.0, 16.0, 1.0, 13.0), Block.box(3.0, 0.0, 3.0, 16.0, 1.0, 16.0), Block.box(0.0, 0.0, 3.0, 16.0, 1.0, 13.0), Block.box(0.0, 0.0, 3.0, 16.0, 1.0, 16.0), Block.box(3.0, 0.0, 0.0, 16.0, 1.0, 13.0), Block.box(3.0, 0.0, 0.0, 16.0, 1.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 13.0), Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0) };
    }
}
