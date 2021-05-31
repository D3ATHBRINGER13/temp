package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.stats.Stats;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import java.util.Random;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class LecternBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING;
    public static final BooleanProperty POWERED;
    public static final BooleanProperty HAS_BOOK;
    public static final VoxelShape SHAPE_BASE;
    public static final VoxelShape SHAPE_POST;
    public static final VoxelShape SHAPE_COMMON;
    public static final VoxelShape SHAPE_TOP_PLATE;
    public static final VoxelShape SHAPE_COLLISION;
    public static final VoxelShape SHAPE_WEST;
    public static final VoxelShape SHAPE_NORTH;
    public static final VoxelShape SHAPE_EAST;
    public static final VoxelShape SHAPE_SOUTH;
    
    protected LecternBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)LecternBlock.FACING, Direction.NORTH)).setValue((Property<Comparable>)LecternBlock.POWERED, false)).<Comparable, Boolean>setValue((Property<Comparable>)LecternBlock.HAS_BOOK, false));
    }
    
    @Override
    public RenderShape getRenderShape(final BlockState bvt) {
        return RenderShape.MODEL;
    }
    
    @Override
    public VoxelShape getOcclusionShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return LecternBlock.SHAPE_COMMON;
    }
    
    @Override
    public boolean useShapeForLightOcclusion(final BlockState bvt) {
        return true;
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        return ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)LecternBlock.FACING, ban.getHorizontalDirection().getOpposite());
    }
    
    @Override
    public VoxelShape getCollisionShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return LecternBlock.SHAPE_COLLISION;
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        switch (bvt.<Direction>getValue((Property<Direction>)LecternBlock.FACING)) {
            case NORTH: {
                return LecternBlock.SHAPE_NORTH;
            }
            case SOUTH: {
                return LecternBlock.SHAPE_SOUTH;
            }
            case EAST: {
                return LecternBlock.SHAPE_EAST;
            }
            case WEST: {
                return LecternBlock.SHAPE_WEST;
            }
            default: {
                return LecternBlock.SHAPE_COMMON;
            }
        }
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)LecternBlock.FACING, brg.rotate(bvt.<Direction>getValue((Property<Direction>)LecternBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        return bvt.rotate(bqg.getRotation(bvt.<Direction>getValue((Property<Direction>)LecternBlock.FACING)));
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(LecternBlock.FACING, LecternBlock.POWERED, LecternBlock.HAS_BOOK);
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        return new LecternBlockEntity();
    }
    
    public static boolean tryPlaceBook(final Level bhr, final BlockPos ew, final BlockState bvt, final ItemStack bcj) {
        if (!bvt.<Boolean>getValue((Property<Boolean>)LecternBlock.HAS_BOOK)) {
            if (!bhr.isClientSide) {
                placeBook(bhr, ew, bvt, bcj);
            }
            return true;
        }
        return false;
    }
    
    private static void placeBook(final Level bhr, final BlockPos ew, final BlockState bvt, final ItemStack bcj) {
        final BlockEntity btw5 = bhr.getBlockEntity(ew);
        if (btw5 instanceof LecternBlockEntity) {
            final LecternBlockEntity buo6 = (LecternBlockEntity)btw5;
            buo6.setBook(bcj.split(1));
            resetBookState(bhr, ew, bvt, true);
            bhr.playSound(null, ew, SoundEvents.BOOK_PUT, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
    }
    
    public static void resetBookState(final Level bhr, final BlockPos ew, final BlockState bvt, final boolean boolean4) {
        bhr.setBlock(ew, (((AbstractStateHolder<O, BlockState>)bvt).setValue((Property<Comparable>)LecternBlock.POWERED, false)).<Comparable, Boolean>setValue((Property<Comparable>)LecternBlock.HAS_BOOK, boolean4), 3);
        updateBelow(bhr, ew, bvt);
    }
    
    public static void signalPageChange(final Level bhr, final BlockPos ew, final BlockState bvt) {
        changePowered(bhr, ew, bvt, true);
        bhr.getBlockTicks().scheduleTick(ew, bvt.getBlock(), 2);
        bhr.levelEvent(1043, ew, 0);
    }
    
    private static void changePowered(final Level bhr, final BlockPos ew, final BlockState bvt, final boolean boolean4) {
        bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)LecternBlock.POWERED, boolean4), 3);
        updateBelow(bhr, ew, bvt);
    }
    
    private static void updateBelow(final Level bhr, final BlockPos ew, final BlockState bvt) {
        bhr.updateNeighborsAt(ew.below(), bvt.getBlock());
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (bhr.isClientSide) {
            return;
        }
        changePowered(bhr, ew, bvt, false);
    }
    
    @Override
    public void onRemove(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (bvt1.getBlock() == bvt4.getBlock()) {
            return;
        }
        if (bvt1.<Boolean>getValue((Property<Boolean>)LecternBlock.HAS_BOOK)) {
            this.popBook(bvt1, bhr, ew);
        }
        if (bvt1.<Boolean>getValue((Property<Boolean>)LecternBlock.POWERED)) {
            bhr.updateNeighborsAt(ew.below(), this);
        }
        super.onRemove(bvt1, bhr, ew, bvt4, boolean5);
    }
    
    private void popBook(final BlockState bvt, final Level bhr, final BlockPos ew) {
        final BlockEntity btw5 = bhr.getBlockEntity(ew);
        if (btw5 instanceof LecternBlockEntity) {
            final LecternBlockEntity buo6 = (LecternBlockEntity)btw5;
            final Direction fb7 = bvt.<Direction>getValue((Property<Direction>)LecternBlock.FACING);
            final ItemStack bcj8 = buo6.getBook().copy();
            final float float9 = 0.25f * fb7.getStepX();
            final float float10 = 0.25f * fb7.getStepZ();
            final ItemEntity atx11 = new ItemEntity(bhr, ew.getX() + 0.5 + float9, ew.getY() + 1, ew.getZ() + 0.5 + float10, bcj8);
            atx11.setDefaultPickUpDelay();
            bhr.addFreshEntity(atx11);
            buo6.clearContent();
        }
    }
    
    @Override
    public boolean isSignalSource(final BlockState bvt) {
        return true;
    }
    
    @Override
    public int getSignal(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        return bvt.<Boolean>getValue((Property<Boolean>)LecternBlock.POWERED) ? 15 : 0;
    }
    
    @Override
    public int getDirectSignal(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        return (fb == Direction.UP && bvt.<Boolean>getValue((Property<Boolean>)LecternBlock.POWERED)) ? 15 : 0;
    }
    
    @Override
    public boolean hasAnalogOutputSignal(final BlockState bvt) {
        return true;
    }
    
    @Override
    public int getAnalogOutputSignal(final BlockState bvt, final Level bhr, final BlockPos ew) {
        if (bvt.<Boolean>getValue((Property<Boolean>)LecternBlock.HAS_BOOK)) {
            final BlockEntity btw5 = bhr.getBlockEntity(ew);
            if (btw5 instanceof LecternBlockEntity) {
                return ((LecternBlockEntity)btw5).getRedstoneSignal();
            }
        }
        return 0;
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        if (bvt.<Boolean>getValue((Property<Boolean>)LecternBlock.HAS_BOOK)) {
            if (!bhr.isClientSide) {
                this.openScreen(bhr, ew, awg);
            }
            return true;
        }
        return false;
    }
    
    @Nullable
    @Override
    public MenuProvider getMenuProvider(final BlockState bvt, final Level bhr, final BlockPos ew) {
        if (!bvt.<Boolean>getValue((Property<Boolean>)LecternBlock.HAS_BOOK)) {
            return null;
        }
        return super.getMenuProvider(bvt, bhr, ew);
    }
    
    private void openScreen(final Level bhr, final BlockPos ew, final Player awg) {
        final BlockEntity btw5 = bhr.getBlockEntity(ew);
        if (btw5 instanceof LecternBlockEntity) {
            awg.openMenu((MenuProvider)btw5);
            awg.awardStat(Stats.INTERACT_WITH_LECTERN);
        }
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return false;
    }
    
    static {
        FACING = HorizontalDirectionalBlock.FACING;
        POWERED = BlockStateProperties.POWERED;
        HAS_BOOK = BlockStateProperties.HAS_BOOK;
        SHAPE_BASE = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
        SHAPE_POST = Block.box(4.0, 2.0, 4.0, 12.0, 14.0, 12.0);
        SHAPE_COMMON = Shapes.or(LecternBlock.SHAPE_BASE, LecternBlock.SHAPE_POST);
        SHAPE_TOP_PLATE = Block.box(0.0, 15.0, 0.0, 16.0, 15.0, 16.0);
        SHAPE_COLLISION = Shapes.or(LecternBlock.SHAPE_COMMON, LecternBlock.SHAPE_TOP_PLATE);
        SHAPE_WEST = Shapes.or(Block.box(1.0, 10.0, 0.0, 5.333333, 14.0, 16.0), Block.box(5.333333, 12.0, 0.0, 9.666667, 16.0, 16.0), Block.box(9.666667, 14.0, 0.0, 14.0, 18.0, 16.0), LecternBlock.SHAPE_COMMON);
        SHAPE_NORTH = Shapes.or(Block.box(0.0, 10.0, 1.0, 16.0, 14.0, 5.333333), Block.box(0.0, 12.0, 5.333333, 16.0, 16.0, 9.666667), Block.box(0.0, 14.0, 9.666667, 16.0, 18.0, 14.0), LecternBlock.SHAPE_COMMON);
        SHAPE_EAST = Shapes.or(Block.box(15.0, 10.0, 0.0, 10.666667, 14.0, 16.0), Block.box(10.666667, 12.0, 0.0, 6.333333, 16.0, 16.0), Block.box(6.333333, 14.0, 0.0, 2.0, 18.0, 16.0), LecternBlock.SHAPE_COMMON);
        SHAPE_SOUTH = Shapes.or(Block.box(0.0, 10.0, 15.0, 16.0, 14.0, 10.666667), Block.box(0.0, 12.0, 10.666667, 16.0, 16.0, 6.333333), Block.box(0.0, 14.0, 6.333333, 16.0, 18.0, 2.0), LecternBlock.SHAPE_COMMON);
    }
}
