package net.minecraft.client;

import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.core.Direction;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Mth;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;

public class Camera {
    private boolean initialized;
    private BlockGetter level;
    private Entity entity;
    private Vec3 position;
    private final BlockPos.MutableBlockPos blockPosition;
    private Vec3 forwards;
    private Vec3 up;
    private Vec3 left;
    private float xRot;
    private float yRot;
    private boolean detached;
    private boolean mirror;
    private float eyeHeight;
    private float eyeHeightOld;
    
    public Camera() {
        this.position = Vec3.ZERO;
        this.blockPosition = new BlockPos.MutableBlockPos();
    }
    
    public void setup(final BlockGetter bhb, final Entity aio, final boolean boolean3, final boolean boolean4, final float float5) {
        this.initialized = true;
        this.level = bhb;
        this.entity = aio;
        this.detached = boolean3;
        this.mirror = boolean4;
        this.setRotation(aio.getViewYRot(float5), aio.getViewXRot(float5));
        this.setPosition(Mth.lerp(float5, aio.xo, aio.x), Mth.lerp(float5, aio.yo, aio.y) + Mth.lerp(float5, this.eyeHeightOld, this.eyeHeight), Mth.lerp(float5, aio.zo, aio.z));
        if (boolean3) {
            if (boolean4) {
                this.yRot += 180.0f;
                this.xRot += -this.xRot * 2.0f;
                this.recalculateViewVector();
            }
            this.move(-this.getMaxZoom(4.0), 0.0, 0.0);
        }
        else if (aio instanceof LivingEntity && ((LivingEntity)aio).isSleeping()) {
            final Direction fb7 = ((LivingEntity)aio).getBedOrientation();
            this.setRotation((fb7 != null) ? (fb7.toYRot() - 180.0f) : 0.0f, 0.0f);
            this.move(0.0, 0.3, 0.0);
        }
        GlStateManager.rotatef(this.xRot, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(this.yRot + 180.0f, 0.0f, 1.0f, 0.0f);
    }
    
    public void tick() {
        if (this.entity != null) {
            this.eyeHeightOld = this.eyeHeight;
            this.eyeHeight += (this.entity.getEyeHeight() - this.eyeHeight) * 0.5f;
        }
    }
    
    private double getMaxZoom(double double1) {
        for (int integer4 = 0; integer4 < 8; ++integer4) {
            float float5 = (float)((integer4 & 0x1) * 2 - 1);
            float float6 = (float)((integer4 >> 1 & 0x1) * 2 - 1);
            float float7 = (float)((integer4 >> 2 & 0x1) * 2 - 1);
            float5 *= 0.1f;
            float6 *= 0.1f;
            float7 *= 0.1f;
            final Vec3 csi8 = this.position.add(float5, float6, float7);
            final Vec3 csi9 = new Vec3(this.position.x - this.forwards.x * double1 + float5 + float7, this.position.y - this.forwards.y * double1 + float6, this.position.z - this.forwards.z * double1 + float7);
            final HitResult csf10 = this.level.clip(new ClipContext(csi8, csi9, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.entity));
            if (csf10.getType() != HitResult.Type.MISS) {
                final double double2 = csf10.getLocation().distanceTo(this.position);
                if (double2 < double1) {
                    double1 = double2;
                }
            }
        }
        return double1;
    }
    
    protected void move(final double double1, final double double2, final double double3) {
        final double double4 = this.forwards.x * double1 + this.up.x * double2 + this.left.x * double3;
        final double double5 = this.forwards.y * double1 + this.up.y * double2 + this.left.y * double3;
        final double double6 = this.forwards.z * double1 + this.up.z * double2 + this.left.z * double3;
        this.setPosition(new Vec3(this.position.x + double4, this.position.y + double5, this.position.z + double6));
    }
    
    protected void recalculateViewVector() {
        final float float2 = Mth.cos((this.yRot + 90.0f) * 0.017453292f);
        final float float3 = Mth.sin((this.yRot + 90.0f) * 0.017453292f);
        final float float4 = Mth.cos(-this.xRot * 0.017453292f);
        final float float5 = Mth.sin(-this.xRot * 0.017453292f);
        final float float6 = Mth.cos((-this.xRot + 90.0f) * 0.017453292f);
        final float float7 = Mth.sin((-this.xRot + 90.0f) * 0.017453292f);
        this.forwards = new Vec3(float2 * float4, float5, float3 * float4);
        this.up = new Vec3(float2 * float6, float7, float3 * float6);
        this.left = this.forwards.cross(this.up).scale(-1.0);
    }
    
    protected void setRotation(final float float1, final float float2) {
        this.xRot = float2;
        this.yRot = float1;
        this.recalculateViewVector();
    }
    
    protected void setPosition(final double double1, final double double2, final double double3) {
        this.setPosition(new Vec3(double1, double2, double3));
    }
    
    protected void setPosition(final Vec3 csi) {
        this.position = csi;
        this.blockPosition.set(csi.x, csi.y, csi.z);
    }
    
    public Vec3 getPosition() {
        return this.position;
    }
    
    public BlockPos getBlockPosition() {
        return this.blockPosition;
    }
    
    public float getXRot() {
        return this.xRot;
    }
    
    public float getYRot() {
        return this.yRot;
    }
    
    public Entity getEntity() {
        return this.entity;
    }
    
    public boolean isInitialized() {
        return this.initialized;
    }
    
    public boolean isDetached() {
        return this.detached;
    }
    
    public FluidState getFluidInCamera() {
        if (!this.initialized) {
            return Fluids.EMPTY.defaultFluidState();
        }
        final FluidState clk2 = this.level.getFluidState(this.blockPosition);
        if (!clk2.isEmpty() && this.position.y >= this.blockPosition.getY() + clk2.getHeight(this.level, this.blockPosition)) {
            return Fluids.EMPTY.defaultFluidState();
        }
        return clk2;
    }
    
    public final Vec3 getLookVector() {
        return this.forwards;
    }
    
    public final Vec3 getUpVector() {
        return this.up;
    }
    
    public void reset() {
        this.level = null;
        this.entity = null;
        this.initialized = false;
    }
}
