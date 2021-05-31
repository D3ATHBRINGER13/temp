package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import javax.annotation.Nullable;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class SeaPickleBlock extends BushBlock implements BonemealableBlock, SimpleWaterloggedBlock {
    public static final IntegerProperty PICKLES;
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape ONE_AABB;
    protected static final VoxelShape TWO_AABB;
    protected static final VoxelShape THREE_AABB;
    protected static final VoxelShape FOUR_AABB;
    
    protected SeaPickleBlock(final Properties c) {
        super(c);
        this.registerDefaultState((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)SeaPickleBlock.PICKLES, 1)).<Comparable, Boolean>setValue((Property<Comparable>)SeaPickleBlock.WATERLOGGED, true));
    }
    
    @Override
    public int getLightEmission(final BlockState bvt) {
        return this.isDead(bvt) ? 0 : (super.getLightEmission(bvt) + 3 * bvt.<Integer>getValue((Property<Integer>)SeaPickleBlock.PICKLES));
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final BlockState bvt3 = ban.getLevel().getBlockState(ban.getClickedPos());
        if (bvt3.getBlock() == this) {
            return ((AbstractStateHolder<O, BlockState>)bvt3).<Comparable, Integer>setValue((Property<Comparable>)SeaPickleBlock.PICKLES, Math.min(4, bvt3.<Integer>getValue((Property<Integer>)SeaPickleBlock.PICKLES) + 1));
        }
        final FluidState clk4 = ban.getLevel().getFluidState(ban.getClickedPos());
        final boolean boolean5 = clk4.is(FluidTags.WATER) && clk4.getAmount() == 8;
        return ((AbstractStateHolder<O, BlockState>)super.getStateForPlacement(ban)).<Comparable, Boolean>setValue((Property<Comparable>)SeaPickleBlock.WATERLOGGED, boolean5);
    }
    
    private boolean isDead(final BlockState bvt) {
        return !bvt.<Boolean>getValue((Property<Boolean>)SeaPickleBlock.WATERLOGGED);
    }
    
    @Override
    protected boolean mayPlaceOn(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return !bvt.getCollisionShape(bhb, ew).getFaceShape(Direction.UP).isEmpty();
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        final BlockPos ew2 = ew.below();
        return this.mayPlaceOn(bhu.getBlockState(ew2), bhu, ew2);
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (!bvt1.canSurvive(bhs, ew5)) {
            return Blocks.AIR.defaultBlockState();
        }
        if (bvt1.<Boolean>getValue((Property<Boolean>)SeaPickleBlock.WATERLOGGED)) {
            bhs.getLiquidTicks().scheduleTick(ew5, Fluids.WATER, Fluids.WATER.getTickDelay(bhs));
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public boolean canBeReplaced(final BlockState bvt, final BlockPlaceContext ban) {
        return (ban.getItemInHand().getItem() == this.asItem() && bvt.<Integer>getValue((Property<Integer>)SeaPickleBlock.PICKLES) < 4) || super.canBeReplaced(bvt, ban);
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        switch (bvt.<Integer>getValue((Property<Integer>)SeaPickleBlock.PICKLES)) {
            default: {
                return SeaPickleBlock.ONE_AABB;
            }
            case 2: {
                return SeaPickleBlock.TWO_AABB;
            }
            case 3: {
                return SeaPickleBlock.THREE_AABB;
            }
            case 4: {
                return SeaPickleBlock.FOUR_AABB;
            }
        }
    }
    
    @Override
    public FluidState getFluidState(final BlockState bvt) {
        if (bvt.<Boolean>getValue((Property<Boolean>)SeaPickleBlock.WATERLOGGED)) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState(bvt);
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(SeaPickleBlock.PICKLES, SeaPickleBlock.WATERLOGGED);
    }
    
    @Override
    public boolean isValidBonemealTarget(final BlockGetter bhb, final BlockPos ew, final BlockState bvt, final boolean boolean4) {
        return true;
    }
    
    @Override
    public boolean isBonemealSuccess(final Level bhr, final Random random, final BlockPos ew, final BlockState bvt) {
        return true;
    }
    
    @Override
    public void performBonemeal(final Level bhr, final Random random, final BlockPos ew, final BlockState bvt) {
        if (!this.isDead(bvt) && bhr.getBlockState(ew.below()).is(BlockTags.CORAL_BLOCKS)) {
            final int integer6 = 5;
            int integer7 = 1;
            final int integer8 = 2;
            int integer9 = 0;
            final int integer10 = ew.getX() - 2;
            int integer11 = 0;
            for (int integer12 = 0; integer12 < 5; ++integer12) {
                for (int integer13 = 0; integer13 < integer7; ++integer13) {
                    for (int integer14 = 2 + ew.getY() - 1, integer15 = integer14 - 2; integer15 < integer14; ++integer15) {
                        final BlockPos ew2 = new BlockPos(integer10 + integer12, integer15, ew.getZ() - integer11 + integer13);
                        if (ew2 != ew) {
                            if (random.nextInt(6) == 0 && bhr.getBlockState(ew2).getBlock() == Blocks.WATER) {
                                final BlockState bvt2 = bhr.getBlockState(ew2.below());
                                if (bvt2.is(BlockTags.CORAL_BLOCKS)) {
                                    bhr.setBlock(ew2, ((AbstractStateHolder<O, BlockState>)Blocks.SEA_PICKLE.defaultBlockState()).<Comparable, Integer>setValue((Property<Comparable>)SeaPickleBlock.PICKLES, random.nextInt(4) + 1), 3);
                                }
                            }
                        }
                    }
                }
                if (integer9 < 2) {
                    integer7 += 2;
                    ++integer11;
                }
                else {
                    integer7 -= 2;
                    --integer11;
                }
                ++integer9;
            }
            bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)SeaPickleBlock.PICKLES, 4), 2);
        }
    }
    
    static {
        PICKLES = BlockStateProperties.PICKLES;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        ONE_AABB = Block.box(6.0, 0.0, 6.0, 10.0, 6.0, 10.0);
        TWO_AABB = Block.box(3.0, 0.0, 3.0, 13.0, 6.0, 13.0);
        THREE_AABB = Block.box(2.0, 0.0, 2.0, 14.0, 6.0, 14.0);
        FOUR_AABB = Block.box(2.0, 0.0, 2.0, 14.0, 7.0, 14.0);
    }
}
