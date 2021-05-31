package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.vehicle.MinecartTNT;

public class TntMinecartRenderer extends MinecartRenderer<MinecartTNT> {
    public TntMinecartRenderer(final EntityRenderDispatcher dsa) {
        super(dsa);
    }
    
    @Override
    protected void renderMinecartContents(final MinecartTNT ayd, final float float2, final BlockState bvt) {
        final int integer5 = ayd.getFuse();
        if (integer5 > -1 && integer5 - float2 + 1.0f < 10.0f) {
            float float3 = 1.0f - (integer5 - float2 + 1.0f) / 10.0f;
            float3 = Mth.clamp(float3, 0.0f, 1.0f);
            float3 *= float3;
            float3 *= float3;
            final float float4 = 1.0f + float3 * 0.3f;
            GlStateManager.scalef(float4, float4, float4);
        }
        super.renderMinecartContents(ayd, float2, bvt);
        if (integer5 > -1 && integer5 / 5 % 2 == 0) {
            final BlockRenderDispatcher dnw6 = Minecraft.getInstance().getBlockRenderer();
            GlStateManager.disableTexture();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, (1.0f - (integer5 - float2 + 1.0f) / 100.0f) * 0.8f);
            GlStateManager.pushMatrix();
            dnw6.renderSingleBlock(Blocks.TNT.defaultBlockState(), 1.0f);
            GlStateManager.popMatrix();
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture();
        }
    }
}
