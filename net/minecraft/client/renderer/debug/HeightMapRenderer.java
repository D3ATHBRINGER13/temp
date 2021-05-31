package net.minecraft.client.renderer.debug;

import java.util.Iterator;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.level.levelgen.Heightmap;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.core.BlockPos;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;

public class HeightMapRenderer implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    
    public HeightMapRenderer(final Minecraft cyc) {
        this.minecraft = cyc;
    }
    
    public void render(final long long1) {
        final Camera cxq4 = this.minecraft.gameRenderer.getMainCamera();
        final LevelAccessor bhs5 = this.minecraft.level;
        final double double6 = cxq4.getPosition().x;
        final double double7 = cxq4.getPosition().y;
        final double double8 = cxq4.getPosition().z;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture();
        final BlockPos ew12 = new BlockPos(cxq4.getPosition().x, 0.0, cxq4.getPosition().z);
        final Tesselator cuz13 = Tesselator.getInstance();
        final BufferBuilder cuw14 = cuz13.getBuilder();
        cuw14.begin(5, DefaultVertexFormat.POSITION_COLOR);
        for (final BlockPos ew13 : BlockPos.betweenClosed(ew12.offset(-40, 0, -40), ew12.offset(40, 0, 40))) {
            final int integer17 = bhs5.getHeight(Heightmap.Types.WORLD_SURFACE_WG, ew13.getX(), ew13.getZ());
            if (bhs5.getBlockState(ew13.offset(0, integer17, 0).below()).isAir()) {
                LevelRenderer.addChainedFilledBoxVertices(cuw14, ew13.getX() + 0.25f - double6, integer17 - double7, ew13.getZ() + 0.25f - double8, ew13.getX() + 0.75f - double6, integer17 + 0.09375 - double7, ew13.getZ() + 0.75f - double8, 0.0f, 0.0f, 1.0f, 0.5f);
            }
            else {
                LevelRenderer.addChainedFilledBoxVertices(cuw14, ew13.getX() + 0.25f - double6, integer17 - double7, ew13.getZ() + 0.25f - double8, ew13.getX() + 0.75f - double6, integer17 + 0.09375 - double7, ew13.getZ() + 0.75f - double8, 0.0f, 1.0f, 0.0f, 0.5f);
            }
        }
        cuz13.end();
        GlStateManager.enableTexture();
        GlStateManager.popMatrix();
    }
}
