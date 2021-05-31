package net.minecraft.client.renderer;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.ItemLike;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

public class ItemModelShaper {
    public final Int2ObjectMap<ModelResourceLocation> shapes;
    private final Int2ObjectMap<BakedModel> shapesCache;
    private final ModelManager modelManager;
    
    public ItemModelShaper(final ModelManager dyt) {
        this.shapes = (Int2ObjectMap<ModelResourceLocation>)new Int2ObjectOpenHashMap(256);
        this.shapesCache = (Int2ObjectMap<BakedModel>)new Int2ObjectOpenHashMap(256);
        this.modelManager = dyt;
    }
    
    public TextureAtlasSprite getParticleIcon(final ItemLike bhq) {
        return this.getParticleIcon(new ItemStack(bhq));
    }
    
    public TextureAtlasSprite getParticleIcon(final ItemStack bcj) {
        final BakedModel dyp3 = this.getItemModel(bcj);
        if ((dyp3 == this.modelManager.getMissingModel() || dyp3.isCustomRenderer()) && bcj.getItem() instanceof BlockItem) {
            return this.modelManager.getBlockModelShaper().getParticleIcon(((BlockItem)bcj.getItem()).getBlock().defaultBlockState());
        }
        return dyp3.getParticleIcon();
    }
    
    public BakedModel getItemModel(final ItemStack bcj) {
        final BakedModel dyp3 = this.getItemModel(bcj.getItem());
        return (dyp3 == null) ? this.modelManager.getMissingModel() : dyp3;
    }
    
    @Nullable
    public BakedModel getItemModel(final Item bce) {
        return (BakedModel)this.shapesCache.get(getIndex(bce));
    }
    
    private static int getIndex(final Item bce) {
        return Item.getId(bce);
    }
    
    public void register(final Item bce, final ModelResourceLocation dyu) {
        this.shapes.put(getIndex(bce), dyu);
    }
    
    public ModelManager getModelManager() {
        return this.modelManager;
    }
    
    public void rebuildCache() {
        this.shapesCache.clear();
        for (final Map.Entry<Integer, ModelResourceLocation> entry3 : this.shapes.entrySet()) {
            this.shapesCache.put((Integer)entry3.getKey(), this.modelManager.getModel((ModelResourceLocation)entry3.getValue()));
        }
    }
}
