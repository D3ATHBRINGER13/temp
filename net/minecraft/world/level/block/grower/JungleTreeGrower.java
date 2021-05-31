package net.minecraft.world.level.block.grower;

import net.minecraft.world.level.levelgen.feature.MegaJungleTreeFeature;
import javax.annotation.Nullable;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import java.util.Random;

public class JungleTreeGrower extends AbstractMegaTreeGrower {
    @Nullable
    @Override
    protected AbstractTreeFeature<NoneFeatureConfiguration> getFeature(final Random random) {
        return new TreeFeature(NoneFeatureConfiguration::deserialize, true, 4 + random.nextInt(7), Blocks.JUNGLE_LOG.defaultBlockState(), Blocks.JUNGLE_LEAVES.defaultBlockState(), false);
    }
    
    @Nullable
    @Override
    protected AbstractTreeFeature<NoneFeatureConfiguration> getMegaFeature(final Random random) {
        return new MegaJungleTreeFeature(NoneFeatureConfiguration::deserialize, true, 10, 20, Blocks.JUNGLE_LOG.defaultBlockState(), Blocks.JUNGLE_LEAVES.defaultBlockState());
    }
}
