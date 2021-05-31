package net.minecraft.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;
import java.util.EnumSet;
import net.minecraft.world.entity.PathfinderMob;

public class MoveTowardsRestrictionGoal extends Goal {
    private final PathfinderMob mob;
    private double wantedX;
    private double wantedY;
    private double wantedZ;
    private final double speedModifier;
    
    public MoveTowardsRestrictionGoal(final PathfinderMob aje, final double double2) {
        this.mob = aje;
        this.speedModifier = double2;
        this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE));
    }
    
    @Override
    public boolean canUse() {
        if (this.mob.isWithinRestriction()) {
            return false;
        }
        final BlockPos ew2 = this.mob.getRestrictCenter();
        final Vec3 csi3 = RandomPos.getPosTowards(this.mob, 16, 7, new Vec3(ew2.getX(), ew2.getY(), ew2.getZ()));
        if (csi3 == null) {
            return false;
        }
        this.wantedX = csi3.x;
        this.wantedY = csi3.y;
        this.wantedZ = csi3.z;
        return true;
    }
    
    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone();
    }
    
    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
    }
}
