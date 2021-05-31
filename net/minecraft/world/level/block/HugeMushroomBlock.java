package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import java.util.Map;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class HugeMushroomBlock extends Block {
    public static final BooleanProperty NORTH;
    public static final BooleanProperty EAST;
    public static final BooleanProperty SOUTH;
    public static final BooleanProperty WEST;
    public static final BooleanProperty UP;
    public static final BooleanProperty DOWN;
    private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION;
    
    public HugeMushroomBlock(final Properties c) {
        super(c);
        this.registerDefaultState((((((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)HugeMushroomBlock.NORTH, true)).setValue((Property<Comparable>)HugeMushroomBlock.EAST, true)).setValue((Property<Comparable>)HugeMushroomBlock.SOUTH, true)).setValue((Property<Comparable>)HugeMushroomBlock.WEST, true)).setValue((Property<Comparable>)HugeMushroomBlock.UP, true)).<Comparable, Boolean>setValue((Property<Comparable>)HugeMushroomBlock.DOWN, true));
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final BlockGetter bhb3 = ban.getLevel();
        final BlockPos ew4 = ban.getClickedPos();
        return (((((((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).setValue((Property<Comparable>)HugeMushroomBlock.DOWN, this != bhb3.getBlockState(ew4.below()).getBlock())).setValue((Property<Comparable>)HugeMushroomBlock.UP, this != bhb3.getBlockState(ew4.above()).getBlock())).setValue((Property<Comparable>)HugeMushroomBlock.NORTH, this != bhb3.getBlockState(ew4.north()).getBlock())).setValue((Property<Comparable>)HugeMushroomBlock.EAST, this != bhb3.getBlockState(ew4.east()).getBlock())).setValue((Property<Comparable>)HugeMushroomBlock.SOUTH, this != bhb3.getBlockState(ew4.south()).getBlock())).<Comparable, Boolean>setValue((Property<Comparable>)HugeMushroomBlock.WEST, this != bhb3.getBlockState(ew4.west()).getBlock());
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (bvt3.getBlock() == this) {
            return ((AbstractStateHolder<O, BlockState>)bvt1).<Comparable, Boolean>setValue((Property<Comparable>)HugeMushroomBlock.PROPERTY_BY_DIRECTION.get(fb), false);
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return (((((((AbstractStateHolder<O, BlockState>)bvt).setValue((Property<Comparable>)HugeMushroomBlock.PROPERTY_BY_DIRECTION.get(brg.rotate(Direction.NORTH)), (Comparable)bvt.<V>getValue((Property<V>)HugeMushroomBlock.NORTH))).setValue((Property<Comparable>)HugeMushroomBlock.PROPERTY_BY_DIRECTION.get(brg.rotate(Direction.SOUTH)), (Comparable)bvt.<V>getValue((Property<V>)HugeMushroomBlock.SOUTH))).setValue((Property<Comparable>)HugeMushroomBlock.PROPERTY_BY_DIRECTION.get(brg.rotate(Direction.EAST)), (Comparable)bvt.<V>getValue((Property<V>)HugeMushroomBlock.EAST))).setValue((Property<Comparable>)HugeMushroomBlock.PROPERTY_BY_DIRECTION.get(brg.rotate(Direction.WEST)), (Comparable)bvt.<V>getValue((Property<V>)HugeMushroomBlock.WEST))).setValue((Property<Comparable>)HugeMushroomBlock.PROPERTY_BY_DIRECTION.get(brg.rotate(Direction.UP)), (Comparable)bvt.<V>getValue((Property<V>)HugeMushroomBlock.UP))).<Comparable, Comparable>setValue((Property<Comparable>)HugeMushroomBlock.PROPERTY_BY_DIRECTION.get(brg.rotate(Direction.DOWN)), (Comparable)bvt.<V>getValue((Property<V>)HugeMushroomBlock.DOWN));
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        return (((((((AbstractStateHolder<O, BlockState>)bvt).setValue((Property<Comparable>)HugeMushroomBlock.PROPERTY_BY_DIRECTION.get(bqg.mirror(Direction.NORTH)), (Comparable)bvt.<V>getValue((Property<V>)HugeMushroomBlock.NORTH))).setValue((Property<Comparable>)HugeMushroomBlock.PROPERTY_BY_DIRECTION.get(bqg.mirror(Direction.SOUTH)), (Comparable)bvt.<V>getValue((Property<V>)HugeMushroomBlock.SOUTH))).setValue((Property<Comparable>)HugeMushroomBlock.PROPERTY_BY_DIRECTION.get(bqg.mirror(Direction.EAST)), (Comparable)bvt.<V>getValue((Property<V>)HugeMushroomBlock.EAST))).setValue((Property<Comparable>)HugeMushroomBlock.PROPERTY_BY_DIRECTION.get(bqg.mirror(Direction.WEST)), (Comparable)bvt.<V>getValue((Property<V>)HugeMushroomBlock.WEST))).setValue((Property<Comparable>)HugeMushroomBlock.PROPERTY_BY_DIRECTION.get(bqg.mirror(Direction.UP)), (Comparable)bvt.<V>getValue((Property<V>)HugeMushroomBlock.UP))).<Comparable, Comparable>setValue((Property<Comparable>)HugeMushroomBlock.PROPERTY_BY_DIRECTION.get(bqg.mirror(Direction.DOWN)), (Comparable)bvt.<V>getValue((Property<V>)HugeMushroomBlock.DOWN));
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(HugeMushroomBlock.UP, HugeMushroomBlock.DOWN, HugeMushroomBlock.NORTH, HugeMushroomBlock.EAST, HugeMushroomBlock.SOUTH, HugeMushroomBlock.WEST);
    }
    
    static {
        NORTH = PipeBlock.NORTH;
        EAST = PipeBlock.EAST;
        SOUTH = PipeBlock.SOUTH;
        WEST = PipeBlock.WEST;
        UP = PipeBlock.UP;
        DOWN = PipeBlock.DOWN;
        PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;
    }
}
