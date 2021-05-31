package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import java.util.List;
import net.minecraft.client.Minecraft;

public class WorldGenAttemptRenderer implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private final List<BlockPos> toRender;
    private final List<Float> scales;
    private final List<Float> alphas;
    private final List<Float> reds;
    private final List<Float> greens;
    private final List<Float> blues;
    
    public WorldGenAttemptRenderer(final Minecraft cyc) {
        this.toRender = (List<BlockPos>)Lists.newArrayList();
        this.scales = (List<Float>)Lists.newArrayList();
        this.alphas = (List<Float>)Lists.newArrayList();
        this.reds = (List<Float>)Lists.newArrayList();
        this.greens = (List<Float>)Lists.newArrayList();
        this.blues = (List<Float>)Lists.newArrayList();
        this.minecraft = cyc;
    }
    
    public void addPos(final BlockPos ew, final float float2, final float float3, final float float4, final float float5, final float float6) {
        this.toRender.add(ew);
        this.scales.add(float2);
        this.alphas.add(float6);
        this.reds.add(float3);
        this.greens.add(float4);
        this.blues.add(float5);
    }
    
    public void render(final long long1) {
        final Camera cxq4 = this.minecraft.gameRenderer.getMainCamera();
        final double double5 = cxq4.getPosition().x;
        final double double6 = cxq4.getPosition().y;
        final double double7 = cxq4.getPosition().z;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture();
        final Tesselator cuz11 = Tesselator.getInstance();
        final BufferBuilder cuw12 = cuz11.getBuilder();
        cuw12.begin(5, DefaultVertexFormat.POSITION_COLOR);
        for (int integer13 = 0; integer13 < this.toRender.size(); ++integer13) {
            final BlockPos ew14 = (BlockPos)this.toRender.get(integer13);
            final Float float15 = (Float)this.scales.get(integer13);
            final float float16 = float15 / 2.0f;
            LevelRenderer.addChainedFilledBoxVertices(cuw12, ew14.getX() + 0.5f - float16 - double5, ew14.getY() + 0.5f - float16 - double6, ew14.getZ() + 0.5f - float16 - double7, ew14.getX() + 0.5f + float16 - double5, ew14.getY() + 0.5f + float16 - double6, ew14.getZ() + 0.5f + float16 - double7, (float)this.reds.get(integer13), (float)this.greens.get(integer13), (float)this.blues.get(integer13), (float)this.alphas.get(integer13));
        }
        cuz11.end();
        GlStateManager.enableTexture();
        GlStateManager.popMatrix();
    }
}
