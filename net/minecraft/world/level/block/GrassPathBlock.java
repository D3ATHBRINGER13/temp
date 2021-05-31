package net.minecraft.world.level.block;

import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.BlockGetter;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GrassPathBlock extends Block {
    protected static final VoxelShape SHAPE;
    
    protected GrassPathBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public boolean useShapeForLightOcclusion(final BlockState bvt) {
        return true;
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        if (!this.defaultBlockState().canSurvive(ban.getLevel(), ban.getClickedPos())) {
            return Block.pushEntitiesUp(this.defaultBlockState(), Blocks.DIRT.defaultBlockState(), ban.getLevel(), ban.getClickedPos());
        }
        return super.getStateForPlacement(ban);
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (fb == Direction.UP && !bvt1.canSurvive(bhs, ew5)) {
            bhs.getBlockTicks().scheduleTick(ew5, this, 1);
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        FarmBlock.turnToDirt(bvt, bhr, ew);
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        final BlockState bvt2 = bhu.getBlockState(ew.above());
        return !bvt2.getMaterial().isSolid() || bvt2.getBlock() instanceof FenceGateBlock;
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return GrassPathBlock.SHAPE;
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return false;
    }
    
    static {
        SHAPE = FarmBlock.SHAPE;
    }
}
