package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.PathfinderMob;

public class SetWalkTargetAwayFromEntity extends Behavior<PathfinderMob> {
    private final MemoryModuleType<? extends Entity> memory;
    private final float speed;
    
    public SetWalkTargetAwayFromEntity(final MemoryModuleType<? extends Entity> apj, final float float2) {
        super((Map)ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, apj, MemoryStatus.VALUE_PRESENT));
        this.memory = apj;
        this.speed = float2;
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final PathfinderMob aje) {
        final Entity aio4 = (Entity)aje.getBrain().getMemory(this.memory).get();
        return aje.distanceToSqr(aio4) < 36.0;
    }
    
    @Override
    protected void start(final ServerLevel vk, final PathfinderMob aje, final long long3) {
        final Entity aio6 = (Entity)aje.getBrain().getMemory(this.memory).get();
        moveAwayFromMob(aje, aio6, this.speed);
    }
    
    public static void moveAwayFromMob(final PathfinderMob aje, final Entity aio, final float float3) {
        for (int integer4 = 0; integer4 < 10; ++integer4) {
            final Vec3 csi5 = new Vec3(aio.x, aio.y, aio.z);
            final Vec3 csi6 = RandomPos.getLandPosAvoid(aje, 16, 7, csi5);
            if (csi6 != null) {
                aje.getBrain().<WalkTarget>setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(csi6, float3, 0));
                return;
            }
        }
    }
}
