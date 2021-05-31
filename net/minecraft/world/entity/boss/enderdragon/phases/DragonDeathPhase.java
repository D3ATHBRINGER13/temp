package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.phys.Vec3;

public class DragonDeathPhase extends AbstractDragonPhaseInstance {
    private Vec3 targetLocation;
    private int time;
    
    public DragonDeathPhase(final EnderDragon asp) {
        super(asp);
    }
    
    @Override
    public void doClientTick() {
        if (this.time++ % 10 == 0) {
            final float float2 = (this.dragon.getRandom().nextFloat() - 0.5f) * 8.0f;
            final float float3 = (this.dragon.getRandom().nextFloat() - 0.5f) * 4.0f;
            final float float4 = (this.dragon.getRandom().nextFloat() - 0.5f) * 8.0f;
            this.dragon.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.dragon.x + float2, this.dragon.y + 2.0 + float3, this.dragon.z + float4, 0.0, 0.0, 0.0);
        }
    }
    
    @Override
    public void doServerTick() {
        ++this.time;
        if (this.targetLocation == null) {
            final BlockPos ew2 = this.dragon.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, EndPodiumFeature.END_PODIUM_LOCATION);
            this.targetLocation = new Vec3(ew2.getX(), ew2.getY(), ew2.getZ());
        }
        final double double2 = this.targetLocation.distanceToSqr(this.dragon.x, this.dragon.y, this.dragon.z);
        if (double2 < 100.0 || double2 > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
            this.dragon.setHealth(0.0f);
        }
        else {
            this.dragon.setHealth(1.0f);
        }
    }
    
    @Override
    public void begin() {
        this.targetLocation = null;
        this.time = 0;
    }
    
    @Override
    public float getFlySpeed() {
        return 3.0f;
    }
    
    @Nullable
    @Override
    public Vec3 getFlyTargetLocation() {
        return this.targetLocation;
    }
    
    public EnderDragonPhase<DragonDeathPhase> getPhase() {
        return EnderDragonPhase.DYING;
    }
}
