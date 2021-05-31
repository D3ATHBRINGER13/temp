package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.renderer.culling.Culler;
import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.ShulkerHeadLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.world.entity.monster.Shulker;

public class ShulkerRenderer extends MobRenderer<Shulker, ShulkerModel<Shulker>> {
    public static final ResourceLocation DEFAULT_TEXTURE_LOCATION;
    public static final ResourceLocation[] TEXTURE_LOCATION;
    
    public ShulkerRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new ShulkerModel(), 0.0f);
        this.addLayer(new ShulkerHeadLayer(this));
    }
    
    @Override
    public void render(final Shulker avb, final double double2, final double double3, final double double4, final float float5, final float float6) {
        final int integer11 = avb.getClientSideTeleportInterpolation();
        if (integer11 > 0 && avb.hasValidInterpolationPositions()) {
            final BlockPos ew12 = avb.getAttachPosition();
            final BlockPos ew13 = avb.getOldAttachPosition();
            double double5 = (integer11 - float6) / 6.0;
            double5 *= double5;
            final double double6 = (ew12.getX() - ew13.getX()) * double5;
            final double double7 = (ew12.getY() - ew13.getY()) * double5;
            final double double8 = (ew12.getZ() - ew13.getZ()) * double5;
            super.render(avb, double2 - double6, double3 - double7, double4 - double8, float5, float6);
        }
        else {
            super.render(avb, double2, double3, double4, float5, float6);
        }
    }
    
    @Override
    public boolean shouldRender(final Shulker avb, final Culler dqe, final double double3, final double double4, final double double5) {
        if (super.shouldRender(avb, dqe, double3, double4, double5)) {
            return true;
        }
        if (avb.getClientSideTeleportInterpolation() > 0 && avb.hasValidInterpolationPositions()) {
            final BlockPos ew10 = avb.getOldAttachPosition();
            final BlockPos ew11 = avb.getAttachPosition();
            final Vec3 csi12 = new Vec3(ew11.getX(), ew11.getY(), ew11.getZ());
            final Vec3 csi13 = new Vec3(ew10.getX(), ew10.getY(), ew10.getZ());
            if (dqe.isVisible(new AABB(csi13.x, csi13.y, csi13.z, csi12.x, csi12.y, csi12.z))) {
                return true;
            }
        }
        return false;
    }
    
    protected ResourceLocation getTextureLocation(final Shulker avb) {
        if (avb.getColor() == null) {
            return ShulkerRenderer.DEFAULT_TEXTURE_LOCATION;
        }
        return ShulkerRenderer.TEXTURE_LOCATION[avb.getColor().getId()];
    }
    
    @Override
    protected void setupRotations(final Shulker avb, final float float2, final float float3, final float float4) {
        super.setupRotations(avb, float2, float3, float4);
        switch (avb.getAttachFace()) {
            case EAST: {
                GlStateManager.translatef(0.5f, 0.5f, 0.0f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
                break;
            }
            case WEST: {
                GlStateManager.translatef(-0.5f, 0.5f, 0.0f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotatef(-90.0f, 0.0f, 0.0f, 1.0f);
                break;
            }
            case NORTH: {
                GlStateManager.translatef(0.0f, 0.5f, -0.5f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                break;
            }
            case SOUTH: {
                GlStateManager.translatef(0.0f, 0.5f, 0.5f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
                break;
            }
            case UP: {
                GlStateManager.translatef(0.0f, 1.0f, 0.0f);
                GlStateManager.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
                break;
            }
        }
    }
    
    @Override
    protected void scale(final Shulker avb, final float float2) {
        final float float3 = 0.999f;
        GlStateManager.scalef(0.999f, 0.999f, 0.999f);
    }
    
    static {
        DEFAULT_TEXTURE_LOCATION = new ResourceLocation("textures/entity/shulker/shulker.png");
        TEXTURE_LOCATION = new ResourceLocation[] { new ResourceLocation("textures/entity/shulker/shulker_white.png"), new ResourceLocation("textures/entity/shulker/shulker_orange.png"), new ResourceLocation("textures/entity/shulker/shulker_magenta.png"), new ResourceLocation("textures/entity/shulker/shulker_light_blue.png"), new ResourceLocation("textures/entity/shulker/shulker_yellow.png"), new ResourceLocation("textures/entity/shulker/shulker_lime.png"), new ResourceLocation("textures/entity/shulker/shulker_pink.png"), new ResourceLocation("textures/entity/shulker/shulker_gray.png"), new ResourceLocation("textures/entity/shulker/shulker_light_gray.png"), new ResourceLocation("textures/entity/shulker/shulker_cyan.png"), new ResourceLocation("textures/entity/shulker/shulker_purple.png"), new ResourceLocation("textures/entity/shulker/shulker_blue.png"), new ResourceLocation("textures/entity/shulker/shulker_brown.png"), new ResourceLocation("textures/entity/shulker/shulker_green.png"), new ResourceLocation("textures/entity/shulker/shulker_red.png"), new ResourceLocation("textures/entity/shulker/shulker_black.png") };
    }
}
