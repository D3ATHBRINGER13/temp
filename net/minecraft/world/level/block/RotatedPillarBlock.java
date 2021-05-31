package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class RotatedPillarBlock extends Block {
    public static final EnumProperty<Direction.Axis> AXIS;
    
    public RotatedPillarBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Direction.Axis, Direction.Axis>setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y));
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        switch (brg) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90: {
                switch (bvt.<Direction.Axis>getValue(RotatedPillarBlock.AXIS)) {
                    case X: {
                        return ((AbstractStateHolder<O, BlockState>)bvt).<Direction.Axis, Direction.Axis>setValue(RotatedPillarBlock.AXIS, Direction.Axis.Z);
                    }
                    case Z: {
                        return ((AbstractStateHolder<O, BlockState>)bvt).<Direction.Axis, Direction.Axis>setValue(RotatedPillarBlock.AXIS, Direction.Axis.X);
                    }
                    default: {
                        return bvt;
                    }
                }
                break;
            }
            default: {
                return bvt;
            }
        }
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(RotatedPillarBlock.AXIS);
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        return ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Direction.Axis, Direction.Axis>setValue(RotatedPillarBlock.AXIS, ban.getClickedFace().getAxis());
    }
    
    static {
        AXIS = BlockStateProperties.AXIS;
    }
}
