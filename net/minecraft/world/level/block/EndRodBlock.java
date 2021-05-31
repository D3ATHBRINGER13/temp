package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EndRodBlock extends DirectionalBlock {
    protected static final VoxelShape Y_AXIS_AABB;
    protected static final VoxelShape Z_AXIS_AABB;
    protected static final VoxelShape X_AXIS_AABB;
    
    protected EndRodBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Direction>setValue((Property<Comparable>)EndRodBlock.FACING, Direction.UP));
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)EndRodBlock.FACING, brg.rotate(bvt.<Direction>getValue((Property<Direction>)EndRodBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)EndRodBlock.FACING, bqg.mirror(bvt.<Direction>getValue((Property<Direction>)EndRodBlock.FACING)));
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        switch (bvt.<Direction>getValue((Property<Direction>)EndRodBlock.FACING).getAxis()) {
            default: {
                return EndRodBlock.X_AXIS_AABB;
            }
            case Z: {
                return EndRodBlock.Z_AXIS_AABB;
            }
            case Y: {
                return EndRodBlock.Y_AXIS_AABB;
            }
        }
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final Direction fb3 = ban.getClickedFace();
        final BlockState bvt4 = ban.getLevel().getBlockState(ban.getClickedPos().relative(fb3.getOpposite()));
        if (bvt4.getBlock() == this && bvt4.<Comparable>getValue((Property<Comparable>)EndRodBlock.FACING) == fb3) {
            return ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)EndRodBlock.FACING, fb3.getOpposite());
        }
        return ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)EndRodBlock.FACING, fb3);
    }
    
    @Override
    public void animateTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        final Direction fb6 = bvt.<Direction>getValue((Property<Direction>)EndRodBlock.FACING);
        final double double7 = ew.getX() + 0.55 - random.nextFloat() * 0.1f;
        final double double8 = ew.getY() + 0.55 - random.nextFloat() * 0.1f;
        final double double9 = ew.getZ() + 0.55 - random.nextFloat() * 0.1f;
        final double double10 = 0.4f - (random.nextFloat() + random.nextFloat()) * 0.4f;
        if (random.nextInt(5) == 0) {
            bhr.addParticle(ParticleTypes.END_ROD, double7 + fb6.getStepX() * double10, double8 + fb6.getStepY() * double10, double9 + fb6.getStepZ() * double10, random.nextGaussian() * 0.005, random.nextGaussian() * 0.005, random.nextGaussian() * 0.005);
        }
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(EndRodBlock.FACING);
    }
    
    @Override
    public PushReaction getPistonPushReaction(final BlockState bvt) {
        return PushReaction.NORMAL;
    }
    
    static {
        Y_AXIS_AABB = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
        Z_AXIS_AABB = Block.box(6.0, 6.0, 0.0, 10.0, 10.0, 16.0);
        X_AXIS_AABB = Block.box(0.0, 6.0, 6.0, 16.0, 10.0, 10.0);
    }
}
