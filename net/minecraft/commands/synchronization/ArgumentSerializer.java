package net.minecraft.commands.synchronization;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import com.mojang.brigadier.arguments.ArgumentType;

public interface ArgumentSerializer<T extends ArgumentType<?>> {
    void serializeToNetwork(final T argumentType, final FriendlyByteBuf je);
    
    T deserializeFromNetwork(final FriendlyByteBuf je);
    
    void serializeToJson(final T argumentType, final JsonObject jsonObject);
}
