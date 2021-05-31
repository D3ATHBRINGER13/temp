package net.minecraft.world.entity.ai.goal.target;

import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;

public class OwnerHurtTargetGoal extends TargetGoal {
    private final TamableAnimal tameAnimal;
    private LivingEntity ownerLastHurt;
    private int timestamp;
    
    public OwnerHurtTargetGoal(final TamableAnimal ajl) {
        super(ajl, false);
        this.tameAnimal = ajl;
        this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.TARGET));
    }
    
    @Override
    public boolean canUse() {
        if (!this.tameAnimal.isTame() || this.tameAnimal.isSitting()) {
            return false;
        }
        final LivingEntity aix2 = this.tameAnimal.getOwner();
        if (aix2 == null) {
            return false;
        }
        this.ownerLastHurt = aix2.getLastHurtMob();
        final int integer3 = aix2.getLastHurtMobTimestamp();
        return integer3 != this.timestamp && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT) && this.tameAnimal.wantsToAttack(this.ownerLastHurt, aix2);
    }
    
    @Override
    public void start() {
        this.mob.setTarget(this.ownerLastHurt);
        final LivingEntity aix2 = this.tameAnimal.getOwner();
        if (aix2 != null) {
            this.timestamp = aix2.getLastHurtMobTimestamp();
        }
        super.start();
    }
}
