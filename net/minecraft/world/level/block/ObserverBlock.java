package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class ObserverBlock extends DirectionalBlock {
    public static final BooleanProperty POWERED;
    
    public ObserverBlock(final Properties c) {
        super(c);
        this.registerDefaultState((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)ObserverBlock.FACING, Direction.SOUTH)).<Comparable, Boolean>setValue((Property<Comparable>)ObserverBlock.POWERED, false));
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(ObserverBlock.FACING, ObserverBlock.POWERED);
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)ObserverBlock.FACING, brg.rotate(bvt.<Direction>getValue((Property<Direction>)ObserverBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        return bvt.rotate(bqg.getRotation(bvt.<Direction>getValue((Property<Direction>)ObserverBlock.FACING)));
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (bvt.<Boolean>getValue((Property<Boolean>)ObserverBlock.POWERED)) {
            bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)ObserverBlock.POWERED, false), 2);
        }
        else {
            bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)ObserverBlock.POWERED, true), 2);
            bhr.getBlockTicks().scheduleTick(ew, this, 2);
        }
        this.updateNeighborsInFront(bhr, ew, bvt);
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (bvt1.<Comparable>getValue((Property<Comparable>)ObserverBlock.FACING) == fb && !bvt1.<Boolean>getValue((Property<Boolean>)ObserverBlock.POWERED)) {
            this.startSignal(bhs, ew5);
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    private void startSignal(final LevelAccessor bhs, final BlockPos ew) {
        if (!bhs.isClientSide() && !bhs.getBlockTicks().hasScheduledTick(ew, this)) {
            bhs.getBlockTicks().scheduleTick(ew, this, 2);
        }
    }
    
    protected void updateNeighborsInFront(final Level bhr, final BlockPos ew, final BlockState bvt) {
        final Direction fb5 = bvt.<Direction>getValue((Property<Direction>)ObserverBlock.FACING);
        final BlockPos ew2 = ew.relative(fb5.getOpposite());
        bhr.neighborChanged(ew2, this, ew);
        bhr.updateNeighborsAtExceptFromFacing(ew2, this, fb5);
    }
    
    @Override
    public boolean isSignalSource(final BlockState bvt) {
        return true;
    }
    
    @Override
    public int getDirectSignal(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        return bvt.getSignal(bhb, ew, fb);
    }
    
    @Override
    public int getSignal(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        if (bvt.<Boolean>getValue((Property<Boolean>)ObserverBlock.POWERED) && bvt.<Comparable>getValue((Property<Comparable>)ObserverBlock.FACING) == fb) {
            return 15;
        }
        return 0;
    }
    
    @Override
    public void onPlace(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (bvt1.getBlock() == bvt4.getBlock()) {
            return;
        }
        if (!bhr.isClientSide() && bvt1.<Boolean>getValue((Property<Boolean>)ObserverBlock.POWERED) && !bhr.getBlockTicks().hasScheduledTick(ew, this)) {
            final BlockState bvt5 = ((AbstractStateHolder<O, BlockState>)bvt1).<Comparable, Boolean>setValue((Property<Comparable>)ObserverBlock.POWERED, false);
            bhr.setBlock(ew, bvt5, 18);
            this.updateNeighborsInFront(bhr, ew, bvt5);
        }
    }
    
    @Override
    public void onRemove(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (bvt1.getBlock() == bvt4.getBlock()) {
            return;
        }
        if (!bhr.isClientSide && bvt1.<Boolean>getValue((Property<Boolean>)ObserverBlock.POWERED) && bhr.getBlockTicks().hasScheduledTick(ew, this)) {
            this.updateNeighborsInFront(bhr, ew, ((AbstractStateHolder<O, BlockState>)bvt1).<Comparable, Boolean>setValue((Property<Comparable>)ObserverBlock.POWERED, false));
        }
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        return ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)ObserverBlock.FACING, ban.getNearestLookingDirection().getOpposite().getOpposite());
    }
    
    static {
        POWERED = BlockStateProperties.POWERED;
    }
}
