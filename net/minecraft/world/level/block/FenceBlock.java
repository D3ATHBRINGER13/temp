package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.Direction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FenceBlock extends CrossCollisionBlock {
    private final VoxelShape[] occlusionByIndex;
    
    public FenceBlock(final Properties c) {
        super(2.0f, 2.0f, 16.0f, 16.0f, 24.0f, c);
        this.registerDefaultState(((((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)FenceBlock.NORTH, false)).setValue((Property<Comparable>)FenceBlock.EAST, false)).setValue((Property<Comparable>)FenceBlock.SOUTH, false)).setValue((Property<Comparable>)FenceBlock.WEST, false)).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.WATERLOGGED, false));
        this.occlusionByIndex = this.makeShapes(2.0f, 1.0f, 16.0f, 6.0f, 15.0f);
    }
    
    @Override
    public VoxelShape getOcclusionShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return this.occlusionByIndex[this.getAABBIndex(bvt)];
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return false;
    }
    
    public boolean connectsTo(final BlockState bvt, final boolean boolean2, final Direction fb) {
        final Block bmv5 = bvt.getBlock();
        final boolean boolean3 = bmv5.is(BlockTags.FENCES) && bvt.getMaterial() == this.material;
        final boolean boolean4 = bmv5 instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(bvt, fb);
        return (!Block.isExceptionForConnection(bmv5) && boolean2) || boolean3 || boolean4;
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        if (bhr.isClientSide) {
            final ItemStack bcj8 = awg.getItemInHand(ahi);
            return bcj8.getItem() == Items.LEAD || bcj8.isEmpty();
        }
        return LeadItem.bindPlayerMobs(awg, bhr, ew);
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final BlockGetter bhb3 = ban.getLevel();
        final BlockPos ew4 = ban.getClickedPos();
        final FluidState clk5 = ban.getLevel().getFluidState(ban.getClickedPos());
        final BlockPos ew5 = ew4.north();
        final BlockPos ew6 = ew4.east();
        final BlockPos ew7 = ew4.south();
        final BlockPos ew8 = ew4.west();
        final BlockState bvt10 = bhb3.getBlockState(ew5);
        final BlockState bvt11 = bhb3.getBlockState(ew6);
        final BlockState bvt12 = bhb3.getBlockState(ew7);
        final BlockState bvt13 = bhb3.getBlockState(ew8);
        return ((((((AbstractStateHolder<O, BlockState>)super.getStateForPlacement(ban)).setValue((Property<Comparable>)FenceBlock.NORTH, this.connectsTo(bvt10, bvt10.isFaceSturdy(bhb3, ew5, Direction.SOUTH), Direction.SOUTH))).setValue((Property<Comparable>)FenceBlock.EAST, this.connectsTo(bvt11, bvt11.isFaceSturdy(bhb3, ew6, Direction.WEST), Direction.WEST))).setValue((Property<Comparable>)FenceBlock.SOUTH, this.connectsTo(bvt12, bvt12.isFaceSturdy(bhb3, ew7, Direction.NORTH), Direction.NORTH))).setValue((Property<Comparable>)FenceBlock.WEST, this.connectsTo(bvt13, bvt13.isFaceSturdy(bhb3, ew8, Direction.EAST), Direction.EAST))).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.WATERLOGGED, clk5.getType() == Fluids.WATER);
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (bvt1.<Boolean>getValue((Property<Boolean>)FenceBlock.WATERLOGGED)) {
            bhs.getLiquidTicks().scheduleTick(ew5, Fluids.WATER, Fluids.WATER.getTickDelay(bhs));
        }
        if (fb.getAxis().getPlane() == Direction.Plane.HORIZONTAL) {
            return ((AbstractStateHolder<O, BlockState>)bvt1).<Comparable, Boolean>setValue((Property<Comparable>)FenceBlock.PROPERTY_BY_DIRECTION.get(fb), this.connectsTo(bvt3, bvt3.isFaceSturdy(bhs, ew6, fb.getOpposite()), fb.getOpposite()));
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(FenceBlock.NORTH, FenceBlock.EAST, FenceBlock.WEST, FenceBlock.SOUTH, FenceBlock.WATERLOGGED);
    }
}
