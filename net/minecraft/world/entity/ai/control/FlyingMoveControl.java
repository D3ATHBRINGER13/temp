package net.minecraft.world.entity.ai.control;

import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;

public class FlyingMoveControl extends MoveControl {
    public FlyingMoveControl(final Mob aiy) {
        super(aiy);
    }
    
    @Override
    public void tick() {
        if (this.operation == Operation.MOVE_TO) {
            this.operation = Operation.WAIT;
            this.mob.setNoGravity(true);
            final double double2 = this.wantedX - this.mob.x;
            final double double3 = this.wantedY - this.mob.y;
            final double double4 = this.wantedZ - this.mob.z;
            final double double5 = double2 * double2 + double3 * double3 + double4 * double4;
            if (double5 < 2.500000277905201E-7) {
                this.mob.setYya(0.0f);
                this.mob.setZza(0.0f);
                return;
            }
            final float float10 = (float)(Mth.atan2(double4, double2) * 57.2957763671875) - 90.0f;
            this.mob.yRot = this.rotlerp(this.mob.yRot, float10, 10.0f);
            float float11;
            if (this.mob.onGround) {
                float11 = (float)(this.speedModifier * this.mob.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
            }
            else {
                float11 = (float)(this.speedModifier * this.mob.getAttribute(SharedMonsterAttributes.FLYING_SPEED).getValue());
            }
            this.mob.setSpeed(float11);
            final double double6 = Mth.sqrt(double2 * double2 + double4 * double4);
            final float float12 = (float)(-(Mth.atan2(double3, double6) * 57.2957763671875));
            this.mob.xRot = this.rotlerp(this.mob.xRot, float12, 10.0f);
            this.mob.setYya((double3 > 0.0) ? float11 : (-float11));
        }
        else {
            this.mob.setNoGravity(false);
            this.mob.setYya(0.0f);
            this.mob.setZza(0.0f);
        }
    }
}
