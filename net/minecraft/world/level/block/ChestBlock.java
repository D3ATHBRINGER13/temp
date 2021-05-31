package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.inventory.AbstractContainerMenu;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.stats.Stats;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Containers;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Container;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class ChestBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING;
    public static final EnumProperty<ChestType> TYPE;
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape NORTH_AABB;
    protected static final VoxelShape SOUTH_AABB;
    protected static final VoxelShape WEST_AABB;
    protected static final VoxelShape EAST_AABB;
    protected static final VoxelShape AABB;
    private static final ChestSearchCallback<Container> CHEST_COMBINER;
    private static final ChestSearchCallback<MenuProvider> MENU_PROVIDER_COMBINER;
    
    protected ChestBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)ChestBlock.FACING, Direction.NORTH)).setValue(ChestBlock.TYPE, ChestType.SINGLE)).<Comparable, Boolean>setValue((Property<Comparable>)ChestBlock.WATERLOGGED, false));
    }
    
    @Override
    public boolean hasCustomBreakingProgress(final BlockState bvt) {
        return true;
    }
    
    @Override
    public RenderShape getRenderShape(final BlockState bvt) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (bvt1.<Boolean>getValue((Property<Boolean>)ChestBlock.WATERLOGGED)) {
            bhs.getLiquidTicks().scheduleTick(ew5, Fluids.WATER, Fluids.WATER.getTickDelay(bhs));
        }
        if (bvt3.getBlock() == this && fb.getAxis().isHorizontal()) {
            final ChestType bwm8 = bvt3.<ChestType>getValue(ChestBlock.TYPE);
            if (bvt1.<ChestType>getValue(ChestBlock.TYPE) == ChestType.SINGLE && bwm8 != ChestType.SINGLE && bvt1.<Comparable>getValue((Property<Comparable>)ChestBlock.FACING) == bvt3.<Comparable>getValue((Property<Comparable>)ChestBlock.FACING) && getConnectedDirection(bvt3) == fb.getOpposite()) {
                return ((AbstractStateHolder<O, BlockState>)bvt1).<ChestType, ChestType>setValue(ChestBlock.TYPE, bwm8.getOpposite());
            }
        }
        else if (getConnectedDirection(bvt1) == fb) {
            return ((AbstractStateHolder<O, BlockState>)bvt1).<ChestType, ChestType>setValue(ChestBlock.TYPE, ChestType.SINGLE);
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        if (bvt.<ChestType>getValue(ChestBlock.TYPE) == ChestType.SINGLE) {
            return ChestBlock.AABB;
        }
        switch (getConnectedDirection(bvt)) {
            default: {
                return ChestBlock.NORTH_AABB;
            }
            case SOUTH: {
                return ChestBlock.SOUTH_AABB;
            }
            case WEST: {
                return ChestBlock.WEST_AABB;
            }
            case EAST: {
                return ChestBlock.EAST_AABB;
            }
        }
    }
    
    public static Direction getConnectedDirection(final BlockState bvt) {
        final Direction fb2 = bvt.<Direction>getValue((Property<Direction>)ChestBlock.FACING);
        return (bvt.<ChestType>getValue(ChestBlock.TYPE) == ChestType.LEFT) ? fb2.getClockWise() : fb2.getCounterClockWise();
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        ChestType bwm3 = ChestType.SINGLE;
        Direction fb4 = ban.getHorizontalDirection().getOpposite();
        final FluidState clk5 = ban.getLevel().getFluidState(ban.getClickedPos());
        final boolean boolean6 = ban.isSneaking();
        final Direction fb5 = ban.getClickedFace();
        if (fb5.getAxis().isHorizontal() && boolean6) {
            final Direction fb6 = this.candidatePartnerFacing(ban, fb5.getOpposite());
            if (fb6 != null && fb6.getAxis() != fb5.getAxis()) {
                fb4 = fb6;
                bwm3 = ((fb4.getCounterClockWise() == fb5.getOpposite()) ? ChestType.RIGHT : ChestType.LEFT);
            }
        }
        if (bwm3 == ChestType.SINGLE && !boolean6) {
            if (fb4 == this.candidatePartnerFacing(ban, fb4.getClockWise())) {
                bwm3 = ChestType.LEFT;
            }
            else if (fb4 == this.candidatePartnerFacing(ban, fb4.getCounterClockWise())) {
                bwm3 = ChestType.RIGHT;
            }
        }
        return ((((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).setValue((Property<Comparable>)ChestBlock.FACING, fb4)).setValue(ChestBlock.TYPE, bwm3)).<Comparable, Boolean>setValue((Property<Comparable>)ChestBlock.WATERLOGGED, clk5.getType() == Fluids.WATER);
    }
    
    @Override
    public FluidState getFluidState(final BlockState bvt) {
        if (bvt.<Boolean>getValue((Property<Boolean>)ChestBlock.WATERLOGGED)) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState(bvt);
    }
    
    @Nullable
    private Direction candidatePartnerFacing(final BlockPlaceContext ban, final Direction fb) {
        final BlockState bvt4 = ban.getLevel().getBlockState(ban.getClickedPos().relative(fb));
        return (bvt4.getBlock() == this && bvt4.<ChestType>getValue(ChestBlock.TYPE) == ChestType.SINGLE) ? bvt4.<Direction>getValue((Property<Direction>)ChestBlock.FACING) : null;
    }
    
    @Override
    public void setPlacedBy(final Level bhr, final BlockPos ew, final BlockState bvt, final LivingEntity aix, final ItemStack bcj) {
        if (bcj.hasCustomHoverName()) {
            final BlockEntity btw7 = bhr.getBlockEntity(ew);
            if (btw7 instanceof ChestBlockEntity) {
                ((ChestBlockEntity)btw7).setCustomName(bcj.getHoverName());
            }
        }
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
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        if (bhr.isClientSide) {
            return true;
        }
        final MenuProvider ahm8 = this.getMenuProvider(bvt, bhr, ew);
        if (ahm8 != null) {
            awg.openMenu(ahm8);
            awg.awardStat(this.getOpenChestStat());
        }
        return true;
    }
    
    protected Stat<ResourceLocation> getOpenChestStat() {
        return Stats.CUSTOM.get(Stats.OPEN_CHEST);
    }
    
    @Nullable
    public static <T> T combineWithNeigbour(final BlockState bvt, final LevelAccessor bhs, final BlockPos ew, final boolean boolean4, final ChestSearchCallback<T> a) {
        final BlockEntity btw6 = bhs.getBlockEntity(ew);
        if (!(btw6 instanceof ChestBlockEntity)) {
            return null;
        }
        if (!boolean4 && isChestBlockedAt(bhs, ew)) {
            return null;
        }
        final ChestBlockEntity bua7 = (ChestBlockEntity)btw6;
        final ChestType bwm8 = bvt.<ChestType>getValue(ChestBlock.TYPE);
        if (bwm8 == ChestType.SINGLE) {
            return a.acceptSingle(bua7);
        }
        final BlockPos ew2 = ew.relative(getConnectedDirection(bvt));
        final BlockState bvt2 = bhs.getBlockState(ew2);
        if (bvt2.getBlock() == bvt.getBlock()) {
            final ChestType bwm9 = bvt2.<ChestType>getValue(ChestBlock.TYPE);
            if (bwm9 != ChestType.SINGLE && bwm8 != bwm9 && bvt2.<Comparable>getValue((Property<Comparable>)ChestBlock.FACING) == bvt.<Comparable>getValue((Property<Comparable>)ChestBlock.FACING)) {
                if (!boolean4 && isChestBlockedAt(bhs, ew2)) {
                    return null;
                }
                final BlockEntity btw7 = bhs.getBlockEntity(ew2);
                if (btw7 instanceof ChestBlockEntity) {
                    final ChestBlockEntity bua8 = (ChestBlockEntity)((bwm8 == ChestType.RIGHT) ? bua7 : btw7);
                    final ChestBlockEntity bua9 = (ChestBlockEntity)((bwm8 == ChestType.RIGHT) ? btw7 : bua7);
                    return a.acceptDouble(bua8, bua9);
                }
            }
        }
        return a.acceptSingle(bua7);
    }
    
    @Nullable
    public static Container getContainer(final BlockState bvt, final Level bhr, final BlockPos ew, final boolean boolean4) {
        return ChestBlock.<Container>combineWithNeigbour(bvt, bhr, ew, boolean4, ChestBlock.CHEST_COMBINER);
    }
    
    @Nullable
    @Override
    public MenuProvider getMenuProvider(final BlockState bvt, final Level bhr, final BlockPos ew) {
        return ChestBlock.<MenuProvider>combineWithNeigbour(bvt, bhr, ew, false, ChestBlock.MENU_PROVIDER_COMBINER);
    }
    
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        return new ChestBlockEntity();
    }
    
    private static boolean isChestBlockedAt(final LevelAccessor bhs, final BlockPos ew) {
        return isBlockedChestByBlock(bhs, ew) || isCatSittingOnChest(bhs, ew);
    }
    
    private static boolean isBlockedChestByBlock(final BlockGetter bhb, final BlockPos ew) {
        final BlockPos ew2 = ew.above();
        return bhb.getBlockState(ew2).isRedstoneConductor(bhb, ew2);
    }
    
    private static boolean isCatSittingOnChest(final LevelAccessor bhs, final BlockPos ew) {
        final List<Cat> list3 = bhs.<Cat>getEntitiesOfClass((java.lang.Class<? extends Cat>)Cat.class, new AABB(ew.getX(), ew.getY() + 1, ew.getZ(), ew.getX() + 1, ew.getY() + 2, ew.getZ() + 1));
        if (!list3.isEmpty()) {
            for (final Cat arb5 : list3) {
                if (arb5.isSitting()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean hasAnalogOutputSignal(final BlockState bvt) {
        return true;
    }
    
    @Override
    public int getAnalogOutputSignal(final BlockState bvt, final Level bhr, final BlockPos ew) {
        return AbstractContainerMenu.getRedstoneSignalFromContainer(getContainer(bvt, bhr, ew, false));
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)ChestBlock.FACING, brg.rotate(bvt.<Direction>getValue((Property<Direction>)ChestBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        return bvt.rotate(bqg.getRotation(bvt.<Direction>getValue((Property<Direction>)ChestBlock.FACING)));
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(ChestBlock.FACING, ChestBlock.TYPE, ChestBlock.WATERLOGGED);
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return false;
    }
    
    static {
        FACING = HorizontalDirectionalBlock.FACING;
        TYPE = BlockStateProperties.CHEST_TYPE;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        NORTH_AABB = Block.box(1.0, 0.0, 0.0, 15.0, 14.0, 15.0);
        SOUTH_AABB = Block.box(1.0, 0.0, 1.0, 15.0, 14.0, 16.0);
        WEST_AABB = Block.box(0.0, 0.0, 1.0, 15.0, 14.0, 15.0);
        EAST_AABB = Block.box(1.0, 0.0, 1.0, 16.0, 14.0, 15.0);
        AABB = Block.box(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
        CHEST_COMBINER = new ChestSearchCallback<Container>() {
            public Container acceptDouble(final ChestBlockEntity bua1, final ChestBlockEntity bua2) {
                return new CompoundContainer(bua1, bua2);
            }
            
            public Container acceptSingle(final ChestBlockEntity bua) {
                return bua;
            }
        };
        MENU_PROVIDER_COMBINER = new ChestSearchCallback<MenuProvider>() {
            public MenuProvider acceptDouble(final ChestBlockEntity bua1, final ChestBlockEntity bua2) {
                final Container ahc4 = new CompoundContainer(bua1, bua2);
                return new MenuProvider() {
                    @Nullable
                    public AbstractContainerMenu createMenu(final int integer, final Inventory awf, final Player awg) {
                        if (bua1.canOpen(awg) && bua2.canOpen(awg)) {
                            bua1.unpackLootTable(awf.player);
                            bua2.unpackLootTable(awf.player);
                            return ChestMenu.sixRows(integer, awf, ahc4);
                        }
                        return null;
                    }
                    
                    public Component getDisplayName() {
                        if (bua1.hasCustomName()) {
                            return bua1.getDisplayName();
                        }
                        if (bua2.hasCustomName()) {
                            return bua2.getDisplayName();
                        }
                        return new TranslatableComponent("container.chestDouble", new Object[0]);
                    }
                };
            }
            
            public MenuProvider acceptSingle(final ChestBlockEntity bua) {
                return bua;
            }
        };
    }
    
    interface ChestSearchCallback<T> {
        T acceptDouble(final ChestBlockEntity bua1, final ChestBlockEntity bua2);
        
        T acceptSingle(final ChestBlockEntity bua);
    }
}
