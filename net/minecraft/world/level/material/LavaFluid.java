package net.minecraft.world.level.material;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.LevelAccessor;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.GameRules;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.BlockGetter;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockLayer;

public abstract class LavaFluid extends FlowingFluid {
    @Override
    public Fluid getFlowing() {
        return Fluids.FLOWING_LAVA;
    }
    
    @Override
    public Fluid getSource() {
        return Fluids.LAVA;
    }
    
    public BlockLayer getRenderLayer() {
        return BlockLayer.SOLID;
    }
    
    @Override
    public Item getBucket() {
        return Items.LAVA_BUCKET;
    }
    
    public void animateTick(final Level bhr, final BlockPos ew, final FluidState clk, final Random random) {
        final BlockPos ew2 = ew.above();
        if (bhr.getBlockState(ew2).isAir() && !bhr.getBlockState(ew2).isSolidRender(bhr, ew2)) {
            if (random.nextInt(100) == 0) {
                final double double7 = ew.getX() + random.nextFloat();
                final double double8 = ew.getY() + 1;
                final double double9 = ew.getZ() + random.nextFloat();
                bhr.addParticle(ParticleTypes.LAVA, double7, double8, double9, 0.0, 0.0, 0.0);
                bhr.playLocalSound(double7, double8, double9, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.2f + random.nextFloat() * 0.2f, 0.9f + random.nextFloat() * 0.15f, false);
            }
            if (random.nextInt(200) == 0) {
                bhr.playLocalSound(ew.getX(), ew.getY(), ew.getZ(), SoundEvents.LAVA_AMBIENT, SoundSource.BLOCKS, 0.2f + random.nextFloat() * 0.2f, 0.9f + random.nextFloat() * 0.15f, false);
            }
        }
    }
    
    public void randomTick(final Level bhr, final BlockPos ew, final FluidState clk, final Random random) {
        if (!bhr.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            return;
        }
        final int integer6 = random.nextInt(3);
        if (integer6 > 0) {
            BlockPos ew2 = ew;
            for (int integer7 = 0; integer7 < integer6; ++integer7) {
                ew2 = ew2.offset(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
                if (!bhr.isLoaded(ew2)) {
                    return;
                }
                final BlockState bvt9 = bhr.getBlockState(ew2);
                if (bvt9.isAir()) {
                    if (this.hasFlammableNeighbours(bhr, ew2)) {
                        bhr.setBlockAndUpdate(ew2, Blocks.FIRE.defaultBlockState());
                        return;
                    }
                }
                else if (bvt9.getMaterial().blocksMotion()) {
                    return;
                }
            }
        }
        else {
            for (int integer8 = 0; integer8 < 3; ++integer8) {
                final BlockPos ew3 = ew.offset(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
                if (!bhr.isLoaded(ew3)) {
                    return;
                }
                if (bhr.isEmptyBlock(ew3.above()) && this.isFlammable(bhr, ew3)) {
                    bhr.setBlockAndUpdate(ew3.above(), Blocks.FIRE.defaultBlockState());
                }
            }
        }
    }
    
    private boolean hasFlammableNeighbours(final LevelReader bhu, final BlockPos ew) {
        for (final Direction fb7 : Direction.values()) {
            if (this.isFlammable(bhu, ew.relative(fb7))) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isFlammable(final LevelReader bhu, final BlockPos ew) {
        return (ew.getY() < 0 || ew.getY() >= 256 || bhu.hasChunkAt(ew)) && bhu.getBlockState(ew).getMaterial().isFlammable();
    }
    
    @Nullable
    public ParticleOptions getDripParticle() {
        return ParticleTypes.DRIPPING_LAVA;
    }
    
    @Override
    protected void beforeDestroyingBlock(final LevelAccessor bhs, final BlockPos ew, final BlockState bvt) {
        this.fizz(bhs, ew);
    }
    
    public int getSlopeFindDistance(final LevelReader bhu) {
        return bhu.getDimension().isUltraWarm() ? 4 : 2;
    }
    
    public BlockState createLegacyBlock(final FluidState clk) {
        return ((AbstractStateHolder<O, BlockState>)Blocks.LAVA.defaultBlockState()).<Comparable, Integer>setValue((Property<Comparable>)LiquidBlock.LEVEL, FlowingFluid.getLegacyLevel(clk));
    }
    
    @Override
    public boolean isSame(final Fluid clj) {
        return clj == Fluids.LAVA || clj == Fluids.FLOWING_LAVA;
    }
    
    public int getDropOff(final LevelReader bhu) {
        return bhu.getDimension().isUltraWarm() ? 1 : 2;
    }
    
    public boolean canBeReplacedWith(final FluidState clk, final BlockGetter bhb, final BlockPos ew, final Fluid clj, final Direction fb) {
        return clk.getHeight(bhb, ew) >= 0.44444445f && clj.is(FluidTags.WATER);
    }
    
    @Override
    public int getTickDelay(final LevelReader bhu) {
        return bhu.getDimension().isHasCeiling() ? 10 : 30;
    }
    
    public int getSpreadDelay(final Level bhr, final BlockPos ew, final FluidState clk3, final FluidState clk4) {
        int integer6 = this.getTickDelay(bhr);
        if (!clk3.isEmpty() && !clk4.isEmpty() && !clk3.<Boolean>getValue((Property<Boolean>)LavaFluid.FALLING) && !clk4.<Boolean>getValue((Property<Boolean>)LavaFluid.FALLING) && clk4.getHeight(bhr, ew) > clk3.getHeight(bhr, ew) && bhr.getRandom().nextInt(4) != 0) {
            integer6 *= 4;
        }
        return integer6;
    }
    
    private void fizz(final LevelAccessor bhs, final BlockPos ew) {
        bhs.levelEvent(1501, ew, 0);
    }
    
    @Override
    protected boolean canConvertToSource() {
        return false;
    }
    
    @Override
    protected void spreadTo(final LevelAccessor bhs, final BlockPos ew, final BlockState bvt, final Direction fb, final FluidState clk) {
        if (fb == Direction.DOWN) {
            final FluidState clk2 = bhs.getFluidState(ew);
            if (this.is(FluidTags.LAVA) && clk2.is(FluidTags.WATER)) {
                if (bvt.getBlock() instanceof LiquidBlock) {
                    bhs.setBlock(ew, Blocks.STONE.defaultBlockState(), 3);
                }
                this.fizz(bhs, ew);
                return;
            }
        }
        super.spreadTo(bhs, ew, bvt, fb, clk);
    }
    
    @Override
    protected boolean isRandomlyTicking() {
        return true;
    }
    
    @Override
    protected float getExplosionResistance() {
        return 100.0f;
    }
    
    public static class Source extends LavaFluid {
        @Override
        public int getAmount(final FluidState clk) {
            return 8;
        }
        
        @Override
        public boolean isSource(final FluidState clk) {
            return true;
        }
    }
    
    public static class Flowing extends LavaFluid {
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
