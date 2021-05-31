package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.RandomPos;
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

public class StrollAroundPoi extends Behavior<PathfinderMob> {
    private final MemoryModuleType<GlobalPos> memoryType;
    private long nextOkStartTime;
    private final int maxDistanceFromPoi;
    
    public StrollAroundPoi(final MemoryModuleType<GlobalPos> apj, final int integer) {
        super((Map)ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, apj, MemoryStatus.VALUE_PRESENT));
        this.memoryType = apj;
        this.maxDistanceFromPoi = integer;
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final PathfinderMob aje) {
        final Optional<GlobalPos> optional4 = aje.getBrain().<GlobalPos>getMemory(this.memoryType);
        return optional4.isPresent() && Objects.equals(vk.getDimension().getType(), ((GlobalPos)optional4.get()).dimension()) && ((GlobalPos)optional4.get()).pos().closerThan(aje.position(), this.maxDistanceFromPoi);
    }
    
    @Override
    protected void start(final ServerLevel vk, final PathfinderMob aje, final long long3) {
        if (long3 > this.nextOkStartTime) {
            final Optional<Vec3> optional6 = (Optional<Vec3>)Optional.ofNullable(RandomPos.getLandPos(aje, 8, 6));
            aje.getBrain().<WalkTarget>setMemory(MemoryModuleType.WALK_TARGET, (java.util.Optional<WalkTarget>)optional6.map(csi -> new WalkTarget(csi, 0.4f, 1)));
            this.nextOkStartTime = long3 + 180L;
        }
    }
}
