package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import java.util.Random;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BambooBlock extends Block implements BonemealableBlock {
    protected static final VoxelShape SMALL_SHAPE;
    protected static final VoxelShape LARGE_SHAPE;
    protected static final VoxelShape COLLISION_SHAPE;
    public static final IntegerProperty AGE;
    public static final EnumProperty<BambooLeaves> LEAVES;
    public static final IntegerProperty STAGE;
    
    public BambooBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)BambooBlock.AGE, 0)).setValue(BambooBlock.LEAVES, BambooLeaves.NONE)).<Comparable, Integer>setValue((Property<Comparable>)BambooBlock.STAGE, 0));
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(BambooBlock.AGE, BambooBlock.LEAVES, BambooBlock.STAGE);
    }
    
    @Override
    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }
    
    @Override
    public boolean propagatesSkylightDown(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return true;
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        final VoxelShape ctc6 = (bvt.<BambooLeaves>getValue(BambooBlock.LEAVES) == BambooLeaves.LARGE) ? BambooBlock.LARGE_SHAPE : BambooBlock.SMALL_SHAPE;
        final Vec3 csi7 = bvt.getOffset(bhb, ew);
        return ctc6.move(csi7.x, csi7.y, csi7.z);
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return false;
    }
    
    @Override
    public VoxelShape getCollisionShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        final Vec3 csi6 = bvt.getOffset(bhb, ew);
        return BambooBlock.COLLISION_SHAPE.move(csi6.x, csi6.y, csi6.z);
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final FluidState clk3 = ban.getLevel().getFluidState(ban.getClickedPos());
        if (!clk3.isEmpty()) {
            return null;
        }
        final BlockState bvt4 = ban.getLevel().getBlockState(ban.getClickedPos().below());
        if (!bvt4.is(BlockTags.BAMBOO_PLANTABLE_ON)) {
            return null;
        }
        final Block bmv5 = bvt4.getBlock();
        if (bmv5 == Blocks.BAMBOO_SAPLING) {
            return ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Integer>setValue((Property<Comparable>)BambooBlock.AGE, 0);
        }
        if (bmv5 == Blocks.BAMBOO) {
            final int integer6 = (bvt4.<Integer>getValue((Property<Integer>)BambooBlock.AGE) > 0) ? 1 : 0;
            return ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Integer>setValue((Property<Comparable>)BambooBlock.AGE, integer6);
        }
        return Blocks.BAMBOO_SAPLING.defaultBlockState();
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (!bvt.canSurvive(bhr, ew)) {
            bhr.destroyBlock(ew, true);
            return;
        }
        if (bvt.<Integer>getValue((Property<Integer>)BambooBlock.STAGE) != 0) {
            return;
        }
        if (random.nextInt(3) == 0 && bhr.isEmptyBlock(ew.above()) && bhr.getRawBrightness(ew.above(), 0) >= 9) {
            final int integer6 = this.getHeightBelowUpToMax(bhr, ew) + 1;
            if (integer6 < 16) {
                this.growBamboo(bvt, bhr, ew, random, integer6);
            }
        }
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        return bhu.getBlockState(ew.below()).is(BlockTags.BAMBOO_PLANTABLE_ON);
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (!bvt1.canSurvive(bhs, ew5)) {
            bhs.getBlockTicks().scheduleTick(ew5, this, 1);
        }
        if (fb == Direction.UP && bvt3.getBlock() == Blocks.BAMBOO && bvt3.<Integer>getValue((Property<Integer>)BambooBlock.AGE) > bvt1.<Integer>getValue((Property<Integer>)BambooBlock.AGE)) {
            bhs.setBlock(ew5, ((AbstractStateHolder<O, BlockState>)bvt1).<Comparable>cycle((Property<Comparable>)BambooBlock.AGE), 2);
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public boolean isValidBonemealTarget(final BlockGetter bhb, final BlockPos ew, final BlockState bvt, final boolean boolean4) {
        final int integer6 = this.getHeightAboveUpToMax(bhb, ew);
        final int integer7 = this.getHeightBelowUpToMax(bhb, ew);
        return integer6 + integer7 + 1 < 16 && bhb.getBlockState(ew.above(integer6)).<Integer>getValue((Property<Integer>)BambooBlock.STAGE) != 1;
    }
    
    @Override
    public boolean isBonemealSuccess(final Level bhr, final Random random, final BlockPos ew, final BlockState bvt) {
        return true;
    }
    
    @Override
    public void performBonemeal(final Level bhr, final Random random, final BlockPos ew, final BlockState bvt) {
        int integer6 = this.getHeightAboveUpToMax(bhr, ew);
        final int integer7 = this.getHeightBelowUpToMax(bhr, ew);
        int integer8 = integer6 + integer7 + 1;
        for (int integer9 = 1 + random.nextInt(2), integer10 = 0; integer10 < integer9; ++integer10) {
            final BlockPos ew2 = ew.above(integer6);
            final BlockState bvt2 = bhr.getBlockState(ew2);
            if (integer8 >= 16 || bvt2.<Integer>getValue((Property<Integer>)BambooBlock.STAGE) == 1 || !bhr.isEmptyBlock(ew2.above())) {
                return;
            }
            this.growBamboo(bvt2, bhr, ew2, random, integer8);
            ++integer6;
            ++integer8;
        }
    }
    
    @Override
    public float getDestroyProgress(final BlockState bvt, final Player awg, final BlockGetter bhb, final BlockPos ew) {
        if (awg.getMainHandItem().getItem() instanceof SwordItem) {
            return 1.0f;
        }
        return super.getDestroyProgress(bvt, awg, bhb, ew);
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    protected void growBamboo(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random, final int integer) {
        final BlockState bvt2 = bhr.getBlockState(ew.below());
        final BlockPos ew2 = ew.below(2);
        final BlockState bvt3 = bhr.getBlockState(ew2);
        BambooLeaves bwh10 = BambooLeaves.NONE;
        if (integer >= 1) {
            if (bvt2.getBlock() != Blocks.BAMBOO || bvt2.<BambooLeaves>getValue(BambooBlock.LEAVES) == BambooLeaves.NONE) {
                bwh10 = BambooLeaves.SMALL;
            }
            else if (bvt2.getBlock() == Blocks.BAMBOO && bvt2.<BambooLeaves>getValue(BambooBlock.LEAVES) != BambooLeaves.NONE) {
                bwh10 = BambooLeaves.LARGE;
                if (bvt3.getBlock() == Blocks.BAMBOO) {
                    bhr.setBlock(ew.below(), ((AbstractStateHolder<O, BlockState>)bvt2).<BambooLeaves, BambooLeaves>setValue(BambooBlock.LEAVES, BambooLeaves.SMALL), 3);
                    bhr.setBlock(ew2, ((AbstractStateHolder<O, BlockState>)bvt3).<BambooLeaves, BambooLeaves>setValue(BambooBlock.LEAVES, BambooLeaves.NONE), 3);
                }
            }
        }
        final int integer2 = (bvt.<Integer>getValue((Property<Integer>)BambooBlock.AGE) == 1 || bvt3.getBlock() == Blocks.BAMBOO) ? 1 : 0;
        final int integer3 = ((integer >= 11 && random.nextFloat() < 0.25f) || integer == 15) ? 1 : 0;
        bhr.setBlock(ew.above(), ((((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).setValue((Property<Comparable>)BambooBlock.AGE, integer2)).setValue(BambooBlock.LEAVES, bwh10)).<Comparable, Integer>setValue((Property<Comparable>)BambooBlock.STAGE, integer3), 3);
    }
    
    protected int getHeightAboveUpToMax(final BlockGetter bhb, final BlockPos ew) {
        int integer4;
        for (integer4 = 0; integer4 < 16 && bhb.getBlockState(ew.above(integer4 + 1)).getBlock() == Blocks.BAMBOO; ++integer4) {}
        return integer4;
    }
    
    protected int getHeightBelowUpToMax(final BlockGetter bhb, final BlockPos ew) {
        int integer4;
        for (integer4 = 0; integer4 < 16 && bhb.getBlockState(ew.below(integer4 + 1)).getBlock() == Blocks.BAMBOO; ++integer4) {}
        return integer4;
    }
    
    static {
        SMALL_SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
        LARGE_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
        COLLISION_SHAPE = Block.box(6.5, 0.0, 6.5, 9.5, 16.0, 9.5);
        AGE = BlockStateProperties.AGE_1;
        LEAVES = BlockStateProperties.BAMBOO_LEAVES;
        STAGE = BlockStateProperties.STAGE;
    }
}
