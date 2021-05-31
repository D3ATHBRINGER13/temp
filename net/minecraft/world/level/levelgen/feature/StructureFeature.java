package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.biome.Biome;
import org.apache.logging.log4j.LogManager;
import net.minecraft.world.level.chunk.FeatureAccess;
import net.minecraft.world.level.chunk.ChunkAccess;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.Level;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.core.Vec3i;
import it.unimi.dsi.fastutil.longs.LongIterator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import org.apache.logging.log4j.Logger;

public abstract class StructureFeature<C extends FeatureConfiguration> extends Feature<C> {
    private static final Logger LOGGER;
    
    public StructureFeature(final Function<Dynamic<?>, ? extends C> function) {
        super(function, false);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final C cbo) {
        if (!bhs.getLevelData().isGenerateMapFeatures()) {
            return false;
        }
        final int integer7 = ew.getX() >> 4;
        final int integer8 = ew.getZ() >> 4;
        final int integer9 = integer7 << 4;
        final int integer10 = integer8 << 4;
        boolean boolean11 = false;
        for (final Long long13 : bhs.getChunk(integer7, integer8).getReferencesForFeature(this.getFeatureName())) {
            final ChunkPos bhd14 = new ChunkPos(long13);
            final StructureStart ciw15 = bhs.getChunk(bhd14.x, bhd14.z).getStartForFeature(this.getFeatureName());
            if (ciw15 != null && ciw15 != StructureStart.INVALID_START) {
                ciw15.postProcess(bhs, random, new BoundingBox(integer9, integer10, integer9 + 15, integer10 + 15), new ChunkPos(integer7, integer8));
                boolean11 = true;
            }
        }
        return boolean11;
    }
    
    protected StructureStart getStructureAt(final LevelAccessor bhs, final BlockPos ew, final boolean boolean3) {
        final List<StructureStart> list5 = this.dereferenceStructureStarts(bhs, ew.getX() >> 4, ew.getZ() >> 4);
        for (final StructureStart ciw7 : list5) {
            if (ciw7.isValid() && ciw7.getBoundingBox().isInside(ew)) {
                if (!boolean3) {
                    return ciw7;
                }
                for (final StructurePiece civ9 : ciw7.getPieces()) {
                    if (civ9.getBoundingBox().isInside(ew)) {
                        return ciw7;
                    }
                }
            }
        }
        return StructureStart.INVALID_START;
    }
    
    public boolean isInsideBoundingFeature(final LevelAccessor bhs, final BlockPos ew) {
        return this.getStructureAt(bhs, ew, false).isValid();
    }
    
    public boolean isInsideFeature(final LevelAccessor bhs, final BlockPos ew) {
        return this.getStructureAt(bhs, ew, true).isValid();
    }
    
    @Nullable
    public BlockPos getNearestGeneratedFeature(final Level bhr, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final BlockPos ew, final int integer, final boolean boolean5) {
        if (!bxi.getBiomeSource().canGenerateStructure(this)) {
            return null;
        }
        final int integer2 = ew.getX() >> 4;
        final int integer3 = ew.getZ() >> 4;
        int integer4 = 0;
        final WorldgenRandom bzk10 = new WorldgenRandom();
        while (integer4 <= integer) {
            for (int integer5 = -integer4; integer5 <= integer4; ++integer5) {
                final boolean boolean6 = integer5 == -integer4 || integer5 == integer4;
                for (int integer6 = -integer4; integer6 <= integer4; ++integer6) {
                    final boolean boolean7 = integer6 == -integer4 || integer6 == integer4;
                    if (boolean6 || boolean7) {
                        final ChunkPos bhd15 = this.getPotentialFeatureChunkFromLocationWithOffset(bxi, bzk10, integer2, integer3, integer5, integer6);
                        final StructureStart ciw16 = bhr.getChunk(bhd15.x, bhd15.z, ChunkStatus.STRUCTURE_STARTS).getStartForFeature(this.getFeatureName());
                        if (ciw16 != null && ciw16.isValid()) {
                            if (boolean5 && ciw16.canBeReferenced()) {
                                ciw16.addReference();
                                return ciw16.getLocatePos();
                            }
                            if (!boolean5) {
                                return ciw16.getLocatePos();
                            }
                        }
                        if (integer4 == 0) {
                            break;
                        }
                    }
                }
                if (integer4 == 0) {
                    break;
                }
            }
            ++integer4;
        }
        return null;
    }
    
    private List<StructureStart> dereferenceStructureStarts(final LevelAccessor bhs, final int integer2, final int integer3) {
        final List<StructureStart> list5 = (List<StructureStart>)Lists.newArrayList();
        final ChunkAccess bxh6 = bhs.getChunk(integer2, integer3, ChunkStatus.STRUCTURE_REFERENCES);
        final LongIterator longIterator7 = bxh6.getReferencesForFeature(this.getFeatureName()).iterator();
        while (longIterator7.hasNext()) {
            final long long8 = longIterator7.nextLong();
            final FeatureAccess bxp10 = bhs.getChunk(ChunkPos.getX(long8), ChunkPos.getZ(long8), ChunkStatus.STRUCTURE_STARTS);
            final StructureStart ciw11 = bxp10.getStartForFeature(this.getFeatureName());
            if (ciw11 != null) {
                list5.add(ciw11);
            }
        }
        return list5;
    }
    
    protected ChunkPos getPotentialFeatureChunkFromLocationWithOffset(final ChunkGenerator<?> bxi, final Random random, final int integer3, final int integer4, final int integer5, final int integer6) {
        return new ChunkPos(integer3 + integer5, integer4 + integer6);
    }
    
    public abstract boolean isFeatureChunk(final ChunkGenerator<?> bxi, final Random random, final int integer3, final int integer4);
    
    public abstract StructureStartFactory getStartFactory();
    
    public abstract String getFeatureName();
    
    public abstract int getLookupRange();
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public interface StructureStartFactory {
        StructureStart create(final StructureFeature<?> ceu, final int integer2, final int integer3, final Biome bio, final BoundingBox cic, final int integer6, final long long7);
    }
}
