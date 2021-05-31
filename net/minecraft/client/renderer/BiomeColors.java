package net.minecraft.client.renderer;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.Cursor3D;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndBiomeGetter;

public class BiomeColors {
    private static final ColorResolver GRASS_COLOR_RESOLVER;
    private static final ColorResolver FOLIAGE_COLOR_RESOLVER;
    private static final ColorResolver WATER_COLOR_RESOLVER;
    private static final ColorResolver WATER_FOG_COLOR_RESOLVER;
    
    private static int getAverageColor(final BlockAndBiomeGetter bgz, final BlockPos ew, final ColorResolver a) {
        int integer4 = 0;
        int integer5 = 0;
        int integer6 = 0;
        final int integer7 = Minecraft.getInstance().options.biomeBlendRadius;
        if (integer7 == 0) {
            return a.getColor(bgz.getBiome(ew), ew);
        }
        final int integer8 = (integer7 * 2 + 1) * (integer7 * 2 + 1);
        final Cursor3D ez9 = new Cursor3D(ew.getX() - integer7, ew.getY(), ew.getZ() - integer7, ew.getX() + integer7, ew.getY(), ew.getZ() + integer7);
        final BlockPos.MutableBlockPos a2 = new BlockPos.MutableBlockPos();
        while (ez9.advance()) {
            a2.set(ez9.nextX(), ez9.nextY(), ez9.nextZ());
            final int integer9 = a.getColor(bgz.getBiome(a2), a2);
            integer4 += (integer9 & 0xFF0000) >> 16;
            integer5 += (integer9 & 0xFF00) >> 8;
            integer6 += (integer9 & 0xFF);
        }
        return (integer4 / integer8 & 0xFF) << 16 | (integer5 / integer8 & 0xFF) << 8 | (integer6 / integer8 & 0xFF);
    }
    
    public static int getAverageGrassColor(final BlockAndBiomeGetter bgz, final BlockPos ew) {
        return getAverageColor(bgz, ew, BiomeColors.GRASS_COLOR_RESOLVER);
    }
    
    public static int getAverageFoliageColor(final BlockAndBiomeGetter bgz, final BlockPos ew) {
        return getAverageColor(bgz, ew, BiomeColors.FOLIAGE_COLOR_RESOLVER);
    }
    
    public static int getAverageWaterColor(final BlockAndBiomeGetter bgz, final BlockPos ew) {
        return getAverageColor(bgz, ew, BiomeColors.WATER_COLOR_RESOLVER);
    }
    
    static {
        GRASS_COLOR_RESOLVER = Biome::getGrassColor;
        FOLIAGE_COLOR_RESOLVER = Biome::getFoliageColor;
        WATER_COLOR_RESOLVER = ((bio, ew) -> bio.getWaterColor());
        WATER_FOG_COLOR_RESOLVER = ((bio, ew) -> bio.getWaterFogColor());
    }
    
    interface ColorResolver {
        int getColor(final Biome bio, final BlockPos ew);
    }
}
