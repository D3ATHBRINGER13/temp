package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.StateDefinition;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class RedstoneLampBlock extends Block {
    public static final BooleanProperty LIT;
    
    public RedstoneLampBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)RedstoneLampBlock.LIT, false));
    }
    
    @Override
    public int getLightEmission(final BlockState bvt) {
        return bvt.<Boolean>getValue((Property<Boolean>)RedstoneLampBlock.LIT) ? super.getLightEmission(bvt) : 0;
    }
    
    @Override
    public void onPlace(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        super.onPlace(bvt1, bhr, ew, bvt4, boolean5);
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        return ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)RedstoneLampBlock.LIT, ban.getLevel().hasNeighborSignal(ban.getClickedPos()));
    }
    
    @Override
    public void neighborChanged(final BlockState bvt, final Level bhr, final BlockPos ew3, final Block bmv, final BlockPos ew5, final boolean boolean6) {
        if (bhr.isClientSide) {
            return;
        }
        final boolean boolean7 = bvt.<Boolean>getValue((Property<Boolean>)RedstoneLampBlock.LIT);
        if (boolean7 != bhr.hasNeighborSignal(ew3)) {
            if (boolean7) {
                bhr.getBlockTicks().scheduleTick(ew3, this, 4);
            }
            else {
                bhr.setBlock(ew3, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable>cycle((Property<Comparable>)RedstoneLampBlock.LIT), 2);
            }
        }
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (bhr.isClientSide) {
            return;
        }
        if (bvt.<Boolean>getValue((Property<Boolean>)RedstoneLampBlock.LIT) && !bhr.hasNeighborSignal(ew)) {
            bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable>cycle((Property<Comparable>)RedstoneLampBlock.LIT), 2);
        }
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(RedstoneLampBlock.LIT);
    }
    
    static {
        LIT = RedstoneTorchBlock.LIT;
    }
}
