package net.minecraft.world.level.block;

import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DragonEggBlock extends FallingBlock {
    protected static final VoxelShape SHAPE;
    
    public DragonEggBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return DragonEggBlock.SHAPE;
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        this.teleport(bvt, bhr, ew);
        return true;
    }
    
    @Override
    public void attack(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg) {
        this.teleport(bvt, bhr, ew);
    }
    
    private void teleport(final BlockState bvt, final Level bhr, final BlockPos ew) {
        for (int integer5 = 0; integer5 < 1000; ++integer5) {
            final BlockPos ew2 = ew.offset(bhr.random.nextInt(16) - bhr.random.nextInt(16), bhr.random.nextInt(8) - bhr.random.nextInt(8), bhr.random.nextInt(16) - bhr.random.nextInt(16));
            if (bhr.getBlockState(ew2).isAir()) {
                if (bhr.isClientSide) {
                    for (int integer6 = 0; integer6 < 128; ++integer6) {
                        final double double8 = bhr.random.nextDouble();
                        final float float10 = (bhr.random.nextFloat() - 0.5f) * 0.2f;
                        final float float11 = (bhr.random.nextFloat() - 0.5f) * 0.2f;
                        final float float12 = (bhr.random.nextFloat() - 0.5f) * 0.2f;
                        final double double9 = Mth.lerp(double8, ew2.getX(), ew.getX()) + (bhr.random.nextDouble() - 0.5) + 0.5;
                        final double double10 = Mth.lerp(double8, ew2.getY(), ew.getY()) + bhr.random.nextDouble() - 0.5;
                        final double double11 = Mth.lerp(double8, ew2.getZ(), ew.getZ()) + (bhr.random.nextDouble() - 0.5) + 0.5;
                        bhr.addParticle(ParticleTypes.PORTAL, double9, double10, double11, float10, float11, float12);
                    }
                }
                else {
                    bhr.setBlock(ew2, bvt, 2);
                    bhr.removeBlock(ew, false);
                }
                return;
            }
        }
    }
    
    @Override
    public int getTickDelay(final LevelReader bhu) {
        return 5;
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return false;
    }
    
    static {
        SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
    }
}
