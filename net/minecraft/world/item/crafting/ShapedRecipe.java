package net.minecraft.world.item.crafting;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.level.ItemLike;
import com.google.gson.JsonParseException;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import java.util.Iterator;
import com.google.gson.JsonElement;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonArray;
import com.google.common.annotations.VisibleForTesting;
import java.util.Set;
import com.google.gson.JsonSyntaxException;
import com.google.common.collect.Sets;
import java.util.Map;
import net.minecraft.world.level.Level;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;

public class ShapedRecipe implements CraftingRecipe {
    private final int width;
    private final int height;
    private final NonNullList<Ingredient> recipeItems;
    private final ItemStack result;
    private final ResourceLocation id;
    private final String group;
    
    public ShapedRecipe(final ResourceLocation qv, final String string, final int integer3, final int integer4, final NonNullList<Ingredient> fk, final ItemStack bcj) {
        this.id = qv;
        this.group = string;
        this.width = integer3;
        this.height = integer4;
        this.recipeItems = fk;
        this.result = bcj;
    }
    
    public ResourceLocation getId() {
        return this.id;
    }
    
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHAPED_RECIPE;
    }
    
    public String getGroup() {
        return this.group;
    }
    
    public ItemStack getResultItem() {
        return this.result;
    }
    
    public NonNullList<Ingredient> getIngredients() {
        return this.recipeItems;
    }
    
    public boolean canCraftInDimensions(final int integer1, final int integer2) {
        return integer1 >= this.width && integer2 >= this.height;
    }
    
    public boolean matches(final CraftingContainer ayw, final Level bhr) {
        for (int integer4 = 0; integer4 <= ayw.getWidth() - this.width; ++integer4) {
            for (int integer5 = 0; integer5 <= ayw.getHeight() - this.height; ++integer5) {
                if (this.matches(ayw, integer4, integer5, true)) {
                    return true;
                }
                if (this.matches(ayw, integer4, integer5, false)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean matches(final CraftingContainer ayw, final int integer2, final int integer3, final boolean boolean4) {
        for (int integer4 = 0; integer4 < ayw.getWidth(); ++integer4) {
            for (int integer5 = 0; integer5 < ayw.getHeight(); ++integer5) {
                final int integer6 = integer4 - integer2;
                final int integer7 = integer5 - integer3;
                Ingredient beo10 = Ingredient.EMPTY;
                if (integer6 >= 0 && integer7 >= 0 && integer6 < this.width && integer7 < this.height) {
                    if (boolean4) {
                        beo10 = this.recipeItems.get(this.width - integer6 - 1 + integer7 * this.width);
                    }
                    else {
                        beo10 = this.recipeItems.get(integer6 + integer7 * this.width);
                    }
                }
                if (!beo10.test(ayw.getItem(integer4 + integer5 * ayw.getWidth()))) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public ItemStack assemble(final CraftingContainer ayw) {
        return this.getResultItem().copy();
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    private static NonNullList<Ingredient> dissolvePattern(final String[] arr, final Map<String, Ingredient> map, final int integer3, final int integer4) {
        final NonNullList<Ingredient> fk5 = NonNullList.<Ingredient>withSize(integer3 * integer4, Ingredient.EMPTY);
        final Set<String> set6 = (Set<String>)Sets.newHashSet((Iterable)map.keySet());
        set6.remove(" ");
        for (int integer5 = 0; integer5 < arr.length; ++integer5) {
            for (int integer6 = 0; integer6 < arr[integer5].length(); ++integer6) {
                final String string9 = arr[integer5].substring(integer6, integer6 + 1);
                final Ingredient beo10 = (Ingredient)map.get(string9);
                if (beo10 == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + string9 + "' but it's not defined in the key");
                }
                set6.remove(string9);
                fk5.set(integer6 + integer3 * integer5, beo10);
            }
        }
        if (!set6.isEmpty()) {
            throw new JsonSyntaxException(new StringBuilder().append("Key defines symbols that aren't used in pattern: ").append(set6).toString());
        }
        return fk5;
    }
    
    @VisibleForTesting
    static String[] shrink(final String... arr) {
        int integer2 = Integer.MAX_VALUE;
        int integer3 = 0;
        int integer4 = 0;
        int integer5 = 0;
        for (int integer6 = 0; integer6 < arr.length; ++integer6) {
            final String string7 = arr[integer6];
            integer2 = Math.min(integer2, firstNonSpace(string7));
            final int integer7 = lastNonSpace(string7);
            integer3 = Math.max(integer3, integer7);
            if (integer7 < 0) {
                if (integer4 == integer6) {
                    ++integer4;
                }
                ++integer5;
            }
            else {
                integer5 = 0;
            }
        }
        if (arr.length == integer5) {
            return new String[0];
        }
        final String[] arr2 = new String[arr.length - integer5 - integer4];
        for (int integer8 = 0; integer8 < arr2.length; ++integer8) {
            arr2[integer8] = arr[integer8 + integer4].substring(integer2, integer3 + 1);
        }
        return arr2;
    }
    
    private static int firstNonSpace(final String string) {
        int integer2;
        for (integer2 = 0; integer2 < string.length() && string.charAt(integer2) == ' '; ++integer2) {}
        return integer2;
    }
    
    private static int lastNonSpace(final String string) {
        int integer2;
        for (integer2 = string.length() - 1; integer2 >= 0 && string.charAt(integer2) == ' '; --integer2) {}
        return integer2;
    }
    
    private static String[] patternFromJson(final JsonArray jsonArray) {
        final String[] arr2 = new String[jsonArray.size()];
        if (arr2.length > 3) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
        }
        if (arr2.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        }
        for (int integer3 = 0; integer3 < arr2.length; ++integer3) {
            final String string4 = GsonHelper.convertToString(jsonArray.get(integer3), new StringBuilder().append("pattern[").append(integer3).append("]").toString());
            if (string4.length() > 3) {
                throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
            }
            if (integer3 > 0 && arr2[0].length() != string4.length()) {
                throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
            }
            arr2[integer3] = string4;
        }
        return arr2;
    }
    
    private static Map<String, Ingredient> keyFromJson(final JsonObject jsonObject) {
        final Map<String, Ingredient> map2 = (Map<String, Ingredient>)Maps.newHashMap();
        for (final Map.Entry<String, JsonElement> entry4 : jsonObject.entrySet()) {
            if (((String)entry4.getKey()).length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + (String)entry4.getKey() + "' is an invalid symbol (must be 1 character only).");
            }
            if (" ".equals(entry4.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }
            map2.put(entry4.getKey(), Ingredient.fromJson((JsonElement)entry4.getValue()));
        }
        map2.put(" ", Ingredient.EMPTY);
        return map2;
    }
    
    public static ItemStack itemFromJson(final JsonObject jsonObject) {
        final String string2 = GsonHelper.getAsString(jsonObject, "item");
        final Item bce3 = (Item)Registry.ITEM.getOptional(new ResourceLocation(string2)).orElseThrow(() -> new JsonSyntaxException("Unknown item '" + string2 + "'"));
        if (jsonObject.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        }
        final int integer4 = GsonHelper.getAsInt(jsonObject, "count", 1);
        return new ItemStack(bce3, integer4);
    }
    
    public static class Serializer implements RecipeSerializer<ShapedRecipe> {
        public ShapedRecipe fromJson(final ResourceLocation qv, final JsonObject jsonObject) {
            final String string4 = GsonHelper.getAsString(jsonObject, "group", "");
            final Map<String, Ingredient> map5 = keyFromJson(GsonHelper.getAsJsonObject(jsonObject, "key"));
            final String[] arr6 = ShapedRecipe.shrink(patternFromJson(GsonHelper.getAsJsonArray(jsonObject, "pattern")));
            final int integer7 = arr6[0].length();
            final int integer8 = arr6.length;
            final NonNullList<Ingredient> fk9 = dissolvePattern(arr6, map5, integer7, integer8);
            final ItemStack bcj10 = ShapedRecipe.itemFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
            return new ShapedRecipe(qv, string4, integer7, integer8, fk9, bcj10);
        }
        
        public ShapedRecipe fromNetwork(final ResourceLocation qv, final FriendlyByteBuf je) {
            final int integer4 = je.readVarInt();
            final int integer5 = je.readVarInt();
            final String string6 = je.readUtf(32767);
            final NonNullList<Ingredient> fk7 = NonNullList.<Ingredient>withSize(integer4 * integer5, Ingredient.EMPTY);
            for (int integer6 = 0; integer6 < fk7.size(); ++integer6) {
                fk7.set(integer6, Ingredient.fromNetwork(je));
            }
            final ItemStack bcj8 = je.readItem();
            return new ShapedRecipe(qv, string6, integer4, integer5, fk7, bcj8);
        }
        
        public void toNetwork(final FriendlyByteBuf je, final ShapedRecipe bew) {
            je.writeVarInt(bew.width);
            je.writeVarInt(bew.height);
            je.writeUtf(bew.group);
            for (final Ingredient beo5 : bew.recipeItems) {
                beo5.toNetwork(je);
            }
            je.writeItem(bew.result);
        }
    }
}
