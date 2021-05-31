package net.minecraft.world.level.block;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MyceliumBlock extends SpreadingSnowyDirtBlock {
    public MyceliumBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public void animateTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        super.animateTick(bvt, bhr, ew, random);
        if (random.nextInt(10) == 0) {
            bhr.addParticle(ParticleTypes.MYCELIUM, ew.getX() + random.nextFloat(), ew.getY() + 1.1f, ew.getZ() + random.nextFloat(), 0.0, 0.0, 0.0);
        }
    }
}
