package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.Entity;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.model.CowModel;
import net.minecraft.world.entity.animal.MushroomCow;

public class MushroomCowMushroomLayer<T extends MushroomCow> extends RenderLayer<T, CowModel<T>> {
    public MushroomCowMushroomLayer(final RenderLayerParent<T, CowModel<T>> dtr) {
        super(dtr);
    }
    
    @Override
    public void render(final T arj, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (arj.isBaby() || arj.isInvisible()) {
            return;
        }
        final BlockState bvt10 = arj.getMushroomType().getBlockState();
        this.bindTexture(TextureAtlas.LOCATION_BLOCKS);
        GlStateManager.enableCull();
        GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
        GlStateManager.pushMatrix();
        GlStateManager.scalef(1.0f, -1.0f, 1.0f);
        GlStateManager.translatef(0.2f, 0.35f, 0.5f);
        GlStateManager.rotatef(42.0f, 0.0f, 1.0f, 0.0f);
        final BlockRenderDispatcher dnw11 = Minecraft.getInstance().getBlockRenderer();
        GlStateManager.pushMatrix();
        GlStateManager.translatef(-0.5f, -0.5f, 0.5f);
        dnw11.renderSingleBlock(bvt10, 1.0f);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.1f, 0.0f, -0.6f);
        GlStateManager.rotatef(42.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(-0.5f, -0.5f, 0.5f);
        dnw11.renderSingleBlock(bvt10, 1.0f);
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        this.getParentModel().getHead().translateTo(0.0625f);
        GlStateManager.scalef(1.0f, -1.0f, 1.0f);
        GlStateManager.translatef(0.0f, 0.7f, -0.2f);
        GlStateManager.rotatef(12.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(-0.5f, -0.5f, 0.5f);
        dnw11.renderSingleBlock(bvt10, 1.0f);
        GlStateManager.popMatrix();
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.disableCull();
    }
    
    @Override
    public boolean colorsOnDamage() {
        return true;
    }
}
