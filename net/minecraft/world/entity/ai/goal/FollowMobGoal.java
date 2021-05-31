package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.entity.Entity;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import java.util.function.Predicate;
import net.minecraft.world.entity.Mob;

public class FollowMobGoal extends Goal {
    private final Mob mob;
    private final Predicate<Mob> followPredicate;
    private Mob followingMob;
    private final double speedModifier;
    private final PathNavigation navigation;
    private int timeToRecalcPath;
    private final float stopDistance;
    private float oldWaterCost;
    private final float areaSize;
    
    public FollowMobGoal(final Mob aiy, final double double2, final float float3, final float float4) {
        this.mob = aiy;
        this.followPredicate = (Predicate<Mob>)(aiy2 -> aiy2 != null && aiy.getClass() != aiy2.getClass());
        this.speedModifier = double2;
        this.navigation = aiy.getNavigation();
        this.stopDistance = float3;
        this.areaSize = float4;
        this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE, (Enum)Flag.LOOK));
        if (!(aiy.getNavigation() instanceof GroundPathNavigation) && !(aiy.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowMobGoal");
        }
    }
    
    @Override
    public boolean canUse() {
        final List<Mob> list2 = this.mob.level.<Mob>getEntitiesOfClass((java.lang.Class<? extends Mob>)Mob.class, this.mob.getBoundingBox().inflate(this.areaSize), (java.util.function.Predicate<? super Mob>)this.followPredicate);
        if (!list2.isEmpty()) {
            for (final Mob aiy4 : list2) {
                if (aiy4.isInvisible()) {
                    continue;
                }
                this.followingMob = aiy4;
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean canContinueToUse() {
        return this.followingMob != null && !this.navigation.isDone() && this.mob.distanceToSqr(this.followingMob) > this.stopDistance * this.stopDistance;
    }
    
    @Override
    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.mob.getPathfindingMalus(BlockPathTypes.WATER);
        this.mob.setPathfindingMalus(BlockPathTypes.WATER, 0.0f);
    }
    
    @Override
    public void stop() {
        this.followingMob = null;
        this.navigation.stop();
        this.mob.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
    }
    
    @Override
    public void tick() {
        if (this.followingMob == null || this.mob.isLeashed()) {
            return;
        }
        this.mob.getLookControl().setLookAt(this.followingMob, 10.0f, (float)this.mob.getMaxHeadXRot());
        if (--this.timeToRecalcPath > 0) {
            return;
        }
        this.timeToRecalcPath = 10;
        final double double2 = this.mob.x - this.followingMob.x;
        final double double3 = this.mob.y - this.followingMob.y;
        final double double4 = this.mob.z - this.followingMob.z;
        final double double5 = double2 * double2 + double3 * double3 + double4 * double4;
        if (double5 <= this.stopDistance * this.stopDistance) {
            this.navigation.stop();
            final LookControl ami10 = this.followingMob.getLookControl();
            if (double5 <= this.stopDistance || (ami10.getWantedX() == this.mob.x && ami10.getWantedY() == this.mob.y && ami10.getWantedZ() == this.mob.z)) {
                final double double6 = this.followingMob.x - this.mob.x;
                final double double7 = this.followingMob.z - this.mob.z;
                this.navigation.moveTo(this.mob.x - double6, this.mob.y, this.mob.z - double7, this.speedModifier);
            }
            return;
        }
        this.navigation.moveTo(this.followingMob, this.speedModifier);
    }
}
