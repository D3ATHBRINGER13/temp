package net.minecraft.world.level.biome;

import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import com.google.common.collect.Sets;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Set;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import java.util.Random;
import java.util.List;

public class CheckerboardBiomeSource extends BiomeSource {
    private final Biome[] allowedBiomes;
    private final int bitShift;
    
    public CheckerboardBiomeSource(final CheckerboardBiomeSourceSettings bix) {
        this.allowedBiomes = bix.getAllowedBiomes();
        this.bitShift = bix.getSize() + 4;
    }
    
    @Override
    public Biome getBiome(final int integer1, final int integer2) {
        return this.allowedBiomes[Math.abs(((integer1 >> this.bitShift) + (integer2 >> this.bitShift)) % this.allowedBiomes.length)];
    }
    
    @Override
    public Biome[] getBiomeBlock(final int integer1, final int integer2, final int integer3, final int integer4, final boolean boolean5) {
        final Biome[] arr7 = new Biome[integer3 * integer4];
        for (int integer5 = 0; integer5 < integer4; ++integer5) {
            for (int integer6 = 0; integer6 < integer3; ++integer6) {
                final int integer7 = Math.abs(((integer1 + integer5 >> this.bitShift) + (integer2 + integer6 >> this.bitShift)) % this.allowedBiomes.length);
                final Biome bio11 = this.allowedBiomes[integer7];
                arr7[integer5 * integer3 + integer6] = bio11;
            }
        }
        return arr7;
    }
    
    @Nullable
    @Override
    public BlockPos findBiome(final int integer1, final int integer2, final int integer3, final List<Biome> list, final Random random) {
        return null;
    }
    
    @Override
    public boolean canGenerateStructure(final StructureFeature<?> ceu) {
        return (boolean)this.supportedStructures.computeIfAbsent(ceu, ceu -> {
            for (final Biome bio6 : this.allowedBiomes) {
                if (bio6.<FeatureConfiguration>isValidStart((StructureFeature<FeatureConfiguration>)ceu)) {
                    return true;
                }
            }
            return false;
        });
    }
    
    @Override
    public Set<BlockState> getSurfaceBlocks() {
        if (this.surfaceBlocks.isEmpty()) {
            for (final Biome bio5 : this.allowedBiomes) {
                this.surfaceBlocks.add(bio5.getSurfaceBuilderConfig().getTopMaterial());
            }
        }
        return this.surfaceBlocks;
    }
    
    @Override
    public Set<Biome> getBiomesWithin(final int integer1, final int integer2, final int integer3) {
        return (Set<Biome>)Sets.newHashSet((Object[])this.allowedBiomes);
    }
}
