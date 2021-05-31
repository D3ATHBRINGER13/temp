package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.npc.Villager;

public class VillagerCalmDown extends Behavior<Villager> {
    public VillagerCalmDown() {
        super((Map)ImmutableMap.of());
    }
    
    @Override
    protected void start(final ServerLevel vk, final Villager avt, final long long3) {
        final boolean boolean6 = VillagerPanicTrigger.isHurt(avt) || VillagerPanicTrigger.hasHostile(avt) || isCloseToEntityThatHurtMe(avt);
        if (!boolean6) {
            avt.getBrain().<DamageSource>eraseMemory(MemoryModuleType.HURT_BY);
            avt.getBrain().<LivingEntity>eraseMemory(MemoryModuleType.HURT_BY_ENTITY);
            avt.getBrain().updateActivity(vk.getDayTime(), vk.getGameTime());
        }
    }
    
    private static boolean isCloseToEntityThatHurtMe(final Villager avt) {
        return avt.getBrain().<LivingEntity>getMemory(MemoryModuleType.HURT_BY_ENTITY).filter(aix -> aix.distanceToSqr(avt) <= 36.0).isPresent();
    }
}
