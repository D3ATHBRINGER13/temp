package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import java.util.Optional;
import net.minecraft.core.Position;
import java.util.Objects;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.PathfinderMob;

public class StrollToPoi extends Behavior<PathfinderMob> {
    private final MemoryModuleType<GlobalPos> memoryType;
    private final int closeEnoughDist;
    private final int maxDistanceFromPoi;
    private long nextOkStartTime;
    
    public StrollToPoi(final MemoryModuleType<GlobalPos> apj, final int integer2, final int integer3) {
        super((Map)ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, apj, MemoryStatus.VALUE_PRESENT));
        this.memoryType = apj;
        this.closeEnoughDist = integer2;
        this.maxDistanceFromPoi = integer3;
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final PathfinderMob aje) {
        final Optional<GlobalPos> optional4 = aje.getBrain().<GlobalPos>getMemory(this.memoryType);
        return optional4.isPresent() && Objects.equals(vk.getDimension().getType(), ((GlobalPos)optional4.get()).dimension()) && ((GlobalPos)optional4.get()).pos().closerThan(aje.position(), this.maxDistanceFromPoi);
    }
    
    @Override
    protected void start(final ServerLevel vk, final PathfinderMob aje, final long long3) {
        if (long3 > this.nextOkStartTime) {
            final Brain<?> ajm6 = aje.getBrain();
            final Optional<GlobalPos> optional7 = ajm6.<GlobalPos>getMemory(this.memoryType);
            optional7.ifPresent(fd -> ajm6.<WalkTarget>setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(fd.pos(), 0.4f, this.closeEnoughDist)));
            this.nextOkStartTime = long3 + 80L;
        }
    }
}
