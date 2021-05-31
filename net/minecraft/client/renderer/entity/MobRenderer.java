package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.LivingEntity;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.renderer.culling.Culler;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.Mob;

public abstract class MobRenderer<T extends Mob, M extends EntityModel<T>> extends LivingEntityRenderer<T, M> {
    public MobRenderer(final EntityRenderDispatcher dsa, final M dhh, final float float3) {
        super(dsa, dhh, float3);
    }
    
    @Override
    protected boolean shouldShowName(final T aiy) {
        return super.shouldShowName(aiy) && (aiy.shouldShowName() || (aiy.hasCustomName() && aiy == this.entityRenderDispatcher.crosshairPickEntity));
    }
    
    public boolean shouldRender(final T aiy, final Culler dqe, final double double3, final double double4, final double double5) {
        if (super.shouldRender((T)aiy, dqe, double3, double4, double5)) {
            return true;
        }
        final Entity aio10 = aiy.getLeashHolder();
        return aio10 != null && dqe.isVisible(aio10.getBoundingBoxForCulling());
    }
    
    @Override
    public void render(final T aiy, final double double2, final double double3, final double double4, final float float5, final float float6) {
        super.render(aiy, double2, double3, double4, float5, float6);
        if (!this.solidRender) {
            this.renderLeash(aiy, double2, double3, double4, float5, float6);
        }
    }
    
    protected void renderLeash(final T aiy, double double2, double double3, double double4, final float float5, final float float6) {
        final Entity aio11 = aiy.getLeashHolder();
        if (aio11 == null) {
            return;
        }
        double3 -= (1.6 - aiy.getBbHeight()) * 0.5;
        final Tesselator cuz12 = Tesselator.getInstance();
        final BufferBuilder cuw13 = cuz12.getBuilder();
        final double double5 = Mth.lerp(float6 * 0.5f, aio11.yRot, aio11.yRotO) * 0.017453292f;
        final double double6 = Mth.lerp(float6 * 0.5f, aio11.xRot, aio11.xRotO) * 0.017453292f;
        double double7 = Math.cos(double5);
        double double8 = Math.sin(double5);
        double double9 = Math.sin(double6);
        if (aio11 instanceof HangingEntity) {
            double7 = 0.0;
            double8 = 0.0;
            double9 = -1.0;
        }
        final double double10 = Math.cos(double6);
        final double double11 = Mth.lerp(float6, aio11.xo, aio11.x) - double7 * 0.7 - double8 * 0.5 * double10;
        final double double12 = Mth.lerp(float6, aio11.yo + aio11.getEyeHeight() * 0.7, aio11.y + aio11.getEyeHeight() * 0.7) - double9 * 0.5 - 0.25;
        final double double13 = Mth.lerp(float6, aio11.zo, aio11.z) - double8 * 0.7 + double7 * 0.5 * double10;
        final double double14 = Mth.lerp(float6, aiy.yBodyRot, aiy.yBodyRotO) * 0.017453292f + 1.5707963267948966;
        double7 = Math.cos(double14) * aiy.getBbWidth() * 0.4;
        double8 = Math.sin(double14) * aiy.getBbWidth() * 0.4;
        final double double15 = Mth.lerp(float6, aiy.xo, aiy.x) + double7;
        final double double16 = Mth.lerp(float6, aiy.yo, aiy.y);
        final double double17 = Mth.lerp(float6, aiy.zo, aiy.z) + double8;
        double2 += double7;
        double4 += double8;
        final double double18 = (float)(double11 - double15);
        final double double19 = (float)(double12 - double16);
        final double double20 = (float)(double13 - double17);
        GlStateManager.disableTexture();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        final int integer46 = 24;
        final double double21 = 0.025;
        cuw13.begin(5, DefaultVertexFormat.POSITION_COLOR);
        for (int integer47 = 0; integer47 <= 24; ++integer47) {
            float float7 = 0.5f;
            float float8 = 0.4f;
            float float9 = 0.3f;
            if (integer47 % 2 == 0) {
                float7 *= 0.7f;
                float8 *= 0.7f;
                float9 *= 0.7f;
            }
            final float float10 = integer47 / 24.0f;
            cuw13.vertex(double2 + double18 * float10 + 0.0, double3 + double19 * (float10 * float10 + float10) * 0.5 + ((24.0f - integer47) / 18.0f + 0.125f), double4 + double20 * float10).color(float7, float8, float9, 1.0f).endVertex();
            cuw13.vertex(double2 + double18 * float10 + 0.025, double3 + double19 * (float10 * float10 + float10) * 0.5 + ((24.0f - integer47) / 18.0f + 0.125f) + 0.025, double4 + double20 * float10).color(float7, float8, float9, 1.0f).endVertex();
        }
        cuz12.end();
        cuw13.begin(5, DefaultVertexFormat.POSITION_COLOR);
        for (int integer47 = 0; integer47 <= 24; ++integer47) {
            float float7 = 0.5f;
            float float8 = 0.4f;
            float float9 = 0.3f;
            if (integer47 % 2 == 0) {
                float7 *= 0.7f;
                float8 *= 0.7f;
                float9 *= 0.7f;
            }
            final float float10 = integer47 / 24.0f;
            cuw13.vertex(double2 + double18 * float10 + 0.0, double3 + double19 * (float10 * float10 + float10) * 0.5 + ((24.0f - integer47) / 18.0f + 0.125f) + 0.025, double4 + double20 * float10).color(float7, float8, float9, 1.0f).endVertex();
            cuw13.vertex(double2 + double18 * float10 + 0.025, double3 + double19 * (float10 * float10 + float10) * 0.5 + ((24.0f - integer47) / 18.0f + 0.125f), double4 + double20 * float10 + 0.025).color(float7, float8, float9, 1.0f).endVertex();
        }
        cuz12.end();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture();
        GlStateManager.enableCull();
    }
}
