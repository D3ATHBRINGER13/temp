package net.minecraft.world.level.levelgen.surfacebuilders;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.block.state.BlockState;

public class WoodedBadlandsSurfaceBuilder extends BadlandsSurfaceBuilder {
    private static final BlockState WHITE_TERRACOTTA;
    private static final BlockState ORANGE_TERRACOTTA;
    private static final BlockState TERRACOTTA;
    
    public WoodedBadlandsSurfaceBuilder(final Function<Dynamic<?>, ? extends SurfaceBuilderBaseConfiguration> function) {
        super(function);
    }
    
    @Override
    public void apply(final Random random, final ChunkAccess bxh, final Biome bio, final int integer4, final int integer5, final int integer6, final double double7, final BlockState bvt8, final BlockState bvt9, final int integer10, final long long11, final SurfaceBuilderBaseConfiguration cki) {
        final int integer11 = integer4 & 0xF;
        final int integer12 = integer5 & 0xF;
        BlockState bvt10 = WoodedBadlandsSurfaceBuilder.WHITE_TERRACOTTA;
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
                            bvt10 = WoodedBadlandsSurfaceBuilder.WHITE_TERRACOTTA;
                            bvt11 = bio.getSurfaceBuilderConfig().getUnderMaterial();
                        }
                        if (integer16 < integer10 && (bvt10 == null || bvt10.isAir())) {
                            bvt10 = bvt9;
                        }
                        integer14 = integer13 + Math.max(0, integer16 - integer10);
                        if (integer16 >= integer10 - 1) {
                            if (integer16 > 86 + integer13 * 2) {
                                if (boolean21) {
                                    bxh.setBlockState(a25, Blocks.COARSE_DIRT.defaultBlockState(), false);
                                }
                                else {
                                    bxh.setBlockState(a25, Blocks.GRASS_BLOCK.defaultBlockState(), false);
                                }
                            }
                            else if (integer16 > integer10 + 3 + integer13) {
                                BlockState bvt13;
                                if (integer16 < 64 || integer16 > 127) {
                                    bvt13 = WoodedBadlandsSurfaceBuilder.ORANGE_TERRACOTTA;
                                }
                                else if (boolean21) {
                                    bvt13 = WoodedBadlandsSurfaceBuilder.TERRACOTTA;
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
                            if (bvt11 == WoodedBadlandsSurfaceBuilder.WHITE_TERRACOTTA) {
                                bxh.setBlockState(a25, WoodedBadlandsSurfaceBuilder.ORANGE_TERRACOTTA, false);
                            }
                        }
                    }
                    else if (integer14 > 0) {
                        --integer14;
                        if (boolean22) {
                            bxh.setBlockState(a25, WoodedBadlandsSurfaceBuilder.ORANGE_TERRACOTTA, false);
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
    
    static {
        WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.defaultBlockState();
        ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.defaultBlockState();
        TERRACOTTA = Blocks.TERRACOTTA.defaultBlockState();
    }
}
