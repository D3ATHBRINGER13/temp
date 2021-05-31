package net.minecraft.world.level.block.grower;

import net.minecraft.world.level.levelgen.feature.MegaPineTreeFeature;
import javax.annotation.Nullable;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.feature.SpruceFeature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import java.util.Random;

public class SpruceTreeGrower extends AbstractMegaTreeGrower {
    @Nullable
    @Override
    protected AbstractTreeFeature<NoneFeatureConfiguration> getFeature(final Random random) {
        return new SpruceFeature(NoneFeatureConfiguration::deserialize, true);
    }
    
    @Nullable
    @Override
    protected AbstractTreeFeature<NoneFeatureConfiguration> getMegaFeature(final Random random) {
        return new MegaPineTreeFeature(NoneFeatureConfiguration::deserialize, false, random.nextBoolean());
    }
}
