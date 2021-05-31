package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.Direction;
import java.util.Map;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class WallBannerBlock extends AbstractBannerBlock {
    public static final DirectionProperty FACING;
    private static final Map<Direction, VoxelShape> SHAPES;
    
    public WallBannerBlock(final DyeColor bbg, final Properties c) {
        super(bbg, c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Direction>setValue((Property<Comparable>)WallBannerBlock.FACING, Direction.NORTH));
    }
    
    @Override
    public String getDescriptionId() {
        return this.asItem().getDescriptionId();
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        return bhu.getBlockState(ew.relative(bvt.<Direction>getValue((Property<Direction>)WallBannerBlock.FACING).getOpposite())).getMaterial().isSolid();
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (fb == bvt1.<Direction>getValue((Property<Direction>)WallBannerBlock.FACING).getOpposite() && !bvt1.canSurvive(bhs, ew5)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return (VoxelShape)WallBannerBlock.SHAPES.get(bvt.getValue((Property<Object>)WallBannerBlock.FACING));
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        BlockState bvt3 = this.defaultBlockState();
        final LevelReader bhu4 = ban.getLevel();
        final BlockPos ew5 = ban.getClickedPos();
        final Direction[] nearestLookingDirections;
        final Direction[] arr6 = nearestLookingDirections = ban.getNearestLookingDirections();
        for (final Direction fb10 : nearestLookingDirections) {
            if (fb10.getAxis().isHorizontal()) {
                final Direction fb11 = fb10.getOpposite();
                bvt3 = ((AbstractStateHolder<O, BlockState>)bvt3).<Comparable, Direction>setValue((Property<Comparable>)WallBannerBlock.FACING, fb11);
                if (bvt3.canSurvive(bhu4, ew5)) {
                    return bvt3;
                }
            }
        }
        return null;
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)WallBannerBlock.FACING, brg.rotate(bvt.<Direction>getValue((Property<Direction>)WallBannerBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        return bvt.rotate(bqg.getRotation(bvt.<Direction>getValue((Property<Direction>)WallBannerBlock.FACING)));
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(WallBannerBlock.FACING);
    }
    
    static {
        FACING = HorizontalDirectionalBlock.FACING;
        SHAPES = (Map)Maps.newEnumMap((Map)ImmutableMap.of(Direction.NORTH, Block.box(0.0, 0.0, 14.0, 16.0, 12.5, 16.0), Direction.SOUTH, Block.box(0.0, 0.0, 0.0, 16.0, 12.5, 2.0), Direction.WEST, Block.box(14.0, 0.0, 0.0, 16.0, 12.5, 16.0), Direction.EAST, Block.box(0.0, 0.0, 0.0, 2.0, 12.5, 16.0)));
    }
}
