package net.minecraft.world.level.levelgen;

import net.minecraft.world.level.block.Blocks;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.core.Registry;
import java.util.stream.Stream;
import net.minecraft.world.level.block.Block;
import net.minecraft.util.Mth;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class DebugLevelSource extends ChunkGenerator<DebugGeneratorSettings> {
    private static final List<BlockState> ALL_BLOCKS;
    private static final int GRID_WIDTH;
    private static final int GRID_HEIGHT;
    protected static final BlockState AIR;
    protected static final BlockState BARRIER;
    
    public DebugLevelSource(final LevelAccessor bhs, final BiomeSource biq, final DebugGeneratorSettings byw) {
        super(bhs, biq, byw);
    }
    
    @Override
    public void buildSurfaceAndBedrock(final ChunkAccess bxh) {
    }
    
    @Override
    public void applyCarvers(final ChunkAccess bxh, final GenerationStep.Carving a) {
    }
    
    @Override
    public int getSpawnHeight() {
        return this.level.getSeaLevel() + 1;
    }
    
    @Override
    public void applyBiomeDecoration(final WorldGenRegion vq) {
        final BlockPos.MutableBlockPos a3 = new BlockPos.MutableBlockPos();
        final int integer4 = vq.getCenterX();
        final int integer5 = vq.getCenterZ();
        for (int integer6 = 0; integer6 < 16; ++integer6) {
            for (int integer7 = 0; integer7 < 16; ++integer7) {
                final int integer8 = (integer4 << 4) + integer6;
                final int integer9 = (integer5 << 4) + integer7;
                vq.setBlock(a3.set(integer8, 60, integer9), DebugLevelSource.BARRIER, 2);
                final BlockState bvt10 = getBlockStateFor(integer8, integer9);
                if (bvt10 != null) {
                    vq.setBlock(a3.set(integer8, 70, integer9), bvt10, 2);
                }
            }
        }
    }
    
    @Override
    public void fillFromNoise(final LevelAccessor bhs, final ChunkAccess bxh) {
    }
    
    @Override
    public int getBaseHeight(final int integer1, final int integer2, final Heightmap.Types a) {
        return 0;
    }
    
    public static BlockState getBlockStateFor(int integer1, int integer2) {
        BlockState bvt3 = DebugLevelSource.AIR;
        if (integer1 > 0 && integer2 > 0 && integer1 % 2 != 0 && integer2 % 2 != 0) {
            integer1 /= 2;
            integer2 /= 2;
            if (integer1 <= DebugLevelSource.GRID_WIDTH && integer2 <= DebugLevelSource.GRID_HEIGHT) {
                final int integer3 = Mth.abs(integer1 * DebugLevelSource.GRID_WIDTH + integer2);
                if (integer3 < DebugLevelSource.ALL_BLOCKS.size()) {
                    bvt3 = (BlockState)DebugLevelSource.ALL_BLOCKS.get(integer3);
                }
            }
        }
        return bvt3;
    }
    
    static {
        ALL_BLOCKS = (List)StreamSupport.stream(Registry.BLOCK.spliterator(), false).flatMap(bmv -> bmv.getStateDefinition().getPossibleStates().stream()).collect(Collectors.toList());
        GRID_WIDTH = Mth.ceil(Mth.sqrt((float)DebugLevelSource.ALL_BLOCKS.size()));
        GRID_HEIGHT = Mth.ceil(DebugLevelSource.ALL_BLOCKS.size() / (float)DebugLevelSource.GRID_WIDTH);
        AIR = Blocks.AIR.defaultBlockState();
        BARRIER = Blocks.BARRIER.defaultBlockState();
    }
}
