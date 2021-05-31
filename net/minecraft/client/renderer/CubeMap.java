package net.minecraft.client.renderer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.client.renderer.texture.TextureManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class CubeMap {
    private final ResourceLocation[] images;
    
    public CubeMap(final ResourceLocation qv) {
        this.images = new ResourceLocation[6];
        for (int integer3 = 0; integer3 < 6; ++integer3) {
            this.images[integer3] = new ResourceLocation(qv.getNamespace(), qv.getPath() + '_' + integer3 + ".png");
        }
    }
    
    public void render(final Minecraft cyc, final float float2, final float float3, final float float4) {
        final Tesselator cuz6 = Tesselator.getInstance();
        final BufferBuilder cuw7 = cuz6.getBuilder();
        GlStateManager.matrixMode(5889);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.multMatrix(Matrix4f.perspective(85.0, cyc.window.getWidth() / (float)cyc.window.getHeight(), 0.05f, 10.0f));
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.enableBlend();
        GlStateManager.disableAlphaTest();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        final int integer8 = 2;
        for (int integer9 = 0; integer9 < 4; ++integer9) {
            GlStateManager.pushMatrix();
            final float float5 = (integer9 % 2 / 2.0f - 0.5f) / 256.0f;
            final float float6 = (integer9 / 2 / 2.0f - 0.5f) / 256.0f;
            final float float7 = 0.0f;
            GlStateManager.translatef(float5, float6, 0.0f);
            GlStateManager.rotatef(float2, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotatef(float3, 0.0f, 1.0f, 0.0f);
            for (int integer10 = 0; integer10 < 6; ++integer10) {
                cyc.getTextureManager().bind(this.images[integer10]);
                cuw7.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
                final int integer11 = Math.round(255.0f * float4) / (integer9 + 1);
                if (integer10 == 0) {
                    cuw7.vertex(-1.0, -1.0, 1.0).uv(0.0, 0.0).color(255, 255, 255, integer11).endVertex();
                    cuw7.vertex(-1.0, 1.0, 1.0).uv(0.0, 1.0).color(255, 255, 255, integer11).endVertex();
                    cuw7.vertex(1.0, 1.0, 1.0).uv(1.0, 1.0).color(255, 255, 255, integer11).endVertex();
                    cuw7.vertex(1.0, -1.0, 1.0).uv(1.0, 0.0).color(255, 255, 255, integer11).endVertex();
                }
                if (integer10 == 1) {
                    cuw7.vertex(1.0, -1.0, 1.0).uv(0.0, 0.0).color(255, 255, 255, integer11).endVertex();
                    cuw7.vertex(1.0, 1.0, 1.0).uv(0.0, 1.0).color(255, 255, 255, integer11).endVertex();
                    cuw7.vertex(1.0, 1.0, -1.0).uv(1.0, 1.0).color(255, 255, 255, integer11).endVertex();
                    cuw7.vertex(1.0, -1.0, -1.0).uv(1.0, 0.0).color(255, 255, 255, integer11).endVertex();
                }
                if (integer10 == 2) {
                    cuw7.vertex(1.0, -1.0, -1.0).uv(0.0, 0.0).color(255, 255, 255, integer11).endVertex();
                    cuw7.vertex(1.0, 1.0, -1.0).uv(0.0, 1.0).color(255, 255, 255, integer11).endVertex();
                    cuw7.vertex(-1.0, 1.0, -1.0).uv(1.0, 1.0).color(255, 255, 255, integer11).endVertex();
                    cuw7.vertex(-1.0, -1.0, -1.0).uv(1.0, 0.0).color(255, 255, 255, integer11).endVertex();
                }
                if (integer10 == 3) {
                    cuw7.vertex(-1.0, -1.0, -1.0).uv(0.0, 0.0).color(255, 255, 255, integer11).endVertex();
                    cuw7.vertex(-1.0, 1.0, -1.0).uv(0.0, 1.0).color(255, 255, 255, integer11).endVertex();
                    cuw7.vertex(-1.0, 1.0, 1.0).uv(1.0, 1.0).color(255, 255, 255, integer11).endVertex();
                    cuw7.vertex(-1.0, -1.0, 1.0).uv(1.0, 0.0).color(255, 255, 255, integer11).endVertex();
                }
                if (integer10 == 4) {
                    cuw7.vertex(-1.0, -1.0, -1.0).uv(0.0, 0.0).color(255, 255, 255, integer11).endVertex();
                    cuw7.vertex(-1.0, -1.0, 1.0).uv(0.0, 1.0).color(255, 255, 255, integer11).endVertex();
                    cuw7.vertex(1.0, -1.0, 1.0).uv(1.0, 1.0).color(255, 255, 255, integer11).endVertex();
                    cuw7.vertex(1.0, -1.0, -1.0).uv(1.0, 0.0).color(255, 255, 255, integer11).endVertex();
                }
                if (integer10 == 5) {
                    cuw7.vertex(-1.0, 1.0, 1.0).uv(0.0, 0.0).color(255, 255, 255, integer11).endVertex();
                    cuw7.vertex(-1.0, 1.0, -1.0).uv(0.0, 1.0).color(255, 255, 255, integer11).endVertex();
                    cuw7.vertex(1.0, 1.0, -1.0).uv(1.0, 1.0).color(255, 255, 255, integer11).endVertex();
                    cuw7.vertex(1.0, 1.0, 1.0).uv(1.0, 0.0).color(255, 255, 255, integer11).endVertex();
                }
                cuz6.end();
            }
            GlStateManager.popMatrix();
            GlStateManager.colorMask(true, true, true, false);
        }
        cuw7.offset(0.0, 0.0, 0.0);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.matrixMode(5889);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableDepthTest();
    }
    
    public CompletableFuture<Void> preload(final TextureManager dxc, final Executor executor) {
        final CompletableFuture<?>[] arr4 = new CompletableFuture[6];
        for (int integer5 = 0; integer5 < arr4.length; ++integer5) {
            arr4[integer5] = dxc.preload(this.images[integer5], executor);
        }
        return (CompletableFuture<Void>)CompletableFuture.allOf((CompletableFuture[])arr4);
    }
}
