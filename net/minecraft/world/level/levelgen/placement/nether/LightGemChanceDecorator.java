package net.minecraft.world.level.levelgen.placement.nether;

import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.placement.DecoratorFrequency;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

public class LightGemChanceDecorator extends SimpleFeatureDecorator<DecoratorFrequency> {
    public LightGemChanceDecorator(final Function<Dynamic<?>, ? extends DecoratorFrequency> function) {
        super(function);
    }
    
    public Stream<BlockPos> place(final Random random, final DecoratorFrequency cgv, final BlockPos ew) {
        return (Stream<BlockPos>)IntStream.range(0, random.nextInt(random.nextInt(cgv.count) + 1)).mapToObj(integer -> {
            final int integer2 = random.nextInt(16);
            final int integer3 = random.nextInt(120) + 4;
            final int integer4 = random.nextInt(16);
            return ew.offset(integer2, integer3, integer4);
        });
    }
}
