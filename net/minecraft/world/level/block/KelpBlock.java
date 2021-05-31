package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.BlockLayer;
import javax.annotation.Nullable;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class KelpBlock extends Block implements LiquidBlockContainer {
    public static final IntegerProperty AGE;
    protected static final VoxelShape SHAPE;
    
    protected KelpBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Integer>setValue((Property<Comparable>)KelpBlock.AGE, 0));
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return KelpBlock.SHAPE;
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final FluidState clk3 = ban.getLevel().getFluidState(ban.getClickedPos());
        if (clk3.is(FluidTags.WATER) && clk3.getAmount() == 8) {
            return this.getStateForPlacement(ban.getLevel());
        }
        return null;
    }
    
    public BlockState getStateForPlacement(final LevelAccessor bhs) {
        return ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Integer>setValue((Property<Comparable>)KelpBlock.AGE, bhs.getRandom().nextInt(25));
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    @Override
    public FluidState getFluidState(final BlockState bvt) {
        return Fluids.WATER.getSource(false);
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (!bvt.canSurvive(bhr, ew)) {
            bhr.destroyBlock(ew, true);
            return;
        }
        final BlockPos ew2 = ew.above();
        final BlockState bvt2 = bhr.getBlockState(ew2);
        if (bvt2.getBlock() == Blocks.WATER && bvt.<Integer>getValue((Property<Integer>)KelpBlock.AGE) < 25 && random.nextDouble() < 0.14) {
            bhr.setBlockAndUpdate(ew2, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable>cycle((Property<Comparable>)KelpBlock.AGE));
        }
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        final BlockPos ew2 = ew.below();
        final BlockState bvt2 = bhu.getBlockState(ew2);
        final Block bmv7 = bvt2.getBlock();
        return bmv7 != Blocks.MAGMA_BLOCK && (bmv7 == this || bmv7 == Blocks.KELP_PLANT || bvt2.isFaceSturdy(bhu, ew2, Direction.UP));
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (!bvt1.canSurvive(bhs, ew5)) {
            if (fb == Direction.DOWN) {
                return Blocks.AIR.defaultBlockState();
            }
            bhs.getBlockTicks().scheduleTick(ew5, this, 1);
        }
        if (fb == Direction.UP && bvt3.getBlock() == this) {
            return Blocks.KELP_PLANT.defaultBlockState();
        }
        bhs.getLiquidTicks().scheduleTick(ew5, Fluids.WATER, Fluids.WATER.getTickDelay(bhs));
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(KelpBlock.AGE);
    }
    
    @Override
    public boolean canPlaceLiquid(final BlockGetter bhb, final BlockPos ew, final BlockState bvt, final Fluid clj) {
        return false;
    }
    
    @Override
    public boolean placeLiquid(final LevelAccessor bhs, final BlockPos ew, final BlockState bvt, final FluidState clk) {
        return false;
    }
    
    static {
        AGE = BlockStateProperties.AGE_25;
        SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 9.0, 16.0);
    }
}
