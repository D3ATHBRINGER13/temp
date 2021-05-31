package net.minecraft.world.entity.ai.goal;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;

public class FollowBoatGoal extends Goal {
    private int timeToRecalcPath;
    private final PathfinderMob mob;
    private LivingEntity following;
    private BoatGoals currentGoal;
    
    public FollowBoatGoal(final PathfinderMob aje) {
        this.mob = aje;
    }
    
    @Override
    public boolean canUse() {
        final List<Boat> list2 = this.mob.level.<Boat>getEntitiesOfClass((java.lang.Class<? extends Boat>)Boat.class, this.mob.getBoundingBox().inflate(5.0));
        boolean boolean3 = false;
        for (final Boat axw5 : list2) {
            if (axw5.getControllingPassenger() != null && (Mth.abs(((LivingEntity)axw5.getControllingPassenger()).xxa) > 0.0f || Mth.abs(((LivingEntity)axw5.getControllingPassenger()).zza) > 0.0f)) {
                boolean3 = true;
                break;
            }
        }
        return (this.following != null && (Mth.abs(this.following.xxa) > 0.0f || Mth.abs(this.following.zza) > 0.0f)) || boolean3;
    }
    
    @Override
    public boolean isInterruptable() {
        return true;
    }
    
    @Override
    public boolean canContinueToUse() {
        return this.following != null && this.following.isPassenger() && (Mth.abs(this.following.xxa) > 0.0f || Mth.abs(this.following.zza) > 0.0f);
    }
    
    @Override
    public void start() {
        final List<Boat> list2 = this.mob.level.<Boat>getEntitiesOfClass((java.lang.Class<? extends Boat>)Boat.class, this.mob.getBoundingBox().inflate(5.0));
        for (final Boat axw4 : list2) {
            if (axw4.getControllingPassenger() != null && axw4.getControllingPassenger() instanceof LivingEntity) {
                this.following = (LivingEntity)axw4.getControllingPassenger();
                break;
            }
        }
        this.timeToRecalcPath = 0;
        this.currentGoal = BoatGoals.GO_TO_BOAT;
    }
    
    @Override
    public void stop() {
        this.following = null;
    }
    
    @Override
    public void tick() {
        final boolean boolean2 = Mth.abs(this.following.xxa) > 0.0f || Mth.abs(this.following.zza) > 0.0f;
        final float float3 = (this.currentGoal == BoatGoals.GO_IN_BOAT_DIRECTION) ? (boolean2 ? 0.17999999f : 0.0f) : 0.135f;
        this.mob.moveRelative(float3, new Vec3(this.mob.xxa, this.mob.yya, this.mob.zza));
        this.mob.move(MoverType.SELF, this.mob.getDeltaMovement());
        final int timeToRecalcPath = this.timeToRecalcPath - 1;
        this.timeToRecalcPath = timeToRecalcPath;
        if (timeToRecalcPath > 0) {
            return;
        }
        this.timeToRecalcPath = 10;
        if (this.currentGoal == BoatGoals.GO_TO_BOAT) {
            BlockPos ew4 = new BlockPos(this.following).relative(this.following.getDirection().getOpposite());
            ew4 = ew4.offset(0, -1, 0);
            this.mob.getNavigation().moveTo(ew4.getX(), ew4.getY(), ew4.getZ(), 1.0);
            if (this.mob.distanceTo(this.following) < 4.0f) {
                this.timeToRecalcPath = 0;
                this.currentGoal = BoatGoals.GO_IN_BOAT_DIRECTION;
            }
        }
        else if (this.currentGoal == BoatGoals.GO_IN_BOAT_DIRECTION) {
            final Direction fb4 = this.following.getMotionDirection();
            final BlockPos ew5 = new BlockPos(this.following).relative(fb4, 10);
            this.mob.getNavigation().moveTo(ew5.getX(), ew5.getY() - 1, ew5.getZ(), 1.0);
            if (this.mob.distanceTo(this.following) > 12.0f) {
                this.timeToRecalcPath = 0;
                this.currentGoal = BoatGoals.GO_TO_BOAT;
            }
        }
    }
}
