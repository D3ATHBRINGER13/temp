package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.util.Mth;
import net.minecraft.core.Vec3i;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class FrostedIceBlock extends IceBlock {
    public static final IntegerProperty AGE;
    
    public FrostedIceBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Integer>setValue((Property<Comparable>)FrostedIceBlock.AGE, 0));
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if ((random.nextInt(3) == 0 || this.fewerNeigboursThan(bhr, ew, 4)) && bhr.getMaxLocalRawBrightness(ew) > 11 - bvt.<Integer>getValue((Property<Integer>)FrostedIceBlock.AGE) - bvt.getLightBlock(bhr, ew) && this.slightlyMelt(bvt, bhr, ew)) {
            try (final BlockPos.PooledMutableBlockPos b6 = BlockPos.PooledMutableBlockPos.acquire()) {
                for (final Direction fb11 : Direction.values()) {
                    b6.set(ew).move(fb11);
                    final BlockState bvt2 = bhr.getBlockState(b6);
                    if (bvt2.getBlock() == this && !this.slightlyMelt(bvt2, bhr, b6)) {
                        bhr.getBlockTicks().scheduleTick(b6, this, Mth.nextInt(random, 20, 40));
                    }
                }
            }
            return;
        }
        bhr.getBlockTicks().scheduleTick(ew, this, Mth.nextInt(random, 20, 40));
    }
    
    private boolean slightlyMelt(final BlockState bvt, final Level bhr, final BlockPos ew) {
        final int integer5 = bvt.<Integer>getValue((Property<Integer>)FrostedIceBlock.AGE);
        if (integer5 < 3) {
            bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)FrostedIceBlock.AGE, integer5 + 1), 2);
            return false;
        }
        this.melt(bvt, bhr, ew);
        return true;
    }
    
    @Override
    public void neighborChanged(final BlockState bvt, final Level bhr, final BlockPos ew3, final Block bmv, final BlockPos ew5, final boolean boolean6) {
        if (bmv == this && this.fewerNeigboursThan(bhr, ew3, 2)) {
            this.melt(bvt, bhr, ew3);
        }
        super.neighborChanged(bvt, bhr, ew3, bmv, ew5, boolean6);
    }
    
    private boolean fewerNeigboursThan(final BlockGetter bhb, final BlockPos ew, final int integer) {
        int integer2 = 0;
        try (final BlockPos.PooledMutableBlockPos b6 = BlockPos.PooledMutableBlockPos.acquire()) {
            for (final Direction fb11 : Direction.values()) {
                b6.set(ew).move(fb11);
                if (bhb.getBlockState(b6).getBlock() == this && ++integer2 >= integer) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(FrostedIceBlock.AGE);
    }
    
    @Override
    public ItemStack getCloneItemStack(final BlockGetter bhb, final BlockPos ew, final BlockState bvt) {
        return ItemStack.EMPTY;
    }
    
    static {
        AGE = BlockStateProperties.AGE_3;
    }
}
