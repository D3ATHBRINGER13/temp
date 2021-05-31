package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import com.google.common.collect.Lists;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.levelgen.structure.SwamplandHutPiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.biome.Biome;
import java.util.List;

public class SwamplandHutFeature extends RandomScatteredFeature<NoneFeatureConfiguration> {
    private static final List<Biome.SpawnerData> SWAMPHUT_ENEMIES;
    private static final List<Biome.SpawnerData> SWAMPHUT_ANIMALS;
    
    public SwamplandHutFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public String getFeatureName() {
        return "Swamp_Hut";
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
        return 14357620;
    }
    
    @Override
    public List<Biome.SpawnerData> getSpecialEnemies() {
        return SwamplandHutFeature.SWAMPHUT_ENEMIES;
    }
    
    @Override
    public List<Biome.SpawnerData> getSpecialAnimals() {
        return SwamplandHutFeature.SWAMPHUT_ANIMALS;
    }
    
    public boolean isSwamphut(final LevelAccessor bhs, final BlockPos ew) {
        final StructureStart ciw4 = this.getStructureAt(bhs, ew, true);
        if (ciw4 == StructureStart.INVALID_START || !(ciw4 instanceof FeatureStart) || ciw4.getPieces().isEmpty()) {
            return false;
        }
        final StructurePiece civ5 = (StructurePiece)ciw4.getPieces().get(0);
        return civ5 instanceof SwamplandHutPiece;
    }
    
    static {
        SWAMPHUT_ENEMIES = (List)Lists.newArrayList((Object[])new Biome.SpawnerData[] { new Biome.SpawnerData(EntityType.WITCH, 1, 1, 1) });
        SWAMPHUT_ANIMALS = (List)Lists.newArrayList((Object[])new Biome.SpawnerData[] { new Biome.SpawnerData(EntityType.CAT, 1, 1, 1) });
    }
    
    public static class FeatureStart extends StructureStart {
        public FeatureStart(final StructureFeature<?> ceu, final int integer2, final int integer3, final Biome bio, final BoundingBox cic, final int integer6, final long long7) {
            super(ceu, integer2, integer3, bio, cic, integer6, long7);
        }
        
        @Override
        public void generatePieces(final ChunkGenerator<?> bxi, final StructureManager cjp, final int integer3, final int integer4, final Biome bio) {
            final SwamplandHutPiece cix7 = new SwamplandHutPiece(this.random, integer3 * 16, integer4 * 16);
            this.pieces.add(cix7);
            this.calculateBoundingBox();
        }
    }
}
