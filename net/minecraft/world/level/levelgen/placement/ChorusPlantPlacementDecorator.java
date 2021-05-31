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
import net.minecraft.world.level.levelgen.feature.NoneDecoratorConfiguration;

public class ChorusPlantPlacementDecorator extends FeatureDecorator<NoneDecoratorConfiguration> {
    public ChorusPlantPlacementDecorator(final Function<Dynamic<?>, ? extends NoneDecoratorConfiguration> function) {
        super(function);
    }
    
    @Override
    public Stream<BlockPos> getPositions(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final NoneDecoratorConfiguration cdc, final BlockPos ew) {
        final int integer7 = random.nextInt(5);
        return (Stream<BlockPos>)IntStream.range(0, integer7).mapToObj(integer -> {
            final int integer2 = random.nextInt(16);
            final int integer3 = random.nextInt(16);
            final int integer4 = bhs.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, ew.offset(integer2, 0, integer3)).getY();
            if (integer4 > 0) {
                final int integer5 = integer4 - 1;
                return new BlockPos(ew.getX() + integer2, integer5, ew.getZ() + integer3);
            }
            return null;
        }).filter(Objects::nonNull);
    }
}
