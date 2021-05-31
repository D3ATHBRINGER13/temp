package net.minecraft.world.level.levelgen.placement;

import net.minecraft.world.level.levelgen.Heightmap;
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

public class CountTopSolidDecorator extends FeatureDecorator<DecoratorFrequency> {
    public CountTopSolidDecorator(final Function<Dynamic<?>, ? extends DecoratorFrequency> function) {
        super(function);
    }
    
    @Override
    public Stream<BlockPos> getPositions(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final DecoratorFrequency cgv, final BlockPos ew) {
        return (Stream<BlockPos>)IntStream.range(0, cgv.count).mapToObj(integer -> {
            final int integer2 = random.nextInt(16) + ew.getX();
            final int integer3 = random.nextInt(16) + ew.getZ();
            return new BlockPos(integer2, bhs.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, integer2, integer3), integer3);
        });
    }
}
