package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.npc.Villager;

public class VillagerPanicTrigger extends Behavior<Villager> {
    public VillagerPanicTrigger() {
        super((Map)ImmutableMap.of());
    }
    
    @Override
    protected boolean canStillUse(final ServerLevel vk, final Villager avt, final long long3) {
        return isHurt(avt) || hasHostile(avt);
    }
    
    @Override
    protected void start(final ServerLevel vk, final Villager avt, final long long3) {
        if (isHurt(avt) || hasHostile(avt)) {
            final Brain<?> ajm6 = avt.getBrain();
            if (!ajm6.isActive(Activity.PANIC)) {
                ajm6.<Path>eraseMemory(MemoryModuleType.PATH);
                ajm6.<WalkTarget>eraseMemory(MemoryModuleType.WALK_TARGET);
                ajm6.<PositionWrapper>eraseMemory(MemoryModuleType.LOOK_TARGET);
                ajm6.<Villager>eraseMemory(MemoryModuleType.BREED_TARGET);
                ajm6.<LivingEntity>eraseMemory(MemoryModuleType.INTERACTION_TARGET);
            }
            ajm6.setActivity(Activity.PANIC);
        }
    }
    
    @Override
    protected void tick(final ServerLevel vk, final Villager avt, final long long3) {
        if (long3 % 100L == 0L) {
            avt.spawnGolemIfNeeded(long3, 3);
        }
    }
    
    public static boolean hasHostile(final LivingEntity aix) {
        return aix.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_HOSTILE);
    }
    
    public static boolean isHurt(final LivingEntity aix) {
        return aix.getBrain().hasMemoryValue(MemoryModuleType.HURT_BY);
    }
}
