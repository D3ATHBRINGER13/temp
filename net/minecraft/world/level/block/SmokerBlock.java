package net.minecraft.world.level.block;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.properties.Property;
import java.util.Random;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.stats.Stats;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SmokerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;

public class SmokerBlock extends AbstractFurnaceBlock {
    protected SmokerBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        return new SmokerBlockEntity();
    }
    
    @Override
    protected void openContainer(final Level bhr, final BlockPos ew, final Player awg) {
        final BlockEntity btw5 = bhr.getBlockEntity(ew);
        if (btw5 instanceof SmokerBlockEntity) {
            awg.openMenu((MenuProvider)btw5);
            awg.awardStat(Stats.INTERACT_WITH_SMOKER);
        }
    }
    
    @Override
    public void animateTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (!bvt.<Boolean>getValue((Property<Boolean>)SmokerBlock.LIT)) {
            return;
        }
        final double double6 = ew.getX() + 0.5;
        final double double7 = ew.getY();
        final double double8 = ew.getZ() + 0.5;
        if (random.nextDouble() < 0.1) {
            bhr.playLocalSound(double6, double7, double8, SoundEvents.SMOKER_SMOKE, SoundSource.BLOCKS, 1.0f, 1.0f, false);
        }
        bhr.addParticle(ParticleTypes.SMOKE, double6, double7 + 1.1, double8, 0.0, 0.0, 0.0);
    }
}
