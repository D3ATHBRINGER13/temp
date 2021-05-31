package net.minecraft.world.level;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.BlockPos;

public interface BlockAndBiomeGetter extends BlockGetter {
    Biome getBiome(final BlockPos ew);
    
    int getBrightness(final LightLayer bia, final BlockPos ew);
    
    default boolean canSeeSky(final BlockPos ew) {
        return this.getBrightness(LightLayer.SKY, ew) >= this.getMaxLightLevel();
    }
    
    default int getLightColor(final BlockPos ew, final int integer) {
        final int integer2 = this.getBrightness(LightLayer.SKY, ew);
        int integer3 = this.getBrightness(LightLayer.BLOCK, ew);
        if (integer3 < integer) {
            integer3 = integer;
        }
        return integer2 << 20 | integer3 << 4;
    }
}
