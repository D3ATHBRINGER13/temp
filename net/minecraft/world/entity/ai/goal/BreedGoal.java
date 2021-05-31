package net.minecraft.world.entity.ai.goal;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.GameRules;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.AgableMob;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import java.util.EnumSet;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class BreedGoal extends Goal {
    private static final TargetingConditions PARTNER_TARGETING;
    protected final Animal animal;
    private final Class<? extends Animal> partnerClass;
    protected final Level level;
    protected Animal partner;
    private int loveTime;
    private final double speedModifier;
    
    public BreedGoal(final Animal ara, final double double2) {
        this(ara, double2, ara.getClass());
    }
    
    public BreedGoal(final Animal ara, final double double2, final Class<? extends Animal> class3) {
        this.animal = ara;
        this.level = ara.level;
        this.partnerClass = class3;
        this.speedModifier = double2;
        this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE, (Enum)Flag.LOOK));
    }
    
    @Override
    public boolean canUse() {
        if (!this.animal.isInLove()) {
            return false;
        }
        this.partner = this.getFreePartner();
        return this.partner != null;
    }
    
    @Override
    public boolean canContinueToUse() {
        return this.partner.isAlive() && this.partner.isInLove() && this.loveTime < 60;
    }
    
    @Override
    public void stop() {
        this.partner = null;
        this.loveTime = 0;
    }
    
    @Override
    public void tick() {
        this.animal.getLookControl().setLookAt(this.partner, 10.0f, (float)this.animal.getMaxHeadXRot());
        this.animal.getNavigation().moveTo(this.partner, this.speedModifier);
        ++this.loveTime;
        if (this.loveTime >= 60 && this.animal.distanceToSqr(this.partner) < 9.0) {
            this.breed();
        }
    }
    
    @Nullable
    private Animal getFreePartner() {
        final List<Animal> list2 = this.level.<Animal>getNearbyEntities(this.partnerClass, BreedGoal.PARTNER_TARGETING, (LivingEntity)this.animal, this.animal.getBoundingBox().inflate(8.0));
        double double3 = Double.MAX_VALUE;
        Animal ara5 = null;
        for (final Animal ara6 : list2) {
            if (this.animal.canMate(ara6) && this.animal.distanceToSqr(ara6) < double3) {
                ara5 = ara6;
                double3 = this.animal.distanceToSqr(ara6);
            }
        }
        return ara5;
    }
    
    protected void breed() {
        final AgableMob aim2 = this.animal.getBreedOffspring(this.partner);
        if (aim2 == null) {
            return;
        }
        ServerPlayer vl3 = this.animal.getLoveCause();
        if (vl3 == null && this.partner.getLoveCause() != null) {
            vl3 = this.partner.getLoveCause();
        }
        if (vl3 != null) {
            vl3.awardStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(vl3, this.animal, this.partner, aim2);
        }
        this.animal.setAge(6000);
        this.partner.setAge(6000);
        this.animal.resetLove();
        this.partner.resetLove();
        aim2.setAge(-24000);
        aim2.moveTo(this.animal.x, this.animal.y, this.animal.z, 0.0f, 0.0f);
        this.level.addFreshEntity(aim2);
        this.level.broadcastEntityEvent(this.animal, (byte)18);
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.level.addFreshEntity(new ExperienceOrb(this.level, this.animal.x, this.animal.y, this.animal.z, this.animal.getRandom().nextInt(7) + 1));
        }
    }
    
    static {
        PARTNER_TARGETING = new TargetingConditions().range(8.0).allowInvulnerable().allowSameTeam().allowUnseeable();
    }
}
