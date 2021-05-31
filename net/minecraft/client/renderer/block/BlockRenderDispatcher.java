package net.minecraft.client.renderer.block;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.ReportedException;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReport;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.resources.model.BakedModel;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.BlockAndBiomeGetter;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.color.block.BlockColors;
import java.util.Random;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class BlockRenderDispatcher implements ResourceManagerReloadListener {
    private final BlockModelShaper blockModelShaper;
    private final ModelBlockRenderer modelRenderer;
    private final AnimatedEntityBlockRenderer entityBlockRenderer;
    private final LiquidBlockRenderer liquidBlockRenderer;
    private final Random random;
    
    public BlockRenderDispatcher(final BlockModelShaper dnv, final BlockColors cyp) {
        this.entityBlockRenderer = new AnimatedEntityBlockRenderer();
        this.random = new Random();
        this.blockModelShaper = dnv;
        this.modelRenderer = new ModelBlockRenderer(cyp);
        this.liquidBlockRenderer = new LiquidBlockRenderer();
    }
    
    public BlockModelShaper getBlockModelShaper() {
        return this.blockModelShaper;
    }
    
    public void renderBreakingTexture(final BlockState bvt, final BlockPos ew, final TextureAtlasSprite dxb, final BlockAndBiomeGetter bgz) {
        if (bvt.getRenderShape() != RenderShape.MODEL) {
            return;
        }
        final BakedModel dyp6 = this.blockModelShaper.getBlockModel(bvt);
        final long long7 = bvt.getSeed(ew);
        final BakedModel dyp7 = new SimpleBakedModel.Builder(bvt, dyp6, dxb, this.random, long7).build();
        this.modelRenderer.tesselateBlock(bgz, dyp7, bvt, ew, Tesselator.getInstance().getBuilder(), true, this.random, long7);
    }
    
    public boolean renderBatched(final BlockState bvt, final BlockPos ew, final BlockAndBiomeGetter bgz, final BufferBuilder cuw, final Random random) {
        try {
            final RenderShape brd7 = bvt.getRenderShape();
            if (brd7 == RenderShape.INVISIBLE) {
                return false;
            }
            switch (brd7) {
                case MODEL: {
                    return this.modelRenderer.tesselateBlock(bgz, this.getBlockModel(bvt), bvt, ew, cuw, true, random, bvt.getSeed(ew));
                }
                case ENTITYBLOCK_ANIMATED: {
                    return false;
                }
            }
        }
        catch (Throwable throwable7) {
            final CrashReport d8 = CrashReport.forThrowable(throwable7, "Tesselating block in world");
            final CrashReportCategory e9 = d8.addCategory("Block being tesselated");
            CrashReportCategory.populateBlockDetails(e9, ew, bvt);
            throw new ReportedException(d8);
        }
        return false;
    }
    
    public boolean renderLiquid(final BlockPos ew, final BlockAndBiomeGetter bgz, final BufferBuilder cuw, final FluidState clk) {
        try {
            return this.liquidBlockRenderer.tesselate(bgz, ew, cuw, clk);
        }
        catch (Throwable throwable6) {
            final CrashReport d7 = CrashReport.forThrowable(throwable6, "Tesselating liquid in world");
            final CrashReportCategory e8 = d7.addCategory("Block being tesselated");
            CrashReportCategory.populateBlockDetails(e8, ew, null);
            throw new ReportedException(d7);
        }
    }
    
    public ModelBlockRenderer getModelRenderer() {
        return this.modelRenderer;
    }
    
    public BakedModel getBlockModel(final BlockState bvt) {
        return this.blockModelShaper.getBlockModel(bvt);
    }
    
    public void renderSingleBlock(final BlockState bvt, final float float2) {
        final RenderShape brd4 = bvt.getRenderShape();
        if (brd4 == RenderShape.INVISIBLE) {
            return;
        }
        switch (brd4) {
            case MODEL: {
                final BakedModel dyp5 = this.getBlockModel(bvt);
                this.modelRenderer.renderSingleBlock(dyp5, bvt, float2, true);
                break;
            }
            case ENTITYBLOCK_ANIMATED: {
                this.entityBlockRenderer.renderSingleBlock(bvt.getBlock(), float2);
                break;
            }
        }
    }
    
    public void onResourceManagerReload(final ResourceManager xi) {
        this.liquidBlockRenderer.setupSprites();
    }
}
