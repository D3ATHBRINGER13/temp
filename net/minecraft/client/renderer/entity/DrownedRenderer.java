package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Mth;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.client.renderer.entity.layers.DrownedOuterLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.world.entity.monster.Drowned;

public class DrownedRenderer extends AbstractZombieRenderer<Drowned, DrownedModel<Drowned>> {
    private static final ResourceLocation DROWNED_LOCATION;
    
    public DrownedRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new DrownedModel(0.0f, 0.0f, 64, 64), new DrownedModel(0.5f, true), new DrownedModel(1.0f, true));
        this.addLayer((RenderLayer<Drowned, DrownedModel<Drowned>>)new DrownedOuterLayer((RenderLayerParent<Zombie, DrownedModel<Zombie>>)this));
    }
    
    @Nullable
    @Override
    protected ResourceLocation getTextureLocation(final Zombie avm) {
        return DrownedRenderer.DROWNED_LOCATION;
    }
    
    @Override
    protected void setupRotations(final Drowned aug, final float float2, final float float3, final float float4) {
        final float float5 = aug.getSwimAmount(float4);
        super.setupRotations(aug, float2, float3, float4);
        if (float5 > 0.0f) {
            GlStateManager.rotatef(Mth.lerp(float5, aug.xRot, -10.0f - aug.xRot), 1.0f, 0.0f, 0.0f);
        }
    }
    
    static {
        DROWNED_LOCATION = new ResourceLocation("textures/entity/zombie/drowned.png");
    }
}
