package net.minecraft.world.level.levelgen.surfacebuilders;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.block.state.BlockState;

public class ErodedBadlandsSurfaceBuilder extends BadlandsSurfaceBuilder {
    private static final BlockState WHITE_TERRACOTTA;
    private static final BlockState ORANGE_TERRACOTTA;
    private static final BlockState TERRACOTTA;
    
    public ErodedBadlandsSurfaceBuilder(final Function<Dynamic<?>, ? extends SurfaceBuilderBaseConfiguration> function) {
        super(function);
    }
    
    @Override
    public void apply(final Random random, final ChunkAccess bxh, final Biome bio, final int integer4, final int integer5, final int integer6, final double double7, final BlockState bvt8, final BlockState bvt9, final int integer10, final long long11, final SurfaceBuilderBaseConfiguration cki) {
        double double8 = 0.0;
        final double double9 = Math.min(Math.abs(double7), this.pillarNoise.getValue(integer4 * 0.25, integer5 * 0.25));
        if (double9 > 0.0) {
            final double double10 = 0.001953125;
            final double double11 = Math.abs(this.pillarRoofNoise.getValue(integer4 * 0.001953125, integer5 * 0.001953125));
            double8 = double9 * double9 * 2.5;
            final double double12 = Math.ceil(double11 * 50.0) + 14.0;
            if (double8 > double12) {
                double8 = double12;
            }
            double8 += 64.0;
        }
        final int integer11 = integer4 & 0xF;
        final int integer12 = integer5 & 0xF;
        BlockState bvt10 = ErodedBadlandsSurfaceBuilder.WHITE_TERRACOTTA;
        BlockState bvt11 = bio.getSurfaceBuilderConfig().getUnderMaterial();
        final int integer13 = (int)(double7 / 3.0 + 3.0 + random.nextDouble() * 0.25);
        final boolean boolean25 = Math.cos(double7 / 3.0 * 3.141592653589793) > 0.0;
        int integer14 = -1;
        boolean boolean26 = false;
        final BlockPos.MutableBlockPos a28 = new BlockPos.MutableBlockPos();
        for (int integer15 = Math.max(integer6, (int)double8 + 1); integer15 >= 0; --integer15) {
            a28.set(integer11, integer15, integer12);
            if (bxh.getBlockState(a28).isAir() && integer15 < (int)double8) {
                bxh.setBlockState(a28, bvt8, false);
            }
            final BlockState bvt12 = bxh.getBlockState(a28);
            if (bvt12.isAir()) {
                integer14 = -1;
            }
            else if (bvt12.getBlock() == bvt8.getBlock()) {
                if (integer14 == -1) {
                    boolean26 = false;
                    if (integer13 <= 0) {
                        bvt10 = Blocks.AIR.defaultBlockState();
                        bvt11 = bvt8;
                    }
                    else if (integer15 >= integer10 - 4 && integer15 <= integer10 + 1) {
                        bvt10 = ErodedBadlandsSurfaceBuilder.WHITE_TERRACOTTA;
                        bvt11 = bio.getSurfaceBuilderConfig().getUnderMaterial();
                    }
                    if (integer15 < integer10 && (bvt10 == null || bvt10.isAir())) {
                        bvt10 = bvt9;
                    }
                    integer14 = integer13 + Math.max(0, integer15 - integer10);
                    if (integer15 >= integer10 - 1) {
                        if (integer15 > integer10 + 3 + integer13) {
                            BlockState bvt13;
                            if (integer15 < 64 || integer15 > 127) {
                                bvt13 = ErodedBadlandsSurfaceBuilder.ORANGE_TERRACOTTA;
                            }
                            else if (boolean25) {
                                bvt13 = ErodedBadlandsSurfaceBuilder.TERRACOTTA;
                            }
                            else {
                                bvt13 = this.getBand(integer4, integer15, integer5);
                            }
                            bxh.setBlockState(a28, bvt13, false);
                        }
                        else {
                            bxh.setBlockState(a28, bio.getSurfaceBuilderConfig().getTopMaterial(), false);
                            boolean26 = true;
                        }
                    }
                    else {
                        bxh.setBlockState(a28, bvt11, false);
                        final Block bmv31 = bvt11.getBlock();
                        if (bmv31 == Blocks.WHITE_TERRACOTTA || bmv31 == Blocks.ORANGE_TERRACOTTA || bmv31 == Blocks.MAGENTA_TERRACOTTA || bmv31 == Blocks.LIGHT_BLUE_TERRACOTTA || bmv31 == Blocks.YELLOW_TERRACOTTA || bmv31 == Blocks.LIME_TERRACOTTA || bmv31 == Blocks.PINK_TERRACOTTA || bmv31 == Blocks.GRAY_TERRACOTTA || bmv31 == Blocks.LIGHT_GRAY_TERRACOTTA || bmv31 == Blocks.CYAN_TERRACOTTA || bmv31 == Blocks.PURPLE_TERRACOTTA || bmv31 == Blocks.BLUE_TERRACOTTA || bmv31 == Blocks.BROWN_TERRACOTTA || bmv31 == Blocks.GREEN_TERRACOTTA || bmv31 == Blocks.RED_TERRACOTTA || bmv31 == Blocks.BLACK_TERRACOTTA) {
                            bxh.setBlockState(a28, ErodedBadlandsSurfaceBuilder.ORANGE_TERRACOTTA, false);
                        }
                    }
                }
                else if (integer14 > 0) {
                    --integer14;
                    if (boolean26) {
                        bxh.setBlockState(a28, ErodedBadlandsSurfaceBuilder.ORANGE_TERRACOTTA, false);
                    }
                    else {
                        bxh.setBlockState(a28, this.getBand(integer4, integer15, integer5), false);
                    }
                }
            }
        }
    }
    
    static {
        WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.defaultBlockState();
        ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.defaultBlockState();
        TERRACOTTA = Blocks.TERRACOTTA.defaultBlockState();
    }
}
