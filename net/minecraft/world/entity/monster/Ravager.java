package net.minecraft.world.entity.monster;

import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;
import java.util.List;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Iterator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.util.Mth;
import javax.annotation.Nullable;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import java.util.function.Predicate;
import net.minecraft.world.entity.raid.Raider;

public class Ravager extends Raider {
    private static final Predicate<Entity> NO_RAVAGER_AND_ALIVE;
    private int attackTick;
    private int stunnedTick;
    private int roarTick;
    
    public Ravager(final EntityType<? extends Ravager> ais, final Level bhr) {
        super(ais, bhr);
        this.maxUpStep = 1.0f;
        this.xpReward = 20;
    }
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new RavagerMeleeAttackGoal());
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.4));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0f));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[] { Raider.class }).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }
    
    @Override
    protected void updateControlFlags() {
        final boolean boolean2 = !(this.getControllingPassenger() instanceof Mob) || this.getControllingPassenger().getType().is(EntityTypeTags.RAIDERS);
        final boolean boolean3 = !(this.getVehicle() instanceof Boat);
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, boolean2);
        this.goalSelector.setControlFlag(Goal.Flag.JUMP, boolean2 && boolean3);
        this.goalSelector.setControlFlag(Goal.Flag.LOOK, boolean2);
        this.goalSelector.setControlFlag(Goal.Flag.TARGET, boolean2);
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
        this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(12.0);
        this.getAttribute(SharedMonsterAttributes.ATTACK_KNOCKBACK).setBaseValue(1.5);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putInt("AttackTick", this.attackTick);
        id.putInt("StunTick", this.stunnedTick);
        id.putInt("RoarTick", this.roarTick);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        this.attackTick = id.getInt("AttackTick");
        this.stunnedTick = id.getInt("StunTick");
        this.roarTick = id.getInt("RoarTick");
    }
    
    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.RAVAGER_CELEBRATE;
    }
    
    @Override
    protected PathNavigation createNavigation(final Level bhr) {
        return new RavagerNavigation(this, bhr);
    }
    
    @Override
    public int getMaxHeadYRot() {
        return 45;
    }
    
    public double getRideHeight() {
        return 2.1;
    }
    
    @Override
    public boolean canBeControlledByRider() {
        return !this.isNoAi() && this.getControllingPassenger() instanceof LivingEntity;
    }
    
    @Nullable
    public Entity getControllingPassenger() {
        if (this.getPassengers().isEmpty()) {
            return null;
        }
        return (Entity)this.getPassengers().get(0);
    }
    
    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.isAlive()) {
            return;
        }
        if (this.isImmobile()) {
            this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0);
        }
        else {
            final double double2 = (this.getTarget() != null) ? 0.35 : 0.3;
            final double double3 = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue();
            this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(Mth.lerp(0.1, double3, double2));
        }
        if (this.horizontalCollision && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            boolean boolean2 = false;
            final AABB csc3 = this.getBoundingBox().inflate(0.2);
            for (final BlockPos ew5 : BlockPos.betweenClosed(Mth.floor(csc3.minX), Mth.floor(csc3.minY), Mth.floor(csc3.minZ), Mth.floor(csc3.maxX), Mth.floor(csc3.maxY), Mth.floor(csc3.maxZ))) {
                final BlockState bvt6 = this.level.getBlockState(ew5);
                final Block bmv7 = bvt6.getBlock();
                if (bmv7 instanceof LeavesBlock) {
                    boolean2 = (this.level.destroyBlock(ew5, true) || boolean2);
                }
            }
            if (!boolean2 && this.onGround) {
                this.jumpFromGround();
            }
        }
        if (this.roarTick > 0) {
            --this.roarTick;
            if (this.roarTick == 10) {
                this.roar();
            }
        }
        if (this.attackTick > 0) {
            --this.attackTick;
        }
        if (this.stunnedTick > 0) {
            --this.stunnedTick;
            this.stunEffect();
            if (this.stunnedTick == 0) {
                this.playSound(SoundEvents.RAVAGER_ROAR, 1.0f, 1.0f);
                this.roarTick = 20;
            }
        }
    }
    
    private void stunEffect() {
        if (this.random.nextInt(6) == 0) {
            final double double2 = this.x - this.getBbWidth() * Math.sin((double)(this.yBodyRot * 0.017453292f)) + (this.random.nextDouble() * 0.6 - 0.3);
            final double double3 = this.y + this.getBbHeight() - 0.3;
            final double double4 = this.z + this.getBbWidth() * Math.cos((double)(this.yBodyRot * 0.017453292f)) + (this.random.nextDouble() * 0.6 - 0.3);
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, double2, double3, double4, 0.4980392156862745, 0.5137254901960784, 0.5725490196078431);
        }
    }
    
    protected boolean isImmobile() {
        return super.isImmobile() || this.attackTick > 0 || this.stunnedTick > 0 || this.roarTick > 0;
    }
    
    public boolean canSee(final Entity aio) {
        return this.stunnedTick <= 0 && this.roarTick <= 0 && super.canSee(aio);
    }
    
    protected void blockedByShield(final LivingEntity aix) {
        if (this.roarTick == 0) {
            if (this.random.nextDouble() < 0.5) {
                this.stunnedTick = 40;
                this.playSound(SoundEvents.RAVAGER_STUNNED, 1.0f, 1.0f);
                this.level.broadcastEntityEvent(this, (byte)39);
                aix.push(this);
            }
            else {
                this.strongKnockback(aix);
            }
            aix.hurtMarked = true;
        }
    }
    
    private void roar() {
        if (this.isAlive()) {
            final List<Entity> list2 = this.level.<Entity>getEntitiesOfClass((java.lang.Class<? extends Entity>)LivingEntity.class, this.getBoundingBox().inflate(4.0), (java.util.function.Predicate<? super Entity>)Ravager.NO_RAVAGER_AND_ALIVE);
            for (final Entity aio4 : list2) {
                if (!(aio4 instanceof AbstractIllager)) {
                    aio4.hurt(DamageSource.mobAttack(this), 6.0f);
                }
                this.strongKnockback(aio4);
            }
            final Vec3 csi3 = this.getBoundingBox().getCenter();
            for (int integer4 = 0; integer4 < 40; ++integer4) {
                final double double5 = this.random.nextGaussian() * 0.2;
                final double double6 = this.random.nextGaussian() * 0.2;
                final double double7 = this.random.nextGaussian() * 0.2;
                this.level.addParticle(ParticleTypes.POOF, csi3.x, csi3.y, csi3.z, double5, double6, double7);
            }
        }
    }
    
    private void strongKnockback(final Entity aio) {
        final double double3 = aio.x - this.x;
        final double double4 = aio.z - this.z;
        final double double5 = Math.max(double3 * double3 + double4 * double4, 0.001);
        aio.push(double3 / double5 * 4.0, 0.2, double4 / double5 * 4.0);
    }
    
    @Override
    public void handleEntityEvent(final byte byte1) {
        if (byte1 == 4) {
            this.attackTick = 10;
            this.playSound(SoundEvents.RAVAGER_ATTACK, 1.0f, 1.0f);
        }
        else if (byte1 == 39) {
            this.stunnedTick = 40;
        }
        super.handleEntityEvent(byte1);
    }
    
    public int getAttackTick() {
        return this.attackTick;
    }
    
    public int getStunnedTick() {
        return this.stunnedTick;
    }
    
    public int getRoarTick() {
        return this.roarTick;
    }
    
    @Override
    public boolean doHurtTarget(final Entity aio) {
        this.attackTick = 10;
        this.level.broadcastEntityEvent(this, (byte)4);
        this.playSound(SoundEvents.RAVAGER_ATTACK, 1.0f, 1.0f);
        return super.doHurtTarget(aio);
    }
    
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.RAVAGER_AMBIENT;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.RAVAGER_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.RAVAGER_DEATH;
    }
    
    protected void playStepSound(final BlockPos ew, final BlockState bvt) {
        this.playSound(SoundEvents.RAVAGER_STEP, 0.15f, 1.0f);
    }
    
    @Override
    public boolean checkSpawnObstruction(final LevelReader bhu) {
        return !bhu.containsAnyLiquid(this.getBoundingBox());
    }
    
    @Override
    public void applyRaidBuffs(final int integer, final boolean boolean2) {
    }
    
    @Override
    public boolean canBeLeader() {
        return false;
    }
    
    static {
        NO_RAVAGER_AND_ALIVE = (aio -> aio.isAlive() && !(aio instanceof Ravager));
    }
    
    class RavagerMeleeAttackGoal extends MeleeAttackGoal {
        public RavagerMeleeAttackGoal() {
            super(Ravager.this, 1.0, true);
        }
        
        @Override
        protected double getAttackReachSqr(final LivingEntity aix) {
            final float float3 = Ravager.this.getBbWidth() - 0.1f;
            return float3 * 2.0f * (float3 * 2.0f) + aix.getBbWidth();
        }
    }
    
    static class RavagerNavigation extends GroundPathNavigation {
        public RavagerNavigation(final Mob aiy, final Level bhr) {
            super(aiy, bhr);
        }
        
        @Override
        protected PathFinder createPathFinder(final int integer) {
            this.nodeEvaluator = new RavagerNodeEvaluator();
            return new PathFinder(this.nodeEvaluator, integer);
        }
    }
    
    static class RavagerNodeEvaluator extends WalkNodeEvaluator {
        private RavagerNodeEvaluator() {
        }
        
        @Override
        protected BlockPathTypes evaluateBlockPathType(final BlockGetter bhb, final boolean boolean2, final boolean boolean3, final BlockPos ew, final BlockPathTypes cnn) {
            if (cnn == BlockPathTypes.LEAVES) {
                return BlockPathTypes.OPEN;
            }
            return super.evaluateBlockPathType(bhb, boolean2, boolean3, ew, cnn);
        }
    }
}
