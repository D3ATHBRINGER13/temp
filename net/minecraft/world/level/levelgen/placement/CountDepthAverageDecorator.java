package net.minecraft.world.level.levelgen.placement;

import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class CountDepthAverageDecorator extends SimpleFeatureDecorator<DepthAverageConfigation> {
    public CountDepthAverageDecorator(final Function<Dynamic<?>, ? extends DepthAverageConfigation> function) {
        super(function);
    }
    
    public Stream<BlockPos> place(final Random random, final DepthAverageConfigation cha, final BlockPos ew) {
        final int integer5 = cha.count;
        final int integer6 = cha.baseline;
        final int integer7 = cha.spread;
        return (Stream<BlockPos>)IntStream.range(0, integer5).mapToObj(integer5 -> {
            final int integer6 = random.nextInt(16);
            final int integer7 = random.nextInt(integer7) + random.nextInt(integer7) - integer7 + integer6;
            final int integer8 = random.nextInt(16);
            return ew.offset(integer6, integer7, integer8);
        });
    }
}
