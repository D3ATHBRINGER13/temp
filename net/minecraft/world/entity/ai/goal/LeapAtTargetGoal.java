package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;
import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class LeapAtTargetGoal extends Goal {
    private final Mob mob;
    private LivingEntity target;
    private final float yd;
    
    public LeapAtTargetGoal(final Mob aiy, final float float2) {
        this.mob = aiy;
        this.yd = float2;
        this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.JUMP, (Enum)Flag.MOVE));
    }
    
    @Override
    public boolean canUse() {
        if (this.mob.isVehicle()) {
            return false;
        }
        this.target = this.mob.getTarget();
        if (this.target == null) {
            return false;
        }
        final double double2 = this.mob.distanceToSqr(this.target);
        return double2 >= 4.0 && double2 <= 16.0 && this.mob.onGround && this.mob.getRandom().nextInt(5) == 0;
    }
    
    @Override
    public boolean canContinueToUse() {
        return !this.mob.onGround;
    }
    
    @Override
    public void start() {
        final Vec3 csi2 = this.mob.getDeltaMovement();
        Vec3 csi3 = new Vec3(this.target.x - this.mob.x, 0.0, this.target.z - this.mob.z);
        if (csi3.lengthSqr() > 1.0E-7) {
            csi3 = csi3.normalize().scale(0.4).add(csi2.scale(0.2));
        }
        this.mob.setDeltaMovement(csi3.x, this.yd, csi3.z);
    }
}
