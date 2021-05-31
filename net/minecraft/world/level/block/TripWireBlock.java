package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.Vec3i;
import java.util.Random;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.Direction;
import java.util.Map;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class TripWireBlock extends Block {
    public static final BooleanProperty POWERED;
    public static final BooleanProperty ATTACHED;
    public static final BooleanProperty DISARMED;
    public static final BooleanProperty NORTH;
    public static final BooleanProperty EAST;
    public static final BooleanProperty SOUTH;
    public static final BooleanProperty WEST;
    private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION;
    protected static final VoxelShape AABB;
    protected static final VoxelShape NOT_ATTACHED_AABB;
    private final TripWireHookBlock hook;
    
    public TripWireBlock(final TripWireHookBlock bsw, final Properties c) {
        super(c);
        this.registerDefaultState(((((((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)TripWireBlock.POWERED, false)).setValue((Property<Comparable>)TripWireBlock.ATTACHED, false)).setValue((Property<Comparable>)TripWireBlock.DISARMED, false)).setValue((Property<Comparable>)TripWireBlock.NORTH, false)).setValue((Property<Comparable>)TripWireBlock.EAST, false)).setValue((Property<Comparable>)TripWireBlock.SOUTH, false)).<Comparable, Boolean>setValue((Property<Comparable>)TripWireBlock.WEST, false));
        this.hook = bsw;
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return bvt.<Boolean>getValue((Property<Boolean>)TripWireBlock.ATTACHED) ? TripWireBlock.AABB : TripWireBlock.NOT_ATTACHED_AABB;
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final BlockGetter bhb3 = ban.getLevel();
        final BlockPos ew4 = ban.getClickedPos();
        return (((((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).setValue((Property<Comparable>)TripWireBlock.NORTH, this.shouldConnectTo(bhb3.getBlockState(ew4.north()), Direction.NORTH))).setValue((Property<Comparable>)TripWireBlock.EAST, this.shouldConnectTo(bhb3.getBlockState(ew4.east()), Direction.EAST))).setValue((Property<Comparable>)TripWireBlock.SOUTH, this.shouldConnectTo(bhb3.getBlockState(ew4.south()), Direction.SOUTH))).<Comparable, Boolean>setValue((Property<Comparable>)TripWireBlock.WEST, this.shouldConnectTo(bhb3.getBlockState(ew4.west()), Direction.WEST));
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (fb.getAxis().isHorizontal()) {
            return ((AbstractStateHolder<O, BlockState>)bvt1).<Comparable, Boolean>setValue((Property<Comparable>)TripWireBlock.PROPERTY_BY_DIRECTION.get(fb), this.shouldConnectTo(bvt3, fb));
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.TRANSLUCENT;
    }
    
    @Override
    public void onPlace(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (bvt4.getBlock() == bvt1.getBlock()) {
            return;
        }
        this.updateSource(bhr, ew, bvt1);
    }
    
    @Override
    public void onRemove(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (boolean5 || bvt1.getBlock() == bvt4.getBlock()) {
            return;
        }
        this.updateSource(bhr, ew, ((AbstractStateHolder<O, BlockState>)bvt1).<Comparable, Boolean>setValue((Property<Comparable>)TripWireBlock.POWERED, true));
    }
    
    @Override
    public void playerWillDestroy(final Level bhr, final BlockPos ew, final BlockState bvt, final Player awg) {
        if (!bhr.isClientSide && !awg.getMainHandItem().isEmpty() && awg.getMainHandItem().getItem() == Items.SHEARS) {
            bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)TripWireBlock.DISARMED, true), 4);
        }
        super.playerWillDestroy(bhr, ew, bvt, awg);
    }
    
    private void updateSource(final Level bhr, final BlockPos ew, final BlockState bvt) {
        for (final Direction fb8 : new Direction[] { Direction.SOUTH, Direction.WEST }) {
            int integer9 = 1;
            while (integer9 < 42) {
                final BlockPos ew2 = ew.relative(fb8, integer9);
                final BlockState bvt2 = bhr.getBlockState(ew2);
                if (bvt2.getBlock() == this.hook) {
                    if (bvt2.<Comparable>getValue((Property<Comparable>)TripWireHookBlock.FACING) == fb8.getOpposite()) {
                        this.hook.calculateState(bhr, ew2, bvt2, false, true, integer9, bvt);
                        break;
                    }
                    break;
                }
                else {
                    if (bvt2.getBlock() != this) {
                        break;
                    }
                    ++integer9;
                }
            }
        }
    }
    
    @Override
    public void entityInside(final BlockState bvt, final Level bhr, final BlockPos ew, final Entity aio) {
        if (bhr.isClientSide) {
            return;
        }
        if (bvt.<Boolean>getValue((Property<Boolean>)TripWireBlock.POWERED)) {
            return;
        }
        this.checkPressed(bhr, ew);
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (bhr.isClientSide) {
            return;
        }
        if (!bhr.getBlockState(ew).<Boolean>getValue((Property<Boolean>)TripWireBlock.POWERED)) {
            return;
        }
        this.checkPressed(bhr, ew);
    }
    
    private void checkPressed(final Level bhr, final BlockPos ew) {
        BlockState bvt4 = bhr.getBlockState(ew);
        final boolean boolean5 = bvt4.<Boolean>getValue((Property<Boolean>)TripWireBlock.POWERED);
        boolean boolean6 = false;
        final List<? extends Entity> list7 = bhr.getEntities(null, bvt4.getShape(bhr, ew).bounds().move(ew));
        if (!list7.isEmpty()) {
            for (final Entity aio9 : list7) {
                if (!aio9.isIgnoringBlockTriggers()) {
                    boolean6 = true;
                    break;
                }
            }
        }
        if (boolean6 != boolean5) {
            bvt4 = ((AbstractStateHolder<O, BlockState>)bvt4).<Comparable, Boolean>setValue((Property<Comparable>)TripWireBlock.POWERED, boolean6);
            bhr.setBlock(ew, bvt4, 3);
            this.updateSource(bhr, ew, bvt4);
        }
        if (boolean6) {
            bhr.getBlockTicks().scheduleTick(new BlockPos(ew), this, this.getTickDelay(bhr));
        }
    }
    
    public boolean shouldConnectTo(final BlockState bvt, final Direction fb) {
        final Block bmv4 = bvt.getBlock();
        if (bmv4 == this.hook) {
            return bvt.<Comparable>getValue((Property<Comparable>)TripWireHookBlock.FACING) == fb.getOpposite();
        }
        return bmv4 == this;
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        switch (brg) {
            case CLOCKWISE_180: {
                return (((((AbstractStateHolder<O, BlockState>)bvt).setValue((Property<Comparable>)TripWireBlock.NORTH, (Comparable)bvt.<V>getValue((Property<V>)TripWireBlock.SOUTH))).setValue((Property<Comparable>)TripWireBlock.EAST, (Comparable)bvt.<V>getValue((Property<V>)TripWireBlock.WEST))).setValue((Property<Comparable>)TripWireBlock.SOUTH, (Comparable)bvt.<V>getValue((Property<V>)TripWireBlock.NORTH))).<Comparable, Comparable>setValue((Property<Comparable>)TripWireBlock.WEST, (Comparable)bvt.<V>getValue((Property<V>)TripWireBlock.EAST));
            }
            case COUNTERCLOCKWISE_90: {
                return (((((AbstractStateHolder<O, BlockState>)bvt).setValue((Property<Comparable>)TripWireBlock.NORTH, (Comparable)bvt.<V>getValue((Property<V>)TripWireBlock.EAST))).setValue((Property<Comparable>)TripWireBlock.EAST, (Comparable)bvt.<V>getValue((Property<V>)TripWireBlock.SOUTH))).setValue((Property<Comparable>)TripWireBlock.SOUTH, (Comparable)bvt.<V>getValue((Property<V>)TripWireBlock.WEST))).<Comparable, Comparable>setValue((Property<Comparable>)TripWireBlock.WEST, (Comparable)bvt.<V>getValue((Property<V>)TripWireBlock.NORTH));
            }
            case CLOCKWISE_90: {
                return (((((AbstractStateHolder<O, BlockState>)bvt).setValue((Property<Comparable>)TripWireBlock.NORTH, (Comparable)bvt.<V>getValue((Property<V>)TripWireBlock.WEST))).setValue((Property<Comparable>)TripWireBlock.EAST, (Comparable)bvt.<V>getValue((Property<V>)TripWireBlock.NORTH))).setValue((Property<Comparable>)TripWireBlock.SOUTH, (Comparable)bvt.<V>getValue((Property<V>)TripWireBlock.EAST))).<Comparable, Comparable>setValue((Property<Comparable>)TripWireBlock.WEST, (Comparable)bvt.<V>getValue((Property<V>)TripWireBlock.SOUTH));
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
                return (((AbstractStateHolder<O, BlockState>)bvt).setValue((Property<Comparable>)TripWireBlock.NORTH, (Comparable)bvt.<V>getValue((Property<V>)TripWireBlock.SOUTH))).<Comparable, Comparable>setValue((Property<Comparable>)TripWireBlock.SOUTH, (Comparable)bvt.<V>getValue((Property<V>)TripWireBlock.NORTH));
            }
            case FRONT_BACK: {
                return (((AbstractStateHolder<O, BlockState>)bvt).setValue((Property<Comparable>)TripWireBlock.EAST, (Comparable)bvt.<V>getValue((Property<V>)TripWireBlock.WEST))).<Comparable, Comparable>setValue((Property<Comparable>)TripWireBlock.WEST, (Comparable)bvt.<V>getValue((Property<V>)TripWireBlock.EAST));
            }
            default: {
                return super.mirror(bvt, bqg);
            }
        }
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(TripWireBlock.POWERED, TripWireBlock.ATTACHED, TripWireBlock.DISARMED, TripWireBlock.NORTH, TripWireBlock.EAST, TripWireBlock.WEST, TripWireBlock.SOUTH);
    }
    
    static {
        POWERED = BlockStateProperties.POWERED;
        ATTACHED = BlockStateProperties.ATTACHED;
        DISARMED = BlockStateProperties.DISARMED;
        NORTH = PipeBlock.NORTH;
        EAST = PipeBlock.EAST;
        SOUTH = PipeBlock.SOUTH;
        WEST = PipeBlock.WEST;
        PROPERTY_BY_DIRECTION = CrossCollisionBlock.PROPERTY_BY_DIRECTION;
        AABB = Block.box(0.0, 1.0, 0.0, 16.0, 2.5, 16.0);
        NOT_ATTACHED_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    }
}
