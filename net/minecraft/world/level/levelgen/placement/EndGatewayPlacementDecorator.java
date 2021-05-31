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
import net.minecraft.world.level.levelgen.feature.NoneDecoratorConfiguration;

public class EndGatewayPlacementDecorator extends FeatureDecorator<NoneDecoratorConfiguration> {
    public EndGatewayPlacementDecorator(final Function<Dynamic<?>, ? extends NoneDecoratorConfiguration> function) {
        super(function);
    }
    
    @Override
    public Stream<BlockPos> getPositions(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final NoneDecoratorConfiguration cdc, final BlockPos ew) {
        if (random.nextInt(700) == 0) {
            final int integer7 = random.nextInt(16);
            final int integer8 = random.nextInt(16);
            final int integer9 = bhs.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, ew.offset(integer7, 0, integer8)).getY();
            if (integer9 > 0) {
                final int integer10 = integer9 + 3 + random.nextInt(7);
                return (Stream<BlockPos>)Stream.of(ew.offset(integer7, integer10, integer8));
            }
        }
        return (Stream<BlockPos>)Stream.empty();
    }
}
