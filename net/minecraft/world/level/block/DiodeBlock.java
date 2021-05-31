package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.TickPriority;
import net.minecraft.world.level.block.state.properties.Property;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class DiodeBlock extends HorizontalDirectionalBlock {
    protected static final VoxelShape SHAPE;
    public static final BooleanProperty POWERED;
    
    protected DiodeBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return DiodeBlock.SHAPE;
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        return Block.canSupportRigidBlock(bhu, ew.below());
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (this.isLocked(bhr, ew, bvt)) {
            return;
        }
        final boolean boolean6 = bvt.<Boolean>getValue((Property<Boolean>)DiodeBlock.POWERED);
        final boolean boolean7 = this.shouldTurnOn(bhr, ew, bvt);
        if (boolean6 && !boolean7) {
            bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)DiodeBlock.POWERED, false), 2);
        }
        else if (!boolean6) {
            bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)DiodeBlock.POWERED, true), 2);
            if (!boolean7) {
                bhr.getBlockTicks().scheduleTick(ew, this, this.getDelay(bvt), TickPriority.HIGH);
            }
        }
    }
    
    @Override
    public int getDirectSignal(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        return bvt.getSignal(bhb, ew, fb);
    }
    
    @Override
    public int getSignal(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        if (!bvt.<Boolean>getValue((Property<Boolean>)DiodeBlock.POWERED)) {
            return 0;
        }
        if (bvt.<Comparable>getValue((Property<Comparable>)DiodeBlock.FACING) == fb) {
            return this.getOutputSignal(bhb, ew, bvt);
        }
        return 0;
    }
    
    @Override
    public void neighborChanged(final BlockState bvt, final Level bhr, final BlockPos ew3, final Block bmv, final BlockPos ew5, final boolean boolean6) {
        if (bvt.canSurvive(bhr, ew3)) {
            this.checkTickOnNeighbor(bhr, ew3, bvt);
            return;
        }
        final BlockEntity btw8 = this.isEntityBlock() ? bhr.getBlockEntity(ew3) : null;
        Block.dropResources(bvt, bhr, ew3, btw8);
        bhr.removeBlock(ew3, false);
        for (final Direction fb12 : Direction.values()) {
            bhr.updateNeighborsAt(ew3.relative(fb12), this);
        }
    }
    
    protected void checkTickOnNeighbor(final Level bhr, final BlockPos ew, final BlockState bvt) {
        if (this.isLocked(bhr, ew, bvt)) {
            return;
        }
        final boolean boolean5 = bvt.<Boolean>getValue((Property<Boolean>)DiodeBlock.POWERED);
        final boolean boolean6 = this.shouldTurnOn(bhr, ew, bvt);
        if (boolean5 != boolean6 && !bhr.getBlockTicks().willTickThisTick(ew, this)) {
            TickPriority bii7 = TickPriority.HIGH;
            if (this.shouldPrioritize(bhr, ew, bvt)) {
                bii7 = TickPriority.EXTREMELY_HIGH;
            }
            else if (boolean5) {
                bii7 = TickPriority.VERY_HIGH;
            }
            bhr.getBlockTicks().scheduleTick(ew, this, this.getDelay(bvt), bii7);
        }
    }
    
    public boolean isLocked(final LevelReader bhu, final BlockPos ew, final BlockState bvt) {
        return false;
    }
    
    protected boolean shouldTurnOn(final Level bhr, final BlockPos ew, final BlockState bvt) {
        return this.getInputSignal(bhr, ew, bvt) > 0;
    }
    
    protected int getInputSignal(final Level bhr, final BlockPos ew, final BlockState bvt) {
        final Direction fb5 = bvt.<Direction>getValue((Property<Direction>)DiodeBlock.FACING);
        final BlockPos ew2 = ew.relative(fb5);
        final int integer7 = bhr.getSignal(ew2, fb5);
        if (integer7 >= 15) {
            return integer7;
        }
        final BlockState bvt2 = bhr.getBlockState(ew2);
        return Math.max(integer7, (bvt2.getBlock() == Blocks.REDSTONE_WIRE) ? ((int)bvt2.<Integer>getValue((Property<Integer>)RedStoneWireBlock.POWER)) : 0);
    }
    
    protected int getAlternateSignal(final LevelReader bhu, final BlockPos ew, final BlockState bvt) {
        final Direction fb5 = bvt.<Direction>getValue((Property<Direction>)DiodeBlock.FACING);
        final Direction fb6 = fb5.getClockWise();
        final Direction fb7 = fb5.getCounterClockWise();
        return Math.max(this.getAlternateSignalAt(bhu, ew.relative(fb6), fb6), this.getAlternateSignalAt(bhu, ew.relative(fb7), fb7));
    }
    
    protected int getAlternateSignalAt(final LevelReader bhu, final BlockPos ew, final Direction fb) {
        final BlockState bvt5 = bhu.getBlockState(ew);
        final Block bmv6 = bvt5.getBlock();
        if (!this.isAlternateInput(bvt5)) {
            return 0;
        }
        if (bmv6 == Blocks.REDSTONE_BLOCK) {
            return 15;
        }
        if (bmv6 == Blocks.REDSTONE_WIRE) {
            return bvt5.<Integer>getValue((Property<Integer>)RedStoneWireBlock.POWER);
        }
        return bhu.getDirectSignal(ew, fb);
    }
    
    @Override
    public boolean isSignalSource(final BlockState bvt) {
        return true;
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        return ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)DiodeBlock.FACING, ban.getHorizontalDirection().getOpposite());
    }
    
    @Override
    public void setPlacedBy(final Level bhr, final BlockPos ew, final BlockState bvt, final LivingEntity aix, final ItemStack bcj) {
        if (this.shouldTurnOn(bhr, ew, bvt)) {
            bhr.getBlockTicks().scheduleTick(ew, this, 1);
        }
    }
    
    @Override
    public void onPlace(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        this.updateNeighborsInFront(bhr, ew, bvt1);
    }
    
    @Override
    public void onRemove(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (boolean5 || bvt1.getBlock() == bvt4.getBlock()) {
            return;
        }
        super.onRemove(bvt1, bhr, ew, bvt4, boolean5);
        this.updateNeighborsInFront(bhr, ew, bvt1);
    }
    
    protected void updateNeighborsInFront(final Level bhr, final BlockPos ew, final BlockState bvt) {
        final Direction fb5 = bvt.<Direction>getValue((Property<Direction>)DiodeBlock.FACING);
        final BlockPos ew2 = ew.relative(fb5.getOpposite());
        bhr.neighborChanged(ew2, this, ew);
        bhr.updateNeighborsAtExceptFromFacing(ew2, this, fb5);
    }
    
    protected boolean isAlternateInput(final BlockState bvt) {
        return bvt.isSignalSource();
    }
    
    protected int getOutputSignal(final BlockGetter bhb, final BlockPos ew, final BlockState bvt) {
        return 15;
    }
    
    public static boolean isDiode(final BlockState bvt) {
        return bvt.getBlock() instanceof DiodeBlock;
    }
    
    public boolean shouldPrioritize(final BlockGetter bhb, final BlockPos ew, final BlockState bvt) {
        final Direction fb5 = bvt.<Direction>getValue((Property<Direction>)DiodeBlock.FACING).getOpposite();
        final BlockState bvt2 = bhb.getBlockState(ew.relative(fb5));
        return isDiode(bvt2) && bvt2.<Comparable>getValue((Property<Comparable>)DiodeBlock.FACING) != fb5;
    }
    
    protected abstract int getDelay(final BlockState bvt);
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    @Override
    public boolean canOcclude(final BlockState bvt) {
        return true;
    }
    
    static {
        SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
        POWERED = BlockStateProperties.POWERED;
    }
}
