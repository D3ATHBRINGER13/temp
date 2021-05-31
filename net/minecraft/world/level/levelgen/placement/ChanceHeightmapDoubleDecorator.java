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

public class ChanceHeightmapDoubleDecorator extends FeatureDecorator<DecoratorChance> {
    public ChanceHeightmapDoubleDecorator(final Function<Dynamic<?>, ? extends DecoratorChance> function) {
        super(function);
    }
    
    @Override
    public Stream<BlockPos> getPositions(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final DecoratorChance cgu, final BlockPos ew) {
        if (random.nextFloat() >= 1.0f / cgu.chance) {
            return (Stream<BlockPos>)Stream.empty();
        }
        final int integer7 = random.nextInt(16);
        final int integer8 = random.nextInt(16);
        final int integer9 = bhs.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, ew.offset(integer7, 0, integer8)).getY() * 2;
        if (integer9 <= 0) {
            return (Stream<BlockPos>)Stream.empty();
        }
        final int integer10 = random.nextInt(integer9);
        return (Stream<BlockPos>)Stream.of(ew.offset(integer7, integer10, integer8));
    }
}
