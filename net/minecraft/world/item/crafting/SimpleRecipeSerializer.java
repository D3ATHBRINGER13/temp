package net.minecraft.world.item.crafting;

import net.minecraft.network.FriendlyByteBuf;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Function;

public class SimpleRecipeSerializer<T extends Recipe<?>> implements RecipeSerializer<T> {
    private final Function<ResourceLocation, T> constructor;
    
    public SimpleRecipeSerializer(final Function<ResourceLocation, T> function) {
        this.constructor = function;
    }
    
    public T fromJson(final ResourceLocation qv, final JsonObject jsonObject) {
        return (T)this.constructor.apply(qv);
    }
    
    public T fromNetwork(final ResourceLocation qv, final FriendlyByteBuf je) {
        return (T)this.constructor.apply(qv);
    }
    
    public void toNetwork(final FriendlyByteBuf je, final T ber) {
    }
}
