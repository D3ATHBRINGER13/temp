package net.minecraft.client.resources.model;

import net.minecraft.world.level.material.FluidState;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.state.BlockState;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;

public class ModelManager extends SimplePreparableReloadListener<ModelBakery> {
    private Map<ResourceLocation, BakedModel> bakedRegistry;
    private final TextureAtlas terrainAtlas;
    private final BlockModelShaper blockModelShaper;
    private final BlockColors blockColors;
    private BakedModel missingModel;
    private Object2IntMap<BlockState> modelGroups;
    
    public ModelManager(final TextureAtlas dxa, final BlockColors cyp) {
        this.terrainAtlas = dxa;
        this.blockColors = cyp;
        this.blockModelShaper = new BlockModelShaper(this);
    }
    
    public BakedModel getModel(final ModelResourceLocation dyu) {
        return (BakedModel)this.bakedRegistry.getOrDefault(dyu, this.missingModel);
    }
    
    public BakedModel getMissingModel() {
        return this.missingModel;
    }
    
    public BlockModelShaper getBlockModelShaper() {
        return this.blockModelShaper;
    }
    
    @Override
    protected ModelBakery prepare(final ResourceManager xi, final ProfilerFiller agn) {
        agn.startTick();
        final ModelBakery dys4 = new ModelBakery(xi, this.terrainAtlas, this.blockColors, agn);
        agn.endTick();
        return dys4;
    }
    
    @Override
    protected void apply(final ModelBakery dys, final ResourceManager xi, final ProfilerFiller agn) {
        agn.startTick();
        agn.push("upload");
        dys.uploadTextures(agn);
        this.bakedRegistry = dys.getBakedTopLevelModels();
        this.modelGroups = dys.getModelGroups();
        this.missingModel = (BakedModel)this.bakedRegistry.get(ModelBakery.MISSING_MODEL_LOCATION);
        agn.popPush("cache");
        this.blockModelShaper.rebuildCache();
        agn.pop();
        agn.endTick();
    }
    
    public boolean requiresRender(final BlockState bvt1, final BlockState bvt2) {
        if (bvt1 == bvt2) {
            return false;
        }
        final int integer4 = this.modelGroups.getInt(bvt1);
        if (integer4 != -1) {
            final int integer5 = this.modelGroups.getInt(bvt2);
            if (integer4 == integer5) {
                final FluidState clk6 = bvt1.getFluidState();
                final FluidState clk7 = bvt2.getFluidState();
                return clk6 != clk7;
            }
        }
        return true;
    }
}
