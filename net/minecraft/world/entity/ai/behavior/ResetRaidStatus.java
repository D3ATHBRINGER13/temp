package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.LivingEntity;

public class ResetRaidStatus extends Behavior<LivingEntity> {
    public ResetRaidStatus() {
        super((Map)ImmutableMap.of());
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final LivingEntity aix) {
        return vk.random.nextInt(20) == 0;
    }
    
    @Override
    protected void start(final ServerLevel vk, final LivingEntity aix, final long long3) {
        final Brain<?> ajm6 = aix.getBrain();
        final Raid axk7 = vk.getRaidAt(new BlockPos(aix));
        if (axk7 == null || axk7.isStopped() || axk7.isLoss()) {
            ajm6.setDefaultActivity(Activity.IDLE);
            ajm6.updateActivity(vk.getDayTime(), vk.getGameTime());
        }
    }
}
