package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import java.util.Random;
import net.minecraft.world.level.levelgen.structure.ShipwreckPieces;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.chunk.ChunkGenerator;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class ShipwreckFeature extends RandomScatteredFeature<ShipwreckConfiguration> {
    public ShipwreckFeature(final Function<Dynamic<?>, ? extends ShipwreckConfiguration> function) {
        super(function);
    }
    
    @Override
    public String getFeatureName() {
        return "Shipwreck";
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
        return 165745295;
    }
    
    @Override
    protected int getSpacing(final ChunkGenerator<?> bxi) {
        return ((ChunkGeneratorSettings)bxi.getSettings()).getShipwreckSpacing();
    }
    
    @Override
    protected int getSeparation(final ChunkGenerator<?> bxi) {
        return ((ChunkGeneratorSettings)bxi.getSettings()).getShipwreckSeparation();
    }
    
    public static class FeatureStart extends StructureStart {
        public FeatureStart(final StructureFeature<?> ceu, final int integer2, final int integer3, final Biome bio, final BoundingBox cic, final int integer6, final long long7) {
            super(ceu, integer2, integer3, bio, cic, integer6, long7);
        }
        
        @Override
        public void generatePieces(final ChunkGenerator<?> bxi, final StructureManager cjp, final int integer3, final int integer4, final Biome bio) {
            final ShipwreckConfiguration cee7 = bxi.<ShipwreckConfiguration>getStructureConfiguration(bio, Feature.SHIPWRECK);
            final Rotation brg8 = Rotation.values()[this.random.nextInt(Rotation.values().length)];
            final BlockPos ew9 = new BlockPos(integer3 * 16, 90, integer4 * 16);
            ShipwreckPieces.addPieces(cjp, ew9, brg8, this.pieces, this.random, cee7);
            this.calculateBoundingBox();
        }
    }
}
