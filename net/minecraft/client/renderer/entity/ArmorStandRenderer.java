package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.model.ArmorStandModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.ArmorStandArmorModel;
import net.minecraft.world.entity.decoration.ArmorStand;

public class ArmorStandRenderer extends LivingEntityRenderer<ArmorStand, ArmorStandArmorModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION;
    
    public ArmorStandRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new ArmorStandModel(), 0.0f);
        this.addLayer((RenderLayer<ArmorStand, ArmorStandArmorModel>)new HumanoidArmorLayer((RenderLayerParent<LivingEntity, HumanoidModel>)this, new ArmorStandArmorModel(0.5f), new ArmorStandArmorModel(1.0f)));
        this.addLayer(new ItemInHandLayer<ArmorStand, ArmorStandArmorModel>(this));
        this.addLayer(new ElytraLayer<ArmorStand, ArmorStandArmorModel>(this));
        this.addLayer(new CustomHeadLayer<ArmorStand, ArmorStandArmorModel>(this));
    }
    
    protected ResourceLocation getTextureLocation(final ArmorStand atl) {
        return ArmorStandRenderer.DEFAULT_SKIN_LOCATION;
    }
    
    @Override
    protected void setupRotations(final ArmorStand atl, final float float2, final float float3, final float float4) {
        GlStateManager.rotatef(180.0f - float3, 0.0f, 1.0f, 0.0f);
        final float float5 = atl.level.getGameTime() - atl.lastHit + float4;
        if (float5 < 5.0f) {
            GlStateManager.rotatef(Mth.sin(float5 / 1.5f * 3.1415927f) * 3.0f, 0.0f, 1.0f, 0.0f);
        }
    }
    
    @Override
    protected boolean shouldShowName(final ArmorStand atl) {
        return atl.isCustomNameVisible();
    }
    
    @Override
    public void render(final ArmorStand atl, final double double2, final double double3, final double double4, final float float5, final float float6) {
        if (atl.isMarker()) {
            this.onlySolidLayers = true;
        }
        super.render(atl, double2, double3, double4, float5, float6);
        if (atl.isMarker()) {
            this.onlySolidLayers = false;
        }
    }
    
    static {
        DEFAULT_SKIN_LOCATION = new ResourceLocation("textures/entity/armorstand/wood.png");
    }
}
