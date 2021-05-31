package net.minecraft.world.level.levelgen.placement;

import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.feature.DecoratorCountRange;

public class CountVeryBiasedRangeDecorator extends SimpleFeatureDecorator<DecoratorCountRange> {
    public CountVeryBiasedRangeDecorator(final Function<Dynamic<?>, ? extends DecoratorCountRange> function) {
        super(function);
    }
    
    public Stream<BlockPos> place(final Random random, final DecoratorCountRange cay, final BlockPos ew) {
        return (Stream<BlockPos>)IntStream.range(0, cay.count).mapToObj(integer -> {
            final int integer2 = random.nextInt(16);
            final int integer3 = random.nextInt(16);
            final int integer4 = random.nextInt(random.nextInt(random.nextInt(cay.maximum - cay.topOffset) + cay.bottomOffset) + cay.bottomOffset);
            return ew.offset(integer2, integer4, integer3);
        });
    }
}
