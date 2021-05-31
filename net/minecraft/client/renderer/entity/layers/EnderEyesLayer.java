package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.Entity;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.world.entity.LivingEntity;

public class EnderEyesLayer<T extends LivingEntity> extends RenderLayer<T, EndermanModel<T>> {
    private static final ResourceLocation ENDERMAN_EYES_LOCATION;
    
    public EnderEyesLayer(final RenderLayerParent<T, EndermanModel<T>> dtr) {
        super(dtr);
    }
    
    @Override
    public void render(final T aix, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        this.bindTexture(EnderEyesLayer.ENDERMAN_EYES_LOCATION);
        GlStateManager.enableBlend();
        GlStateManager.disableAlphaTest();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(!aix.isInvisible());
        final int integer10 = 61680;
        final int integer11 = 61680;
        final int integer12 = 0;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 61680.0f, 0.0f);
        GlStateManager.enableLighting();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final GameRenderer dnc13 = Minecraft.getInstance().gameRenderer;
        dnc13.resetFogColor(true);
        this.getParentModel().render(aix, float2, float3, float5, float6, float7, float8);
        dnc13.resetFogColor(false);
        this.setLightColor(aix);
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
    }
    
    @Override
    public boolean colorsOnDamage() {
        return false;
    }
    
    static {
        ENDERMAN_EYES_LOCATION = new ResourceLocation("textures/entity/enderman/enderman_eyes.png");
    }
}
