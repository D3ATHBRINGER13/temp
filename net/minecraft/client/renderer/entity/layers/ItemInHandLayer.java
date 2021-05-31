package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.Entity;
import net.minecraft.client.renderer.entity.ArmedModel;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;

public class ItemInHandLayer<T extends LivingEntity, M extends EntityModel> extends RenderLayer<T, M> {
    public ItemInHandLayer(final RenderLayerParent<T, M> dtr) {
        super(dtr);
    }
    
    @Override
    public void render(final T aix, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        final boolean boolean10 = aix.getMainArm() == HumanoidArm.RIGHT;
        final ItemStack bcj11 = boolean10 ? aix.getOffhandItem() : aix.getMainHandItem();
        final ItemStack bcj12 = boolean10 ? aix.getMainHandItem() : aix.getOffhandItem();
        if (bcj11.isEmpty() && bcj12.isEmpty()) {
            return;
        }
        GlStateManager.pushMatrix();
        if (((net.minecraft.client.model.EntityModel)this.getParentModel()).young) {
            final float float9 = 0.5f;
            GlStateManager.translatef(0.0f, 0.75f, 0.0f);
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
        }
        this.renderArmWithItem(aix, bcj12, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, HumanoidArm.RIGHT);
        this.renderArmWithItem(aix, bcj11, ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND, HumanoidArm.LEFT);
        GlStateManager.popMatrix();
    }
    
    private void renderArmWithItem(final LivingEntity aix, final ItemStack bcj, final ItemTransforms.TransformType b, final HumanoidArm aiw) {
        if (bcj.isEmpty()) {
            return;
        }
        GlStateManager.pushMatrix();
        this.translateToHand(aiw);
        if (aix.isVisuallySneaking()) {
            GlStateManager.translatef(0.0f, 0.2f, 0.0f);
        }
        GlStateManager.rotatef(-90.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
        final boolean boolean6 = aiw == HumanoidArm.LEFT;
        GlStateManager.translatef((boolean6 ? -1 : 1) / 16.0f, 0.125f, -0.625f);
        Minecraft.getInstance().getItemInHandRenderer().renderItem(aix, bcj, b, boolean6);
        GlStateManager.popMatrix();
    }
    
    protected void translateToHand(final HumanoidArm aiw) {
        this.getParentModel().translateToHand(0.0625f, aiw);
    }
    
    @Override
    public boolean colorsOnDamage() {
        return false;
    }
}
