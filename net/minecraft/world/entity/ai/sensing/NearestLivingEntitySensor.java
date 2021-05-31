package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.world.entity.ai.Brain;
import java.util.stream.Collectors;
import java.util.List;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import java.util.Comparator;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.LivingEntity;

public class NearestLivingEntitySensor extends Sensor<LivingEntity> {
    private static final TargetingConditions TARGETING;
    
    @Override
    protected void doTick(final ServerLevel vk, final LivingEntity aix) {
        final List<LivingEntity> list4 = vk.<LivingEntity>getEntitiesOfClass((java.lang.Class<? extends LivingEntity>)LivingEntity.class, aix.getBoundingBox().inflate(16.0, 16.0, 16.0), (java.util.function.Predicate<? super LivingEntity>)(aix2 -> aix2 != aix && aix2.isAlive()));
        list4.sort(Comparator.comparingDouble(aix::distanceToSqr));
        final Brain<?> ajm5 = aix.getBrain();
        ajm5.<List<LivingEntity>>setMemory(MemoryModuleType.LIVING_ENTITIES, list4);
        ajm5.setMemory((MemoryModuleType<Object>)MemoryModuleType.VISIBLE_LIVING_ENTITIES, list4.stream().filter(aix2 -> NearestLivingEntitySensor.TARGETING.test(aix, aix2)).filter(aix::canSee).collect(Collectors.toList()));
    }
    
    @Override
    public Set<MemoryModuleType<?>> requires() {
        return (Set<MemoryModuleType<?>>)ImmutableSet.of(MemoryModuleType.LIVING_ENTITIES, MemoryModuleType.VISIBLE_LIVING_ENTITIES);
    }
    
    static {
        TARGETING = new TargetingConditions().range(16.0).allowSameTeam().allowNonAttackable().allowUnseeable();
    }
}
