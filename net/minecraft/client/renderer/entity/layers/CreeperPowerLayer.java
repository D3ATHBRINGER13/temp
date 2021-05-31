package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.Entity;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperPowerLayer extends RenderLayer<Creeper, CreeperModel<Creeper>> {
    private static final ResourceLocation POWER_LOCATION;
    private final CreeperModel<Creeper> model;
    
    public CreeperPowerLayer(final RenderLayerParent<Creeper, CreeperModel<Creeper>> dtr) {
        super(dtr);
        this.model = new CreeperModel<Creeper>(2.0f);
    }
    
    @Override
    public void render(final Creeper aue, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (!aue.isPowered()) {
            return;
        }
        final boolean boolean10 = aue.isInvisible();
        GlStateManager.depthMask(!boolean10);
        this.bindTexture(CreeperPowerLayer.POWER_LOCATION);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        final float float9 = aue.tickCount + float4;
        GlStateManager.translatef(float9 * 0.01f, float9 * 0.01f, 0.0f);
        GlStateManager.matrixMode(5888);
        GlStateManager.enableBlend();
        final float float10 = 0.5f;
        GlStateManager.color4f(0.5f, 0.5f, 0.5f, 1.0f);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        ((RenderLayer<T, CreeperModel<Creeper>>)this).getParentModel().copyPropertiesTo(this.model);
        final GameRenderer dnc13 = Minecraft.getInstance().gameRenderer;
        dnc13.resetFogColor(true);
        this.model.render(aue, float2, float3, float5, float6, float7, float8);
        dnc13.resetFogColor(false);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
    }
    
    @Override
    public boolean colorsOnDamage() {
        return false;
    }
    
    static {
        POWER_LOCATION = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
    }
}
