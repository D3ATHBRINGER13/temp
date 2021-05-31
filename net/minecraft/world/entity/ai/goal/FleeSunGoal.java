package net.minecraft.world.entity.ai.goal;

import javax.annotation.Nullable;
import java.util.Random;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.core.BlockPos;
import java.util.EnumSet;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.PathfinderMob;

public class FleeSunGoal extends Goal {
    protected final PathfinderMob mob;
    private double wantedX;
    private double wantedY;
    private double wantedZ;
    private final double speedModifier;
    private final Level level;
    
    public FleeSunGoal(final PathfinderMob aje, final double double2) {
        this.mob = aje;
        this.speedModifier = double2;
        this.level = aje.level;
        this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE));
    }
    
    @Override
    public boolean canUse() {
        return this.mob.getTarget() == null && this.level.isDay() && this.mob.isOnFire() && this.level.canSeeSky(new BlockPos(this.mob.x, this.mob.getBoundingBox().minY, this.mob.z)) && this.mob.getItemBySlot(EquipmentSlot.HEAD).isEmpty() && this.setWantedPos();
    }
    
    protected boolean setWantedPos() {
        final Vec3 csi2 = this.getHidePos();
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
        return !this.mob.getNavigation().isDone();
    }
    
    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
    }
    
    @Nullable
    protected Vec3 getHidePos() {
        final Random random2 = this.mob.getRandom();
        final BlockPos ew3 = new BlockPos(this.mob.x, this.mob.getBoundingBox().minY, this.mob.z);
        for (int integer4 = 0; integer4 < 10; ++integer4) {
            final BlockPos ew4 = ew3.offset(random2.nextInt(20) - 10, random2.nextInt(6) - 3, random2.nextInt(20) - 10);
            if (!this.level.canSeeSky(ew4) && this.mob.getWalkTargetValue(ew4) < 0.0f) {
                return new Vec3(ew4.getX(), ew4.getY(), ew4.getZ());
            }
        }
        return null;
    }
}
