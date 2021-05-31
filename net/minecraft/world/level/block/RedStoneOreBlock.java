package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.Direction;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;
import java.util.Random;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class RedStoneOreBlock extends Block {
    public static final BooleanProperty LIT;
    
    public RedStoneOreBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)RedStoneOreBlock.LIT, false));
    }
    
    @Override
    public int getLightEmission(final BlockState bvt) {
        return bvt.<Boolean>getValue((Property<Boolean>)RedStoneOreBlock.LIT) ? super.getLightEmission(bvt) : 0;
    }
    
    @Override
    public void attack(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg) {
        interact(bvt, bhr, ew);
        super.attack(bvt, bhr, ew, awg);
    }
    
    @Override
    public void stepOn(final Level bhr, final BlockPos ew, final Entity aio) {
        interact(bhr.getBlockState(ew), bhr, ew);
        super.stepOn(bhr, ew, aio);
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        interact(bvt, bhr, ew);
        return super.use(bvt, bhr, ew, awg, ahi, csd);
    }
    
    private static void interact(final BlockState bvt, final Level bhr, final BlockPos ew) {
        spawnParticles(bhr, ew);
        if (!bvt.<Boolean>getValue((Property<Boolean>)RedStoneOreBlock.LIT)) {
            bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)RedStoneOreBlock.LIT, true), 3);
        }
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (bvt.<Boolean>getValue((Property<Boolean>)RedStoneOreBlock.LIT)) {
            bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)RedStoneOreBlock.LIT, false), 3);
        }
    }
    
    @Override
    public void spawnAfterBreak(final BlockState bvt, final Level bhr, final BlockPos ew, final ItemStack bcj) {
        super.spawnAfterBreak(bvt, bhr, ew, bcj);
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, bcj) == 0) {
            final int integer6 = 1 + bhr.random.nextInt(5);
            this.popExperience(bhr, ew, integer6);
        }
    }
    
    @Override
    public void animateTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (bvt.<Boolean>getValue((Property<Boolean>)RedStoneOreBlock.LIT)) {
            spawnParticles(bhr, ew);
        }
    }
    
    private static void spawnParticles(final Level bhr, final BlockPos ew) {
        final double double3 = 0.5625;
        final Random random5 = bhr.random;
        for (final Direction fb9 : Direction.values()) {
            final BlockPos ew2 = ew.relative(fb9);
            if (!bhr.getBlockState(ew2).isSolidRender(bhr, ew2)) {
                final Direction.Axis a11 = fb9.getAxis();
                final double double4 = (a11 == Direction.Axis.X) ? (0.5 + 0.5625 * fb9.getStepX()) : random5.nextFloat();
                final double double5 = (a11 == Direction.Axis.Y) ? (0.5 + 0.5625 * fb9.getStepY()) : random5.nextFloat();
                final double double6 = (a11 == Direction.Axis.Z) ? (0.5 + 0.5625 * fb9.getStepZ()) : random5.nextFloat();
                bhr.addParticle(DustParticleOptions.REDSTONE, ew.getX() + double4, ew.getY() + double5, ew.getZ() + double6, 0.0, 0.0, 0.0);
            }
        }
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(RedStoneOreBlock.LIT);
    }
    
    static {
        LIT = RedstoneTorchBlock.LIT;
    }
}
