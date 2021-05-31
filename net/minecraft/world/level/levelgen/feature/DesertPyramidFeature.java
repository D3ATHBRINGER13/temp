package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import net.minecraft.world.level.levelgen.structure.DesertPyramidPiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class DesertPyramidFeature extends RandomScatteredFeature<NoneFeatureConfiguration> {
    public DesertPyramidFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public String getFeatureName() {
        return "Desert_Pyramid";
    }
    
    @Override
    public int getLookupRange() {
        return 3;
    }
    
    @Override
    public StructureStartFactory getStartFactory() {
        return FeatureStart::new;
    }
    
    @Override
    protected int getRandomSalt() {
        return 14357617;
    }
    
    public static class FeatureStart extends StructureStart {
        public FeatureStart(final StructureFeature<?> ceu, final int integer2, final int integer3, final Biome bio, final BoundingBox cic, final int integer6, final long long7) {
            super(ceu, integer2, integer3, bio, cic, integer6, long7);
        }
        
        @Override
        public void generatePieces(final ChunkGenerator<?> bxi, final StructureManager cjp, final int integer3, final int integer4, final Biome bio) {
            final DesertPyramidPiece cie7 = new DesertPyramidPiece(this.random, integer3 * 16, integer4 * 16);
            this.pieces.add(cie7);
            this.calculateBoundingBox();
        }
    }
}
