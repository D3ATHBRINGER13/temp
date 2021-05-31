package net.minecraft.client.renderer.blockentity;

import net.minecraft.world.level.block.entity.BlockEntity;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Direction;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.MemoryTracker;
import java.nio.FloatBuffer;
import java.util.Random;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;

public class TheEndPortalRenderer extends BlockEntityRenderer<TheEndPortalBlockEntity> {
    private static final ResourceLocation END_SKY_LOCATION;
    private static final ResourceLocation END_PORTAL_LOCATION;
    private static final Random RANDOM;
    private static final FloatBuffer MODELVIEW;
    private static final FloatBuffer PROJECTION;
    private final FloatBuffer buffer;
    
    public TheEndPortalRenderer() {
        this.buffer = MemoryTracker.createFloatBuffer(16);
    }
    
    @Override
    public void render(final TheEndPortalBlockEntity buy, final double double2, final double double3, final double double4, final float float5, final int integer) {
        GlStateManager.disableLighting();
        TheEndPortalRenderer.RANDOM.setSeed(31100L);
        GlStateManager.getMatrix(2982, TheEndPortalRenderer.MODELVIEW);
        GlStateManager.getMatrix(2983, TheEndPortalRenderer.PROJECTION);
        final double double5 = double2 * double2 + double3 * double3 + double4 * double4;
        final int integer2 = this.getPasses(double5);
        final float float6 = this.getOffset();
        boolean boolean15 = false;
        final GameRenderer dnc16 = Minecraft.getInstance().gameRenderer;
        for (int integer3 = 0; integer3 < integer2; ++integer3) {
            GlStateManager.pushMatrix();
            float float7 = 2.0f / (18 - integer3);
            if (integer3 == 0) {
                this.bindTexture(TheEndPortalRenderer.END_SKY_LOCATION);
                float7 = 0.15f;
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            }
            if (integer3 >= 1) {
                this.bindTexture(TheEndPortalRenderer.END_PORTAL_LOCATION);
                boolean15 = true;
                dnc16.resetFogColor(true);
            }
            if (integer3 == 1) {
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            }
            GlStateManager.texGenMode(GlStateManager.TexGen.S, 9216);
            GlStateManager.texGenMode(GlStateManager.TexGen.T, 9216);
            GlStateManager.texGenMode(GlStateManager.TexGen.R, 9216);
            GlStateManager.texGenParam(GlStateManager.TexGen.S, 9474, this.getBuffer(1.0f, 0.0f, 0.0f, 0.0f));
            GlStateManager.texGenParam(GlStateManager.TexGen.T, 9474, this.getBuffer(0.0f, 1.0f, 0.0f, 0.0f));
            GlStateManager.texGenParam(GlStateManager.TexGen.R, 9474, this.getBuffer(0.0f, 0.0f, 1.0f, 0.0f));
            GlStateManager.enableTexGen(GlStateManager.TexGen.S);
            GlStateManager.enableTexGen(GlStateManager.TexGen.T);
            GlStateManager.enableTexGen(GlStateManager.TexGen.R);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            GlStateManager.translatef(0.5f, 0.5f, 0.0f);
            GlStateManager.scalef(0.5f, 0.5f, 1.0f);
            final float float8 = (float)(integer3 + 1);
            GlStateManager.translatef(17.0f / float8, (2.0f + float8 / 1.5f) * (Util.getMillis() % 800000L / 800000.0f), 0.0f);
            GlStateManager.rotatef((float8 * float8 * 4321.0f + float8 * 9.0f) * 2.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.scalef(4.5f - float8 / 4.0f, 4.5f - float8 / 4.0f, 1.0f);
            GlStateManager.multMatrix(TheEndPortalRenderer.PROJECTION);
            GlStateManager.multMatrix(TheEndPortalRenderer.MODELVIEW);
            final Tesselator cuz20 = Tesselator.getInstance();
            final BufferBuilder cuw21 = cuz20.getBuilder();
            cuw21.begin(7, DefaultVertexFormat.POSITION_COLOR);
            final float float9 = (TheEndPortalRenderer.RANDOM.nextFloat() * 0.5f + 0.1f) * float7;
            final float float10 = (TheEndPortalRenderer.RANDOM.nextFloat() * 0.5f + 0.4f) * float7;
            final float float11 = (TheEndPortalRenderer.RANDOM.nextFloat() * 0.5f + 0.5f) * float7;
            if (buy.shouldRenderFace(Direction.SOUTH)) {
                cuw21.vertex(double2, double3, double4 + 1.0).color(float9, float10, float11, 1.0f).endVertex();
                cuw21.vertex(double2 + 1.0, double3, double4 + 1.0).color(float9, float10, float11, 1.0f).endVertex();
                cuw21.vertex(double2 + 1.0, double3 + 1.0, double4 + 1.0).color(float9, float10, float11, 1.0f).endVertex();
                cuw21.vertex(double2, double3 + 1.0, double4 + 1.0).color(float9, float10, float11, 1.0f).endVertex();
            }
            if (buy.shouldRenderFace(Direction.NORTH)) {
                cuw21.vertex(double2, double3 + 1.0, double4).color(float9, float10, float11, 1.0f).endVertex();
                cuw21.vertex(double2 + 1.0, double3 + 1.0, double4).color(float9, float10, float11, 1.0f).endVertex();
                cuw21.vertex(double2 + 1.0, double3, double4).color(float9, float10, float11, 1.0f).endVertex();
                cuw21.vertex(double2, double3, double4).color(float9, float10, float11, 1.0f).endVertex();
            }
            if (buy.shouldRenderFace(Direction.EAST)) {
                cuw21.vertex(double2 + 1.0, double3 + 1.0, double4).color(float9, float10, float11, 1.0f).endVertex();
                cuw21.vertex(double2 + 1.0, double3 + 1.0, double4 + 1.0).color(float9, float10, float11, 1.0f).endVertex();
                cuw21.vertex(double2 + 1.0, double3, double4 + 1.0).color(float9, float10, float11, 1.0f).endVertex();
                cuw21.vertex(double2 + 1.0, double3, double4).color(float9, float10, float11, 1.0f).endVertex();
            }
            if (buy.shouldRenderFace(Direction.WEST)) {
                cuw21.vertex(double2, double3, double4).color(float9, float10, float11, 1.0f).endVertex();
                cuw21.vertex(double2, double3, double4 + 1.0).color(float9, float10, float11, 1.0f).endVertex();
                cuw21.vertex(double2, double3 + 1.0, double4 + 1.0).color(float9, float10, float11, 1.0f).endVertex();
                cuw21.vertex(double2, double3 + 1.0, double4).color(float9, float10, float11, 1.0f).endVertex();
            }
            if (buy.shouldRenderFace(Direction.DOWN)) {
                cuw21.vertex(double2, double3, double4).color(float9, float10, float11, 1.0f).endVertex();
                cuw21.vertex(double2 + 1.0, double3, double4).color(float9, float10, float11, 1.0f).endVertex();
                cuw21.vertex(double2 + 1.0, double3, double4 + 1.0).color(float9, float10, float11, 1.0f).endVertex();
                cuw21.vertex(double2, double3, double4 + 1.0).color(float9, float10, float11, 1.0f).endVertex();
            }
            if (buy.shouldRenderFace(Direction.UP)) {
                cuw21.vertex(double2, double3 + float6, double4 + 1.0).color(float9, float10, float11, 1.0f).endVertex();
                cuw21.vertex(double2 + 1.0, double3 + float6, double4 + 1.0).color(float9, float10, float11, 1.0f).endVertex();
                cuw21.vertex(double2 + 1.0, double3 + float6, double4).color(float9, float10, float11, 1.0f).endVertex();
                cuw21.vertex(double2, double3 + float6, double4).color(float9, float10, float11, 1.0f).endVertex();
            }
            cuz20.end();
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
            this.bindTexture(TheEndPortalRenderer.END_SKY_LOCATION);
        }
        GlStateManager.disableBlend();
        GlStateManager.disableTexGen(GlStateManager.TexGen.S);
        GlStateManager.disableTexGen(GlStateManager.TexGen.T);
        GlStateManager.disableTexGen(GlStateManager.TexGen.R);
        GlStateManager.enableLighting();
        if (boolean15) {
            dnc16.resetFogColor(false);
        }
    }
    
    protected int getPasses(final double double1) {
        int integer4;
        if (double1 > 36864.0) {
            integer4 = 1;
        }
        else if (double1 > 25600.0) {
            integer4 = 3;
        }
        else if (double1 > 16384.0) {
            integer4 = 5;
        }
        else if (double1 > 9216.0) {
            integer4 = 7;
        }
        else if (double1 > 4096.0) {
            integer4 = 9;
        }
        else if (double1 > 1024.0) {
            integer4 = 11;
        }
        else if (double1 > 576.0) {
            integer4 = 13;
        }
        else if (double1 > 256.0) {
            integer4 = 14;
        }
        else {
            integer4 = 15;
        }
        return integer4;
    }
    
    protected float getOffset() {
        return 0.75f;
    }
    
    private FloatBuffer getBuffer(final float float1, final float float2, final float float3, final float float4) {
        this.buffer.clear();
        this.buffer.put(float1).put(float2).put(float3).put(float4);
        this.buffer.flip();
        return this.buffer;
    }
    
    static {
        END_SKY_LOCATION = new ResourceLocation("textures/environment/end_sky.png");
        END_PORTAL_LOCATION = new ResourceLocation("textures/entity/end_portal.png");
        RANDOM = new Random(31100L);
        MODELVIEW = MemoryTracker.createFloatBuffer(16);
        PROJECTION = MemoryTracker.createFloatBuffer(16);
    }
}
