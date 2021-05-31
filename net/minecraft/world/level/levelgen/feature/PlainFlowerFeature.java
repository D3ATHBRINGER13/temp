package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class PlainFlowerFeature extends FlowerFeature {
    public PlainFlowerFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public BlockState getRandomFlower(final Random random, final BlockPos ew) {
        final double double4 = Biome.BIOME_INFO_NOISE.getValue(ew.getX() / 200.0, ew.getZ() / 200.0);
        if (double4 < -0.8) {
            final int integer6 = random.nextInt(4);
            switch (integer6) {
                case 0: {
                    return Blocks.ORANGE_TULIP.defaultBlockState();
                }
                case 1: {
                    return Blocks.RED_TULIP.defaultBlockState();
                }
                case 2: {
                    return Blocks.PINK_TULIP.defaultBlockState();
                }
                default: {
                    return Blocks.WHITE_TULIP.defaultBlockState();
                }
            }
        }
        else {
            if (random.nextInt(3) <= 0) {
                return Blocks.DANDELION.defaultBlockState();
            }
            final int integer6 = random.nextInt(4);
            switch (integer6) {
                case 0: {
                    return Blocks.POPPY.defaultBlockState();
                }
                case 1: {
                    return Blocks.AZURE_BLUET.defaultBlockState();
                }
                case 2: {
                    return Blocks.OXEYE_DAISY.defaultBlockState();
                }
                default: {
                    return Blocks.CORNFLOWER.defaultBlockState();
                }
            }
        }
    }
}
