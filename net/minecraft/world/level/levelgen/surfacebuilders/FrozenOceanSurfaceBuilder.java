package net.minecraft.world.level.levelgen.surfacebuilders;

import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.block.state.BlockState;

public class FrozenOceanSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderBaseConfiguration> {
    protected static final BlockState PACKED_ICE;
    protected static final BlockState SNOW_BLOCK;
    private static final BlockState AIR;
    private static final BlockState GRAVEL;
    private static final BlockState ICE;
    private PerlinSimplexNoise icebergNoise;
    private PerlinSimplexNoise icebergRoofNoise;
    private long seed;
    
    public FrozenOceanSurfaceBuilder(final Function<Dynamic<?>, ? extends SurfaceBuilderBaseConfiguration> function) {
        super(function);
    }
    
    @Override
    public void apply(final Random random, final ChunkAccess bxh, final Biome bio, final int integer4, final int integer5, final int integer6, final double double7, final BlockState bvt8, final BlockState bvt9, final int integer10, final long long11, final SurfaceBuilderBaseConfiguration cki) {
        double double8 = 0.0;
        double double9 = 0.0;
        final BlockPos.MutableBlockPos a20 = new BlockPos.MutableBlockPos();
        final float float21 = bio.getTemperature(a20.set(integer4, 63, integer5));
        final double double10 = Math.min(Math.abs(double7), this.icebergNoise.getValue(integer4 * 0.1, integer5 * 0.1));
        if (double10 > 1.8) {
            final double double11 = 0.09765625;
            final double double12 = Math.abs(this.icebergRoofNoise.getValue(integer4 * 0.09765625, integer5 * 0.09765625));
            double8 = double10 * double10 * 1.2;
            final double double13 = Math.ceil(double12 * 40.0) + 14.0;
            if (double8 > double13) {
                double8 = double13;
            }
            if (float21 > 0.1f) {
                double8 -= 2.0;
            }
            if (double8 > 2.0) {
                double9 = integer10 - double8 - 7.0;
                double8 += integer10;
            }
            else {
                double8 = 0.0;
            }
        }
        final int integer11 = integer4 & 0xF;
        final int integer12 = integer5 & 0xF;
        BlockState bvt10 = bio.getSurfaceBuilderConfig().getUnderMaterial();
        BlockState bvt11 = bio.getSurfaceBuilderConfig().getTopMaterial();
        final int integer13 = (int)(double7 / 3.0 + 3.0 + random.nextDouble() * 0.25);
        int integer14 = -1;
        int integer15 = 0;
        final int integer16 = 2 + random.nextInt(4);
        final int integer17 = integer10 + 18 + random.nextInt(10);
        for (int integer18 = Math.max(integer6, (int)double8 + 1); integer18 >= 0; --integer18) {
            a20.set(integer11, integer18, integer12);
            if (bxh.getBlockState(a20).isAir() && integer18 < (int)double8 && random.nextDouble() > 0.01) {
                bxh.setBlockState(a20, FrozenOceanSurfaceBuilder.PACKED_ICE, false);
            }
            else if (bxh.getBlockState(a20).getMaterial() == Material.WATER && integer18 > (int)double9 && integer18 < integer10 && double9 != 0.0 && random.nextDouble() > 0.15) {
                bxh.setBlockState(a20, FrozenOceanSurfaceBuilder.PACKED_ICE, false);
            }
            final BlockState bvt12 = bxh.getBlockState(a20);
            if (bvt12.isAir()) {
                integer14 = -1;
            }
            else if (bvt12.getBlock() == bvt8.getBlock()) {
                if (integer14 == -1) {
                    if (integer13 <= 0) {
                        bvt11 = FrozenOceanSurfaceBuilder.AIR;
                        bvt10 = bvt8;
                    }
                    else if (integer18 >= integer10 - 4 && integer18 <= integer10 + 1) {
                        bvt11 = bio.getSurfaceBuilderConfig().getTopMaterial();
                        bvt10 = bio.getSurfaceBuilderConfig().getUnderMaterial();
                    }
                    if (integer18 < integer10 && (bvt11 == null || bvt11.isAir())) {
                        if (bio.getTemperature(a20.set(integer4, integer18, integer5)) < 0.15f) {
                            bvt11 = FrozenOceanSurfaceBuilder.ICE;
                        }
                        else {
                            bvt11 = bvt9;
                        }
                    }
                    integer14 = integer13;
                    if (integer18 >= integer10 - 1) {
                        bxh.setBlockState(a20, bvt11, false);
                    }
                    else if (integer18 < integer10 - 7 - integer13) {
                        bvt11 = FrozenOceanSurfaceBuilder.AIR;
                        bvt10 = bvt8;
                        bxh.setBlockState(a20, FrozenOceanSurfaceBuilder.GRAVEL, false);
                    }
                    else {
                        bxh.setBlockState(a20, bvt10, false);
                    }
                }
                else if (integer14 > 0) {
                    --integer14;
                    bxh.setBlockState(a20, bvt10, false);
                    if (integer14 == 0 && bvt10.getBlock() == Blocks.SAND && integer13 > 1) {
                        integer14 = random.nextInt(4) + Math.max(0, integer18 - 63);
                        bvt10 = ((bvt10.getBlock() == Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.defaultBlockState() : Blocks.SANDSTONE.defaultBlockState());
                    }
                }
            }
            else if (bvt12.getBlock() == Blocks.PACKED_ICE && integer15 <= integer16 && integer18 > integer17) {
                bxh.setBlockState(a20, FrozenOceanSurfaceBuilder.SNOW_BLOCK, false);
                ++integer15;
            }
        }
    }
    
    @Override
    public void initNoise(final long long1) {
        if (this.seed != long1 || this.icebergNoise == null || this.icebergRoofNoise == null) {
            final Random random4 = new WorldgenRandom(long1);
            this.icebergNoise = new PerlinSimplexNoise(random4, 4);
            this.icebergRoofNoise = new PerlinSimplexNoise(random4, 1);
        }
        this.seed = long1;
    }
    
    static {
        PACKED_ICE = Blocks.PACKED_ICE.defaultBlockState();
        SNOW_BLOCK = Blocks.SNOW_BLOCK.defaultBlockState();
        AIR = Blocks.AIR.defaultBlockState();
        GRAVEL = Blocks.GRAVEL.defaultBlockState();
        ICE = Blocks.ICE.defaultBlockState();
    }
}
