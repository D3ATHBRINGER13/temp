package net.minecraft.world.entity.ai.control;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;

public class DolphinLookControl extends LookControl {
    private final int maxYRotFromCenter;
    
    public DolphinLookControl(final Mob aiy, final int integer) {
        super(aiy);
        this.maxYRotFromCenter = integer;
    }
    
    @Override
    public void tick() {
        if (this.hasWanted) {
            this.hasWanted = false;
            this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, this.getYRotD() + 20.0f, this.yMaxRotSpeed);
            this.mob.xRot = this.rotateTowards(this.mob.xRot, this.getXRotD() + 10.0f, this.xMaxRotAngle);
        }
        else {
            if (this.mob.getNavigation().isDone()) {
                this.mob.xRot = this.rotateTowards(this.mob.xRot, 0.0f, 5.0f);
            }
            this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, this.mob.yBodyRot, this.yMaxRotSpeed);
        }
        final float float2 = Mth.wrapDegrees(this.mob.yHeadRot - this.mob.yBodyRot);
        if (float2 < -this.maxYRotFromCenter) {
            final Mob mob = this.mob;
            mob.yBodyRot -= 4.0f;
        }
        else if (float2 > this.maxYRotFromCenter) {
            final Mob mob2 = this.mob;
            mob2.yBodyRot += 4.0f;
        }
    }
}
