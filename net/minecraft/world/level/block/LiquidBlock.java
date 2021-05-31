package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import java.util.Collections;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.BlockGetter;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import com.google.common.collect.Lists;
import net.minecraft.world.level.material.FluidState;
import java.util.List;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class LiquidBlock extends Block implements BucketPickup {
    public static final IntegerProperty LEVEL;
    protected final FlowingFluid fluid;
    private final List<FluidState> stateCache;
    
    protected LiquidBlock(final FlowingFluid cli, final Properties c) {
        super(c);
        this.fluid = cli;
        (this.stateCache = (List<FluidState>)Lists.newArrayList()).add(cli.getSource(false));
        for (int integer4 = 1; integer4 < 8; ++integer4) {
            this.stateCache.add(cli.getFlowing(8 - integer4, false));
        }
        this.stateCache.add(cli.getFlowing(8, true));
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Integer>setValue((Property<Comparable>)LiquidBlock.LEVEL, 0));
    }
    
    @Override
    public void randomTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        bhr.getFluidState(ew).randomTick(bhr, ew, random);
    }
    
    @Override
    public boolean propagatesSkylightDown(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return false;
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return !this.fluid.is(FluidTags.LAVA);
    }
    
    @Override
    public FluidState getFluidState(final BlockState bvt) {
        final int integer3 = bvt.<Integer>getValue((Property<Integer>)LiquidBlock.LEVEL);
        return (FluidState)this.stateCache.get(Math.min(integer3, 8));
    }
    
    @Override
    public boolean skipRendering(final BlockState bvt1, final BlockState bvt2, final Direction fb) {
        return bvt2.getFluidState().getType().isSame(this.fluid) || super.canOcclude(bvt1);
    }
    
    @Override
    public RenderShape getRenderShape(final BlockState bvt) {
        return RenderShape.INVISIBLE;
    }
    
    @Override
    public List<ItemStack> getDrops(final BlockState bvt, final LootContext.Builder a) {
        return (List<ItemStack>)Collections.emptyList();
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return Shapes.empty();
    }
    
    @Override
    public int getTickDelay(final LevelReader bhu) {
        return this.fluid.getTickDelay(bhu);
    }
    
    @Override
    public void onPlace(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (this.shouldSpreadLiquid(bhr, ew, bvt1)) {
            bhr.getLiquidTicks().scheduleTick(ew, bvt1.getFluidState().getType(), this.getTickDelay(bhr));
        }
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (bvt1.getFluidState().isSource() || bvt3.getFluidState().isSource()) {
            bhs.getLiquidTicks().scheduleTick(ew5, bvt1.getFluidState().getType(), this.getTickDelay(bhs));
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public void neighborChanged(final BlockState bvt, final Level bhr, final BlockPos ew3, final Block bmv, final BlockPos ew5, final boolean boolean6) {
        if (this.shouldSpreadLiquid(bhr, ew3, bvt)) {
            bhr.getLiquidTicks().scheduleTick(ew3, bvt.getFluidState().getType(), this.getTickDelay(bhr));
        }
    }
    
    public boolean shouldSpreadLiquid(final Level bhr, final BlockPos ew, final BlockState bvt) {
        if (this.fluid.is(FluidTags.LAVA)) {
            boolean boolean5 = false;
            for (final Direction fb9 : Direction.values()) {
                if (fb9 != Direction.DOWN && bhr.getFluidState(ew.relative(fb9)).is(FluidTags.WATER)) {
                    boolean5 = true;
                    break;
                }
            }
            if (boolean5) {
                final FluidState clk6 = bhr.getFluidState(ew);
                if (clk6.isSource()) {
                    bhr.setBlockAndUpdate(ew, Blocks.OBSIDIAN.defaultBlockState());
                    this.fizz(bhr, ew);
                    return false;
                }
                if (clk6.getHeight(bhr, ew) >= 0.44444445f) {
                    bhr.setBlockAndUpdate(ew, Blocks.COBBLESTONE.defaultBlockState());
                    this.fizz(bhr, ew);
                    return false;
                }
            }
        }
        return true;
    }
    
    private void fizz(final LevelAccessor bhs, final BlockPos ew) {
        bhs.levelEvent(1501, ew, 0);
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(LiquidBlock.LEVEL);
    }
    
    @Override
    public Fluid takeLiquid(final LevelAccessor bhs, final BlockPos ew, final BlockState bvt) {
        if (bvt.<Integer>getValue((Property<Integer>)LiquidBlock.LEVEL) == 0) {
            bhs.setBlock(ew, Blocks.AIR.defaultBlockState(), 11);
            return this.fluid;
        }
        return Fluids.EMPTY;
    }
    
    @Override
    public void entityInside(final BlockState bvt, final Level bhr, final BlockPos ew, final Entity aio) {
        if (this.fluid.is(FluidTags.LAVA)) {
            aio.setInLava();
        }
    }
    
    static {
        LEVEL = BlockStateProperties.LEVEL;
    }
}
