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
import net.minecraft.world.level.levelgen.feature.NoneDecoratorConfiguration;

public class DarkOakTreePlacementDecorator extends FeatureDecorator<NoneDecoratorConfiguration> {
    public DarkOakTreePlacementDecorator(final Function<Dynamic<?>, ? extends NoneDecoratorConfiguration> function) {
        super(function);
    }
    
    @Override
    public Stream<BlockPos> getPositions(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final NoneDecoratorConfiguration cdc, final BlockPos ew) {
        return (Stream<BlockPos>)IntStream.range(0, 16).mapToObj(integer -> {
            final int integer2 = integer / 4;
            final int integer3 = integer % 4;
            final int integer4 = integer2 * 4 + 1 + random.nextInt(3);
            final int integer5 = integer3 * 4 + 1 + random.nextInt(3);
            return bhs.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, ew.offset(integer4, 0, integer5));
        });
    }
}
