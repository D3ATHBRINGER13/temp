package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import java.util.EnumSet;
import net.minecraft.world.entity.TamableAnimal;

public class SitGoal extends Goal {
    private final TamableAnimal mob;
    private boolean wantToSit;
    
    public SitGoal(final TamableAnimal ajl) {
        this.mob = ajl;
        this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.JUMP, (Enum)Flag.MOVE));
    }
    
    @Override
    public boolean canContinueToUse() {
        return this.wantToSit;
    }
    
    @Override
    public boolean canUse() {
        if (!this.mob.isTame()) {
            return false;
        }
        if (this.mob.isInWaterOrBubble()) {
            return false;
        }
        if (!this.mob.onGround) {
            return false;
        }
        final LivingEntity aix2 = this.mob.getOwner();
        return aix2 == null || ((this.mob.distanceToSqr(aix2) >= 144.0 || aix2.getLastHurtByMob() == null) && this.wantToSit);
    }
    
    @Override
    public void start() {
        this.mob.getNavigation().stop();
        this.mob.setSitting(true);
    }
    
    @Override
    public void stop() {
        this.mob.setSitting(false);
    }
    
    public void wantToSit(final boolean boolean1) {
        this.wantToSit = boolean1;
    }
}
