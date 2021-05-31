package net.minecraft.world.entity.ai.goal.target;

import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.LivingEntity;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class HurtByTargetGoal extends TargetGoal {
    private static final TargetingConditions HURT_BY_TARGETING;
    private boolean alertSameType;
    private int timestamp;
    private final Class<?>[] toIgnoreDamage;
    private Class<?>[] toIgnoreAlert;
    
    public HurtByTargetGoal(final PathfinderMob aje, final Class<?>... arr) {
        super(aje, true);
        this.toIgnoreDamage = arr;
        this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.TARGET));
    }
    
    @Override
    public boolean canUse() {
        final int integer2 = this.mob.getLastHurtByMobTimestamp();
        final LivingEntity aix3 = this.mob.getLastHurtByMob();
        if (integer2 == this.timestamp || aix3 == null) {
            return false;
        }
        for (final Class<?> class7 : this.toIgnoreDamage) {
            if (class7.isAssignableFrom(aix3.getClass())) {
                return false;
            }
        }
        return this.canAttack(aix3, HurtByTargetGoal.HURT_BY_TARGETING);
    }
    
    public HurtByTargetGoal setAlertOthers(final Class<?>... arr) {
        this.alertSameType = true;
        this.toIgnoreAlert = arr;
        return this;
    }
    
    @Override
    public void start() {
        this.mob.setTarget(this.mob.getLastHurtByMob());
        this.targetMob = this.mob.getTarget();
        this.timestamp = this.mob.getLastHurtByMobTimestamp();
        this.unseenMemoryTicks = 300;
        if (this.alertSameType) {
            this.alertOthers();
        }
        super.start();
    }
    
    protected void alertOthers() {
        final double double2 = this.getFollowDistance();
        final List<Mob> list4 = this.mob.level.<Mob>getLoadedEntitiesOfClass((java.lang.Class<? extends Mob>)this.mob.getClass(), new AABB(this.mob.x, this.mob.y, this.mob.z, this.mob.x + 1.0, this.mob.y + 1.0, this.mob.z + 1.0).inflate(double2, 10.0, double2));
        for (final Mob aiy6 : list4) {
            if (this.mob == aiy6) {
                continue;
            }
            if (aiy6.getTarget() != null) {
                continue;
            }
            if (this.mob instanceof TamableAnimal && ((TamableAnimal)this.mob).getOwner() != ((TamableAnimal)aiy6).getOwner()) {
                continue;
            }
            if (aiy6.isAlliedTo(this.mob.getLastHurtByMob())) {
                continue;
            }
            if (this.toIgnoreAlert != null) {
                boolean boolean7 = false;
                for (final Class<?> class11 : this.toIgnoreAlert) {
                    if (aiy6.getClass() == class11) {
                        boolean7 = true;
                        break;
                    }
                }
                if (boolean7) {
                    continue;
                }
            }
            this.alertOther(aiy6, this.mob.getLastHurtByMob());
        }
    }
    
    protected void alertOther(final Mob aiy, final LivingEntity aix) {
        aiy.setTarget(aix);
    }
    
    static {
        HURT_BY_TARGETING = new TargetingConditions().allowUnseeable().ignoreInvisibilityTesting();
    }
}
