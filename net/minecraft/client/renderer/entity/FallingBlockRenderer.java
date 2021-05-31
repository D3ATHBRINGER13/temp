package net.minecraft.client.renderer.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockAndBiomeGetter;
import java.util.Random;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.entity.item.FallingBlockEntity;

public class FallingBlockRenderer extends EntityRenderer<FallingBlockEntity> {
    public FallingBlockRenderer(final EntityRenderDispatcher dsa) {
        super(dsa);
        this.shadowRadius = 0.5f;
    }
    
    @Override
    public void render(final FallingBlockEntity atw, final double double2, final double double3, final double double4, final float float5, final float float6) {
        final BlockState bvt11 = atw.getBlockState();
        if (bvt11.getRenderShape() != RenderShape.MODEL) {
            return;
        }
        final Level bhr12 = atw.getLevel();
        if (bvt11 == bhr12.getBlockState(new BlockPos(atw)) || bvt11.getRenderShape() == RenderShape.INVISIBLE) {
            return;
        }
        this.bindTexture(TextureAtlas.LOCATION_BLOCKS);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        final Tesselator cuz13 = Tesselator.getInstance();
        final BufferBuilder cuw14 = cuz13.getBuilder();
        if (this.solidRender) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(atw));
        }
        cuw14.begin(7, DefaultVertexFormat.BLOCK);
        final BlockPos ew15 = new BlockPos(atw.x, atw.getBoundingBox().maxY, atw.z);
        GlStateManager.translatef((float)(double2 - ew15.getX() - 0.5), (float)(double3 - ew15.getY()), (float)(double4 - ew15.getZ() - 0.5));
        final BlockRenderDispatcher dnw16 = Minecraft.getInstance().getBlockRenderer();
        dnw16.getModelRenderer().tesselateBlock(bhr12, dnw16.getBlockModel(bvt11), bvt11, ew15, cuw14, false, new Random(), bvt11.getSeed(atw.getStartPos()));
        cuz13.end();
        if (this.solidRender) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        super.render(atw, double2, double3, double4, float5, float6);
    }
    
    @Override
    protected ResourceLocation getTextureLocation(final FallingBlockEntity atw) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
