package net.minecraft.client.renderer;

import java.util.Iterator;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import com.google.common.collect.Lists;
import net.minecraft.server.packs.resources.ResourceManager;
import com.mojang.math.Matrix4f;
import java.util.List;
import com.mojang.blaze3d.pipeline.RenderTarget;

public class PostPass implements AutoCloseable {
    private final EffectInstance effect;
    public final RenderTarget inTarget;
    public final RenderTarget outTarget;
    private final List<Object> auxAssets;
    private final List<String> auxNames;
    private final List<Integer> auxWidths;
    private final List<Integer> auxHeights;
    private Matrix4f shaderOrthoMatrix;
    
    public PostPass(final ResourceManager xi, final String string, final RenderTarget ctz3, final RenderTarget ctz4) throws IOException {
        this.auxAssets = (List<Object>)Lists.newArrayList();
        this.auxNames = (List<String>)Lists.newArrayList();
        this.auxWidths = (List<Integer>)Lists.newArrayList();
        this.auxHeights = (List<Integer>)Lists.newArrayList();
        this.effect = new EffectInstance(xi, string);
        this.inTarget = ctz3;
        this.outTarget = ctz4;
    }
    
    public void close() {
        this.effect.close();
    }
    
    public void addAuxAsset(final String string, final Object object, final int integer3, final int integer4) {
        this.auxNames.add(this.auxNames.size(), string);
        this.auxAssets.add(this.auxAssets.size(), object);
        this.auxWidths.add(this.auxWidths.size(), integer3);
        this.auxHeights.add(this.auxHeights.size(), integer4);
    }
    
    private void prepareState() {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
        GlStateManager.disableDepthTest();
        GlStateManager.disableAlphaTest();
        GlStateManager.disableFog();
        GlStateManager.disableLighting();
        GlStateManager.disableColorMaterial();
        GlStateManager.enableTexture();
        GlStateManager.bindTexture(0);
    }
    
    public void setOrthoMatrix(final Matrix4f blaze3D. {
        this.shaderOrthoMatrix = blaze3D.
    }
    
    public void process(final float float1) {
        this.prepareState();
        this.inTarget.unbindWrite();
        final float float2 = (float)this.outTarget.width;
        final float float3 = (float)this.outTarget.height;
        GlStateManager.viewport(0, 0, (int)float2, (int)float3);
        this.effect.setSampler("DiffuseSampler", this.inTarget);
        for (int integer5 = 0; integer5 < this.auxAssets.size(); ++integer5) {
            this.effect.setSampler((String)this.auxNames.get(integer5), this.auxAssets.get(integer5));
            this.effect.safeGetUniform(new StringBuilder().append("AuxSize").append(integer5).toString()).set((float)(int)this.auxWidths.get(integer5), (float)(int)this.auxHeights.get(integer5));
        }
        this.effect.safeGetUniform("ProjMat").set(this.shaderOrthoMatrix);
        this.effect.safeGetUniform("InSize").set((float)this.inTarget.width, (float)this.inTarget.height);
        this.effect.safeGetUniform("OutSize").set(float2, float3);
        this.effect.safeGetUniform("Time").set(float1);
        final Minecraft cyc5 = Minecraft.getInstance();
        this.effect.safeGetUniform("ScreenSize").set((float)cyc5.window.getWidth(), (float)cyc5.window.getHeight());
        this.effect.apply();
        this.outTarget.clear(Minecraft.ON_OSX);
        this.outTarget.bindWrite(false);
        GlStateManager.depthMask(false);
        GlStateManager.colorMask(true, true, true, true);
        final Tesselator cuz6 = Tesselator.getInstance();
        final BufferBuilder cuw7 = cuz6.getBuilder();
        cuw7.begin(7, DefaultVertexFormat.POSITION_COLOR);
        cuw7.vertex(0.0, 0.0, 500.0).color(255, 255, 255, 255).endVertex();
        cuw7.vertex(float2, 0.0, 500.0).color(255, 255, 255, 255).endVertex();
        cuw7.vertex(float2, float3, 500.0).color(255, 255, 255, 255).endVertex();
        cuw7.vertex(0.0, float3, 500.0).color(255, 255, 255, 255).endVertex();
        cuz6.end();
        GlStateManager.depthMask(true);
        GlStateManager.colorMask(true, true, true, true);
        this.effect.clear();
        this.outTarget.unbindWrite();
        this.inTarget.unbindRead();
        for (final Object object9 : this.auxAssets) {
            if (object9 instanceof RenderTarget) {
                ((RenderTarget)object9).unbindRead();
            }
        }
    }
    
    public EffectInstance getEffect() {
        return this.effect;
    }
}
