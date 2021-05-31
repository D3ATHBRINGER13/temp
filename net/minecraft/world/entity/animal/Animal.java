package net.minecraft.world.entity.animal;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import javax.annotation.Nullable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import java.util.Random;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import java.util.UUID;
import net.minecraft.world.entity.AgableMob;

public abstract class Animal extends AgableMob {
    private int inLove;
    private UUID loveCause;
    
    protected Animal(final EntityType<? extends Animal> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    @Override
    protected void customServerAiStep() {
        if (this.getAge() != 0) {
            this.inLove = 0;
        }
        super.customServerAiStep();
    }
    
    @Override
    public void aiStep() {
        super.aiStep();
        if (this.getAge() != 0) {
            this.inLove = 0;
        }
        if (this.inLove > 0) {
            --this.inLove;
            if (this.inLove % 10 == 0) {
                final double double2 = this.random.nextGaussian() * 0.02;
                final double double3 = this.random.nextGaussian() * 0.02;
                final double double4 = this.random.nextGaussian() * 0.02;
                this.level.addParticle(ParticleTypes.HEART, this.x + this.random.nextFloat() * this.getBbWidth() * 2.0f - this.getBbWidth(), this.y + 0.5 + this.random.nextFloat() * this.getBbHeight(), this.z + this.random.nextFloat() * this.getBbWidth() * 2.0f - this.getBbWidth(), double2, double3, double4);
            }
        }
    }
    
    @Override
    public boolean hurt(final DamageSource ahx, final float float2) {
        if (this.isInvulnerableTo(ahx)) {
            return false;
        }
        this.inLove = 0;
        return super.hurt(ahx, float2);
    }
    
    @Override
    public float getWalkTargetValue(final BlockPos ew, final LevelReader bhu) {
        if (bhu.getBlockState(ew.below()).getBlock() == Blocks.GRASS_BLOCK) {
            return 10.0f;
        }
        return bhu.getBrightness(ew) - 0.5f;
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putInt("InLove", this.inLove);
        if (this.loveCause != null) {
            id.putUUID("LoveCause", this.loveCause);
        }
    }
    
    @Override
    public double getRidingHeight() {
        return 0.14;
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        this.inLove = id.getInt("InLove");
        this.loveCause = (id.hasUUID("LoveCause") ? id.getUUID("LoveCause") : null);
    }
    
    public static boolean checkAnimalSpawnRules(final EntityType<? extends Animal> ais, final LevelAccessor bhs, final MobSpawnType aja, final BlockPos ew, final Random random) {
        return bhs.getBlockState(ew.below()).getBlock() == Blocks.GRASS_BLOCK && bhs.getRawBrightness(ew, 0) > 8;
    }
    
    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }
    
    @Override
    public boolean removeWhenFarAway(final double double1) {
        return false;
    }
    
    @Override
    protected int getExperienceReward(final Player awg) {
        return 1 + this.level.random.nextInt(3);
    }
    
    public boolean isFood(final ItemStack bcj) {
        return bcj.getItem() == Items.WHEAT;
    }
    
    @Override
    public boolean mobInteract(final Player awg, final InteractionHand ahi) {
        final ItemStack bcj4 = awg.getItemInHand(ahi);
        if (this.isFood(bcj4)) {
            if (this.getAge() == 0 && this.canFallInLove()) {
                this.usePlayerItem(awg, bcj4);
                this.setInLove(awg);
                return true;
            }
            if (this.isBaby()) {
                this.usePlayerItem(awg, bcj4);
                this.ageUp((int)(-this.getAge() / 20 * 0.1f), true);
                return true;
            }
        }
        return super.mobInteract(awg, ahi);
    }
    
    protected void usePlayerItem(final Player awg, final ItemStack bcj) {
        if (!awg.abilities.instabuild) {
            bcj.shrink(1);
        }
    }
    
    public boolean canFallInLove() {
        return this.inLove <= 0;
    }
    
    public void setInLove(@Nullable final Player awg) {
        this.inLove = 600;
        if (awg != null) {
            this.loveCause = awg.getUUID();
        }
        this.level.broadcastEntityEvent(this, (byte)18);
    }
    
    public void setInLoveTime(final int integer) {
        this.inLove = integer;
    }
    
    @Nullable
    public ServerPlayer getLoveCause() {
        if (this.loveCause == null) {
            return null;
        }
        final Player awg2 = this.level.getPlayerByUUID(this.loveCause);
        if (awg2 instanceof ServerPlayer) {
            return (ServerPlayer)awg2;
        }
        return null;
    }
    
    public boolean isInLove() {
        return this.inLove > 0;
    }
    
    public void resetLove() {
        this.inLove = 0;
    }
    
    public boolean canMate(final Animal ara) {
        return ara != this && ara.getClass() == this.getClass() && this.isInLove() && ara.isInLove();
    }
    
    @Override
    public void handleEntityEvent(final byte byte1) {
        if (byte1 == 18) {
            for (int integer3 = 0; integer3 < 7; ++integer3) {
                final double double4 = this.random.nextGaussian() * 0.02;
                final double double5 = this.random.nextGaussian() * 0.02;
                final double double6 = this.random.nextGaussian() * 0.02;
                this.level.addParticle(ParticleTypes.HEART, this.x + this.random.nextFloat() * this.getBbWidth() * 2.0f - this.getBbWidth(), this.y + 0.5 + this.random.nextFloat() * this.getBbHeight(), this.z + this.random.nextFloat() * this.getBbWidth() * 2.0f - this.getBbWidth(), double4, double5, double6);
            }
        }
        else {
            super.handleEntityEvent(byte1);
        }
    }
}
