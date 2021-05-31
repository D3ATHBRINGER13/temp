package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import javax.annotation.Nullable;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class SlabBlock extends Block implements SimpleWaterloggedBlock {
    public static final EnumProperty<SlabType> TYPE;
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape BOTTOM_AABB;
    protected static final VoxelShape TOP_AABB;
    
    public SlabBlock(final Properties c) {
        super(c);
        this.registerDefaultState((((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).setValue(SlabBlock.TYPE, SlabType.BOTTOM)).<Comparable, Boolean>setValue((Property<Comparable>)SlabBlock.WATERLOGGED, false));
    }
    
    @Override
    public boolean useShapeForLightOcclusion(final BlockState bvt) {
        return bvt.<SlabType>getValue(SlabBlock.TYPE) != SlabType.DOUBLE;
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(SlabBlock.TYPE, SlabBlock.WATERLOGGED);
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        final SlabType bwz6 = bvt.<SlabType>getValue(SlabBlock.TYPE);
        switch (bwz6) {
            case DOUBLE: {
                return Shapes.block();
            }
            case TOP: {
                return SlabBlock.TOP_AABB;
            }
            default: {
                return SlabBlock.BOTTOM_AABB;
            }
        }
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final BlockPos ew3 = ban.getClickedPos();
        final BlockState bvt4 = ban.getLevel().getBlockState(ew3);
        if (bvt4.getBlock() == this) {
            return (((AbstractStateHolder<O, BlockState>)bvt4).setValue(SlabBlock.TYPE, SlabType.DOUBLE)).<Comparable, Boolean>setValue((Property<Comparable>)SlabBlock.WATERLOGGED, false);
        }
        final FluidState clk5 = ban.getLevel().getFluidState(ew3);
        final BlockState bvt5 = (((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).setValue(SlabBlock.TYPE, SlabType.BOTTOM)).<Comparable, Boolean>setValue((Property<Comparable>)SlabBlock.WATERLOGGED, clk5.getType() == Fluids.WATER);
        final Direction fb7 = ban.getClickedFace();
        if (fb7 == Direction.DOWN || (fb7 != Direction.UP && ban.getClickLocation().y - ew3.getY() > 0.5)) {
            return ((AbstractStateHolder<O, BlockState>)bvt5).<SlabType, SlabType>setValue(SlabBlock.TYPE, SlabType.TOP);
        }
        return bvt5;
    }
    
    @Override
    public boolean canBeReplaced(final BlockState bvt, final BlockPlaceContext ban) {
        final ItemStack bcj4 = ban.getItemInHand();
        final SlabType bwz5 = bvt.<SlabType>getValue(SlabBlock.TYPE);
        if (bwz5 == SlabType.DOUBLE || bcj4.getItem() != this.asItem()) {
            return false;
        }
        if (!ban.replacingClickedOnBlock()) {
            return true;
        }
        final boolean boolean6 = ban.getClickLocation().y - ban.getClickedPos().getY() > 0.5;
        final Direction fb7 = ban.getClickedFace();
        if (bwz5 == SlabType.BOTTOM) {
            return fb7 == Direction.UP || (boolean6 && fb7.getAxis().isHorizontal());
        }
        return fb7 == Direction.DOWN || (!boolean6 && fb7.getAxis().isHorizontal());
    }
    
    @Override
    public FluidState getFluidState(final BlockState bvt) {
        if (bvt.<Boolean>getValue((Property<Boolean>)SlabBlock.WATERLOGGED)) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState(bvt);
    }
    
    @Override
    public boolean placeLiquid(final LevelAccessor bhs, final BlockPos ew, final BlockState bvt, final FluidState clk) {
        return bvt.<SlabType>getValue(SlabBlock.TYPE) != SlabType.DOUBLE && super.placeLiquid(bhs, ew, bvt, clk);
    }
    
    @Override
    public boolean canPlaceLiquid(final BlockGetter bhb, final BlockPos ew, final BlockState bvt, final Fluid clj) {
        return bvt.<SlabType>getValue(SlabBlock.TYPE) != SlabType.DOUBLE && super.canPlaceLiquid(bhb, ew, bvt, clj);
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (bvt1.<Boolean>getValue((Property<Boolean>)SlabBlock.WATERLOGGED)) {
            bhs.getLiquidTicks().scheduleTick(ew5, Fluids.WATER, Fluids.WATER.getTickDelay(bhs));
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        switch (cns) {
            case LAND: {
                return false;
            }
            case WATER: {
                return bhb.getFluidState(ew).is(FluidTags.WATER);
            }
            case AIR: {
                return false;
            }
            default: {
                return false;
            }
        }
    }
    
    static {
        TYPE = BlockStateProperties.SLAB_TYPE;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        BOTTOM_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
        TOP_AABB = Block.box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);
    }
}
