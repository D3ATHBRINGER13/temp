package net.minecraft.world.level.levelgen.placement;

import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.Heightmap;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class ChanceHeightmapDecorator extends FeatureDecorator<DecoratorChance> {
    public ChanceHeightmapDecorator(final Function<Dynamic<?>, ? extends DecoratorChance> function) {
        super(function);
    }
    
    @Override
    public Stream<BlockPos> getPositions(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final DecoratorChance cgu, final BlockPos ew) {
        if (random.nextFloat() < 1.0f / cgu.chance) {
            final int integer7 = random.nextInt(16);
            final int integer8 = random.nextInt(16);
            final BlockPos ew2 = bhs.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, ew.offset(integer7, 0, integer8));
            return (Stream<BlockPos>)Stream.of(ew2);
        }
        return (Stream<BlockPos>)Stream.empty();
    }
}
