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

public class TopSolidHeightMapRangeDecorator extends FeatureDecorator<DecoratorRange> {
    public TopSolidHeightMapRangeDecorator(final Function<Dynamic<?>, ? extends DecoratorRange> function) {
        super(function);
    }
    
    @Override
    public Stream<BlockPos> getPositions(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final DecoratorRange cgz, final BlockPos ew) {
        final int integer7 = random.nextInt(cgz.max - cgz.min) + cgz.min;
        return (Stream<BlockPos>)IntStream.range(0, integer7).mapToObj(integer -> {
            final int integer2 = random.nextInt(16);
            final int integer3 = random.nextInt(16);
            final int integer4 = bhs.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, ew.getX() + integer2, ew.getZ() + integer3);
            return new BlockPos(ew.getX() + integer2, integer4, ew.getZ() + integer3);
        });
    }
}
