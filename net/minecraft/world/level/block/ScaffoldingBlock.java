package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import java.util.Iterator;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import java.util.Random;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ScaffoldingBlock extends Block implements SimpleWaterloggedBlock {
    private static final VoxelShape STABLE_SHAPE;
    private static final VoxelShape UNSTABLE_SHAPE;
    private static final VoxelShape UNSTABLE_SHAPE_BOTTOM;
    private static final VoxelShape BELOW_BLOCK;
    public static final IntegerProperty DISTANCE;
    public static final BooleanProperty WATERLOGGED;
    public static final BooleanProperty BOTTOM;
    
    protected ScaffoldingBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)ScaffoldingBlock.DISTANCE, 7)).setValue((Property<Comparable>)ScaffoldingBlock.WATERLOGGED, false)).<Comparable, Boolean>setValue((Property<Comparable>)ScaffoldingBlock.BOTTOM, false));
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(ScaffoldingBlock.DISTANCE, ScaffoldingBlock.WATERLOGGED, ScaffoldingBlock.BOTTOM);
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        if (!csn.isHoldingItem(bvt.getBlock().asItem())) {
            return bvt.<Boolean>getValue((Property<Boolean>)ScaffoldingBlock.BOTTOM) ? ScaffoldingBlock.UNSTABLE_SHAPE : ScaffoldingBlock.STABLE_SHAPE;
        }
        return Shapes.block();
    }
    
    @Override
    public VoxelShape getInteractionShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return Shapes.block();
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    @Override
    public boolean canBeReplaced(final BlockState bvt, final BlockPlaceContext ban) {
        return ban.getItemInHand().getItem() == this.asItem();
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final BlockPos ew3 = ban.getClickedPos();
        final Level bhr4 = ban.getLevel();
        final int integer5 = getDistance(bhr4, ew3);
        return ((((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).setValue((Property<Comparable>)ScaffoldingBlock.WATERLOGGED, bhr4.getFluidState(ew3).getType() == Fluids.WATER)).setValue((Property<Comparable>)ScaffoldingBlock.DISTANCE, integer5)).<Comparable, Boolean>setValue((Property<Comparable>)ScaffoldingBlock.BOTTOM, this.isBottom(bhr4, ew3, integer5));
    }
    
    @Override
    public void onPlace(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (!bhr.isClientSide) {
            bhr.getBlockTicks().scheduleTick(ew, this, 1);
        }
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (bvt1.<Boolean>getValue((Property<Boolean>)ScaffoldingBlock.WATERLOGGED)) {
            bhs.getLiquidTicks().scheduleTick(ew5, Fluids.WATER, Fluids.WATER.getTickDelay(bhs));
        }
        if (!bhs.isClientSide()) {
            bhs.getBlockTicks().scheduleTick(ew5, this, 1);
        }
        return bvt1;
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        final int integer6 = getDistance(bhr, ew);
        final BlockState bvt2 = (((AbstractStateHolder<O, BlockState>)bvt).setValue((Property<Comparable>)ScaffoldingBlock.DISTANCE, integer6)).<Comparable, Boolean>setValue((Property<Comparable>)ScaffoldingBlock.BOTTOM, this.isBottom(bhr, ew, integer6));
        if (bvt2.<Integer>getValue((Property<Integer>)ScaffoldingBlock.DISTANCE) == 7) {
            if (bvt.<Integer>getValue((Property<Integer>)ScaffoldingBlock.DISTANCE) == 7) {
                bhr.addFreshEntity(new FallingBlockEntity(bhr, ew.getX() + 0.5, ew.getY(), ew.getZ() + 0.5, ((AbstractStateHolder<O, BlockState>)bvt2).<Comparable, Boolean>setValue((Property<Comparable>)ScaffoldingBlock.WATERLOGGED, false)));
            }
            else {
                bhr.destroyBlock(ew, true);
            }
        }
        else if (bvt != bvt2) {
            bhr.setBlock(ew, bvt2, 3);
        }
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        return getDistance(bhu, ew) < 7;
    }
    
    @Override
    public VoxelShape getCollisionShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        if (csn.isAbove(Shapes.block(), ew, true) && !csn.isSneaking()) {
            return ScaffoldingBlock.STABLE_SHAPE;
        }
        if (bvt.<Integer>getValue((Property<Integer>)ScaffoldingBlock.DISTANCE) != 0 && bvt.<Boolean>getValue((Property<Boolean>)ScaffoldingBlock.BOTTOM) && csn.isAbove(ScaffoldingBlock.BELOW_BLOCK, ew, true)) {
            return ScaffoldingBlock.UNSTABLE_SHAPE_BOTTOM;
        }
        return Shapes.empty();
    }
    
    @Override
    public FluidState getFluidState(final BlockState bvt) {
        if (bvt.<Boolean>getValue((Property<Boolean>)ScaffoldingBlock.WATERLOGGED)) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState(bvt);
    }
    
    private boolean isBottom(final BlockGetter bhb, final BlockPos ew, final int integer) {
        return integer > 0 && bhb.getBlockState(ew.below()).getBlock() != this;
    }
    
    public static int getDistance(final BlockGetter bhb, final BlockPos ew) {
        final BlockPos.MutableBlockPos a3 = new BlockPos.MutableBlockPos(ew).move(Direction.DOWN);
        final BlockState bvt4 = bhb.getBlockState(a3);
        int integer5 = 7;
        if (bvt4.getBlock() == Blocks.SCAFFOLDING) {
            integer5 = bvt4.<Integer>getValue((Property<Integer>)ScaffoldingBlock.DISTANCE);
        }
        else if (bvt4.isFaceSturdy(bhb, a3, Direction.UP)) {
            return 0;
        }
        for (final Direction fb7 : Direction.Plane.HORIZONTAL) {
            final BlockState bvt5 = bhb.getBlockState(a3.set(ew).move(fb7));
            if (bvt5.getBlock() != Blocks.SCAFFOLDING) {
                continue;
            }
            integer5 = Math.min(integer5, bvt5.<Integer>getValue((Property<Integer>)ScaffoldingBlock.DISTANCE) + 1);
            if (integer5 == 1) {
                break;
            }
        }
        return integer5;
    }
    
    static {
        UNSTABLE_SHAPE_BOTTOM = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
        BELOW_BLOCK = Shapes.block().move(0.0, -1.0, 0.0);
        DISTANCE = BlockStateProperties.STABILITY_DISTANCE;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        BOTTOM = BlockStateProperties.BOTTOM;
        final VoxelShape ctc1 = Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);
        final VoxelShape ctc2 = Block.box(0.0, 0.0, 0.0, 2.0, 16.0, 2.0);
        final VoxelShape ctc3 = Block.box(14.0, 0.0, 0.0, 16.0, 16.0, 2.0);
        final VoxelShape ctc4 = Block.box(0.0, 0.0, 14.0, 2.0, 16.0, 16.0);
        final VoxelShape ctc5 = Block.box(14.0, 0.0, 14.0, 16.0, 16.0, 16.0);
        STABLE_SHAPE = Shapes.or(ctc1, ctc2, ctc3, ctc4, ctc5);
        final VoxelShape ctc6 = Block.box(0.0, 0.0, 0.0, 2.0, 2.0, 16.0);
        final VoxelShape ctc7 = Block.box(14.0, 0.0, 0.0, 16.0, 2.0, 16.0);
        final VoxelShape ctc8 = Block.box(0.0, 0.0, 14.0, 16.0, 2.0, 16.0);
        final VoxelShape ctc9 = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 2.0);
        UNSTABLE_SHAPE = Shapes.or(ScaffoldingBlock.UNSTABLE_SHAPE_BOTTOM, ScaffoldingBlock.STABLE_SHAPE, ctc7, ctc6, ctc9, ctc8);
    }
}
