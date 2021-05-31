package net.minecraft.world.entity.ai.goal.target;

import java.util.Iterator;
import java.util.List;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.npc.Villager;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;

public class DefendVillageTargetGoal extends TargetGoal {
    private final IronGolem golem;
    private LivingEntity potentialTarget;
    private final TargetingConditions attackTargeting;
    
    public DefendVillageTargetGoal(final IronGolem ari) {
        super(ari, false, true);
        this.attackTargeting = new TargetingConditions().range(64.0);
        this.golem = ari;
        this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.TARGET));
    }
    
    @Override
    public boolean canUse() {
        final AABB csc2 = this.golem.getBoundingBox().inflate(10.0, 8.0, 10.0);
        final List<LivingEntity> list3 = this.golem.level.<LivingEntity>getNearbyEntities((java.lang.Class<? extends LivingEntity>)Villager.class, this.attackTargeting, (LivingEntity)this.golem, csc2);
        final List<Player> list4 = this.golem.level.getNearbyPlayers(this.attackTargeting, this.golem, csc2);
        for (final LivingEntity aix6 : list3) {
            final Villager avt7 = (Villager)aix6;
            for (final Player awg9 : list4) {
                final int integer10 = avt7.getPlayerReputation(awg9);
                if (integer10 <= -100) {
                    this.potentialTarget = awg9;
                }
            }
        }
        return this.potentialTarget != null;
    }
    
    @Override
    public void start() {
        this.golem.setTarget(this.potentialTarget);
        super.start();
    }
}
