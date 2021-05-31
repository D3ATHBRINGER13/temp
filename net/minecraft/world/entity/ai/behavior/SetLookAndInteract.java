package net.minecraft.world.entity.ai.behavior;

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

public class SetLookAndInteract extends Behavior<LivingEntity> {
    private final EntityType<?> type;
    private final int interactionRangeSqr;
    private final Predicate<LivingEntity> targetFilter;
    private final Predicate<LivingEntity> selfFilter;
    
    public SetLookAndInteract(final EntityType<?> ais, final int integer, final Predicate<LivingEntity> predicate3, final Predicate<LivingEntity> predicate4) {
        super((Map)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
        this.type = ais;
        this.interactionRangeSqr = integer * integer;
        this.targetFilter = predicate4;
        this.selfFilter = predicate3;
    }
    
    public SetLookAndInteract(final EntityType<?> ais, final int integer) {
        this(ais, integer, (Predicate<LivingEntity>)(aix -> true), (Predicate<LivingEntity>)(aix -> true));
    }
    
    public boolean checkExtraStartConditions(final ServerLevel vk, final LivingEntity aix) {
        return this.selfFilter.test(aix) && this.getVisibleEntities(aix).stream().anyMatch(this::isMatchingTarget);
    }
    
    public void start(final ServerLevel vk, final LivingEntity aix, final long long3) {
        super.start(vk, aix, long3);
        final Brain<?> ajm6 = aix.getBrain();
        ajm6.<List<LivingEntity>>getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).ifPresent(list -> list.stream().filter(aix2 -> aix2.distanceToSqr(aix) <= this.interactionRangeSqr).filter(this::isMatchingTarget).findFirst().ifPresent(aix -> {
            ajm6.<LivingEntity>setMemory(MemoryModuleType.INTERACTION_TARGET, aix);
            ajm6.<EntityPosWrapper>setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(aix));
        }));
    }
    
    private boolean isMatchingTarget(final LivingEntity aix) {
        return this.type.equals(aix.getType()) && this.targetFilter.test(aix);
    }
    
    private List<LivingEntity> getVisibleEntities(final LivingEntity aix) {
        return (List<LivingEntity>)aix.getBrain().<List<LivingEntity>>getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).get();
    }
}
