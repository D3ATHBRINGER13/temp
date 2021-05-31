package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import javax.annotation.Nullable;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Mth;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.FoxHeldItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.FoxModel;
import net.minecraft.world.entity.animal.Fox;

public class FoxRenderer extends MobRenderer<Fox, FoxModel<Fox>> {
    private static final ResourceLocation RED_FOX_TEXTURE;
    private static final ResourceLocation RED_FOX_SLEEP_TEXTURE;
    private static final ResourceLocation SNOW_FOX_TEXTURE;
    private static final ResourceLocation SNOW_FOX_SLEEP_TEXTURE;
    
    public FoxRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new FoxModel(), 0.4f);
        this.addLayer(new FoxHeldItemLayer(this));
    }
    
    @Override
    protected void setupRotations(final Fox arh, final float float2, final float float3, final float float4) {
        super.setupRotations(arh, float2, float3, float4);
        if (arh.isPouncing() || arh.isFaceplanted()) {
            GlStateManager.rotatef(-Mth.lerp(float4, arh.xRotO, arh.xRot), 1.0f, 0.0f, 0.0f);
        }
    }
    
    @Nullable
    protected ResourceLocation getTextureLocation(final Fox arh) {
        if (arh.getFoxType() == Fox.Type.RED) {
            return arh.isSleeping() ? FoxRenderer.RED_FOX_SLEEP_TEXTURE : FoxRenderer.RED_FOX_TEXTURE;
        }
        return arh.isSleeping() ? FoxRenderer.SNOW_FOX_SLEEP_TEXTURE : FoxRenderer.SNOW_FOX_TEXTURE;
    }
    
    static {
        RED_FOX_TEXTURE = new ResourceLocation("textures/entity/fox/fox.png");
        RED_FOX_SLEEP_TEXTURE = new ResourceLocation("textures/entity/fox/fox_sleep.png");
        SNOW_FOX_TEXTURE = new ResourceLocation("textures/entity/fox/snow_fox.png");
        SNOW_FOX_SLEEP_TEXTURE = new ResourceLocation("textures/entity/fox/snow_fox_sleep.png");
    }
}
