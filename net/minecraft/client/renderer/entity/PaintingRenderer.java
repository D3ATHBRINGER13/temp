package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.resources.PaintingTextureManager;
import net.minecraft.world.entity.decoration.Motive;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.entity.decoration.Painting;

public class PaintingRenderer extends EntityRenderer<Painting> {
    public PaintingRenderer(final EntityRenderDispatcher dsa) {
        super(dsa);
    }
    
    @Override
    public void render(final Painting atq, final double double2, final double double3, final double double4, final float float5, final float float6) {
        GlStateManager.pushMatrix();
        GlStateManager.translated(double2, double3, double4);
        GlStateManager.rotatef(180.0f - float5, 0.0f, 1.0f, 0.0f);
        GlStateManager.enableRescaleNormal();
        this.bindTexture(atq);
        final Motive atp11 = atq.motive;
        final float float7 = 0.0625f;
        GlStateManager.scalef(0.0625f, 0.0625f, 0.0625f);
        if (this.solidRender) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(atq));
        }
        final PaintingTextureManager dxs13 = Minecraft.getInstance().getPaintingTextures();
        this.renderPainting(atq, atp11.getWidth(), atp11.getHeight(), dxs13.get(atp11), dxs13.getBackSprite());
        if (this.solidRender) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.render(atq, double2, double3, double4, float5, float6);
    }
    
    @Override
    protected ResourceLocation getTextureLocation(final Painting atq) {
        return TextureAtlas.LOCATION_PAINTINGS;
    }
    
    private void renderPainting(final Painting atq, final int integer2, final int integer3, final TextureAtlasSprite dxb4, final TextureAtlasSprite dxb5) {
        final float float7 = -integer2 / 2.0f;
        final float float8 = -integer3 / 2.0f;
        final float float9 = 0.5f;
        final float float10 = dxb5.getU0();
        final float float11 = dxb5.getU1();
        final float float12 = dxb5.getV0();
        final float float13 = dxb5.getV1();
        final float float14 = dxb5.getU0();
        final float float15 = dxb5.getU1();
        final float float16 = dxb5.getV0();
        final float float17 = dxb5.getV(1.0);
        final float float18 = dxb5.getU0();
        final float float19 = dxb5.getU(1.0);
        final float float20 = dxb5.getV0();
        final float float21 = dxb5.getV1();
        final int integer4 = integer2 / 16;
        final int integer5 = integer3 / 16;
        final double double24 = 16.0 / integer4;
        final double double25 = 16.0 / integer5;
        for (int integer6 = 0; integer6 < integer4; ++integer6) {
            for (int integer7 = 0; integer7 < integer5; ++integer7) {
                final float float22 = float7 + (integer6 + 1) * 16;
                final float float23 = float7 + integer6 * 16;
                final float float24 = float8 + (integer7 + 1) * 16;
                final float float25 = float8 + integer7 * 16;
                this.setBrightness(atq, (float22 + float23) / 2.0f, (float24 + float25) / 2.0f);
                final float float26 = dxb4.getU(double24 * (integer4 - integer6));
                final float float27 = dxb4.getU(double24 * (integer4 - (integer6 + 1)));
                final float float28 = dxb4.getV(double25 * (integer5 - integer7));
                final float float29 = dxb4.getV(double25 * (integer5 - (integer7 + 1)));
                final Tesselator cuz38 = Tesselator.getInstance();
                final BufferBuilder cuw39 = cuz38.getBuilder();
                cuw39.begin(7, DefaultVertexFormat.POSITION_TEX_NORMAL);
                cuw39.vertex(float22, float25, -0.5).uv(float27, float28).normal(0.0f, 0.0f, -1.0f).endVertex();
                cuw39.vertex(float23, float25, -0.5).uv(float26, float28).normal(0.0f, 0.0f, -1.0f).endVertex();
                cuw39.vertex(float23, float24, -0.5).uv(float26, float29).normal(0.0f, 0.0f, -1.0f).endVertex();
                cuw39.vertex(float22, float24, -0.5).uv(float27, float29).normal(0.0f, 0.0f, -1.0f).endVertex();
                cuw39.vertex(float22, float24, 0.5).uv(float10, float12).normal(0.0f, 0.0f, 1.0f).endVertex();
                cuw39.vertex(float23, float24, 0.5).uv(float11, float12).normal(0.0f, 0.0f, 1.0f).endVertex();
                cuw39.vertex(float23, float25, 0.5).uv(float11, float13).normal(0.0f, 0.0f, 1.0f).endVertex();
                cuw39.vertex(float22, float25, 0.5).uv(float10, float13).normal(0.0f, 0.0f, 1.0f).endVertex();
                cuw39.vertex(float22, float24, -0.5).uv(float14, float16).normal(0.0f, 1.0f, 0.0f).endVertex();
                cuw39.vertex(float23, float24, -0.5).uv(float15, float16).normal(0.0f, 1.0f, 0.0f).endVertex();
                cuw39.vertex(float23, float24, 0.5).uv(float15, float17).normal(0.0f, 1.0f, 0.0f).endVertex();
                cuw39.vertex(float22, float24, 0.5).uv(float14, float17).normal(0.0f, 1.0f, 0.0f).endVertex();
                cuw39.vertex(float22, float25, 0.5).uv(float14, float16).normal(0.0f, -1.0f, 0.0f).endVertex();
                cuw39.vertex(float23, float25, 0.5).uv(float15, float16).normal(0.0f, -1.0f, 0.0f).endVertex();
                cuw39.vertex(float23, float25, -0.5).uv(float15, float17).normal(0.0f, -1.0f, 0.0f).endVertex();
                cuw39.vertex(float22, float25, -0.5).uv(float14, float17).normal(0.0f, -1.0f, 0.0f).endVertex();
                cuw39.vertex(float22, float24, 0.5).uv(float19, float20).normal(-1.0f, 0.0f, 0.0f).endVertex();
                cuw39.vertex(float22, float25, 0.5).uv(float19, float21).normal(-1.0f, 0.0f, 0.0f).endVertex();
                cuw39.vertex(float22, float25, -0.5).uv(float18, float21).normal(-1.0f, 0.0f, 0.0f).endVertex();
                cuw39.vertex(float22, float24, -0.5).uv(float18, float20).normal(-1.0f, 0.0f, 0.0f).endVertex();
                cuw39.vertex(float23, float24, -0.5).uv(float19, float20).normal(1.0f, 0.0f, 0.0f).endVertex();
                cuw39.vertex(float23, float25, -0.5).uv(float19, float21).normal(1.0f, 0.0f, 0.0f).endVertex();
                cuw39.vertex(float23, float25, 0.5).uv(float18, float21).normal(1.0f, 0.0f, 0.0f).endVertex();
                cuw39.vertex(float23, float24, 0.5).uv(float18, float20).normal(1.0f, 0.0f, 0.0f).endVertex();
                cuz38.end();
            }
        }
    }
    
    private void setBrightness(final Painting atq, final float float2, final float float3) {
        int integer5 = Mth.floor(atq.x);
        final int integer6 = Mth.floor(atq.y + float3 / 16.0f);
        int integer7 = Mth.floor(atq.z);
        final Direction fb8 = atq.getDirection();
        if (fb8 == Direction.NORTH) {
            integer5 = Mth.floor(atq.x + float2 / 16.0f);
        }
        if (fb8 == Direction.WEST) {
            integer7 = Mth.floor(atq.z - float2 / 16.0f);
        }
        if (fb8 == Direction.SOUTH) {
            integer5 = Mth.floor(atq.x - float2 / 16.0f);
        }
        if (fb8 == Direction.EAST) {
            integer7 = Mth.floor(atq.z + float2 / 16.0f);
        }
        final int integer8 = this.entityRenderDispatcher.level.getLightColor(new BlockPos(integer5, integer6, integer7), 0);
        final int integer9 = integer8 % 65536;
        final int integer10 = integer8 / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)integer9, (float)integer10);
        GlStateManager.color3f(1.0f, 1.0f, 1.0f);
    }
}
