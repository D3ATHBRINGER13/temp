package net.minecraft.world.level.block;

import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BaseCoralPlantBlock extends BaseCoralPlantTypeBlock {
    protected static final VoxelShape SHAPE;
    
    protected BaseCoralPlantBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return BaseCoralPlantBlock.SHAPE;
    }
    
    static {
        SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 15.0, 14.0);
    }
}
