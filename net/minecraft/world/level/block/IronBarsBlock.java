package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;

public class IronBarsBlock extends CrossCollisionBlock {
    protected IronBarsBlock(final Properties c) {
        super(1.0f, 1.0f, 16.0f, 16.0f, 16.0f, c);
        this.registerDefaultState(((((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)IronBarsBlock.NORTH, false)).setValue((Property<Comparable>)IronBarsBlock.EAST, false)).setValue((Property<Comparable>)IronBarsBlock.SOUTH, false)).setValue((Property<Comparable>)IronBarsBlock.WEST, false)).<Comparable, Boolean>setValue((Property<Comparable>)IronBarsBlock.WATERLOGGED, false));
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final BlockGetter bhb3 = ban.getLevel();
        final BlockPos ew4 = ban.getClickedPos();
        final FluidState clk5 = ban.getLevel().getFluidState(ban.getClickedPos());
        final BlockPos ew5 = ew4.north();
        final BlockPos ew6 = ew4.south();
        final BlockPos ew7 = ew4.west();
        final BlockPos ew8 = ew4.east();
        final BlockState bvt10 = bhb3.getBlockState(ew5);
        final BlockState bvt11 = bhb3.getBlockState(ew6);
        final BlockState bvt12 = bhb3.getBlockState(ew7);
        final BlockState bvt13 = bhb3.getBlockState(ew8);
        return ((((((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).setValue((Property<Comparable>)IronBarsBlock.NORTH, this.attachsTo(bvt10, bvt10.isFaceSturdy(bhb3, ew5, Direction.SOUTH)))).setValue((Property<Comparable>)IronBarsBlock.SOUTH, this.attachsTo(bvt11, bvt11.isFaceSturdy(bhb3, ew6, Direction.NORTH)))).setValue((Property<Comparable>)IronBarsBlock.WEST, this.attachsTo(bvt12, bvt12.isFaceSturdy(bhb3, ew7, Direction.EAST)))).setValue((Property<Comparable>)IronBarsBlock.EAST, this.attachsTo(bvt13, bvt13.isFaceSturdy(bhb3, ew8, Direction.WEST)))).<Comparable, Boolean>setValue((Property<Comparable>)IronBarsBlock.WATERLOGGED, clk5.getType() == Fluids.WATER);
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (bvt1.<Boolean>getValue((Property<Boolean>)IronBarsBlock.WATERLOGGED)) {
            bhs.getLiquidTicks().scheduleTick(ew5, Fluids.WATER, Fluids.WATER.getTickDelay(bhs));
        }
        if (fb.getAxis().isHorizontal()) {
            return ((AbstractStateHolder<O, BlockState>)bvt1).<Comparable, Boolean>setValue((Property<Comparable>)IronBarsBlock.PROPERTY_BY_DIRECTION.get(fb), this.attachsTo(bvt3, bvt3.isFaceSturdy(bhs, ew6, fb.getOpposite())));
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public boolean skipRendering(final BlockState bvt1, final BlockState bvt2, final Direction fb) {
        if (bvt2.getBlock() == this) {
            if (!fb.getAxis().isHorizontal()) {
                return true;
            }
            if (bvt1.<Boolean>getValue((Property<Boolean>)IronBarsBlock.PROPERTY_BY_DIRECTION.get(fb)) && bvt2.<Boolean>getValue((Property<Boolean>)IronBarsBlock.PROPERTY_BY_DIRECTION.get(fb.getOpposite()))) {
                return true;
            }
        }
        return super.skipRendering(bvt1, bvt2, fb);
    }
    
    public final boolean attachsTo(final BlockState bvt, final boolean boolean2) {
        final Block bmv4 = bvt.getBlock();
        return (!Block.isExceptionForConnection(bmv4) && boolean2) || bmv4 instanceof IronBarsBlock;
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT_MIPPED;
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(IronBarsBlock.NORTH, IronBarsBlock.EAST, IronBarsBlock.WEST, IronBarsBlock.SOUTH, IronBarsBlock.WATERLOGGED);
    }
}
