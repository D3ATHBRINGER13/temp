package net.minecraft.world.level.block;

import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StructureVoidBlock extends Block {
    private static final VoxelShape SHAPE;
    
    protected StructureVoidBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public RenderShape getRenderShape(final BlockState bvt) {
        return RenderShape.INVISIBLE;
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return StructureVoidBlock.SHAPE;
    }
    
    @Override
    public float getShadeBrightness(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return 1.0f;
    }
    
    @Override
    public PushReaction getPistonPushReaction(final BlockState bvt) {
        return PushReaction.DESTROY;
    }
    
    static {
        SHAPE = Block.box(5.0, 5.0, 5.0, 11.0, 11.0, 11.0);
    }
}
