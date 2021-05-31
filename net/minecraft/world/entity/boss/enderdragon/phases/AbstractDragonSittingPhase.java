package net.minecraft.world.entity.boss.enderdragon.phases;

import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;

public abstract class AbstractDragonSittingPhase extends AbstractDragonPhaseInstance {
    public AbstractDragonSittingPhase(final EnderDragon asp) {
        super(asp);
    }
    
    @Override
    public boolean isSitting() {
        return true;
    }
    
    @Override
    public float onHurt(final DamageSource ahx, final float float2) {
        if (ahx.getDirectEntity() instanceof AbstractArrow) {
            ahx.getDirectEntity().setSecondsOnFire(1);
            return 0.0f;
        }
        return super.onHurt(ahx, float2);
    }
}
