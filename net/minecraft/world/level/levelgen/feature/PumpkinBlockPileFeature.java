package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class PumpkinBlockPileFeature extends BlockPileFeature {
    public PumpkinBlockPileFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    protected BlockState getBlockState(final LevelAccessor bhs) {
        if (bhs.getRandom().nextFloat() < 0.95f) {
            return Blocks.PUMPKIN.defaultBlockState();
        }
        return Blocks.JACK_O_LANTERN.defaultBlockState();
    }
}
