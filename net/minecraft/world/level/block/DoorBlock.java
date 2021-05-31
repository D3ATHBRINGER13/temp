package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class DoorBlock extends Block {
    public static final DirectionProperty FACING;
    public static final BooleanProperty OPEN;
    public static final EnumProperty<DoorHingeSide> HINGE;
    public static final BooleanProperty POWERED;
    public static final EnumProperty<DoubleBlockHalf> HALF;
    protected static final VoxelShape SOUTH_AABB;
    protected static final VoxelShape NORTH_AABB;
    protected static final VoxelShape WEST_AABB;
    protected static final VoxelShape EAST_AABB;
    
    protected DoorBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)DoorBlock.FACING, Direction.NORTH)).setValue((Property<Comparable>)DoorBlock.OPEN, false)).setValue(DoorBlock.HINGE, DoorHingeSide.LEFT)).setValue((Property<Comparable>)DoorBlock.POWERED, false)).<DoubleBlockHalf, DoubleBlockHalf>setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER));
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        final Direction fb6 = bvt.<Direction>getValue((Property<Direction>)DoorBlock.FACING);
        final boolean boolean7 = !bvt.<Boolean>getValue((Property<Boolean>)DoorBlock.OPEN);
        final boolean boolean8 = bvt.<DoorHingeSide>getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT;
        switch (fb6) {
            default: {
                return boolean7 ? DoorBlock.EAST_AABB : (boolean8 ? DoorBlock.NORTH_AABB : DoorBlock.SOUTH_AABB);
            }
            case SOUTH: {
                return boolean7 ? DoorBlock.SOUTH_AABB : (boolean8 ? DoorBlock.EAST_AABB : DoorBlock.WEST_AABB);
            }
            case WEST: {
                return boolean7 ? DoorBlock.WEST_AABB : (boolean8 ? DoorBlock.SOUTH_AABB : DoorBlock.NORTH_AABB);
            }
            case NORTH: {
                return boolean7 ? DoorBlock.NORTH_AABB : (boolean8 ? DoorBlock.WEST_AABB : DoorBlock.EAST_AABB);
            }
        }
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        final DoubleBlockHalf bwq8 = bvt1.<DoubleBlockHalf>getValue(DoorBlock.HALF);
        if (fb.getAxis() == Direction.Axis.Y && bwq8 == DoubleBlockHalf.LOWER == (fb == Direction.UP)) {
            if (bvt3.getBlock() == this && bvt3.<DoubleBlockHalf>getValue(DoorBlock.HALF) != bwq8) {
                return (((((AbstractStateHolder<O, BlockState>)bvt1).setValue((Property<Comparable>)DoorBlock.FACING, (Comparable)bvt3.<V>getValue((Property<V>)DoorBlock.FACING))).setValue((Property<Comparable>)DoorBlock.OPEN, (Comparable)bvt3.<V>getValue((Property<V>)DoorBlock.OPEN))).setValue(DoorBlock.HINGE, (Comparable)bvt3.<V>getValue((Property<V>)DoorBlock.HINGE))).<Comparable, Comparable>setValue((Property<Comparable>)DoorBlock.POWERED, (Comparable)bvt3.<V>getValue((Property<V>)DoorBlock.POWERED));
            }
            return Blocks.AIR.defaultBlockState();
        }
        else {
            if (bwq8 == DoubleBlockHalf.LOWER && fb == Direction.DOWN && !bvt1.canSurvive(bhs, ew5)) {
                return Blocks.AIR.defaultBlockState();
            }
            return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
        }
    }
    
    @Override
    public void playerDestroy(final Level bhr, final Player awg, final BlockPos ew, final BlockState bvt, @Nullable final BlockEntity btw, final ItemStack bcj) {
        super.playerDestroy(bhr, awg, ew, Blocks.AIR.defaultBlockState(), btw, bcj);
    }
    
    @Override
    public void playerWillDestroy(final Level bhr, final BlockPos ew, final BlockState bvt, final Player awg) {
        final DoubleBlockHalf bwq6 = bvt.<DoubleBlockHalf>getValue(DoorBlock.HALF);
        final BlockPos ew2 = (bwq6 == DoubleBlockHalf.LOWER) ? ew.above() : ew.below();
        final BlockState bvt2 = bhr.getBlockState(ew2);
        if (bvt2.getBlock() == this && bvt2.<DoubleBlockHalf>getValue(DoorBlock.HALF) != bwq6) {
            bhr.setBlock(ew2, Blocks.AIR.defaultBlockState(), 35);
            bhr.levelEvent(awg, 2001, ew2, Block.getId(bvt2));
            final ItemStack bcj9 = awg.getMainHandItem();
            if (!bhr.isClientSide && !awg.isCreative()) {
                Block.dropResources(bvt, bhr, ew, null, awg, bcj9);
                Block.dropResources(bvt2, bhr, ew2, null, awg, bcj9);
            }
        }
        super.playerWillDestroy(bhr, ew, bvt, awg);
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        switch (cns) {
            case LAND: {
                return bvt.<Boolean>getValue((Property<Boolean>)DoorBlock.OPEN);
            }
            case WATER: {
                return false;
            }
            case AIR: {
                return bvt.<Boolean>getValue((Property<Boolean>)DoorBlock.OPEN);
            }
            default: {
                return false;
            }
        }
    }
    
    private int getCloseSound() {
        return (this.material == Material.METAL) ? 1011 : 1012;
    }
    
    private int getOpenSound() {
        return (this.material == Material.METAL) ? 1005 : 1006;
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final BlockPos ew3 = ban.getClickedPos();
        if (ew3.getY() < 255 && ban.getLevel().getBlockState(ew3.above()).canBeReplaced(ban)) {
            final Level bhr4 = ban.getLevel();
            final boolean boolean5 = bhr4.hasNeighborSignal(ew3) || bhr4.hasNeighborSignal(ew3.above());
            return ((((((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).setValue((Property<Comparable>)DoorBlock.FACING, ban.getHorizontalDirection())).setValue(DoorBlock.HINGE, this.getHinge(ban))).setValue((Property<Comparable>)DoorBlock.POWERED, boolean5)).setValue((Property<Comparable>)DoorBlock.OPEN, boolean5)).<DoubleBlockHalf, DoubleBlockHalf>setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER);
        }
        return null;
    }
    
    @Override
    public void setPlacedBy(final Level bhr, final BlockPos ew, final BlockState bvt, final LivingEntity aix, final ItemStack bcj) {
        bhr.setBlock(ew.above(), ((AbstractStateHolder<O, BlockState>)bvt).<DoubleBlockHalf, DoubleBlockHalf>setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), 3);
    }
    
    private DoorHingeSide getHinge(final BlockPlaceContext ban) {
        final BlockGetter bhb3 = ban.getLevel();
        final BlockPos ew4 = ban.getClickedPos();
        final Direction fb5 = ban.getHorizontalDirection();
        final BlockPos ew5 = ew4.above();
        final Direction fb6 = fb5.getCounterClockWise();
        final BlockPos ew6 = ew4.relative(fb6);
        final BlockState bvt9 = bhb3.getBlockState(ew6);
        final BlockPos ew7 = ew5.relative(fb6);
        final BlockState bvt10 = bhb3.getBlockState(ew7);
        final Direction fb7 = fb5.getClockWise();
        final BlockPos ew8 = ew4.relative(fb7);
        final BlockState bvt11 = bhb3.getBlockState(ew8);
        final BlockPos ew9 = ew5.relative(fb7);
        final BlockState bvt12 = bhb3.getBlockState(ew9);
        final int integer17 = (bvt9.isCollisionShapeFullBlock(bhb3, ew6) ? -1 : 0) + (bvt10.isCollisionShapeFullBlock(bhb3, ew7) ? -1 : 0) + (bvt11.isCollisionShapeFullBlock(bhb3, ew8) ? 1 : 0) + (bvt12.isCollisionShapeFullBlock(bhb3, ew9) ? 1 : 0);
        final boolean boolean18 = bvt9.getBlock() == this && bvt9.<DoubleBlockHalf>getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER;
        final boolean boolean19 = bvt11.getBlock() == this && bvt11.<DoubleBlockHalf>getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER;
        if ((boolean18 && !boolean19) || integer17 > 0) {
            return DoorHingeSide.RIGHT;
        }
        if ((boolean19 && !boolean18) || integer17 < 0) {
            return DoorHingeSide.LEFT;
        }
        final int integer18 = fb5.getStepX();
        final int integer19 = fb5.getStepZ();
        final Vec3 csi22 = ban.getClickLocation();
        final double double23 = csi22.x - ew4.getX();
        final double double24 = csi22.z - ew4.getZ();
        return ((integer18 < 0 && double24 < 0.5) || (integer18 > 0 && double24 > 0.5) || (integer19 < 0 && double23 > 0.5) || (integer19 > 0 && double23 < 0.5)) ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT;
    }
    
    @Override
    public boolean use(BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        if (this.material == Material.METAL) {
            return false;
        }
        bvt = ((AbstractStateHolder<O, BlockState>)bvt).<Comparable>cycle((Property<Comparable>)DoorBlock.OPEN);
        bhr.setBlock(ew, bvt, 10);
        bhr.levelEvent(awg, ((boolean)bvt.<Boolean>getValue((Property<Boolean>)DoorBlock.OPEN)) ? this.getOpenSound() : this.getCloseSound(), ew, 0);
        return true;
    }
    
    public void setOpen(final Level bhr, final BlockPos ew, final boolean boolean3) {
        final BlockState bvt5 = bhr.getBlockState(ew);
        if (bvt5.getBlock() != this || bvt5.<Boolean>getValue((Property<Boolean>)DoorBlock.OPEN) == boolean3) {
            return;
        }
        bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt5).<Comparable, Boolean>setValue((Property<Comparable>)DoorBlock.OPEN, boolean3), 10);
        this.playSound(bhr, ew, boolean3);
    }
    
    @Override
    public void neighborChanged(final BlockState bvt, final Level bhr, final BlockPos ew3, final Block bmv, final BlockPos ew5, final boolean boolean6) {
        final boolean boolean7 = bhr.hasNeighborSignal(ew3) || bhr.hasNeighborSignal(ew3.relative((bvt.<DoubleBlockHalf>getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) ? Direction.UP : Direction.DOWN));
        if (bmv != this && boolean7 != bvt.<Boolean>getValue((Property<Boolean>)DoorBlock.POWERED)) {
            if (boolean7 != bvt.<Boolean>getValue((Property<Boolean>)DoorBlock.OPEN)) {
                this.playSound(bhr, ew3, boolean7);
            }
            bhr.setBlock(ew3, (((AbstractStateHolder<O, BlockState>)bvt).setValue((Property<Comparable>)DoorBlock.POWERED, boolean7)).<Comparable, Boolean>setValue((Property<Comparable>)DoorBlock.OPEN, boolean7), 2);
        }
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        final BlockPos ew2 = ew.below();
        final BlockState bvt2 = bhu.getBlockState(ew2);
        if (bvt.<DoubleBlockHalf>getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
            return bvt2.isFaceSturdy(bhu, ew2, Direction.UP);
        }
        return bvt2.getBlock() == this;
    }
    
    private void playSound(final Level bhr, final BlockPos ew, final boolean boolean3) {
        bhr.levelEvent(null, boolean3 ? this.getOpenSound() : this.getCloseSound(), ew, 0);
    }
    
    @Override
    public PushReaction getPistonPushReaction(final BlockState bvt) {
        return PushReaction.DESTROY;
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)DoorBlock.FACING, brg.rotate(bvt.<Direction>getValue((Property<Direction>)DoorBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        if (bqg == Mirror.NONE) {
            return bvt;
        }
        return ((AbstractStateHolder<O, BlockState>)bvt.rotate(bqg.getRotation(bvt.<Direction>getValue((Property<Direction>)DoorBlock.FACING)))).<DoorHingeSide>cycle(DoorBlock.HINGE);
    }
    
    @Override
    public long getSeed(final BlockState bvt, final BlockPos ew) {
        return Mth.getSeed(ew.getX(), ew.below((bvt.<DoubleBlockHalf>getValue(DoorBlock.HALF) != DoubleBlockHalf.LOWER) ? 1 : 0).getY(), ew.getZ());
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(DoorBlock.HALF, DoorBlock.FACING, DoorBlock.OPEN, DoorBlock.HINGE, DoorBlock.POWERED);
    }
    
    static {
        FACING = HorizontalDirectionalBlock.FACING;
        OPEN = BlockStateProperties.OPEN;
        HINGE = BlockStateProperties.DOOR_HINGE;
        POWERED = BlockStateProperties.POWERED;
        HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
        SOUTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
        NORTH_AABB = Block.box(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
        WEST_AABB = Block.box(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        EAST_AABB = Block.box(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
    }
}
