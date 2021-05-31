package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.Brain;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import java.util.function.Predicate;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public class InteractWith<E extends LivingEntity, T extends LivingEntity> extends Behavior<E> {
    private final int maxDist;
    private final float speed;
    private final EntityType<? extends T> type;
    private final int interactionRangeSqr;
    private final Predicate<T> targetFilter;
    private final Predicate<E> selfFilter;
    private final MemoryModuleType<T> memory;
    
    public InteractWith(final EntityType<? extends T> ais, final int integer2, final Predicate<E> predicate3, final Predicate<T> predicate4, final MemoryModuleType<T> apj, final float float6, final int integer7) {
        super((Map)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, apj, MemoryStatus.VALUE_ABSENT, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
        this.type = ais;
        this.speed = float6;
        this.interactionRangeSqr = integer2 * integer2;
        this.maxDist = integer7;
        this.targetFilter = predicate4;
        this.selfFilter = predicate3;
        this.memory = apj;
    }
    
    public static <T extends LivingEntity> InteractWith<LivingEntity, T> of(final EntityType<? extends T> ais, final int integer2, final MemoryModuleType<T> apj, final float float4, final int integer5) {
        return new InteractWith<LivingEntity, T>(ais, integer2, (java.util.function.Predicate<LivingEntity>)(aix -> true), (java.util.function.Predicate<T>)(aix -> true), apj, float4, integer5);
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final E aix) {
        return this.selfFilter.test(aix) && ((List)aix.getBrain().<List<LivingEntity>>getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).get()).stream().anyMatch(aix -> this.type.equals(aix.getType()) && this.targetFilter.test(aix));
    }
    
    @Override
    protected void start(final ServerLevel vk, final E aix, final long long3) {
        final Brain<?> ajm6 = aix.getBrain();
        ajm6.<List<LivingEntity>>getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).ifPresent(list -> list.stream().filter(aix -> this.type.equals(aix.getType())).map(aix -> aix).filter(aix2 -> aix2.distanceToSqr(aix) <= this.interactionRangeSqr).filter((Predicate)this.targetFilter).findFirst().ifPresent(aix -> {
            ajm6.<LivingEntity>setMemory(this.memory, aix);
            ajm6.<EntityPosWrapper>setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(aix));
            ajm6.<WalkTarget>setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityPosWrapper(aix), this.speed, this.maxDist));
        }));
    }
}
