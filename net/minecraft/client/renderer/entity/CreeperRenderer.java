package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Mth;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.CreeperPowerLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperRenderer extends MobRenderer<Creeper, CreeperModel<Creeper>> {
    private static final ResourceLocation CREEPER_LOCATION;
    
    public CreeperRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new CreeperModel(), 0.5f);
        this.addLayer(new CreeperPowerLayer(this));
    }
    
    @Override
    protected void scale(final Creeper aue, final float float2) {
        float float3 = aue.getSwelling(float2);
        final float float4 = 1.0f + Mth.sin(float3 * 100.0f) * float3 * 0.01f;
        float3 = Mth.clamp(float3, 0.0f, 1.0f);
        float3 *= float3;
        float3 *= float3;
        final float float5 = (1.0f + float3 * 0.4f) * float4;
        final float float6 = (1.0f + float3 * 0.1f) / float4;
        GlStateManager.scalef(float5, float6, float5);
    }
    
    @Override
    protected int getOverlayColor(final Creeper aue, final float float2, final float float3) {
        final float float4 = aue.getSwelling(float3);
        if ((int)(float4 * 10.0f) % 2 == 0) {
            return 0;
        }
        int integer6 = (int)(float4 * 0.2f * 255.0f);
        integer6 = Mth.clamp(integer6, 0, 255);
        return integer6 << 24 | 0x30FFFFFF;
    }
    
    protected ResourceLocation getTextureLocation(final Creeper aue) {
        return CreeperRenderer.CREEPER_LOCATION;
    }
    
    static {
        CREEPER_LOCATION = new ResourceLocation("textures/entity/creeper/creeper.png");
    }
}
