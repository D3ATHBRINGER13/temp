package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.renderer.culling.Culler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.GuardianModel;
import net.minecraft.world.entity.monster.Guardian;

public class GuardianRenderer extends MobRenderer<Guardian, GuardianModel> {
    private static final ResourceLocation GUARDIAN_LOCATION;
    private static final ResourceLocation GUARDIAN_BEAM_LOCATION;
    
    public GuardianRenderer(final EntityRenderDispatcher dsa) {
        this(dsa, 0.5f);
    }
    
    protected GuardianRenderer(final EntityRenderDispatcher dsa, final float float2) {
        super(dsa, new GuardianModel(), float2);
    }
    
    @Override
    public boolean shouldRender(final Guardian auo, final Culler dqe, final double double3, final double double4, final double double5) {
        if (super.shouldRender(auo, dqe, double3, double4, double5)) {
            return true;
        }
        if (auo.hasActiveAttackTarget()) {
            final LivingEntity aix10 = auo.getActiveAttackTarget();
            if (aix10 != null) {
                final Vec3 csi11 = this.getPosition(aix10, aix10.getBbHeight() * 0.5, 1.0f);
                final Vec3 csi12 = this.getPosition(auo, auo.getEyeHeight(), 1.0f);
                if (dqe.isVisible(new AABB(csi12.x, csi12.y, csi12.z, csi11.x, csi11.y, csi11.z))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private Vec3 getPosition(final LivingEntity aix, final double double2, final float float3) {
        final double double3 = Mth.lerp(float3, aix.xOld, aix.x);
        final double double4 = Mth.lerp(float3, aix.yOld, aix.y) + double2;
        final double double5 = Mth.lerp(float3, aix.zOld, aix.z);
        return new Vec3(double3, double4, double5);
    }
    
    @Override
    public void render(final Guardian auo, final double double2, final double double3, final double double4, final float float5, final float float6) {
        super.render(auo, double2, double3, double4, float5, float6);
        final LivingEntity aix11 = auo.getActiveAttackTarget();
        if (aix11 != null) {
            final float float7 = auo.getAttackAnimationScale(float6);
            final Tesselator cuz13 = Tesselator.getInstance();
            final BufferBuilder cuw14 = cuz13.getBuilder();
            this.bindTexture(GuardianRenderer.GUARDIAN_BEAM_LOCATION);
            GlStateManager.texParameter(3553, 10242, 10497);
            GlStateManager.texParameter(3553, 10243, 10497);
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            final float float8 = 240.0f;
            GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0f, 240.0f);
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            final float float9 = auo.level.getGameTime() + float6;
            final float float10 = float9 * 0.5f % 1.0f;
            final float float11 = auo.getEyeHeight();
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)double2, (float)double3 + float11, (float)double4);
            final Vec3 csi19 = this.getPosition(aix11, aix11.getBbHeight() * 0.5, float6);
            final Vec3 csi20 = this.getPosition(auo, float11, float6);
            Vec3 csi21 = csi19.subtract(csi20);
            final double double5 = csi21.length() + 1.0;
            csi21 = csi21.normalize();
            final float float12 = (float)Math.acos(csi21.y);
            final float float13 = (float)Math.atan2(csi21.z, csi21.x);
            GlStateManager.rotatef((1.5707964f - float13) * 57.295776f, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotatef(float12 * 57.295776f, 1.0f, 0.0f, 0.0f);
            final int integer26 = 1;
            final double double6 = float9 * 0.05 * -1.5;
            cuw14.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
            final float float14 = float7 * float7;
            final int integer27 = 64 + (int)(float14 * 191.0f);
            final int integer28 = 32 + (int)(float14 * 191.0f);
            final int integer29 = 128 - (int)(float14 * 64.0f);
            final double double7 = 0.2;
            final double double8 = 0.282;
            final double double9 = 0.0 + Math.cos(double6 + 2.356194490192345) * 0.282;
            final double double10 = 0.0 + Math.sin(double6 + 2.356194490192345) * 0.282;
            final double double11 = 0.0 + Math.cos(double6 + 0.7853981633974483) * 0.282;
            final double double12 = 0.0 + Math.sin(double6 + 0.7853981633974483) * 0.282;
            final double double13 = 0.0 + Math.cos(double6 + 3.9269908169872414) * 0.282;
            final double double14 = 0.0 + Math.sin(double6 + 3.9269908169872414) * 0.282;
            final double double15 = 0.0 + Math.cos(double6 + 5.497787143782138) * 0.282;
            final double double16 = 0.0 + Math.sin(double6 + 5.497787143782138) * 0.282;
            final double double17 = 0.0 + Math.cos(double6 + 3.141592653589793) * 0.2;
            final double double18 = 0.0 + Math.sin(double6 + 3.141592653589793) * 0.2;
            final double double19 = 0.0 + Math.cos(double6 + 0.0) * 0.2;
            final double double20 = 0.0 + Math.sin(double6 + 0.0) * 0.2;
            final double double21 = 0.0 + Math.cos(double6 + 1.5707963267948966) * 0.2;
            final double double22 = 0.0 + Math.sin(double6 + 1.5707963267948966) * 0.2;
            final double double23 = 0.0 + Math.cos(double6 + 4.71238898038469) * 0.2;
            final double double24 = 0.0 + Math.sin(double6 + 4.71238898038469) * 0.2;
            final double double25 = double5;
            final double double26 = 0.0;
            final double double27 = 0.4999;
            final double double28 = -1.0f + float10;
            final double double29 = double5 * 2.5 + double28;
            cuw14.vertex(double17, double25, double18).uv(0.4999, double29).color(integer27, integer28, integer29, 255).endVertex();
            cuw14.vertex(double17, 0.0, double18).uv(0.4999, double28).color(integer27, integer28, integer29, 255).endVertex();
            cuw14.vertex(double19, 0.0, double20).uv(0.0, double28).color(integer27, integer28, integer29, 255).endVertex();
            cuw14.vertex(double19, double25, double20).uv(0.0, double29).color(integer27, integer28, integer29, 255).endVertex();
            cuw14.vertex(double21, double25, double22).uv(0.4999, double29).color(integer27, integer28, integer29, 255).endVertex();
            cuw14.vertex(double21, 0.0, double22).uv(0.4999, double28).color(integer27, integer28, integer29, 255).endVertex();
            cuw14.vertex(double23, 0.0, double24).uv(0.0, double28).color(integer27, integer28, integer29, 255).endVertex();
            cuw14.vertex(double23, double25, double24).uv(0.0, double29).color(integer27, integer28, integer29, 255).endVertex();
            double double30 = 0.0;
            if (auo.tickCount % 2 == 0) {
                double30 = 0.5;
            }
            cuw14.vertex(double9, double25, double10).uv(0.5, double30 + 0.5).color(integer27, integer28, integer29, 255).endVertex();
            cuw14.vertex(double11, double25, double12).uv(1.0, double30 + 0.5).color(integer27, integer28, integer29, 255).endVertex();
            cuw14.vertex(double15, double25, double16).uv(1.0, double30).color(integer27, integer28, integer29, 255).endVertex();
            cuw14.vertex(double13, double25, double14).uv(0.5, double30).color(integer27, integer28, integer29, 255).endVertex();
            cuz13.end();
            GlStateManager.popMatrix();
        }
    }
    
    protected ResourceLocation getTextureLocation(final Guardian auo) {
        return GuardianRenderer.GUARDIAN_LOCATION;
    }
    
    static {
        GUARDIAN_LOCATION = new ResourceLocation("textures/entity/guardian.png");
        GUARDIAN_BEAM_LOCATION = new ResourceLocation("textures/entity/guardian_beam.png");
    }
}
