package net.minecraft.world.level.levelgen.placement;

import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import java.util.Objects;
import java.util.stream.IntStream;
import net.minecraft.world.level.biome.Biome;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.feature.DecoratorNoiseDependant;

public class NoiseHeightmap32Decorator extends FeatureDecorator<DecoratorNoiseDependant> {
    public NoiseHeightmap32Decorator(final Function<Dynamic<?>, ? extends DecoratorNoiseDependant> function) {
        super(function);
    }
    
    @Override
    public Stream<BlockPos> getPositions(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final DecoratorNoiseDependant caz, final BlockPos ew) {
        final double double7 = Biome.BIOME_INFO_NOISE.getValue(ew.getX() / 200.0, ew.getZ() / 200.0);
        final int integer9 = (double7 < caz.noiseLevel) ? caz.belowNoise : caz.aboveNoise;
        return (Stream<BlockPos>)IntStream.range(0, integer9).mapToObj(integer -> {
            final int integer2 = random.nextInt(16);
            final int integer3 = random.nextInt(16);
            final int integer4 = bhs.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, ew.offset(integer2, 0, integer3)).getY() + 32;
            if (integer4 <= 0) {
                return null;
            }
            final int integer5 = random.nextInt(integer4);
            return ew.offset(integer2, integer5, integer3);
        }).filter(Objects::nonNull);
    }
}
