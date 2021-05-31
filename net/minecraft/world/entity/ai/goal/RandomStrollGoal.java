package net.minecraft.world.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;
import java.util.EnumSet;
import net.minecraft.world.entity.PathfinderMob;

public class RandomStrollGoal extends Goal {
    protected final PathfinderMob mob;
    protected double wantedX;
    protected double wantedY;
    protected double wantedZ;
    protected final double speedModifier;
    protected int interval;
    protected boolean forceTrigger;
    
    public RandomStrollGoal(final PathfinderMob aje, final double double2) {
        this(aje, double2, 120);
    }
    
    public RandomStrollGoal(final PathfinderMob aje, final double double2, final int integer) {
        this.mob = aje;
        this.speedModifier = double2;
        this.interval = integer;
        this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE));
    }
    
    @Override
    public boolean canUse() {
        if (this.mob.isVehicle()) {
            return false;
        }
        if (!this.forceTrigger) {
            if (this.mob.getNoActionTime() >= 100) {
                return false;
            }
            if (this.mob.getRandom().nextInt(this.interval) != 0) {
                return false;
            }
        }
        final Vec3 csi2 = this.getPosition();
        if (csi2 == null) {
            return false;
        }
        this.wantedX = csi2.x;
        this.wantedY = csi2.y;
        this.wantedZ = csi2.z;
        this.forceTrigger = false;
        return true;
    }
    
    @Nullable
    protected Vec3 getPosition() {
        return RandomPos.getPos(this.mob, 10, 7);
    }
    
    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone();
    }
    
    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
    }
    
    public void trigger() {
        this.forceTrigger = true;
    }
    
    public void setInterval(final int integer) {
        this.interval = integer;
    }
}
