package net.minecraft.core.particles;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.StringReader;
import net.minecraft.network.FriendlyByteBuf;

public interface ParticleOptions {
    ParticleType<?> getType();
    
    void writeToNetwork(final FriendlyByteBuf je);
    
    String writeToString();
    
    public interface Deserializer<T extends ParticleOptions> {
        T fromCommand(final ParticleType<T> gg, final StringReader stringReader) throws CommandSyntaxException;
        
        T fromNetwork(final ParticleType<T> gg, final FriendlyByteBuf je);
    }
}
