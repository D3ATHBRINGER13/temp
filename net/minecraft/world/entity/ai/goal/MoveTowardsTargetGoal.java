package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;
import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;

public class MoveTowardsTargetGoal extends Goal {
    private final PathfinderMob mob;
    private LivingEntity target;
    private double wantedX;
    private double wantedY;
    private double wantedZ;
    private final double speedModifier;
    private final float within;
    
    public MoveTowardsTargetGoal(final PathfinderMob aje, final double double2, final float float3) {
        this.mob = aje;
        this.speedModifier = double2;
        this.within = float3;
        this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE));
    }
    
    @Override
    public boolean canUse() {
        this.target = this.mob.getTarget();
        if (this.target == null) {
            return false;
        }
        if (this.target.distanceToSqr(this.mob) > this.within * this.within) {
            return false;
        }
        final Vec3 csi2 = RandomPos.getPosTowards(this.mob, 16, 7, new Vec3(this.target.x, this.target.y, this.target.z));
        if (csi2 == null) {
            return false;
        }
        this.wantedX = csi2.x;
        this.wantedY = csi2.y;
        this.wantedZ = csi2.z;
        return true;
    }
    
    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone() && this.target.isAlive() && this.target.distanceToSqr(this.mob) < this.within * this.within;
    }
    
    @Override
    public void stop() {
        this.target = null;
    }
    
    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
    }
}
