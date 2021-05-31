package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class SnowBlockPileFeature extends BlockPileFeature {
    public SnowBlockPileFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    protected BlockState getBlockState(final LevelAccessor bhs) {
        return Blocks.SNOW_BLOCK.defaultBlockState();
    }
}
