package net.minecraft.world.level.levelgen.placement.nether;

import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.feature.DecoratorChanceRange;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

public class ChanceRangeDecorator extends SimpleFeatureDecorator<DecoratorChanceRange> {
    public ChanceRangeDecorator(final Function<Dynamic<?>, ? extends DecoratorChanceRange> function) {
        super(function);
    }
    
    public Stream<BlockPos> place(final Random random, final DecoratorChanceRange caw, final BlockPos ew) {
        if (random.nextFloat() < caw.chance) {
            final int integer5 = random.nextInt(16);
            final int integer6 = random.nextInt(caw.top - caw.topOffset) + caw.bottomOffset;
            final int integer7 = random.nextInt(16);
            return (Stream<BlockPos>)Stream.of(ew.offset(integer5, integer6, integer7));
        }
        return (Stream<BlockPos>)Stream.empty();
    }
}
