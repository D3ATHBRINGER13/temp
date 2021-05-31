package net.minecraft.world.entity.ai;

import com.google.common.collect.ImmutableMap;
import java.util.stream.Collectors;
import net.minecraft.server.level.ServerLevel;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableList;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import java.util.Iterator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import java.util.function.Function;
import com.google.common.collect.Sets;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Dynamic;
import java.util.Collection;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.entity.ai.behavior.Behavior;
import java.util.Set;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import java.util.Optional;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import java.util.Map;
import net.minecraft.util.Serializable;
import net.minecraft.world.entity.LivingEntity;

public class Brain<E extends LivingEntity> implements Serializable {
    private final Map<MemoryModuleType<?>, Optional<?>> memories;
    private final Map<SensorType<? extends Sensor<? super E>>, Sensor<? super E>> sensors;
    private final Map<Integer, Map<Activity, Set<Behavior<? super E>>>> availableGoalsByPriority;
    private Schedule schedule;
    private final Map<Activity, Set<Pair<MemoryModuleType<?>, MemoryStatus>>> activityRequirements;
    private Set<Activity> coreActivities;
    private final Set<Activity> activeActivities;
    private Activity defaultActivity;
    private long lastScheduleUpdate;
    
    public <T> Brain(final Collection<MemoryModuleType<?>> collection1, final Collection<SensorType<? extends Sensor<? super E>>> collection2, final Dynamic<T> dynamic) {
        this.memories = (Map<MemoryModuleType<?>, Optional<?>>)Maps.newHashMap();
        this.sensors = (Map<SensorType<? extends Sensor<? super E>>, Sensor<? super E>>)Maps.newLinkedHashMap();
        this.availableGoalsByPriority = (Map<Integer, Map<Activity, Set<Behavior<? super E>>>>)Maps.newTreeMap();
        this.schedule = Schedule.EMPTY;
        this.activityRequirements = (Map<Activity, Set<Pair<MemoryModuleType<?>, MemoryStatus>>>)Maps.newHashMap();
        this.coreActivities = (Set<Activity>)Sets.newHashSet();
        this.activeActivities = (Set<Activity>)Sets.newHashSet();
        this.defaultActivity = Activity.IDLE;
        this.lastScheduleUpdate = -9999L;
        collection1.forEach(apj -> {
            final Optional optional = (Optional)this.memories.put(apj, Optional.empty());
        });
        collection2.forEach(aqe -> {
            final Sensor sensor = (Sensor)this.sensors.put(aqe, aqe.create());
        });
        this.sensors.values().forEach(aqd -> {
            for (final MemoryModuleType<?> apj4 : aqd.requires()) {
                this.memories.put(apj4, Optional.empty());
            }
        });
        for (final Map.Entry<Dynamic<T>, Dynamic<T>> entry6 : dynamic.get("memories").asMap(Function.identity(), Function.identity()).entrySet()) {
            this.readMemory(Registry.MEMORY_MODULE_TYPE.get(new ResourceLocation(((Dynamic)entry6.getKey()).asString(""))), (com.mojang.datafixers.Dynamic<Object>)entry6.getValue());
        }
    }
    
    public boolean hasMemoryValue(final MemoryModuleType<?> apj) {
        return this.checkMemory(apj, MemoryStatus.VALUE_PRESENT);
    }
    
    private <T, U> void readMemory(final MemoryModuleType<U> apj, final Dynamic<T> dynamic) {
        this.setMemory((MemoryModuleType<Object>)apj, ((Function)apj.getDeserializer().orElseThrow(RuntimeException::new)).apply(dynamic));
    }
    
    public <U> void eraseMemory(final MemoryModuleType<U> apj) {
        this.<U>setMemory(apj, (java.util.Optional<U>)Optional.empty());
    }
    
    public <U> void setMemory(final MemoryModuleType<U> apj, @Nullable final U object) {
        this.<U>setMemory(apj, (java.util.Optional<U>)Optional.ofNullable(object));
    }
    
    public <U> void setMemory(final MemoryModuleType<U> apj, final Optional<U> optional) {
        if (this.memories.containsKey(apj)) {
            if (optional.isPresent() && this.isEmptyCollection(optional.get())) {
                this.<U>eraseMemory(apj);
            }
            else {
                this.memories.put(apj, optional);
            }
        }
    }
    
    public <U> Optional<U> getMemory(final MemoryModuleType<U> apj) {
        return (Optional<U>)this.memories.get(apj);
    }
    
    public boolean checkMemory(final MemoryModuleType<?> apj, final MemoryStatus apk) {
        final Optional<?> optional4 = this.memories.get(apj);
        return optional4 != null && (apk == MemoryStatus.REGISTERED || (apk == MemoryStatus.VALUE_PRESENT && optional4.isPresent()) || (apk == MemoryStatus.VALUE_ABSENT && !optional4.isPresent()));
    }
    
    public Schedule getSchedule() {
        return this.schedule;
    }
    
    public void setSchedule(final Schedule axq) {
        this.schedule = axq;
    }
    
    public void setCoreActivities(final Set<Activity> set) {
        this.coreActivities = set;
    }
    
    @Deprecated
    public Stream<Behavior<? super E>> getRunningBehaviorsStream() {
        return (Stream<Behavior<? super E>>)this.availableGoalsByPriority.values().stream().flatMap(map -> map.values().stream()).flatMap(Collection::stream).filter(ajy -> ajy.getStatus() == Behavior.Status.RUNNING);
    }
    
    public void setActivity(final Activity axo) {
        this.activeActivities.clear();
        this.activeActivities.addAll((Collection)this.coreActivities);
        final boolean boolean3 = this.activityRequirements.keySet().contains(axo) && this.activityRequirementsAreMet(axo);
        this.activeActivities.add((boolean3 ? axo : this.defaultActivity));
    }
    
    public void updateActivity(final long long1, final long long2) {
        if (long2 - this.lastScheduleUpdate > 20L) {
            this.lastScheduleUpdate = long2;
            final Activity axo6 = this.getSchedule().getActivityAt((int)(long1 % 24000L));
            if (!this.activeActivities.contains(axo6)) {
                this.setActivity(axo6);
            }
        }
    }
    
    public void setDefaultActivity(final Activity axo) {
        this.defaultActivity = axo;
    }
    
    public void addActivity(final Activity axo, final ImmutableList<Pair<Integer, ? extends Behavior<? super E>>> immutableList) {
        this.addActivity(axo, immutableList, (Set<Pair<MemoryModuleType<?>, MemoryStatus>>)ImmutableSet.of());
    }
    
    public void addActivity(final Activity axo, final ImmutableList<Pair<Integer, ? extends Behavior<? super E>>> immutableList, final Set<Pair<MemoryModuleType<?>, MemoryStatus>> set) {
        this.activityRequirements.put(axo, set);
        immutableList.forEach(pair -> ((Set)((Map)this.availableGoalsByPriority.computeIfAbsent(pair.getFirst(), integer -> Maps.newHashMap())).computeIfAbsent(axo, axo -> Sets.newLinkedHashSet())).add(pair.getSecond()));
    }
    
    public boolean isActive(final Activity axo) {
        return this.activeActivities.contains(axo);
    }
    
    public Brain<E> copyWithoutGoals() {
        final Brain<E> ajm2 = new Brain<E>((Collection<MemoryModuleType<?>>)this.memories.keySet(), (Collection<SensorType<? extends Sensor<? super E>>>)this.sensors.keySet(), (Dynamic<T>)new Dynamic((DynamicOps)NbtOps.INSTANCE, new CompoundTag()));
        this.memories.forEach((apj, optional) -> optional.ifPresent(object -> {
            final Optional optional = (Optional)ajm2.memories.put(apj, Optional.of(object));
        }));
        return ajm2;
    }
    
    public void tick(final ServerLevel vk, final E aix) {
        this.tickEachSensor(vk, aix);
        this.startEachNonRunningBehavior(vk, aix);
        this.tickEachRunningBehavior(vk, aix);
    }
    
    public void stopAll(final ServerLevel vk, final E aix) {
        final long long4 = aix.level.getGameTime();
        this.getRunningBehaviorsStream().forEach(ajy -> ajy.doStop(vk, aix, long4));
    }
    
    public <T> T serialize(final DynamicOps<T> dynamicOps) {
        final T object3 = (T)dynamicOps.createMap((Map)this.memories.entrySet().stream().filter(entry -> ((MemoryModuleType)entry.getKey()).getDeserializer().isPresent() && ((Optional)entry.getValue()).isPresent()).map(entry -> Pair.of(dynamicOps.createString(Registry.MEMORY_MODULE_TYPE.getKey(entry.getKey()).toString()), ((Serializable)((Optional)entry.getValue()).get()).serialize((com.mojang.datafixers.types.DynamicOps<Object>)dynamicOps))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
        return (T)dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("memories"), object3));
    }
    
    private void tickEachSensor(final ServerLevel vk, final E aix) {
        this.sensors.values().forEach(aqd -> aqd.tick(vk, aix));
    }
    
    private void startEachNonRunningBehavior(final ServerLevel vk, final E aix) {
        final long long4 = vk.getGameTime();
        this.availableGoalsByPriority.values().stream().flatMap(map -> map.entrySet().stream()).filter(entry -> this.activeActivities.contains(entry.getKey())).map(Map.Entry::getValue).flatMap(Collection::stream).filter(ajy -> ajy.getStatus() == Behavior.Status.STOPPED).forEach(ajy -> ajy.tryStart(vk, aix, long4));
    }
    
    private void tickEachRunningBehavior(final ServerLevel vk, final E aix) {
        final long long4 = vk.getGameTime();
        this.getRunningBehaviorsStream().forEach(ajy -> ajy.tickOrStop(vk, aix, long4));
    }
    
    private boolean activityRequirementsAreMet(final Activity axo) {
        return ((Set)this.activityRequirements.get(axo)).stream().allMatch(pair -> {
            final MemoryModuleType<?> apj3 = pair.getFirst();
            final MemoryStatus apk4 = (MemoryStatus)pair.getSecond();
            return this.checkMemory(apj3, apk4);
        });
    }
    
    private boolean isEmptyCollection(final Object object) {
        return object instanceof Collection && ((Collection)object).isEmpty();
    }
}
