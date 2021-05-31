package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.LevelAccessor;
import javax.annotation.Nullable;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelReader;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class CocoaBlock extends HorizontalDirectionalBlock implements BonemealableBlock {
    public static final IntegerProperty AGE;
    protected static final VoxelShape[] EAST_AABB;
    protected static final VoxelShape[] WEST_AABB;
    protected static final VoxelShape[] NORTH_AABB;
    protected static final VoxelShape[] SOUTH_AABB;
    
    public CocoaBlock(final Properties c) {
        super(c);
        this.registerDefaultState((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)CocoaBlock.FACING, Direction.NORTH)).<Comparable, Integer>setValue((Property<Comparable>)CocoaBlock.AGE, 0));
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (bhr.random.nextInt(5) == 0) {
            final int integer6 = bvt.<Integer>getValue((Property<Integer>)CocoaBlock.AGE);
            if (integer6 < 2) {
                bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)CocoaBlock.AGE, integer6 + 1), 2);
            }
        }
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        final Block bmv5 = bhu.getBlockState(ew.relative(bvt.<Direction>getValue((Property<Direction>)CocoaBlock.FACING))).getBlock();
        return bmv5.is(BlockTags.JUNGLE_LOGS);
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        final int integer6 = bvt.<Integer>getValue((Property<Integer>)CocoaBlock.AGE);
        switch (bvt.<Direction>getValue((Property<Direction>)CocoaBlock.FACING)) {
            case SOUTH: {
                return CocoaBlock.SOUTH_AABB[integer6];
            }
            default: {
                return CocoaBlock.NORTH_AABB[integer6];
            }
            case WEST: {
                return CocoaBlock.WEST_AABB[integer6];
            }
            case EAST: {
                return CocoaBlock.EAST_AABB[integer6];
            }
        }
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        BlockState bvt3 = this.defaultBlockState();
        final LevelReader bhu4 = ban.getLevel();
        final BlockPos ew5 = ban.getClickedPos();
        for (final Direction fb9 : ban.getNearestLookingDirections()) {
            if (fb9.getAxis().isHorizontal()) {
                bvt3 = ((AbstractStateHolder<O, BlockState>)bvt3).<Comparable, Direction>setValue((Property<Comparable>)CocoaBlock.FACING, fb9);
                if (bvt3.canSurvive(bhu4, ew5)) {
                    return bvt3;
                }
            }
        }
        return null;
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (fb == bvt1.<Comparable>getValue((Property<Comparable>)CocoaBlock.FACING) && !bvt1.canSurvive(bhs, ew5)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public boolean isValidBonemealTarget(final BlockGetter bhb, final BlockPos ew, final BlockState bvt, final boolean boolean4) {
        return bvt.<Integer>getValue((Property<Integer>)CocoaBlock.AGE) < 2;
    }
    
    @Override
    public boolean isBonemealSuccess(final Level bhr, final Random random, final BlockPos ew, final BlockState bvt) {
        return true;
    }
    
    @Override
    public void performBonemeal(final Level bhr, final Random random, final BlockPos ew, final BlockState bvt) {
        bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)CocoaBlock.AGE, bvt.<Integer>getValue((Property<Integer>)CocoaBlock.AGE) + 1), 2);
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(CocoaBlock.FACING, CocoaBlock.AGE);
    }
    
    static {
        AGE = BlockStateProperties.AGE_2;
        EAST_AABB = new VoxelShape[] { Block.box(11.0, 7.0, 6.0, 15.0, 12.0, 10.0), Block.box(9.0, 5.0, 5.0, 15.0, 12.0, 11.0), Block.box(7.0, 3.0, 4.0, 15.0, 12.0, 12.0) };
        WEST_AABB = new VoxelShape[] { Block.box(1.0, 7.0, 6.0, 5.0, 12.0, 10.0), Block.box(1.0, 5.0, 5.0, 7.0, 12.0, 11.0), Block.box(1.0, 3.0, 4.0, 9.0, 12.0, 12.0) };
        NORTH_AABB = new VoxelShape[] { Block.box(6.0, 7.0, 1.0, 10.0, 12.0, 5.0), Block.box(5.0, 5.0, 1.0, 11.0, 12.0, 7.0), Block.box(4.0, 3.0, 1.0, 12.0, 12.0, 9.0) };
        SOUTH_AABB = new VoxelShape[] { Block.box(6.0, 7.0, 11.0, 10.0, 12.0, 15.0), Block.box(5.0, 5.0, 9.0, 11.0, 12.0, 15.0), Block.box(4.0, 3.0, 7.0, 12.0, 12.0, 15.0) };
    }
}
