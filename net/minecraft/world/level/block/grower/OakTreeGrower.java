package net.minecraft.world.level.block.grower;

import javax.annotation.Nullable;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.feature.BigTreeFeature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import java.util.Random;

public class OakTreeGrower extends AbstractTreeGrower {
    @Nullable
    @Override
    protected AbstractTreeFeature<NoneFeatureConfiguration> getFeature(final Random random) {
        return (random.nextInt(10) == 0) ? new BigTreeFeature(NoneFeatureConfiguration::deserialize, true) : new TreeFeature(NoneFeatureConfiguration::deserialize, true);
    }
}
