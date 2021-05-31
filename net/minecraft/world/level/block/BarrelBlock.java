package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import javax.annotation.Nullable;
import net.minecraft.world.level.BlockGetter;
import java.util.Random;
import net.minecraft.world.Containers;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.stats.Stats;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class BarrelBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING;
    public static final BooleanProperty OPEN;
    
    public BarrelBlock(final Properties c) {
        super(c);
        this.registerDefaultState((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)BarrelBlock.FACING, Direction.NORTH)).<Comparable, Boolean>setValue((Property<Comparable>)BarrelBlock.OPEN, false));
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        if (bhr.isClientSide) {
            return true;
        }
        final BlockEntity btw8 = bhr.getBlockEntity(ew);
        if (btw8 instanceof BarrelBlockEntity) {
            awg.openMenu((MenuProvider)btw8);
            awg.awardStat(Stats.OPEN_BARREL);
        }
        return true;
    }
    
    @Override
    public void onRemove(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (bvt1.getBlock() == bvt4.getBlock()) {
            return;
        }
        final BlockEntity btw7 = bhr.getBlockEntity(ew);
        if (btw7 instanceof Container) {
            Containers.dropContents(bhr, ew, (Container)btw7);
            bhr.updateNeighbourForOutputSignal(ew, this);
        }
        super.onRemove(bvt1, bhr, ew, bvt4, boolean5);
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        final BlockEntity btw6 = bhr.getBlockEntity(ew);
        if (btw6 instanceof BarrelBlockEntity) {
            ((BarrelBlockEntity)btw6).recheckOpen();
        }
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        return new BarrelBlockEntity();
    }
    
    @Override
    public RenderShape getRenderShape(final BlockState bvt) {
        return RenderShape.MODEL;
    }
    
    @Override
    public void setPlacedBy(final Level bhr, final BlockPos ew, final BlockState bvt, @Nullable final LivingEntity aix, final ItemStack bcj) {
        if (bcj.hasCustomHoverName()) {
            final BlockEntity btw7 = bhr.getBlockEntity(ew);
            if (btw7 instanceof BarrelBlockEntity) {
                ((BarrelBlockEntity)btw7).setCustomName(bcj.getHoverName());
            }
        }
    }
    
    @Override
    public boolean hasAnalogOutputSignal(final BlockState bvt) {
        return true;
    }
    
    @Override
    public int getAnalogOutputSignal(final BlockState bvt, final Level bhr, final BlockPos ew) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(bhr.getBlockEntity(ew));
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)BarrelBlock.FACING, brg.rotate(bvt.<Direction>getValue((Property<Direction>)BarrelBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        return bvt.rotate(bqg.getRotation(bvt.<Direction>getValue((Property<Direction>)BarrelBlock.FACING)));
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(BarrelBlock.FACING, BarrelBlock.OPEN);
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        return ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)BarrelBlock.FACING, ban.getNearestLookingDirection().getOpposite());
    }
    
    static {
        FACING = BlockStateProperties.FACING;
        OPEN = BlockStateProperties.OPEN;
    }
}
