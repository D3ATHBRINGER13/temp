package net.minecraft.world.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.LivingEntity;

public class UpdateActivityFromSchedule extends Behavior<LivingEntity> {
    public UpdateActivityFromSchedule() {
        super((Map)ImmutableMap.of());
    }
    
    @Override
    protected void start(final ServerLevel vk, final LivingEntity aix, final long long3) {
        aix.getBrain().updateActivity(vk.getDayTime(), vk.getGameTime());
    }
}
