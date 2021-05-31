package net.minecraft.client.renderer.blockentity;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockAndBiomeGetter;
import java.util.Random;
import net.minecraft.world.level.Level;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.client.renderer.texture.TextureAtlas;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;

public class PistonHeadRenderer extends BlockEntityRenderer<PistonMovingBlockEntity> {
    private final BlockRenderDispatcher blockRenderer;
    
    public PistonHeadRenderer() {
        this.blockRenderer = Minecraft.getInstance().getBlockRenderer();
    }
    
    @Override
    public void render(final PistonMovingBlockEntity bvp, final double double2, final double double3, final double double4, final float float5, final int integer) {
        final BlockPos ew11 = bvp.getBlockPos().relative(bvp.getMovementDirection().getOpposite());
        BlockState bvt12 = bvp.getMovedState();
        if (bvt12.isAir() || bvp.getProgress(float5) >= 1.0f) {
            return;
        }
        final Tesselator cuz13 = Tesselator.getInstance();
        final BufferBuilder cuw14 = cuz13.getBuilder();
        this.bindTexture(TextureAtlas.LOCATION_BLOCKS);
        Lighting.turnOff();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        if (Minecraft.useAmbientOcclusion()) {
            GlStateManager.shadeModel(7425);
        }
        else {
            GlStateManager.shadeModel(7424);
        }
        ModelBlockRenderer.enableCaching();
        cuw14.begin(7, DefaultVertexFormat.BLOCK);
        cuw14.offset(double2 - ew11.getX() + bvp.getXOff(float5), double3 - ew11.getY() + bvp.getYOff(float5), double4 - ew11.getZ() + bvp.getZOff(float5));
        final Level bhr15 = this.getLevel();
        if (bvt12.getBlock() == Blocks.PISTON_HEAD && bvp.getProgress(float5) <= 4.0f) {
            bvt12 = ((AbstractStateHolder<O, BlockState>)bvt12).<Comparable, Boolean>setValue((Property<Comparable>)PistonHeadBlock.SHORT, true);
            this.renderBlock(ew11, bvt12, cuw14, bhr15, false);
        }
        else if (bvp.isSourcePiston() && !bvp.isExtending()) {
            final PistonType bwv16 = (bvt12.getBlock() == Blocks.STICKY_PISTON) ? PistonType.STICKY : PistonType.DEFAULT;
            BlockState bvt13 = (((AbstractStateHolder<O, BlockState>)Blocks.PISTON_HEAD.defaultBlockState()).setValue(PistonHeadBlock.TYPE, bwv16)).<Comparable, Comparable>setValue((Property<Comparable>)PistonHeadBlock.FACING, (Comparable)bvt12.<V>getValue((Property<V>)PistonBaseBlock.FACING));
            bvt13 = ((AbstractStateHolder<O, BlockState>)bvt13).<Comparable, Boolean>setValue((Property<Comparable>)PistonHeadBlock.SHORT, bvp.getProgress(float5) >= 0.5f);
            this.renderBlock(ew11, bvt13, cuw14, bhr15, false);
            final BlockPos ew12 = ew11.relative(bvp.getMovementDirection());
            cuw14.offset(double2 - ew12.getX(), double3 - ew12.getY(), double4 - ew12.getZ());
            bvt12 = ((AbstractStateHolder<O, BlockState>)bvt12).<Comparable, Boolean>setValue((Property<Comparable>)PistonBaseBlock.EXTENDED, true);
            this.renderBlock(ew12, bvt12, cuw14, bhr15, true);
        }
        else {
            this.renderBlock(ew11, bvt12, cuw14, bhr15, false);
        }
        cuw14.offset(0.0, 0.0, 0.0);
        cuz13.end();
        ModelBlockRenderer.clearCache();
        Lighting.turnOn();
    }
    
    private boolean renderBlock(final BlockPos ew, final BlockState bvt, final BufferBuilder cuw, final Level bhr, final boolean boolean5) {
        return this.blockRenderer.getModelRenderer().tesselateBlock(bhr, this.blockRenderer.getBlockModel(bvt), bvt, ew, cuw, boolean5, new Random(), bvt.getSeed(ew));
    }
}
