package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class JungleTreeFeature extends TreeFeature {
    public JungleTreeFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function, final boolean boolean2, final int integer, final BlockState bvt4, final BlockState bvt5, final boolean boolean6) {
        super(function, boolean2, integer, bvt4, bvt5, boolean6);
    }
    
    @Override
    protected int getTreeHeight(final Random random) {
        return this.baseHeight + random.nextInt(7);
    }
}
