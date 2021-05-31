package net.minecraft.core.particles;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.StringReader;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;

public class SimpleParticleType extends ParticleType<SimpleParticleType> implements ParticleOptions {
    private static final Deserializer<SimpleParticleType> DESERIALIZER;
    
    protected SimpleParticleType(final boolean boolean1) {
        super(boolean1, SimpleParticleType.DESERIALIZER);
    }
    
    @Override
    public ParticleType<SimpleParticleType> getType() {
        return this;
    }
    
    @Override
    public void writeToNetwork(final FriendlyByteBuf je) {
    }
    
    @Override
    public String writeToString() {
        return Registry.PARTICLE_TYPE.getKey(this).toString();
    }
    
    static {
        DESERIALIZER = new Deserializer<SimpleParticleType>() {
            public SimpleParticleType fromCommand(final ParticleType<SimpleParticleType> gg, final StringReader stringReader) throws CommandSyntaxException {
                return (SimpleParticleType)gg;
            }
            
            public SimpleParticleType fromNetwork(final ParticleType<SimpleParticleType> gg, final FriendlyByteBuf je) {
                return (SimpleParticleType)gg;
            }
        };
    }
}
