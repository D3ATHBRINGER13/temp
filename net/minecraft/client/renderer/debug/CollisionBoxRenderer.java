package net.minecraft.client.renderer.debug;

import java.util.Iterator;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.stream.Collectors;
import net.minecraft.world.entity.Entity;
import java.util.Set;
import net.minecraft.Util;
import java.util.Collections;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.List;
import net.minecraft.client.Minecraft;

public class CollisionBoxRenderer implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private double lastUpdateTime;
    private List<VoxelShape> shapes;
    
    public CollisionBoxRenderer(final Minecraft cyc) {
        this.lastUpdateTime = Double.MIN_VALUE;
        this.shapes = (List<VoxelShape>)Collections.emptyList();
        this.minecraft = cyc;
    }
    
    public void render(final long long1) {
        final Camera cxq4 = this.minecraft.gameRenderer.getMainCamera();
        final double double5 = (double)Util.getNanos();
        if (double5 - this.lastUpdateTime > 1.0E8) {
            this.lastUpdateTime = double5;
            this.shapes = (List<VoxelShape>)cxq4.getEntity().level.getCollisions(cxq4.getEntity(), cxq4.getEntity().getBoundingBox().inflate(6.0), (Set<Entity>)Collections.emptySet()).collect(Collectors.toList());
        }
        final double double6 = cxq4.getPosition().x;
        final double double7 = cxq4.getPosition().y;
        final double double8 = cxq4.getPosition().z;
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.lineWidth(2.0f);
        GlStateManager.disableTexture();
        GlStateManager.depthMask(false);
        for (final VoxelShape ctc14 : this.shapes) {
            LevelRenderer.renderVoxelShape(ctc14, -double6, -double7, -double8, 1.0f, 1.0f, 1.0f, 1.0f);
        }
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }
}
