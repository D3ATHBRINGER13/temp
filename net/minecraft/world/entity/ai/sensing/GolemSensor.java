package net.minecraft.world.entity.ai.sensing;

import net.minecraft.world.entity.EntityType;
import java.util.Optional;
import net.minecraft.world.entity.ai.Brain;
import java.util.List;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public class GolemSensor extends Sensor<LivingEntity> {
    public GolemSensor() {
        this(200);
    }
    
    public GolemSensor(final int integer) {
        super(integer);
    }
    
    @Override
    protected void doTick(final ServerLevel vk, final LivingEntity aix) {
        checkForNearbyGolem(vk.getGameTime(), aix);
    }
    
    @Override
    public Set<MemoryModuleType<?>> requires() {
        return (Set<MemoryModuleType<?>>)ImmutableSet.of(MemoryModuleType.LIVING_ENTITIES);
    }
    
    public static void checkForNearbyGolem(final long long1, final LivingEntity aix) {
        final Brain<?> ajm4 = aix.getBrain();
        final Optional<List<LivingEntity>> optional5 = ajm4.<List<LivingEntity>>getMemory(MemoryModuleType.LIVING_ENTITIES);
        if (!optional5.isPresent()) {
            return;
        }
        final boolean boolean6 = ((List)optional5.get()).stream().anyMatch(aix -> aix.getType().equals(EntityType.IRON_GOLEM));
        if (boolean6) {
            ajm4.<Long>setMemory(MemoryModuleType.GOLEM_LAST_SEEN_TIME, long1);
        }
    }
}
