package net.minecraft.world.level.block;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.shapes.CollisionContext;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.effect.MobEffect;

public class WitherRoseBlock extends FlowerBlock {
    public WitherRoseBlock(final MobEffect aig, final Properties c) {
        super(aig, 8, c);
    }
    
    @Override
    protected boolean mayPlaceOn(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        final Block bmv5 = bvt.getBlock();
        return super.mayPlaceOn(bvt, bhb, ew) || bmv5 == Blocks.NETHERRACK || bmv5 == Blocks.SOUL_SAND;
    }
    
    @Override
    public void animateTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        final VoxelShape ctc6 = this.getShape(bvt, bhr, ew, CollisionContext.empty());
        final Vec3 csi7 = ctc6.bounds().getCenter();
        final double double8 = ew.getX() + csi7.x;
        final double double9 = ew.getZ() + csi7.z;
        for (int integer12 = 0; integer12 < 3; ++integer12) {
            if (random.nextBoolean()) {
                bhr.addParticle(ParticleTypes.SMOKE, double8 + random.nextFloat() / 5.0f, ew.getY() + (0.5 - random.nextFloat()), double9 + random.nextFloat() / 5.0f, 0.0, 0.0, 0.0);
            }
        }
    }
    
    @Override
    public void entityInside(final BlockState bvt, final Level bhr, final BlockPos ew, final Entity aio) {
        if (bhr.isClientSide || bhr.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }
        if (aio instanceof LivingEntity) {
            final LivingEntity aix6 = (LivingEntity)aio;
            if (!aix6.isInvulnerableTo(DamageSource.WITHER)) {
                aix6.addEffect(new MobEffectInstance(MobEffects.WITHER, 40));
            }
        }
    }
}
