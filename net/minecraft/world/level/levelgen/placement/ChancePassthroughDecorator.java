package net.minecraft.world.level.levelgen.placement;

import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class ChancePassthroughDecorator extends SimpleFeatureDecorator<DecoratorChance> {
    public ChancePassthroughDecorator(final Function<Dynamic<?>, ? extends DecoratorChance> function) {
        super(function);
    }
    
    public Stream<BlockPos> place(final Random random, final DecoratorChance cgu, final BlockPos ew) {
        if (random.nextFloat() < 1.0f / cgu.chance) {
            return (Stream<BlockPos>)Stream.of(ew);
        }
        return (Stream<BlockPos>)Stream.empty();
    }
}
