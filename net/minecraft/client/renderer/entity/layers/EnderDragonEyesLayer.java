package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.Entity;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.dragon.DragonModel;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;

public class EnderDragonEyesLayer extends RenderLayer<EnderDragon, DragonModel> {
    private static final ResourceLocation DRAGON_EYES_LOCATION;
    
    public EnderDragonEyesLayer(final RenderLayerParent<EnderDragon, DragonModel> dtr) {
        super(dtr);
    }
    
    @Override
    public void render(final EnderDragon asp, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        this.bindTexture(EnderDragonEyesLayer.DRAGON_EYES_LOCATION);
        GlStateManager.enableBlend();
        GlStateManager.disableAlphaTest();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        GlStateManager.disableLighting();
        GlStateManager.depthFunc(514);
        final int integer10 = 61680;
        final int integer11 = 61680;
        final int integer12 = 0;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 61680.0f, 0.0f);
        GlStateManager.enableLighting();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final GameRenderer dnc13 = Minecraft.getInstance().gameRenderer;
        dnc13.resetFogColor(true);
        ((RenderLayer<T, DragonModel>)this).getParentModel().render(asp, float2, float3, float5, float6, float7, float8);
        dnc13.resetFogColor(false);
        ((RenderLayer<EnderDragon, M>)this).setLightColor(asp);
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
        GlStateManager.depthFunc(515);
    }
    
    @Override
    public boolean colorsOnDamage() {
        return false;
    }
    
    static {
        DRAGON_EYES_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon_eyes.png");
    }
}
