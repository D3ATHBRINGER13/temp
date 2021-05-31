package net.minecraft.client.renderer.debug;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.gui.Font;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.LevelRenderer;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.EntityHitResult;
import java.util.function.Predicate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.Minecraft;

public class DebugRenderer {
    public final PathfindingRenderer pathfindingRenderer;
    public final SimpleDebugRenderer waterDebugRenderer;
    public final SimpleDebugRenderer chunkBorderRenderer;
    public final SimpleDebugRenderer heightMapRenderer;
    public final SimpleDebugRenderer collisionBoxRenderer;
    public final SimpleDebugRenderer neighborsUpdateRenderer;
    public final CaveDebugRenderer caveRenderer;
    public final StructureRenderer structureRenderer;
    public final SimpleDebugRenderer lightDebugRenderer;
    public final SimpleDebugRenderer worldGenAttemptRenderer;
    public final SimpleDebugRenderer solidFaceRenderer;
    public final SimpleDebugRenderer chunkRenderer;
    public final VillageDebugRenderer villageDebugRenderer;
    public final RaidDebugRenderer raidDebugRenderer;
    public final GoalSelectorDebugRenderer goalSelectorRenderer;
    private boolean renderChunkborder;
    
    public DebugRenderer(final Minecraft cyc) {
        this.pathfindingRenderer = new PathfindingRenderer(cyc);
        this.waterDebugRenderer = new WaterDebugRenderer(cyc);
        this.chunkBorderRenderer = new ChunkBorderRenderer(cyc);
        this.heightMapRenderer = new HeightMapRenderer(cyc);
        this.collisionBoxRenderer = new CollisionBoxRenderer(cyc);
        this.neighborsUpdateRenderer = new NeighborsUpdateRenderer(cyc);
        this.caveRenderer = new CaveDebugRenderer(cyc);
        this.structureRenderer = new StructureRenderer(cyc);
        this.lightDebugRenderer = new LightDebugRenderer(cyc);
        this.worldGenAttemptRenderer = new WorldGenAttemptRenderer(cyc);
        this.solidFaceRenderer = new SolidFaceRenderer(cyc);
        this.chunkRenderer = new ChunkDebugRenderer(cyc);
        this.villageDebugRenderer = new VillageDebugRenderer(cyc);
        this.raidDebugRenderer = new RaidDebugRenderer(cyc);
        this.goalSelectorRenderer = new GoalSelectorDebugRenderer(cyc);
    }
    
    public void clear() {
        this.pathfindingRenderer.clear();
        this.waterDebugRenderer.clear();
        this.chunkBorderRenderer.clear();
        this.heightMapRenderer.clear();
        this.collisionBoxRenderer.clear();
        this.neighborsUpdateRenderer.clear();
        this.caveRenderer.clear();
        this.structureRenderer.clear();
        this.lightDebugRenderer.clear();
        this.worldGenAttemptRenderer.clear();
        this.solidFaceRenderer.clear();
        this.chunkRenderer.clear();
        this.villageDebugRenderer.clear();
        this.raidDebugRenderer.clear();
        this.goalSelectorRenderer.clear();
    }
    
    public boolean shouldRender() {
        return this.renderChunkborder;
    }
    
    public boolean switchRenderChunkborder() {
        return this.renderChunkborder = !this.renderChunkborder;
    }
    
    public void render(final long long1) {
        if (this.renderChunkborder && !Minecraft.getInstance().showOnlyReducedInfo()) {
            this.chunkBorderRenderer.render(long1);
        }
    }
    
    public static Optional<Entity> getTargetedEntity(@Nullable final Entity aio, final int integer) {
        if (aio == null) {
            return (Optional<Entity>)Optional.empty();
        }
        final Vec3 csi3 = aio.getEyePosition(1.0f);
        final Vec3 csi4 = aio.getViewVector(1.0f).scale(integer);
        final Vec3 csi5 = csi3.add(csi4);
        final AABB csc6 = aio.getBoundingBox().expandTowards(csi4).inflate(1.0);
        final int integer2 = integer * integer;
        final Predicate<Entity> predicate8 = (Predicate<Entity>)(aio -> !aio.isSpectator() && aio.isPickable());
        final EntityHitResult cse9 = ProjectileUtil.getEntityHitResult(aio, csi3, csi5, csc6, predicate8, integer2);
        if (cse9 == null) {
            return (Optional<Entity>)Optional.empty();
        }
        if (csi3.distanceToSqr(cse9.getLocation()) > integer2) {
            return (Optional<Entity>)Optional.empty();
        }
        return (Optional<Entity>)Optional.of(cse9.getEntity());
    }
    
    public static void renderFilledBox(final BlockPos ew1, final BlockPos ew2, final float float3, final float float4, final float float5, final float float6) {
        final Camera cxq7 = Minecraft.getInstance().gameRenderer.getMainCamera();
        if (!cxq7.isInitialized()) {
            return;
        }
        final Vec3 csi8 = cxq7.getPosition().reverse();
        final AABB csc9 = new AABB(ew1, ew2).move(csi8);
        renderFilledBox(csc9, float3, float4, float5, float6);
    }
    
    public static void renderFilledBox(final BlockPos ew, final float float2, final float float3, final float float4, final float float5, final float float6) {
        final Camera cxq7 = Minecraft.getInstance().gameRenderer.getMainCamera();
        if (!cxq7.isInitialized()) {
            return;
        }
        final Vec3 csi8 = cxq7.getPosition().reverse();
        final AABB csc9 = new AABB(ew).move(csi8).inflate(float2);
        renderFilledBox(csc9, float3, float4, float5, float6);
    }
    
    public static void renderFilledBox(final AABB csc, final float float2, final float float3, final float float4, final float float5) {
        renderFilledBox(csc.minX, csc.minY, csc.minZ, csc.maxX, csc.maxY, csc.maxZ, float2, float3, float4, float5);
    }
    
    public static void renderFilledBox(final double double1, final double double2, final double double3, final double double4, final double double5, final double double6, final float float7, final float float8, final float float9, final float float10) {
        final Tesselator cuz17 = Tesselator.getInstance();
        final BufferBuilder cuw18 = cuz17.getBuilder();
        cuw18.begin(5, DefaultVertexFormat.POSITION_COLOR);
        LevelRenderer.addChainedFilledBoxVertices(cuw18, double1, double2, double3, double4, double5, double6, float7, float8, float9, float10);
        cuz17.end();
    }
    
    public static void renderFloatingText(final String string, final int integer2, final int integer3, final int integer4, final int integer5) {
        renderFloatingText(string, integer2 + 0.5, integer3 + 0.5, integer4 + 0.5, integer5);
    }
    
    public static void renderFloatingText(final String string, final double double2, final double double3, final double double4, final int integer) {
        renderFloatingText(string, double2, double3, double4, integer, 0.02f);
    }
    
    public static void renderFloatingText(final String string, final double double2, final double double3, final double double4, final int integer, final float float6) {
        renderFloatingText(string, double2, double3, double4, integer, float6, true, 0.0f, false);
    }
    
    public static void renderFloatingText(final String string, final double double2, final double double3, final double double4, final int integer, final float float6, final boolean boolean7, final float float8, final boolean boolean9) {
        final Minecraft cyc13 = Minecraft.getInstance();
        final Camera cxq14 = cyc13.gameRenderer.getMainCamera();
        if (!cxq14.isInitialized() || cyc13.getEntityRenderDispatcher().options == null) {
            return;
        }
        final Font cyu15 = cyc13.font;
        final double double5 = cxq14.getPosition().x;
        final double double6 = cxq14.getPosition().y;
        final double double7 = cxq14.getPosition().z;
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)(double2 - double5), (float)(double3 - double6) + 0.07f, (float)(double4 - double7));
        GlStateManager.normal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.scalef(float6, -float6, float6);
        final EntityRenderDispatcher dsa22 = cyc13.getEntityRenderDispatcher();
        GlStateManager.rotatef(-dsa22.playerRotY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(-dsa22.playerRotX, 1.0f, 0.0f, 0.0f);
        GlStateManager.enableTexture();
        if (boolean9) {
            GlStateManager.disableDepthTest();
        }
        else {
            GlStateManager.enableDepthTest();
        }
        GlStateManager.depthMask(true);
        GlStateManager.scalef(-1.0f, 1.0f, 1.0f);
        float float9 = boolean7 ? (-cyu15.width(string) / 2.0f) : 0.0f;
        float9 -= float8 / float6;
        cyu15.draw(string, float9, 0.0f, integer);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableDepthTest();
        GlStateManager.popMatrix();
    }
    
    public interface SimpleDebugRenderer {
        void render(final long long1);
        
        default void clear() {
        }
    }
}
