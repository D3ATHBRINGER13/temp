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

public class CountWithExtraChanceHeightmapDecorator extends FeatureDecorator<DecoratorFrequencyWithExtraChance> {
    public CountWithExtraChanceHeightmapDecorator(final Function<Dynamic<?>, ? extends DecoratorFrequencyWithExtraChance> function) {
        super(function);
    }
    
    @Override
    public Stream<BlockPos> getPositions(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final DecoratorFrequencyWithExtraChance cgx, final BlockPos ew) {
        int integer7 = cgx.count;
        if (random.nextFloat() < cgx.extraChance) {
            integer7 += cgx.extraCount;
        }
        return (Stream<BlockPos>)IntStream.range(0, integer7).mapToObj(integer -> {
            final int integer2 = random.nextInt(16);
            final int integer3 = random.nextInt(16);
            return bhs.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, ew.offset(integer2, 0, integer3));
        });
    }
}
