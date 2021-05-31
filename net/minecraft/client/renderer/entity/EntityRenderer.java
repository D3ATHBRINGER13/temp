package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GLX;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Iterator;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.phys.AABB;
import net.minecraft.client.renderer.culling.Culler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public abstract class EntityRenderer<T extends Entity> {
    private static final ResourceLocation SHADOW_LOCATION;
    protected final EntityRenderDispatcher entityRenderDispatcher;
    protected float shadowRadius;
    protected float shadowStrength;
    protected boolean solidRender;
    
    protected EntityRenderer(final EntityRenderDispatcher dsa) {
        this.shadowStrength = 1.0f;
        this.entityRenderDispatcher = dsa;
    }
    
    public void setSolidRender(final boolean boolean1) {
        this.solidRender = boolean1;
    }
    
    public boolean shouldRender(final T aio, final Culler dqe, final double double3, final double double4, final double double5) {
        if (!aio.shouldRender(double3, double4, double5)) {
            return false;
        }
        if (aio.noCulling) {
            return true;
        }
        AABB csc10 = aio.getBoundingBoxForCulling().inflate(0.5);
        if (csc10.hasNaN() || csc10.getSize() == 0.0) {
            csc10 = new AABB(aio.x - 2.0, aio.y - 2.0, aio.z - 2.0, aio.x + 2.0, aio.y + 2.0, aio.z + 2.0);
        }
        return dqe.isVisible(csc10);
    }
    
    public void render(final T aio, final double double2, final double double3, final double double4, final float float5, final float float6) {
        if (!this.solidRender) {
            this.renderName(aio, double2, double3, double4);
        }
    }
    
    protected int getTeamColor(final T aio) {
        final PlayerTeam ctg3 = (PlayerTeam)aio.getTeam();
        if (ctg3 != null && ctg3.getColor().getColor() != null) {
            return ctg3.getColor().getColor();
        }
        return 16777215;
    }
    
    protected void renderName(final T aio, final double double2, final double double3, final double double4) {
        if (!this.shouldShowName(aio)) {
            return;
        }
        this.renderNameTag(aio, aio.getDisplayName().getColoredString(), double2, double3, double4, 64);
    }
    
    protected boolean shouldShowName(final T aio) {
        return aio.shouldShowName() && aio.hasCustomName();
    }
    
    protected void renderNameTags(final T aio, final double double2, final double double3, final double double4, final String string, final double double6) {
        this.renderNameTag(aio, string, double2, double3, double4, 64);
    }
    
    @Nullable
    protected abstract ResourceLocation getTextureLocation(final T aio);
    
    protected boolean bindTexture(final T aio) {
        final ResourceLocation qv3 = this.getTextureLocation(aio);
        if (qv3 == null) {
            return false;
        }
        this.bindTexture(qv3);
        return true;
    }
    
    public void bindTexture(final ResourceLocation qv) {
        this.entityRenderDispatcher.textureManager.bind(qv);
    }
    
    private void renderFlame(final Entity aio, final double double2, final double double3, final double double4, final float float5) {
        GlStateManager.disableLighting();
        final TextureAtlas dxa10 = Minecraft.getInstance().getTextureAtlas();
        final TextureAtlasSprite dxb11 = dxa10.getSprite(ModelBakery.FIRE_0);
        final TextureAtlasSprite dxb12 = dxa10.getSprite(ModelBakery.FIRE_1);
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)double2, (float)double3, (float)double4);
        final float float6 = aio.getBbWidth() * 1.4f;
        GlStateManager.scalef(float6, float6, float6);
        final Tesselator cuz14 = Tesselator.getInstance();
        final BufferBuilder cuw15 = cuz14.getBuilder();
        float float7 = 0.5f;
        final float float8 = 0.0f;
        float float9 = aio.getBbHeight() / float6;
        float float10 = (float)(aio.y - aio.getBoundingBox().minY);
        GlStateManager.rotatef(-this.entityRenderDispatcher.playerRotY, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(0.0f, 0.0f, -0.3f + (int)float9 * 0.02f);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float float11 = 0.0f;
        int integer21 = 0;
        cuw15.begin(7, DefaultVertexFormat.POSITION_TEX);
        while (float9 > 0.0f) {
            final TextureAtlasSprite dxb13 = (integer21 % 2 == 0) ? dxb11 : dxb12;
            this.bindTexture(TextureAtlas.LOCATION_BLOCKS);
            float float12 = dxb13.getU0();
            final float float13 = dxb13.getV0();
            float float14 = dxb13.getU1();
            final float float15 = dxb13.getV1();
            if (integer21 / 2 % 2 == 0) {
                final float float16 = float14;
                float14 = float12;
                float12 = float16;
            }
            cuw15.vertex(float7 - 0.0f, 0.0f - float10, float11).uv(float14, float15).endVertex();
            cuw15.vertex(-float7 - 0.0f, 0.0f - float10, float11).uv(float12, float15).endVertex();
            cuw15.vertex(-float7 - 0.0f, 1.4f - float10, float11).uv(float12, float13).endVertex();
            cuw15.vertex(float7 - 0.0f, 1.4f - float10, float11).uv(float14, float13).endVertex();
            float9 -= 0.45f;
            float10 -= 0.45f;
            float7 *= 0.9f;
            float11 += 0.03f;
            ++integer21;
        }
        cuz14.end();
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
    }
    
    private void renderShadow(final Entity aio, final double double2, final double double3, final double double4, final float float5, final float float6) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        this.entityRenderDispatcher.textureManager.bind(EntityRenderer.SHADOW_LOCATION);
        final LevelReader bhu11 = this.getLevel();
        GlStateManager.depthMask(false);
        float float7 = this.shadowRadius;
        if (aio instanceof Mob) {
            final Mob aiy13 = (Mob)aio;
            if (aiy13.isBaby()) {
                float7 *= 0.5f;
            }
        }
        final double double5 = Mth.lerp(float6, aio.xOld, aio.x);
        final double double6 = Mth.lerp(float6, aio.yOld, aio.y);
        final double double7 = Mth.lerp(float6, aio.zOld, aio.z);
        final int integer19 = Mth.floor(double5 - float7);
        final int integer20 = Mth.floor(double5 + float7);
        final int integer21 = Mth.floor(double6 - float7);
        final int integer22 = Mth.floor(double6);
        final int integer23 = Mth.floor(double7 - float7);
        final int integer24 = Mth.floor(double7 + float7);
        final double double8 = double2 - double5;
        final double double9 = double3 - double6;
        final double double10 = double4 - double7;
        final Tesselator cuz31 = Tesselator.getInstance();
        final BufferBuilder cuw32 = cuz31.getBuilder();
        cuw32.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
        for (final BlockPos ew34 : BlockPos.betweenClosed(new BlockPos(integer19, integer21, integer23), new BlockPos(integer20, integer22, integer24))) {
            final BlockPos ew35 = ew34.below();
            final BlockState bvt36 = bhu11.getBlockState(ew35);
            if (bvt36.getRenderShape() != RenderShape.INVISIBLE && bhu11.getMaxLocalRawBrightness(ew34) > 3) {
                this.renderBlockShadow(bvt36, bhu11, ew35, double2, double3, double4, ew34, float5, float7, double8, double9, double10);
            }
        }
        cuz31.end();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
    }
    
    private LevelReader getLevel() {
        return this.entityRenderDispatcher.level;
    }
    
    private void renderBlockShadow(final BlockState bvt, final LevelReader bhu, final BlockPos ew3, final double double4, final double double5, final double double6, final BlockPos ew7, final float float8, final float float9, final double double10, final double double11, final double double12) {
        if (!bvt.isCollisionShapeFullBlock(bhu, ew3)) {
            return;
        }
        final VoxelShape ctc20 = bvt.getShape(this.getLevel(), ew7.below());
        if (ctc20.isEmpty()) {
            return;
        }
        final Tesselator cuz21 = Tesselator.getInstance();
        final BufferBuilder cuw22 = cuz21.getBuilder();
        double double13 = (float8 - (double5 - (ew7.getY() + double11)) / 2.0) * 0.5 * this.getLevel().getBrightness(ew7);
        if (double13 < 0.0) {
            return;
        }
        if (double13 > 1.0) {
            double13 = 1.0;
        }
        final AABB csc25 = ctc20.bounds();
        final double double14 = ew7.getX() + csc25.minX + double10;
        final double double15 = ew7.getX() + csc25.maxX + double10;
        final double double16 = ew7.getY() + csc25.minY + double11 + 0.015625;
        final double double17 = ew7.getZ() + csc25.minZ + double12;
        final double double18 = ew7.getZ() + csc25.maxZ + double12;
        final float float10 = (float)((double4 - double14) / 2.0 / float9 + 0.5);
        final float float11 = (float)((double4 - double15) / 2.0 / float9 + 0.5);
        final float float12 = (float)((double6 - double17) / 2.0 / float9 + 0.5);
        final float float13 = (float)((double6 - double18) / 2.0 / float9 + 0.5);
        cuw22.vertex(double14, double16, double17).uv(float10, float12).color(1.0f, 1.0f, 1.0f, (float)double13).endVertex();
        cuw22.vertex(double14, double16, double18).uv(float10, float13).color(1.0f, 1.0f, 1.0f, (float)double13).endVertex();
        cuw22.vertex(double15, double16, double18).uv(float11, float13).color(1.0f, 1.0f, 1.0f, (float)double13).endVertex();
        cuw22.vertex(double15, double16, double17).uv(float11, float12).color(1.0f, 1.0f, 1.0f, (float)double13).endVertex();
    }
    
    public static void render(final AABB csc, final double double2, final double double3, final double double4) {
        GlStateManager.disableTexture();
        final Tesselator cuz8 = Tesselator.getInstance();
        final BufferBuilder cuw9 = cuz8.getBuilder();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        cuw9.offset(double2, double3, double4);
        cuw9.begin(7, DefaultVertexFormat.POSITION_NORMAL);
        cuw9.vertex(csc.minX, csc.maxY, csc.minZ).normal(0.0f, 0.0f, -1.0f).endVertex();
        cuw9.vertex(csc.maxX, csc.maxY, csc.minZ).normal(0.0f, 0.0f, -1.0f).endVertex();
        cuw9.vertex(csc.maxX, csc.minY, csc.minZ).normal(0.0f, 0.0f, -1.0f).endVertex();
        cuw9.vertex(csc.minX, csc.minY, csc.minZ).normal(0.0f, 0.0f, -1.0f).endVertex();
        cuw9.vertex(csc.minX, csc.minY, csc.maxZ).normal(0.0f, 0.0f, 1.0f).endVertex();
        cuw9.vertex(csc.maxX, csc.minY, csc.maxZ).normal(0.0f, 0.0f, 1.0f).endVertex();
        cuw9.vertex(csc.maxX, csc.maxY, csc.maxZ).normal(0.0f, 0.0f, 1.0f).endVertex();
        cuw9.vertex(csc.minX, csc.maxY, csc.maxZ).normal(0.0f, 0.0f, 1.0f).endVertex();
        cuw9.vertex(csc.minX, csc.minY, csc.minZ).normal(0.0f, -1.0f, 0.0f).endVertex();
        cuw9.vertex(csc.maxX, csc.minY, csc.minZ).normal(0.0f, -1.0f, 0.0f).endVertex();
        cuw9.vertex(csc.maxX, csc.minY, csc.maxZ).normal(0.0f, -1.0f, 0.0f).endVertex();
        cuw9.vertex(csc.minX, csc.minY, csc.maxZ).normal(0.0f, -1.0f, 0.0f).endVertex();
        cuw9.vertex(csc.minX, csc.maxY, csc.maxZ).normal(0.0f, 1.0f, 0.0f).endVertex();
        cuw9.vertex(csc.maxX, csc.maxY, csc.maxZ).normal(0.0f, 1.0f, 0.0f).endVertex();
        cuw9.vertex(csc.maxX, csc.maxY, csc.minZ).normal(0.0f, 1.0f, 0.0f).endVertex();
        cuw9.vertex(csc.minX, csc.maxY, csc.minZ).normal(0.0f, 1.0f, 0.0f).endVertex();
        cuw9.vertex(csc.minX, csc.minY, csc.maxZ).normal(-1.0f, 0.0f, 0.0f).endVertex();
        cuw9.vertex(csc.minX, csc.maxY, csc.maxZ).normal(-1.0f, 0.0f, 0.0f).endVertex();
        cuw9.vertex(csc.minX, csc.maxY, csc.minZ).normal(-1.0f, 0.0f, 0.0f).endVertex();
        cuw9.vertex(csc.minX, csc.minY, csc.minZ).normal(-1.0f, 0.0f, 0.0f).endVertex();
        cuw9.vertex(csc.maxX, csc.minY, csc.minZ).normal(1.0f, 0.0f, 0.0f).endVertex();
        cuw9.vertex(csc.maxX, csc.maxY, csc.minZ).normal(1.0f, 0.0f, 0.0f).endVertex();
        cuw9.vertex(csc.maxX, csc.maxY, csc.maxZ).normal(1.0f, 0.0f, 0.0f).endVertex();
        cuw9.vertex(csc.maxX, csc.minY, csc.maxZ).normal(1.0f, 0.0f, 0.0f).endVertex();
        cuz8.end();
        cuw9.offset(0.0, 0.0, 0.0);
        GlStateManager.enableTexture();
    }
    
    public void postRender(final Entity aio, final double double2, final double double3, final double double4, final float float5, final float float6) {
        if (this.entityRenderDispatcher.options == null) {
            return;
        }
        if (this.entityRenderDispatcher.options.entityShadows && this.shadowRadius > 0.0f && !aio.isInvisible() && this.entityRenderDispatcher.shouldRenderShadow()) {
            final double double5 = this.entityRenderDispatcher.distanceToSqr(aio.x, aio.y, aio.z);
            final float float7 = (float)((1.0 - double5 / 256.0) * this.shadowStrength);
            if (float7 > 0.0f) {
                this.renderShadow(aio, double2, double3, double4, float7, float6);
            }
        }
        if (aio.displayFireAnimation() && !aio.isSpectator()) {
            this.renderFlame(aio, double2, double3, double4, float6);
        }
    }
    
    public Font getFont() {
        return this.entityRenderDispatcher.getFont();
    }
    
    protected void renderNameTag(final T aio, final String string, final double double3, final double double4, final double double5, final int integer) {
        final double double6 = aio.distanceToSqr(this.entityRenderDispatcher.camera.getPosition());
        if (double6 > integer * integer) {
            return;
        }
        final boolean boolean13 = aio.isVisuallySneaking();
        final float float14 = this.entityRenderDispatcher.playerRotY;
        final float float15 = this.entityRenderDispatcher.playerRotX;
        final float float16 = aio.getBbHeight() + 0.5f - (boolean13 ? 0.25f : 0.0f);
        final int integer2 = "deadmau5".equals(string) ? -10 : 0;
        GameRenderer.renderNameTagInWorld(this.getFont(), string, (float)double3, (float)double4 + float16, (float)double5, integer2, float14, float15, boolean13);
    }
    
    public EntityRenderDispatcher getDispatcher() {
        return this.entityRenderDispatcher;
    }
    
    public boolean hasSecondPass() {
        return false;
    }
    
    public void renderSecondPass(final T aio, final double double2, final double double3, final double double4, final float float5, final float float6) {
    }
    
    public void setLightColor(final T aio) {
        final int integer3 = aio.getLightColor();
        final int integer4 = integer3 % 65536;
        final int integer5 = integer3 / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)integer4, (float)integer5);
    }
    
    static {
        SHADOW_LOCATION = new ResourceLocation("textures/misc/shadow.png");
    }
}
