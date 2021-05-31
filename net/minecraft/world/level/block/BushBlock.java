package net.minecraft.world.level.block;

import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class BushBlock extends Block {
    protected BushBlock(final Properties c) {
        super(c);
    }
    
    protected boolean mayPlaceOn(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        final Block bmv5 = bvt.getBlock();
        return bmv5 == Blocks.GRASS_BLOCK || bmv5 == Blocks.DIRT || bmv5 == Blocks.COARSE_DIRT || bmv5 == Blocks.PODZOL || bmv5 == Blocks.FARMLAND;
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (!bvt1.canSurvive(bhs, ew5)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        final BlockPos ew2 = ew.below();
        return this.mayPlaceOn(bhu.getBlockState(ew2), bhu, ew2);
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    @Override
    public boolean propagatesSkylightDown(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return true;
    }
}
