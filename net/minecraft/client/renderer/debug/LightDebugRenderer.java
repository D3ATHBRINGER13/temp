package net.minecraft.client.renderer.debug;

import java.util.Iterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.world.level.Level;
import net.minecraft.client.Camera;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;

public class LightDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    
    public LightDebugRenderer(final Minecraft cyc) {
        this.minecraft = cyc;
    }
    
    public void render(final long long1) {
        final Camera cxq4 = this.minecraft.gameRenderer.getMainCamera();
        final Level bhr5 = this.minecraft.level;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture();
        final BlockPos ew6 = new BlockPos(cxq4.getPosition());
        final LongSet longSet7 = (LongSet)new LongOpenHashSet();
        for (final BlockPos ew7 : BlockPos.betweenClosed(ew6.offset(-10, -10, -10), ew6.offset(10, 10, 10))) {
            final int integer10 = bhr5.getBrightness(LightLayer.SKY, ew7);
            final float float11 = (15 - integer10) / 15.0f * 0.5f + 0.16f;
            final int integer11 = Mth.hsvToRgb(float11, 0.9f, 0.9f);
            final long long2 = SectionPos.blockToSection(ew7.asLong());
            if (longSet7.add(long2)) {
                DebugRenderer.renderFloatingText(bhr5.getChunkSource().getLightEngine().getDebugData(LightLayer.SKY, SectionPos.of(long2)), SectionPos.x(long2) * 16 + 8, SectionPos.y(long2) * 16 + 8, SectionPos.z(long2) * 16 + 8, 16711680, 0.3f);
            }
            if (integer10 != 15) {
                DebugRenderer.renderFloatingText(String.valueOf(integer10), ew7.getX() + 0.5, ew7.getY() + 0.25, ew7.getZ() + 0.5, integer11);
            }
        }
        GlStateManager.enableTexture();
        GlStateManager.popMatrix();
    }
}
