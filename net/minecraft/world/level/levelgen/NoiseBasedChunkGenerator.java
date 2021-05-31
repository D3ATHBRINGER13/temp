package net.minecraft.world.level.levelgen;

import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.chunk.LevelChunkSection;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.feature.structures.JigsawJunction;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import java.util.Random;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.synth.SurfaceNoise;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;

public abstract class NoiseBasedChunkGenerator<T extends ChunkGeneratorSettings> extends ChunkGenerator<T> {
    private static final float[] BEARD_KERNEL;
    private static final BlockState AIR;
    private final int chunkHeight;
    private final int chunkWidth;
    private final int chunkCountX;
    private final int chunkCountY;
    private final int chunkCountZ;
    protected final WorldgenRandom random;
    private final PerlinNoise minLimitPerlinNoise;
    private final PerlinNoise maxLimitPerlinNoise;
    private final PerlinNoise mainPerlinNoise;
    private final SurfaceNoise surfaceNoise;
    protected final BlockState defaultBlock;
    protected final BlockState defaultFluid;
    
    public NoiseBasedChunkGenerator(final LevelAccessor bhs, final BiomeSource biq, final int integer3, final int integer4, final int integer5, final T byv, final boolean boolean7) {
        super(bhs, biq, byv);
        this.chunkHeight = integer4;
        this.chunkWidth = integer3;
        this.defaultBlock = byv.getDefaultBlock();
        this.defaultFluid = byv.getDefaultFluid();
        this.chunkCountX = 16 / this.chunkWidth;
        this.chunkCountY = integer5 / this.chunkHeight;
        this.chunkCountZ = 16 / this.chunkWidth;
        this.random = new WorldgenRandom(this.seed);
        this.minLimitPerlinNoise = new PerlinNoise(this.random, 16);
        this.maxLimitPerlinNoise = new PerlinNoise(this.random, 16);
        this.mainPerlinNoise = new PerlinNoise(this.random, 8);
        this.surfaceNoise = (boolean7 ? new PerlinSimplexNoise(this.random, 4) : new PerlinNoise(this.random, 4));
    }
    
    private double sampleAndClampNoise(final int integer1, final int integer2, final int integer3, final double double4, final double double5, final double double6, final double double7) {
        double double8 = 0.0;
        double double9 = 0.0;
        double double10 = 0.0;
        double double11 = 1.0;
        for (int integer4 = 0; integer4 < 16; ++integer4) {
            final double double12 = PerlinNoise.wrap(integer1 * double4 * double11);
            final double double13 = PerlinNoise.wrap(integer2 * double5 * double11);
            final double double14 = PerlinNoise.wrap(integer3 * double4 * double11);
            final double double15 = double5 * double11;
            double8 += this.minLimitPerlinNoise.getOctaveNoise(integer4).noise(double12, double13, double14, double15, integer2 * double15) / double11;
            double9 += this.maxLimitPerlinNoise.getOctaveNoise(integer4).noise(double12, double13, double14, double15, integer2 * double15) / double11;
            if (integer4 < 8) {
                double10 += this.mainPerlinNoise.getOctaveNoise(integer4).noise(PerlinNoise.wrap(integer1 * double6 * double11), PerlinNoise.wrap(integer2 * double7 * double11), PerlinNoise.wrap(integer3 * double6 * double11), double7 * double11, integer2 * double7 * double11) / double11;
            }
            double11 /= 2.0;
        }
        return Mth.clampedLerp(double8 / 512.0, double9 / 512.0, (double10 / 10.0 + 1.0) / 2.0);
    }
    
    protected double[] makeAndFillNoiseColumn(final int integer1, final int integer2) {
        final double[] arr4 = new double[this.chunkCountY + 1];
        this.fillNoiseColumn(arr4, integer1, integer2);
        return arr4;
    }
    
    protected void fillNoiseColumn(final double[] arr, final int integer2, final int integer3, final double double4, final double double5, final double double6, final double double7, final int integer8, final int integer9) {
        final double[] arr2 = this.getDepthAndScale(integer2, integer3);
        final double double8 = arr2[0];
        final double double9 = arr2[1];
        final double double10 = this.getTopSlideStart();
        final double double11 = this.getBottomSlideStart();
        for (int integer10 = 0; integer10 < this.getNoiseSizeY(); ++integer10) {
            double double12 = this.sampleAndClampNoise(integer2, integer10, integer3, double4, double5, double6, double7);
            double12 -= this.getYOffset(double8, double9, integer10);
            if (integer10 > double10) {
                double12 = Mth.clampedLerp(double12, integer9, (integer10 - double10) / integer8);
            }
            else if (integer10 < double11) {
                double12 = Mth.clampedLerp(double12, -30.0, (double11 - integer10) / (double11 - 1.0));
            }
            arr[integer10] = double12;
        }
    }
    
    protected abstract double[] getDepthAndScale(final int integer1, final int integer2);
    
    protected abstract double getYOffset(final double double1, final double double2, final int integer);
    
    protected double getTopSlideStart() {
        return this.getNoiseSizeY() - 4;
    }
    
    protected double getBottomSlideStart() {
        return 0.0;
    }
    
    @Override
    public int getBaseHeight(final int integer1, final int integer2, final Heightmap.Types a) {
        final int integer3 = Math.floorDiv(integer1, this.chunkWidth);
        final int integer4 = Math.floorDiv(integer2, this.chunkWidth);
        final int integer5 = Math.floorMod(integer1, this.chunkWidth);
        final int integer6 = Math.floorMod(integer2, this.chunkWidth);
        final double double9 = integer5 / (double)this.chunkWidth;
        final double double10 = integer6 / (double)this.chunkWidth;
        final double[][] arr13 = { this.makeAndFillNoiseColumn(integer3, integer4), this.makeAndFillNoiseColumn(integer3, integer4 + 1), this.makeAndFillNoiseColumn(integer3 + 1, integer4), this.makeAndFillNoiseColumn(integer3 + 1, integer4 + 1) };
        final int integer7 = this.getSeaLevel();
        for (int integer8 = this.chunkCountY - 1; integer8 >= 0; --integer8) {
            final double double11 = arr13[0][integer8];
            final double double12 = arr13[1][integer8];
            final double double13 = arr13[2][integer8];
            final double double14 = arr13[3][integer8];
            final double double15 = arr13[0][integer8 + 1];
            final double double16 = arr13[1][integer8 + 1];
            final double double17 = arr13[2][integer8 + 1];
            final double double18 = arr13[3][integer8 + 1];
            for (int integer9 = this.chunkHeight - 1; integer9 >= 0; --integer9) {
                final double double19 = integer9 / (double)this.chunkHeight;
                final double double20 = Mth.lerp3(double19, double9, double10, double11, double15, double13, double17, double12, double16, double14, double18);
                final int integer10 = integer8 * this.chunkHeight + integer9;
                if (double20 > 0.0 || integer10 < integer7) {
                    BlockState bvt38;
                    if (double20 > 0.0) {
                        bvt38 = this.defaultBlock;
                    }
                    else {
                        bvt38 = this.defaultFluid;
                    }
                    if (a.isOpaque().test(bvt38)) {
                        return integer10 + 1;
                    }
                }
            }
        }
        return 0;
    }
    
    protected abstract void fillNoiseColumn(final double[] arr, final int integer2, final int integer3);
    
    public int getNoiseSizeY() {
        return this.chunkCountY + 1;
    }
    
    @Override
    public void buildSurfaceAndBedrock(final ChunkAccess bxh) {
        final ChunkPos bhd3 = bxh.getPos();
        final int integer4 = bhd3.x;
        final int integer5 = bhd3.z;
        final WorldgenRandom bzk6 = new WorldgenRandom();
        bzk6.setBaseChunkSeed(integer4, integer5);
        final ChunkPos bhd4 = bxh.getPos();
        final int integer6 = bhd4.getMinBlockX();
        final int integer7 = bhd4.getMinBlockZ();
        final double double10 = 0.0625;
        final Biome[] arr12 = bxh.getBiomes();
        for (int integer8 = 0; integer8 < 16; ++integer8) {
            for (int integer9 = 0; integer9 < 16; ++integer9) {
                final int integer10 = integer6 + integer8;
                final int integer11 = integer7 + integer9;
                final int integer12 = bxh.getHeight(Heightmap.Types.WORLD_SURFACE_WG, integer8, integer9) + 1;
                final double double11 = this.surfaceNoise.getSurfaceNoiseValue(integer10 * 0.0625, integer11 * 0.0625, 0.0625, integer8 * 0.0625);
                arr12[integer9 * 16 + integer8].buildSurfaceAt(bzk6, bxh, integer10, integer11, integer12, double11, this.getSettings().getDefaultBlock(), this.getSettings().getDefaultFluid(), this.getSeaLevel(), this.level.getSeed());
            }
        }
        this.setBedrock(bxh, bzk6);
    }
    
    protected void setBedrock(final ChunkAccess bxh, final Random random) {
        final BlockPos.MutableBlockPos a4 = new BlockPos.MutableBlockPos();
        final int integer5 = bxh.getPos().getMinBlockX();
        final int integer6 = bxh.getPos().getMinBlockZ();
        final T byv7 = this.getSettings();
        final int integer7 = byv7.getBedrockFloorPosition();
        final int integer8 = byv7.getBedrockRoofPosition();
        for (final BlockPos ew11 : BlockPos.betweenClosed(integer5, 0, integer6, integer5 + 15, 0, integer6 + 15)) {
            if (integer8 > 0) {
                for (int integer9 = integer8; integer9 >= integer8 - 4; --integer9) {
                    if (integer9 >= integer8 - random.nextInt(5)) {
                        bxh.setBlockState(a4.set(ew11.getX(), integer9, ew11.getZ()), Blocks.BEDROCK.defaultBlockState(), false);
                    }
                }
            }
            if (integer7 < 256) {
                for (int integer9 = integer7 + 4; integer9 >= integer7; --integer9) {
                    if (integer9 <= integer7 + random.nextInt(5)) {
                        bxh.setBlockState(a4.set(ew11.getX(), integer9, ew11.getZ()), Blocks.BEDROCK.defaultBlockState(), false);
                    }
                }
            }
        }
    }
    
    @Override
    public void fillFromNoise(final LevelAccessor bhs, final ChunkAccess bxh) {
        final int integer4 = this.getSeaLevel();
        final ObjectList<PoolElementStructurePiece> objectList5 = (ObjectList<PoolElementStructurePiece>)new ObjectArrayList(10);
        final ObjectList<JigsawJunction> objectList6 = (ObjectList<JigsawJunction>)new ObjectArrayList(32);
        final ChunkPos bhd7 = bxh.getPos();
        final int integer5 = bhd7.x;
        final int integer6 = bhd7.z;
        final int integer7 = integer5 << 4;
        final int integer8 = integer6 << 4;
        for (final StructureFeature<?> ceu13 : Feature.NOISE_AFFECTING_FEATURES) {
            final String string14 = ceu13.getFeatureName();
            final LongIterator longIterator15 = bxh.getReferencesForFeature(string14).iterator();
            while (longIterator15.hasNext()) {
                final long long16 = longIterator15.nextLong();
                final ChunkPos bhd8 = new ChunkPos(long16);
                final ChunkAccess bxh2 = bhs.getChunk(bhd8.x, bhd8.z);
                final StructureStart ciw20 = bxh2.getStartForFeature(string14);
                if (ciw20 != null) {
                    if (!ciw20.isValid()) {
                        continue;
                    }
                    for (final StructurePiece civ22 : ciw20.getPieces()) {
                        if (!civ22.isCloseToChunk(bhd7, 12)) {
                            continue;
                        }
                        if (!(civ22 instanceof PoolElementStructurePiece)) {
                            continue;
                        }
                        final PoolElementStructurePiece cip23 = (PoolElementStructurePiece)civ22;
                        final StructureTemplatePool.Projection a24 = cip23.getElement().getProjection();
                        if (a24 == StructureTemplatePool.Projection.RIGID) {
                            objectList5.add(cip23);
                        }
                        for (final JigsawJunction cfn26 : cip23.getJunctions()) {
                            final int integer9 = cfn26.getSourceX();
                            final int integer10 = cfn26.getSourceZ();
                            if (integer9 > integer7 - 12 && integer10 > integer8 - 12 && integer9 < integer7 + 15 + 12) {
                                if (integer10 >= integer8 + 15 + 12) {
                                    continue;
                                }
                                objectList6.add(cfn26);
                            }
                        }
                    }
                }
            }
        }
        final double[][][] arr12 = new double[2][this.chunkCountZ + 1][this.chunkCountY + 1];
        for (int integer11 = 0; integer11 < this.chunkCountZ + 1; ++integer11) {
            this.fillNoiseColumn(arr12[0][integer11] = new double[this.chunkCountY + 1], integer5 * this.chunkCountX, integer6 * this.chunkCountZ + integer11);
            arr12[1][integer11] = new double[this.chunkCountY + 1];
        }
        final ProtoChunk byb13 = (ProtoChunk)bxh;
        final Heightmap bza14 = byb13.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        final Heightmap bza15 = byb13.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
        final BlockPos.MutableBlockPos a25 = new BlockPos.MutableBlockPos();
        final ObjectListIterator<PoolElementStructurePiece> objectListIterator17 = (ObjectListIterator<PoolElementStructurePiece>)objectList5.iterator();
        final ObjectListIterator<JigsawJunction> objectListIterator18 = (ObjectListIterator<JigsawJunction>)objectList6.iterator();
        for (int integer12 = 0; integer12 < this.chunkCountX; ++integer12) {
            for (int integer13 = 0; integer13 < this.chunkCountZ + 1; ++integer13) {
                this.fillNoiseColumn(arr12[1][integer13], integer5 * this.chunkCountX + integer12 + 1, integer6 * this.chunkCountZ + integer13);
            }
            for (int integer13 = 0; integer13 < this.chunkCountZ; ++integer13) {
                LevelChunkSection bxu21 = byb13.getOrCreateSection(15);
                bxu21.acquire();
                for (int integer14 = this.chunkCountY - 1; integer14 >= 0; --integer14) {
                    final double double23 = arr12[0][integer13][integer14];
                    final double double24 = arr12[0][integer13 + 1][integer14];
                    final double double25 = arr12[1][integer13][integer14];
                    final double double26 = arr12[1][integer13 + 1][integer14];
                    final double double27 = arr12[0][integer13][integer14 + 1];
                    final double double28 = arr12[0][integer13 + 1][integer14 + 1];
                    final double double29 = arr12[1][integer13][integer14 + 1];
                    final double double30 = arr12[1][integer13 + 1][integer14 + 1];
                    for (int integer15 = this.chunkHeight - 1; integer15 >= 0; --integer15) {
                        final int integer16 = integer14 * this.chunkHeight + integer15;
                        final int integer17 = integer16 & 0xF;
                        final int integer18 = integer16 >> 4;
                        if (bxu21.bottomBlockY() >> 4 != integer18) {
                            bxu21.release();
                            bxu21 = byb13.getOrCreateSection(integer18);
                            bxu21.acquire();
                        }
                        final double double31 = integer15 / (double)this.chunkHeight;
                        final double double32 = Mth.lerp(double31, double23, double27);
                        final double double33 = Mth.lerp(double31, double25, double29);
                        final double double34 = Mth.lerp(double31, double24, double28);
                        final double double35 = Mth.lerp(double31, double26, double30);
                        for (int integer19 = 0; integer19 < this.chunkWidth; ++integer19) {
                            final int integer20 = integer7 + integer12 * this.chunkWidth + integer19;
                            final int integer21 = integer20 & 0xF;
                            final double double36 = integer19 / (double)this.chunkWidth;
                            final double double37 = Mth.lerp(double36, double32, double33);
                            final double double38 = Mth.lerp(double36, double34, double35);
                            for (int integer22 = 0; integer22 < this.chunkWidth; ++integer22) {
                                final int integer23 = integer8 + integer13 * this.chunkWidth + integer22;
                                final int integer24 = integer23 & 0xF;
                                final double double39 = integer22 / (double)this.chunkWidth;
                                final double double40 = Mth.lerp(double39, double37, double38);
                                double double41 = Mth.clamp(double40 / 200.0, -1.0, 1.0);
                                double41 = double41 / 2.0 - double41 * double41 * double41 / 24.0;
                                while (objectListIterator17.hasNext()) {
                                    final PoolElementStructurePiece cip24 = (PoolElementStructurePiece)objectListIterator17.next();
                                    final BoundingBox cic72 = cip24.getBoundingBox();
                                    final int integer25 = Math.max(0, Math.max(cic72.x0 - integer20, integer20 - cic72.x1));
                                    final int integer26 = integer16 - (cic72.y0 + cip24.getGroundLevelDelta());
                                    final int integer27 = Math.max(0, Math.max(cic72.z0 - integer23, integer23 - cic72.z1));
                                    double41 += getContribution(integer25, integer26, integer27) * 0.8;
                                }
                                objectListIterator17.back(objectList5.size());
                                while (objectListIterator18.hasNext()) {
                                    final JigsawJunction cfn27 = (JigsawJunction)objectListIterator18.next();
                                    final int integer28 = integer20 - cfn27.getSourceX();
                                    final int integer25 = integer16 - cfn27.getSourceGroundY();
                                    final int integer26 = integer23 - cfn27.getSourceZ();
                                    double41 += getContribution(integer28, integer25, integer26) * 0.4;
                                }
                                objectListIterator18.back(objectList6.size());
                                BlockState bvt71;
                                if (double41 > 0.0) {
                                    bvt71 = this.defaultBlock;
                                }
                                else if (integer16 < integer4) {
                                    bvt71 = this.defaultFluid;
                                }
                                else {
                                    bvt71 = NoiseBasedChunkGenerator.AIR;
                                }
                                if (bvt71 != NoiseBasedChunkGenerator.AIR) {
                                    if (bvt71.getLightEmission() != 0) {
                                        a25.set(integer20, integer16, integer23);
                                        byb13.addLight(a25);
                                    }
                                    bxu21.setBlockState(integer21, integer17, integer24, bvt71, false);
                                    bza14.update(integer21, integer16, integer24, bvt71);
                                    bza15.update(integer21, integer16, integer24, bvt71);
                                }
                            }
                        }
                    }
                }
                bxu21.release();
            }
            final double[][] arr13 = arr12[0];
            arr12[0] = arr12[1];
            arr12[1] = arr13;
        }
    }
    
    private static double getContribution(final int integer1, final int integer2, final int integer3) {
        final int integer4 = integer1 + 12;
        final int integer5 = integer2 + 12;
        final int integer6 = integer3 + 12;
        if (integer4 < 0 || integer4 >= 24) {
            return 0.0;
        }
        if (integer5 < 0 || integer5 >= 24) {
            return 0.0;
        }
        if (integer6 < 0 || integer6 >= 24) {
            return 0.0;
        }
        return NoiseBasedChunkGenerator.BEARD_KERNEL[integer6 * 24 * 24 + integer4 * 24 + integer5];
    }
    
    private static double computeContribution(final int integer1, final int integer2, final int integer3) {
        final double double4 = integer1 * integer1 + integer3 * integer3;
        final double double5 = integer2 + 0.5;
        final double double6 = double5 * double5;
        final double double7 = Math.pow(2.718281828459045, -(double6 / 16.0 + double4 / 16.0));
        final double double8 = -double5 * Mth.fastInvSqrt(double6 / 2.0 + double4 / 2.0) / 2.0;
        return double8 * double7;
    }
    
    static {
        BEARD_KERNEL = Util.<float[]>make(new float[13824], (java.util.function.Consumer<float[]>)(arr -> {
            for (int integer2 = 0; integer2 < 24; ++integer2) {
                for (int integer3 = 0; integer3 < 24; ++integer3) {
                    for (int integer4 = 0; integer4 < 24; ++integer4) {
                        arr[integer2 * 24 * 24 + integer3 * 24 + integer4] = (float)computeContribution(integer3 - 12, integer4 - 12, integer2 - 12);
                    }
                }
            }
        }));
        AIR = Blocks.AIR.defaultBlockState();
    }
}
