package net.minecraft.world.level.block;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TallGrassBlock extends BushBlock implements BonemealableBlock {
    protected static final VoxelShape SHAPE;
    
    protected TallGrassBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return TallGrassBlock.SHAPE;
    }
    
    @Override
    public boolean isValidBonemealTarget(final BlockGetter bhb, final BlockPos ew, final BlockState bvt, final boolean boolean4) {
        return true;
    }
    
    @Override
    public boolean isBonemealSuccess(final Level bhr, final Random random, final BlockPos ew, final BlockState bvt) {
        return true;
    }
    
    @Override
    public void performBonemeal(final Level bhr, final Random random, final BlockPos ew, final BlockState bvt) {
        final DoublePlantBlock boh6 = (DoublePlantBlock)((this == Blocks.FERN) ? Blocks.LARGE_FERN : Blocks.TALL_GRASS);
        if (boh6.defaultBlockState().canSurvive(bhr, ew) && bhr.isEmptyBlock(ew.above())) {
            boh6.placeAt(bhr, ew, 2);
        }
    }
    
    @Override
    public OffsetType getOffsetType() {
        return OffsetType.XYZ;
    }
    
    static {
        SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);
    }
}
