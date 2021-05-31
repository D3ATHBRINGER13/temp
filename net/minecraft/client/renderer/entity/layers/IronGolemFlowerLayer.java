package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.world.entity.animal.IronGolem;

public class IronGolemFlowerLayer extends RenderLayer<IronGolem, IronGolemModel<IronGolem>> {
    public IronGolemFlowerLayer(final RenderLayerParent<IronGolem, IronGolemModel<IronGolem>> dtr) {
        super(dtr);
    }
    
    @Override
    public void render(final IronGolem ari, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (ari.getOfferFlowerTick() == 0) {
            return;
        }
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        GlStateManager.rotatef(5.0f + 180.0f * ((RenderLayer<T, IronGolemModel>)this).getParentModel().getFlowerHoldingArm().xRot / 3.1415927f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.translatef(-0.9375f, -0.625f, -0.9375f);
        final float float9 = 0.5f;
        GlStateManager.scalef(0.5f, -0.5f, 0.5f);
        final int integer11 = ari.getLightColor();
        final int integer12 = integer11 % 65536;
        final int integer13 = integer11 / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)integer12, (float)integer13);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.bindTexture(TextureAtlas.LOCATION_BLOCKS);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.POPPY.defaultBlockState(), 1.0f);
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
    }
    
    @Override
    public boolean colorsOnDamage() {
        return false;
    }
}
