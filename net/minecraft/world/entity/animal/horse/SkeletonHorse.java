package net.minecraft.world.entity.animal.horse;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import javax.annotation.Nullable;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

public class SkeletonHorse extends AbstractHorse {
    private final SkeletonTrapGoal skeletonTrapGoal;
    private boolean isTrap;
    private int trapTime;
    
    public SkeletonHorse(final EntityType<? extends SkeletonHorse> ais, final Level bhr) {
        super(ais, bhr);
        this.skeletonTrapGoal = new SkeletonTrapGoal(this);
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(15.0);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224);
        this.getAttribute(SkeletonHorse.JUMP_STRENGTH).setBaseValue(this.generateRandomJumpStrength());
    }
    
    @Override
    protected void addBehaviourGoals() {
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        if (this.isUnderLiquid(FluidTags.WATER)) {
            return SoundEvents.SKELETON_HORSE_AMBIENT_WATER;
        }
        return SoundEvents.SKELETON_HORSE_AMBIENT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.SKELETON_HORSE_DEATH;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        super.getHurtSound(ahx);
        return SoundEvents.SKELETON_HORSE_HURT;
    }
    
    protected SoundEvent getSwimSound() {
        if (this.onGround) {
            if (!this.isVehicle()) {
                return SoundEvents.SKELETON_HORSE_STEP_WATER;
            }
            ++this.gallopSoundCounter;
            if (this.gallopSoundCounter > 5 && this.gallopSoundCounter % 3 == 0) {
                return SoundEvents.SKELETON_HORSE_GALLOP_WATER;
            }
            if (this.gallopSoundCounter <= 5) {
                return SoundEvents.SKELETON_HORSE_STEP_WATER;
            }
        }
        return SoundEvents.SKELETON_HORSE_SWIM;
    }
    
    protected void playSwimSound(final float float1) {
        if (this.onGround) {
            super.playSwimSound(0.3f);
        }
        else {
            super.playSwimSound(Math.min(0.1f, float1 * 25.0f));
        }
    }
    
    @Override
    protected void playJumpSound() {
        if (this.isInWater()) {
            this.playSound(SoundEvents.SKELETON_HORSE_JUMP_WATER, 0.4f, 1.0f);
        }
        else {
            super.playJumpSound();
        }
    }
    
    public MobType getMobType() {
        return MobType.UNDEAD;
    }
    
    public double getRideHeight() {
        return super.getRideHeight() - 0.1875;
    }
    
    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isTrap() && this.trapTime++ >= 18000) {
            this.remove();
        }
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putBoolean("SkeletonTrap", this.isTrap());
        id.putInt("SkeletonTrapTime", this.trapTime);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        this.setTrap(id.getBoolean("SkeletonTrap"));
        this.trapTime = id.getInt("SkeletonTrapTime");
    }
    
    public boolean rideableUnderWater() {
        return true;
    }
    
    protected float getWaterSlowDown() {
        return 0.96f;
    }
    
    public boolean isTrap() {
        return this.isTrap;
    }
    
    public void setTrap(final boolean boolean1) {
        if (boolean1 == this.isTrap) {
            return;
        }
        this.isTrap = boolean1;
        if (boolean1) {
            this.goalSelector.addGoal(1, this.skeletonTrapGoal);
        }
        else {
            this.goalSelector.removeGoal(this.skeletonTrapGoal);
        }
    }
    
    @Nullable
    @Override
    public AgableMob getBreedOffspring(final AgableMob aim) {
        return EntityType.SKELETON_HORSE.create(this.level);
    }
    
    @Override
    public boolean mobInteract(final Player awg, final InteractionHand ahi) {
        final ItemStack bcj4 = awg.getItemInHand(ahi);
        if (bcj4.getItem() instanceof SpawnEggItem) {
            return super.mobInteract(awg, ahi);
        }
        if (!this.isTamed()) {
            return false;
        }
        if (this.isBaby()) {
            return super.mobInteract(awg, ahi);
        }
        if (awg.isSneaking()) {
            this.openInventory(awg);
            return true;
        }
        if (this.isVehicle()) {
            return super.mobInteract(awg, ahi);
        }
        if (!bcj4.isEmpty()) {
            if (bcj4.getItem() == Items.SADDLE && !this.isSaddled()) {
                this.openInventory(awg);
                return true;
            }
            if (bcj4.interactEnemy(awg, this, ahi)) {
                return true;
            }
        }
        this.doPlayerRide(awg);
        return true;
    }
}
