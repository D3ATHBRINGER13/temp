package net.minecraft.world.item.crafting;

import net.minecraft.world.Container;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.resources.ResourceLocation;

public class FireworkRocketRecipe extends CustomRecipe {
    private static final Ingredient PAPER_INGREDIENT;
    private static final Ingredient GUNPOWDER_INGREDIENT;
    private static final Ingredient STAR_INGREDIENT;
    
    public FireworkRocketRecipe(final ResourceLocation qv) {
        super(qv);
    }
    
    public boolean matches(final CraftingContainer ayw, final Level bhr) {
        boolean boolean4 = false;
        int integer5 = 0;
        for (int integer6 = 0; integer6 < ayw.getContainerSize(); ++integer6) {
            final ItemStack bcj7 = ayw.getItem(integer6);
            if (!bcj7.isEmpty()) {
                if (FireworkRocketRecipe.PAPER_INGREDIENT.test(bcj7)) {
                    if (boolean4) {
                        return false;
                    }
                    boolean4 = true;
                }
                else if (FireworkRocketRecipe.GUNPOWDER_INGREDIENT.test(bcj7)) {
                    if (++integer5 > 3) {
                        return false;
                    }
                }
                else if (!FireworkRocketRecipe.STAR_INGREDIENT.test(bcj7)) {
                    return false;
                }
            }
        }
        return boolean4 && integer5 >= 1;
    }
    
    public ItemStack assemble(final CraftingContainer ayw) {
        final ItemStack bcj3 = new ItemStack(Items.FIREWORK_ROCKET, 3);
        final CompoundTag id4 = bcj3.getOrCreateTagElement("Fireworks");
        final ListTag ik5 = new ListTag();
        int integer6 = 0;
        for (int integer7 = 0; integer7 < ayw.getContainerSize(); ++integer7) {
            final ItemStack bcj4 = ayw.getItem(integer7);
            if (!bcj4.isEmpty()) {
                if (FireworkRocketRecipe.GUNPOWDER_INGREDIENT.test(bcj4)) {
                    ++integer6;
                }
                else if (FireworkRocketRecipe.STAR_INGREDIENT.test(bcj4)) {
                    final CompoundTag id5 = bcj4.getTagElement("Explosion");
                    if (id5 != null) {
                        ik5.add(id5);
                    }
                }
            }
        }
        id4.putByte("Flight", (byte)integer6);
        if (!ik5.isEmpty()) {
            id4.put("Explosions", (Tag)ik5);
        }
        return bcj3;
    }
    
    public boolean canCraftInDimensions(final int integer1, final int integer2) {
        return integer1 * integer2 >= 2;
    }
    
    @Override
    public ItemStack getResultItem() {
        return new ItemStack(Items.FIREWORK_ROCKET);
    }
    
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.FIREWORK_ROCKET;
    }
    
    static {
        PAPER_INGREDIENT = Ingredient.of(Items.PAPER);
        GUNPOWDER_INGREDIENT = Ingredient.of(Items.GUNPOWDER);
        STAR_INGREDIENT = Ingredient.of(Items.FIREWORK_STAR);
    }
}
