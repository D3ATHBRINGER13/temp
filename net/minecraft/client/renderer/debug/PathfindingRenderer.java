package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.core.BlockPos;
import java.util.Locale;
import net.minecraft.world.phys.AABB;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Camera;
import java.util.Iterator;
import net.minecraft.Util;
import com.google.common.collect.Maps;
import net.minecraft.world.level.pathfinder.Path;
import java.util.Map;
import net.minecraft.client.Minecraft;

public class PathfindingRenderer implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private final Map<Integer, Path> pathMap;
    private final Map<Integer, Float> pathMaxDist;
    private final Map<Integer, Long> creationMap;
    
    public PathfindingRenderer(final Minecraft cyc) {
        this.pathMap = (Map<Integer, Path>)Maps.newHashMap();
        this.pathMaxDist = (Map<Integer, Float>)Maps.newHashMap();
        this.creationMap = (Map<Integer, Long>)Maps.newHashMap();
        this.minecraft = cyc;
    }
    
    public void addPath(final int integer, final Path cnr, final float float3) {
        this.pathMap.put(integer, cnr);
        this.creationMap.put(integer, Util.getMillis());
        this.pathMaxDist.put(integer, float3);
    }
    
    public void render(final long long1) {
        if (this.pathMap.isEmpty()) {
            return;
        }
        final long long2 = Util.getMillis();
        for (final Integer integer7 : this.pathMap.keySet()) {
            final Path cnr8 = (Path)this.pathMap.get(integer7);
            final float float9 = (float)this.pathMaxDist.get(integer7);
            renderPath(this.getCamera(), cnr8, float9, true, true);
        }
        for (final Integer integer8 : (Integer[])this.creationMap.keySet().toArray((Object[])new Integer[0])) {
            if (long2 - (long)this.creationMap.get(integer8) > 20000L) {
                this.pathMap.remove(integer8);
                this.creationMap.remove(integer8);
            }
        }
    }
    
    public static void renderPath(final Camera cxq, final Path cnr, final float float3, final boolean boolean4, final boolean boolean5) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color4f(0.0f, 1.0f, 0.0f, 0.75f);
        GlStateManager.disableTexture();
        GlStateManager.lineWidth(6.0f);
        doRenderPath(cxq, cnr, float3, boolean4, boolean5);
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
    
    private static void doRenderPath(final Camera cxq, final Path cnr, final float float3, final boolean boolean4, final boolean boolean5) {
        renderPathLine(cxq, cnr);
        final double double6 = cxq.getPosition().x;
        final double double7 = cxq.getPosition().y;
        final double double8 = cxq.getPosition().z;
        final BlockPos ew12 = cnr.getTarget();
        if (distanceToCamera(cxq, ew12) <= 40.0f) {
            DebugRenderer.renderFilledBox(new AABB(ew12.getX() + 0.25f, ew12.getY() + 0.25f, ew12.getZ() + 0.25, ew12.getX() + 0.75f, ew12.getY() + 0.75f, ew12.getZ() + 0.75f).move(-double6, -double7, -double8), 0.0f, 1.0f, 0.0f, 0.5f);
            for (int integer13 = 0; integer13 < cnr.getSize(); ++integer13) {
                final Node cnp14 = cnr.get(integer13);
                if (distanceToCamera(cxq, cnp14.asBlockPos()) <= 40.0f) {
                    final float float4 = (integer13 == cnr.getIndex()) ? 1.0f : 0.0f;
                    final float float5 = (integer13 == cnr.getIndex()) ? 0.0f : 1.0f;
                    DebugRenderer.renderFilledBox(new AABB(cnp14.x + 0.5f - float3, cnp14.y + 0.01f * integer13, cnp14.z + 0.5f - float3, cnp14.x + 0.5f + float3, cnp14.y + 0.25f + 0.01f * integer13, cnp14.z + 0.5f + float3).move(-double6, -double7, -double8), float4, 0.0f, float5, 0.5f);
                }
            }
        }
        if (boolean4) {
            for (final Node cnp15 : cnr.getClosedSet()) {
                if (distanceToCamera(cxq, cnp15.asBlockPos()) <= 40.0f) {
                    DebugRenderer.renderFloatingText(String.format("%s", new Object[] { cnp15.type }), cnp15.x + 0.5, cnp15.y + 0.75, cnp15.z + 0.5, -65536);
                    DebugRenderer.renderFloatingText(String.format(Locale.ROOT, "%.2f", new Object[] { cnp15.costMalus }), cnp15.x + 0.5, cnp15.y + 0.25, cnp15.z + 0.5, -65536);
                }
            }
            for (final Node cnp15 : cnr.getOpenSet()) {
                if (distanceToCamera(cxq, cnp15.asBlockPos()) <= 40.0f) {
                    DebugRenderer.renderFloatingText(String.format("%s", new Object[] { cnp15.type }), cnp15.x + 0.5, cnp15.y + 0.75, cnp15.z + 0.5, -16776961);
                    DebugRenderer.renderFloatingText(String.format(Locale.ROOT, "%.2f", new Object[] { cnp15.costMalus }), cnp15.x + 0.5, cnp15.y + 0.25, cnp15.z + 0.5, -16776961);
                }
            }
        }
        if (boolean5) {
            for (int integer13 = 0; integer13 < cnr.getSize(); ++integer13) {
                final Node cnp14 = cnr.get(integer13);
                if (distanceToCamera(cxq, cnp14.asBlockPos()) <= 40.0f) {
                    DebugRenderer.renderFloatingText(String.format("%s", new Object[] { cnp14.type }), cnp14.x + 0.5, cnp14.y + 0.75, cnp14.z + 0.5, -1);
                    DebugRenderer.renderFloatingText(String.format(Locale.ROOT, "%.2f", new Object[] { cnp14.costMalus }), cnp14.x + 0.5, cnp14.y + 0.25, cnp14.z + 0.5, -1);
                }
            }
        }
    }
    
    public static void renderPathLine(final Camera cxq, final Path cnr) {
        final Tesselator cuz3 = Tesselator.getInstance();
        final BufferBuilder cuw4 = cuz3.getBuilder();
        final double double5 = cxq.getPosition().x;
        final double double6 = cxq.getPosition().y;
        final double double7 = cxq.getPosition().z;
        cuw4.begin(3, DefaultVertexFormat.POSITION_COLOR);
        for (int integer11 = 0; integer11 < cnr.getSize(); ++integer11) {
            final Node cnp12 = cnr.get(integer11);
            if (distanceToCamera(cxq, cnp12.asBlockPos()) <= 40.0f) {
                final float float13 = integer11 / (float)cnr.getSize() * 0.33f;
                final int integer12 = (integer11 == 0) ? 0 : Mth.hsvToRgb(float13, 0.9f, 0.9f);
                final int integer13 = integer12 >> 16 & 0xFF;
                final int integer14 = integer12 >> 8 & 0xFF;
                final int integer15 = integer12 & 0xFF;
                cuw4.vertex(cnp12.x - double5 + 0.5, cnp12.y - double6 + 0.5, cnp12.z - double7 + 0.5).color(integer13, integer14, integer15, 255).endVertex();
            }
        }
        cuz3.end();
    }
    
    private static float distanceToCamera(final Camera cxq, final BlockPos ew) {
        return (float)(Math.abs(ew.getX() - cxq.getPosition().x) + Math.abs(ew.getY() - cxq.getPosition().y) + Math.abs(ew.getZ() - cxq.getPosition().z));
    }
    
    private Camera getCamera() {
        return this.minecraft.gameRenderer.getMainCamera();
    }
}
