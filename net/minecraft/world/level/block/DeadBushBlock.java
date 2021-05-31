package net.minecraft.world.level.block;

import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DeadBushBlock extends BushBlock {
    protected static final VoxelShape SHAPE;
    
    protected DeadBushBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return DeadBushBlock.SHAPE;
    }
    
    @Override
    protected boolean mayPlaceOn(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        final Block bmv5 = bvt.getBlock();
        return bmv5 == Blocks.SAND || bmv5 == Blocks.RED_SAND || bmv5 == Blocks.TERRACOTTA || bmv5 == Blocks.WHITE_TERRACOTTA || bmv5 == Blocks.ORANGE_TERRACOTTA || bmv5 == Blocks.MAGENTA_TERRACOTTA || bmv5 == Blocks.LIGHT_BLUE_TERRACOTTA || bmv5 == Blocks.YELLOW_TERRACOTTA || bmv5 == Blocks.LIME_TERRACOTTA || bmv5 == Blocks.PINK_TERRACOTTA || bmv5 == Blocks.GRAY_TERRACOTTA || bmv5 == Blocks.LIGHT_GRAY_TERRACOTTA || bmv5 == Blocks.CYAN_TERRACOTTA || bmv5 == Blocks.PURPLE_TERRACOTTA || bmv5 == Blocks.BLUE_TERRACOTTA || bmv5 == Blocks.BROWN_TERRACOTTA || bmv5 == Blocks.GREEN_TERRACOTTA || bmv5 == Blocks.RED_TERRACOTTA || bmv5 == Blocks.BLACK_TERRACOTTA || bmv5 == Blocks.DIRT || bmv5 == Blocks.COARSE_DIRT || bmv5 == Blocks.PODZOL;
    }
    
    static {
        SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);
    }
}
