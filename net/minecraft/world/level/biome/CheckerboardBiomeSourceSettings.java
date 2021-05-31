package net.minecraft.world.level.biome;

public class CheckerboardBiomeSourceSettings implements BiomeSourceSettings {
    private Biome[] allowedBiomes;
    private int size;
    
    public CheckerboardBiomeSourceSettings() {
        this.allowedBiomes = new Biome[] { Biomes.PLAINS };
        this.size = 1;
    }
    
    public CheckerboardBiomeSourceSettings setAllowedBiomes(final Biome[] arr) {
        this.allowedBiomes = arr;
        return this;
    }
    
    public CheckerboardBiomeSourceSettings setSize(final int integer) {
        this.size = integer;
        return this;
    }
    
    public Biome[] getAllowedBiomes() {
        return this.allowedBiomes;
    }
    
    public int getSize() {
        return this.size;
    }
}
