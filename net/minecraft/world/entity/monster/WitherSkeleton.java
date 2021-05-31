package net.minecraft.world.entity.monster;

import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

public class WitherSkeleton extends AbstractSkeleton {
    public WitherSkeleton(final EntityType<? extends WitherSkeleton> ais, final Level bhr) {
        super(ais, bhr);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0f);
    }
    
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WITHER_SKELETON_AMBIENT;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.WITHER_SKELETON_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITHER_SKELETON_DEATH;
    }
    
    @Override
    SoundEvent getStepSound() {
        return SoundEvents.WITHER_SKELETON_STEP;
    }
    
    protected void dropCustomDeathLoot(final DamageSource ahx, final int integer, final boolean boolean3) {
        super.dropCustomDeathLoot(ahx, integer, boolean3);
        final Entity aio5 = ahx.getEntity();
        if (aio5 instanceof Creeper) {
            final Creeper aue6 = (Creeper)aio5;
            if (aue6.canDropMobsSkull()) {
                aue6.increaseDroppedSkulls();
                this.spawnAtLocation(Items.WITHER_SKELETON_SKULL);
            }
        }
    }
    
    @Override
    protected void populateDefaultEquipmentSlots(final DifficultyInstance ahh) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
    }
    
    protected void populateDefaultEquipmentEnchantments(final DifficultyInstance ahh) {
    }
    
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(final LevelAccessor bhs, final DifficultyInstance ahh, final MobSpawnType aja, @Nullable final SpawnGroupData ajj, @Nullable final CompoundTag id) {
        final SpawnGroupData ajj2 = super.finalizeSpawn(bhs, ahh, aja, ajj, id);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
        this.reassessWeaponGoal();
        return ajj2;
    }
    
    @Override
    protected float getStandingEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return 2.1f;
    }
    
    public boolean doHurtTarget(final Entity aio) {
        if (!super.doHurtTarget(aio)) {
            return false;
        }
        if (aio instanceof LivingEntity) {
            ((LivingEntity)aio).addEffect(new MobEffectInstance(MobEffects.WITHER, 200));
        }
        return true;
    }
    
    @Override
    protected AbstractArrow getArrow(final ItemStack bcj, final float float2) {
        final AbstractArrow awk4 = super.getArrow(bcj, float2);
        awk4.setSecondsOnFire(100);
        return awk4;
    }
    
    public boolean canBeAffected(final MobEffectInstance aii) {
        return aii.getEffect() != MobEffects.WITHER && super.canBeAffected(aii);
    }
}
