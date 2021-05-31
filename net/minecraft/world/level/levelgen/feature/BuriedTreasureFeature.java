package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.levelgen.structure.BuriedTreasurePieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.chunk.ChunkGenerator;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class BuriedTreasureFeature extends StructureFeature<BuriedTreasureConfiguration> {
    public BuriedTreasureFeature(final Function<Dynamic<?>, ? extends BuriedTreasureConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean isFeatureChunk(final ChunkGenerator<?> bxi, final Random random, final int integer3, final int integer4) {
        final Biome bio6 = bxi.getBiomeSource().getBiome(new BlockPos((integer3 << 4) + 9, 0, (integer4 << 4) + 9));
        if (bxi.isBiomeValidStartForStructure(bio6, Feature.BURIED_TREASURE)) {
            ((WorldgenRandom)random).setLargeFeatureWithSalt(bxi.getSeed(), integer3, integer4, 10387320);
            final BuriedTreasureConfiguration cae7 = bxi.<BuriedTreasureConfiguration>getStructureConfiguration(bio6, Feature.BURIED_TREASURE);
            return random.nextFloat() < cae7.probability;
        }
        return false;
    }
    
    @Override
    public StructureStartFactory getStartFactory() {
        return BuriedTreasureStart::new;
    }
    
    @Override
    public String getFeatureName() {
        return "Buried_Treasure";
    }
    
    @Override
    public int getLookupRange() {
        return 1;
    }
    
    public static class BuriedTreasureStart extends StructureStart {
        public BuriedTreasureStart(final StructureFeature<?> ceu, final int integer2, final int integer3, final Biome bio, final BoundingBox cic, final int integer6, final long long7) {
            super(ceu, integer2, integer3, bio, cic, integer6, long7);
        }
        
        @Override
        public void generatePieces(final ChunkGenerator<?> bxi, final StructureManager cjp, final int integer3, final int integer4, final Biome bio) {
            final int integer5 = integer3 * 16;
            final int integer6 = integer4 * 16;
            final BlockPos ew9 = new BlockPos(integer5 + 9, 90, integer6 + 9);
            this.pieces.add(new BuriedTreasurePieces.BuriedTreasurePiece(ew9));
            this.calculateBoundingBox();
        }
        
        @Override
        public BlockPos getLocatePos() {
            return new BlockPos((this.getChunkX() << 4) + 9, 0, (this.getChunkZ() << 4) + 9);
        }
    }
}
