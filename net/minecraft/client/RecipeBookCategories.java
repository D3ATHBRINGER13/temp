package net.minecraft.client;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.item.ItemStack;
import java.util.List;

public enum RecipeBookCategories {
    SEARCH(new ItemStack[] { new ItemStack(Items.COMPASS) }), 
    BUILDING_BLOCKS(new ItemStack[] { new ItemStack(Blocks.BRICKS) }), 
    REDSTONE(new ItemStack[] { new ItemStack(Items.REDSTONE) }), 
    EQUIPMENT(new ItemStack[] { new ItemStack(Items.IRON_AXE), new ItemStack(Items.GOLDEN_SWORD) }), 
    MISC(new ItemStack[] { new ItemStack(Items.LAVA_BUCKET), new ItemStack(Items.APPLE) }), 
    FURNACE_SEARCH(new ItemStack[] { new ItemStack(Items.COMPASS) }), 
    FURNACE_FOOD(new ItemStack[] { new ItemStack(Items.PORKCHOP) }), 
    FURNACE_BLOCKS(new ItemStack[] { new ItemStack(Blocks.STONE) }), 
    FURNACE_MISC(new ItemStack[] { new ItemStack(Items.LAVA_BUCKET), new ItemStack(Items.EMERALD) }), 
    BLAST_FURNACE_SEARCH(new ItemStack[] { new ItemStack(Items.COMPASS) }), 
    BLAST_FURNACE_BLOCKS(new ItemStack[] { new ItemStack(Blocks.REDSTONE_ORE) }), 
    BLAST_FURNACE_MISC(new ItemStack[] { new ItemStack(Items.IRON_SHOVEL), new ItemStack(Items.GOLDEN_LEGGINGS) }), 
    SMOKER_SEARCH(new ItemStack[] { new ItemStack(Items.COMPASS) }), 
    SMOKER_FOOD(new ItemStack[] { new ItemStack(Items.PORKCHOP) }), 
    STONECUTTER(new ItemStack[] { new ItemStack(Items.CHISELED_STONE_BRICKS) }), 
    CAMPFIRE(new ItemStack[] { new ItemStack(Items.PORKCHOP) });
    
    private final List<ItemStack> itemIcons;
    
    private RecipeBookCategories(final ItemStack[] arr) {
        this.itemIcons = (List<ItemStack>)ImmutableList.copyOf((Object[])arr);
    }
    
    public List<ItemStack> getIconItems() {
        return this.itemIcons;
    }
}
