package net.minecraft.client.gui.screens.recipebook;

import net.minecraft.world.item.ItemStack;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.entity.player.StackedContents;
import java.util.Iterator;
import net.minecraft.stats.RecipeBook;
import com.google.common.collect.Sets;
import com.google.common.collect.Lists;
import java.util.Set;
import net.minecraft.world.item.crafting.Recipe;
import java.util.List;

public class RecipeCollection {
    private final List<Recipe<?>> recipes;
    private final Set<Recipe<?>> craftable;
    private final Set<Recipe<?>> fitsDimensions;
    private final Set<Recipe<?>> known;
    private boolean singleResultItem;
    
    public RecipeCollection() {
        this.recipes = (List<Recipe<?>>)Lists.newArrayList();
        this.craftable = (Set<Recipe<?>>)Sets.newHashSet();
        this.fitsDimensions = (Set<Recipe<?>>)Sets.newHashSet();
        this.known = (Set<Recipe<?>>)Sets.newHashSet();
        this.singleResultItem = true;
    }
    
    public boolean hasKnownRecipes() {
        return !this.known.isEmpty();
    }
    
    public void updateKnownRecipes(final RecipeBook ys) {
        for (final Recipe<?> ber4 : this.recipes) {
            if (ys.contains(ber4)) {
                this.known.add(ber4);
            }
        }
    }
    
    public void canCraft(final StackedContents awi, final int integer2, final int integer3, final RecipeBook ys) {
        for (int integer4 = 0; integer4 < this.recipes.size(); ++integer4) {
            final Recipe<?> ber7 = this.recipes.get(integer4);
            final boolean boolean8 = ber7.canCraftInDimensions(integer2, integer3) && ys.contains(ber7);
            if (boolean8) {
                this.fitsDimensions.add(ber7);
            }
            else {
                this.fitsDimensions.remove(ber7);
            }
            if (boolean8 && awi.canCraft(ber7, null)) {
                this.craftable.add(ber7);
            }
            else {
                this.craftable.remove(ber7);
            }
        }
    }
    
    public boolean isCraftable(final Recipe<?> ber) {
        return this.craftable.contains(ber);
    }
    
    public boolean hasCraftable() {
        return !this.craftable.isEmpty();
    }
    
    public boolean hasFitting() {
        return !this.fitsDimensions.isEmpty();
    }
    
    public List<Recipe<?>> getRecipes() {
        return this.recipes;
    }
    
    public List<Recipe<?>> getRecipes(final boolean boolean1) {
        final List<Recipe<?>> list3 = (List<Recipe<?>>)Lists.newArrayList();
        final Set<Recipe<?>> set4 = boolean1 ? this.craftable : this.fitsDimensions;
        for (final Recipe<?> ber6 : this.recipes) {
            if (set4.contains(ber6)) {
                list3.add(ber6);
            }
        }
        return list3;
    }
    
    public List<Recipe<?>> getDisplayRecipes(final boolean boolean1) {
        final List<Recipe<?>> list3 = (List<Recipe<?>>)Lists.newArrayList();
        for (final Recipe<?> ber5 : this.recipes) {
            if (this.fitsDimensions.contains(ber5) && this.craftable.contains(ber5) == boolean1) {
                list3.add(ber5);
            }
        }
        return list3;
    }
    
    public void add(final Recipe<?> ber) {
        this.recipes.add(ber);
        if (this.singleResultItem) {
            final ItemStack bcj3 = ((Recipe)this.recipes.get(0)).getResultItem();
            final ItemStack bcj4 = ber.getResultItem();
            this.singleResultItem = (ItemStack.isSame(bcj3, bcj4) && ItemStack.tagMatches(bcj3, bcj4));
        }
    }
    
    public boolean hasSingleResultItem() {
        return this.singleResultItem;
    }
}
