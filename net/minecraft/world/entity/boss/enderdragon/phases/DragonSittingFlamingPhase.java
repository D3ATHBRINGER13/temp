package net.minecraft.world.entity.boss.enderdragon.phases;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Mth;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.AreaEffectCloud;

public class DragonSittingFlamingPhase extends AbstractDragonSittingPhase {
    private int flameTicks;
    private int flameCount;
    private AreaEffectCloud flame;
    
    public DragonSittingFlamingPhase(final EnderDragon asp) {
        super(asp);
    }
    
    @Override
    public void doClientTick() {
        ++this.flameTicks;
        if (this.flameTicks % 2 == 0 && this.flameTicks < 10) {
            final Vec3 csi2 = this.dragon.getHeadLookVector(1.0f).normalize();
            csi2.yRot(-0.7853982f);
            final double double3 = this.dragon.head.x;
            final double double4 = this.dragon.head.y + this.dragon.head.getBbHeight() / 2.0f;
            final double double5 = this.dragon.head.z;
            for (int integer9 = 0; integer9 < 8; ++integer9) {
                final double double6 = double3 + this.dragon.getRandom().nextGaussian() / 2.0;
                final double double7 = double4 + this.dragon.getRandom().nextGaussian() / 2.0;
                final double double8 = double5 + this.dragon.getRandom().nextGaussian() / 2.0;
                for (int integer10 = 0; integer10 < 6; ++integer10) {
                    this.dragon.level.addParticle(ParticleTypes.DRAGON_BREATH, double6, double7, double8, -csi2.x * 0.07999999821186066 * integer10, -csi2.y * 0.6000000238418579, -csi2.z * 0.07999999821186066 * integer10);
                }
                csi2.yRot(0.19634955f);
            }
        }
    }
    
    @Override
    public void doServerTick() {
        ++this.flameTicks;
        if (this.flameTicks >= 200) {
            if (this.flameCount >= 4) {
                this.dragon.getPhaseManager().setPhase(EnderDragonPhase.TAKEOFF);
            }
            else {
                this.dragon.getPhaseManager().setPhase(EnderDragonPhase.SITTING_SCANNING);
            }
        }
        else if (this.flameTicks == 10) {
            final Vec3 csi2 = new Vec3(this.dragon.head.x - this.dragon.x, 0.0, this.dragon.head.z - this.dragon.z).normalize();
            final float float3 = 5.0f;
            final double double4 = this.dragon.head.x + csi2.x * 5.0 / 2.0;
            final double double5 = this.dragon.head.z + csi2.z * 5.0 / 2.0;
            double double6 = this.dragon.head.y + this.dragon.head.getBbHeight() / 2.0f;
            final BlockPos.MutableBlockPos a10 = new BlockPos.MutableBlockPos(double4, double6, double5);
            while (this.dragon.level.isEmptyBlock(a10)) {
                --double6;
                a10.set(double4, double6, double5);
            }
            double6 = Mth.floor(double6) + 1;
            (this.flame = new AreaEffectCloud(this.dragon.level, double4, double6, double5)).setOwner(this.dragon);
            this.flame.setRadius(5.0f);
            this.flame.setDuration(200);
            this.flame.setParticle(ParticleTypes.DRAGON_BREATH);
            this.flame.addEffect(new MobEffectInstance(MobEffects.HARM));
            this.dragon.level.addFreshEntity(this.flame);
        }
    }
    
    @Override
    public void begin() {
        this.flameTicks = 0;
        ++this.flameCount;
    }
    
    @Override
    public void end() {
        if (this.flame != null) {
            this.flame.remove();
            this.flame = null;
        }
    }
    
    public EnderDragonPhase<DragonSittingFlamingPhase> getPhase() {
        return EnderDragonPhase.SITTING_FLAMING;
    }
    
    public void resetFlameCount() {
        this.flameCount = 0;
    }
}
