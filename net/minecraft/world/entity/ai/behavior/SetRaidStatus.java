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

public class SetRaidStatus extends Behavior<LivingEntity> {
    public SetRaidStatus() {
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
        if (axk7 != null) {
            if (!axk7.hasFirstWaveSpawned() || axk7.isBetweenWaves()) {
                ajm6.setDefaultActivity(Activity.PRE_RAID);
                ajm6.setActivity(Activity.PRE_RAID);
            }
            else {
                ajm6.setDefaultActivity(Activity.RAID);
                ajm6.setActivity(Activity.RAID);
            }
        }
    }
}
