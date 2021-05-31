package net.minecraft.world.entity.ai.sensing;

import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.world.entity.ai.Brain;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.function.Predicate;
import net.minecraft.world.entity.EntitySelector;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public class PlayerSensor extends Sensor<LivingEntity> {
    @Override
    protected void doTick(final ServerLevel vk, final LivingEntity aix) {
        final List<Player> list4 = (List<Player>)vk.players().stream().filter((Predicate)EntitySelector.NO_SPECTATORS).filter(vl -> aix.distanceToSqr(vl) < 256.0).sorted(Comparator.comparingDouble(aix::distanceToSqr)).collect(Collectors.toList());
        final Brain<?> ajm5 = aix.getBrain();
        ajm5.<List<Player>>setMemory(MemoryModuleType.NEAREST_PLAYERS, list4);
        ajm5.<Player>setMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER, (java.util.Optional<Player>)list4.stream().filter(aix::canSee).findFirst());
    }
    
    @Override
    public Set<MemoryModuleType<?>> requires() {
        return (Set<MemoryModuleType<?>>)ImmutableSet.of(MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER);
    }
}
