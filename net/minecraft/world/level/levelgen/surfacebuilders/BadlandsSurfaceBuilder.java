package net.minecraft.world.level.levelgen.surfacebuilders;

import java.util.Arrays;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.block.state.BlockState;

public class BadlandsSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderBaseConfiguration> {
    private static final BlockState WHITE_TERRACOTTA;
    private static final BlockState ORANGE_TERRACOTTA;
    private static final BlockState TERRACOTTA;
    private static final BlockState YELLOW_TERRACOTTA;
    private static final BlockState BROWN_TERRACOTTA;
    private static final BlockState RED_TERRACOTTA;
    private static final BlockState LIGHT_GRAY_TERRACOTTA;
    protected BlockState[] clayBands;
    protected long seed;
    protected PerlinSimplexNoise pillarNoise;
    protected PerlinSimplexNoise pillarRoofNoise;
    protected PerlinSimplexNoise clayBandsOffsetNoise;
    
    public BadlandsSurfaceBuilder(final Function<Dynamic<?>, ? extends SurfaceBuilderBaseConfiguration> function) {
        super(function);
    }
    
    @Override
    public void apply(final Random random, final ChunkAccess bxh, final Biome bio, final int integer4, final int integer5, final int integer6, final double double7, final BlockState bvt8, final BlockState bvt9, final int integer10, final long long11, final SurfaceBuilderBaseConfiguration cki) {
        final int integer11 = integer4 & 0xF;
        final int integer12 = integer5 & 0xF;
        BlockState bvt10 = BadlandsSurfaceBuilder.WHITE_TERRACOTTA;
        BlockState bvt11 = bio.getSurfaceBuilderConfig().getUnderMaterial();
        final int integer13 = (int)(double7 / 3.0 + 3.0 + random.nextDouble() * 0.25);
        final boolean boolean21 = Math.cos(double7 / 3.0 * 3.141592653589793) > 0.0;
        int integer14 = -1;
        boolean boolean22 = false;
        int integer15 = 0;
        final BlockPos.MutableBlockPos a25 = new BlockPos.MutableBlockPos();
        for (int integer16 = integer6; integer16 >= 0; --integer16) {
            if (integer15 < 15) {
                a25.set(integer11, integer16, integer12);
                final BlockState bvt12 = bxh.getBlockState(a25);
                if (bvt12.isAir()) {
                    integer14 = -1;
                }
                else if (bvt12.getBlock() == bvt8.getBlock()) {
                    if (integer14 == -1) {
                        boolean22 = false;
                        if (integer13 <= 0) {
                            bvt10 = Blocks.AIR.defaultBlockState();
                            bvt11 = bvt8;
                        }
                        else if (integer16 >= integer10 - 4 && integer16 <= integer10 + 1) {
                            bvt10 = BadlandsSurfaceBuilder.WHITE_TERRACOTTA;
                            bvt11 = bio.getSurfaceBuilderConfig().getUnderMaterial();
                        }
                        if (integer16 < integer10 && (bvt10 == null || bvt10.isAir())) {
                            bvt10 = bvt9;
                        }
                        integer14 = integer13 + Math.max(0, integer16 - integer10);
                        if (integer16 >= integer10 - 1) {
                            if (integer16 > integer10 + 3 + integer13) {
                                BlockState bvt13;
                                if (integer16 < 64 || integer16 > 127) {
                                    bvt13 = BadlandsSurfaceBuilder.ORANGE_TERRACOTTA;
                                }
                                else if (boolean21) {
                                    bvt13 = BadlandsSurfaceBuilder.TERRACOTTA;
                                }
                                else {
                                    bvt13 = this.getBand(integer4, integer16, integer5);
                                }
                                bxh.setBlockState(a25, bvt13, false);
                            }
                            else {
                                bxh.setBlockState(a25, bio.getSurfaceBuilderConfig().getTopMaterial(), false);
                                boolean22 = true;
                            }
                        }
                        else {
                            bxh.setBlockState(a25, bvt11, false);
                            final Block bmv28 = bvt11.getBlock();
                            if (bmv28 == Blocks.WHITE_TERRACOTTA || bmv28 == Blocks.ORANGE_TERRACOTTA || bmv28 == Blocks.MAGENTA_TERRACOTTA || bmv28 == Blocks.LIGHT_BLUE_TERRACOTTA || bmv28 == Blocks.YELLOW_TERRACOTTA || bmv28 == Blocks.LIME_TERRACOTTA || bmv28 == Blocks.PINK_TERRACOTTA || bmv28 == Blocks.GRAY_TERRACOTTA || bmv28 == Blocks.LIGHT_GRAY_TERRACOTTA || bmv28 == Blocks.CYAN_TERRACOTTA || bmv28 == Blocks.PURPLE_TERRACOTTA || bmv28 == Blocks.BLUE_TERRACOTTA || bmv28 == Blocks.BROWN_TERRACOTTA || bmv28 == Blocks.GREEN_TERRACOTTA || bmv28 == Blocks.RED_TERRACOTTA || bmv28 == Blocks.BLACK_TERRACOTTA) {
                                bxh.setBlockState(a25, BadlandsSurfaceBuilder.ORANGE_TERRACOTTA, false);
                            }
                        }
                    }
                    else if (integer14 > 0) {
                        --integer14;
                        if (boolean22) {
                            bxh.setBlockState(a25, BadlandsSurfaceBuilder.ORANGE_TERRACOTTA, false);
                        }
                        else {
                            bxh.setBlockState(a25, this.getBand(integer4, integer16, integer5), false);
                        }
                    }
                    ++integer15;
                }
            }
        }
    }
    
    @Override
    public void initNoise(final long long1) {
        if (this.seed != long1 || this.clayBands == null) {
            this.generateBands(long1);
        }
        if (this.seed != long1 || this.pillarNoise == null || this.pillarRoofNoise == null) {
            final Random random4 = new WorldgenRandom(long1);
            this.pillarNoise = new PerlinSimplexNoise(random4, 4);
            this.pillarRoofNoise = new PerlinSimplexNoise(random4, 1);
        }
        this.seed = long1;
    }
    
    protected void generateBands(final long long1) {
        Arrays.fill((Object[])(this.clayBands = new BlockState[64]), BadlandsSurfaceBuilder.TERRACOTTA);
        final Random random4 = new WorldgenRandom(long1);
        this.clayBandsOffsetNoise = new PerlinSimplexNoise(random4, 1);
        for (int integer5 = 0; integer5 < 64; ++integer5) {
            integer5 += random4.nextInt(5) + 1;
            if (integer5 < 64) {
                this.clayBands[integer5] = BadlandsSurfaceBuilder.ORANGE_TERRACOTTA;
            }
        }
        for (int integer5 = random4.nextInt(4) + 2, integer6 = 0; integer6 < integer5; ++integer6) {
            for (int integer7 = random4.nextInt(3) + 1, integer8 = random4.nextInt(64), integer9 = 0; integer8 + integer9 < 64 && integer9 < integer7; ++integer9) {
                this.clayBands[integer8 + integer9] = BadlandsSurfaceBuilder.YELLOW_TERRACOTTA;
            }
        }
        for (int integer6 = random4.nextInt(4) + 2, integer7 = 0; integer7 < integer6; ++integer7) {
            for (int integer8 = random4.nextInt(3) + 2, integer9 = random4.nextInt(64), integer10 = 0; integer9 + integer10 < 64 && integer10 < integer8; ++integer10) {
                this.clayBands[integer9 + integer10] = BadlandsSurfaceBuilder.BROWN_TERRACOTTA;
            }
        }
        for (int integer7 = random4.nextInt(4) + 2, integer8 = 0; integer8 < integer7; ++integer8) {
            for (int integer9 = random4.nextInt(3) + 1, integer10 = random4.nextInt(64), integer11 = 0; integer10 + integer11 < 64 && integer11 < integer9; ++integer11) {
                this.clayBands[integer10 + integer11] = BadlandsSurfaceBuilder.RED_TERRACOTTA;
            }
        }
        int integer8 = random4.nextInt(3) + 3;
        int integer9 = 0;
        for (int integer10 = 0; integer10 < integer8; ++integer10) {
            final int integer11 = 1;
            integer9 += random4.nextInt(16) + 4;
            for (int integer12 = 0; integer9 + integer12 < 64 && integer12 < 1; ++integer12) {
                this.clayBands[integer9 + integer12] = BadlandsSurfaceBuilder.WHITE_TERRACOTTA;
                if (integer9 + integer12 > 1 && random4.nextBoolean()) {
                    this.clayBands[integer9 + integer12 - 1] = BadlandsSurfaceBuilder.LIGHT_GRAY_TERRACOTTA;
                }
                if (integer9 + integer12 < 63 && random4.nextBoolean()) {
                    this.clayBands[integer9 + integer12 + 1] = BadlandsSurfaceBuilder.LIGHT_GRAY_TERRACOTTA;
                }
            }
        }
    }
    
    protected BlockState getBand(final int integer1, final int integer2, final int integer3) {
        final int integer4 = (int)Math.round(this.clayBandsOffsetNoise.getValue(integer1 / 512.0, integer3 / 512.0) * 2.0);
        return this.clayBands[(integer2 + integer4 + 64) % 64];
    }
    
    static {
        WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.defaultBlockState();
        ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.defaultBlockState();
        TERRACOTTA = Blocks.TERRACOTTA.defaultBlockState();
        YELLOW_TERRACOTTA = Blocks.YELLOW_TERRACOTTA.defaultBlockState();
        BROWN_TERRACOTTA = Blocks.BROWN_TERRACOTTA.defaultBlockState();
        RED_TERRACOTTA = Blocks.RED_TERRACOTTA.defaultBlockState();
        LIGHT_GRAY_TERRACOTTA = Blocks.LIGHT_GRAY_TERRACOTTA.defaultBlockState();
    }
}
