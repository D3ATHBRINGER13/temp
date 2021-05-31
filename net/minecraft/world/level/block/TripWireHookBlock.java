package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import java.util.Random;
import com.google.common.base.MoreObjects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class TripWireHookBlock extends Block {
    public static final DirectionProperty FACING;
    public static final BooleanProperty POWERED;
    public static final BooleanProperty ATTACHED;
    protected static final VoxelShape NORTH_AABB;
    protected static final VoxelShape SOUTH_AABB;
    protected static final VoxelShape WEST_AABB;
    protected static final VoxelShape EAST_AABB;
    
    public TripWireHookBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)TripWireHookBlock.FACING, Direction.NORTH)).setValue((Property<Comparable>)TripWireHookBlock.POWERED, false)).<Comparable, Boolean>setValue((Property<Comparable>)TripWireHookBlock.ATTACHED, false));
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        switch (bvt.<Direction>getValue((Property<Direction>)TripWireHookBlock.FACING)) {
            default: {
                return TripWireHookBlock.EAST_AABB;
            }
            case WEST: {
                return TripWireHookBlock.WEST_AABB;
            }
            case SOUTH: {
                return TripWireHookBlock.SOUTH_AABB;
            }
            case NORTH: {
                return TripWireHookBlock.NORTH_AABB;
            }
        }
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        final Direction fb5 = bvt.<Direction>getValue((Property<Direction>)TripWireHookBlock.FACING);
        final BlockPos ew2 = ew.relative(fb5.getOpposite());
        final BlockState bvt2 = bhu.getBlockState(ew2);
        return fb5.getAxis().isHorizontal() && bvt2.isFaceSturdy(bhu, ew2, fb5) && !bvt2.isSignalSource();
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (fb.getOpposite() == bvt1.<Comparable>getValue((Property<Comparable>)TripWireHookBlock.FACING) && !bvt1.canSurvive(bhs, ew5)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        BlockState bvt3 = (((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).setValue((Property<Comparable>)TripWireHookBlock.POWERED, false)).<Comparable, Boolean>setValue((Property<Comparable>)TripWireHookBlock.ATTACHED, false);
        final LevelReader bhu4 = ban.getLevel();
        final BlockPos ew5 = ban.getClickedPos();
        final Direction[] nearestLookingDirections;
        final Direction[] arr6 = nearestLookingDirections = ban.getNearestLookingDirections();
        for (final Direction fb10 : nearestLookingDirections) {
            if (fb10.getAxis().isHorizontal()) {
                final Direction fb11 = fb10.getOpposite();
                bvt3 = ((AbstractStateHolder<O, BlockState>)bvt3).<Comparable, Direction>setValue((Property<Comparable>)TripWireHookBlock.FACING, fb11);
                if (bvt3.canSurvive(bhu4, ew5)) {
                    return bvt3;
                }
            }
        }
        return null;
    }
    
    @Override
    public void setPlacedBy(final Level bhr, final BlockPos ew, final BlockState bvt, final LivingEntity aix, final ItemStack bcj) {
        this.calculateState(bhr, ew, bvt, false, false, -1, null);
    }
    
    public void calculateState(final Level bhr, final BlockPos ew, final BlockState bvt3, final boolean boolean4, final boolean boolean5, final int integer, @Nullable final BlockState bvt7) {
        final Direction fb9 = bvt3.<Direction>getValue((Property<Direction>)TripWireHookBlock.FACING);
        final boolean boolean6 = bvt3.<Boolean>getValue((Property<Boolean>)TripWireHookBlock.ATTACHED);
        final boolean boolean7 = bvt3.<Boolean>getValue((Property<Boolean>)TripWireHookBlock.POWERED);
        boolean boolean8 = !boolean4;
        boolean boolean9 = false;
        int integer2 = 0;
        final BlockState[] arr15 = new BlockState[42];
        int integer3 = 1;
        while (integer3 < 42) {
            final BlockPos ew2 = ew.relative(fb9, integer3);
            BlockState bvt8 = bhr.getBlockState(ew2);
            if (bvt8.getBlock() == Blocks.TRIPWIRE_HOOK) {
                if (bvt8.<Comparable>getValue((Property<Comparable>)TripWireHookBlock.FACING) == fb9.getOpposite()) {
                    integer2 = integer3;
                    break;
                }
                break;
            }
            else {
                if (bvt8.getBlock() == Blocks.TRIPWIRE || integer3 == integer) {
                    if (integer3 == integer) {
                        bvt8 = (BlockState)MoreObjects.firstNonNull(bvt7, bvt8);
                    }
                    final boolean boolean10 = !bvt8.<Boolean>getValue((Property<Boolean>)TripWireBlock.DISARMED);
                    final boolean boolean11 = bvt8.<Boolean>getValue((Property<Boolean>)TripWireBlock.POWERED);
                    boolean9 |= (boolean10 && boolean11);
                    arr15[integer3] = bvt8;
                    if (integer3 == integer) {
                        bhr.getBlockTicks().scheduleTick(ew, this, this.getTickDelay(bhr));
                        boolean8 &= boolean10;
                    }
                }
                else {
                    arr15[integer3] = null;
                    boolean8 = false;
                }
                ++integer3;
            }
        }
        boolean8 &= (integer2 > 1);
        boolean9 &= boolean8;
        final BlockState bvt9 = (((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).setValue((Property<Comparable>)TripWireHookBlock.ATTACHED, boolean8)).<Comparable, Boolean>setValue((Property<Comparable>)TripWireHookBlock.POWERED, boolean9);
        if (integer2 > 0) {
            final BlockPos ew2 = ew.relative(fb9, integer2);
            final Direction fb10 = fb9.getOpposite();
            bhr.setBlock(ew2, ((AbstractStateHolder<O, BlockState>)bvt9).<Comparable, Direction>setValue((Property<Comparable>)TripWireHookBlock.FACING, fb10), 3);
            this.notifyNeighbors(bhr, ew2, fb10);
            this.playSound(bhr, ew2, boolean8, boolean9, boolean6, boolean7);
        }
        this.playSound(bhr, ew, boolean8, boolean9, boolean6, boolean7);
        if (!boolean4) {
            bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt9).<Comparable, Direction>setValue((Property<Comparable>)TripWireHookBlock.FACING, fb9), 3);
            if (boolean5) {
                this.notifyNeighbors(bhr, ew, fb9);
            }
        }
        if (boolean6 != boolean8) {
            for (int integer4 = 1; integer4 < integer2; ++integer4) {
                final BlockPos ew3 = ew.relative(fb9, integer4);
                final BlockState bvt10 = arr15[integer4];
                if (bvt10 != null) {
                    bhr.setBlock(ew3, ((AbstractStateHolder<O, BlockState>)bvt10).<Comparable, Boolean>setValue((Property<Comparable>)TripWireHookBlock.ATTACHED, boolean8), 3);
                    if (!bhr.getBlockState(ew3).isAir()) {}
                }
            }
        }
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        this.calculateState(bhr, ew, bvt, false, true, -1, null);
    }
    
    private void playSound(final Level bhr, final BlockPos ew, final boolean boolean3, final boolean boolean4, final boolean boolean5, final boolean boolean6) {
        if (boolean4 && !boolean6) {
            bhr.playSound(null, ew, SoundEvents.TRIPWIRE_CLICK_ON, SoundSource.BLOCKS, 0.4f, 0.6f);
        }
        else if (!boolean4 && boolean6) {
            bhr.playSound(null, ew, SoundEvents.TRIPWIRE_CLICK_OFF, SoundSource.BLOCKS, 0.4f, 0.5f);
        }
        else if (boolean3 && !boolean5) {
            bhr.playSound(null, ew, SoundEvents.TRIPWIRE_ATTACH, SoundSource.BLOCKS, 0.4f, 0.7f);
        }
        else if (!boolean3 && boolean5) {
            bhr.playSound(null, ew, SoundEvents.TRIPWIRE_DETACH, SoundSource.BLOCKS, 0.4f, 1.2f / (bhr.random.nextFloat() * 0.2f + 0.9f));
        }
    }
    
    private void notifyNeighbors(final Level bhr, final BlockPos ew, final Direction fb) {
        bhr.updateNeighborsAt(ew, this);
        bhr.updateNeighborsAt(ew.relative(fb.getOpposite()), this);
    }
    
    @Override
    public void onRemove(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (boolean5 || bvt1.getBlock() == bvt4.getBlock()) {
            return;
        }
        final boolean boolean6 = bvt1.<Boolean>getValue((Property<Boolean>)TripWireHookBlock.ATTACHED);
        final boolean boolean7 = bvt1.<Boolean>getValue((Property<Boolean>)TripWireHookBlock.POWERED);
        if (boolean6 || boolean7) {
            this.calculateState(bhr, ew, bvt1, true, false, -1, null);
        }
        if (boolean7) {
            bhr.updateNeighborsAt(ew, this);
            bhr.updateNeighborsAt(ew.relative(bvt1.<Direction>getValue((Property<Direction>)TripWireHookBlock.FACING).getOpposite()), this);
        }
        super.onRemove(bvt1, bhr, ew, bvt4, boolean5);
    }
    
    @Override
    public int getSignal(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        return bvt.<Boolean>getValue((Property<Boolean>)TripWireHookBlock.POWERED) ? 15 : 0;
    }
    
    @Override
    public int getDirectSignal(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        if (!bvt.<Boolean>getValue((Property<Boolean>)TripWireHookBlock.POWERED)) {
            return 0;
        }
        if (bvt.<Comparable>getValue((Property<Comparable>)TripWireHookBlock.FACING) == fb) {
            return 15;
        }
        return 0;
    }
    
    @Override
    public boolean isSignalSource(final BlockState bvt) {
        return true;
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT_MIPPED;
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)TripWireHookBlock.FACING, brg.rotate(bvt.<Direction>getValue((Property<Direction>)TripWireHookBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        return bvt.rotate(bqg.getRotation(bvt.<Direction>getValue((Property<Direction>)TripWireHookBlock.FACING)));
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(TripWireHookBlock.FACING, TripWireHookBlock.POWERED, TripWireHookBlock.ATTACHED);
    }
    
    static {
        FACING = HorizontalDirectionalBlock.FACING;
        POWERED = BlockStateProperties.POWERED;
        ATTACHED = BlockStateProperties.ATTACHED;
        NORTH_AABB = Block.box(5.0, 0.0, 10.0, 11.0, 10.0, 16.0);
        SOUTH_AABB = Block.box(5.0, 0.0, 0.0, 11.0, 10.0, 6.0);
        WEST_AABB = Block.box(10.0, 0.0, 5.0, 16.0, 10.0, 11.0);
        EAST_AABB = Block.box(0.0, 0.0, 5.0, 6.0, 10.0, 11.0);
    }
}
