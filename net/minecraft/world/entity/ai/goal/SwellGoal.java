package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.entity.Entity;
import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;

public class SwellGoal extends Goal {
    private final Creeper creeper;
    private LivingEntity target;
    
    public SwellGoal(final Creeper aue) {
        this.creeper = aue;
        this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE));
    }
    
    @Override
    public boolean canUse() {
        final LivingEntity aix2 = this.creeper.getTarget();
        return this.creeper.getSwellDir() > 0 || (aix2 != null && this.creeper.distanceToSqr(aix2) < 9.0);
    }
    
    @Override
    public void start() {
        this.creeper.getNavigation().stop();
        this.target = this.creeper.getTarget();
    }
    
    @Override
    public void stop() {
        this.target = null;
    }
    
    @Override
    public void tick() {
        if (this.target == null) {
            this.creeper.setSwellDir(-1);
            return;
        }
        if (this.creeper.distanceToSqr(this.target) > 49.0) {
            this.creeper.setSwellDir(-1);
            return;
        }
        if (!this.creeper.getSensing().canSee(this.target)) {
            this.creeper.setSwellDir(-1);
            return;
        }
        this.creeper.setSwellDir(1);
    }
}
