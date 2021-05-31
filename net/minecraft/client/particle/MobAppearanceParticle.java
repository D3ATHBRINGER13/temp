package net.minecraft.client.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;

public class MobAppearanceParticle extends Particle {
    private LivingEntity displayEntity;
    
    private MobAppearanceParticle(final Level bhr, final double double2, final double double3, final double double4) {
        super(bhr, double2, double3, double4);
        this.gravity = 0.0f;
        this.lifetime = 30;
    }
    
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.displayEntity == null) {
            final ElderGuardian auh2 = EntityType.ELDER_GUARDIAN.create(this.level);
            auh2.setGhost();
            this.displayEntity = auh2;
        }
    }
    
    @Override
    public void render(final BufferBuilder cuw, final Camera cxq, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (this.displayEntity == null) {
            return;
        }
        final EntityRenderDispatcher dsa10 = Minecraft.getInstance().getEntityRenderDispatcher();
        dsa10.setPosition(Particle.xOff, Particle.yOff, Particle.zOff);
        final float float9 = 1.0f / ElderGuardian.ELDER_SIZE_SCALE;
        final float float10 = (this.age + float3) / this.lifetime;
        GlStateManager.depthMask(true);
        GlStateManager.enableBlend();
        GlStateManager.enableDepthTest();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        final float float11 = 240.0f;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0f, 240.0f);
        GlStateManager.pushMatrix();
        final float float12 = 0.05f + 0.5f * Mth.sin(float10 * 3.1415927f);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, float12);
        GlStateManager.translatef(0.0f, 1.8f, 0.0f);
        GlStateManager.rotatef(180.0f - cxq.getYRot(), 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(60.0f - 150.0f * float10 - cxq.getXRot(), 1.0f, 0.0f, 0.0f);
        GlStateManager.translatef(0.0f, -0.4f, -1.5f);
        GlStateManager.scalef(float9, float9, float9);
        this.displayEntity.yRot = 0.0f;
        this.displayEntity.yHeadRot = 0.0f;
        this.displayEntity.yRotO = 0.0f;
        this.displayEntity.yHeadRotO = 0.0f;
        dsa10.render(this.displayEntity, 0.0, 0.0, 0.0, 0.0f, float3, false);
        GlStateManager.popMatrix();
        GlStateManager.enableDepthTest();
    }
    
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        public Particle createParticle(final SimpleParticleType gi, final Level bhr, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
            return new MobAppearanceParticle(bhr, double3, double4, double5, null);
        }
    }
}
