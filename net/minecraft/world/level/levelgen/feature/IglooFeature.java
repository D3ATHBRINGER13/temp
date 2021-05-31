package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import net.minecraft.world.level.levelgen.structure.IglooPieces;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class IglooFeature extends RandomScatteredFeature<NoneFeatureConfiguration> {
    public IglooFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public String getFeatureName() {
        return "Igloo";
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
        return 14357618;
    }
    
    public static class FeatureStart extends StructureStart {
        public FeatureStart(final StructureFeature<?> ceu, final int integer2, final int integer3, final Biome bio, final BoundingBox cic, final int integer6, final long long7) {
            super(ceu, integer2, integer3, bio, cic, integer6, long7);
        }
        
        @Override
        public void generatePieces(final ChunkGenerator<?> bxi, final StructureManager cjp, final int integer3, final int integer4, final Biome bio) {
            final NoneFeatureConfiguration cdd7 = bxi.<NoneFeatureConfiguration>getStructureConfiguration(bio, Feature.IGLOO);
            final int integer5 = integer3 * 16;
            final int integer6 = integer4 * 16;
            final BlockPos ew10 = new BlockPos(integer5, 90, integer6);
            final Rotation brg11 = Rotation.values()[this.random.nextInt(Rotation.values().length)];
            IglooPieces.addPieces(cjp, ew10, brg11, this.pieces, this.random, cdd7);
            this.calculateBoundingBox();
        }
    }
}
