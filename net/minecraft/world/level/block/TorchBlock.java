package net.minecraft.world.level.block;

import net.minecraft.world.level.BlockLayer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TorchBlock extends Block {
    protected static final VoxelShape AABB;
    
    protected TorchBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return TorchBlock.AABB;
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (fb == Direction.DOWN && !this.canSurvive(bvt1, bhs, ew5)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        return Block.canSupportCenter(bhu, ew.below(), Direction.UP);
    }
    
    @Override
    public void animateTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        final double double6 = ew.getX() + 0.5;
        final double double7 = ew.getY() + 0.7;
        final double double8 = ew.getZ() + 0.5;
        bhr.addParticle(ParticleTypes.SMOKE, double6, double7, double8, 0.0, 0.0, 0.0);
        bhr.addParticle(ParticleTypes.FLAME, double6, double7, double8, 0.0, 0.0, 0.0);
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    static {
        AABB = Block.box(6.0, 0.0, 6.0, 10.0, 10.0, 10.0);
    }
}
