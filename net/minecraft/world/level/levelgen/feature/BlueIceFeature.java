package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class BlueIceFeature extends Feature<NoneFeatureConfiguration> {
    public BlueIceFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final NoneFeatureConfiguration cdd) {
        if (ew.getY() > bhs.getSeaLevel() - 1) {
            return false;
        }
        if (bhs.getBlockState(ew).getBlock() != Blocks.WATER && bhs.getBlockState(ew.below()).getBlock() != Blocks.WATER) {
            return false;
        }
        boolean boolean7 = false;
        for (final Direction fb11 : Direction.values()) {
            if (fb11 != Direction.DOWN) {
                if (bhs.getBlockState(ew.relative(fb11)).getBlock() == Blocks.PACKED_ICE) {
                    boolean7 = true;
                    break;
                }
            }
        }
        if (!boolean7) {
            return false;
        }
        bhs.setBlock(ew, Blocks.BLUE_ICE.defaultBlockState(), 2);
        for (int integer8 = 0; integer8 < 200; ++integer8) {
            final int integer9 = random.nextInt(5) - random.nextInt(6);
            int integer10 = 3;
            if (integer9 < 2) {
                integer10 += integer9 / 2;
            }
            if (integer10 >= 1) {
                final BlockPos ew2 = ew.offset(random.nextInt(integer10) - random.nextInt(integer10), integer9, random.nextInt(integer10) - random.nextInt(integer10));
                final BlockState bvt12 = bhs.getBlockState(ew2);
                final Block bmv13 = bvt12.getBlock();
                if (bvt12.getMaterial() == Material.AIR || bmv13 == Blocks.WATER || bmv13 == Blocks.PACKED_ICE || bmv13 == Blocks.ICE) {
                    for (final Direction fb12 : Direction.values()) {
                        final Block bmv14 = bhs.getBlockState(ew2.relative(fb12)).getBlock();
                        if (bmv14 == Blocks.BLUE_ICE) {
                            bhs.setBlock(ew2, Blocks.BLUE_ICE.defaultBlockState(), 2);
                            break;
                        }
                    }
                }
            }
        }
        return true;
    }
}
