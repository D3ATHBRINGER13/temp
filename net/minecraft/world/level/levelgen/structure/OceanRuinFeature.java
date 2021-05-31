package net.minecraft.world.level.levelgen.structure;

import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.chunk.ChunkGenerator;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.feature.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.feature.RandomScatteredFeature;

public class OceanRuinFeature extends RandomScatteredFeature<OceanRuinConfiguration> {
    public OceanRuinFeature(final Function<Dynamic<?>, ? extends OceanRuinConfiguration> function) {
        super(function);
    }
    
    @Override
    public String getFeatureName() {
        return "Ocean_Ruin";
    }
    
    @Override
    public int getLookupRange() {
        return 3;
    }
    
    @Override
    protected int getSpacing(final ChunkGenerator<?> bxi) {
        return ((ChunkGeneratorSettings)bxi.getSettings()).getOceanRuinSpacing();
    }
    
    @Override
    protected int getSeparation(final ChunkGenerator<?> bxi) {
        return ((ChunkGeneratorSettings)bxi.getSettings()).getOceanRuinSeparation();
    }
    
    @Override
    public StructureStartFactory getStartFactory() {
        return OceanRuinStart::new;
    }
    
    @Override
    protected int getRandomSalt() {
        return 14357621;
    }
    
    public static class OceanRuinStart extends StructureStart {
        public OceanRuinStart(final StructureFeature<?> ceu, final int integer2, final int integer3, final Biome bio, final BoundingBox cic, final int integer6, final long long7) {
            super(ceu, integer2, integer3, bio, cic, integer6, long7);
        }
        
        @Override
        public void generatePieces(final ChunkGenerator<?> bxi, final StructureManager cjp, final int integer3, final int integer4, final Biome bio) {
            final OceanRuinConfiguration cdf7 = bxi.<OceanRuinConfiguration>getStructureConfiguration(bio, Feature.OCEAN_RUIN);
            final int integer5 = integer3 * 16;
            final int integer6 = integer4 * 16;
            final BlockPos ew10 = new BlockPos(integer5, 90, integer6);
            final Rotation brg11 = Rotation.values()[this.random.nextInt(Rotation.values().length)];
            OceanRuinPieces.addPieces(cjp, ew10, brg11, this.pieces, this.random, cdf7);
            this.calculateBoundingBox();
        }
    }
    
    public enum Type {
        WARM("warm"), 
        COLD("cold");
        
        private static final Map<String, Type> BY_NAME;
        private final String name;
        
        private Type(final String string3) {
            this.name = string3;
        }
        
        public String getName() {
            return this.name;
        }
        
        public static Type byName(final String string) {
            return (Type)Type.BY_NAME.get(string);
        }
        
        static {
            BY_NAME = (Map)Arrays.stream((Object[])values()).collect(Collectors.toMap(Type::getName, b -> b));
        }
    }
}
