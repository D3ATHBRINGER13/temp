package net.minecraft.world.level.block;

import net.minecraft.world.level.material.PushReaction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BasePressurePlateBlock extends Block {
    protected static final VoxelShape PRESSED_AABB;
    protected static final VoxelShape AABB;
    protected static final AABB TOUCH_AABB;
    
    protected BasePressurePlateBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return (this.getSignalForState(bvt) > 0) ? BasePressurePlateBlock.PRESSED_AABB : BasePressurePlateBlock.AABB;
    }
    
    @Override
    public int getTickDelay(final LevelReader bhu) {
        return 20;
    }
    
    @Override
    public boolean isPossibleToRespawnInThis() {
        return true;
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (fb == Direction.DOWN && !bvt1.canSurvive(bhs, ew5)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        final BlockPos ew2 = ew.below();
        return Block.canSupportRigidBlock(bhu, ew2) || Block.canSupportCenter(bhu, ew2, Direction.UP);
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (bhr.isClientSide) {
            return;
        }
        final int integer6 = this.getSignalForState(bvt);
        if (integer6 > 0) {
            this.checkPressed(bhr, ew, bvt, integer6);
        }
    }
    
    @Override
    public void entityInside(final BlockState bvt, final Level bhr, final BlockPos ew, final Entity aio) {
        if (bhr.isClientSide) {
            return;
        }
        final int integer6 = this.getSignalForState(bvt);
        if (integer6 == 0) {
            this.checkPressed(bhr, ew, bvt, integer6);
        }
    }
    
    protected void checkPressed(final Level bhr, final BlockPos ew, final BlockState bvt, final int integer) {
        final int integer2 = this.getSignalStrength(bhr, ew);
        final boolean boolean7 = integer > 0;
        final boolean boolean8 = integer2 > 0;
        if (integer != integer2) {
            final BlockState bvt2 = this.setSignalForState(bvt, integer2);
            bhr.setBlock(ew, bvt2, 2);
            this.updateNeighbours(bhr, ew);
            bhr.setBlocksDirty(ew, bvt, bvt2);
        }
        if (!boolean8 && boolean7) {
            this.playOffSound(bhr, ew);
        }
        else if (boolean8 && !boolean7) {
            this.playOnSound(bhr, ew);
        }
        if (boolean8) {
            bhr.getBlockTicks().scheduleTick(new BlockPos(ew), this, this.getTickDelay(bhr));
        }
    }
    
    protected abstract void playOnSound(final LevelAccessor bhs, final BlockPos ew);
    
    protected abstract void playOffSound(final LevelAccessor bhs, final BlockPos ew);
    
    @Override
    public void onRemove(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (boolean5 || bvt1.getBlock() == bvt4.getBlock()) {
            return;
        }
        if (this.getSignalForState(bvt1) > 0) {
            this.updateNeighbours(bhr, ew);
        }
        super.onRemove(bvt1, bhr, ew, bvt4, boolean5);
    }
    
    protected void updateNeighbours(final Level bhr, final BlockPos ew) {
        bhr.updateNeighborsAt(ew, this);
        bhr.updateNeighborsAt(ew.below(), this);
    }
    
    @Override
    public int getSignal(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        return this.getSignalForState(bvt);
    }
    
    @Override
    public int getDirectSignal(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        if (fb == Direction.UP) {
            return this.getSignalForState(bvt);
        }
        return 0;
    }
    
    @Override
    public boolean isSignalSource(final BlockState bvt) {
        return true;
    }
    
    @Override
    public PushReaction getPistonPushReaction(final BlockState bvt) {
        return PushReaction.DESTROY;
    }
    
    protected abstract int getSignalStrength(final Level bhr, final BlockPos ew);
    
    protected abstract int getSignalForState(final BlockState bvt);
    
    protected abstract BlockState setSignalForState(final BlockState bvt, final int integer);
    
    static {
        PRESSED_AABB = Block.box(1.0, 0.0, 1.0, 15.0, 0.5, 15.0);
        AABB = Block.box(1.0, 0.0, 1.0, 15.0, 1.0, 15.0);
        TOUCH_AABB = new AABB(0.125, 0.0, 0.125, 0.875, 0.25, 0.875);
    }
}
