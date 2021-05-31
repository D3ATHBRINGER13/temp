package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;

public class CapeLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public CapeLayer(final RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> dtr) {
        super(dtr);
    }
    
    @Override
    public void render(final AbstractClientPlayer dmm, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (!dmm.isCapeLoaded() || dmm.isInvisible() || !dmm.isModelPartShown(PlayerModelPart.CAPE) || dmm.getCloakTextureLocation() == null) {
            return;
        }
        final ItemStack bcj10 = dmm.getItemBySlot(EquipmentSlot.CHEST);
        if (bcj10.getItem() == Items.ELYTRA) {
            return;
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.bindTexture(dmm.getCloakTextureLocation());
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, 0.0f, 0.125f);
        final double double11 = Mth.lerp(float4, dmm.xCloakO, dmm.xCloak) - Mth.lerp(float4, dmm.xo, dmm.x);
        final double double12 = Mth.lerp(float4, dmm.yCloakO, dmm.yCloak) - Mth.lerp(float4, dmm.yo, dmm.y);
        final double double13 = Mth.lerp(float4, dmm.zCloakO, dmm.zCloak) - Mth.lerp(float4, dmm.zo, dmm.z);
        final float float9 = dmm.yBodyRotO + (dmm.yBodyRot - dmm.yBodyRotO);
        final double double14 = Mth.sin(float9 * 0.017453292f);
        final double double15 = -Mth.cos(float9 * 0.017453292f);
        float float10 = (float)double12 * 10.0f;
        float10 = Mth.clamp(float10, -6.0f, 32.0f);
        float float11 = (float)(double11 * double14 + double13 * double15) * 100.0f;
        float11 = Mth.clamp(float11, 0.0f, 150.0f);
        float float12 = (float)(double11 * double15 - double13 * double14) * 100.0f;
        float12 = Mth.clamp(float12, -20.0f, 20.0f);
        if (float11 < 0.0f) {
            float11 = 0.0f;
        }
        final float float13 = Mth.lerp(float4, dmm.oBob, dmm.bob);
        float10 += Mth.sin(Mth.lerp(float4, dmm.walkDistO, dmm.walkDist) * 6.0f) * 32.0f * float13;
        if (dmm.isVisuallySneaking()) {
            float10 += 25.0f;
        }
        GlStateManager.rotatef(6.0f + float11 / 2.0f + float10, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(float12 / 2.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotatef(-float12 / 2.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
        ((RenderLayer<T, PlayerModel>)this).getParentModel().renderCloak(0.0625f);
        GlStateManager.popMatrix();
    }
    
    @Override
    public boolean colorsOnDamage() {
        return false;
    }
}
