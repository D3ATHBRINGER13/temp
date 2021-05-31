package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.Blocks;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.core.BlockPos;

public class VoidStartPlatformFeature extends Feature<NoneFeatureConfiguration> {
    private static final BlockPos PLATFORM_ORIGIN;
    private static final ChunkPos PLATFORM_ORIGIN_CHUNK;
    
    public VoidStartPlatformFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    private static int checkerboardDistance(final int integer1, final int integer2, final int integer3, final int integer4) {
        return Math.max(Math.abs(integer1 - integer3), Math.abs(integer2 - integer4));
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final NoneFeatureConfiguration cdd) {
        final ChunkPos bhd7 = new ChunkPos(ew);
        if (checkerboardDistance(bhd7.x, bhd7.z, VoidStartPlatformFeature.PLATFORM_ORIGIN_CHUNK.x, VoidStartPlatformFeature.PLATFORM_ORIGIN_CHUNK.z) > 1) {
            return true;
        }
        final BlockPos.MutableBlockPos a8 = new BlockPos.MutableBlockPos();
        for (int integer9 = bhd7.getMinBlockZ(); integer9 <= bhd7.getMaxBlockZ(); ++integer9) {
            for (int integer10 = bhd7.getMinBlockX(); integer10 <= bhd7.getMaxBlockX(); ++integer10) {
                if (checkerboardDistance(VoidStartPlatformFeature.PLATFORM_ORIGIN.getX(), VoidStartPlatformFeature.PLATFORM_ORIGIN.getZ(), integer10, integer9) <= 16) {
                    a8.set(integer10, VoidStartPlatformFeature.PLATFORM_ORIGIN.getY(), integer9);
                    if (a8.equals(VoidStartPlatformFeature.PLATFORM_ORIGIN)) {
                        bhs.setBlock(a8, Blocks.COBBLESTONE.defaultBlockState(), 2);
                    }
                    else {
                        bhs.setBlock(a8, Blocks.STONE.defaultBlockState(), 2);
                    }
                }
            }
        }
        return true;
    }
    
    static {
        PLATFORM_ORIGIN = new BlockPos(8, 3, 8);
        PLATFORM_ORIGIN_CHUNK = new ChunkPos(VoidStartPlatformFeature.PLATFORM_ORIGIN);
    }
}
