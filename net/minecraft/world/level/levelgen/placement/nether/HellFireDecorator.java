package net.minecraft.world.level.levelgen.placement.nether;

import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import java.util.List;
import com.google.common.collect.Lists;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.placement.DecoratorFrequency;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

public class HellFireDecorator extends SimpleFeatureDecorator<DecoratorFrequency> {
    public HellFireDecorator(final Function<Dynamic<?>, ? extends DecoratorFrequency> function) {
        super(function);
    }
    
    public Stream<BlockPos> place(final Random random, final DecoratorFrequency cgv, final BlockPos ew) {
        final List<BlockPos> list5 = (List<BlockPos>)Lists.newArrayList();
        for (int integer6 = 0; integer6 < random.nextInt(random.nextInt(cgv.count) + 1) + 1; ++integer6) {
            final int integer7 = random.nextInt(16);
            final int integer8 = random.nextInt(120) + 4;
            final int integer9 = random.nextInt(16);
            list5.add(ew.offset(integer7, integer8, integer9));
        }
        return (Stream<BlockPos>)list5.stream();
    }
}
