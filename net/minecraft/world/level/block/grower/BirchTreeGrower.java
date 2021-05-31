package net.minecraft.world.level.block.grower;

import javax.annotation.Nullable;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.feature.BirchFeature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import java.util.Random;

public class BirchTreeGrower extends AbstractTreeGrower {
    @Nullable
    @Override
    protected AbstractTreeFeature<NoneFeatureConfiguration> getFeature(final Random random) {
        return new BirchFeature(NoneFeatureConfiguration::deserialize, true, false);
    }
}
