package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Mth;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;

public class Deadmau5EarsLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public Deadmau5EarsLayer(final RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> dtr) {
        super(dtr);
    }
    
    @Override
    public void render(final AbstractClientPlayer dmm, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (!"deadmau5".equals(dmm.getName().getString()) || !dmm.isSkinLoaded() || dmm.isInvisible()) {
            return;
        }
        this.bindTexture(dmm.getSkinTextureLocation());
        for (int integer10 = 0; integer10 < 2; ++integer10) {
            final float float9 = Mth.lerp(float4, dmm.yRotO, dmm.yRot) - Mth.lerp(float4, dmm.yBodyRotO, dmm.yBodyRot);
            final float float10 = Mth.lerp(float4, dmm.xRotO, dmm.xRot);
            GlStateManager.pushMatrix();
            GlStateManager.rotatef(float9, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotatef(float10, 1.0f, 0.0f, 0.0f);
            GlStateManager.translatef(0.375f * (integer10 * 2 - 1), 0.0f, 0.0f);
            GlStateManager.translatef(0.0f, -0.375f, 0.0f);
            GlStateManager.rotatef(-float10, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotatef(-float9, 0.0f, 1.0f, 0.0f);
            final float float11 = 1.3333334f;
            GlStateManager.scalef(1.3333334f, 1.3333334f, 1.3333334f);
            ((RenderLayer<T, PlayerModel>)this).getParentModel().renderEars(0.0625f);
            GlStateManager.popMatrix();
        }
    }
    
    @Override
    public boolean colorsOnDamage() {
        return true;
    }
}
