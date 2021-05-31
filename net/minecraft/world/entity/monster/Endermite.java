package net.minecraft.world.entity.monster;

import net.minecraft.world.entity.MobType;
import java.util.Random;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

public class Endermite extends Monster {
    private int life;
    private boolean playerSpawned;
    
    public Endermite(final EntityType<? extends Endermite> ais, final Level bhr) {
        super(ais, bhr);
        this.xpReward = 3;
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }
    
    protected float getStandingEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return 0.1f;
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0);
    }
    
    protected boolean makeStepSound() {
        return false;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENDERMITE_AMBIENT;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.ENDERMITE_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENDERMITE_DEATH;
    }
    
    protected void playStepSound(final BlockPos ew, final BlockState bvt) {
        this.playSound(SoundEvents.ENDERMITE_STEP, 0.15f, 1.0f);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        this.life = id.getInt("Lifetime");
        this.playerSpawned = id.getBoolean("PlayerSpawned");
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putInt("Lifetime", this.life);
        id.putBoolean("PlayerSpawned", this.playerSpawned);
    }
    
    @Override
    public void tick() {
        this.yBodyRot = this.yRot;
        super.tick();
    }
    
    public void setYBodyRot(final float float1) {
        super.setYBodyRot(this.yRot = float1);
    }
    
    public double getRidingHeight() {
        return 0.1;
    }
    
    public boolean isPlayerSpawned() {
        return this.playerSpawned;
    }
    
    public void setPlayerSpawned(final boolean boolean1) {
        this.playerSpawned = boolean1;
    }
    
    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level.isClientSide) {
            for (int integer2 = 0; integer2 < 2; ++integer2) {
                this.level.addParticle(ParticleTypes.PORTAL, this.x + (this.random.nextDouble() - 0.5) * this.getBbWidth(), this.y + this.random.nextDouble() * this.getBbHeight(), this.z + (this.random.nextDouble() - 0.5) * this.getBbWidth(), (this.random.nextDouble() - 0.5) * 2.0, -this.random.nextDouble(), (this.random.nextDouble() - 0.5) * 2.0);
            }
        }
        else {
            if (!this.isPersistenceRequired()) {
                ++this.life;
            }
            if (this.life >= 2400) {
                this.remove();
            }
        }
    }
    
    public static boolean checkEndermiteSpawnRules(final EntityType<Endermite> ais, final LevelAccessor bhs, final MobSpawnType aja, final BlockPos ew, final Random random) {
        if (Monster.checkAnyLightMonsterSpawnRules(ais, bhs, aja, ew, random)) {
            final Player awg6 = bhs.getNearestPlayer(ew.getX() + 0.5, ew.getY() + 0.5, ew.getZ() + 0.5, 5.0, true);
            return awg6 == null;
        }
        return false;
    }
    
    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }
}
