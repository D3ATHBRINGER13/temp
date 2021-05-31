package net.minecraft.world.level.block;

import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.HugeMushroomFeatureConfig;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.LevelAccessor;
import java.util.Iterator;
import net.minecraft.world.level.LevelReader;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MushroomBlock extends BushBlock implements BonemealableBlock {
    protected static final VoxelShape SHAPE;
    
    public MushroomBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return MushroomBlock.SHAPE;
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, BlockPos ew, final Random random) {
        if (random.nextInt(25) == 0) {
            int integer6 = 5;
            final int integer7 = 4;
            for (final BlockPos ew2 : BlockPos.betweenClosed(ew.offset(-4, -1, -4), ew.offset(4, 1, 4))) {
                if (bhr.getBlockState(ew2).getBlock() == this && --integer6 <= 0) {
                    return;
                }
            }
            BlockPos ew3 = ew.offset(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
            for (int integer8 = 0; integer8 < 4; ++integer8) {
                if (bhr.isEmptyBlock(ew3) && bvt.canSurvive(bhr, ew3)) {
                    ew = ew3;
                }
                ew3 = ew.offset(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
            }
            if (bhr.isEmptyBlock(ew3) && bvt.canSurvive(bhr, ew3)) {
                bhr.setBlock(ew3, bvt, 2);
            }
        }
    }
    
    @Override
    protected boolean mayPlaceOn(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return bvt.isSolidRender(bhb, ew);
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        final BlockPos ew2 = ew.below();
        final BlockState bvt2 = bhu.getBlockState(ew2);
        final Block bmv7 = bvt2.getBlock();
        return bmv7 == Blocks.MYCELIUM || bmv7 == Blocks.PODZOL || (bhu.getRawBrightness(ew, 0) < 13 && this.mayPlaceOn(bvt2, bhu, ew2));
    }
    
    public boolean growMushroom(final LevelAccessor bhs, final BlockPos ew, final BlockState bvt, final Random random) {
        bhs.removeBlock(ew, false);
        Feature<HugeMushroomFeatureConfig> cbn6 = null;
        if (this == Blocks.BROWN_MUSHROOM) {
            cbn6 = Feature.HUGE_BROWN_MUSHROOM;
        }
        else if (this == Blocks.RED_MUSHROOM) {
            cbn6 = Feature.HUGE_RED_MUSHROOM;
        }
        if (cbn6 != null && cbn6.place(bhs, bhs.getChunkSource().getGenerator(), random, ew, new HugeMushroomFeatureConfig(true))) {
            return true;
        }
        bhs.setBlock(ew, bvt, 3);
        return false;
    }
    
    @Override
    public boolean isValidBonemealTarget(final BlockGetter bhb, final BlockPos ew, final BlockState bvt, final boolean boolean4) {
        return true;
    }
    
    @Override
    public boolean isBonemealSuccess(final Level bhr, final Random random, final BlockPos ew, final BlockState bvt) {
        return random.nextFloat() < 0.4;
    }
    
    @Override
    public void performBonemeal(final Level bhr, final Random random, final BlockPos ew, final BlockState bvt) {
        this.growMushroom(bhr, ew, bvt, random);
    }
    
    @Override
    public boolean hasPostProcess(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return true;
    }
    
    static {
        SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);
    }
}
