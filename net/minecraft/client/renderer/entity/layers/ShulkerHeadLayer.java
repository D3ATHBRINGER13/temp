package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.ShulkerRenderer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.world.entity.monster.Shulker;

public class ShulkerHeadLayer extends RenderLayer<Shulker, ShulkerModel<Shulker>> {
    public ShulkerHeadLayer(final RenderLayerParent<Shulker, ShulkerModel<Shulker>> dtr) {
        super(dtr);
    }
    
    @Override
    public void render(final Shulker avb, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        GlStateManager.pushMatrix();
        switch (avb.getAttachFace()) {
            case EAST: {
                GlStateManager.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.translatef(1.0f, -1.0f, 0.0f);
                GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
                break;
            }
            case WEST: {
                GlStateManager.rotatef(-90.0f, 0.0f, 0.0f, 1.0f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.translatef(-1.0f, -1.0f, 0.0f);
                GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
                break;
            }
            case NORTH: {
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.translatef(0.0f, -1.0f, -1.0f);
                break;
            }
            case SOUTH: {
                GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.translatef(0.0f, -1.0f, 1.0f);
                break;
            }
            case UP: {
                GlStateManager.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.translatef(0.0f, -2.0f, 0.0f);
                break;
            }
        }
        final ModelPart djv10 = ((RenderLayer<T, ShulkerModel>)this).getParentModel().getHead();
        djv10.yRot = float6 * 0.017453292f;
        djv10.xRot = float7 * 0.017453292f;
        final DyeColor bbg11 = avb.getColor();
        if (bbg11 == null) {
            this.bindTexture(ShulkerRenderer.DEFAULT_TEXTURE_LOCATION);
        }
        else {
            this.bindTexture(ShulkerRenderer.TEXTURE_LOCATION[bbg11.getId()]);
        }
        djv10.render(float8);
        GlStateManager.popMatrix();
    }
    
    @Override
    public boolean colorsOnDamage() {
        return false;
    }
}
