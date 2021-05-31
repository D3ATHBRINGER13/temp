package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class IceBlockPileFeature extends BlockPileFeature {
    public IceBlockPileFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    protected BlockState getBlockState(final LevelAccessor bhs) {
        return (bhs.getRandom().nextInt(7) == 0) ? Blocks.BLUE_ICE.defaultBlockState() : Blocks.PACKED_ICE.defaultBlockState();
    }
}
