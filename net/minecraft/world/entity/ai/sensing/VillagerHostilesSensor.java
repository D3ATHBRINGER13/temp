package net.minecraft.world.entity.ai.sensing;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import java.util.List;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import java.util.Set;
import net.minecraft.world.entity.EntityType;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.LivingEntity;

public class VillagerHostilesSensor extends Sensor<LivingEntity> {
    private static final ImmutableMap<EntityType<?>, Float> ACCEPTABLE_DISTANCE_FROM_HOSTILES;
    
    @Override
    public Set<MemoryModuleType<?>> requires() {
        return (Set<MemoryModuleType<?>>)ImmutableSet.of(MemoryModuleType.NEAREST_HOSTILE);
    }
    
    @Override
    protected void doTick(final ServerLevel vk, final LivingEntity aix) {
        aix.getBrain().<LivingEntity>setMemory(MemoryModuleType.NEAREST_HOSTILE, this.getNearestHostile(aix));
    }
    
    private Optional<LivingEntity> getNearestHostile(final LivingEntity aix) {
        return (Optional<LivingEntity>)this.getVisibleEntities(aix).flatMap(list -> list.stream().filter(this::isHostile).filter(aix2 -> this.isClose(aix, aix2)).min((aix2, aix3) -> this.compareMobDistance(aix, aix2, aix3)));
    }
    
    private Optional<List<LivingEntity>> getVisibleEntities(final LivingEntity aix) {
        return aix.getBrain().<List<LivingEntity>>getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES);
    }
    
    private int compareMobDistance(final LivingEntity aix1, final LivingEntity aix2, final LivingEntity aix3) {
        return Mth.floor(aix2.distanceToSqr(aix1) - aix3.distanceToSqr(aix1));
    }
    
    private boolean isClose(final LivingEntity aix1, final LivingEntity aix2) {
        final float float4 = (float)VillagerHostilesSensor.ACCEPTABLE_DISTANCE_FROM_HOSTILES.get(aix2.getType());
        return aix2.distanceToSqr(aix1) <= float4 * float4;
    }
    
    private boolean isHostile(final LivingEntity aix) {
        return VillagerHostilesSensor.ACCEPTABLE_DISTANCE_FROM_HOSTILES.containsKey(aix.getType());
    }
    
    static {
        ACCEPTABLE_DISTANCE_FROM_HOSTILES = ImmutableMap.builder().put(EntityType.DROWNED, 8.0f).put(EntityType.EVOKER, 12.0f).put(EntityType.HUSK, 8.0f).put(EntityType.ILLUSIONER, 12.0f).put(EntityType.PILLAGER, 15.0f).put(EntityType.RAVAGER, 12.0f).put(EntityType.VEX, 8.0f).put(EntityType.VINDICATOR, 10.0f).put(EntityType.ZOMBIE, 8.0f).put(EntityType.ZOMBIE_VILLAGER, 8.0f).build();
    }
}
