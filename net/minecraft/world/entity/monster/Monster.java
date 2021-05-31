package net.minecraft.world.entity.monster;

import java.util.function.Predicate;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LightLayer;
import java.util.Random;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;

public abstract class Monster extends PathfinderMob implements Enemy {
    protected Monster(final EntityType<? extends Monster> ais, final Level bhr) {
        super(ais, bhr);
        this.xpReward = 5;
    }
    
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }
    
    @Override
    public void aiStep() {
        this.updateSwingTime();
        this.updateNoActionTime();
        super.aiStep();
    }
    
    protected void updateNoActionTime() {
        final float float2 = this.getBrightness();
        if (float2 > 0.5f) {
            this.noActionTime += 2;
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide && this.level.getDifficulty() == Difficulty.PEACEFUL) {
            this.remove();
        }
    }
    
    protected SoundEvent getSwimSound() {
        return SoundEvents.HOSTILE_SWIM;
    }
    
    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.HOSTILE_SPLASH;
    }
    
    public boolean hurt(final DamageSource ahx, final float float2) {
        return !this.isInvulnerableTo(ahx) && super.hurt(ahx, float2);
    }
    
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.HOSTILE_HURT;
    }
    
    protected SoundEvent getDeathSound() {
        return SoundEvents.HOSTILE_DEATH;
    }
    
    protected SoundEvent getFallDamageSound(final int integer) {
        if (integer > 4) {
            return SoundEvents.HOSTILE_BIG_FALL;
        }
        return SoundEvents.HOSTILE_SMALL_FALL;
    }
    
    @Override
    public float getWalkTargetValue(final BlockPos ew, final LevelReader bhu) {
        return 0.5f - bhu.getBrightness(ew);
    }
    
    public static boolean isDarkEnoughToSpawn(final LevelAccessor bhs, final BlockPos ew, final Random random) {
        if (bhs.getBrightness(LightLayer.SKY, ew) > random.nextInt(32)) {
            return false;
        }
        final int integer4 = bhs.getLevel().isThundering() ? bhs.getMaxLocalRawBrightness(ew, 10) : bhs.getMaxLocalRawBrightness(ew);
        return integer4 <= random.nextInt(8);
    }
    
    public static boolean checkMonsterSpawnRules(final EntityType<? extends Monster> ais, final LevelAccessor bhs, final MobSpawnType aja, final BlockPos ew, final Random random) {
        return bhs.getDifficulty() != Difficulty.PEACEFUL && isDarkEnoughToSpawn(bhs, ew, random) && Mob.checkMobSpawnRules(ais, bhs, aja, ew, random);
    }
    
    public static boolean checkAnyLightMonsterSpawnRules(final EntityType<? extends Monster> ais, final LevelAccessor bhs, final MobSpawnType aja, final BlockPos ew, final Random random) {
        return bhs.getDifficulty() != Difficulty.PEACEFUL && Mob.checkMobSpawnRules(ais, bhs, aja, ew, random);
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
    }
    
    protected boolean shouldDropExperience() {
        return true;
    }
    
    public boolean isPreventingPlayerRest(final Player awg) {
        return true;
    }
    
    public ItemStack getProjectile(final ItemStack bcj) {
        if (bcj.getItem() instanceof ProjectileWeaponItem) {
            final Predicate<ItemStack> predicate3 = ((ProjectileWeaponItem)bcj.getItem()).getSupportedHeldProjectiles();
            final ItemStack bcj2 = ProjectileWeaponItem.getHeldProjectile(this, predicate3);
            return bcj2.isEmpty() ? new ItemStack(Items.ARROW) : bcj2;
        }
        return ItemStack.EMPTY;
    }
}
