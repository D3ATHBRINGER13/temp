package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import javax.annotation.Nullable;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.Direction;
import java.util.Map;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class BaseCoralWallFanBlock extends BaseCoralFanBlock {
    public static final DirectionProperty FACING;
    private static final Map<Direction, VoxelShape> SHAPES;
    
    protected BaseCoralWallFanBlock(final Properties c) {
        super(c);
        this.registerDefaultState((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)BaseCoralWallFanBlock.FACING, Direction.NORTH)).<Comparable, Boolean>setValue((Property<Comparable>)BaseCoralWallFanBlock.WATERLOGGED, true));
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return (VoxelShape)BaseCoralWallFanBlock.SHAPES.get(bvt.getValue((Property<Object>)BaseCoralWallFanBlock.FACING));
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)BaseCoralWallFanBlock.FACING, brg.rotate(bvt.<Direction>getValue((Property<Direction>)BaseCoralWallFanBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        return bvt.rotate(bqg.getRotation(bvt.<Direction>getValue((Property<Direction>)BaseCoralWallFanBlock.FACING)));
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(BaseCoralWallFanBlock.FACING, BaseCoralWallFanBlock.WATERLOGGED);
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (bvt1.<Boolean>getValue((Property<Boolean>)BaseCoralWallFanBlock.WATERLOGGED)) {
            bhs.getLiquidTicks().scheduleTick(ew5, Fluids.WATER, Fluids.WATER.getTickDelay(bhs));
        }
        if (fb.getOpposite() == bvt1.<Comparable>getValue((Property<Comparable>)BaseCoralWallFanBlock.FACING) && !bvt1.canSurvive(bhs, ew5)) {
            return Blocks.AIR.defaultBlockState();
        }
        return bvt1;
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        final Direction fb5 = bvt.<Direction>getValue((Property<Direction>)BaseCoralWallFanBlock.FACING);
        final BlockPos ew2 = ew.relative(fb5.getOpposite());
        final BlockState bvt2 = bhu.getBlockState(ew2);
        return bvt2.isFaceSturdy(bhu, ew2, fb5);
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        BlockState bvt3 = super.getStateForPlacement(ban);
        final LevelReader bhu4 = ban.getLevel();
        final BlockPos ew5 = ban.getClickedPos();
        final Direction[] nearestLookingDirections;
        final Direction[] arr6 = nearestLookingDirections = ban.getNearestLookingDirections();
        for (final Direction fb10 : nearestLookingDirections) {
            if (fb10.getAxis().isHorizontal()) {
                bvt3 = ((AbstractStateHolder<O, BlockState>)bvt3).<Comparable, Direction>setValue((Property<Comparable>)BaseCoralWallFanBlock.FACING, fb10.getOpposite());
                if (bvt3.canSurvive(bhu4, ew5)) {
                    return bvt3;
                }
            }
        }
        return null;
    }
    
    static {
        FACING = HorizontalDirectionalBlock.FACING;
        SHAPES = (Map)Maps.newEnumMap((Map)ImmutableMap.of(Direction.NORTH, Block.box(0.0, 4.0, 5.0, 16.0, 12.0, 16.0), Direction.SOUTH, Block.box(0.0, 4.0, 0.0, 16.0, 12.0, 11.0), Direction.WEST, Block.box(5.0, 4.0, 0.0, 16.0, 12.0, 16.0), Direction.EAST, Block.box(0.0, 4.0, 0.0, 11.0, 12.0, 16.0)));
    }
}
