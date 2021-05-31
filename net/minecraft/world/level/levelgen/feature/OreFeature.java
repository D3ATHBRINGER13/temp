package net.minecraft.world.level.levelgen.feature;

import java.util.BitSet;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.util.Mth;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class OreFeature extends Feature<OreConfiguration> {
    public OreFeature(final Function<Dynamic<?>, ? extends OreConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final OreConfiguration cdg) {
        final float float7 = random.nextFloat() * 3.1415927f;
        final float float8 = cdg.size / 8.0f;
        final int integer9 = Mth.ceil((cdg.size / 16.0f * 2.0f + 1.0f) / 2.0f);
        final double double10 = ew.getX() + Mth.sin(float7) * float8;
        final double double11 = ew.getX() - Mth.sin(float7) * float8;
        final double double12 = ew.getZ() + Mth.cos(float7) * float8;
        final double double13 = ew.getZ() - Mth.cos(float7) * float8;
        final int integer10 = 2;
        final double double14 = ew.getY() + random.nextInt(3) - 2;
        final double double15 = ew.getY() + random.nextInt(3) - 2;
        final int integer11 = ew.getX() - Mth.ceil(float8) - integer9;
        final int integer12 = ew.getY() - 2 - integer9;
        final int integer13 = ew.getZ() - Mth.ceil(float8) - integer9;
        final int integer14 = 2 * (Mth.ceil(float8) + integer9);
        final int integer15 = 2 * (2 + integer9);
        for (int integer16 = integer11; integer16 <= integer11 + integer14; ++integer16) {
            for (int integer17 = integer13; integer17 <= integer13 + integer14; ++integer17) {
                if (integer12 <= bhs.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, integer16, integer17)) {
                    return this.doPlace(bhs, random, cdg, double10, double11, double12, double13, double14, double15, integer11, integer12, integer13, integer14, integer15);
                }
            }
        }
        return false;
    }
    
    protected boolean doPlace(final LevelAccessor bhs, final Random random, final OreConfiguration cdg, final double double4, final double double5, final double double6, final double double7, final double double8, final double double9, final int integer10, final int integer11, final int integer12, final int integer13, final int integer14) {
        int integer15 = 0;
        final BitSet bitSet23 = new BitSet(integer13 * integer14 * integer13);
        final BlockPos.MutableBlockPos a24 = new BlockPos.MutableBlockPos();
        final double[] arr25 = new double[cdg.size * 4];
        for (int integer16 = 0; integer16 < cdg.size; ++integer16) {
            final float float27 = integer16 / (float)cdg.size;
            final double double10 = Mth.lerp(float27, double4, double5);
            final double double11 = Mth.lerp(float27, double8, double9);
            final double double12 = Mth.lerp(float27, double6, double7);
            final double double13 = random.nextDouble() * cdg.size / 16.0;
            final double double14 = ((Mth.sin(3.1415927f * float27) + 1.0f) * double13 + 1.0) / 2.0;
            arr25[integer16 * 4 + 0] = double10;
            arr25[integer16 * 4 + 1] = double11;
            arr25[integer16 * 4 + 2] = double12;
            arr25[integer16 * 4 + 3] = double14;
        }
        for (int integer16 = 0; integer16 < cdg.size - 1; ++integer16) {
            if (arr25[integer16 * 4 + 3] > 0.0) {
                for (int integer17 = integer16 + 1; integer17 < cdg.size; ++integer17) {
                    if (arr25[integer17 * 4 + 3] > 0.0) {
                        final double double10 = arr25[integer16 * 4 + 0] - arr25[integer17 * 4 + 0];
                        final double double11 = arr25[integer16 * 4 + 1] - arr25[integer17 * 4 + 1];
                        final double double12 = arr25[integer16 * 4 + 2] - arr25[integer17 * 4 + 2];
                        final double double13 = arr25[integer16 * 4 + 3] - arr25[integer17 * 4 + 3];
                        if (double13 * double13 > double10 * double10 + double11 * double11 + double12 * double12) {
                            if (double13 > 0.0) {
                                arr25[integer17 * 4 + 3] = -1.0;
                            }
                            else {
                                arr25[integer16 * 4 + 3] = -1.0;
                            }
                        }
                    }
                }
            }
        }
        for (int integer16 = 0; integer16 < cdg.size; ++integer16) {
            final double double15 = arr25[integer16 * 4 + 3];
            if (double15 >= 0.0) {
                final double double16 = arr25[integer16 * 4 + 0];
                final double double17 = arr25[integer16 * 4 + 1];
                final double double18 = arr25[integer16 * 4 + 2];
                final int integer18 = Math.max(Mth.floor(double16 - double15), integer10);
                final int integer19 = Math.max(Mth.floor(double17 - double15), integer11);
                final int integer20 = Math.max(Mth.floor(double18 - double15), integer12);
                final int integer21 = Math.max(Mth.floor(double16 + double15), integer18);
                final int integer22 = Math.max(Mth.floor(double17 + double15), integer19);
                final int integer23 = Math.max(Mth.floor(double18 + double15), integer20);
                for (int integer24 = integer18; integer24 <= integer21; ++integer24) {
                    final double double19 = (integer24 + 0.5 - double16) / double15;
                    if (double19 * double19 < 1.0) {
                        for (int integer25 = integer19; integer25 <= integer22; ++integer25) {
                            final double double20 = (integer25 + 0.5 - double17) / double15;
                            if (double19 * double19 + double20 * double20 < 1.0) {
                                for (int integer26 = integer20; integer26 <= integer23; ++integer26) {
                                    final double double21 = (integer26 + 0.5 - double18) / double15;
                                    if (double19 * double19 + double20 * double20 + double21 * double21 < 1.0) {
                                        final int integer27 = integer24 - integer10 + (integer25 - integer11) * integer13 + (integer26 - integer12) * integer13 * integer14;
                                        if (!bitSet23.get(integer27)) {
                                            bitSet23.set(integer27);
                                            a24.set(integer24, integer25, integer26);
                                            if (cdg.target.getPredicate().test(bhs.getBlockState(a24))) {
                                                bhs.setBlock(a24, cdg.state, 2);
                                                ++integer15;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return integer15 > 0;
    }
}
