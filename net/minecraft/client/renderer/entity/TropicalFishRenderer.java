package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.TropicalFishPatternLayer;
import net.minecraft.client.model.TropicalFishModelB;
import net.minecraft.client.model.TropicalFishModelA;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.animal.TropicalFish;

public class TropicalFishRenderer extends MobRenderer<TropicalFish, EntityModel<TropicalFish>> {
    private final TropicalFishModelA<TropicalFish> modelA;
    private final TropicalFishModelB<TropicalFish> modelB;
    
    public TropicalFishRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new TropicalFishModelA(), 0.15f);
        this.modelA = new TropicalFishModelA<TropicalFish>();
        this.modelB = new TropicalFishModelB<TropicalFish>();
        this.addLayer(new TropicalFishPatternLayer(this));
    }
    
    @Nullable
    protected ResourceLocation getTextureLocation(final TropicalFish arw) {
        return arw.getBaseTextureLocation();
    }
    
    @Override
    public void render(final TropicalFish arw, final double double2, final double double3, final double double4, final float float5, final float float6) {
        this.model = (M)((arw.getBaseVariant() == 0) ? this.modelA : this.modelB);
        final float[] arr11 = arw.getBaseColor();
        GlStateManager.color3f(arr11[0], arr11[1], arr11[2]);
        super.render(arw, double2, double3, double4, float5, float6);
    }
    
    @Override
    protected void setupRotations(final TropicalFish arw, final float float2, final float float3, final float float4) {
        super.setupRotations(arw, float2, float3, float4);
        final float float5 = 4.3f * Mth.sin(0.6f * float2);
        GlStateManager.rotatef(float5, 0.0f, 1.0f, 0.0f);
        if (!arw.isInWater()) {
            GlStateManager.translatef(0.2f, 0.1f, 0.0f);
            GlStateManager.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
        }
    }
}
