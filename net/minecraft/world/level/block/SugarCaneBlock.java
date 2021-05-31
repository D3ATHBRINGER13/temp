package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.material.FluidState;
import java.util.Iterator;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class SugarCaneBlock extends Block {
    public static final IntegerProperty AGE;
    protected static final VoxelShape SHAPE;
    
    protected SugarCaneBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Integer>setValue((Property<Comparable>)SugarCaneBlock.AGE, 0));
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return SugarCaneBlock.SHAPE;
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (!bvt.canSurvive(bhr, ew)) {
            bhr.destroyBlock(ew, true);
        }
        else if (bhr.isEmptyBlock(ew.above())) {
            int integer6;
            for (integer6 = 1; bhr.getBlockState(ew.below(integer6)).getBlock() == this; ++integer6) {}
            if (integer6 < 3) {
                final int integer7 = bvt.<Integer>getValue((Property<Integer>)SugarCaneBlock.AGE);
                if (integer7 == 15) {
                    bhr.setBlockAndUpdate(ew.above(), this.defaultBlockState());
                    bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)SugarCaneBlock.AGE, 0), 4);
                }
                else {
                    bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)SugarCaneBlock.AGE, integer7 + 1), 4);
                }
            }
        }
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (!bvt1.canSurvive(bhs, ew5)) {
            bhs.getBlockTicks().scheduleTick(ew5, this, 1);
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        final Block bmv5 = bhu.getBlockState(ew.below()).getBlock();
        if (bmv5 == this) {
            return true;
        }
        if (bmv5 == Blocks.GRASS_BLOCK || bmv5 == Blocks.DIRT || bmv5 == Blocks.COARSE_DIRT || bmv5 == Blocks.PODZOL || bmv5 == Blocks.SAND || bmv5 == Blocks.RED_SAND) {
            final BlockPos ew2 = ew.below();
            for (final Direction fb8 : Direction.Plane.HORIZONTAL) {
                final BlockState bvt2 = bhu.getBlockState(ew2.relative(fb8));
                final FluidState clk10 = bhu.getFluidState(ew2.relative(fb8));
                if (clk10.is(FluidTags.WATER) || bvt2.getBlock() == Blocks.FROSTED_ICE) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(SugarCaneBlock.AGE);
    }
    
    static {
        AGE = BlockStateProperties.AGE_15;
        SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
    }
}
