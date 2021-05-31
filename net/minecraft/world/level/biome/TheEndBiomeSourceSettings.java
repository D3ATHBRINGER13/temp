package net.minecraft.world.level.biome;

public class TheEndBiomeSourceSettings implements BiomeSourceSettings {
    private long seed;
    
    public TheEndBiomeSourceSettings setSeed(final long long1) {
        this.seed = long1;
        return this;
    }
    
    public long getSeed() {
        return this.seed;
    }
}
