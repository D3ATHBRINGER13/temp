package net.minecraft.client.renderer.block;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.util.Mth;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.tags.FluidTags;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.world.level.BlockAndBiomeGetter;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class LiquidBlockRenderer {
    private final TextureAtlasSprite[] lavaIcons;
    private final TextureAtlasSprite[] waterIcons;
    private TextureAtlasSprite waterOverlay;
    
    public LiquidBlockRenderer() {
        this.lavaIcons = new TextureAtlasSprite[2];
        this.waterIcons = new TextureAtlasSprite[2];
    }
    
    protected void setupSprites() {
        final TextureAtlas dxa2 = Minecraft.getInstance().getTextureAtlas();
        this.lavaIcons[0] = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(Blocks.LAVA.defaultBlockState()).getParticleIcon();
        this.lavaIcons[1] = dxa2.getSprite(ModelBakery.LAVA_FLOW);
        this.waterIcons[0] = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(Blocks.WATER.defaultBlockState()).getParticleIcon();
        this.waterIcons[1] = dxa2.getSprite(ModelBakery.WATER_FLOW);
        this.waterOverlay = dxa2.getSprite(ModelBakery.WATER_OVERLAY);
    }
    
    private static boolean isNeighborSameFluid(final BlockGetter bhb, final BlockPos ew, final Direction fb, final FluidState clk) {
        final BlockPos ew2 = ew.relative(fb);
        final FluidState clk2 = bhb.getFluidState(ew2);
        return clk2.getType().isSame(clk.getType());
    }
    
    private static boolean isFaceOccluded(final BlockGetter bhb, final BlockPos ew, final Direction fb, final float float4) {
        final BlockPos ew2 = ew.relative(fb);
        final BlockState bvt6 = bhb.getBlockState(ew2);
        if (bvt6.canOcclude()) {
            final VoxelShape ctc7 = Shapes.box(0.0, 0.0, 0.0, 1.0, float4, 1.0);
            final VoxelShape ctc8 = bvt6.getOcclusionShape(bhb, ew2);
            return Shapes.blockOccudes(ctc7, ctc8, fb);
        }
        return false;
    }
    
    public boolean tesselate(final BlockAndBiomeGetter bgz, final BlockPos ew, final BufferBuilder cuw, final FluidState clk) {
        final boolean boolean6 = clk.is(FluidTags.LAVA);
        final TextureAtlasSprite[] arr7 = boolean6 ? this.lavaIcons : this.waterIcons;
        final int integer8 = boolean6 ? 16777215 : BiomeColors.getAverageWaterColor(bgz, ew);
        final float float9 = (integer8 >> 16 & 0xFF) / 255.0f;
        final float float10 = (integer8 >> 8 & 0xFF) / 255.0f;
        final float float11 = (integer8 & 0xFF) / 255.0f;
        final boolean boolean7 = !isNeighborSameFluid(bgz, ew, Direction.UP, clk);
        final boolean boolean8 = !isNeighborSameFluid(bgz, ew, Direction.DOWN, clk) && !isFaceOccluded(bgz, ew, Direction.DOWN, 0.8888889f);
        final boolean boolean9 = !isNeighborSameFluid(bgz, ew, Direction.NORTH, clk);
        final boolean boolean10 = !isNeighborSameFluid(bgz, ew, Direction.SOUTH, clk);
        final boolean boolean11 = !isNeighborSameFluid(bgz, ew, Direction.WEST, clk);
        final boolean boolean12 = !isNeighborSameFluid(bgz, ew, Direction.EAST, clk);
        if (!boolean7 && !boolean8 && !boolean12 && !boolean11 && !boolean9 && !boolean10) {
            return false;
        }
        boolean boolean13 = false;
        final float float12 = 0.5f;
        final float float13 = 1.0f;
        final float float14 = 0.8f;
        final float float15 = 0.6f;
        float float16 = this.getWaterHeight(bgz, ew, clk.getType());
        float float17 = this.getWaterHeight(bgz, ew.south(), clk.getType());
        float float18 = this.getWaterHeight(bgz, ew.east().south(), clk.getType());
        float float19 = this.getWaterHeight(bgz, ew.east(), clk.getType());
        final double double27 = ew.getX();
        final double double28 = ew.getY();
        final double double29 = ew.getZ();
        final float float20 = 0.001f;
        if (boolean7 && !isFaceOccluded(bgz, ew, Direction.UP, Math.min(Math.min(float16, float17), Math.min(float18, float19)))) {
            boolean13 = true;
            float16 -= 0.001f;
            float17 -= 0.001f;
            float18 -= 0.001f;
            float19 -= 0.001f;
            final Vec3 csi42 = clk.getFlow(bgz, ew);
            float float21;
            float float22;
            float float23;
            float float24;
            float float25;
            float float26;
            float float27;
            float float28;
            if (csi42.x == 0.0 && csi42.z == 0.0) {
                final TextureAtlasSprite dxb43 = arr7[0];
                float21 = dxb43.getU(0.0);
                float22 = dxb43.getV(0.0);
                float23 = float21;
                float24 = dxb43.getV(16.0);
                float25 = dxb43.getU(16.0);
                float26 = float24;
                float27 = float25;
                float28 = float22;
            }
            else {
                final TextureAtlasSprite dxb43 = arr7[1];
                final float float29 = (float)Mth.atan2(csi42.z, csi42.x) - 1.5707964f;
                final float float30 = Mth.sin(float29) * 0.25f;
                final float float31 = Mth.cos(float29) * 0.25f;
                final float float32 = 8.0f;
                float21 = dxb43.getU(8.0f + (-float31 - float30) * 16.0f);
                float22 = dxb43.getV(8.0f + (-float31 + float30) * 16.0f);
                float23 = dxb43.getU(8.0f + (-float31 + float30) * 16.0f);
                float24 = dxb43.getV(8.0f + (float31 + float30) * 16.0f);
                float25 = dxb43.getU(8.0f + (float31 + float30) * 16.0f);
                float26 = dxb43.getV(8.0f + (float31 - float30) * 16.0f);
                float27 = dxb43.getU(8.0f + (float31 - float30) * 16.0f);
                float28 = dxb43.getV(8.0f + (-float31 - float30) * 16.0f);
            }
            final float float33 = (float21 + float23 + float25 + float27) / 4.0f;
            final float float29 = (float22 + float24 + float26 + float28) / 4.0f;
            final float float30 = arr7[0].getWidth() / (arr7[0].getU1() - arr7[0].getU0());
            final float float31 = arr7[0].getHeight() / (arr7[0].getV1() - arr7[0].getV0());
            final float float32 = 4.0f / Math.max(float31, float30);
            float21 = Mth.lerp(float32, float21, float33);
            float23 = Mth.lerp(float32, float23, float33);
            float25 = Mth.lerp(float32, float25, float33);
            float27 = Mth.lerp(float32, float27, float33);
            float22 = Mth.lerp(float32, float22, float29);
            float24 = Mth.lerp(float32, float24, float29);
            float26 = Mth.lerp(float32, float26, float29);
            float28 = Mth.lerp(float32, float28, float29);
            final int integer9 = this.getLightColor(bgz, ew);
            final int integer10 = integer9 >> 16 & 0xFFFF;
            final int integer11 = integer9 & 0xFFFF;
            final float float34 = 1.0f * float9;
            final float float35 = 1.0f * float10;
            final float float36 = 1.0f * float11;
            cuw.vertex(double27 + 0.0, double28 + float16, double29 + 0.0).color(float34, float35, float36, 1.0f).uv(float21, float22).uv2(integer10, integer11).endVertex();
            cuw.vertex(double27 + 0.0, double28 + float17, double29 + 1.0).color(float34, float35, float36, 1.0f).uv(float23, float24).uv2(integer10, integer11).endVertex();
            cuw.vertex(double27 + 1.0, double28 + float18, double29 + 1.0).color(float34, float35, float36, 1.0f).uv(float25, float26).uv2(integer10, integer11).endVertex();
            cuw.vertex(double27 + 1.0, double28 + float19, double29 + 0.0).color(float34, float35, float36, 1.0f).uv(float27, float28).uv2(integer10, integer11).endVertex();
            if (clk.shouldRenderBackwardUpFace(bgz, ew.above())) {
                cuw.vertex(double27 + 0.0, double28 + float16, double29 + 0.0).color(float34, float35, float36, 1.0f).uv(float21, float22).uv2(integer10, integer11).endVertex();
                cuw.vertex(double27 + 1.0, double28 + float19, double29 + 0.0).color(float34, float35, float36, 1.0f).uv(float27, float28).uv2(integer10, integer11).endVertex();
                cuw.vertex(double27 + 1.0, double28 + float18, double29 + 1.0).color(float34, float35, float36, 1.0f).uv(float25, float26).uv2(integer10, integer11).endVertex();
                cuw.vertex(double27 + 0.0, double28 + float17, double29 + 1.0).color(float34, float35, float36, 1.0f).uv(float23, float24).uv2(integer10, integer11).endVertex();
            }
        }
        if (boolean8) {
            final float float21 = arr7[0].getU0();
            final float float23 = arr7[0].getU1();
            final float float25 = arr7[0].getV0();
            final float float27 = arr7[0].getV1();
            final int integer12 = this.getLightColor(bgz, ew.below());
            final int integer13 = integer12 >> 16 & 0xFFFF;
            final int integer14 = integer12 & 0xFFFF;
            final float float28 = 0.5f * float9;
            final float float37 = 0.5f * float10;
            final float float33 = 0.5f * float11;
            cuw.vertex(double27, double28, double29 + 1.0).color(float28, float37, float33, 1.0f).uv(float21, float27).uv2(integer13, integer14).endVertex();
            cuw.vertex(double27, double28, double29).color(float28, float37, float33, 1.0f).uv(float21, float25).uv2(integer13, integer14).endVertex();
            cuw.vertex(double27 + 1.0, double28, double29).color(float28, float37, float33, 1.0f).uv(float23, float25).uv2(integer13, integer14).endVertex();
            cuw.vertex(double27 + 1.0, double28, double29 + 1.0).color(float28, float37, float33, 1.0f).uv(float23, float27).uv2(integer13, integer14).endVertex();
            boolean13 = true;
        }
        for (int integer15 = 0; integer15 < 4; ++integer15) {
            float float23;
            float float25;
            double double30;
            double double31;
            double double32;
            double double33;
            Direction fb45;
            boolean boolean14;
            if (integer15 == 0) {
                float23 = float16;
                float25 = float19;
                double30 = double27;
                double31 = double27 + 1.0;
                double32 = double29 + 0.0010000000474974513;
                double33 = double29 + 0.0010000000474974513;
                fb45 = Direction.NORTH;
                boolean14 = boolean9;
            }
            else if (integer15 == 1) {
                float23 = float18;
                float25 = float17;
                double30 = double27 + 1.0;
                double31 = double27;
                double32 = double29 + 1.0 - 0.0010000000474974513;
                double33 = double29 + 1.0 - 0.0010000000474974513;
                fb45 = Direction.SOUTH;
                boolean14 = boolean10;
            }
            else if (integer15 == 2) {
                float23 = float17;
                float25 = float16;
                double30 = double27 + 0.0010000000474974513;
                double31 = double27 + 0.0010000000474974513;
                double32 = double29 + 1.0;
                double33 = double29;
                fb45 = Direction.WEST;
                boolean14 = boolean11;
            }
            else {
                float23 = float19;
                float25 = float18;
                double30 = double27 + 1.0 - 0.0010000000474974513;
                double31 = double27 + 1.0 - 0.0010000000474974513;
                double32 = double29;
                double33 = double29 + 1.0;
                fb45 = Direction.EAST;
                boolean14 = boolean12;
            }
            if (boolean14 && !isFaceOccluded(bgz, ew, fb45, Math.max(float23, float25))) {
                boolean13 = true;
                final BlockPos ew2 = ew.relative(fb45);
                TextureAtlasSprite dxb44 = arr7[1];
                if (!boolean6) {
                    final Block bmv49 = bgz.getBlockState(ew2).getBlock();
                    if (bmv49 == Blocks.GLASS || bmv49 instanceof StainedGlassBlock) {
                        dxb44 = this.waterOverlay;
                    }
                }
                final float float38 = dxb44.getU(0.0);
                final float float39 = dxb44.getU(8.0);
                final float float34 = dxb44.getV((1.0f - float23) * 16.0f * 0.5f);
                final float float35 = dxb44.getV((1.0f - float25) * 16.0f * 0.5f);
                final float float36 = dxb44.getV(8.0);
                final int integer16 = this.getLightColor(bgz, ew2);
                final int integer17 = integer16 >> 16 & 0xFFFF;
                final int integer18 = integer16 & 0xFFFF;
                final float float40 = (integer15 < 2) ? 0.8f : 0.6f;
                final float float41 = 1.0f * float40 * float9;
                final float float42 = 1.0f * float40 * float10;
                final float float43 = 1.0f * float40 * float11;
                cuw.vertex(double30, double28 + float23, double32).color(float41, float42, float43, 1.0f).uv(float38, float34).uv2(integer17, integer18).endVertex();
                cuw.vertex(double31, double28 + float25, double33).color(float41, float42, float43, 1.0f).uv(float39, float35).uv2(integer17, integer18).endVertex();
                cuw.vertex(double31, double28 + 0.0, double33).color(float41, float42, float43, 1.0f).uv(float39, float36).uv2(integer17, integer18).endVertex();
                cuw.vertex(double30, double28 + 0.0, double32).color(float41, float42, float43, 1.0f).uv(float38, float36).uv2(integer17, integer18).endVertex();
                if (dxb44 != this.waterOverlay) {
                    cuw.vertex(double30, double28 + 0.0, double32).color(float41, float42, float43, 1.0f).uv(float38, float36).uv2(integer17, integer18).endVertex();
                    cuw.vertex(double31, double28 + 0.0, double33).color(float41, float42, float43, 1.0f).uv(float39, float36).uv2(integer17, integer18).endVertex();
                    cuw.vertex(double31, double28 + float25, double33).color(float41, float42, float43, 1.0f).uv(float39, float35).uv2(integer17, integer18).endVertex();
                    cuw.vertex(double30, double28 + float23, double32).color(float41, float42, float43, 1.0f).uv(float38, float34).uv2(integer17, integer18).endVertex();
                }
            }
        }
        return boolean13;
    }
    
    private int getLightColor(final BlockAndBiomeGetter bgz, final BlockPos ew) {
        final int integer4 = bgz.getLightColor(ew, 0);
        final int integer5 = bgz.getLightColor(ew.above(), 0);
        final int integer6 = integer4 & 0xFF;
        final int integer7 = integer5 & 0xFF;
        final int integer8 = integer4 >> 16 & 0xFF;
        final int integer9 = integer5 >> 16 & 0xFF;
        return ((integer6 > integer7) ? integer6 : integer7) | ((integer8 > integer9) ? integer8 : integer9) << 16;
    }
    
    private float getWaterHeight(final BlockGetter bhb, final BlockPos ew, final Fluid clj) {
        int integer5 = 0;
        float float6 = 0.0f;
        for (int integer6 = 0; integer6 < 4; ++integer6) {
            final BlockPos ew2 = ew.offset(-(integer6 & 0x1), 0, -(integer6 >> 1 & 0x1));
            if (bhb.getFluidState(ew2.above()).getType().isSame(clj)) {
                return 1.0f;
            }
            final FluidState clk9 = bhb.getFluidState(ew2);
            if (clk9.getType().isSame(clj)) {
                final float float7 = clk9.getHeight(bhb, ew2);
                if (float7 >= 0.8f) {
                    float6 += float7 * 10.0f;
                    integer5 += 10;
                }
                else {
                    float6 += float7;
                    ++integer5;
                }
            }
            else if (!bhb.getBlockState(ew2).getMaterial().isSolid()) {
                ++integer5;
            }
        }
        return float6 / integer5;
    }
}
