package net.minecraft.client.renderer.debug;

import net.minecraft.world.level.material.FluidState;
import java.util.Iterator;
import net.minecraft.world.level.LevelReader;
import net.minecraft.client.Camera;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.BlockPos;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;

public class WaterDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    
    public WaterDebugRenderer(final Minecraft cyc) {
        this.minecraft = cyc;
    }
    
    public void render(final long long1) {
        final Camera cxq4 = this.minecraft.gameRenderer.getMainCamera();
        final double double5 = cxq4.getPosition().x;
        final double double6 = cxq4.getPosition().y;
        final double double7 = cxq4.getPosition().z;
        final BlockPos ew11 = this.minecraft.player.getCommandSenderBlockPosition();
        final LevelReader bhu12 = this.minecraft.player.level;
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color4f(0.0f, 1.0f, 0.0f, 0.75f);
        GlStateManager.disableTexture();
        GlStateManager.lineWidth(6.0f);
        for (final BlockPos ew12 : BlockPos.betweenClosed(ew11.offset(-10, -10, -10), ew11.offset(10, 10, 10))) {
            final FluidState clk15 = bhu12.getFluidState(ew12);
            if (clk15.is(FluidTags.WATER)) {
                final double double8 = ew12.getY() + clk15.getHeight(bhu12, ew12);
                DebugRenderer.renderFilledBox(new AABB(ew12.getX() + 0.01f, ew12.getY() + 0.01f, ew12.getZ() + 0.01f, ew12.getX() + 0.99f, double8, ew12.getZ() + 0.99f).move(-double5, -double6, -double7), 1.0f, 1.0f, 1.0f, 0.2f);
            }
        }
        for (final BlockPos ew12 : BlockPos.betweenClosed(ew11.offset(-10, -10, -10), ew11.offset(10, 10, 10))) {
            final FluidState clk15 = bhu12.getFluidState(ew12);
            if (clk15.is(FluidTags.WATER)) {
                DebugRenderer.renderFloatingText(String.valueOf(clk15.getAmount()), ew12.getX() + 0.5, ew12.getY() + clk15.getHeight(bhu12, ew12), ew12.getZ() + 0.5, -16777216);
            }
        }
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }
}
