package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.LavaSlimeModel;
import net.minecraft.world.entity.monster.MagmaCube;

public class LavaSlimeRenderer extends MobRenderer<MagmaCube, LavaSlimeModel<MagmaCube>> {
    private static final ResourceLocation MAGMACUBE_LOCATION;
    
    public LavaSlimeRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new LavaSlimeModel(), 0.25f);
    }
    
    protected ResourceLocation getTextureLocation(final MagmaCube aur) {
        return LavaSlimeRenderer.MAGMACUBE_LOCATION;
    }
    
    @Override
    protected void scale(final MagmaCube aur, final float float2) {
        final int integer4 = aur.getSize();
        final float float3 = Mth.lerp(float2, aur.oSquish, aur.squish) / (integer4 * 0.5f + 1.0f);
        final float float4 = 1.0f / (float3 + 1.0f);
        GlStateManager.scalef(float4 * integer4, 1.0f / float4 * integer4, float4 * integer4);
    }
    
    static {
        MAGMACUBE_LOCATION = new ResourceLocation("textures/entity/slime/magmacube.png");
    }
}
