package net.minecraft.world.level.levelgen.placement;

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

public class MonsterRoomPlacementDecorator extends FeatureDecorator<MonsterRoomPlacementConfiguration> {
    public MonsterRoomPlacementDecorator(final Function<Dynamic<?>, ? extends MonsterRoomPlacementConfiguration> function) {
        super(function);
    }
    
    @Override
    public Stream<BlockPos> getPositions(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final MonsterRoomPlacementConfiguration chk, final BlockPos ew) {
        final int integer7 = chk.chance;
        return (Stream<BlockPos>)IntStream.range(0, integer7).mapToObj(integer -> {
            final int integer2 = random.nextInt(16);
            final int integer3 = random.nextInt(bxi.getGenDepth());
            final int integer4 = random.nextInt(16);
            return ew.offset(integer2, integer3, integer4);
        });
    }
}
