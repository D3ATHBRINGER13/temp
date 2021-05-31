package net.minecraft.world.level.block.grower;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.feature.DarkOakFeature;
import javax.annotation.Nullable;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import java.util.Random;

public class DarkOakTreeGrower extends AbstractMegaTreeGrower {
    @Nullable
    @Override
    protected AbstractTreeFeature<NoneFeatureConfiguration> getFeature(final Random random) {
        return null;
    }
    
    @Nullable
    @Override
    protected AbstractTreeFeature<NoneFeatureConfiguration> getMegaFeature(final Random random) {
        return new DarkOakFeature(NoneFeatureConfiguration::deserialize, true);
    }
}
