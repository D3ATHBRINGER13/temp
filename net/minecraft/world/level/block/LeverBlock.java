package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import java.util.Random;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class LeverBlock extends FaceAttachedHorizontalDirectionalBlock {
    public static final BooleanProperty POWERED;
    protected static final VoxelShape NORTH_AABB;
    protected static final VoxelShape SOUTH_AABB;
    protected static final VoxelShape WEST_AABB;
    protected static final VoxelShape EAST_AABB;
    protected static final VoxelShape UP_AABB_Z;
    protected static final VoxelShape UP_AABB_X;
    protected static final VoxelShape DOWN_AABB_Z;
    protected static final VoxelShape DOWN_AABB_X;
    
    protected LeverBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)LeverBlock.FACING, Direction.NORTH)).setValue((Property<Comparable>)LeverBlock.POWERED, false)).<AttachFace, AttachFace>setValue(LeverBlock.FACE, AttachFace.WALL));
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        switch (bvt.<AttachFace>getValue(LeverBlock.FACE)) {
            case FLOOR: {
                switch (bvt.<Direction>getValue((Property<Direction>)LeverBlock.FACING).getAxis()) {
                    case X: {
                        return LeverBlock.UP_AABB_X;
                    }
                    default: {
                        return LeverBlock.UP_AABB_Z;
                    }
                }
                break;
            }
            case WALL: {
                switch (bvt.<Direction>getValue((Property<Direction>)LeverBlock.FACING)) {
                    case EAST: {
                        return LeverBlock.EAST_AABB;
                    }
                    case WEST: {
                        return LeverBlock.WEST_AABB;
                    }
                    case SOUTH: {
                        return LeverBlock.SOUTH_AABB;
                    }
                    default: {
                        return LeverBlock.NORTH_AABB;
                    }
                }
                break;
            }
            default: {
                switch (bvt.<Direction>getValue((Property<Direction>)LeverBlock.FACING).getAxis()) {
                    case X: {
                        return LeverBlock.DOWN_AABB_X;
                    }
                    default: {
                        return LeverBlock.DOWN_AABB_Z;
                    }
                }
                break;
            }
        }
    }
    
    @Override
    public boolean use(BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        bvt = ((AbstractStateHolder<O, BlockState>)bvt).<Comparable>cycle((Property<Comparable>)LeverBlock.POWERED);
        final boolean boolean8 = bvt.<Boolean>getValue((Property<Boolean>)LeverBlock.POWERED);
        if (bhr.isClientSide) {
            if (boolean8) {
                makeParticle(bvt, bhr, ew, 1.0f);
            }
            return true;
        }
        bhr.setBlock(ew, bvt, 3);
        final float float9 = boolean8 ? 0.6f : 0.5f;
        bhr.playSound(null, ew, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3f, float9);
        this.updateNeighbours(bvt, bhr, ew);
        return true;
    }
    
    private static void makeParticle(final BlockState bvt, final LevelAccessor bhs, final BlockPos ew, final float float4) {
        final Direction fb5 = bvt.<Direction>getValue((Property<Direction>)LeverBlock.FACING).getOpposite();
        final Direction fb6 = FaceAttachedHorizontalDirectionalBlock.getConnectedDirection(bvt).getOpposite();
        final double double7 = ew.getX() + 0.5 + 0.1 * fb5.getStepX() + 0.2 * fb6.getStepX();
        final double double8 = ew.getY() + 0.5 + 0.1 * fb5.getStepY() + 0.2 * fb6.getStepY();
        final double double9 = ew.getZ() + 0.5 + 0.1 * fb5.getStepZ() + 0.2 * fb6.getStepZ();
        bhs.addParticle(new DustParticleOptions(1.0f, 0.0f, 0.0f, float4), double7, double8, double9, 0.0, 0.0, 0.0);
    }
    
    @Override
    public void animateTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (bvt.<Boolean>getValue((Property<Boolean>)LeverBlock.POWERED) && random.nextFloat() < 0.25f) {
            makeParticle(bvt, bhr, ew, 0.5f);
        }
    }
    
    @Override
    public void onRemove(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (boolean5 || bvt1.getBlock() == bvt4.getBlock()) {
            return;
        }
        if (bvt1.<Boolean>getValue((Property<Boolean>)LeverBlock.POWERED)) {
            this.updateNeighbours(bvt1, bhr, ew);
        }
        super.onRemove(bvt1, bhr, ew, bvt4, boolean5);
    }
    
    @Override
    public int getSignal(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        return bvt.<Boolean>getValue((Property<Boolean>)LeverBlock.POWERED) ? 15 : 0;
    }
    
    @Override
    public int getDirectSignal(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        if (bvt.<Boolean>getValue((Property<Boolean>)LeverBlock.POWERED) && FaceAttachedHorizontalDirectionalBlock.getConnectedDirection(bvt) == fb) {
            return 15;
        }
        return 0;
    }
    
    @Override
    public boolean isSignalSource(final BlockState bvt) {
        return true;
    }
    
    private void updateNeighbours(final BlockState bvt, final Level bhr, final BlockPos ew) {
        bhr.updateNeighborsAt(ew, this);
        bhr.updateNeighborsAt(ew.relative(FaceAttachedHorizontalDirectionalBlock.getConnectedDirection(bvt).getOpposite()), this);
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(LeverBlock.FACE, LeverBlock.FACING, LeverBlock.POWERED);
    }
    
    static {
        POWERED = BlockStateProperties.POWERED;
        NORTH_AABB = Block.box(5.0, 4.0, 10.0, 11.0, 12.0, 16.0);
        SOUTH_AABB = Block.box(5.0, 4.0, 0.0, 11.0, 12.0, 6.0);
        WEST_AABB = Block.box(10.0, 4.0, 5.0, 16.0, 12.0, 11.0);
        EAST_AABB = Block.box(0.0, 4.0, 5.0, 6.0, 12.0, 11.0);
        UP_AABB_Z = Block.box(5.0, 0.0, 4.0, 11.0, 6.0, 12.0);
        UP_AABB_X = Block.box(4.0, 0.0, 5.0, 12.0, 6.0, 11.0);
        DOWN_AABB_Z = Block.box(5.0, 10.0, 4.0, 11.0, 16.0, 12.0);
        DOWN_AABB_X = Block.box(4.0, 10.0, 5.0, 12.0, 16.0, 11.0);
    }
}
