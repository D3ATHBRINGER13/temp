package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.biome.Biomes;
import org.apache.logging.log4j.LogManager;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.Context;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.level.newbiome.layer.traits.DimensionOffset1Transformer;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer2;

public enum RegionHillsLayer implements AreaTransformer2, DimensionOffset1Transformer {
    INSTANCE;
    
    private static final Logger LOGGER;
    private static final int BIRCH_FOREST;
    private static final int BIRCH_FOREST_HILLS;
    private static final int DESERT;
    private static final int DESERT_HILLS;
    private static final int MOUNTAINS;
    private static final int WOODED_MOUNTAINS;
    private static final int FOREST;
    private static final int WOODED_HILLS;
    private static final int SNOWY_TUNDRA;
    private static final int SNOWY_MOUNTAIN;
    private static final int JUNGLE;
    private static final int JUNGLE_HILLS;
    private static final int BAMBOO_JUNGLE;
    private static final int BAMBOO_JUNGLE_HILLS;
    private static final int BADLANDS;
    private static final int WOODED_BADLANDS_PLATEAU;
    private static final int PLAINS;
    private static final int GIANT_TREE_TAIGA;
    private static final int GIANT_TREE_TAIGA_HILLS;
    private static final int DARK_FOREST;
    private static final int SAVANNA;
    private static final int SAVANNA_PLATEAU;
    private static final int TAIGA;
    private static final int SNOWY_TAIGA;
    private static final int SNOWY_TAIGA_HILLS;
    private static final int TAIGA_HILLS;
    
    public int applyPixel(final Context cly, final Area clt2, final Area clt3, final int integer4, final int integer5) {
        final int integer6 = clt2.get(this.getParentX(integer4 + 1), this.getParentY(integer5 + 1));
        final int integer7 = clt3.get(this.getParentX(integer4 + 1), this.getParentY(integer5 + 1));
        if (integer6 > 255) {
            RegionHillsLayer.LOGGER.debug("old! {}", integer6);
        }
        final int integer8 = (integer7 - 2) % 29;
        if (!Layers.isShallowOcean(integer6) && integer7 >= 2 && integer8 == 1) {
            final Biome bio10 = Registry.BIOME.byId(integer6);
            if (bio10 == null || !bio10.isMutated()) {
                final Biome bio11 = Biome.getMutatedVariant(bio10);
                return (bio11 == null) ? integer6 : Registry.BIOME.getId(bio11);
            }
        }
        if (cly.nextRandom(3) == 0 || integer8 == 0) {
            int integer9;
            if ((integer9 = integer6) == RegionHillsLayer.DESERT) {
                integer9 = RegionHillsLayer.DESERT_HILLS;
            }
            else if (integer6 == RegionHillsLayer.FOREST) {
                integer9 = RegionHillsLayer.WOODED_HILLS;
            }
            else if (integer6 == RegionHillsLayer.BIRCH_FOREST) {
                integer9 = RegionHillsLayer.BIRCH_FOREST_HILLS;
            }
            else if (integer6 == RegionHillsLayer.DARK_FOREST) {
                integer9 = RegionHillsLayer.PLAINS;
            }
            else if (integer6 == RegionHillsLayer.TAIGA) {
                integer9 = RegionHillsLayer.TAIGA_HILLS;
            }
            else if (integer6 == RegionHillsLayer.GIANT_TREE_TAIGA) {
                integer9 = RegionHillsLayer.GIANT_TREE_TAIGA_HILLS;
            }
            else if (integer6 == RegionHillsLayer.SNOWY_TAIGA) {
                integer9 = RegionHillsLayer.SNOWY_TAIGA_HILLS;
            }
            else if (integer6 == RegionHillsLayer.PLAINS) {
                integer9 = ((cly.nextRandom(3) == 0) ? RegionHillsLayer.WOODED_HILLS : RegionHillsLayer.FOREST);
            }
            else if (integer6 == RegionHillsLayer.SNOWY_TUNDRA) {
                integer9 = RegionHillsLayer.SNOWY_MOUNTAIN;
            }
            else if (integer6 == RegionHillsLayer.JUNGLE) {
                integer9 = RegionHillsLayer.JUNGLE_HILLS;
            }
            else if (integer6 == RegionHillsLayer.BAMBOO_JUNGLE) {
                integer9 = RegionHillsLayer.BAMBOO_JUNGLE_HILLS;
            }
            else if (integer6 == Layers.OCEAN) {
                integer9 = Layers.DEEP_OCEAN;
            }
            else if (integer6 == Layers.LUKEWARM_OCEAN) {
                integer9 = Layers.DEEP_LUKEWARM_OCEAN;
            }
            else if (integer6 == Layers.COLD_OCEAN) {
                integer9 = Layers.DEEP_COLD_OCEAN;
            }
            else if (integer6 == Layers.FROZEN_OCEAN) {
                integer9 = Layers.DEEP_FROZEN_OCEAN;
            }
            else if (integer6 == RegionHillsLayer.MOUNTAINS) {
                integer9 = RegionHillsLayer.WOODED_MOUNTAINS;
            }
            else if (integer6 == RegionHillsLayer.SAVANNA) {
                integer9 = RegionHillsLayer.SAVANNA_PLATEAU;
            }
            else if (Layers.isSame(integer6, RegionHillsLayer.WOODED_BADLANDS_PLATEAU)) {
                integer9 = RegionHillsLayer.BADLANDS;
            }
            else if ((integer6 == Layers.DEEP_OCEAN || integer6 == Layers.DEEP_LUKEWARM_OCEAN || integer6 == Layers.DEEP_COLD_OCEAN || integer6 == Layers.DEEP_FROZEN_OCEAN) && cly.nextRandom(3) == 0) {
                integer9 = ((cly.nextRandom(2) == 0) ? RegionHillsLayer.PLAINS : RegionHillsLayer.FOREST);
            }
            if (integer8 == 0 && integer9 != integer6) {
                final Biome bio11 = Biome.getMutatedVariant(Registry.BIOME.byId(integer9));
                integer9 = ((bio11 == null) ? integer6 : Registry.BIOME.getId(bio11));
            }
            if (integer9 != integer6) {
                int integer10 = 0;
                if (Layers.isSame(clt2.get(this.getParentX(integer4 + 1), this.getParentY(integer5 + 0)), integer6)) {
                    ++integer10;
                }
                if (Layers.isSame(clt2.get(this.getParentX(integer4 + 2), this.getParentY(integer5 + 1)), integer6)) {
                    ++integer10;
                }
                if (Layers.isSame(clt2.get(this.getParentX(integer4 + 0), this.getParentY(integer5 + 1)), integer6)) {
                    ++integer10;
                }
                if (Layers.isSame(clt2.get(this.getParentX(integer4 + 1), this.getParentY(integer5 + 2)), integer6)) {
                    ++integer10;
                }
                if (integer10 >= 3) {
                    return integer9;
                }
            }
        }
        return integer6;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        BIRCH_FOREST = Registry.BIOME.getId(Biomes.BIRCH_FOREST);
        BIRCH_FOREST_HILLS = Registry.BIOME.getId(Biomes.BIRCH_FOREST_HILLS);
        DESERT = Registry.BIOME.getId(Biomes.DESERT);
        DESERT_HILLS = Registry.BIOME.getId(Biomes.DESERT_HILLS);
        MOUNTAINS = Registry.BIOME.getId(Biomes.MOUNTAINS);
        WOODED_MOUNTAINS = Registry.BIOME.getId(Biomes.WOODED_MOUNTAINS);
        FOREST = Registry.BIOME.getId(Biomes.FOREST);
        WOODED_HILLS = Registry.BIOME.getId(Biomes.WOODED_HILLS);
        SNOWY_TUNDRA = Registry.BIOME.getId(Biomes.SNOWY_TUNDRA);
        SNOWY_MOUNTAIN = Registry.BIOME.getId(Biomes.SNOWY_MOUNTAINS);
        JUNGLE = Registry.BIOME.getId(Biomes.JUNGLE);
        JUNGLE_HILLS = Registry.BIOME.getId(Biomes.JUNGLE_HILLS);
        BAMBOO_JUNGLE = Registry.BIOME.getId(Biomes.BAMBOO_JUNGLE);
        BAMBOO_JUNGLE_HILLS = Registry.BIOME.getId(Biomes.BAMBOO_JUNGLE_HILLS);
        BADLANDS = Registry.BIOME.getId(Biomes.BADLANDS);
        WOODED_BADLANDS_PLATEAU = Registry.BIOME.getId(Biomes.WOODED_BADLANDS_PLATEAU);
        PLAINS = Registry.BIOME.getId(Biomes.PLAINS);
        GIANT_TREE_TAIGA = Registry.BIOME.getId(Biomes.GIANT_TREE_TAIGA);
        GIANT_TREE_TAIGA_HILLS = Registry.BIOME.getId(Biomes.GIANT_TREE_TAIGA_HILLS);
        DARK_FOREST = Registry.BIOME.getId(Biomes.DARK_FOREST);
        SAVANNA = Registry.BIOME.getId(Biomes.SAVANNA);
        SAVANNA_PLATEAU = Registry.BIOME.getId(Biomes.SAVANNA_PLATEAU);
        TAIGA = Registry.BIOME.getId(Biomes.TAIGA);
        SNOWY_TAIGA = Registry.BIOME.getId(Biomes.SNOWY_TAIGA);
        SNOWY_TAIGA_HILLS = Registry.BIOME.getId(Biomes.SNOWY_TAIGA_HILLS);
        TAIGA_HILLS = Registry.BIOME.getId(Biomes.TAIGA_HILLS);
    }
}
