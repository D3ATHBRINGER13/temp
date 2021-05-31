package net.minecraft.client.renderer.debug;

import java.util.Iterator;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.Vec3i;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import net.minecraft.core.BlockPos;
import java.util.Map;
import net.minecraft.client.Minecraft;

public class CaveDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private final Map<BlockPos, BlockPos> tunnelsList;
    private final Map<BlockPos, Float> thicknessMap;
    private final List<BlockPos> startPoses;
    
    public CaveDebugRenderer(final Minecraft cyc) {
        this.tunnelsList = (Map<BlockPos, BlockPos>)Maps.newHashMap();
        this.thicknessMap = (Map<BlockPos, Float>)Maps.newHashMap();
        this.startPoses = (List<BlockPos>)Lists.newArrayList();
        this.minecraft = cyc;
    }
    
    public void addTunnel(final BlockPos ew, final List<BlockPos> list2, final List<Float> list3) {
        for (int integer5 = 0; integer5 < list2.size(); ++integer5) {
            this.tunnelsList.put(list2.get(integer5), ew);
            this.thicknessMap.put(list2.get(integer5), list3.get(integer5));
        }
        this.startPoses.add(ew);
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
        final BlockPos ew11 = new BlockPos(cxq4.getPosition().x, 0.0, cxq4.getPosition().z);
        final Tesselator cuz12 = Tesselator.getInstance();
        final BufferBuilder cuw13 = cuz12.getBuilder();
        cuw13.begin(5, DefaultVertexFormat.POSITION_COLOR);
        for (final Map.Entry<BlockPos, BlockPos> entry15 : this.tunnelsList.entrySet()) {
            final BlockPos ew12 = (BlockPos)entry15.getKey();
            final BlockPos ew13 = (BlockPos)entry15.getValue();
            final float float18 = ew13.getX() * 128 % 256 / 256.0f;
            final float float19 = ew13.getY() * 128 % 256 / 256.0f;
            final float float20 = ew13.getZ() * 128 % 256 / 256.0f;
            final float float21 = (float)this.thicknessMap.get(ew12);
            if (ew11.closerThan(ew12, 160.0)) {
                LevelRenderer.addChainedFilledBoxVertices(cuw13, ew12.getX() + 0.5f - double5 - float21, ew12.getY() + 0.5f - double6 - float21, ew12.getZ() + 0.5f - double7 - float21, ew12.getX() + 0.5f - double5 + float21, ew12.getY() + 0.5f - double6 + float21, ew12.getZ() + 0.5f - double7 + float21, float18, float19, float20, 0.5f);
            }
        }
        for (final BlockPos ew14 : this.startPoses) {
            if (ew11.closerThan(ew14, 160.0)) {
                LevelRenderer.addChainedFilledBoxVertices(cuw13, ew14.getX() - double5, ew14.getY() - double6, ew14.getZ() - double7, ew14.getX() + 1.0f - double5, ew14.getY() + 1.0f - double6, ew14.getZ() + 1.0f - double7, 1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
        cuz12.end();
        GlStateManager.enableDepthTest();
        GlStateManager.enableTexture();
        GlStateManager.popMatrix();
    }
}
