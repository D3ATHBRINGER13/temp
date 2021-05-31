package net.minecraft.world.entity.ai.targeting;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import java.util.function.Predicate;

public class TargetingConditions {
    public static final TargetingConditions DEFAULT;
    private double range;
    private boolean allowInvulnerable;
    private boolean allowSameTeam;
    private boolean allowUnseeable;
    private boolean allowNonAttackable;
    private boolean testInvisible;
    private Predicate<LivingEntity> selector;
    
    public TargetingConditions() {
        this.range = -1.0;
        this.testInvisible = true;
    }
    
    public TargetingConditions range(final double double1) {
        this.range = double1;
        return this;
    }
    
    public TargetingConditions allowInvulnerable() {
        this.allowInvulnerable = true;
        return this;
    }
    
    public TargetingConditions allowSameTeam() {
        this.allowSameTeam = true;
        return this;
    }
    
    public TargetingConditions allowUnseeable() {
        this.allowUnseeable = true;
        return this;
    }
    
    public TargetingConditions allowNonAttackable() {
        this.allowNonAttackable = true;
        return this;
    }
    
    public TargetingConditions ignoreInvisibilityTesting() {
        this.testInvisible = false;
        return this;
    }
    
    public TargetingConditions selector(@Nullable final Predicate<LivingEntity> predicate) {
        this.selector = predicate;
        return this;
    }
    
    public boolean test(@Nullable final LivingEntity aix1, final LivingEntity aix2) {
        if (aix1 == aix2) {
            return false;
        }
        if (aix2.isSpectator()) {
            return false;
        }
        if (!aix2.isAlive()) {
            return false;
        }
        if (!this.allowInvulnerable && aix2.isInvulnerable()) {
            return false;
        }
        if (this.selector != null && !this.selector.test(aix2)) {
            return false;
        }
        if (aix1 != null) {
            if (!this.allowNonAttackable) {
                if (!aix1.canAttack(aix2)) {
                    return false;
                }
                if (!aix1.canAttackType(aix2.getType())) {
                    return false;
                }
            }
            if (!this.allowSameTeam && aix1.isAlliedTo(aix2)) {
                return false;
            }
            if (this.range > 0.0) {
                final double double4 = this.testInvisible ? aix2.getVisibilityPercent(aix1) : 1.0;
                final double double5 = this.range * double4;
                final double double6 = aix1.distanceToSqr(aix2.x, aix2.y, aix2.z);
                if (double6 > double5 * double5) {
                    return false;
                }
            }
            if (!this.allowUnseeable && aix1 instanceof Mob && !((Mob)aix1).getSensing().canSee(aix2)) {
                return false;
            }
        }
        return true;
    }
    
    static {
        DEFAULT = new TargetingConditions();
    }
}
