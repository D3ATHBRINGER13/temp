package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.LivingEntity;

public class SetWalkTargetFromLookTarget extends Behavior<LivingEntity> {
    private final float speed;
    private final int closeEnoughDistance;
    
    public SetWalkTargetFromLookTarget(final float float1, final int integer) {
        super((Map)ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_PRESENT));
        this.speed = float1;
        this.closeEnoughDistance = integer;
    }
    
    @Override
    protected void start(final ServerLevel vk, final LivingEntity aix, final long long3) {
        final Brain<?> ajm6 = aix.getBrain();
        final PositionWrapper akw7 = (PositionWrapper)ajm6.<PositionWrapper>getMemory(MemoryModuleType.LOOK_TARGET).get();
        ajm6.<WalkTarget>setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(akw7, this.speed, this.closeEnoughDistance));
    }
}
