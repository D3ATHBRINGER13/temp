package net.minecraft.world.level.levelgen.placement;

import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class CountHeighmapDoubleDecorator extends FeatureDecorator<DecoratorFrequency> {
    public CountHeighmapDoubleDecorator(final Function<Dynamic<?>, ? extends DecoratorFrequency> function) {
        super(function);
    }
    
    @Override
    public Stream<BlockPos> getPositions(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final DecoratorFrequency cgv, final BlockPos ew) {
        return (Stream<BlockPos>)IntStream.range(0, cgv.count).mapToObj(integer -> {
            final int integer2 = random.nextInt(16);
            final int integer3 = random.nextInt(16);
            final int integer4 = bhs.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, ew.offset(integer2, 0, integer3)).getY() * 2;
            if (integer4 <= 0) {
                return null;
            }
            final int integer5 = random.nextInt(integer4);
            return ew.offset(integer2, integer5, integer3);
        }).filter(Objects::nonNull);
    }
}
