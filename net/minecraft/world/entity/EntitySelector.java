package net.minecraft.world.entity;

import javax.annotation.Nullable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import com.google.common.base.Predicates;
import net.minecraft.world.scores.Team;
import java.util.function.Predicate;

public final class EntitySelector {
    public static final Predicate<Entity> ENTITY_STILL_ALIVE;
    public static final Predicate<LivingEntity> LIVING_ENTITY_STILL_ALIVE;
    public static final Predicate<Entity> ENTITY_NOT_BEING_RIDDEN;
    public static final Predicate<Entity> CONTAINER_ENTITY_SELECTOR;
    public static final Predicate<Entity> NO_CREATIVE_OR_SPECTATOR;
    public static final Predicate<Entity> NO_SPECTATORS;
    
    public static Predicate<Entity> withinDistance(final double double1, final double double2, final double double3, final double double4) {
        final double double5 = double4 * double4;
        return (Predicate<Entity>)(aio -> aio != null && aio.distanceToSqr(double1, double2, double3) <= double5);
    }
    
    public static Predicate<Entity> pushableBy(final Entity aio) {
        final Team ctk2 = aio.getTeam();
        final Team.CollisionRule a3 = (ctk2 == null) ? Team.CollisionRule.ALWAYS : ctk2.getCollisionRule();
        if (a3 == Team.CollisionRule.NEVER) {
            return (Predicate<Entity>)Predicates.alwaysFalse();
        }
        return (Predicate<Entity>)EntitySelector.NO_SPECTATORS.and(aio4 -> {
            if (!aio4.isPushable()) {
                return false;
            }
            if (aio.level.isClientSide && (!(aio4 instanceof Player) || !((Player)aio4).isLocalPlayer())) {
                return false;
            }
            final Team ctk2 = aio4.getTeam();
            final Team.CollisionRule a2 = (ctk2 == null) ? Team.CollisionRule.ALWAYS : ctk2.getCollisionRule();
            if (a2 == Team.CollisionRule.NEVER) {
                return false;
            }
            final boolean boolean7 = ctk2 != null && ctk2.isAlliedTo(ctk2);
            return ((a3 != Team.CollisionRule.PUSH_OWN_TEAM && a2 != Team.CollisionRule.PUSH_OWN_TEAM) || !boolean7) && ((a3 != Team.CollisionRule.PUSH_OTHER_TEAMS && a2 != Team.CollisionRule.PUSH_OTHER_TEAMS) || boolean7);
        });
    }
    
    public static Predicate<Entity> notRiding(final Entity aio) {
        return (Predicate<Entity>)(aio2 -> {
            while (aio2.isPassenger()) {
                aio2 = aio2.getVehicle();
                if (aio2 == aio) {
                    return false;
                }
            }
            return true;
        });
    }
    
    static {
        ENTITY_STILL_ALIVE = Entity::isAlive;
        LIVING_ENTITY_STILL_ALIVE = LivingEntity::isAlive;
        ENTITY_NOT_BEING_RIDDEN = (aio -> aio.isAlive() && !aio.isVehicle() && !aio.isPassenger());
        CONTAINER_ENTITY_SELECTOR = (aio -> aio instanceof Container && aio.isAlive());
        NO_CREATIVE_OR_SPECTATOR = (aio -> !(aio instanceof Player) || (!aio.isSpectator() && !((Player)aio).isCreative()));
        NO_SPECTATORS = (aio -> !aio.isSpectator());
    }
    
    public static class MobCanWearArmourEntitySelector implements Predicate<Entity> {
        private final ItemStack itemStack;
        
        public MobCanWearArmourEntitySelector(final ItemStack bcj) {
            this.itemStack = bcj;
        }
        
        public boolean test(@Nullable final Entity aio) {
            if (!aio.isAlive()) {
                return false;
            }
            if (!(aio instanceof LivingEntity)) {
                return false;
            }
            final LivingEntity aix3 = (LivingEntity)aio;
            return aix3.canTakeItem(this.itemStack);
        }
    }
}
