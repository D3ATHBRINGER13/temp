package net.minecraft.core.particles;

public class ParticleType<T extends ParticleOptions> {
    private final boolean overrideLimiter;
    private final ParticleOptions.Deserializer<T> deserializer;
    
    protected ParticleType(final boolean boolean1, final ParticleOptions.Deserializer<T> a) {
        this.overrideLimiter = boolean1;
        this.deserializer = a;
    }
    
    public boolean getOverrideLimiter() {
        return this.overrideLimiter;
    }
    
    public ParticleOptions.Deserializer<T> getDeserializer() {
        return this.deserializer;
    }
}
