package net.minecraft.client.renderer.debug;

import java.util.List;
import java.util.Iterator;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.core.BlockPos;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.collect.Maps;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.dimension.DimensionType;
import java.util.Map;
import net.minecraft.client.Minecraft;

public class StructureRenderer implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private final Map<DimensionType, Map<String, BoundingBox>> postMainBoxes;
    private final Map<DimensionType, Map<String, BoundingBox>> postPiecesBoxes;
    private final Map<DimensionType, Map<String, Boolean>> startPiecesMap;
    
    public StructureRenderer(final Minecraft cyc) {
        this.postMainBoxes = (Map<DimensionType, Map<String, BoundingBox>>)Maps.newIdentityHashMap();
        this.postPiecesBoxes = (Map<DimensionType, Map<String, BoundingBox>>)Maps.newIdentityHashMap();
        this.startPiecesMap = (Map<DimensionType, Map<String, Boolean>>)Maps.newIdentityHashMap();
        this.minecraft = cyc;
    }
    
    public void render(final long long1) {
        final Camera cxq4 = this.minecraft.gameRenderer.getMainCamera();
        final LevelAccessor bhs5 = this.minecraft.level;
        final DimensionType byn6 = bhs5.getDimension().getType();
        final double double7 = cxq4.getPosition().x;
        final double double8 = cxq4.getPosition().y;
        final double double9 = cxq4.getPosition().z;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture();
        GlStateManager.disableDepthTest();
        final BlockPos ew13 = new BlockPos(cxq4.getPosition().x, 0.0, cxq4.getPosition().z);
        final Tesselator cuz14 = Tesselator.getInstance();
        final BufferBuilder cuw15 = cuz14.getBuilder();
        cuw15.begin(3, DefaultVertexFormat.POSITION_COLOR);
        GlStateManager.lineWidth(1.0f);
        if (this.postMainBoxes.containsKey(byn6)) {
            for (final BoundingBox cic17 : ((Map)this.postMainBoxes.get(byn6)).values()) {
                if (ew13.closerThan(cic17.getCenter(), 500.0)) {
                    LevelRenderer.addChainedLineBoxVertices(cuw15, cic17.x0 - double7, cic17.y0 - double8, cic17.z0 - double9, cic17.x1 + 1 - double7, cic17.y1 + 1 - double8, cic17.z1 + 1 - double9, 1.0f, 1.0f, 1.0f, 1.0f);
                }
            }
        }
        if (this.postPiecesBoxes.containsKey(byn6)) {
            for (final Map.Entry<String, BoundingBox> entry17 : ((Map)this.postPiecesBoxes.get(byn6)).entrySet()) {
                final String string18 = (String)entry17.getKey();
                final BoundingBox cic18 = (BoundingBox)entry17.getValue();
                final Boolean boolean20 = (Boolean)((Map)this.startPiecesMap.get(byn6)).get(string18);
                if (ew13.closerThan(cic18.getCenter(), 500.0)) {
                    if (boolean20) {
                        LevelRenderer.addChainedLineBoxVertices(cuw15, cic18.x0 - double7, cic18.y0 - double8, cic18.z0 - double9, cic18.x1 + 1 - double7, cic18.y1 + 1 - double8, cic18.z1 + 1 - double9, 0.0f, 1.0f, 0.0f, 1.0f);
                    }
                    else {
                        LevelRenderer.addChainedLineBoxVertices(cuw15, cic18.x0 - double7, cic18.y0 - double8, cic18.z0 - double9, cic18.x1 + 1 - double7, cic18.y1 + 1 - double8, cic18.z1 + 1 - double9, 0.0f, 0.0f, 1.0f, 1.0f);
                    }
                }
            }
        }
        cuz14.end();
        GlStateManager.enableDepthTest();
        GlStateManager.enableTexture();
        GlStateManager.popMatrix();
    }
    
    public void addBoundingBox(final BoundingBox cic, final List<BoundingBox> list2, final List<Boolean> list3, final DimensionType byn) {
        if (!this.postMainBoxes.containsKey(byn)) {
            this.postMainBoxes.put(byn, Maps.newHashMap());
        }
        if (!this.postPiecesBoxes.containsKey(byn)) {
            this.postPiecesBoxes.put(byn, Maps.newHashMap());
            this.startPiecesMap.put(byn, Maps.newHashMap());
        }
        ((Map)this.postMainBoxes.get(byn)).put(cic.toString(), cic);
        for (int integer6 = 0; integer6 < list2.size(); ++integer6) {
            final BoundingBox cic2 = (BoundingBox)list2.get(integer6);
            final Boolean boolean8 = (Boolean)list3.get(integer6);
            ((Map)this.postPiecesBoxes.get(byn)).put(cic2.toString(), cic2);
            ((Map)this.startPiecesMap.get(byn)).put(cic2.toString(), boolean8);
        }
    }
    
    public void clear() {
        this.postMainBoxes.clear();
        this.postPiecesBoxes.clear();
        this.startPiecesMap.clear();
    }
}
