package net.minecraft.client.player;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.MultiPlayerLevel;

public class RemotePlayer extends AbstractClientPlayer {
    public RemotePlayer(final MultiPlayerLevel dkf, final GameProfile gameProfile) {
        super(dkf, gameProfile);
        this.maxUpStep = 1.0f;
        this.noPhysics = true;
    }
    
    @Override
    public boolean shouldRenderAtSqrDistance(final double double1) {
        double double2 = this.getBoundingBox().getSize() * 10.0;
        if (Double.isNaN(double2)) {
            double2 = 1.0;
        }
        double2 *= 64.0 * getViewScale();
        return double1 < double2 * double2;
    }
    
    @Override
    public boolean hurt(final DamageSource ahx, final float float2) {
        return true;
    }
    
    @Override
    public void tick() {
        super.tick();
        this.animationSpeedOld = this.animationSpeed;
        final double double2 = this.x - this.xo;
        final double double3 = this.z - this.zo;
        float float6 = Mth.sqrt(double2 * double2 + double3 * double3) * 4.0f;
        if (float6 > 1.0f) {
            float6 = 1.0f;
        }
        this.animationSpeed += (float6 - this.animationSpeed) * 0.4f;
        this.animationPosition += this.animationSpeed;
    }
    
    @Override
    public void aiStep() {
        if (this.lerpSteps > 0) {
            final double double2 = this.x + (this.lerpX - this.x) / this.lerpSteps;
            final double double3 = this.y + (this.lerpY - this.y) / this.lerpSteps;
            final double double4 = this.z + (this.lerpZ - this.z) / this.lerpSteps;
            this.yRot += (float)(Mth.wrapDegrees(this.lerpYRot - this.yRot) / this.lerpSteps);
            this.xRot += (float)((this.lerpXRot - this.xRot) / this.lerpSteps);
            --this.lerpSteps;
            this.setPos(double2, double3, double4);
            this.setRot(this.yRot, this.xRot);
        }
        if (this.lerpHeadSteps > 0) {
            this.yHeadRot += (float)(Mth.wrapDegrees(this.lyHeadRot - this.yHeadRot) / this.lerpHeadSteps);
            --this.lerpHeadSteps;
        }
        this.oBob = this.bob;
        this.updateSwingTime();
        float float2;
        if (!this.onGround || this.getHealth() <= 0.0f) {
            float2 = 0.0f;
        }
        else {
            float2 = Math.min(0.1f, Mth.sqrt(Entity.getHorizontalDistanceSqr(this.getDeltaMovement())));
        }
        if (this.onGround || this.getHealth() <= 0.0f) {
            final float float3 = 0.0f;
        }
        else {
            final float float3 = (float)Math.atan(-this.getDeltaMovement().y * 0.20000000298023224) * 15.0f;
        }
        this.bob += (float2 - this.bob) * 0.4f;
        this.level.getProfiler().push("push");
        this.pushEntities();
        this.level.getProfiler().pop();
    }
    
    @Override
    protected void updatePlayerPose() {
    }
    
    @Override
    public void sendMessage(final Component jo) {
        Minecraft.getInstance().gui.getChat().addMessage(jo);
    }
}
