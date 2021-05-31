package net.minecraft.stats;

import net.minecraft.world.inventory.SmokerMenu;
import net.minecraft.world.inventory.BlastFurnaceMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import javax.annotation.Nullable;
import net.minecraft.world.item.crafting.Recipe;
import java.util.Collection;
import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceLocation;
import java.util.Set;

public class RecipeBook {
    protected final Set<ResourceLocation> known;
    protected final Set<ResourceLocation> highlight;
    protected boolean guiOpen;
    protected boolean filteringCraftable;
    protected boolean furnaceGuiOpen;
    protected boolean furnaceFilteringCraftable;
    protected boolean blastingFurnaceGuiOpen;
    protected boolean blastingFurnaceFilteringCraftable;
    protected boolean smokerGuiOpen;
    protected boolean smokerFilteringCraftable;
    
    public RecipeBook() {
        this.known = (Set<ResourceLocation>)Sets.newHashSet();
        this.highlight = (Set<ResourceLocation>)Sets.newHashSet();
    }
    
    public void copyOverData(final RecipeBook ys) {
        this.known.clear();
        this.highlight.clear();
        this.known.addAll((Collection)ys.known);
        this.highlight.addAll((Collection)ys.highlight);
    }
    
    public void add(final Recipe<?> ber) {
        if (!ber.isSpecial()) {
            this.add(ber.getId());
        }
    }
    
    protected void add(final ResourceLocation qv) {
        this.known.add(qv);
    }
    
    public boolean contains(@Nullable final Recipe<?> ber) {
        return ber != null && this.known.contains(ber.getId());
    }
    
    public void remove(final Recipe<?> ber) {
        this.remove(ber.getId());
    }
    
    protected void remove(final ResourceLocation qv) {
        this.known.remove(qv);
        this.highlight.remove(qv);
    }
    
    public boolean willHighlight(final Recipe<?> ber) {
        return this.highlight.contains(ber.getId());
    }
    
    public void removeHighlight(final Recipe<?> ber) {
        this.highlight.remove(ber.getId());
    }
    
    public void addHighlight(final Recipe<?> ber) {
        this.addHighlight(ber.getId());
    }
    
    protected void addHighlight(final ResourceLocation qv) {
        this.highlight.add(qv);
    }
    
    public boolean isGuiOpen() {
        return this.guiOpen;
    }
    
    public void setGuiOpen(final boolean boolean1) {
        this.guiOpen = boolean1;
    }
    
    public boolean isFilteringCraftable(final RecipeBookMenu<?> azq) {
        if (azq instanceof FurnaceMenu) {
            return this.furnaceFilteringCraftable;
        }
        if (azq instanceof BlastFurnaceMenu) {
            return this.blastingFurnaceFilteringCraftable;
        }
        if (azq instanceof SmokerMenu) {
            return this.smokerFilteringCraftable;
        }
        return this.filteringCraftable;
    }
    
    public boolean isFilteringCraftable() {
        return this.filteringCraftable;
    }
    
    public void setFilteringCraftable(final boolean boolean1) {
        this.filteringCraftable = boolean1;
    }
    
    public boolean isFurnaceGuiOpen() {
        return this.furnaceGuiOpen;
    }
    
    public void setFurnaceGuiOpen(final boolean boolean1) {
        this.furnaceGuiOpen = boolean1;
    }
    
    public boolean isFurnaceFilteringCraftable() {
        return this.furnaceFilteringCraftable;
    }
    
    public void setFurnaceFilteringCraftable(final boolean boolean1) {
        this.furnaceFilteringCraftable = boolean1;
    }
    
    public boolean isBlastingFurnaceGuiOpen() {
        return this.blastingFurnaceGuiOpen;
    }
    
    public void setBlastingFurnaceGuiOpen(final boolean boolean1) {
        this.blastingFurnaceGuiOpen = boolean1;
    }
    
    public boolean isBlastingFurnaceFilteringCraftable() {
        return this.blastingFurnaceFilteringCraftable;
    }
    
    public void setBlastingFurnaceFilteringCraftable(final boolean boolean1) {
        this.blastingFurnaceFilteringCraftable = boolean1;
    }
    
    public boolean isSmokerGuiOpen() {
        return this.smokerGuiOpen;
    }
    
    public void setSmokerGuiOpen(final boolean boolean1) {
        this.smokerGuiOpen = boolean1;
    }
    
    public boolean isSmokerFilteringCraftable() {
        return this.smokerFilteringCraftable;
    }
    
    public void setSmokerFilteringCraftable(final boolean boolean1) {
        this.smokerFilteringCraftable = boolean1;
    }
}
