package net.minecraft.world.level.biome;

public class FixedBiomeSourceSettings implements BiomeSourceSettings {
    private Biome biome;
    
    public FixedBiomeSourceSettings() {
        this.biome = Biomes.PLAINS;
    }
    
    public FixedBiomeSourceSettings setBiome(final Biome bio) {
        this.biome = bio;
        return this;
    }
    
    public Biome getBiome() {
        return this.biome;
    }
}
