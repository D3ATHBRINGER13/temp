package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelAccessor;
import java.util.List;
import java.util.Collection;
import net.minecraft.world.level.levelgen.structure.WoodlandMansionPieces;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.ChunkPos;
import java.util.Random;
import net.minecraft.world.level.chunk.ChunkGenerator;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class WoodlandMansionFeature extends StructureFeature<NoneFeatureConfiguration> {
    public WoodlandMansionFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    protected ChunkPos getPotentialFeatureChunkFromLocationWithOffset(final ChunkGenerator<?> bxi, final Random random, final int integer3, final int integer4, final int integer5, final int integer6) {
        final int integer7 = ((ChunkGeneratorSettings)bxi.getSettings()).getWoodlandMansionSpacing();
        final int integer8 = ((ChunkGeneratorSettings)bxi.getSettings()).getWoodlandMangionSeparation();
        final int integer9 = integer3 + integer7 * integer5;
        final int integer10 = integer4 + integer7 * integer6;
        final int integer11 = (integer9 < 0) ? (integer9 - integer7 + 1) : integer9;
        final int integer12 = (integer10 < 0) ? (integer10 - integer7 + 1) : integer10;
        int integer13 = integer11 / integer7;
        int integer14 = integer12 / integer7;
        ((WorldgenRandom)random).setLargeFeatureWithSalt(bxi.getSeed(), integer13, integer14, 10387319);
        integer13 *= integer7;
        integer14 *= integer7;
        integer13 += (random.nextInt(integer7 - integer8) + random.nextInt(integer7 - integer8)) / 2;
        integer14 += (random.nextInt(integer7 - integer8) + random.nextInt(integer7 - integer8)) / 2;
        return new ChunkPos(integer13, integer14);
    }
    
    @Override
    public boolean isFeatureChunk(final ChunkGenerator<?> bxi, final Random random, final int integer3, final int integer4) {
        final ChunkPos bhd6 = this.getPotentialFeatureChunkFromLocationWithOffset(bxi, random, integer3, integer4, 0, 0);
        if (integer3 == bhd6.x && integer4 == bhd6.z) {
            final Set<Biome> set7 = bxi.getBiomeSource().getBiomesWithin(integer3 * 16 + 9, integer4 * 16 + 9, 32);
            for (final Biome bio9 : set7) {
                if (!bxi.isBiomeValidStartForStructure(bio9, Feature.WOODLAND_MANSION)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public StructureStartFactory getStartFactory() {
        return WoodlandMansionStart::new;
    }
    
    @Override
    public String getFeatureName() {
        return "Mansion";
    }
    
    @Override
    public int getLookupRange() {
        return 8;
    }
    
    public static class WoodlandMansionStart extends StructureStart {
        public WoodlandMansionStart(final StructureFeature<?> ceu, final int integer2, final int integer3, final Biome bio, final BoundingBox cic, final int integer6, final long long7) {
            super(ceu, integer2, integer3, bio, cic, integer6, long7);
        }
        
        @Override
        public void generatePieces(final ChunkGenerator<?> bxi, final StructureManager cjp, final int integer3, final int integer4, final Biome bio) {
            final Rotation brg7 = Rotation.values()[this.random.nextInt(Rotation.values().length)];
            int integer5 = 5;
            int integer6 = 5;
            if (brg7 == Rotation.CLOCKWISE_90) {
                integer5 = -5;
            }
            else if (brg7 == Rotation.CLOCKWISE_180) {
                integer5 = -5;
                integer6 = -5;
            }
            else if (brg7 == Rotation.COUNTERCLOCKWISE_90) {
                integer6 = -5;
            }
            final int integer7 = (integer3 << 4) + 7;
            final int integer8 = (integer4 << 4) + 7;
            final int integer9 = bxi.getFirstOccupiedHeight(integer7, integer8, Heightmap.Types.WORLD_SURFACE_WG);
            final int integer10 = bxi.getFirstOccupiedHeight(integer7, integer8 + integer6, Heightmap.Types.WORLD_SURFACE_WG);
            final int integer11 = bxi.getFirstOccupiedHeight(integer7 + integer5, integer8, Heightmap.Types.WORLD_SURFACE_WG);
            final int integer12 = bxi.getFirstOccupiedHeight(integer7 + integer5, integer8 + integer6, Heightmap.Types.WORLD_SURFACE_WG);
            final int integer13 = Math.min(Math.min(integer9, integer10), Math.min(integer11, integer12));
            if (integer13 < 60) {
                return;
            }
            final BlockPos ew17 = new BlockPos(integer3 * 16 + 8, integer13 + 1, integer4 * 16 + 8);
            final List<WoodlandMansionPieces.WoodlandMansionPiece> list18 = (List<WoodlandMansionPieces.WoodlandMansionPiece>)Lists.newLinkedList();
            WoodlandMansionPieces.generateMansion(cjp, ew17, brg7, list18, this.random);
            this.pieces.addAll((Collection)list18);
            this.calculateBoundingBox();
        }
        
        @Override
        public void postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            super.postProcess(bhs, random, cic, bhd);
            final int integer6 = this.boundingBox.y0;
            for (int integer7 = cic.x0; integer7 <= cic.x1; ++integer7) {
                for (int integer8 = cic.z0; integer8 <= cic.z1; ++integer8) {
                    final BlockPos ew9 = new BlockPos(integer7, integer6, integer8);
                    if (!bhs.isEmptyBlock(ew9) && this.boundingBox.isInside(ew9)) {
                        boolean boolean10 = false;
                        for (final StructurePiece civ12 : this.pieces) {
                            if (civ12.getBoundingBox().isInside(ew9)) {
                                boolean10 = true;
                                break;
                            }
                        }
                        if (boolean10) {
                            for (int integer9 = integer6 - 1; integer9 > 1; --integer9) {
                                final BlockPos ew10 = new BlockPos(integer7, integer9, integer8);
                                if (!bhs.isEmptyBlock(ew10) && !bhs.getBlockState(ew10).getMaterial().isLiquid()) {
                                    break;
                                }
                                bhs.setBlock(ew10, Blocks.COBBLESTONE.defaultBlockState(), 2);
                            }
                        }
                    }
                }
            }
        }
    }
}
