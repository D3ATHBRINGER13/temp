package net.minecraft.world.level.levelgen;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.biome.Biome;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.Level;

public class NetherLevelSource extends NoiseBasedChunkGenerator<NetherGeneratorSettings> {
    private final double[] yOffsets;
    
    public NetherLevelSource(final Level bhr, final BiomeSource biq, final NetherGeneratorSettings bzb) {
        super(bhr, biq, 4, 8, 128, bzb, false);
        this.yOffsets = this.makeYOffsets();
    }
    
    @Override
    protected void fillNoiseColumn(final double[] arr, final int integer2, final int integer3) {
        final double double5 = 684.412;
        final double double6 = 2053.236;
        final double double7 = 8.555150000000001;
        final double double8 = 34.2206;
        final int integer4 = -10;
        final int integer5 = 3;
        this.fillNoiseColumn(arr, integer2, integer3, 684.412, 2053.236, 8.555150000000001, 34.2206, 3, -10);
    }
    
    @Override
    protected double[] getDepthAndScale(final int integer1, final int integer2) {
        return new double[] { 0.0, 0.0 };
    }
    
    @Override
    protected double getYOffset(final double double1, final double double2, final int integer) {
        return this.yOffsets[integer];
    }
    
    private double[] makeYOffsets() {
        final double[] arr2 = new double[this.getNoiseSizeY()];
        for (int integer3 = 0; integer3 < this.getNoiseSizeY(); ++integer3) {
            arr2[integer3] = Math.cos(integer3 * 3.141592653589793 * 6.0 / this.getNoiseSizeY()) * 2.0;
            double double4 = integer3;
            if (integer3 > this.getNoiseSizeY() / 2) {
                double4 = this.getNoiseSizeY() - 1 - integer3;
            }
            if (double4 < 4.0) {
                double4 = 4.0 - double4;
                final double[] array = arr2;
                final int n = integer3;
                array[n] -= double4 * double4 * double4 * 10.0;
            }
        }
        return arr2;
    }
    
    @Override
    public List<Biome.SpawnerData> getMobsAt(final MobCategory aiz, final BlockPos ew) {
        if (aiz == MobCategory.MONSTER) {
            if (Feature.NETHER_BRIDGE.isInsideFeature(this.level, ew)) {
                return Feature.NETHER_BRIDGE.getSpecialEnemies();
            }
            if (Feature.NETHER_BRIDGE.isInsideBoundingFeature(this.level, ew) && this.level.getBlockState(ew.below()).getBlock() == Blocks.NETHER_BRICKS) {
                return Feature.NETHER_BRIDGE.getSpecialEnemies();
            }
        }
        return super.getMobsAt(aiz, ew);
    }
    
    @Override
    public int getSpawnHeight() {
        return this.level.getSeaLevel() + 1;
    }
    
    @Override
    public int getGenDepth() {
        return 128;
    }
    
    @Override
    public int getSeaLevel() {
        return 32;
    }
}
