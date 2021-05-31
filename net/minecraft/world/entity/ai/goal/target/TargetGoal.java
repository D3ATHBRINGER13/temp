package net.minecraft.world.entity.ai.goal.target;

import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.util.Mth;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import javax.annotation.Nullable;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.scores.Team;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public abstract class TargetGoal extends Goal {
    protected final Mob mob;
    protected final boolean mustSee;
    private final boolean mustReach;
    private int reachCache;
    private int reachCacheTime;
    private int unseenTicks;
    protected LivingEntity targetMob;
    protected int unseenMemoryTicks;
    
    public TargetGoal(final Mob aiy, final boolean boolean2) {
        this(aiy, boolean2, false);
    }
    
    public TargetGoal(final Mob aiy, final boolean boolean2, final boolean boolean3) {
        this.unseenMemoryTicks = 60;
        this.mob = aiy;
        this.mustSee = boolean2;
        this.mustReach = boolean3;
    }
    
    @Override
    public boolean canContinueToUse() {
        LivingEntity aix2 = this.mob.getTarget();
        if (aix2 == null) {
            aix2 = this.targetMob;
        }
        if (aix2 == null) {
            return false;
        }
        if (!aix2.isAlive()) {
            return false;
        }
        final Team ctk3 = this.mob.getTeam();
        final Team ctk4 = aix2.getTeam();
        if (ctk3 != null && ctk4 == ctk3) {
            return false;
        }
        final double double5 = this.getFollowDistance();
        if (this.mob.distanceToSqr(aix2) > double5 * double5) {
            return false;
        }
        if (this.mustSee) {
            if (this.mob.getSensing().canSee(aix2)) {
                this.unseenTicks = 0;
            }
            else if (++this.unseenTicks > this.unseenMemoryTicks) {
                return false;
            }
        }
        if (aix2 instanceof Player && ((Player)aix2).abilities.invulnerable) {
            return false;
        }
        this.mob.setTarget(aix2);
        return true;
    }
    
    protected double getFollowDistance() {
        final AttributeInstance ajo2 = this.mob.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
        return (ajo2 == null) ? 16.0 : ajo2.getValue();
    }
    
    @Override
    public void start() {
        this.reachCache = 0;
        this.reachCacheTime = 0;
        this.unseenTicks = 0;
    }
    
    @Override
    public void stop() {
        this.mob.setTarget(null);
        this.targetMob = null;
    }
    
    protected boolean canAttack(@Nullable final LivingEntity aix, final TargetingConditions aqi) {
        if (aix == null) {
            return false;
        }
        if (!aqi.test(this.mob, aix)) {
            return false;
        }
        if (!this.mob.isWithinRestriction(new BlockPos(aix))) {
            return false;
        }
        if (this.mustReach) {
            if (--this.reachCacheTime <= 0) {
                this.reachCache = 0;
            }
            if (this.reachCache == 0) {
                this.reachCache = (this.canReach(aix) ? 1 : 2);
            }
            if (this.reachCache == 2) {
                return false;
            }
        }
        return true;
    }
    
    private boolean canReach(final LivingEntity aix) {
        this.reachCacheTime = 10 + this.mob.getRandom().nextInt(5);
        final Path cnr3 = this.mob.getNavigation().createPath(aix, 0);
        if (cnr3 == null) {
            return false;
        }
        final Node cnp4 = cnr3.last();
        if (cnp4 == null) {
            return false;
        }
        final int integer5 = cnp4.x - Mth.floor(aix.x);
        final int integer6 = cnp4.z - Mth.floor(aix.z);
        return integer5 * integer5 + integer6 * integer6 <= 2.25;
    }
    
    public TargetGoal setUnseenMemoryTicks(final int integer) {
        this.unseenMemoryTicks = integer;
        return this;
    }
}
