package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.phys.shapes.CollisionContext;
import javax.annotation.Nullable;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class BaseCoralPlantTypeBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED;
    private static final VoxelShape AABB;
    
    protected BaseCoralPlantTypeBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Boolean>setValue((Property<Comparable>)BaseCoralPlantTypeBlock.WATERLOGGED, true));
    }
    
    protected void tryScheduleDieTick(final BlockState bvt, final LevelAccessor bhs, final BlockPos ew) {
        if (!scanForWater(bvt, bhs, ew)) {
            bhs.getBlockTicks().scheduleTick(ew, this, 60 + bhs.getRandom().nextInt(40));
        }
    }
    
    protected static boolean scanForWater(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        if (bvt.<Boolean>getValue((Property<Boolean>)BaseCoralPlantTypeBlock.WATERLOGGED)) {
            return true;
        }
        for (final Direction fb7 : Direction.values()) {
            if (bhb.getFluidState(ew.relative(fb7)).is(FluidTags.WATER)) {
                return true;
            }
        }
        return false;
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final FluidState clk3 = ban.getLevel().getFluidState(ban.getClickedPos());
        return ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)BaseCoralPlantTypeBlock.WATERLOGGED, clk3.is(FluidTags.WATER) && clk3.getAmount() == 8);
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return BaseCoralPlantTypeBlock.AABB;
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (bvt1.<Boolean>getValue((Property<Boolean>)BaseCoralPlantTypeBlock.WATERLOGGED)) {
            bhs.getLiquidTicks().scheduleTick(ew5, Fluids.WATER, Fluids.WATER.getTickDelay(bhs));
        }
        if (fb == Direction.DOWN && !this.canSurvive(bvt1, bhs, ew5)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        final BlockPos ew2 = ew.below();
        return bhu.getBlockState(ew2).isFaceSturdy(bhu, ew2, Direction.UP);
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(BaseCoralPlantTypeBlock.WATERLOGGED);
    }
    
    @Override
    public FluidState getFluidState(final BlockState bvt) {
        if (bvt.<Boolean>getValue((Property<Boolean>)BaseCoralPlantTypeBlock.WATERLOGGED)) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState(bvt);
    }
    
    static {
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        AABB = Block.box(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);
    }
}
