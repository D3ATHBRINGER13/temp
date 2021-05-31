package net.minecraft.world.entity.ai.control;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Mob;

public class LookControl {
    protected final Mob mob;
    protected float yMaxRotSpeed;
    protected float xMaxRotAngle;
    protected boolean hasWanted;
    protected double wantedX;
    protected double wantedY;
    protected double wantedZ;
    
    public LookControl(final Mob aiy) {
        this.mob = aiy;
    }
    
    public void setLookAt(final Vec3 csi) {
        this.setLookAt(csi.x, csi.y, csi.z);
    }
    
    public void setLookAt(final Entity aio, final float float2, final float float3) {
        this.setLookAt(aio.x, getWantedY(aio), aio.z, float2, float3);
    }
    
    public void setLookAt(final double double1, final double double2, final double double3) {
        this.setLookAt(double1, double2, double3, (float)this.mob.getHeadRotSpeed(), (float)this.mob.getMaxHeadXRot());
    }
    
    public void setLookAt(final double double1, final double double2, final double double3, final float float4, final float float5) {
        this.wantedX = double1;
        this.wantedY = double2;
        this.wantedZ = double3;
        this.yMaxRotSpeed = float4;
        this.xMaxRotAngle = float5;
        this.hasWanted = true;
    }
    
    public void tick() {
        if (this.resetXRotOnTick()) {
            this.mob.xRot = 0.0f;
        }
        if (this.hasWanted) {
            this.hasWanted = false;
            this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, this.getYRotD(), this.yMaxRotSpeed);
            this.mob.xRot = this.rotateTowards(this.mob.xRot, this.getXRotD(), this.xMaxRotAngle);
        }
        else {
            this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, this.mob.yBodyRot, 10.0f);
        }
        if (!this.mob.getNavigation().isDone()) {
            this.mob.yHeadRot = Mth.rotateIfNecessary(this.mob.yHeadRot, this.mob.yBodyRot, (float)this.mob.getMaxHeadYRot());
        }
    }
    
    protected boolean resetXRotOnTick() {
        return true;
    }
    
    public boolean isHasWanted() {
        return this.hasWanted;
    }
    
    public double getWantedX() {
        return this.wantedX;
    }
    
    public double getWantedY() {
        return this.wantedY;
    }
    
    public double getWantedZ() {
        return this.wantedZ;
    }
    
    protected float getXRotD() {
        final double double2 = this.wantedX - this.mob.x;
        final double double3 = this.wantedY - (this.mob.y + this.mob.getEyeHeight());
        final double double4 = this.wantedZ - this.mob.z;
        final double double5 = Mth.sqrt(double2 * double2 + double4 * double4);
        return (float)(-(Mth.atan2(double3, double5) * 57.2957763671875));
    }
    
    protected float getYRotD() {
        final double double2 = this.wantedX - this.mob.x;
        final double double3 = this.wantedZ - this.mob.z;
        return (float)(Mth.atan2(double3, double2) * 57.2957763671875) - 90.0f;
    }
    
    protected float rotateTowards(final float float1, final float float2, final float float3) {
        final float float4 = Mth.degreesDifference(float1, float2);
        final float float5 = Mth.clamp(float4, -float3, float3);
        return float1 + float5;
    }
    
    private static double getWantedY(final Entity aio) {
        if (aio instanceof LivingEntity) {
            return aio.y + aio.getEyeHeight();
        }
        return (aio.getBoundingBox().minY + aio.getBoundingBox().maxY) / 2.0;
    }
}
