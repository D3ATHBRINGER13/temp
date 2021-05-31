package net.minecraft.world.item.crafting;

import net.minecraft.network.FriendlyByteBuf;
import com.google.gson.JsonElement;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Registry;
import net.minecraft.world.level.ItemLike;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public class SimpleCookingSerializer<T extends AbstractCookingRecipe> implements RecipeSerializer<T> {
    private final int defaultCookingTime;
    private final CookieBaker<T> factory;
    
    public SimpleCookingSerializer(final CookieBaker<T> a, final int integer) {
        this.defaultCookingTime = integer;
        this.factory = a;
    }
    
    public T fromJson(final ResourceLocation qv, final JsonObject jsonObject) {
        final String string4 = GsonHelper.getAsString(jsonObject, "group", "");
        final JsonElement jsonElement5 = (JsonElement)(GsonHelper.isArrayNode(jsonObject, "ingredient") ? GsonHelper.getAsJsonArray(jsonObject, "ingredient") : GsonHelper.getAsJsonObject(jsonObject, "ingredient"));
        final Ingredient beo6 = Ingredient.fromJson(jsonElement5);
        final String string5 = GsonHelper.getAsString(jsonObject, "result");
        final ResourceLocation qv2 = new ResourceLocation(string5);
        final ItemStack bcj9 = new ItemStack((ItemLike)Registry.ITEM.getOptional(qv2).orElseThrow(() -> new IllegalStateException("Item: " + string5 + " does not exist")));
        final float float10 = GsonHelper.getAsFloat(jsonObject, "experience", 0.0f);
        final int integer11 = GsonHelper.getAsInt(jsonObject, "cookingtime", this.defaultCookingTime);
        return this.factory.create(qv, string4, beo6, bcj9, float10, integer11);
    }
    
    public T fromNetwork(final ResourceLocation qv, final FriendlyByteBuf je) {
        final String string4 = je.readUtf(32767);
        final Ingredient beo5 = Ingredient.fromNetwork(je);
        final ItemStack bcj6 = je.readItem();
        final float float7 = je.readFloat();
        final int integer8 = je.readVarInt();
        return this.factory.create(qv, string4, beo5, bcj6, float7, integer8);
    }
    
    public void toNetwork(final FriendlyByteBuf je, final T bed) {
        je.writeUtf(bed.group);
        bed.ingredient.toNetwork(je);
        je.writeItem(bed.result);
        je.writeFloat(bed.experience);
        je.writeVarInt(bed.cookingTime);
    }
    
    interface CookieBaker<T extends AbstractCookingRecipe> {
        T create(final ResourceLocation qv, final String string, final Ingredient beo, final ItemStack bcj, final float float5, final int integer);
    }
}
