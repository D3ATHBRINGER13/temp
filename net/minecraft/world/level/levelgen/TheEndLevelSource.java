package net.minecraft.world.level.levelgen;

import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;

public class TheEndLevelSource extends NoiseBasedChunkGenerator<TheEndGeneratorSettings> {
    private final BlockPos dimensionSpawnPosition;
    
    public TheEndLevelSource(final LevelAccessor bhs, final BiomeSource biq, final TheEndGeneratorSettings bzi) {
        super(bhs, biq, 8, 4, 128, bzi, true);
        this.dimensionSpawnPosition = bzi.getSpawnPosition();
    }
    
    @Override
    protected void fillNoiseColumn(final double[] arr, final int integer2, final int integer3) {
        final double double5 = 1368.824;
        final double double6 = 684.412;
        final double double7 = 17.110300000000002;
        final double double8 = 4.277575000000001;
        final int integer4 = 64;
        final int integer5 = -3000;
        this.fillNoiseColumn(arr, integer2, integer3, 1368.824, 684.412, 17.110300000000002, 4.277575000000001, 64, -3000);
    }
    
    @Override
    protected double[] getDepthAndScale(final int integer1, final int integer2) {
        return new double[] { this.biomeSource.getHeightValue(integer1, integer2), 0.0 };
    }
    
    @Override
    protected double getYOffset(final double double1, final double double2, final int integer) {
        return 8.0 - double1;
    }
    
    @Override
    protected double getTopSlideStart() {
        return (int)super.getTopSlideStart() / 2;
    }
    
    @Override
    protected double getBottomSlideStart() {
        return 8.0;
    }
    
    @Override
    public int getSpawnHeight() {
        return 50;
    }
    
    @Override
    public int getSeaLevel() {
        return 0;
    }
}
