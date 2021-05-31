package net.minecraft.world.level.levelgen.placement.nether;

import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.placement.DecoratorFrequency;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

public class MagmaDecorator extends FeatureDecorator<DecoratorFrequency> {
    public MagmaDecorator(final Function<Dynamic<?>, ? extends DecoratorFrequency> function) {
        super(function);
    }
    
    @Override
    public Stream<BlockPos> getPositions(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final DecoratorFrequency cgv, final BlockPos ew) {
        final int integer7 = bhs.getSeaLevel() / 2 + 1;
        return (Stream<BlockPos>)IntStream.range(0, cgv.count).mapToObj(integer4 -> {
            final int integer5 = random.nextInt(16);
            final int integer6 = integer7 - 5 + random.nextInt(10);
            final int integer7 = random.nextInt(16);
            return ew.offset(integer5, integer6, integer7);
        });
    }
}
