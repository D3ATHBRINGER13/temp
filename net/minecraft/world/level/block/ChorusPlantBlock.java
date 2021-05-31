package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockLayer;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;

public class ChorusPlantBlock extends PipeBlock {
    protected ChorusPlantBlock(final Properties c) {
        super(0.3125f, c);
        this.registerDefaultState((((((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)ChorusPlantBlock.NORTH, false)).setValue((Property<Comparable>)ChorusPlantBlock.EAST, false)).setValue((Property<Comparable>)ChorusPlantBlock.SOUTH, false)).setValue((Property<Comparable>)ChorusPlantBlock.WEST, false)).setValue((Property<Comparable>)ChorusPlantBlock.UP, false)).<Comparable, Boolean>setValue((Property<Comparable>)ChorusPlantBlock.DOWN, false));
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        return this.getStateForPlacement(ban.getLevel(), ban.getClickedPos());
    }
    
    public BlockState getStateForPlacement(final BlockGetter bhb, final BlockPos ew) {
        final Block bmv4 = bhb.getBlockState(ew.below()).getBlock();
        final Block bmv5 = bhb.getBlockState(ew.above()).getBlock();
        final Block bmv6 = bhb.getBlockState(ew.north()).getBlock();
        final Block bmv7 = bhb.getBlockState(ew.east()).getBlock();
        final Block bmv8 = bhb.getBlockState(ew.south()).getBlock();
        final Block bmv9 = bhb.getBlockState(ew.west()).getBlock();
        return (((((((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).setValue((Property<Comparable>)ChorusPlantBlock.DOWN, bmv4 == this || bmv4 == Blocks.CHORUS_FLOWER || bmv4 == Blocks.END_STONE)).setValue((Property<Comparable>)ChorusPlantBlock.UP, bmv5 == this || bmv5 == Blocks.CHORUS_FLOWER)).setValue((Property<Comparable>)ChorusPlantBlock.NORTH, bmv6 == this || bmv6 == Blocks.CHORUS_FLOWER)).setValue((Property<Comparable>)ChorusPlantBlock.EAST, bmv7 == this || bmv7 == Blocks.CHORUS_FLOWER)).setValue((Property<Comparable>)ChorusPlantBlock.SOUTH, bmv8 == this || bmv8 == Blocks.CHORUS_FLOWER)).<Comparable, Boolean>setValue((Property<Comparable>)ChorusPlantBlock.WEST, bmv9 == this || bmv9 == Blocks.CHORUS_FLOWER);
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (!bvt1.canSurvive(bhs, ew5)) {
            bhs.getBlockTicks().scheduleTick(ew5, this, 1);
            return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
        }
        final Block bmv8 = bvt3.getBlock();
        final boolean boolean9 = bmv8 == this || bmv8 == Blocks.CHORUS_FLOWER || (fb == Direction.DOWN && bmv8 == Blocks.END_STONE);
        return ((AbstractStateHolder<O, BlockState>)bvt1).<Comparable, Boolean>setValue((Property<Comparable>)ChorusPlantBlock.PROPERTY_BY_DIRECTION.get(fb), boolean9);
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (!bvt.canSurvive(bhr, ew)) {
            bhr.destroyBlock(ew, true);
        }
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        final BlockState bvt2 = bhu.getBlockState(ew.below());
        final boolean boolean6 = !bhu.getBlockState(ew.above()).isAir() && !bvt2.isAir();
        for (final Direction fb8 : Direction.Plane.HORIZONTAL) {
            final BlockPos ew2 = ew.relative(fb8);
            final Block bmv10 = bhu.getBlockState(ew2).getBlock();
            if (bmv10 == this) {
                if (boolean6) {
                    return false;
                }
                final Block bmv11 = bhu.getBlockState(ew2.below()).getBlock();
                if (bmv11 == this || bmv11 == Blocks.END_STONE) {
                    return true;
                }
                continue;
            }
        }
        final Block bmv12 = bvt2.getBlock();
        return bmv12 == this || bmv12 == Blocks.END_STONE;
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(ChorusPlantBlock.NORTH, ChorusPlantBlock.EAST, ChorusPlantBlock.SOUTH, ChorusPlantBlock.WEST, ChorusPlantBlock.UP, ChorusPlantBlock.DOWN);
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return false;
    }
}
