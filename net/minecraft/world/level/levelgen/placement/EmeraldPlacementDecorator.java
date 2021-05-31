package net.minecraft.world.level.levelgen.placement;

import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.feature.NoneDecoratorConfiguration;

public class EmeraldPlacementDecorator extends SimpleFeatureDecorator<NoneDecoratorConfiguration> {
    public EmeraldPlacementDecorator(final Function<Dynamic<?>, ? extends NoneDecoratorConfiguration> function) {
        super(function);
    }
    
    public Stream<BlockPos> place(final Random random, final NoneDecoratorConfiguration cdc, final BlockPos ew) {
        final int integer5 = 3 + random.nextInt(6);
        return (Stream<BlockPos>)IntStream.range(0, integer5).mapToObj(integer -> {
            final int integer2 = random.nextInt(16);
            final int integer3 = random.nextInt(28) + 4;
            final int integer4 = random.nextInt(16);
            return ew.offset(integer2, integer3, integer4);
        });
    }
}
