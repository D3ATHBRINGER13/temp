package net.minecraft.world.level.block;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import java.util.Random;
import net.minecraft.world.level.BlockAndBiomeGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class MagmaBlock extends Block {
    public MagmaBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public void stepOn(final Level bhr, final BlockPos ew, final Entity aio) {
        if (!aio.fireImmune() && aio instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)aio)) {
            aio.hurt(DamageSource.HOT_FLOOR, 1.0f);
        }
        super.stepOn(bhr, ew, aio);
    }
    
    @Override
    public int getLightColor(final BlockState bvt, final BlockAndBiomeGetter bgz, final BlockPos ew) {
        return 15728880;
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        BubbleColumnBlock.growColumn(bhr, ew.above(), true);
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (fb == Direction.UP && bvt3.getBlock() == Blocks.WATER) {
            bhs.getBlockTicks().scheduleTick(ew5, this, this.getTickDelay(bhs));
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public void randomTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        final BlockPos ew2 = ew.above();
        if (bhr.getFluidState(ew).is(FluidTags.WATER)) {
            bhr.playSound(null, ew, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.6f + (bhr.random.nextFloat() - bhr.random.nextFloat()) * 0.8f);
            if (bhr instanceof ServerLevel) {
                ((ServerLevel)bhr).<SimpleParticleType>sendParticles(ParticleTypes.LARGE_SMOKE, ew2.getX() + 0.5, ew2.getY() + 0.25, ew2.getZ() + 0.5, 8, 0.5, 0.25, 0.5, 0.0);
            }
        }
    }
    
    @Override
    public int getTickDelay(final LevelReader bhu) {
        return 20;
    }
    
    @Override
    public void onPlace(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        bhr.getBlockTicks().scheduleTick(ew, this, this.getTickDelay(bhr));
    }
    
    @Override
    public boolean isValidSpawn(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final EntityType<?> ais) {
        return ais.fireImmune();
    }
    
    @Override
    public boolean hasPostProcess(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return true;
    }
}
