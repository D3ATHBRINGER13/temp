package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class HayBlockPileFeature extends BlockPileFeature {
    public HayBlockPileFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    protected BlockState getBlockState(final LevelAccessor bhs) {
        final Direction.Axis a3 = Direction.Axis.getRandomAxis(bhs.getRandom());
        return ((AbstractStateHolder<O, BlockState>)Blocks.HAY_BLOCK.defaultBlockState()).<Direction.Axis, Direction.Axis>setValue(RotatedPillarBlock.AXIS, a3);
    }
}
