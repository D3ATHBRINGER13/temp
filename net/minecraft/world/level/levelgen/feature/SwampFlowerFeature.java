package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class SwampFlowerFeature extends FlowerFeature {
    public SwampFlowerFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public BlockState getRandomFlower(final Random random, final BlockPos ew) {
        return Blocks.BLUE_ORCHID.defaultBlockState();
    }
}
