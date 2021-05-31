package net.minecraft.commands.synchronization;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import java.util.function.Supplier;
import com.mojang.brigadier.arguments.ArgumentType;

public class EmptyArgumentSerializer<T extends ArgumentType<?>> implements ArgumentSerializer<T> {
    private final Supplier<T> constructor;
    
    public EmptyArgumentSerializer(final Supplier<T> supplier) {
        this.constructor = supplier;
    }
    
    public void serializeToNetwork(final T argumentType, final FriendlyByteBuf je) {
    }
    
    public T deserializeFromNetwork(final FriendlyByteBuf je) {
        return (T)this.constructor.get();
    }
    
    public void serializeToJson(final T argumentType, final JsonObject jsonObject) {
    }
}
