package net.minecraft.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.util.Mth;
import net.minecraft.client.Camera;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;

public class ItemPickupParticle extends Particle {
    private final Entity itemEntity;
    private final Entity target;
    private int life;
    private final int lifeTime;
    private final float yOffs;
    private final EntityRenderDispatcher entityRenderDispatcher;
    
    public ItemPickupParticle(final Level bhr, final Entity aio2, final Entity aio3, final float float4) {
        this(bhr, aio2, aio3, float4, aio2.getDeltaMovement());
    }
    
    private ItemPickupParticle(final Level bhr, final Entity aio2, final Entity aio3, final float float4, final Vec3 csi) {
        super(bhr, aio2.x, aio2.y, aio2.z, csi.x, csi.y, csi.z);
        this.entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        this.itemEntity = aio2;
        this.target = aio3;
        this.lifeTime = 3;
        this.yOffs = float4;
    }
    
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }
    
    @Override
    public void render(final BufferBuilder cuw, final Camera cxq, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        float float9 = (this.life + float3) / this.lifeTime;
        float9 *= float9;
        final double double11 = this.itemEntity.x;
        final double double12 = this.itemEntity.y;
        final double double13 = this.itemEntity.z;
        final double double14 = Mth.lerp(float3, this.target.xOld, this.target.x);
        final double double15 = Mth.lerp(float3, this.target.yOld, this.target.y) + this.yOffs;
        final double double16 = Mth.lerp(float3, this.target.zOld, this.target.z);
        double double17 = Mth.lerp(float9, double11, double14);
        double double18 = Mth.lerp(float9, double12, double15);
        double double19 = Mth.lerp(float9, double13, double16);
        final int integer29 = this.getLightColor(float3);
        final int integer30 = integer29 % 65536;
        final int integer31 = integer29 / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)integer30, (float)integer31);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        double17 -= ItemPickupParticle.xOff;
        double18 -= ItemPickupParticle.yOff;
        double19 -= ItemPickupParticle.zOff;
        GlStateManager.enableLighting();
        this.entityRenderDispatcher.render(this.itemEntity, double17, double18, double19, this.itemEntity.yRot, float3, false);
    }
    
    @Override
    public void tick() {
        ++this.life;
        if (this.life == this.lifeTime) {
            this.remove();
        }
    }
}
