package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import javax.annotation.Nullable;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class Lantern extends Block {
    public static final BooleanProperty HANGING;
    protected static final VoxelShape AABB;
    protected static final VoxelShape HANGING_AABB;
    
    public Lantern(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Boolean>setValue((Property<Comparable>)Lantern.HANGING, false));
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        for (final Direction fb6 : ban.getNearestLookingDirections()) {
            if (fb6.getAxis() == Direction.Axis.Y) {
                final BlockState bvt7 = ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)Lantern.HANGING, fb6 == Direction.UP);
                if (bvt7.canSurvive(ban.getLevel(), ban.getClickedPos())) {
                    return bvt7;
                }
            }
        }
        return null;
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return bvt.<Boolean>getValue((Property<Boolean>)Lantern.HANGING) ? Lantern.HANGING_AABB : Lantern.AABB;
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(Lantern.HANGING);
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        final Direction fb5 = getConnectedDirection(bvt).getOpposite();
        return Block.canSupportCenter(bhu, ew.relative(fb5), fb5.getOpposite());
    }
    
    protected static Direction getConnectedDirection(final BlockState bvt) {
        return bvt.<Boolean>getValue((Property<Boolean>)Lantern.HANGING) ? Direction.DOWN : Direction.UP;
    }
    
    @Override
    public PushReaction getPistonPushReaction(final BlockState bvt) {
        return PushReaction.DESTROY;
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (getConnectedDirection(bvt1).getOpposite() == fb && !bvt1.canSurvive(bhs, ew5)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return false;
    }
    
    static {
        HANGING = BlockStateProperties.HANGING;
        AABB = Shapes.or(Block.box(5.0, 0.0, 5.0, 11.0, 7.0, 11.0), Block.box(6.0, 7.0, 6.0, 10.0, 9.0, 10.0));
        HANGING_AABB = Shapes.or(Block.box(5.0, 1.0, 5.0, 11.0, 8.0, 11.0), Block.box(6.0, 8.0, 6.0, 10.0, 10.0, 10.0));
    }
}
