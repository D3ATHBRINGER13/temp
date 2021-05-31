package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public abstract class HorizontalDirectionalBlock extends Block {
    public static final DirectionProperty FACING;
    
    protected HorizontalDirectionalBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)HorizontalDirectionalBlock.FACING, brg.rotate(bvt.<Direction>getValue((Property<Direction>)HorizontalDirectionalBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        return bvt.rotate(bqg.getRotation(bvt.<Direction>getValue((Property<Direction>)HorizontalDirectionalBlock.FACING)));
    }
    
    static {
        FACING = BlockStateProperties.HORIZONTAL_FACING;
    }
}
