package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import com.google.common.collect.Maps;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.item.DyeColor;
import java.util.Map;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class BannerBlock extends AbstractBannerBlock {
    public static final IntegerProperty ROTATION;
    private static final Map<DyeColor, Block> BY_COLOR;
    private static final VoxelShape SHAPE;
    
    public BannerBlock(final DyeColor bbg, final Properties c) {
        super(bbg, c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Integer>setValue((Property<Comparable>)BannerBlock.ROTATION, 0));
        BannerBlock.BY_COLOR.put(bbg, this);
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        return bhu.getBlockState(ew.below()).getMaterial().isSolid();
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return BannerBlock.SHAPE;
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        return ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Integer>setValue((Property<Comparable>)BannerBlock.ROTATION, Mth.floor((180.0f + ban.getRotation()) * 16.0f / 360.0f + 0.5) & 0xF);
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (fb == Direction.DOWN && !bvt1.canSurvive(bhs, ew5)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)BannerBlock.ROTATION, brg.rotate(bvt.<Integer>getValue((Property<Integer>)BannerBlock.ROTATION), 16));
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)BannerBlock.ROTATION, bqg.mirror(bvt.<Integer>getValue((Property<Integer>)BannerBlock.ROTATION), 16));
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(BannerBlock.ROTATION);
    }
    
    public static Block byColor(final DyeColor bbg) {
        return (Block)BannerBlock.BY_COLOR.getOrDefault(bbg, Blocks.WHITE_BANNER);
    }
    
    static {
        ROTATION = BlockStateProperties.ROTATION_16;
        BY_COLOR = (Map)Maps.newHashMap();
        SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
    }
}
