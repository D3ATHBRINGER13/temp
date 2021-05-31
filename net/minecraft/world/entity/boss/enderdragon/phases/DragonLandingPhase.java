package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.levelgen.Heightmap;
import java.util.Random;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.phys.Vec3;

public class DragonLandingPhase extends AbstractDragonPhaseInstance {
    private Vec3 targetLocation;
    
    public DragonLandingPhase(final EnderDragon asp) {
        super(asp);
    }
    
    @Override
    public void doClientTick() {
        final Vec3 csi2 = this.dragon.getHeadLookVector(1.0f).normalize();
        csi2.yRot(-0.7853982f);
        final double double3 = this.dragon.head.x;
        final double double4 = this.dragon.head.y + this.dragon.head.getBbHeight() / 2.0f;
        final double double5 = this.dragon.head.z;
        for (int integer9 = 0; integer9 < 8; ++integer9) {
            final Random random10 = this.dragon.getRandom();
            final double double6 = double3 + random10.nextGaussian() / 2.0;
            final double double7 = double4 + random10.nextGaussian() / 2.0;
            final double double8 = double5 + random10.nextGaussian() / 2.0;
            final Vec3 csi3 = this.dragon.getDeltaMovement();
            this.dragon.level.addParticle(ParticleTypes.DRAGON_BREATH, double6, double7, double8, -csi2.x * 0.07999999821186066 + csi3.x, -csi2.y * 0.30000001192092896 + csi3.y, -csi2.z * 0.07999999821186066 + csi3.z);
            csi2.yRot(0.19634955f);
        }
    }
    
    @Override
    public void doServerTick() {
        if (this.targetLocation == null) {
            this.targetLocation = new Vec3(this.dragon.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION));
        }
        if (this.targetLocation.distanceToSqr(this.dragon.x, this.dragon.y, this.dragon.z) < 1.0) {
            this.dragon.getPhaseManager().<DragonSittingFlamingPhase>getPhase(EnderDragonPhase.SITTING_FLAMING).resetFlameCount();
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.SITTING_SCANNING);
        }
    }
    
    @Override
    public float getFlySpeed() {
        return 1.5f;
    }
    
    @Override
    public float getTurnSpeed() {
        final float float2 = Mth.sqrt(Entity.getHorizontalDistanceSqr(this.dragon.getDeltaMovement())) + 1.0f;
        final float float3 = Math.min(float2, 40.0f);
        return float3 / float2;
    }
    
    @Override
    public void begin() {
        this.targetLocation = null;
    }
    
    @Nullable
    @Override
    public Vec3 getFlyTargetLocation() {
        return this.targetLocation;
    }
    
    public EnderDragonPhase<DragonLandingPhase> getPhase() {
        return EnderDragonPhase.LANDING;
    }
}
