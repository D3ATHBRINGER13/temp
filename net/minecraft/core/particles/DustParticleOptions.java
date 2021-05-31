package net.minecraft.core.particles;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.StringReader;
import net.minecraft.core.Registry;
import java.util.Locale;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;

public class DustParticleOptions implements ParticleOptions {
    public static final DustParticleOptions REDSTONE;
    public static final Deserializer<DustParticleOptions> DESERIALIZER;
    private final float r;
    private final float g;
    private final float b;
    private final float scale;
    
    public DustParticleOptions(final float float1, final float float2, final float float3, final float float4) {
        this.r = float1;
        this.g = float2;
        this.b = float3;
        this.scale = Mth.clamp(float4, 0.01f, 4.0f);
    }
    
    public void writeToNetwork(final FriendlyByteBuf je) {
        je.writeFloat(this.r);
        je.writeFloat(this.g);
        je.writeFloat(this.b);
        je.writeFloat(this.scale);
    }
    
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", new Object[] { Registry.PARTICLE_TYPE.getKey(this.getType()), this.r, this.g, this.b, this.scale });
    }
    
    public ParticleType<DustParticleOptions> getType() {
        return ParticleTypes.DUST;
    }
    
    public float getR() {
        return this.r;
    }
    
    public float getG() {
        return this.g;
    }
    
    public float getB() {
        return this.b;
    }
    
    public float getScale() {
        return this.scale;
    }
    
    static {
        REDSTONE = new DustParticleOptions(1.0f, 0.0f, 0.0f, 1.0f);
        DESERIALIZER = new Deserializer<DustParticleOptions>() {
            public DustParticleOptions fromCommand(final ParticleType<DustParticleOptions> gg, final StringReader stringReader) throws CommandSyntaxException {
                stringReader.expect(' ');
                final float float4 = (float)stringReader.readDouble();
                stringReader.expect(' ');
                final float float5 = (float)stringReader.readDouble();
                stringReader.expect(' ');
                final float float6 = (float)stringReader.readDouble();
                stringReader.expect(' ');
                final float float7 = (float)stringReader.readDouble();
                return new DustParticleOptions(float4, float5, float6, float7);
            }
            
            public DustParticleOptions fromNetwork(final ParticleType<DustParticleOptions> gg, final FriendlyByteBuf je) {
                return new DustParticleOptions(je.readFloat(), je.readFloat(), je.readFloat(), je.readFloat());
            }
        };
    }
}
