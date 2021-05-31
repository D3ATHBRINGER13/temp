package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.world.entity.animal.SnowGolem;

public class SnowGolemHeadLayer extends RenderLayer<SnowGolem, SnowGolemModel<SnowGolem>> {
    public SnowGolemHeadLayer(final RenderLayerParent<SnowGolem, SnowGolemModel<SnowGolem>> dtr) {
        super(dtr);
    }
    
    @Override
    public void render(final SnowGolem aru, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (aru.isInvisible() || !aru.hasPumpkin()) {
            return;
        }
        GlStateManager.pushMatrix();
        ((RenderLayer<T, SnowGolemModel>)this).getParentModel().getHead().translateTo(0.0625f);
        final float float9 = 0.625f;
        GlStateManager.translatef(0.0f, -0.34375f, 0.0f);
        GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.scalef(0.625f, -0.625f, -0.625f);
        Minecraft.getInstance().getItemInHandRenderer().renderItem(aru, new ItemStack(Blocks.CARVED_PUMPKIN), ItemTransforms.TransformType.HEAD);
        GlStateManager.popMatrix();
    }
    
    @Override
    public boolean colorsOnDamage() {
        return true;
    }
}
