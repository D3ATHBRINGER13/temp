package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.DustParticleOptions;
import java.util.Random;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class RepeaterBlock extends DiodeBlock {
    public static final BooleanProperty LOCKED;
    public static final IntegerProperty DELAY;
    
    protected RepeaterBlock(final Properties c) {
        super(c);
        this.registerDefaultState((((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)RepeaterBlock.FACING, Direction.NORTH)).setValue((Property<Comparable>)RepeaterBlock.DELAY, 1)).setValue((Property<Comparable>)RepeaterBlock.LOCKED, false)).<Comparable, Boolean>setValue((Property<Comparable>)RepeaterBlock.POWERED, false));
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        if (!awg.abilities.mayBuild) {
            return false;
        }
        bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable>cycle((Property<Comparable>)RepeaterBlock.DELAY), 3);
        return true;
    }
    
    @Override
    protected int getDelay(final BlockState bvt) {
        return bvt.<Integer>getValue((Property<Integer>)RepeaterBlock.DELAY) * 2;
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final BlockState bvt3 = super.getStateForPlacement(ban);
        return ((AbstractStateHolder<O, BlockState>)bvt3).<Comparable, Boolean>setValue((Property<Comparable>)RepeaterBlock.LOCKED, this.isLocked(ban.getLevel(), ban.getClickedPos(), bvt3));
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (!bhs.isClientSide() && fb.getAxis() != bvt1.<Direction>getValue((Property<Direction>)RepeaterBlock.FACING).getAxis()) {
            return ((AbstractStateHolder<O, BlockState>)bvt1).<Comparable, Boolean>setValue((Property<Comparable>)RepeaterBlock.LOCKED, this.isLocked(bhs, ew5, bvt1));
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public boolean isLocked(final LevelReader bhu, final BlockPos ew, final BlockState bvt) {
        return this.getAlternateSignal(bhu, ew, bvt) > 0;
    }
    
    @Override
    protected boolean isAlternateInput(final BlockState bvt) {
        return DiodeBlock.isDiode(bvt);
    }
    
    @Override
    public void animateTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (!bvt.<Boolean>getValue((Property<Boolean>)RepeaterBlock.POWERED)) {
            return;
        }
        final Direction fb6 = bvt.<Direction>getValue((Property<Direction>)RepeaterBlock.FACING);
        final double double7 = ew.getX() + 0.5f + (random.nextFloat() - 0.5f) * 0.2;
        final double double8 = ew.getY() + 0.4f + (random.nextFloat() - 0.5f) * 0.2;
        final double double9 = ew.getZ() + 0.5f + (random.nextFloat() - 0.5f) * 0.2;
        float float13 = -5.0f;
        if (random.nextBoolean()) {
            float13 = (float)(bvt.<Integer>getValue((Property<Integer>)RepeaterBlock.DELAY) * 2 - 1);
        }
        float13 /= 16.0f;
        final double double10 = float13 * fb6.getStepX();
        final double double11 = float13 * fb6.getStepZ();
        bhr.addParticle(DustParticleOptions.REDSTONE, double7 + double10, double8, double9 + double11, 0.0, 0.0, 0.0);
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(RepeaterBlock.FACING, RepeaterBlock.DELAY, RepeaterBlock.LOCKED, RepeaterBlock.POWERED);
    }
    
    static {
        LOCKED = BlockStateProperties.LOCKED;
        DELAY = BlockStateProperties.DELAY;
    }
}
