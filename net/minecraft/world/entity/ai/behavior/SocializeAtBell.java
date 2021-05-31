package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.Entity;
import java.util.Optional;
import net.minecraft.world.entity.ai.Brain;
import java.util.List;
import net.minecraft.core.Position;
import java.util.Objects;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.LivingEntity;

public class SocializeAtBell extends Behavior<LivingEntity> {
    public SocializeAtBell() {
        super((Map)ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.MEETING_POINT, MemoryStatus.VALUE_PRESENT, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT, MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_ABSENT));
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final LivingEntity aix) {
        final Brain<?> ajm4 = aix.getBrain();
        final Optional<GlobalPos> optional5 = ajm4.<GlobalPos>getMemory(MemoryModuleType.MEETING_POINT);
        return vk.getRandom().nextInt(100) == 0 && optional5.isPresent() && Objects.equals(vk.getDimension().getType(), ((GlobalPos)optional5.get()).dimension()) && ((GlobalPos)optional5.get()).pos().closerThan(aix.position(), 4.0) && ((List)ajm4.<List<LivingEntity>>getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).get()).stream().anyMatch(aix -> EntityType.VILLAGER.equals(aix.getType()));
    }
    
    @Override
    protected void start(final ServerLevel vk, final LivingEntity aix, final long long3) {
        final Brain<?> ajm6 = aix.getBrain();
        ajm6.<List<LivingEntity>>getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).ifPresent(list -> list.stream().filter(aix -> EntityType.VILLAGER.equals(aix.getType())).filter(aix2 -> aix2.distanceToSqr(aix) <= 32.0).findFirst().ifPresent(aix -> {
            ajm6.<LivingEntity>setMemory(MemoryModuleType.INTERACTION_TARGET, aix);
            ajm6.<EntityPosWrapper>setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(aix));
            ajm6.<WalkTarget>setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityPosWrapper(aix), 0.3f, 1));
        }));
    }
}
