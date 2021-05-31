package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import java.util.Random;
import net.minecraft.world.level.TickPriority;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ComparatorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ComparatorMode;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class ComparatorBlock extends DiodeBlock implements EntityBlock {
    public static final EnumProperty<ComparatorMode> MODE;
    
    public ComparatorBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)ComparatorBlock.FACING, Direction.NORTH)).setValue((Property<Comparable>)ComparatorBlock.POWERED, false)).<ComparatorMode, ComparatorMode>setValue(ComparatorBlock.MODE, ComparatorMode.COMPARE));
    }
    
    @Override
    protected int getDelay(final BlockState bvt) {
        return 2;
    }
    
    @Override
    protected int getOutputSignal(final BlockGetter bhb, final BlockPos ew, final BlockState bvt) {
        final BlockEntity btw5 = bhb.getBlockEntity(ew);
        if (btw5 instanceof ComparatorBlockEntity) {
            return ((ComparatorBlockEntity)btw5).getOutputSignal();
        }
        return 0;
    }
    
    private int calculateOutputSignal(final Level bhr, final BlockPos ew, final BlockState bvt) {
        if (bvt.<ComparatorMode>getValue(ComparatorBlock.MODE) == ComparatorMode.SUBTRACT) {
            return Math.max(this.getInputSignal(bhr, ew, bvt) - this.getAlternateSignal(bhr, ew, bvt), 0);
        }
        return this.getInputSignal(bhr, ew, bvt);
    }
    
    @Override
    protected boolean shouldTurnOn(final Level bhr, final BlockPos ew, final BlockState bvt) {
        final int integer5 = this.getInputSignal(bhr, ew, bvt);
        return integer5 >= 15 || (integer5 != 0 && integer5 >= this.getAlternateSignal(bhr, ew, bvt));
    }
    
    @Override
    protected int getInputSignal(final Level bhr, final BlockPos ew, final BlockState bvt) {
        int integer5 = super.getInputSignal(bhr, ew, bvt);
        final Direction fb6 = bvt.<Direction>getValue((Property<Direction>)ComparatorBlock.FACING);
        BlockPos ew2 = ew.relative(fb6);
        BlockState bvt2 = bhr.getBlockState(ew2);
        if (bvt2.hasAnalogOutputSignal()) {
            integer5 = bvt2.getAnalogOutputSignal(bhr, ew2);
        }
        else if (integer5 < 15 && bvt2.isRedstoneConductor(bhr, ew2)) {
            ew2 = ew2.relative(fb6);
            bvt2 = bhr.getBlockState(ew2);
            if (bvt2.hasAnalogOutputSignal()) {
                integer5 = bvt2.getAnalogOutputSignal(bhr, ew2);
            }
            else if (bvt2.isAir()) {
                final ItemFrame atn9 = this.getItemFrame(bhr, fb6, ew2);
                if (atn9 != null) {
                    integer5 = atn9.getAnalogOutput();
                }
            }
        }
        return integer5;
    }
    
    @Nullable
    private ItemFrame getItemFrame(final Level bhr, final Direction fb, final BlockPos ew) {
        final List<ItemFrame> list5 = bhr.<ItemFrame>getEntitiesOfClass((java.lang.Class<? extends ItemFrame>)ItemFrame.class, new AABB(ew.getX(), ew.getY(), ew.getZ(), ew.getX() + 1, ew.getY() + 1, ew.getZ() + 1), (java.util.function.Predicate<? super ItemFrame>)(atn -> atn != null && atn.getDirection() == fb));
        if (list5.size() == 1) {
            return (ItemFrame)list5.get(0);
        }
        return null;
    }
    
    public boolean use(BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        if (!awg.abilities.mayBuild) {
            return false;
        }
        bvt = ((AbstractStateHolder<O, BlockState>)bvt).<ComparatorMode>cycle(ComparatorBlock.MODE);
        final float float8 = (bvt.<ComparatorMode>getValue(ComparatorBlock.MODE) == ComparatorMode.SUBTRACT) ? 0.55f : 0.5f;
        bhr.playSound(awg, ew, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 0.3f, float8);
        bhr.setBlock(ew, bvt, 2);
        this.refreshOutputState(bhr, ew, bvt);
        return true;
    }
    
    @Override
    protected void checkTickOnNeighbor(final Level bhr, final BlockPos ew, final BlockState bvt) {
        if (bhr.getBlockTicks().willTickThisTick(ew, this)) {
            return;
        }
        final int integer5 = this.calculateOutputSignal(bhr, ew, bvt);
        final BlockEntity btw6 = bhr.getBlockEntity(ew);
        final int integer6 = (btw6 instanceof ComparatorBlockEntity) ? ((ComparatorBlockEntity)btw6).getOutputSignal() : 0;
        if (integer5 != integer6 || bvt.<Boolean>getValue((Property<Boolean>)ComparatorBlock.POWERED) != this.shouldTurnOn(bhr, ew, bvt)) {
            final TickPriority bii8 = this.shouldPrioritize(bhr, ew, bvt) ? TickPriority.HIGH : TickPriority.NORMAL;
            bhr.getBlockTicks().scheduleTick(ew, this, 2, bii8);
        }
    }
    
    private void refreshOutputState(final Level bhr, final BlockPos ew, final BlockState bvt) {
        final int integer5 = this.calculateOutputSignal(bhr, ew, bvt);
        final BlockEntity btw6 = bhr.getBlockEntity(ew);
        int integer6 = 0;
        if (btw6 instanceof ComparatorBlockEntity) {
            final ComparatorBlockEntity buc8 = (ComparatorBlockEntity)btw6;
            integer6 = buc8.getOutputSignal();
            buc8.setOutputSignal(integer5);
        }
        if (integer6 != integer5 || bvt.<ComparatorMode>getValue(ComparatorBlock.MODE) == ComparatorMode.COMPARE) {
            final boolean boolean8 = this.shouldTurnOn(bhr, ew, bvt);
            final boolean boolean9 = bvt.<Boolean>getValue((Property<Boolean>)ComparatorBlock.POWERED);
            if (boolean9 && !boolean8) {
                bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)ComparatorBlock.POWERED, false), 2);
            }
            else if (!boolean9 && boolean8) {
                bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)ComparatorBlock.POWERED, true), 2);
            }
            this.updateNeighborsInFront(bhr, ew, bvt);
        }
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        this.refreshOutputState(bhr, ew, bvt);
    }
    
    public boolean triggerEvent(final BlockState bvt, final Level bhr, final BlockPos ew, final int integer4, final int integer5) {
        super.triggerEvent(bvt, bhr, ew, integer4, integer5);
        final BlockEntity btw7 = bhr.getBlockEntity(ew);
        return btw7 != null && btw7.triggerEvent(integer4, integer5);
    }
    
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        return new ComparatorBlockEntity();
    }
    
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(ComparatorBlock.FACING, ComparatorBlock.MODE, ComparatorBlock.POWERED);
    }
    
    static {
        MODE = BlockStateProperties.MODE_COMPARATOR;
    }
}
