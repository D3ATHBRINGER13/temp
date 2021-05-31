package net.minecraft.world.item.crafting;

import java.util.Iterator;
import net.minecraft.network.FriendlyByteBuf;
import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonObject;
import net.minecraft.world.Container;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

public class ShapelessRecipe implements CraftingRecipe {
    private final ResourceLocation id;
    private final String group;
    private final ItemStack result;
    private final NonNullList<Ingredient> ingredients;
    
    public ShapelessRecipe(final ResourceLocation qv, final String string, final ItemStack bcj, final NonNullList<Ingredient> fk) {
        this.id = qv;
        this.group = string;
        this.result = bcj;
        this.ingredients = fk;
    }
    
    public ResourceLocation getId() {
        return this.id;
    }
    
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHAPELESS_RECIPE;
    }
    
    public String getGroup() {
        return this.group;
    }
    
    public ItemStack getResultItem() {
        return this.result;
    }
    
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }
    
    public boolean matches(final CraftingContainer ayw, final Level bhr) {
        final StackedContents awi4 = new StackedContents();
        int integer5 = 0;
        for (int integer6 = 0; integer6 < ayw.getContainerSize(); ++integer6) {
            final ItemStack bcj7 = ayw.getItem(integer6);
            if (!bcj7.isEmpty()) {
                ++integer5;
                awi4.accountStack(bcj7, 1);
            }
        }
        return integer5 == this.ingredients.size() && awi4.canCraft(this, null);
    }
    
    public ItemStack assemble(final CraftingContainer ayw) {
        return this.result.copy();
    }
    
    public boolean canCraftInDimensions(final int integer1, final int integer2) {
        return integer1 * integer2 >= this.ingredients.size();
    }
    
    public static class Serializer implements RecipeSerializer<ShapelessRecipe> {
        public ShapelessRecipe fromJson(final ResourceLocation qv, final JsonObject jsonObject) {
            final String string4 = GsonHelper.getAsString(jsonObject, "group", "");
            final NonNullList<Ingredient> fk5 = itemsFromJson(GsonHelper.getAsJsonArray(jsonObject, "ingredients"));
            if (fk5.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            }
            if (fk5.size() > 9) {
                throw new JsonParseException("Too many ingredients for shapeless recipe");
            }
            final ItemStack bcj6 = ShapedRecipe.itemFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
            return new ShapelessRecipe(qv, string4, bcj6, fk5);
        }
        
        private static NonNullList<Ingredient> itemsFromJson(final JsonArray jsonArray) {
            final NonNullList<Ingredient> fk2 = NonNullList.<Ingredient>create();
            for (int integer3 = 0; integer3 < jsonArray.size(); ++integer3) {
                final Ingredient beo4 = Ingredient.fromJson(jsonArray.get(integer3));
                if (!beo4.isEmpty()) {
                    fk2.add(beo4);
                }
            }
            return fk2;
        }
        
        public ShapelessRecipe fromNetwork(final ResourceLocation qv, final FriendlyByteBuf je) {
            final String string4 = je.readUtf(32767);
            final int integer5 = je.readVarInt();
            final NonNullList<Ingredient> fk6 = NonNullList.<Ingredient>withSize(integer5, Ingredient.EMPTY);
            for (int integer6 = 0; integer6 < fk6.size(); ++integer6) {
                fk6.set(integer6, Ingredient.fromNetwork(je));
            }
            final ItemStack bcj7 = je.readItem();
            return new ShapelessRecipe(qv, string4, bcj7, fk6);
        }
        
        public void toNetwork(final FriendlyByteBuf je, final ShapelessRecipe bex) {
            je.writeUtf(bex.group);
            je.writeVarInt(bex.ingredients.size());
            for (final Ingredient beo5 : bex.ingredients) {
                beo5.toNetwork(je);
            }
            je.writeItem(bex.result);
        }
    }
}
