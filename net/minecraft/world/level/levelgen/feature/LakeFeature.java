package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.block.state.BlockState;

public class LakeFeature extends Feature<LakeConfiguration> {
    private static final BlockState AIR;
    
    public LakeFeature(final Function<Dynamic<?>, ? extends LakeConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, BlockPos ew, final LakeConfiguration ccp) {
        while (ew.getY() > 5 && bhs.isEmptyBlock(ew)) {
            ew = ew.below();
        }
        if (ew.getY() <= 4) {
            return false;
        }
        ew = ew.below(4);
        final ChunkPos bhd7 = new ChunkPos(ew);
        if (!bhs.getChunk(bhd7.x, bhd7.z, ChunkStatus.STRUCTURE_REFERENCES).getReferencesForFeature(Feature.VILLAGE.getFeatureName()).isEmpty()) {
            return false;
        }
        final boolean[] arr8 = new boolean[2048];
        for (int integer9 = random.nextInt(4) + 4, integer10 = 0; integer10 < integer9; ++integer10) {
            final double double11 = random.nextDouble() * 6.0 + 3.0;
            final double double12 = random.nextDouble() * 4.0 + 2.0;
            final double double13 = random.nextDouble() * 6.0 + 3.0;
            final double double14 = random.nextDouble() * (16.0 - double11 - 2.0) + 1.0 + double11 / 2.0;
            final double double15 = random.nextDouble() * (8.0 - double12 - 4.0) + 2.0 + double12 / 2.0;
            final double double16 = random.nextDouble() * (16.0 - double13 - 2.0) + 1.0 + double13 / 2.0;
            for (int integer11 = 1; integer11 < 15; ++integer11) {
                for (int integer12 = 1; integer12 < 15; ++integer12) {
                    for (int integer13 = 1; integer13 < 7; ++integer13) {
                        final double double17 = (integer11 - double14) / (double11 / 2.0);
                        final double double18 = (integer13 - double15) / (double12 / 2.0);
                        final double double19 = (integer12 - double16) / (double13 / 2.0);
                        final double double20 = double17 * double17 + double18 * double18 + double19 * double19;
                        if (double20 < 1.0) {
                            arr8[(integer11 * 16 + integer12) * 8 + integer13] = true;
                        }
                    }
                }
            }
        }
        for (int integer10 = 0; integer10 < 16; ++integer10) {
            for (int integer14 = 0; integer14 < 16; ++integer14) {
                for (int integer15 = 0; integer15 < 8; ++integer15) {
                    final boolean boolean13 = !arr8[(integer10 * 16 + integer14) * 8 + integer15] && ((integer10 < 15 && arr8[((integer10 + 1) * 16 + integer14) * 8 + integer15]) || (integer10 > 0 && arr8[((integer10 - 1) * 16 + integer14) * 8 + integer15]) || (integer14 < 15 && arr8[(integer10 * 16 + integer14 + 1) * 8 + integer15]) || (integer14 > 0 && arr8[(integer10 * 16 + (integer14 - 1)) * 8 + integer15]) || (integer15 < 7 && arr8[(integer10 * 16 + integer14) * 8 + integer15 + 1]) || (integer15 > 0 && arr8[(integer10 * 16 + integer14) * 8 + (integer15 - 1)]));
                    if (boolean13) {
                        final Material clo14 = bhs.getBlockState(ew.offset(integer10, integer15, integer14)).getMaterial();
                        if (integer15 >= 4 && clo14.isLiquid()) {
                            return false;
                        }
                        if (integer15 < 4 && !clo14.isSolid() && bhs.getBlockState(ew.offset(integer10, integer15, integer14)) != ccp.state) {
                            return false;
                        }
                    }
                }
            }
        }
        for (int integer10 = 0; integer10 < 16; ++integer10) {
            for (int integer14 = 0; integer14 < 16; ++integer14) {
                for (int integer15 = 0; integer15 < 8; ++integer15) {
                    if (arr8[(integer10 * 16 + integer14) * 8 + integer15]) {
                        bhs.setBlock(ew.offset(integer10, integer15, integer14), (integer15 >= 4) ? LakeFeature.AIR : ccp.state, 2);
                    }
                }
            }
        }
        for (int integer10 = 0; integer10 < 16; ++integer10) {
            for (int integer14 = 0; integer14 < 16; ++integer14) {
                for (int integer15 = 4; integer15 < 8; ++integer15) {
                    if (arr8[(integer10 * 16 + integer14) * 8 + integer15]) {
                        final BlockPos ew2 = ew.offset(integer10, integer15 - 1, integer14);
                        if (Block.equalsDirt(bhs.getBlockState(ew2).getBlock()) && bhs.getBrightness(LightLayer.SKY, ew.offset(integer10, integer15, integer14)) > 0) {
                            final Biome bio14 = bhs.getBiome(ew2);
                            if (bio14.getSurfaceBuilderConfig().getTopMaterial().getBlock() == Blocks.MYCELIUM) {
                                bhs.setBlock(ew2, Blocks.MYCELIUM.defaultBlockState(), 2);
                            }
                            else {
                                bhs.setBlock(ew2, Blocks.GRASS_BLOCK.defaultBlockState(), 2);
                            }
                        }
                    }
                }
            }
        }
        if (ccp.state.getMaterial() == Material.LAVA) {
            for (int integer10 = 0; integer10 < 16; ++integer10) {
                for (int integer14 = 0; integer14 < 16; ++integer14) {
                    for (int integer15 = 0; integer15 < 8; ++integer15) {
                        final boolean boolean13 = !arr8[(integer10 * 16 + integer14) * 8 + integer15] && ((integer10 < 15 && arr8[((integer10 + 1) * 16 + integer14) * 8 + integer15]) || (integer10 > 0 && arr8[((integer10 - 1) * 16 + integer14) * 8 + integer15]) || (integer14 < 15 && arr8[(integer10 * 16 + integer14 + 1) * 8 + integer15]) || (integer14 > 0 && arr8[(integer10 * 16 + (integer14 - 1)) * 8 + integer15]) || (integer15 < 7 && arr8[(integer10 * 16 + integer14) * 8 + integer15 + 1]) || (integer15 > 0 && arr8[(integer10 * 16 + integer14) * 8 + (integer15 - 1)]));
                        if (boolean13 && (integer15 < 4 || random.nextInt(2) != 0) && bhs.getBlockState(ew.offset(integer10, integer15, integer14)).getMaterial().isSolid()) {
                            bhs.setBlock(ew.offset(integer10, integer15, integer14), Blocks.STONE.defaultBlockState(), 2);
                        }
                    }
                }
            }
        }
        if (ccp.state.getMaterial() == Material.WATER) {
            for (int integer10 = 0; integer10 < 16; ++integer10) {
                for (int integer14 = 0; integer14 < 16; ++integer14) {
                    final int integer15 = 4;
                    final BlockPos ew2 = ew.offset(integer10, 4, integer14);
                    if (bhs.getBiome(ew2).shouldFreeze(bhs, ew2, false)) {
                        bhs.setBlock(ew2, Blocks.ICE.defaultBlockState(), 2);
                    }
                }
            }
        }
        return true;
    }
    
    static {
        AIR = Blocks.CAVE_AIR.defaultBlockState();
    }
}
