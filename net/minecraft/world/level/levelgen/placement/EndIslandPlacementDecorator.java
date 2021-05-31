package net.minecraft.world.level.levelgen.placement;

import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.feature.NoneDecoratorConfiguration;

public class EndIslandPlacementDecorator extends SimpleFeatureDecorator<NoneDecoratorConfiguration> {
    public EndIslandPlacementDecorator(final Function<Dynamic<?>, ? extends NoneDecoratorConfiguration> function) {
        super(function);
    }
    
    public Stream<BlockPos> place(final Random random, final NoneDecoratorConfiguration cdc, final BlockPos ew) {
        Stream<BlockPos> stream5 = (Stream<BlockPos>)Stream.empty();
        if (random.nextInt(14) == 0) {
            stream5 = (Stream<BlockPos>)Stream.concat((Stream)stream5, Stream.of(ew.offset(random.nextInt(16), 55 + random.nextInt(16), random.nextInt(16))));
            if (random.nextInt(4) == 0) {
                stream5 = (Stream<BlockPos>)Stream.concat((Stream)stream5, Stream.of(ew.offset(random.nextInt(16), 55 + random.nextInt(16), random.nextInt(16))));
            }
            return stream5;
        }
        return (Stream<BlockPos>)Stream.empty();
    }
}
