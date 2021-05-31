package net.minecraft.world.level.block;

import net.minecraft.world.level.BlockLayer;
import java.util.List;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.DecoratedFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.FlowerFeature;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

public class GrassBlock extends SpreadingSnowyDirtBlock implements BonemealableBlock {
    public GrassBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public boolean isValidBonemealTarget(final BlockGetter bhb, final BlockPos ew, final BlockState bvt, final boolean boolean4) {
        return bhb.getBlockState(ew.above()).isAir();
    }
    
    @Override
    public boolean isBonemealSuccess(final Level bhr, final Random random, final BlockPos ew, final BlockState bvt) {
        return true;
    }
    
    @Override
    public void performBonemeal(final Level bhr, final Random random, final BlockPos ew, final BlockState bvt) {
        final BlockPos ew2 = ew.above();
        final BlockState bvt2 = Blocks.GRASS.defaultBlockState();
        int integer8 = 0;
    Label_0273_Outer:
        while (integer8 < 128) {
            BlockPos ew3 = ew2;
            int integer9 = 0;
            while (true) {
                while (integer9 < integer8 / 16) {
                    ew3 = ew3.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                    if (bhr.getBlockState(ew3.below()).getBlock() == this) {
                        if (!bhr.getBlockState(ew3).isCollisionShapeFullBlock(bhr, ew3)) {
                            ++integer9;
                            continue Label_0273_Outer;
                        }
                    }
                    ++integer8;
                    continue Label_0273_Outer;
                }
                final BlockState bvt3 = bhr.getBlockState(ew3);
                if (bvt3.getBlock() == bvt2.getBlock() && random.nextInt(10) == 0) {
                    ((BonemealableBlock)bvt2.getBlock()).performBonemeal(bhr, random, ew3, bvt3);
                }
                if (!bvt3.isAir()) {
                    continue;
                }
                BlockState bvt4;
                if (random.nextInt(8) == 0) {
                    final List<ConfiguredFeature<?>> list12 = bhr.getBiome(ew3).getFlowerFeatures();
                    if (list12.isEmpty()) {
                        continue;
                    }
                    bvt4 = ((FlowerFeature)((DecoratedFeatureConfiguration)((ConfiguredFeature)list12.get(0)).config).feature.feature).getRandomFlower(random, ew3);
                }
                else {
                    bvt4 = bvt2;
                }
                if (bvt4.canSurvive(bhr, ew3)) {
                    bhr.setBlock(ew3, bvt4, 3);
                }
                continue;
            }
        }
    }
    
    public boolean canOcclude(final BlockState bvt) {
        return true;
    }
    
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT_MIPPED;
    }
}
