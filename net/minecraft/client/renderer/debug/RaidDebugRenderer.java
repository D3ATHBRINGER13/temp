package net.minecraft.client.renderer.debug;

import net.minecraft.client.Camera;
import java.util.Iterator;
import net.minecraft.core.Vec3i;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import java.util.Collection;
import net.minecraft.client.Minecraft;

public class RaidDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private Collection<BlockPos> raidCenters;
    
    public RaidDebugRenderer(final Minecraft cyc) {
        this.raidCenters = (Collection<BlockPos>)Lists.newArrayList();
        this.minecraft = cyc;
    }
    
    public void setRaidCenters(final Collection<BlockPos> collection) {
        this.raidCenters = collection;
    }
    
    public void render(final long long1) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture();
        this.doRender();
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
    
    private void doRender() {
        final BlockPos ew2 = this.getCamera().getBlockPosition();
        for (final BlockPos ew3 : this.raidCenters) {
            if (ew2.closerThan(ew3, 160.0)) {
                highlightRaidCenter(ew3);
            }
        }
    }
    
    private static void highlightRaidCenter(final BlockPos ew) {
        DebugRenderer.renderFilledBox(ew.offset(-0.5, -0.5, -0.5), ew.offset(1.5, 1.5, 1.5), 1.0f, 0.0f, 0.0f, 0.15f);
        final int integer2 = -65536;
        renderTextOverBlock("Raid center", ew, -65536);
    }
    
    private static void renderTextOverBlock(final String string, final BlockPos ew, final int integer) {
        final double double4 = ew.getX() + 0.5;
        final double double5 = ew.getY() + 1.3;
        final double double6 = ew.getZ() + 0.5;
        DebugRenderer.renderFloatingText(string, double4, double5, double6, integer, 0.04f, true, 0.0f, true);
    }
    
    private Camera getCamera() {
        return this.minecraft.gameRenderer.getMainCamera();
    }
}
