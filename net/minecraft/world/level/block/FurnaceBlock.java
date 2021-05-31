package net.minecraft.world.level.block;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;

public class FurnaceBlock extends AbstractFurnaceBlock {
    protected FurnaceBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        return new FurnaceBlockEntity();
    }
    
    @Override
    protected void openContainer(final Level bhr, final BlockPos ew, final Player awg) {
        final BlockEntity btw5 = bhr.getBlockEntity(ew);
        if (btw5 instanceof FurnaceBlockEntity) {
            awg.openMenu((MenuProvider)btw5);
            awg.awardStat(Stats.INTERACT_WITH_FURNACE);
        }
    }
    
    @Override
    public void animateTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (!bvt.<Boolean>getValue((Property<Boolean>)FurnaceBlock.LIT)) {
            return;
        }
        final double double6 = ew.getX() + 0.5;
        final double double7 = ew.getY();
        final double double8 = ew.getZ() + 0.5;
        if (random.nextDouble() < 0.1) {
            bhr.playLocalSound(double6, double7, double8, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0f, 1.0f, false);
        }
        final Direction fb12 = bvt.<Direction>getValue((Property<Direction>)FurnaceBlock.FACING);
        final Direction.Axis a13 = fb12.getAxis();
        final double double9 = 0.52;
        final double double10 = random.nextDouble() * 0.6 - 0.3;
        final double double11 = (a13 == Direction.Axis.X) ? (fb12.getStepX() * 0.52) : double10;
        final double double12 = random.nextDouble() * 6.0 / 16.0;
        final double double13 = (a13 == Direction.Axis.Z) ? (fb12.getStepZ() * 0.52) : double10;
        bhr.addParticle(ParticleTypes.SMOKE, double6 + double11, double7 + double12, double8 + double13, 0.0, 0.0, 0.0);
        bhr.addParticle(ParticleTypes.FLAME, double6 + double11, double7 + double12, double8 + double13, 0.0, 0.0, 0.0);
    }
}
