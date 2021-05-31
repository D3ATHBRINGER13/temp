package net.minecraft.world.level.biome;

import com.google.common.collect.Sets;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Set;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import java.util.Random;
import java.util.List;
import java.util.Arrays;

public class FixedBiomeSource extends BiomeSource {
    private final Biome biome;
    
    public FixedBiomeSource(final FixedBiomeSourceSettings bjo) {
        this.biome = bjo.getBiome();
    }
    
    @Override
    public Biome getBiome(final int integer1, final int integer2) {
        return this.biome;
    }
    
    @Override
    public Biome[] getBiomeBlock(final int integer1, final int integer2, final int integer3, final int integer4, final boolean boolean5) {
        final Biome[] arr7 = new Biome[integer3 * integer4];
        Arrays.fill((Object[])arr7, 0, integer3 * integer4, this.biome);
        return arr7;
    }
    
    @Nullable
    @Override
    public BlockPos findBiome(final int integer1, final int integer2, final int integer3, final List<Biome> list, final Random random) {
        if (list.contains(this.biome)) {
            return new BlockPos(integer1 - integer3 + random.nextInt(integer3 * 2 + 1), 0, integer2 - integer3 + random.nextInt(integer3 * 2 + 1));
        }
        return null;
    }
    
    @Override
    public boolean canGenerateStructure(final StructureFeature<?> ceu) {
        return (boolean)this.supportedStructures.computeIfAbsent(ceu, this.biome::isValidStart);
    }
    
    @Override
    public Set<BlockState> getSurfaceBlocks() {
        if (this.surfaceBlocks.isEmpty()) {
            this.surfaceBlocks.add(this.biome.getSurfaceBuilderConfig().getTopMaterial());
        }
        return this.surfaceBlocks;
    }
    
    @Override
    public Set<Biome> getBiomesWithin(final int integer1, final int integer2, final int integer3) {
        return (Set<Biome>)Sets.newHashSet((Object[])new Biome[] { this.biome });
    }
}
