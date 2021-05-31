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

public class CountRangeDecorator extends SimpleFeatureDecorator<DecoratorCountRange> {
    public CountRangeDecorator(final Function<Dynamic<?>, ? extends DecoratorCountRange> function) {
        super(function);
    }
    
    public Stream<BlockPos> place(final Random random, final DecoratorCountRange cay, final BlockPos ew) {
        return (Stream<BlockPos>)IntStream.range(0, cay.count).mapToObj(integer -> {
            final int integer2 = random.nextInt(16);
            final int integer3 = random.nextInt(cay.maximum - cay.topOffset) + cay.bottomOffset;
            final int integer4 = random.nextInt(16);
            return ew.offset(integer2, integer3, integer4);
        });
    }
}
