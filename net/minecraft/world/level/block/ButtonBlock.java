package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import java.util.List;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.Entity;
import java.util.Random;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import javax.annotation.Nullable;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public abstract class ButtonBlock extends FaceAttachedHorizontalDirectionalBlock {
    public static final BooleanProperty POWERED;
    protected static final VoxelShape CEILING_AABB_X;
    protected static final VoxelShape CEILING_AABB_Z;
    protected static final VoxelShape FLOOR_AABB_X;
    protected static final VoxelShape FLOOR_AABB_Z;
    protected static final VoxelShape NORTH_AABB;
    protected static final VoxelShape SOUTH_AABB;
    protected static final VoxelShape WEST_AABB;
    protected static final VoxelShape EAST_AABB;
    protected static final VoxelShape PRESSED_CEILING_AABB_X;
    protected static final VoxelShape PRESSED_CEILING_AABB_Z;
    protected static final VoxelShape PRESSED_FLOOR_AABB_X;
    protected static final VoxelShape PRESSED_FLOOR_AABB_Z;
    protected static final VoxelShape PRESSED_NORTH_AABB;
    protected static final VoxelShape PRESSED_SOUTH_AABB;
    protected static final VoxelShape PRESSED_WEST_AABB;
    protected static final VoxelShape PRESSED_EAST_AABB;
    private final boolean sensitive;
    
    protected ButtonBlock(final boolean boolean1, final Properties c) {
        super(c);
        this.registerDefaultState(((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)ButtonBlock.FACING, Direction.NORTH)).setValue((Property<Comparable>)ButtonBlock.POWERED, false)).<AttachFace, AttachFace>setValue(ButtonBlock.FACE, AttachFace.WALL));
        this.sensitive = boolean1;
    }
    
    @Override
    public int getTickDelay(final LevelReader bhu) {
        return this.sensitive ? 30 : 20;
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        final Direction fb6 = bvt.<Direction>getValue((Property<Direction>)ButtonBlock.FACING);
        final boolean boolean7 = bvt.<Boolean>getValue((Property<Boolean>)ButtonBlock.POWERED);
        switch (bvt.<AttachFace>getValue(ButtonBlock.FACE)) {
            case FLOOR: {
                if (fb6.getAxis() == Direction.Axis.X) {
                    return boolean7 ? ButtonBlock.PRESSED_FLOOR_AABB_X : ButtonBlock.FLOOR_AABB_X;
                }
                return boolean7 ? ButtonBlock.PRESSED_FLOOR_AABB_Z : ButtonBlock.FLOOR_AABB_Z;
            }
            case WALL: {
                switch (fb6) {
                    case EAST: {
                        return boolean7 ? ButtonBlock.PRESSED_EAST_AABB : ButtonBlock.EAST_AABB;
                    }
                    case WEST: {
                        return boolean7 ? ButtonBlock.PRESSED_WEST_AABB : ButtonBlock.WEST_AABB;
                    }
                    case SOUTH: {
                        return boolean7 ? ButtonBlock.PRESSED_SOUTH_AABB : ButtonBlock.SOUTH_AABB;
                    }
                    default: {
                        return boolean7 ? ButtonBlock.PRESSED_NORTH_AABB : ButtonBlock.NORTH_AABB;
                    }
                }
                break;
            }
            default: {
                if (fb6.getAxis() == Direction.Axis.X) {
                    return boolean7 ? ButtonBlock.PRESSED_CEILING_AABB_X : ButtonBlock.CEILING_AABB_X;
                }
                return boolean7 ? ButtonBlock.PRESSED_CEILING_AABB_Z : ButtonBlock.CEILING_AABB_Z;
            }
        }
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        if (bvt.<Boolean>getValue((Property<Boolean>)ButtonBlock.POWERED)) {
            return true;
        }
        bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)ButtonBlock.POWERED, true), 3);
        this.playSound(awg, bhr, ew, true);
        this.updateNeighbours(bvt, bhr, ew);
        bhr.getBlockTicks().scheduleTick(ew, this, this.getTickDelay(bhr));
        return true;
    }
    
    protected void playSound(@Nullable final Player awg, final LevelAccessor bhs, final BlockPos ew, final boolean boolean4) {
        bhs.playSound(boolean4 ? awg : null, ew, this.getSound(boolean4), SoundSource.BLOCKS, 0.3f, boolean4 ? 0.6f : 0.5f);
    }
    
    protected abstract SoundEvent getSound(final boolean boolean1);
    
    @Override
    public void onRemove(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (boolean5 || bvt1.getBlock() == bvt4.getBlock()) {
            return;
        }
        if (bvt1.<Boolean>getValue((Property<Boolean>)ButtonBlock.POWERED)) {
            this.updateNeighbours(bvt1, bhr, ew);
        }
        super.onRemove(bvt1, bhr, ew, bvt4, boolean5);
    }
    
    @Override
    public int getSignal(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        return bvt.<Boolean>getValue((Property<Boolean>)ButtonBlock.POWERED) ? 15 : 0;
    }
    
    @Override
    public int getDirectSignal(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        if (bvt.<Boolean>getValue((Property<Boolean>)ButtonBlock.POWERED) && FaceAttachedHorizontalDirectionalBlock.getConnectedDirection(bvt) == fb) {
            return 15;
        }
        return 0;
    }
    
    @Override
    public boolean isSignalSource(final BlockState bvt) {
        return true;
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (bhr.isClientSide || !bvt.<Boolean>getValue((Property<Boolean>)ButtonBlock.POWERED)) {
            return;
        }
        if (this.sensitive) {
            this.checkPressed(bvt, bhr, ew);
        }
        else {
            bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)ButtonBlock.POWERED, false), 3);
            this.updateNeighbours(bvt, bhr, ew);
            this.playSound(null, bhr, ew, false);
        }
    }
    
    @Override
    public void entityInside(final BlockState bvt, final Level bhr, final BlockPos ew, final Entity aio) {
        if (bhr.isClientSide || !this.sensitive || bvt.<Boolean>getValue((Property<Boolean>)ButtonBlock.POWERED)) {
            return;
        }
        this.checkPressed(bvt, bhr, ew);
    }
    
    private void checkPressed(final BlockState bvt, final Level bhr, final BlockPos ew) {
        final List<? extends Entity> list5 = bhr.getEntitiesOfClass((java.lang.Class<? extends Entity>)AbstractArrow.class, bvt.getShape(bhr, ew).bounds().move(ew));
        final boolean boolean6 = !list5.isEmpty();
        final boolean boolean7 = bvt.<Boolean>getValue((Property<Boolean>)ButtonBlock.POWERED);
        if (boolean6 != boolean7) {
            bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)ButtonBlock.POWERED, boolean6), 3);
            this.updateNeighbours(bvt, bhr, ew);
            this.playSound(null, bhr, ew, boolean6);
        }
        if (boolean6) {
            bhr.getBlockTicks().scheduleTick(new BlockPos(ew), this, this.getTickDelay(bhr));
        }
    }
    
    private void updateNeighbours(final BlockState bvt, final Level bhr, final BlockPos ew) {
        bhr.updateNeighborsAt(ew, this);
        bhr.updateNeighborsAt(ew.relative(FaceAttachedHorizontalDirectionalBlock.getConnectedDirection(bvt).getOpposite()), this);
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(ButtonBlock.FACING, ButtonBlock.POWERED, ButtonBlock.FACE);
    }
    
    static {
        POWERED = BlockStateProperties.POWERED;
        CEILING_AABB_X = Block.box(6.0, 14.0, 5.0, 10.0, 16.0, 11.0);
        CEILING_AABB_Z = Block.box(5.0, 14.0, 6.0, 11.0, 16.0, 10.0);
        FLOOR_AABB_X = Block.box(6.0, 0.0, 5.0, 10.0, 2.0, 11.0);
        FLOOR_AABB_Z = Block.box(5.0, 0.0, 6.0, 11.0, 2.0, 10.0);
        NORTH_AABB = Block.box(5.0, 6.0, 14.0, 11.0, 10.0, 16.0);
        SOUTH_AABB = Block.box(5.0, 6.0, 0.0, 11.0, 10.0, 2.0);
        WEST_AABB = Block.box(14.0, 6.0, 5.0, 16.0, 10.0, 11.0);
        EAST_AABB = Block.box(0.0, 6.0, 5.0, 2.0, 10.0, 11.0);
        PRESSED_CEILING_AABB_X = Block.box(6.0, 15.0, 5.0, 10.0, 16.0, 11.0);
        PRESSED_CEILING_AABB_Z = Block.box(5.0, 15.0, 6.0, 11.0, 16.0, 10.0);
        PRESSED_FLOOR_AABB_X = Block.box(6.0, 0.0, 5.0, 10.0, 1.0, 11.0);
        PRESSED_FLOOR_AABB_Z = Block.box(5.0, 0.0, 6.0, 11.0, 1.0, 10.0);
        PRESSED_NORTH_AABB = Block.box(5.0, 6.0, 15.0, 11.0, 10.0, 16.0);
        PRESSED_SOUTH_AABB = Block.box(5.0, 6.0, 0.0, 11.0, 10.0, 1.0);
        PRESSED_WEST_AABB = Block.box(15.0, 6.0, 5.0, 16.0, 10.0, 11.0);
        PRESSED_EAST_AABB = Block.box(0.0, 6.0, 5.0, 1.0, 10.0, 11.0);
    }
}
