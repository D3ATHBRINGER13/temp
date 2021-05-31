package net.minecraft.client;

import java.util.Collections;
import net.minecraft.world.inventory.SmokerMenu;
import net.minecraft.world.inventory.BlastFurnaceMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.crafting.RecipeType;
import java.util.Iterator;
import com.google.common.collect.Table;
import net.minecraft.world.item.crafting.Recipe;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import java.util.List;
import java.util.Map;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.stats.RecipeBook;

public class ClientRecipeBook extends RecipeBook {
    private final RecipeManager recipes;
    private final Map<RecipeBookCategories, List<RecipeCollection>> collectionsByTab;
    private final List<RecipeCollection> collections;
    
    public ClientRecipeBook(final RecipeManager bes) {
        this.collectionsByTab = (Map<RecipeBookCategories, List<RecipeCollection>>)Maps.newHashMap();
        this.collections = (List<RecipeCollection>)Lists.newArrayList();
        this.recipes = bes;
    }
    
    public void setupCollections() {
        this.collections.clear();
        this.collectionsByTab.clear();
        final Table<RecipeBookCategories, String, RecipeCollection> table2 = (Table<RecipeBookCategories, String, RecipeCollection>)HashBasedTable.create();
        for (final Recipe<?> ber4 : this.recipes.getRecipes()) {
            if (ber4.isSpecial()) {
                continue;
            }
            final RecipeBookCategories cyj5 = getCategory(ber4);
            final String string6 = ber4.getGroup();
            RecipeCollection dfc7;
            if (string6.isEmpty()) {
                dfc7 = this.createCollection(cyj5);
            }
            else {
                dfc7 = (RecipeCollection)table2.get(cyj5, string6);
                if (dfc7 == null) {
                    dfc7 = this.createCollection(cyj5);
                    table2.put(cyj5, string6, dfc7);
                }
            }
            dfc7.add(ber4);
        }
    }
    
    private RecipeCollection createCollection(final RecipeBookCategories cyj) {
        final RecipeCollection dfc3 = new RecipeCollection();
        this.collections.add(dfc3);
        ((List)this.collectionsByTab.computeIfAbsent(cyj, cyj -> Lists.newArrayList())).add(dfc3);
        if (cyj == RecipeBookCategories.FURNACE_BLOCKS || cyj == RecipeBookCategories.FURNACE_FOOD || cyj == RecipeBookCategories.FURNACE_MISC) {
            this.addToCollection(RecipeBookCategories.FURNACE_SEARCH, dfc3);
        }
        else if (cyj == RecipeBookCategories.BLAST_FURNACE_BLOCKS || cyj == RecipeBookCategories.BLAST_FURNACE_MISC) {
            this.addToCollection(RecipeBookCategories.BLAST_FURNACE_SEARCH, dfc3);
        }
        else if (cyj == RecipeBookCategories.SMOKER_FOOD) {
            this.addToCollection(RecipeBookCategories.SMOKER_SEARCH, dfc3);
        }
        else if (cyj == RecipeBookCategories.STONECUTTER) {
            this.addToCollection(RecipeBookCategories.STONECUTTER, dfc3);
        }
        else if (cyj == RecipeBookCategories.CAMPFIRE) {
            this.addToCollection(RecipeBookCategories.CAMPFIRE, dfc3);
        }
        else {
            this.addToCollection(RecipeBookCategories.SEARCH, dfc3);
        }
        return dfc3;
    }
    
    private void addToCollection(final RecipeBookCategories cyj, final RecipeCollection dfc) {
        ((List)this.collectionsByTab.computeIfAbsent(cyj, cyj -> Lists.newArrayList())).add(dfc);
    }
    
    private static RecipeBookCategories getCategory(final Recipe<?> ber) {
        final RecipeType<?> beu2 = ber.getType();
        if (beu2 == RecipeType.SMELTING) {
            if (ber.getResultItem().getItem().isEdible()) {
                return RecipeBookCategories.FURNACE_FOOD;
            }
            if (ber.getResultItem().getItem() instanceof BlockItem) {
                return RecipeBookCategories.FURNACE_BLOCKS;
            }
            return RecipeBookCategories.FURNACE_MISC;
        }
        else if (beu2 == RecipeType.BLASTING) {
            if (ber.getResultItem().getItem() instanceof BlockItem) {
                return RecipeBookCategories.BLAST_FURNACE_BLOCKS;
            }
            return RecipeBookCategories.BLAST_FURNACE_MISC;
        }
        else {
            if (beu2 == RecipeType.SMOKING) {
                return RecipeBookCategories.SMOKER_FOOD;
            }
            if (beu2 == RecipeType.STONECUTTING) {
                return RecipeBookCategories.STONECUTTER;
            }
            if (beu2 == RecipeType.CAMPFIRE_COOKING) {
                return RecipeBookCategories.CAMPFIRE;
            }
            final ItemStack bcj3 = ber.getResultItem();
            final CreativeModeTab bba4 = bcj3.getItem().getItemCategory();
            if (bba4 == CreativeModeTab.TAB_BUILDING_BLOCKS) {
                return RecipeBookCategories.BUILDING_BLOCKS;
            }
            if (bba4 == CreativeModeTab.TAB_TOOLS || bba4 == CreativeModeTab.TAB_COMBAT) {
                return RecipeBookCategories.EQUIPMENT;
            }
            if (bba4 == CreativeModeTab.TAB_REDSTONE) {
                return RecipeBookCategories.REDSTONE;
            }
            return RecipeBookCategories.MISC;
        }
    }
    
    public static List<RecipeBookCategories> getCategories(final RecipeBookMenu<?> azq) {
        if (azq instanceof CraftingMenu || azq instanceof InventoryMenu) {
            return (List<RecipeBookCategories>)Lists.newArrayList((Object[])new RecipeBookCategories[] { RecipeBookCategories.SEARCH, RecipeBookCategories.EQUIPMENT, RecipeBookCategories.BUILDING_BLOCKS, RecipeBookCategories.MISC, RecipeBookCategories.REDSTONE });
        }
        if (azq instanceof FurnaceMenu) {
            return (List<RecipeBookCategories>)Lists.newArrayList((Object[])new RecipeBookCategories[] { RecipeBookCategories.FURNACE_SEARCH, RecipeBookCategories.FURNACE_FOOD, RecipeBookCategories.FURNACE_BLOCKS, RecipeBookCategories.FURNACE_MISC });
        }
        if (azq instanceof BlastFurnaceMenu) {
            return (List<RecipeBookCategories>)Lists.newArrayList((Object[])new RecipeBookCategories[] { RecipeBookCategories.BLAST_FURNACE_SEARCH, RecipeBookCategories.BLAST_FURNACE_BLOCKS, RecipeBookCategories.BLAST_FURNACE_MISC });
        }
        if (azq instanceof SmokerMenu) {
            return (List<RecipeBookCategories>)Lists.newArrayList((Object[])new RecipeBookCategories[] { RecipeBookCategories.SMOKER_SEARCH, RecipeBookCategories.SMOKER_FOOD });
        }
        return (List<RecipeBookCategories>)Lists.newArrayList();
    }
    
    public List<RecipeCollection> getCollections() {
        return this.collections;
    }
    
    public List<RecipeCollection> getCollection(final RecipeBookCategories cyj) {
        return (List<RecipeCollection>)this.collectionsByTab.getOrDefault(cyj, Collections.emptyList());
    }
}
