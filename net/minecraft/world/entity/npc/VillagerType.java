package net.minecraft.world.entity.npc;

import java.util.function.Consumer;
import net.minecraft.Util;
import com.google.common.collect.Maps;
import net.minecraft.world.level.biome.Biomes;
import java.util.HashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import java.util.Map;

public interface VillagerType {
    public static final VillagerType DESERT = register("desert");
    public static final VillagerType JUNGLE = register("jungle");
    public static final VillagerType PLAINS = register("plains");
    public static final VillagerType SAVANNA = register("savanna");
    public static final VillagerType SNOW = register("snow");
    public static final VillagerType SWAMP = register("swamp");
    public static final VillagerType TAIGA = register("taiga");
    public static final Map<Biome, VillagerType> BY_BIOME = Util.<Map>make((Map)Maps.newHashMap(), (java.util.function.Consumer<Map>)(hashMap -> {
        hashMap.put(Biomes.BADLANDS, VillagerType.DESERT);
        hashMap.put(Biomes.BADLANDS_PLATEAU, VillagerType.DESERT);
        hashMap.put(Biomes.DESERT, VillagerType.DESERT);
        hashMap.put(Biomes.DESERT_HILLS, VillagerType.DESERT);
        hashMap.put(Biomes.DESERT_LAKES, VillagerType.DESERT);
        hashMap.put(Biomes.ERODED_BADLANDS, VillagerType.DESERT);
        hashMap.put(Biomes.MODIFIED_BADLANDS_PLATEAU, VillagerType.DESERT);
        hashMap.put(Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, VillagerType.DESERT);
        hashMap.put(Biomes.WOODED_BADLANDS_PLATEAU, VillagerType.DESERT);
        hashMap.put(Biomes.BAMBOO_JUNGLE, VillagerType.JUNGLE);
        hashMap.put(Biomes.BAMBOO_JUNGLE_HILLS, VillagerType.JUNGLE);
        hashMap.put(Biomes.JUNGLE, VillagerType.JUNGLE);
        hashMap.put(Biomes.JUNGLE_EDGE, VillagerType.JUNGLE);
        hashMap.put(Biomes.JUNGLE_HILLS, VillagerType.JUNGLE);
        hashMap.put(Biomes.MODIFIED_JUNGLE, VillagerType.JUNGLE);
        hashMap.put(Biomes.MODIFIED_JUNGLE_EDGE, VillagerType.JUNGLE);
        hashMap.put(Biomes.SAVANNA_PLATEAU, VillagerType.SAVANNA);
        hashMap.put(Biomes.SAVANNA, VillagerType.SAVANNA);
        hashMap.put(Biomes.SHATTERED_SAVANNA, VillagerType.SAVANNA);
        hashMap.put(Biomes.SHATTERED_SAVANNA_PLATEAU, VillagerType.SAVANNA);
        hashMap.put(Biomes.DEEP_FROZEN_OCEAN, VillagerType.SNOW);
        hashMap.put(Biomes.FROZEN_OCEAN, VillagerType.SNOW);
        hashMap.put(Biomes.FROZEN_RIVER, VillagerType.SNOW);
        hashMap.put(Biomes.ICE_SPIKES, VillagerType.SNOW);
        hashMap.put(Biomes.SNOWY_BEACH, VillagerType.SNOW);
        hashMap.put(Biomes.SNOWY_MOUNTAINS, VillagerType.SNOW);
        hashMap.put(Biomes.SNOWY_TAIGA, VillagerType.SNOW);
        hashMap.put(Biomes.SNOWY_TAIGA_HILLS, VillagerType.SNOW);
        hashMap.put(Biomes.SNOWY_TAIGA_MOUNTAINS, VillagerType.SNOW);
        hashMap.put(Biomes.SNOWY_TUNDRA, VillagerType.SNOW);
        hashMap.put(Biomes.SWAMP, VillagerType.SWAMP);
        hashMap.put(Biomes.SWAMP_HILLS, VillagerType.SWAMP);
        hashMap.put(Biomes.GIANT_SPRUCE_TAIGA, VillagerType.TAIGA);
        hashMap.put(Biomes.GIANT_SPRUCE_TAIGA_HILLS, VillagerType.TAIGA);
        hashMap.put(Biomes.GIANT_TREE_TAIGA, VillagerType.TAIGA);
        hashMap.put(Biomes.GIANT_TREE_TAIGA_HILLS, VillagerType.TAIGA);
        hashMap.put(Biomes.GRAVELLY_MOUNTAINS, VillagerType.TAIGA);
        hashMap.put(Biomes.MODIFIED_GRAVELLY_MOUNTAINS, VillagerType.TAIGA);
        hashMap.put(Biomes.MOUNTAIN_EDGE, VillagerType.TAIGA);
        hashMap.put(Biomes.MOUNTAINS, VillagerType.TAIGA);
        hashMap.put(Biomes.TAIGA, VillagerType.TAIGA);
        hashMap.put(Biomes.TAIGA_HILLS, VillagerType.TAIGA);
        hashMap.put(Biomes.TAIGA_MOUNTAINS, VillagerType.TAIGA);
        hashMap.put(Biomes.WOODED_MOUNTAINS, VillagerType.TAIGA);
    }));
    
    default VillagerType register(final String string) {
        return Registry.register(Registry.VILLAGER_TYPE, new ResourceLocation(string), new VillagerType() {
            public String toString() {
                return string;
            }
        });
    }
    
    default VillagerType byBiome(final Biome bio) {
        return (VillagerType)VillagerType.BY_BIOME.getOrDefault(bio, VillagerType.PLAINS);
    }
}
