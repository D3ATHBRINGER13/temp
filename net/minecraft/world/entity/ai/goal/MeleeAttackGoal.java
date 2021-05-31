package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import java.util.EnumSet;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.entity.PathfinderMob;

public class MeleeAttackGoal extends Goal {
    protected final PathfinderMob mob;
    protected int attackTime;
    private final double speedModifier;
    private final boolean trackTarget;
    private Path path;
    private int timeToRecalcPath;
    private double pathedTargetX;
    private double pathedTargetY;
    private double pathedTargetZ;
    protected final int attackInterval = 20;
    private long lastUpdate;
    
    public MeleeAttackGoal(final PathfinderMob aje, final double double2, final boolean boolean3) {
        this.mob = aje;
        this.speedModifier = double2;
        this.trackTarget = boolean3;
        this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE, (Enum)Flag.LOOK));
    }
    
    @Override
    public boolean canUse() {
        final long long2 = this.mob.level.getGameTime();
        if (long2 - this.lastUpdate < 20L) {
            return false;
        }
        this.lastUpdate = long2;
        final LivingEntity aix4 = this.mob.getTarget();
        if (aix4 == null) {
            return false;
        }
        if (!aix4.isAlive()) {
            return false;
        }
        this.path = this.mob.getNavigation().createPath(aix4, 0);
        return this.path != null || this.getAttackReachSqr(aix4) >= this.mob.distanceToSqr(aix4.x, aix4.getBoundingBox().minY, aix4.z);
    }
    
    @Override
    public boolean canContinueToUse() {
        final LivingEntity aix2 = this.mob.getTarget();
        if (aix2 == null) {
            return false;
        }
        if (!aix2.isAlive()) {
            return false;
        }
        if (!this.trackTarget) {
            return !this.mob.getNavigation().isDone();
        }
        return this.mob.isWithinRestriction(new BlockPos(aix2)) && (!(aix2 instanceof Player) || (!aix2.isSpectator() && !((Player)aix2).isCreative()));
    }
    
    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.path, this.speedModifier);
        this.mob.setAggressive(true);
        this.timeToRecalcPath = 0;
    }
    
    @Override
    public void stop() {
        final LivingEntity aix2 = this.mob.getTarget();
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(aix2)) {
            this.mob.setTarget(null);
        }
        this.mob.setAggressive(false);
        this.mob.getNavigation().stop();
    }
    
    @Override
    public void tick() {
        final LivingEntity aix2 = this.mob.getTarget();
        this.mob.getLookControl().setLookAt(aix2, 30.0f, 30.0f);
        final double double3 = this.mob.distanceToSqr(aix2.x, aix2.getBoundingBox().minY, aix2.z);
        --this.timeToRecalcPath;
        if ((this.trackTarget || this.mob.getSensing().canSee(aix2)) && this.timeToRecalcPath <= 0 && ((this.pathedTargetX == 0.0 && this.pathedTargetY == 0.0 && this.pathedTargetZ == 0.0) || aix2.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0 || this.mob.getRandom().nextFloat() < 0.05f)) {
            this.pathedTargetX = aix2.x;
            this.pathedTargetY = aix2.getBoundingBox().minY;
            this.pathedTargetZ = aix2.z;
            this.timeToRecalcPath = 4 + this.mob.getRandom().nextInt(7);
            if (double3 > 1024.0) {
                this.timeToRecalcPath += 10;
            }
            else if (double3 > 256.0) {
                this.timeToRecalcPath += 5;
            }
            if (!this.mob.getNavigation().moveTo(aix2, this.speedModifier)) {
                this.timeToRecalcPath += 15;
            }
        }
        this.attackTime = Math.max(this.attackTime - 1, 0);
        this.checkAndPerformAttack(aix2, double3);
    }
    
    protected void checkAndPerformAttack(final LivingEntity aix, final double double2) {
        final double double3 = this.getAttackReachSqr(aix);
        if (double2 <= double3 && this.attackTime <= 0) {
            this.attackTime = 20;
            this.mob.swing(InteractionHand.MAIN_HAND);
            this.mob.doHurtTarget(aix);
        }
    }
    
    protected double getAttackReachSqr(final LivingEntity aix) {
        return this.mob.getBbWidth() * 2.0f * (this.mob.getBbWidth() * 2.0f) + aix.getBbWidth();
    }
}
