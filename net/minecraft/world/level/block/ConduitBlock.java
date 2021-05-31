package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.item.ItemStack;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class ConduitBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape SHAPE;
    
    public ConduitBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Boolean>setValue((Property<Comparable>)ConduitBlock.WATERLOGGED, true));
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(ConduitBlock.WATERLOGGED);
    }
    
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        return new ConduitBlockEntity();
    }
    
    @Override
    public RenderShape getRenderShape(final BlockState bvt) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
    
    @Override
    public FluidState getFluidState(final BlockState bvt) {
        if (bvt.<Boolean>getValue((Property<Boolean>)ConduitBlock.WATERLOGGED)) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState(bvt);
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (bvt1.<Boolean>getValue((Property<Boolean>)ConduitBlock.WATERLOGGED)) {
            bhs.getLiquidTicks().scheduleTick(ew5, Fluids.WATER, Fluids.WATER.getTickDelay(bhs));
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return ConduitBlock.SHAPE;
    }
    
    @Override
    public void setPlacedBy(final Level bhr, final BlockPos ew, final BlockState bvt, @Nullable final LivingEntity aix, final ItemStack bcj) {
        if (bcj.hasCustomHoverName()) {
            final BlockEntity btw7 = bhr.getBlockEntity(ew);
            if (btw7 instanceof BeaconBlockEntity) {
                ((BeaconBlockEntity)btw7).setCustomName(bcj.getHoverName());
            }
        }
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final FluidState clk3 = ban.getLevel().getFluidState(ban.getClickedPos());
        return ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)ConduitBlock.WATERLOGGED, clk3.is(FluidTags.WATER) && clk3.getAmount() == 8);
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return false;
    }
    
    static {
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        SHAPE = Block.box(5.0, 5.0, 5.0, 11.0, 11.0, 11.0);
    }
}
