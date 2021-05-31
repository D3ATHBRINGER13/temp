package net.minecraft.world.level.block;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class AirBlock extends Block {
    protected AirBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public RenderShape getRenderShape(final BlockState bvt) {
        return RenderShape.INVISIBLE;
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return Shapes.empty();
    }
    
    @Override
    public boolean isAir(final BlockState bvt) {
        return true;
    }
}
