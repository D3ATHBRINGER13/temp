package net.minecraft.world.entity.monster;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

public class Husk extends Zombie {
    public Husk(final EntityType<? extends Husk> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    public static boolean checkHuskSpawnRules(final EntityType<Husk> ais, final LevelAccessor bhs, final MobSpawnType aja, final BlockPos ew, final Random random) {
        return Monster.checkMonsterSpawnRules(ais, bhs, aja, ew, random) && (aja == MobSpawnType.SPAWNER || bhs.canSeeSky(ew));
    }
    
    @Override
    protected boolean isSunSensitive() {
        return false;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.HUSK_AMBIENT;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.HUSK_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.HUSK_DEATH;
    }
    
    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.HUSK_STEP;
    }
    
    @Override
    public boolean doHurtTarget(final Entity aio) {
        final boolean boolean3 = super.doHurtTarget(aio);
        if (boolean3 && this.getMainHandItem().isEmpty() && aio instanceof LivingEntity) {
            final float float4 = this.level.getCurrentDifficultyAt(new BlockPos(this)).getEffectiveDifficulty();
            ((LivingEntity)aio).addEffect(new MobEffectInstance(MobEffects.HUNGER, 140 * (int)float4));
        }
        return boolean3;
    }
    
    @Override
    protected boolean convertsInWater() {
        return true;
    }
    
    @Override
    protected void doUnderWaterConversion() {
        this.convertTo(EntityType.ZOMBIE);
        this.level.levelEvent(null, 1041, new BlockPos(this), 0);
    }
    
    @Override
    protected ItemStack getSkull() {
        return ItemStack.EMPTY;
    }
}
