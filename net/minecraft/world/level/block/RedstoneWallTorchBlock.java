package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.DustParticleOptions;
import java.util.Random;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class RedstoneWallTorchBlock extends RedstoneTorchBlock {
    public static final DirectionProperty FACING;
    public static final BooleanProperty LIT;
    
    protected RedstoneWallTorchBlock(final Properties c) {
        super(c);
        this.registerDefaultState((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)RedstoneWallTorchBlock.FACING, Direction.NORTH)).<Comparable, Boolean>setValue((Property<Comparable>)RedstoneWallTorchBlock.LIT, true));
    }
    
    @Override
    public String getDescriptionId() {
        return this.asItem().getDescriptionId();
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return WallTorchBlock.getShape(bvt);
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        return Blocks.WALL_TORCH.canSurvive(bvt, bhu, ew);
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        return Blocks.WALL_TORCH.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final BlockState bvt3 = Blocks.WALL_TORCH.getStateForPlacement(ban);
        return (bvt3 == null) ? null : ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Comparable>setValue((Property<Comparable>)RedstoneWallTorchBlock.FACING, (Comparable)bvt3.<V>getValue((Property<V>)RedstoneWallTorchBlock.FACING));
    }
    
    @Override
    public void animateTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (!bvt.<Boolean>getValue((Property<Boolean>)RedstoneWallTorchBlock.LIT)) {
            return;
        }
        final Direction fb6 = bvt.<Direction>getValue((Property<Direction>)RedstoneWallTorchBlock.FACING).getOpposite();
        final double double7 = 0.27;
        final double double8 = ew.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2 + 0.27 * fb6.getStepX();
        final double double9 = ew.getY() + 0.7 + (random.nextDouble() - 0.5) * 0.2 + 0.22;
        final double double10 = ew.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2 + 0.27 * fb6.getStepZ();
        bhr.addParticle(DustParticleOptions.REDSTONE, double8, double9, double10, 0.0, 0.0, 0.0);
    }
    
    @Override
    protected boolean hasNeighborSignal(final Level bhr, final BlockPos ew, final BlockState bvt) {
        final Direction fb5 = bvt.<Direction>getValue((Property<Direction>)RedstoneWallTorchBlock.FACING).getOpposite();
        return bhr.hasSignal(ew.relative(fb5), fb5);
    }
    
    @Override
    public int getSignal(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        if (bvt.<Boolean>getValue((Property<Boolean>)RedstoneWallTorchBlock.LIT) && bvt.<Comparable>getValue((Property<Comparable>)RedstoneWallTorchBlock.FACING) != fb) {
            return 15;
        }
        return 0;
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return Blocks.WALL_TORCH.rotate(bvt, brg);
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        return Blocks.WALL_TORCH.mirror(bvt, bqg);
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(RedstoneWallTorchBlock.FACING, RedstoneWallTorchBlock.LIT);
    }
    
    static {
        FACING = HorizontalDirectionalBlock.FACING;
        LIT = RedstoneTorchBlock.LIT;
    }
}
