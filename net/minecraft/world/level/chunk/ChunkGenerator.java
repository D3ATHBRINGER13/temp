package net.minecraft.world.level.chunk;

import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.network.protocol.game.DebugPackets;
import java.util.Map;
import java.util.Iterator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.ReportedException;
import net.minecraft.core.Registry;
import net.minecraft.CrashReport;
import javax.annotation.Nullable;
import java.util.Locale;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.Level;
import java.util.ListIterator;
import java.util.List;
import java.util.BitSet;
import java.util.Random;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public abstract class ChunkGenerator<C extends ChunkGeneratorSettings> {
    protected final LevelAccessor level;
    protected final long seed;
    protected final BiomeSource biomeSource;
    protected final C settings;
    
    public ChunkGenerator(final LevelAccessor bhs, final BiomeSource biq, final C byv) {
        this.level = bhs;
        this.seed = bhs.getSeed();
        this.biomeSource = biq;
        this.settings = byv;
    }
    
    public void createBiomes(final ChunkAccess bxh) {
        final ChunkPos bhd3 = bxh.getPos();
        final int integer4 = bhd3.x;
        final int integer5 = bhd3.z;
        final Biome[] arr6 = this.biomeSource.getBiomeBlock(integer4 * 16, integer5 * 16, 16, 16);
        bxh.setBiomes(arr6);
    }
    
    protected Biome getCarvingBiome(final ChunkAccess bxh) {
        return bxh.getBiome(BlockPos.ZERO);
    }
    
    protected Biome getDecorationBiome(final WorldGenRegion vq, final BlockPos ew) {
        return this.biomeSource.getBiome(ew);
    }
    
    public void applyCarvers(final ChunkAccess bxh, final GenerationStep.Carving a) {
        final WorldgenRandom bzk4 = new WorldgenRandom();
        final int integer5 = 8;
        final ChunkPos bhd6 = bxh.getPos();
        final int integer6 = bhd6.x;
        final int integer7 = bhd6.z;
        final BitSet bitSet9 = bxh.getCarvingMask(a);
        for (int integer8 = integer6 - 8; integer8 <= integer6 + 8; ++integer8) {
            for (int integer9 = integer7 - 8; integer9 <= integer7 + 8; ++integer9) {
                final List<ConfiguredWorldCarver<?>> list12 = this.getCarvingBiome(bxh).getCarvers(a);
                final ListIterator<ConfiguredWorldCarver<?>> listIterator13 = (ListIterator<ConfiguredWorldCarver<?>>)list12.listIterator();
                while (listIterator13.hasNext()) {
                    final int integer10 = listIterator13.nextIndex();
                    final ConfiguredWorldCarver<?> bzo15 = listIterator13.next();
                    bzk4.setLargeFeatureSeed(this.seed + integer10, integer8, integer9);
                    if (bzo15.isStartChunk(bzk4, integer8, integer9)) {
                        bzo15.carve(bxh, bzk4, this.getSeaLevel(), integer8, integer9, integer6, integer7, bitSet9);
                    }
                }
            }
        }
    }
    
    @Nullable
    public BlockPos findNearestMapFeature(final Level bhr, final String string, final BlockPos ew, final int integer, final boolean boolean5) {
        final StructureFeature<?> ceu7 = Feature.STRUCTURES_REGISTRY.get(string.toLowerCase(Locale.ROOT));
        if (ceu7 != null) {
            return ceu7.getNearestGeneratedFeature(bhr, this, ew, integer, boolean5);
        }
        return null;
    }
    
    public void applyBiomeDecoration(final WorldGenRegion vq) {
        final int integer3 = vq.getCenterX();
        final int integer4 = vq.getCenterZ();
        final int integer5 = integer3 * 16;
        final int integer6 = integer4 * 16;
        final BlockPos ew7 = new BlockPos(integer5, 0, integer6);
        final Biome bio8 = this.getDecorationBiome(vq, ew7.offset(8, 8, 8));
        final WorldgenRandom bzk9 = new WorldgenRandom();
        final long long10 = bzk9.setDecorationSeed(vq.getSeed(), integer5, integer6);
        for (final GenerationStep.Decoration b15 : GenerationStep.Decoration.values()) {
            try {
                bio8.generate(b15, this, vq, long10, bzk9, ew7);
            }
            catch (Exception exception16) {
                final CrashReport d17 = CrashReport.forThrowable((Throwable)exception16, "Biome decoration");
                d17.addCategory("Generation").setDetail("CenterX", integer3).setDetail("CenterZ", integer4).setDetail("Step", b15).setDetail("Seed", long10).setDetail("Biome", Registry.BIOME.getKey(bio8));
                throw new ReportedException(d17);
            }
        }
    }
    
    public abstract void buildSurfaceAndBedrock(final ChunkAccess bxh);
    
    public void spawnOriginalMobs(final WorldGenRegion vq) {
    }
    
    public C getSettings() {
        return this.settings;
    }
    
    public abstract int getSpawnHeight();
    
    public void tickCustomSpawners(final ServerLevel vk, final boolean boolean2, final boolean boolean3) {
    }
    
    public boolean isBiomeValidStartForStructure(final Biome bio, final StructureFeature<? extends FeatureConfiguration> ceu) {
        return bio.isValidStart(ceu);
    }
    
    @Nullable
    public <C extends FeatureConfiguration> C getStructureConfiguration(final Biome bio, final StructureFeature<C> ceu) {
        return bio.<C>getStructureConfiguration(ceu);
    }
    
    public BiomeSource getBiomeSource() {
        return this.biomeSource;
    }
    
    public long getSeed() {
        return this.seed;
    }
    
    public int getGenDepth() {
        return 256;
    }
    
    public List<Biome.SpawnerData> getMobsAt(final MobCategory aiz, final BlockPos ew) {
        return this.level.getBiome(ew).getMobs(aiz);
    }
    
    public void createStructures(final ChunkAccess bxh, final ChunkGenerator<?> bxi, final StructureManager cjp) {
        for (final StructureFeature<?> ceu6 : Feature.STRUCTURES_REGISTRY.values()) {
            if (!bxi.getBiomeSource().canGenerateStructure(ceu6)) {
                continue;
            }
            final WorldgenRandom bzk7 = new WorldgenRandom();
            final ChunkPos bhd8 = bxh.getPos();
            StructureStart ciw9 = StructureStart.INVALID_START;
            if (ceu6.isFeatureChunk(bxi, bzk7, bhd8.x, bhd8.z)) {
                final Biome bio10 = this.getBiomeSource().getBiome(new BlockPos(bhd8.getMinBlockX() + 9, 0, bhd8.getMinBlockZ() + 9));
                final StructureStart ciw10 = ceu6.getStartFactory().create(ceu6, bhd8.x, bhd8.z, bio10, BoundingBox.getUnknownBox(), 0, bxi.getSeed());
                ciw10.generatePieces(this, cjp, bhd8.x, bhd8.z, bio10);
                ciw9 = (ciw10.isValid() ? ciw10 : StructureStart.INVALID_START);
            }
            bxh.setStartForFeature(ceu6.getFeatureName(), ciw9);
        }
    }
    
    public void createReferences(final LevelAccessor bhs, final ChunkAccess bxh) {
        final int integer4 = 8;
        final int integer5 = bxh.getPos().x;
        final int integer6 = bxh.getPos().z;
        final int integer7 = integer5 << 4;
        final int integer8 = integer6 << 4;
        for (int integer9 = integer5 - 8; integer9 <= integer5 + 8; ++integer9) {
            for (int integer10 = integer6 - 8; integer10 <= integer6 + 8; ++integer10) {
                final long long11 = ChunkPos.asLong(integer9, integer10);
                for (final Map.Entry<String, StructureStart> entry14 : bhs.getChunk(integer9, integer10).getAllStarts().entrySet()) {
                    final StructureStart ciw15 = (StructureStart)entry14.getValue();
                    if (ciw15 != StructureStart.INVALID_START && ciw15.getBoundingBox().intersects(integer7, integer8, integer7 + 15, integer8 + 15)) {
                        bxh.addReferenceForFeature((String)entry14.getKey(), long11);
                        DebugPackets.sendStructurePacket(bhs, ciw15);
                    }
                }
            }
        }
    }
    
    public abstract void fillFromNoise(final LevelAccessor bhs, final ChunkAccess bxh);
    
    public int getSeaLevel() {
        return 63;
    }
    
    public abstract int getBaseHeight(final int integer1, final int integer2, final Heightmap.Types a);
    
    public int getFirstFreeHeight(final int integer1, final int integer2, final Heightmap.Types a) {
        return this.getBaseHeight(integer1, integer2, a);
    }
    
    public int getFirstOccupiedHeight(final int integer1, final int integer2, final Heightmap.Types a) {
        return this.getBaseHeight(integer1, integer2, a) - 1;
    }
}
