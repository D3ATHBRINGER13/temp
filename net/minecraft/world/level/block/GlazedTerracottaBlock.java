package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class GlazedTerracottaBlock extends HorizontalDirectionalBlock {
    public GlazedTerracottaBlock(final Properties c) {
        super(c);
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(GlazedTerracottaBlock.FACING);
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        return ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)GlazedTerracottaBlock.FACING, ban.getHorizontalDirection().getOpposite());
    }
    
    @Override
    public PushReaction getPistonPushReaction(final BlockState bvt) {
        return PushReaction.PUSH_ONLY;
    }
}
