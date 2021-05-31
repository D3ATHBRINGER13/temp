package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.Mob;

public class FloatGoal extends Goal {
    private final Mob mob;
    
    public FloatGoal(final Mob aiy) {
        this.mob = aiy;
        this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.JUMP));
        aiy.getNavigation().setCanFloat(true);
    }
    
    @Override
    public boolean canUse() {
        final double double2 = (this.mob.getEyeHeight() < 0.4) ? 0.2 : 0.4;
        return (this.mob.isInWater() && this.mob.getWaterHeight() > double2) || this.mob.isInLava();
    }
    
    @Override
    public void tick() {
        if (this.mob.getRandom().nextFloat() < 0.8f) {
            this.mob.getJumpControl().jump();
        }
    }
}
