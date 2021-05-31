package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public class HurtBySensor extends Sensor<LivingEntity> {
    @Override
    protected void doTick(final ServerLevel vk, final LivingEntity aix) {
        final Brain<?> ajm4 = aix.getBrain();
        if (aix.getLastDamageSource() != null) {
            ajm4.<DamageSource>setMemory(MemoryModuleType.HURT_BY, aix.getLastDamageSource());
            final Entity aio5 = ((DamageSource)ajm4.<DamageSource>getMemory(MemoryModuleType.HURT_BY).get()).getEntity();
            if (aio5 instanceof LivingEntity) {
                ajm4.<LivingEntity>setMemory(MemoryModuleType.HURT_BY_ENTITY, (LivingEntity)aio5);
            }
        }
        else {
            ajm4.<DamageSource>eraseMemory(MemoryModuleType.HURT_BY);
        }
    }
    
    @Override
    public Set<MemoryModuleType<?>> requires() {
        return (Set<MemoryModuleType<?>>)ImmutableSet.of(MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY);
    }
}
