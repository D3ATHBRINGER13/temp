package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.Direction;
import java.util.Map;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class WallSkullBlock extends AbstractSkullBlock {
    public static final DirectionProperty FACING;
    private static final Map<Direction, VoxelShape> AABBS;
    
    protected WallSkullBlock(final SkullBlock.Type a, final Properties c) {
        super(a, c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Direction>setValue((Property<Comparable>)WallSkullBlock.FACING, Direction.NORTH));
    }
    
    @Override
    public String getDescriptionId() {
        return this.asItem().getDescriptionId();
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return (VoxelShape)WallSkullBlock.AABBS.get(bvt.getValue((Property<Object>)WallSkullBlock.FACING));
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        BlockState bvt3 = this.defaultBlockState();
        final BlockGetter bhb4 = ban.getLevel();
        final BlockPos ew5 = ban.getClickedPos();
        final Direction[] nearestLookingDirections;
        final Direction[] arr6 = nearestLookingDirections = ban.getNearestLookingDirections();
        for (final Direction fb10 : nearestLookingDirections) {
            if (fb10.getAxis().isHorizontal()) {
                final Direction fb11 = fb10.getOpposite();
                bvt3 = ((AbstractStateHolder<O, BlockState>)bvt3).<Comparable, Direction>setValue((Property<Comparable>)WallSkullBlock.FACING, fb11);
                if (!bhb4.getBlockState(ew5.relative(fb10)).canBeReplaced(ban)) {
                    return bvt3;
                }
            }
        }
        return null;
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)WallSkullBlock.FACING, brg.rotate(bvt.<Direction>getValue((Property<Direction>)WallSkullBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        return bvt.rotate(bqg.getRotation(bvt.<Direction>getValue((Property<Direction>)WallSkullBlock.FACING)));
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(WallSkullBlock.FACING);
    }
    
    static {
        FACING = HorizontalDirectionalBlock.FACING;
        AABBS = (Map)Maps.newEnumMap((Map)ImmutableMap.of(Direction.NORTH, Block.box(4.0, 4.0, 8.0, 12.0, 12.0, 16.0), Direction.SOUTH, Block.box(4.0, 4.0, 0.0, 12.0, 12.0, 8.0), Direction.EAST, Block.box(0.0, 4.0, 4.0, 8.0, 12.0, 12.0), Direction.WEST, Block.box(8.0, 4.0, 4.0, 16.0, 12.0, 12.0)));
    }
}
