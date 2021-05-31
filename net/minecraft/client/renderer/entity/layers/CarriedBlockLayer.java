package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.world.entity.monster.EnderMan;

public class CarriedBlockLayer extends RenderLayer<EnderMan, EndermanModel<EnderMan>> {
    public CarriedBlockLayer(final RenderLayerParent<EnderMan, EndermanModel<EnderMan>> dtr) {
        super(dtr);
    }
    
    @Override
    public void render(final EnderMan aui, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        final BlockState bvt10 = aui.getCarriedBlock();
        if (bvt10 == null) {
            return;
        }
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, 0.6875f, -0.75f);
        GlStateManager.rotatef(20.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(45.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(0.25f, 0.1875f, 0.25f);
        final float float9 = 0.5f;
        GlStateManager.scalef(-0.5f, -0.5f, 0.5f);
        final int integer12 = aui.getLightColor();
        final int integer13 = integer12 % 65536;
        final int integer14 = integer12 / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)integer13, (float)integer14);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.bindTexture(TextureAtlas.LOCATION_BLOCKS);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(bvt10, 1.0f);
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
    }
    
    @Override
    public boolean colorsOnDamage() {
        return false;
    }
}
