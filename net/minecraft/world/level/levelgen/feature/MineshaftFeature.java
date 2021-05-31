package net.minecraft.world.level.levelgen.feature;

import java.util.Iterator;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.MineShaftPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Map;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import java.util.Random;
import net.minecraft.world.level.chunk.ChunkGenerator;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class MineshaftFeature extends StructureFeature<MineshaftConfiguration> {
    public MineshaftFeature(final Function<Dynamic<?>, ? extends MineshaftConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean isFeatureChunk(final ChunkGenerator<?> bxi, final Random random, final int integer3, final int integer4) {
        ((WorldgenRandom)random).setLargeFeatureSeed(bxi.getSeed(), integer3, integer4);
        final Biome bio6 = bxi.getBiomeSource().getBiome(new BlockPos((integer3 << 4) + 9, 0, (integer4 << 4) + 9));
        if (bxi.isBiomeValidStartForStructure(bio6, Feature.MINESHAFT)) {
            final MineshaftConfiguration ccx7 = bxi.<MineshaftConfiguration>getStructureConfiguration(bio6, Feature.MINESHAFT);
            final double double8 = ccx7.probability;
            return random.nextDouble() < double8;
        }
        return false;
    }
    
    @Override
    public StructureStartFactory getStartFactory() {
        return MineShaftStart::new;
    }
    
    @Override
    public String getFeatureName() {
        return "Mineshaft";
    }
    
    @Override
    public int getLookupRange() {
        return 8;
    }
    
    public enum Type {
        NORMAL("normal"), 
        MESA("mesa");
        
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
        
        public static Type byId(final int integer) {
            if (integer < 0 || integer >= values().length) {
                return Type.NORMAL;
            }
            return values()[integer];
        }
        
        static {
            BY_NAME = (Map)Arrays.stream((Object[])values()).collect(Collectors.toMap(Type::getName, b -> b));
        }
    }
    
    public static class MineShaftStart extends StructureStart {
        public MineShaftStart(final StructureFeature<?> ceu, final int integer2, final int integer3, final Biome bio, final BoundingBox cic, final int integer6, final long long7) {
            super(ceu, integer2, integer3, bio, cic, integer6, long7);
        }
        
        @Override
        public void generatePieces(final ChunkGenerator<?> bxi, final StructureManager cjp, final int integer3, final int integer4, final Biome bio) {
            final MineshaftConfiguration ccx7 = bxi.<MineshaftConfiguration>getStructureConfiguration(bio, Feature.MINESHAFT);
            final MineShaftPieces.MineShaftRoom d8 = new MineShaftPieces.MineShaftRoom(0, this.random, (integer3 << 4) + 2, (integer4 << 4) + 2, ccx7.type);
            this.pieces.add(d8);
            d8.addChildren(d8, this.pieces, this.random);
            this.calculateBoundingBox();
            if (ccx7.type == Type.MESA) {
                final int integer5 = -5;
                final int integer6 = bxi.getSeaLevel() - this.boundingBox.y1 + this.boundingBox.getYSpan() / 2 + 5;
                this.boundingBox.move(0, integer6, 0);
                for (final StructurePiece civ12 : this.pieces) {
                    civ12.move(0, integer6, 0);
                }
            }
            else {
                this.moveBelowSeaLevel(bxi.getSeaLevel(), this.random, 10);
            }
        }
    }
}
