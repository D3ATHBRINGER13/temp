package net.minecraft.client.renderer.block;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.resources.model.ModelResourceLocation;
import java.util.Iterator;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.Registry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.google.common.collect.Maps;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Map;

public class BlockModelShaper {
    private final Map<BlockState, BakedModel> modelByStateCache;
    private final ModelManager modelManager;
    
    public BlockModelShaper(final ModelManager dyt) {
        this.modelByStateCache = (Map<BlockState, BakedModel>)Maps.newIdentityHashMap();
        this.modelManager = dyt;
    }
    
    public TextureAtlasSprite getParticleIcon(final BlockState bvt) {
        return this.getBlockModel(bvt).getParticleIcon();
    }
    
    public BakedModel getBlockModel(final BlockState bvt) {
        BakedModel dyp3 = (BakedModel)this.modelByStateCache.get(bvt);
        if (dyp3 == null) {
            dyp3 = this.modelManager.getMissingModel();
        }
        return dyp3;
    }
    
    public ModelManager getModelManager() {
        return this.modelManager;
    }
    
    public void rebuildCache() {
        this.modelByStateCache.clear();
        for (final Block bmv3 : Registry.BLOCK) {
            bmv3.getStateDefinition().getPossibleStates().forEach(bvt -> {
                final BakedModel bakedModel = (BakedModel)this.modelByStateCache.put(bvt, this.modelManager.getModel(stateToModelLocation(bvt)));
            });
        }
    }
    
    public static ModelResourceLocation stateToModelLocation(final BlockState bvt) {
        return stateToModelLocation(Registry.BLOCK.getKey(bvt.getBlock()), bvt);
    }
    
    public static ModelResourceLocation stateToModelLocation(final ResourceLocation qv, final BlockState bvt) {
        return new ModelResourceLocation(qv, statePropertiesToString((Map<Property<?>, Comparable<?>>)bvt.getValues()));
    }
    
    public static String statePropertiesToString(final Map<Property<?>, Comparable<?>> map) {
        final StringBuilder stringBuilder2 = new StringBuilder();
        for (final Map.Entry<Property<?>, Comparable<?>> entry4 : map.entrySet()) {
            if (stringBuilder2.length() != 0) {
                stringBuilder2.append(',');
            }
            final Property<?> bww5 = entry4.getKey();
            stringBuilder2.append(bww5.getName());
            stringBuilder2.append('=');
            stringBuilder2.append(BlockModelShaper.getValue(bww5, entry4.getValue()));
        }
        return stringBuilder2.toString();
    }
    
    private static <T extends Comparable<T>> String getValue(final Property<T> bww, final Comparable<?> comparable) {
        return bww.getName((T)comparable);
    }
}
