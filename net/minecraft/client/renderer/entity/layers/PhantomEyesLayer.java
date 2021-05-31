package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.PhantomModel;
import net.minecraft.world.entity.Entity;

public class PhantomEyesLayer<T extends Entity> extends RenderLayer<T, PhantomModel<T>> {
    private static final ResourceLocation PHANTOM_EYES_LOCATION;
    
    public PhantomEyesLayer(final RenderLayerParent<T, PhantomModel<T>> dtr) {
        super(dtr);
    }
    
    @Override
    public void render(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        this.bindTexture(PhantomEyesLayer.PHANTOM_EYES_LOCATION);
        GlStateManager.enableBlend();
        GlStateManager.disableAlphaTest();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(!aio.isInvisible());
        final int integer10 = 61680;
        final int integer11 = 61680;
        final int integer12 = 0;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 61680.0f, 0.0f);
        GlStateManager.enableLighting();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final GameRenderer dnc13 = Minecraft.getInstance().gameRenderer;
        dnc13.resetFogColor(true);
        this.getParentModel().render(aio, float2, float3, float5, float6, float7, float8);
        dnc13.resetFogColor(false);
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
        PHANTOM_EYES_LOCATION = new ResourceLocation("textures/entity/phantom_eyes.png");
    }
}
