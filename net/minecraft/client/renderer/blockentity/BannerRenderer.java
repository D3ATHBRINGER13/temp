package net.minecraft.client.renderer.blockentity;

import net.minecraft.world.level.block.entity.BlockEntity;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.banner.BannerTextures;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.BannerBlock;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.BannerModel;
import net.minecraft.world.level.block.entity.BannerBlockEntity;

public class BannerRenderer extends BlockEntityRenderer<BannerBlockEntity> {
    private final BannerModel bannerModel;
    
    public BannerRenderer() {
        this.bannerModel = new BannerModel();
    }
    
    @Override
    public void render(final BannerBlockEntity bto, final double double2, final double double3, final double double4, final float float5, final int integer) {
        final float float6 = 0.6666667f;
        final boolean boolean12 = bto.getLevel() == null;
        GlStateManager.pushMatrix();
        final ModelPart djv15 = this.bannerModel.getPole();
        long long13;
        if (boolean12) {
            long13 = 0L;
            GlStateManager.translatef((float)double2 + 0.5f, (float)double3 + 0.5f, (float)double4 + 0.5f);
            djv15.visible = true;
        }
        else {
            long13 = bto.getLevel().getGameTime();
            final BlockState bvt16 = bto.getBlockState();
            if (bvt16.getBlock() instanceof BannerBlock) {
                GlStateManager.translatef((float)double2 + 0.5f, (float)double3 + 0.5f, (float)double4 + 0.5f);
                GlStateManager.rotatef(-bvt16.<Integer>getValue((Property<Integer>)BannerBlock.ROTATION) * 360 / 16.0f, 0.0f, 1.0f, 0.0f);
                djv15.visible = true;
            }
            else {
                GlStateManager.translatef((float)double2 + 0.5f, (float)double3 - 0.16666667f, (float)double4 + 0.5f);
                GlStateManager.rotatef(-bvt16.<Direction>getValue((Property<Direction>)WallBannerBlock.FACING).toYRot(), 0.0f, 1.0f, 0.0f);
                GlStateManager.translatef(0.0f, -0.3125f, -0.4375f);
                djv15.visible = false;
            }
        }
        final BlockPos ew16 = bto.getBlockPos();
        final float float7 = ew16.getX() * 7 + ew16.getY() * 9 + ew16.getZ() * 13 + long13 + float5;
        this.bannerModel.getFlag().xRot = (-0.0125f + 0.01f * Mth.cos(float7 * 3.1415927f * 0.02f)) * 3.1415927f;
        GlStateManager.enableRescaleNormal();
        final ResourceLocation qv18 = this.getTextureLocation(bto);
        if (qv18 != null) {
            this.bindTexture(qv18);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.6666667f, -0.6666667f, -0.6666667f);
            this.bannerModel.render();
            GlStateManager.popMatrix();
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }
    
    @Nullable
    private ResourceLocation getTextureLocation(final BannerBlockEntity bto) {
        return BannerTextures.BANNER_CACHE.getTextureLocation(bto.getTextureHashName(), bto.getPatterns(), bto.getColors());
    }
}
