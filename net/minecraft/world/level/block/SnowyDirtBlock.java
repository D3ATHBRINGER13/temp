package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class SnowyDirtBlock extends Block {
    public static final BooleanProperty SNOWY;
    
    protected SnowyDirtBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Boolean>setValue((Property<Comparable>)SnowyDirtBlock.SNOWY, false));
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (fb == Direction.UP) {
            final Block bmv8 = bvt3.getBlock();
            return ((AbstractStateHolder<O, BlockState>)bvt1).<Comparable, Boolean>setValue((Property<Comparable>)SnowyDirtBlock.SNOWY, bmv8 == Blocks.SNOW_BLOCK || bmv8 == Blocks.SNOW);
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final Block bmv3 = ban.getLevel().getBlockState(ban.getClickedPos().above()).getBlock();
        return ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)SnowyDirtBlock.SNOWY, bmv3 == Blocks.SNOW_BLOCK || bmv3 == Blocks.SNOW);
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(SnowyDirtBlock.SNOWY);
    }
    
    static {
        SNOWY = BlockStateProperties.SNOWY;
    }
}
