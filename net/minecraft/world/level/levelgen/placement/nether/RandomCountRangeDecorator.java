package net.minecraft.world.level.levelgen.placement.nether;

import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.feature.DecoratorCountRange;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

public class RandomCountRangeDecorator extends SimpleFeatureDecorator<DecoratorCountRange> {
    public RandomCountRangeDecorator(final Function<Dynamic<?>, ? extends DecoratorCountRange> function) {
        super(function);
    }
    
    public Stream<BlockPos> place(final Random random, final DecoratorCountRange cay, final BlockPos ew) {
        final int integer5 = random.nextInt(Math.max(cay.count, 1));
        return (Stream<BlockPos>)IntStream.range(0, integer5).mapToObj(integer -> {
            final int integer2 = random.nextInt(16);
            final int integer3 = random.nextInt(cay.maximum - cay.topOffset) + cay.bottomOffset;
            final int integer4 = random.nextInt(16);
            return ew.offset(integer2, integer3, integer4);
        });
    }
}
