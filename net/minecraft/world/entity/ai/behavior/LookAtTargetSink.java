package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.Mob;

public class LookAtTargetSink extends Behavior<Mob> {
    public LookAtTargetSink(final int integer1, final int integer2) {
        super((Map)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_PRESENT), integer1, integer2);
    }
    
    @Override
    protected boolean canStillUse(final ServerLevel vk, final Mob aiy, final long long3) {
        return aiy.getBrain().<PositionWrapper>getMemory(MemoryModuleType.LOOK_TARGET).filter(akw -> akw.isVisible(aiy)).isPresent();
    }
    
    @Override
    protected void stop(final ServerLevel vk, final Mob aiy, final long long3) {
        aiy.getBrain().<PositionWrapper>eraseMemory(MemoryModuleType.LOOK_TARGET);
    }
    
    @Override
    protected void tick(final ServerLevel vk, final Mob aiy, final long long3) {
        aiy.getBrain().<PositionWrapper>getMemory(MemoryModuleType.LOOK_TARGET).ifPresent(akw -> aiy.getLookControl().setLookAt(akw.getLookAtPos()));
    }
}
