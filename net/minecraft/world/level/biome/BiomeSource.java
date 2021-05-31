package net.minecraft.world.level.biome;

import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import java.util.Random;
import net.minecraft.core.BlockPos;
import com.google.common.collect.Sets;
import com.google.common.collect.Maps;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Set;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import java.util.Map;
import java.util.List;

public abstract class BiomeSource {
    private static final List<Biome> PLAYER_SPAWN_BIOMES;
    protected final Map<StructureFeature<?>, Boolean> supportedStructures;
    protected final Set<BlockState> surfaceBlocks;
    
    protected BiomeSource() {
        this.supportedStructures = (Map<StructureFeature<?>, Boolean>)Maps.newHashMap();
        this.surfaceBlocks = (Set<BlockState>)Sets.newHashSet();
    }
    
    public List<Biome> getPlayerSpawnBiomes() {
        return BiomeSource.PLAYER_SPAWN_BIOMES;
    }
    
    public Biome getBiome(final BlockPos ew) {
        return this.getBiome(ew.getX(), ew.getZ());
    }
    
    public abstract Biome getBiome(final int integer1, final int integer2);
    
    public Biome getNoiseBiome(final int integer1, final int integer2) {
        return this.getBiome(integer1 << 2, integer2 << 2);
    }
    
    public Biome[] getBiomeBlock(final int integer1, final int integer2, final int integer3, final int integer4) {
        return this.getBiomeBlock(integer1, integer2, integer3, integer4, true);
    }
    
    public abstract Biome[] getBiomeBlock(final int integer1, final int integer2, final int integer3, final int integer4, final boolean boolean5);
    
    public abstract Set<Biome> getBiomesWithin(final int integer1, final int integer2, final int integer3);
    
    @Nullable
    public abstract BlockPos findBiome(final int integer1, final int integer2, final int integer3, final List<Biome> list, final Random random);
    
    public float getHeightValue(final int integer1, final int integer2) {
        return 0.0f;
    }
    
    public abstract boolean canGenerateStructure(final StructureFeature<?> ceu);
    
    public abstract Set<BlockState> getSurfaceBlocks();
    
    static {
        PLAYER_SPAWN_BIOMES = (List)Lists.newArrayList((Object[])new Biome[] { Biomes.FOREST, Biomes.PLAINS, Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.WOODED_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_HILLS });
    }
}
