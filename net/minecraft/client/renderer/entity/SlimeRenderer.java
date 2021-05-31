package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.renderer.entity.layers.SlimeOuterLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.world.entity.monster.Slime;

public class SlimeRenderer extends MobRenderer<Slime, SlimeModel<Slime>> {
    private static final ResourceLocation SLIME_LOCATION;
    
    public SlimeRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new SlimeModel(16), 0.25f);
        this.addLayer((RenderLayer<Slime, SlimeModel<Slime>>)new SlimeOuterLayer((RenderLayerParent<Entity, SlimeModel<Entity>>)this));
    }
    
    @Override
    public void render(final Slime ave, final double double2, final double double3, final double double4, final float float5, final float float6) {
        this.shadowRadius = 0.25f * ave.getSize();
        super.render(ave, double2, double3, double4, float5, float6);
    }
    
    @Override
    protected void scale(final Slime ave, final float float2) {
        final float float3 = 0.999f;
        GlStateManager.scalef(0.999f, 0.999f, 0.999f);
        final float float4 = (float)ave.getSize();
        final float float5 = Mth.lerp(float2, ave.oSquish, ave.squish) / (float4 * 0.5f + 1.0f);
        final float float6 = 1.0f / (float5 + 1.0f);
        GlStateManager.scalef(float6 * float4, 1.0f / float6 * float4, float6 * float4);
    }
    
    protected ResourceLocation getTextureLocation(final Slime ave) {
        return SlimeRenderer.SLIME_LOCATION;
    }
    
    static {
        SLIME_LOCATION = new ResourceLocation("textures/entity/slime/slime.png");
    }
}
