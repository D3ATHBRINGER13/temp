package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.LivingEntity;

public class WakeUp extends Behavior<LivingEntity> {
    public WakeUp() {
        super((Map)ImmutableMap.of());
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final LivingEntity aix) {
        return !aix.getBrain().isActive(Activity.REST) && aix.isSleeping();
    }
    
    @Override
    protected void start(final ServerLevel vk, final LivingEntity aix, final long long3) {
        aix.stopSleeping();
    }
}
