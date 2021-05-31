package net.minecraft.world.entity.monster;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.tags.Tag;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.Difficulty;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

public class MagmaCube extends Slime {
    public MagmaCube(final EntityType<? extends MagmaCube> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224);
    }
    
    public static boolean checkMagmaCubeSpawnRules(final EntityType<MagmaCube> ais, final LevelAccessor bhs, final MobSpawnType aja, final BlockPos ew, final Random random) {
        return bhs.getDifficulty() != Difficulty.PEACEFUL;
    }
    
    @Override
    public boolean checkSpawnObstruction(final LevelReader bhu) {
        return bhu.isUnobstructed(this) && !bhu.containsAnyLiquid(this.getBoundingBox());
    }
    
    @Override
    protected void setSize(final int integer, final boolean boolean2) {
        super.setSize(integer, boolean2);
        this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(integer * 3);
    }
    
    public int getLightColor() {
        return 15728880;
    }
    
    public float getBrightness() {
        return 1.0f;
    }
    
    @Override
    protected ParticleOptions getParticleType() {
        return ParticleTypes.FLAME;
    }
    
    @Override
    protected ResourceLocation getDefaultLootTable() {
        return this.isTiny() ? BuiltInLootTables.EMPTY : this.getType().getDefaultLootTable();
    }
    
    public boolean isOnFire() {
        return false;
    }
    
    @Override
    protected int getJumpDelay() {
        return super.getJumpDelay() * 4;
    }
    
    @Override
    protected void decreaseSquish() {
        this.targetSquish *= 0.9f;
    }
    
    @Override
    protected void jumpFromGround() {
        final Vec3 csi2 = this.getDeltaMovement();
        this.setDeltaMovement(csi2.x, 0.42f + this.getSize() * 0.1f, csi2.z);
        this.hasImpulse = true;
    }
    
    @Override
    protected void jumpInLiquid(final Tag<Fluid> zg) {
        if (zg == FluidTags.LAVA) {
            final Vec3 csi3 = this.getDeltaMovement();
            this.setDeltaMovement(csi3.x, 0.22f + this.getSize() * 0.05f, csi3.z);
            this.hasImpulse = true;
        }
        else {
            super.jumpInLiquid(zg);
        }
    }
    
    @Override
    public void causeFallDamage(final float float1, final float float2) {
    }
    
    @Override
    protected boolean isDealsDamage() {
        return this.isEffectiveAi();
    }
    
    @Override
    protected int getAttackDamage() {
        return super.getAttackDamage() + 2;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        if (this.isTiny()) {
            return SoundEvents.MAGMA_CUBE_HURT_SMALL;
        }
        return SoundEvents.MAGMA_CUBE_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        if (this.isTiny()) {
            return SoundEvents.MAGMA_CUBE_DEATH_SMALL;
        }
        return SoundEvents.MAGMA_CUBE_DEATH;
    }
    
    @Override
    protected SoundEvent getSquishSound() {
        if (this.isTiny()) {
            return SoundEvents.MAGMA_CUBE_SQUISH_SMALL;
        }
        return SoundEvents.MAGMA_CUBE_SQUISH;
    }
    
    @Override
    protected SoundEvent getJumpSound() {
        return SoundEvents.MAGMA_CUBE_JUMP;
    }
}
