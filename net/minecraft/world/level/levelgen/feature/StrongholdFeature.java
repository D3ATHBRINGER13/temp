package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StrongholdPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import java.util.Iterator;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.Registry;
import javax.annotation.Nullable;
import net.minecraft.core.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.Level;
import java.util.Random;
import net.minecraft.world.level.chunk.ChunkGenerator;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import java.util.List;
import net.minecraft.world.level.ChunkPos;

public class StrongholdFeature extends StructureFeature<NoneFeatureConfiguration> {
    private boolean isSpotSelected;
    private ChunkPos[] strongholdPos;
    private final List<StructureStart> discoveredStarts;
    private long currentSeed;
    
    public StrongholdFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
        this.discoveredStarts = (List<StructureStart>)Lists.newArrayList();
    }
    
    @Override
    public boolean isFeatureChunk(final ChunkGenerator<?> bxi, final Random random, final int integer3, final int integer4) {
        if (this.currentSeed != bxi.getSeed()) {
            this.reset();
        }
        if (!this.isSpotSelected) {
            this.generatePositions(bxi);
            this.isSpotSelected = true;
        }
        for (final ChunkPos bhd9 : this.strongholdPos) {
            if (integer3 == bhd9.x && integer4 == bhd9.z) {
                return true;
            }
        }
        return false;
    }
    
    private void reset() {
        this.isSpotSelected = false;
        this.strongholdPos = null;
        this.discoveredStarts.clear();
    }
    
    @Override
    public StructureStartFactory getStartFactory() {
        return StrongholdStart::new;
    }
    
    @Override
    public String getFeatureName() {
        return "Stronghold";
    }
    
    @Override
    public int getLookupRange() {
        return 8;
    }
    
    @Nullable
    @Override
    public BlockPos getNearestGeneratedFeature(final Level bhr, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final BlockPos ew, final int integer, final boolean boolean5) {
        if (!bxi.getBiomeSource().canGenerateStructure(this)) {
            return null;
        }
        if (this.currentSeed != bhr.getSeed()) {
            this.reset();
        }
        if (!this.isSpotSelected) {
            this.generatePositions(bxi);
            this.isSpotSelected = true;
        }
        BlockPos ew2 = null;
        final BlockPos.MutableBlockPos a8 = new BlockPos.MutableBlockPos();
        double double9 = Double.MAX_VALUE;
        for (final ChunkPos bhd14 : this.strongholdPos) {
            a8.set((bhd14.x << 4) + 8, 32, (bhd14.z << 4) + 8);
            final double double10 = a8.distSqr(ew);
            if (ew2 == null) {
                ew2 = new BlockPos(a8);
                double9 = double10;
            }
            else if (double10 < double9) {
                ew2 = new BlockPos(a8);
                double9 = double10;
            }
        }
        return ew2;
    }
    
    private void generatePositions(final ChunkGenerator<?> bxi) {
        this.currentSeed = bxi.getSeed();
        final List<Biome> list3 = (List<Biome>)Lists.newArrayList();
        for (final Biome bio5 : Registry.BIOME) {
            if (bio5 != null && bxi.isBiomeValidStartForStructure(bio5, Feature.STRONGHOLD)) {
                list3.add(bio5);
            }
        }
        final int integer4 = ((ChunkGeneratorSettings)bxi.getSettings()).getStrongholdsDistance();
        final int integer5 = ((ChunkGeneratorSettings)bxi.getSettings()).getStrongholdsCount();
        int integer6 = ((ChunkGeneratorSettings)bxi.getSettings()).getStrongholdsSpread();
        this.strongholdPos = new ChunkPos[integer5];
        int integer7 = 0;
        for (final StructureStart ciw9 : this.discoveredStarts) {
            if (integer7 < this.strongholdPos.length) {
                this.strongholdPos[integer7++] = new ChunkPos(ciw9.getChunkX(), ciw9.getChunkZ());
            }
        }
        final Random random8 = new Random();
        random8.setSeed(bxi.getSeed());
        double double9 = random8.nextDouble() * 3.141592653589793 * 2.0;
        final int integer8 = integer7;
        if (integer8 < this.strongholdPos.length) {
            int integer9 = 0;
            int integer10 = 0;
            for (int integer11 = 0; integer11 < this.strongholdPos.length; ++integer11) {
                final double double10 = 4 * integer4 + integer4 * integer10 * 6 + (random8.nextDouble() - 0.5) * (integer4 * 2.5);
                int integer12 = (int)Math.round(Math.cos(double9) * double10);
                int integer13 = (int)Math.round(Math.sin(double9) * double10);
                final BlockPos ew19 = bxi.getBiomeSource().findBiome((integer12 << 4) + 8, (integer13 << 4) + 8, 112, list3, random8);
                if (ew19 != null) {
                    integer12 = ew19.getX() >> 4;
                    integer13 = ew19.getZ() >> 4;
                }
                if (integer11 >= integer8) {
                    this.strongholdPos[integer11] = new ChunkPos(integer12, integer13);
                }
                double9 += 6.283185307179586 / integer6;
                if (++integer9 == integer6) {
                    ++integer10;
                    integer9 = 0;
                    integer6 += 2 * integer6 / (integer10 + 1);
                    integer6 = Math.min(integer6, this.strongholdPos.length - integer11);
                    double9 += random8.nextDouble() * 3.141592653589793 * 2.0;
                }
            }
        }
    }
    
    public static class StrongholdStart extends StructureStart {
        public StrongholdStart(final StructureFeature<?> ceu, final int integer2, final int integer3, final Biome bio, final BoundingBox cic, final int integer6, final long long7) {
            super(ceu, integer2, integer3, bio, cic, integer6, long7);
        }
        
        @Override
        public void generatePieces(final ChunkGenerator<?> bxi, final StructureManager cjp, final int integer3, final int integer4, final Biome bio) {
            int integer5 = 0;
            final long long8 = bxi.getSeed();
            StrongholdPieces.StartPiece m10;
            do {
                this.pieces.clear();
                this.boundingBox = BoundingBox.getUnknownBox();
                this.random.setLargeFeatureSeed(long8 + integer5++, integer3, integer4);
                StrongholdPieces.resetPieces();
                m10 = new StrongholdPieces.StartPiece(this.random, (integer3 << 4) + 2, (integer4 << 4) + 2);
                this.pieces.add(m10);
                m10.addChildren(m10, this.pieces, this.random);
                final List<StructurePiece> list11 = m10.pendingChildren;
                while (!list11.isEmpty()) {
                    final int integer6 = this.random.nextInt(list11.size());
                    final StructurePiece civ13 = (StructurePiece)list11.remove(integer6);
                    civ13.addChildren(m10, this.pieces, this.random);
                }
                this.calculateBoundingBox();
                this.moveBelowSeaLevel(bxi.getSeaLevel(), this.random, 10);
            } while (this.pieces.isEmpty() || m10.portalRoomPiece == null);
            ((StrongholdFeature)this.getFeature()).discoveredStarts.add(this);
        }
    }
}
