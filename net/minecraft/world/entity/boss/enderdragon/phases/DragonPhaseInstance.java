package net.minecraft.world.entity.boss.enderdragon.phases;

import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;

public interface DragonPhaseInstance {
    boolean isSitting();
    
    void doClientTick();
    
    void doServerTick();
    
    void onCrystalDestroyed(final EndCrystal aso, final BlockPos ew, final DamageSource ahx, @Nullable final Player awg);
    
    void begin();
    
    void end();
    
    float getFlySpeed();
    
    float getTurnSpeed();
    
    EnderDragonPhase<? extends DragonPhaseInstance> getPhase();
    
    @Nullable
    Vec3 getFlyTargetLocation();
    
    float onHurt(final DamageSource ahx, final float float2);
}
