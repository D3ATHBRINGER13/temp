package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.LivingEntity;

public class ReactToBell extends Behavior<LivingEntity> {
    public ReactToBell() {
        super((Map)ImmutableMap.of(MemoryModuleType.HEARD_BELL_TIME, MemoryStatus.VALUE_PRESENT));
    }
    
    @Override
    protected void start(final ServerLevel vk, final LivingEntity aix, final long long3) {
        final Brain<?> ajm6 = aix.getBrain();
        final Raid axk7 = vk.getRaidAt(new BlockPos(aix));
        if (axk7 == null) {
            ajm6.setActivity(Activity.HIDE);
        }
    }
}
