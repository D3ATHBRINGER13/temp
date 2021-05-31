package net.minecraft.world.level.material;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.properties.Property;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockLayer;

public abstract class WaterFluid extends FlowingFluid {
    @Override
    public Fluid getFlowing() {
        return Fluids.FLOWING_WATER;
    }
    
    @Override
    public Fluid getSource() {
        return Fluids.WATER;
    }
    
    public BlockLayer getRenderLayer() {
        return BlockLayer.TRANSLUCENT;
    }
    
    @Override
    public Item getBucket() {
        return Items.WATER_BUCKET;
    }
    
    public void animateTick(final Level bhr, final BlockPos ew, final FluidState clk, final Random random) {
        if (!clk.isSource() && !clk.<Boolean>getValue((Property<Boolean>)WaterFluid.FALLING)) {
            if (random.nextInt(64) == 0) {
                bhr.playLocalSound(ew.getX() + 0.5, ew.getY() + 0.5, ew.getZ() + 0.5, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, random.nextFloat() * 0.25f + 0.75f, random.nextFloat() + 0.5f, false);
            }
        }
        else if (random.nextInt(10) == 0) {
            bhr.addParticle(ParticleTypes.UNDERWATER, ew.getX() + random.nextFloat(), ew.getY() + random.nextFloat(), ew.getZ() + random.nextFloat(), 0.0, 0.0, 0.0);
        }
    }
    
    @Nullable
    public ParticleOptions getDripParticle() {
        return ParticleTypes.DRIPPING_WATER;
    }
    
    @Override
    protected boolean canConvertToSource() {
        return true;
    }
    
    @Override
    protected void beforeDestroyingBlock(final LevelAccessor bhs, final BlockPos ew, final BlockState bvt) {
        final BlockEntity btw5 = bvt.getBlock().isEntityBlock() ? bhs.getBlockEntity(ew) : null;
        Block.dropResources(bvt, bhs.getLevel(), ew, btw5);
    }
    
    public int getSlopeFindDistance(final LevelReader bhu) {
        return 4;
    }
    
    public BlockState createLegacyBlock(final FluidState clk) {
        return ((AbstractStateHolder<O, BlockState>)Blocks.WATER.defaultBlockState()).<Comparable, Integer>setValue((Property<Comparable>)LiquidBlock.LEVEL, FlowingFluid.getLegacyLevel(clk));
    }
    
    @Override
    public boolean isSame(final Fluid clj) {
        return clj == Fluids.WATER || clj == Fluids.FLOWING_WATER;
    }
    
    public int getDropOff(final LevelReader bhu) {
        return 1;
    }
    
    @Override
    public int getTickDelay(final LevelReader bhu) {
        return 5;
    }
    
    public boolean canBeReplacedWith(final FluidState clk, final BlockGetter bhb, final BlockPos ew, final Fluid clj, final Direction fb) {
        return fb == Direction.DOWN && !clj.is(FluidTags.WATER);
    }
    
    @Override
    protected float getExplosionResistance() {
        return 100.0f;
    }
    
    public static class Source extends WaterFluid {
        @Override
        public int getAmount(final FluidState clk) {
            return 8;
        }
        
        @Override
        public boolean isSource(final FluidState clk) {
            return true;
        }
    }
    
    public static class Flowing extends WaterFluid {
        @Override
        protected void createFluidStateDefinition(final StateDefinition.Builder<Fluid, FluidState> a) {
            super.createFluidStateDefinition(a);
            a.add(Flowing.LEVEL);
        }
        
        @Override
        public int getAmount(final FluidState clk) {
            return clk.<Integer>getValue((Property<Integer>)Flowing.LEVEL);
        }
        
        @Override
        public boolean isSource(final FluidState clk) {
            return false;
        }
    }
}
