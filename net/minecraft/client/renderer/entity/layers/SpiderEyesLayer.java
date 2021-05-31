package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.world.entity.Entity;

public class SpiderEyesLayer<T extends Entity, M extends SpiderModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation SPIDER_EYES_LOCATION;
    
    public SpiderEyesLayer(final RenderLayerParent<T, M> dtr) {
        super(dtr);
    }
    
    @Override
    public void render(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        this.bindTexture(SpiderEyesLayer.SPIDER_EYES_LOCATION);
        GlStateManager.enableBlend();
        GlStateManager.disableAlphaTest();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        if (aio.isInvisible()) {
            GlStateManager.depthMask(false);
        }
        else {
            GlStateManager.depthMask(true);
        }
        int integer10 = 61680;
        int integer11 = integer10 % 65536;
        int integer12 = integer10 / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)integer11, (float)integer12);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final GameRenderer dnc13 = Minecraft.getInstance().gameRenderer;
        dnc13.resetFogColor(true);
        this.getParentModel().render(aio, float2, float3, float5, float6, float7, float8);
        dnc13.resetFogColor(false);
        integer10 = aio.getLightColor();
        integer11 = integer10 % 65536;
        integer12 = integer10 / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)integer11, (float)integer12);
        this.setLightColor(aio);
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
    }
    
    @Override
    public boolean colorsOnDamage() {
        return false;
    }
    
    static {
        SPIDER_EYES_LOCATION = new ResourceLocation("textures/entity/spider_eyes.png");
    }
}
