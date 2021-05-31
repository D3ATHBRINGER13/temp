package net.minecraft.world.entity.monster;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

public class Stray extends AbstractSkeleton {
    public Stray(final EntityType<? extends Stray> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    public static boolean checkStraySpawnRules(final EntityType<Stray> ais, final LevelAccessor bhs, final MobSpawnType aja, final BlockPos ew, final Random random) {
        return Monster.checkMonsterSpawnRules(ais, bhs, aja, ew, random) && (aja == MobSpawnType.SPAWNER || bhs.canSeeSky(ew));
    }
    
    protected SoundEvent getAmbientSound() {
        return SoundEvents.STRAY_AMBIENT;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.STRAY_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.STRAY_DEATH;
    }
    
    @Override
    SoundEvent getStepSound() {
        return SoundEvents.STRAY_STEP;
    }
    
    @Override
    protected AbstractArrow getArrow(final ItemStack bcj, final float float2) {
        final AbstractArrow awk4 = super.getArrow(bcj, float2);
        if (awk4 instanceof Arrow) {
            ((Arrow)awk4).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600));
        }
        return awk4;
    }
}
