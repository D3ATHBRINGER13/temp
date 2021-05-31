package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.block.Block;

public class ForestFlowerFeature extends FlowerFeature {
    private static final Block[] flowers;
    
    public ForestFlowerFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public BlockState getRandomFlower(final Random random, final BlockPos ew) {
        final double double4 = Mth.clamp((1.0 + Biome.BIOME_INFO_NOISE.getValue(ew.getX() / 48.0, ew.getZ() / 48.0)) / 2.0, 0.0, 0.9999);
        final Block bmv6 = ForestFlowerFeature.flowers[(int)(double4 * ForestFlowerFeature.flowers.length)];
        if (bmv6 == Blocks.BLUE_ORCHID) {
            return Blocks.POPPY.defaultBlockState();
        }
        return bmv6.defaultBlockState();
    }
    
    static {
        flowers = new Block[] { Blocks.DANDELION, Blocks.POPPY, Blocks.BLUE_ORCHID, Blocks.ALLIUM, Blocks.AZURE_BLUET, Blocks.RED_TULIP, Blocks.ORANGE_TULIP, Blocks.WHITE_TULIP, Blocks.PINK_TULIP, Blocks.OXEYE_DAISY, Blocks.CORNFLOWER, Blocks.LILY_OF_THE_VALLEY };
    }
}
