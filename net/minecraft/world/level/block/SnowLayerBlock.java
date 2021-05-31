package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import javax.annotation.Nullable;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.LightLayer;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class SnowLayerBlock extends Block {
    public static final IntegerProperty LAYERS;
    protected static final VoxelShape[] SHAPE_BY_LAYER;
    
    protected SnowLayerBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Integer>setValue((Property<Comparable>)SnowLayerBlock.LAYERS, 1));
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        switch (cns) {
            case LAND: {
                return bvt.<Integer>getValue((Property<Integer>)SnowLayerBlock.LAYERS) < 5;
            }
            case WATER: {
                return false;
            }
            case AIR: {
                return false;
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return SnowLayerBlock.SHAPE_BY_LAYER[bvt.<Integer>getValue((Property<Integer>)SnowLayerBlock.LAYERS)];
    }
    
    @Override
    public VoxelShape getCollisionShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return SnowLayerBlock.SHAPE_BY_LAYER[bvt.<Integer>getValue((Property<Integer>)SnowLayerBlock.LAYERS) - 1];
    }
    
    @Override
    public boolean useShapeForLightOcclusion(final BlockState bvt) {
        return true;
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        final BlockState bvt2 = bhu.getBlockState(ew.below());
        final Block bmv6 = bvt2.getBlock();
        return bmv6 != Blocks.ICE && bmv6 != Blocks.PACKED_ICE && bmv6 != Blocks.BARRIER && (Block.isFaceFull(bvt2.getCollisionShape(bhu, ew.below()), Direction.UP) || (bmv6 == this && bvt2.<Integer>getValue((Property<Integer>)SnowLayerBlock.LAYERS) == 8));
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (!bvt1.canSurvive(bhs, ew5)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (bhr.getBrightness(LightLayer.BLOCK, ew) > 11) {
            Block.dropResources(bvt, bhr, ew);
            bhr.removeBlock(ew, false);
        }
    }
    
    @Override
    public boolean canBeReplaced(final BlockState bvt, final BlockPlaceContext ban) {
        final int integer4 = bvt.<Integer>getValue((Property<Integer>)SnowLayerBlock.LAYERS);
        if (ban.getItemInHand().getItem() == this.asItem() && integer4 < 8) {
            return !ban.replacingClickedOnBlock() || ban.getClickedFace() == Direction.UP;
        }
        return integer4 == 1;
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final BlockState bvt3 = ban.getLevel().getBlockState(ban.getClickedPos());
        if (bvt3.getBlock() == this) {
            final int integer4 = bvt3.<Integer>getValue((Property<Integer>)SnowLayerBlock.LAYERS);
            return ((AbstractStateHolder<O, BlockState>)bvt3).<Comparable, Integer>setValue((Property<Comparable>)SnowLayerBlock.LAYERS, Math.min(8, integer4 + 1));
        }
        return super.getStateForPlacement(ban);
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(SnowLayerBlock.LAYERS);
    }
    
    static {
        LAYERS = BlockStateProperties.LAYERS;
        SHAPE_BY_LAYER = new VoxelShape[] { Shapes.empty(), Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0) };
    }
}
