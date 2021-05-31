package net.minecraft.client.searchtree;

import java.util.Iterator;
import net.minecraft.server.packs.resources.ResourceManager;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class SearchRegistry implements ResourceManagerReloadListener {
    public static final Key<ItemStack> CREATIVE_NAMES;
    public static final Key<ItemStack> CREATIVE_TAGS;
    public static final Key<RecipeCollection> RECIPE_COLLECTIONS;
    private final Map<Key<?>, MutableSearchTree<?>> searchTrees;
    
    public SearchRegistry() {
        this.searchTrees = (Map<Key<?>, MutableSearchTree<?>>)Maps.newHashMap();
    }
    
    public void onResourceManagerReload(final ResourceManager xi) {
        for (final MutableSearchTree<?> dzu4 : this.searchTrees.values()) {
            dzu4.refresh();
        }
    }
    
    public <T> void register(final Key<T> a, final MutableSearchTree<T> dzu) {
        this.searchTrees.put(a, dzu);
    }
    
    public <T> MutableSearchTree<T> getTree(final Key<T> a) {
        return (MutableSearchTree<T>)this.searchTrees.get(a);
    }
    
    static {
        CREATIVE_NAMES = new Key<ItemStack>();
        CREATIVE_TAGS = new Key<ItemStack>();
        RECIPE_COLLECTIONS = new Key<RecipeCollection>();
    }
    
    public static class Key<T> {
    }
}
