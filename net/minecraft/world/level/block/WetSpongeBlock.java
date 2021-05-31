package net.minecraft.world.level.block;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.Direction;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class WetSpongeBlock extends Block {
    protected WetSpongeBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public void animateTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        final Direction fb6 = Direction.getRandomFace(random);
        if (fb6 == Direction.UP) {
            return;
        }
        final BlockPos ew2 = ew.relative(fb6);
        final BlockState bvt2 = bhr.getBlockState(ew2);
        if (bvt.canOcclude() && bvt2.isFaceSturdy(bhr, ew2, fb6.getOpposite())) {
            return;
        }
        double double9 = ew.getX();
        double double10 = ew.getY();
        double double11 = ew.getZ();
        if (fb6 == Direction.DOWN) {
            double10 -= 0.05;
            double9 += random.nextDouble();
            double11 += random.nextDouble();
        }
        else {
            double10 += random.nextDouble() * 0.8;
            if (fb6.getAxis() == Direction.Axis.X) {
                double11 += random.nextDouble();
                if (fb6 == Direction.EAST) {
                    double9 += 1.1;
                }
                else {
                    double9 += 0.05;
                }
            }
            else {
                double9 += random.nextDouble();
                if (fb6 == Direction.SOUTH) {
                    double11 += 1.1;
                }
                else {
                    double11 += 0.05;
                }
            }
        }
        bhr.addParticle(ParticleTypes.DRIPPING_WATER, double9, double10, double11, 0.0, 0.0, 0.0);
    }
}
