package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class PoweredRailBlock extends BaseRailBlock {
    public static final EnumProperty<RailShape> SHAPE;
    public static final BooleanProperty POWERED;
    
    protected PoweredRailBlock(final Properties c) {
        super(true, c);
        this.registerDefaultState((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue(PoweredRailBlock.SHAPE, RailShape.NORTH_SOUTH)).<Comparable, Boolean>setValue((Property<Comparable>)PoweredRailBlock.POWERED, false));
    }
    
    protected boolean findPoweredRailSignal(final Level bhr, final BlockPos ew, final BlockState bvt, final boolean boolean4, final int integer) {
        if (integer >= 8) {
            return false;
        }
        int integer2 = ew.getX();
        int integer3 = ew.getY();
        int integer4 = ew.getZ();
        boolean boolean5 = true;
        RailShape bwx11 = bvt.<RailShape>getValue(PoweredRailBlock.SHAPE);
        switch (bwx11) {
            case NORTH_SOUTH: {
                if (boolean4) {
                    ++integer4;
                    break;
                }
                --integer4;
                break;
            }
            case EAST_WEST: {
                if (boolean4) {
                    --integer2;
                    break;
                }
                ++integer2;
                break;
            }
            case ASCENDING_EAST: {
                if (boolean4) {
                    --integer2;
                }
                else {
                    ++integer2;
                    ++integer3;
                    boolean5 = false;
                }
                bwx11 = RailShape.EAST_WEST;
                break;
            }
            case ASCENDING_WEST: {
                if (boolean4) {
                    --integer2;
                    ++integer3;
                    boolean5 = false;
                }
                else {
                    ++integer2;
                }
                bwx11 = RailShape.EAST_WEST;
                break;
            }
            case ASCENDING_NORTH: {
                if (boolean4) {
                    ++integer4;
                }
                else {
                    --integer4;
                    ++integer3;
                    boolean5 = false;
                }
                bwx11 = RailShape.NORTH_SOUTH;
                break;
            }
            case ASCENDING_SOUTH: {
                if (boolean4) {
                    ++integer4;
                    ++integer3;
                    boolean5 = false;
                }
                else {
                    --integer4;
                }
                bwx11 = RailShape.NORTH_SOUTH;
                break;
            }
        }
        return this.isSameRailWithPower(bhr, new BlockPos(integer2, integer3, integer4), boolean4, integer, bwx11) || (boolean5 && this.isSameRailWithPower(bhr, new BlockPos(integer2, integer3 - 1, integer4), boolean4, integer, bwx11));
    }
    
    protected boolean isSameRailWithPower(final Level bhr, final BlockPos ew, final boolean boolean3, final int integer, final RailShape bwx) {
        final BlockState bvt7 = bhr.getBlockState(ew);
        if (bvt7.getBlock() != this) {
            return false;
        }
        final RailShape bwx2 = bvt7.<RailShape>getValue(PoweredRailBlock.SHAPE);
        return (bwx != RailShape.EAST_WEST || (bwx2 != RailShape.NORTH_SOUTH && bwx2 != RailShape.ASCENDING_NORTH && bwx2 != RailShape.ASCENDING_SOUTH)) && (bwx != RailShape.NORTH_SOUTH || (bwx2 != RailShape.EAST_WEST && bwx2 != RailShape.ASCENDING_EAST && bwx2 != RailShape.ASCENDING_WEST)) && bvt7.<Boolean>getValue((Property<Boolean>)PoweredRailBlock.POWERED) && (bhr.hasNeighborSignal(ew) || this.findPoweredRailSignal(bhr, ew, bvt7, boolean3, integer + 1));
    }
    
    @Override
    protected void updateState(final BlockState bvt, final Level bhr, final BlockPos ew, final Block bmv) {
        final boolean boolean6 = bvt.<Boolean>getValue((Property<Boolean>)PoweredRailBlock.POWERED);
        final boolean boolean7 = bhr.hasNeighborSignal(ew) || this.findPoweredRailSignal(bhr, ew, bvt, true, 0) || this.findPoweredRailSignal(bhr, ew, bvt, false, 0);
        if (boolean7 != boolean6) {
            bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)PoweredRailBlock.POWERED, boolean7), 3);
            bhr.updateNeighborsAt(ew.below(), this);
            if (bvt.<RailShape>getValue(PoweredRailBlock.SHAPE).isAscending()) {
                bhr.updateNeighborsAt(ew.above(), this);
            }
        }
    }
    
    @Override
    public Property<RailShape> getShapeProperty() {
        return PoweredRailBlock.SHAPE;
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        Label_0424: {
            switch (brg) {
                case CLOCKWISE_180: {
                    switch (bvt.<RailShape>getValue(PoweredRailBlock.SHAPE)) {
                        case ASCENDING_EAST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.ASCENDING_WEST);
                        }
                        case ASCENDING_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.ASCENDING_EAST);
                        }
                        case ASCENDING_NORTH: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.ASCENDING_SOUTH);
                        }
                        case ASCENDING_SOUTH: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.ASCENDING_NORTH);
                        }
                        case SOUTH_EAST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.NORTH_WEST);
                        }
                        case SOUTH_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.NORTH_EAST);
                        }
                        case NORTH_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.SOUTH_EAST);
                        }
                        case NORTH_EAST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.SOUTH_WEST);
                        }
                        default: {
                            break Label_0424;
                        }
                    }
                    break;
                }
                case COUNTERCLOCKWISE_90: {
                    switch (bvt.<RailShape>getValue(PoweredRailBlock.SHAPE)) {
                        case NORTH_SOUTH: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.EAST_WEST);
                        }
                        case EAST_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.NORTH_SOUTH);
                        }
                        case ASCENDING_EAST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.ASCENDING_NORTH);
                        }
                        case ASCENDING_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.ASCENDING_SOUTH);
                        }
                        case ASCENDING_NORTH: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.ASCENDING_WEST);
                        }
                        case ASCENDING_SOUTH: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.ASCENDING_EAST);
                        }
                        case SOUTH_EAST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.NORTH_EAST);
                        }
                        case SOUTH_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.SOUTH_EAST);
                        }
                        case NORTH_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.SOUTH_WEST);
                        }
                        case NORTH_EAST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.NORTH_WEST);
                        }
                        default: {
                            break Label_0424;
                        }
                    }
                    break;
                }
                case CLOCKWISE_90: {
                    switch (bvt.<RailShape>getValue(PoweredRailBlock.SHAPE)) {
                        case NORTH_SOUTH: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.EAST_WEST);
                        }
                        case EAST_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.NORTH_SOUTH);
                        }
                        case ASCENDING_EAST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.ASCENDING_SOUTH);
                        }
                        case ASCENDING_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.ASCENDING_NORTH);
                        }
                        case ASCENDING_NORTH: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.ASCENDING_EAST);
                        }
                        case ASCENDING_SOUTH: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.ASCENDING_WEST);
                        }
                        case SOUTH_EAST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.SOUTH_WEST);
                        }
                        case SOUTH_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.NORTH_WEST);
                        }
                        case NORTH_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.NORTH_EAST);
                        }
                        case NORTH_EAST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.SOUTH_EAST);
                        }
                        default: {
                            break Label_0424;
                        }
                    }
                    break;
                }
            }
        }
        return bvt;
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        final RailShape bwx4 = bvt.<RailShape>getValue(PoweredRailBlock.SHAPE);
        Label_0319: {
            switch (bqg) {
                case LEFT_RIGHT: {
                    switch (bwx4) {
                        case ASCENDING_NORTH: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.ASCENDING_SOUTH);
                        }
                        case ASCENDING_SOUTH: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.ASCENDING_NORTH);
                        }
                        case SOUTH_EAST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.NORTH_EAST);
                        }
                        case SOUTH_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.NORTH_WEST);
                        }
                        case NORTH_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.SOUTH_WEST);
                        }
                        case NORTH_EAST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.SOUTH_EAST);
                        }
                        default: {
                            break Label_0319;
                        }
                    }
                    break;
                }
                case FRONT_BACK: {
                    switch (bwx4) {
                        case ASCENDING_EAST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.ASCENDING_WEST);
                        }
                        case ASCENDING_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.ASCENDING_EAST);
                        }
                        case SOUTH_EAST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.SOUTH_WEST);
                        }
                        case SOUTH_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.SOUTH_EAST);
                        }
                        case NORTH_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.NORTH_EAST);
                        }
                        case NORTH_EAST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(PoweredRailBlock.SHAPE, RailShape.NORTH_WEST);
                        }
                        default: {
                            break Label_0319;
                        }
                    }
                    break;
                }
            }
        }
        return super.mirror(bvt, bqg);
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(PoweredRailBlock.SHAPE, PoweredRailBlock.POWERED);
    }
    
    static {
        SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
        POWERED = BlockStateProperties.POWERED;
    }
}
