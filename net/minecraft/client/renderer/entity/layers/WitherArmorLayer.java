package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.Entity;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.WitherBossModel;
import net.minecraft.world.entity.boss.wither.WitherBoss;

public class WitherArmorLayer extends RenderLayer<WitherBoss, WitherBossModel<WitherBoss>> {
    private static final ResourceLocation WITHER_ARMOR_LOCATION;
    private final WitherBossModel<WitherBoss> model;
    
    public WitherArmorLayer(final RenderLayerParent<WitherBoss, WitherBossModel<WitherBoss>> dtr) {
        super(dtr);
        this.model = new WitherBossModel<WitherBoss>(0.5f);
    }
    
    @Override
    public void render(final WitherBoss atj, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (!atj.isPowered()) {
            return;
        }
        GlStateManager.depthMask(!atj.isInvisible());
        this.bindTexture(WitherArmorLayer.WITHER_ARMOR_LOCATION);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        final float float9 = atj.tickCount + float4;
        final float float10 = Mth.cos(float9 * 0.02f) * 3.0f;
        final float float11 = float9 * 0.01f;
        GlStateManager.translatef(float10, float11, 0.0f);
        GlStateManager.matrixMode(5888);
        GlStateManager.enableBlend();
        final float float12 = 0.5f;
        GlStateManager.color4f(0.5f, 0.5f, 0.5f, 1.0f);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        this.model.prepareMobModel(atj, float2, float3, float4);
        ((RenderLayer<T, WitherBossModel<WitherBoss>>)this).getParentModel().copyPropertiesTo(this.model);
        final GameRenderer dnc14 = Minecraft.getInstance().gameRenderer;
        dnc14.resetFogColor(true);
        this.model.render(atj, float2, float3, float5, float6, float7, float8);
        dnc14.resetFogColor(false);
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
        WITHER_ARMOR_LOCATION = new ResourceLocation("textures/entity/wither/wither_armor.png");
    }
}
