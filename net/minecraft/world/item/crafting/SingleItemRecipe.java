package net.minecraft.world.item.crafting;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ItemLike;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import com.google.gson.JsonElement;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.Container;

public abstract class SingleItemRecipe implements Recipe<Container> {
    protected final Ingredient ingredient;
    protected final ItemStack result;
    private final RecipeType<?> type;
    private final RecipeSerializer<?> serializer;
    protected final ResourceLocation id;
    protected final String group;
    
    public SingleItemRecipe(final RecipeType<?> beu, final RecipeSerializer<?> bet, final ResourceLocation qv, final String string, final Ingredient beo, final ItemStack bcj) {
        this.type = beu;
        this.serializer = bet;
        this.id = qv;
        this.group = string;
        this.ingredient = beo;
        this.result = bcj;
    }
    
    public RecipeType<?> getType() {
        return this.type;
    }
    
    public RecipeSerializer<?> getSerializer() {
        return this.serializer;
    }
    
    public ResourceLocation getId() {
        return this.id;
    }
    
    public String getGroup() {
        return this.group;
    }
    
    public ItemStack getResultItem() {
        return this.result;
    }
    
    public NonNullList<Ingredient> getIngredients() {
        final NonNullList<Ingredient> fk2 = NonNullList.<Ingredient>create();
        fk2.add(this.ingredient);
        return fk2;
    }
    
    public boolean canCraftInDimensions(final int integer1, final int integer2) {
        return true;
    }
    
    public ItemStack assemble(final Container ahc) {
        return this.result.copy();
    }
    
    public static class Serializer<T extends SingleItemRecipe> implements RecipeSerializer<T> {
        final SingleItemMaker<T> factory;
        
        protected Serializer(final SingleItemMaker<T> a) {
            this.factory = a;
        }
        
        public T fromJson(final ResourceLocation qv, final JsonObject jsonObject) {
            final String string4 = GsonHelper.getAsString(jsonObject, "group", "");
            Ingredient beo5;
            if (GsonHelper.isArrayNode(jsonObject, "ingredient")) {
                beo5 = Ingredient.fromJson((JsonElement)GsonHelper.getAsJsonArray(jsonObject, "ingredient"));
            }
            else {
                beo5 = Ingredient.fromJson((JsonElement)GsonHelper.getAsJsonObject(jsonObject, "ingredient"));
            }
            final String string5 = GsonHelper.getAsString(jsonObject, "result");
            final int integer7 = GsonHelper.getAsInt(jsonObject, "count");
            final ItemStack bcj8 = new ItemStack(Registry.ITEM.get(new ResourceLocation(string5)), integer7);
            return this.factory.create(qv, string4, beo5, bcj8);
        }
        
        public T fromNetwork(final ResourceLocation qv, final FriendlyByteBuf je) {
            final String string4 = je.readUtf(32767);
            final Ingredient beo5 = Ingredient.fromNetwork(je);
            final ItemStack bcj6 = je.readItem();
            return this.factory.create(qv, string4, beo5, bcj6);
        }
        
        public void toNetwork(final FriendlyByteBuf je, final T bfc) {
            je.writeUtf(bfc.group);
            bfc.ingredient.toNetwork(je);
            je.writeItem(bfc.result);
        }
        
        interface SingleItemMaker<T extends SingleItemRecipe> {
            T create(final ResourceLocation qv, final String string, final Ingredient beo, final ItemStack bcj);
        }
    }
}
