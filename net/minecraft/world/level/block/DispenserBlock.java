package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.core.PositionImpl;
import net.minecraft.core.Position;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import java.util.Random;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockSource;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.ItemLike;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.Item;
import java.util.Map;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class DispenserBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING;
    public static final BooleanProperty TRIGGERED;
    private static final Map<Item, DispenseItemBehavior> DISPENSER_REGISTRY;
    
    public static void registerBehavior(final ItemLike bhq, final DispenseItemBehavior fx) {
        DispenserBlock.DISPENSER_REGISTRY.put(bhq.asItem(), fx);
    }
    
    protected DispenserBlock(final Properties c) {
        super(c);
        this.registerDefaultState((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)DispenserBlock.FACING, Direction.NORTH)).<Comparable, Boolean>setValue((Property<Comparable>)DispenserBlock.TRIGGERED, false));
    }
    
    @Override
    public int getTickDelay(final LevelReader bhu) {
        return 4;
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        if (bhr.isClientSide) {
            return true;
        }
        final BlockEntity btw8 = bhr.getBlockEntity(ew);
        if (btw8 instanceof DispenserBlockEntity) {
            awg.openMenu((MenuProvider)btw8);
            if (btw8 instanceof DropperBlockEntity) {
                awg.awardStat(Stats.INSPECT_DROPPER);
            }
            else {
                awg.awardStat(Stats.INSPECT_DISPENSER);
            }
        }
        return true;
    }
    
    protected void dispenseFrom(final Level bhr, final BlockPos ew) {
        final BlockSourceImpl ey4 = new BlockSourceImpl(bhr, ew);
        final DispenserBlockEntity buf5 = ey4.<DispenserBlockEntity>getEntity();
        final int integer6 = buf5.getRandomSlot();
        if (integer6 < 0) {
            bhr.levelEvent(1001, ew, 0);
            return;
        }
        final ItemStack bcj7 = buf5.getItem(integer6);
        final DispenseItemBehavior fx8 = this.getDispenseMethod(bcj7);
        if (fx8 != DispenseItemBehavior.NOOP) {
            buf5.setItem(integer6, fx8.dispense(ey4, bcj7));
        }
    }
    
    protected DispenseItemBehavior getDispenseMethod(final ItemStack bcj) {
        return (DispenseItemBehavior)DispenserBlock.DISPENSER_REGISTRY.get(bcj.getItem());
    }
    
    @Override
    public void neighborChanged(final BlockState bvt, final Level bhr, final BlockPos ew3, final Block bmv, final BlockPos ew5, final boolean boolean6) {
        final boolean boolean7 = bhr.hasNeighborSignal(ew3) || bhr.hasNeighborSignal(ew3.above());
        final boolean boolean8 = bvt.<Boolean>getValue((Property<Boolean>)DispenserBlock.TRIGGERED);
        if (boolean7 && !boolean8) {
            bhr.getBlockTicks().scheduleTick(ew3, this, this.getTickDelay(bhr));
            bhr.setBlock(ew3, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)DispenserBlock.TRIGGERED, true), 4);
        }
        else if (!boolean7 && boolean8) {
            bhr.setBlock(ew3, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)DispenserBlock.TRIGGERED, false), 4);
        }
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (!bhr.isClientSide) {
            this.dispenseFrom(bhr, ew);
        }
    }
    
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        return new DispenserBlockEntity();
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        return ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)DispenserBlock.FACING, ban.getNearestLookingDirection().getOpposite());
    }
    
    @Override
    public void setPlacedBy(final Level bhr, final BlockPos ew, final BlockState bvt, final LivingEntity aix, final ItemStack bcj) {
        if (bcj.hasCustomHoverName()) {
            final BlockEntity btw7 = bhr.getBlockEntity(ew);
            if (btw7 instanceof DispenserBlockEntity) {
                ((DispenserBlockEntity)btw7).setCustomName(bcj.getHoverName());
            }
        }
    }
    
    @Override
    public void onRemove(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (bvt1.getBlock() == bvt4.getBlock()) {
            return;
        }
        final BlockEntity btw7 = bhr.getBlockEntity(ew);
        if (btw7 instanceof DispenserBlockEntity) {
            Containers.dropContents(bhr, ew, (Container)btw7);
            bhr.updateNeighbourForOutputSignal(ew, this);
        }
        super.onRemove(bvt1, bhr, ew, bvt4, boolean5);
    }
    
    public static Position getDispensePosition(final BlockSource ex) {
        final Direction fb2 = ex.getBlockState().<Direction>getValue((Property<Direction>)DispenserBlock.FACING);
        final double double3 = ex.x() + 0.7 * fb2.getStepX();
        final double double4 = ex.y() + 0.7 * fb2.getStepY();
        final double double5 = ex.z() + 0.7 * fb2.getStepZ();
        return new PositionImpl(double3, double4, double5);
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
    public RenderShape getRenderShape(final BlockState bvt) {
        return RenderShape.MODEL;
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)DispenserBlock.FACING, brg.rotate(bvt.<Direction>getValue((Property<Direction>)DispenserBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        return bvt.rotate(bqg.getRotation(bvt.<Direction>getValue((Property<Direction>)DispenserBlock.FACING)));
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(DispenserBlock.FACING, DispenserBlock.TRIGGERED);
    }
    
    static {
        FACING = DirectionalBlock.FACING;
        TRIGGERED = BlockStateProperties.TRIGGERED;
        DISPENSER_REGISTRY = Util.<Map>make((Map)new Object2ObjectOpenHashMap(), (java.util.function.Consumer<Map>)(object2ObjectOpenHashMap -> object2ObjectOpenHashMap.defaultReturnValue(new DefaultDispenseItemBehavior())));
    }
}
