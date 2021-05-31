package net.minecraft.world.level;

import net.minecraft.world.phys.shapes.BooleanOp;
import java.util.UUID;
import com.google.common.collect.Lists;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import java.util.Iterator;
import net.minecraft.world.phys.shapes.Shapes;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.Set;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.phys.AABB;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;

public interface EntityGetter {
    List<Entity> getEntities(@Nullable final Entity aio, final AABB csc, @Nullable final Predicate<? super Entity> predicate);
    
     <T extends Entity> List<T> getEntitiesOfClass(final Class<? extends T> class1, final AABB csc, @Nullable final Predicate<? super T> predicate);
    
    default <T extends Entity> List<T> getLoadedEntitiesOfClass(final Class<? extends T> class1, final AABB csc, @Nullable final Predicate<? super T> predicate) {
        return this.getEntitiesOfClass((java.lang.Class<? extends Entity>)class1, csc, (java.util.function.Predicate<? super Entity>)predicate);
    }
    
    List<? extends Player> players();
    
    default List<Entity> getEntities(@Nullable final Entity aio, final AABB csc) {
        return this.getEntities(aio, csc, EntitySelector.NO_SPECTATORS);
    }
    
    default boolean isUnobstructed(@Nullable final Entity aio, final VoxelShape ctc) {
        return ctc.isEmpty() || this.getEntities(aio, ctc.bounds()).stream().filter(aio2 -> !aio2.removed && aio2.blocksBuilding && (aio == null || !aio2.isPassengerOfSameVehicle(aio))).noneMatch(aio -> Shapes.joinIsNotEmpty(ctc, Shapes.create(aio.getBoundingBox()), BooleanOp.AND));
    }
    
    default <T extends Entity> List<T> getEntitiesOfClass(final Class<? extends T> class1, final AABB csc) {
        return this.<T>getEntitiesOfClass(class1, csc, (java.util.function.Predicate<? super T>)EntitySelector.NO_SPECTATORS);
    }
    
    default <T extends Entity> List<T> getLoadedEntitiesOfClass(final Class<? extends T> class1, final AABB csc) {
        return this.<T>getLoadedEntitiesOfClass(class1, csc, (java.util.function.Predicate<? super T>)EntitySelector.NO_SPECTATORS);
    }
    
    default Stream<VoxelShape> getEntityCollisions(@Nullable final Entity aio, final AABB csc, final Set<Entity> set) {
        if (csc.getSize() < 1.0E-7) {
            return (Stream<VoxelShape>)Stream.empty();
        }
        final AABB csc2 = csc.inflate(1.0E-7);
        return (Stream<VoxelShape>)this.getEntities(aio, csc2).stream().filter(aio -> !set.contains(aio)).filter(aio2 -> aio == null || !aio.isPassengerOfSameVehicle(aio2)).flatMap(aio2 -> Stream.of((Object[])new AABB[] { aio2.getCollideBox(), (aio == null) ? null : aio.getCollideAgainstBox(aio2) })).filter(Objects::nonNull).filter(csc2::intersects).map(Shapes::create);
    }
    
    @Nullable
    default Player getNearestPlayer(final double double1, final double double2, final double double3, final double double4, @Nullable final Predicate<Entity> predicate) {
        double double5 = -1.0;
        Player awg13 = null;
        for (final Player awg14 : this.players()) {
            if (predicate != null && !predicate.test(awg14)) {
                continue;
            }
            final double double6 = awg14.distanceToSqr(double1, double2, double3);
            if ((double4 >= 0.0 && double6 >= double4 * double4) || (double5 != -1.0 && double6 >= double5)) {
                continue;
            }
            double5 = double6;
            awg13 = awg14;
        }
        return awg13;
    }
    
    @Nullable
    default Player getNearestPlayer(final Entity aio, final double double2) {
        return this.getNearestPlayer(aio.x, aio.y, aio.z, double2, false);
    }
    
    @Nullable
    default Player getNearestPlayer(final double double1, final double double2, final double double3, final double double4, final boolean boolean5) {
        final Predicate<Entity> predicate11 = boolean5 ? EntitySelector.NO_CREATIVE_OR_SPECTATOR : EntitySelector.NO_SPECTATORS;
        return this.getNearestPlayer(double1, double2, double3, double4, predicate11);
    }
    
    @Nullable
    default Player getNearestPlayerIgnoreY(final double double1, final double double2, final double double3) {
        double double4 = -1.0;
        Player awg10 = null;
        for (final Player awg11 : this.players()) {
            if (!EntitySelector.NO_SPECTATORS.test(awg11)) {
                continue;
            }
            final double double5 = awg11.distanceToSqr(double1, awg11.y, double2);
            if ((double3 >= 0.0 && double5 >= double3 * double3) || (double4 != -1.0 && double5 >= double4)) {
                continue;
            }
            double4 = double5;
            awg10 = awg11;
        }
        return awg10;
    }
    
    default boolean hasNearbyAlivePlayer(final double double1, final double double2, final double double3, final double double4) {
        for (final Player awg11 : this.players()) {
            if (EntitySelector.NO_SPECTATORS.test(awg11)) {
                if (!EntitySelector.LIVING_ENTITY_STILL_ALIVE.test(awg11)) {
                    continue;
                }
                final double double5 = awg11.distanceToSqr(double1, double2, double3);
                if (double4 < 0.0 || double5 < double4 * double4) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    @Nullable
    default Player getNearestPlayer(final TargetingConditions aqi, final LivingEntity aix) {
        return this.<Player>getNearestEntity(this.players(), aqi, aix, aix.x, aix.y, aix.z);
    }
    
    @Nullable
    default Player getNearestPlayer(final TargetingConditions aqi, final LivingEntity aix, final double double3, final double double4, final double double5) {
        return this.<Player>getNearestEntity(this.players(), aqi, aix, double3, double4, double5);
    }
    
    @Nullable
    default Player getNearestPlayer(final TargetingConditions aqi, final double double2, final double double3, final double double4) {
        return this.<Player>getNearestEntity(this.players(), aqi, (LivingEntity)null, double2, double3, double4);
    }
    
    @Nullable
    default <T extends LivingEntity> T getNearestEntity(final Class<? extends T> class1, final TargetingConditions aqi, @Nullable final LivingEntity aix, final double double4, final double double5, final double double6, final AABB csc) {
        return this.<T>getNearestEntity(this.getEntitiesOfClass(class1, csc, (java.util.function.Predicate<? super T>)null), aqi, aix, double4, double5, double6);
    }
    
    @Nullable
    default <T extends LivingEntity> T getNearestLoadedEntity(final Class<? extends T> class1, final TargetingConditions aqi, @Nullable final LivingEntity aix, final double double4, final double double5, final double double6, final AABB csc) {
        return this.<T>getNearestEntity(this.getLoadedEntitiesOfClass(class1, csc, (java.util.function.Predicate<? super T>)null), aqi, aix, double4, double5, double6);
    }
    
    @Nullable
    default <T extends LivingEntity> T getNearestEntity(final List<? extends T> list, final TargetingConditions aqi, @Nullable final LivingEntity aix, final double double4, final double double5, final double double6) {
        double double7 = -1.0;
        T aix2 = null;
        for (final T aix3 : list) {
            if (!aqi.test(aix, aix3)) {
                continue;
            }
            final double double8 = aix3.distanceToSqr(double4, double5, double6);
            if (double7 != -1.0 && double8 >= double7) {
                continue;
            }
            double7 = double8;
            aix2 = aix3;
        }
        return aix2;
    }
    
    default List<Player> getNearbyPlayers(final TargetingConditions aqi, final LivingEntity aix, final AABB csc) {
        final List<Player> list5 = (List<Player>)Lists.newArrayList();
        for (final Player awg7 : this.players()) {
            if (csc.contains(awg7.x, awg7.y, awg7.z) && aqi.test(aix, awg7)) {
                list5.add(awg7);
            }
        }
        return list5;
    }
    
    default <T extends LivingEntity> List<T> getNearbyEntities(final Class<? extends T> class1, final TargetingConditions aqi, final LivingEntity aix, final AABB csc) {
        final List<T> list6 = this.<T>getEntitiesOfClass(class1, csc, (java.util.function.Predicate<? super T>)null);
        final List<T> list7 = (List<T>)Lists.newArrayList();
        for (final T aix2 : list6) {
            if (aqi.test(aix, aix2)) {
                list7.add(aix2);
            }
        }
        return list7;
    }
    
    @Nullable
    default Player getPlayerByUUID(final UUID uUID) {
        for (int integer3 = 0; integer3 < this.players().size(); ++integer3) {
            final Player awg4 = (Player)this.players().get(integer3);
            if (uUID.equals(awg4.getUUID())) {
                return awg4;
            }
        }
        return null;
    }
}
