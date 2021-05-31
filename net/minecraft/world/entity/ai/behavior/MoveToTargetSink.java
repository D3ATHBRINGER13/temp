package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import java.util.Optional;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.core.BlockPos;
import javax.annotation.Nullable;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.entity.Mob;

public class MoveToTargetSink extends Behavior<Mob> {
    @Nullable
    private Path path;
    @Nullable
    private BlockPos lastTargetPos;
    private float speed;
    private int remainingDelay;
    
    public MoveToTargetSink(final int integer) {
        super((Map)ImmutableMap.of(MemoryModuleType.PATH, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_PRESENT), integer);
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final Mob aiy) {
        final Brain<?> ajm4 = aiy.getBrain();
        final WalkTarget apl5 = (WalkTarget)ajm4.<WalkTarget>getMemory(MemoryModuleType.WALK_TARGET).get();
        if (!this.reachedTarget(aiy, apl5) && this.tryComputePath(aiy, apl5, vk.getGameTime())) {
            this.lastTargetPos = apl5.getTarget().getPos();
            return true;
        }
        ajm4.<WalkTarget>eraseMemory(MemoryModuleType.WALK_TARGET);
        return false;
    }
    
    @Override
    protected boolean canStillUse(final ServerLevel vk, final Mob aiy, final long long3) {
        if (this.path == null || this.lastTargetPos == null) {
            return false;
        }
        final Optional<WalkTarget> optional6 = aiy.getBrain().<WalkTarget>getMemory(MemoryModuleType.WALK_TARGET);
        final PathNavigation app7 = aiy.getNavigation();
        return !app7.isDone() && optional6.isPresent() && !this.reachedTarget(aiy, (WalkTarget)optional6.get());
    }
    
    @Override
    protected void stop(final ServerLevel vk, final Mob aiy, final long long3) {
        aiy.getNavigation().stop();
        aiy.getBrain().<WalkTarget>eraseMemory(MemoryModuleType.WALK_TARGET);
        aiy.getBrain().<Path>eraseMemory(MemoryModuleType.PATH);
        this.path = null;
    }
    
    @Override
    protected void start(final ServerLevel vk, final Mob aiy, final long long3) {
        aiy.getBrain().<Path>setMemory(MemoryModuleType.PATH, this.path);
        aiy.getNavigation().moveTo(this.path, this.speed);
        this.remainingDelay = vk.getRandom().nextInt(10);
    }
    
    @Override
    protected void tick(final ServerLevel vk, final Mob aiy, final long long3) {
        --this.remainingDelay;
        if (this.remainingDelay > 0) {
            return;
        }
        final Path cnr6 = aiy.getNavigation().getPath();
        final Brain<?> ajm7 = aiy.getBrain();
        if (this.path != cnr6) {
            this.path = cnr6;
            ajm7.<Path>setMemory(MemoryModuleType.PATH, cnr6);
        }
        if (cnr6 == null || this.lastTargetPos == null) {
            return;
        }
        final WalkTarget apl8 = (WalkTarget)ajm7.<WalkTarget>getMemory(MemoryModuleType.WALK_TARGET).get();
        if (apl8.getTarget().getPos().distSqr(this.lastTargetPos) > 4.0 && this.tryComputePath(aiy, apl8, vk.getGameTime())) {
            this.lastTargetPos = apl8.getTarget().getPos();
            this.start(vk, aiy, long3);
        }
    }
    
    private boolean tryComputePath(final Mob aiy, final WalkTarget apl, final long long3) {
        final BlockPos ew6 = apl.getTarget().getPos();
        this.path = aiy.getNavigation().createPath(ew6, 0);
        this.speed = apl.getSpeed();
        if (!this.reachedTarget(aiy, apl)) {
            final Brain<?> ajm7 = aiy.getBrain();
            final boolean boolean8 = this.path != null && this.path.canReach();
            if (boolean8) {
                ajm7.<Long>setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, (java.util.Optional<Long>)Optional.empty());
            }
            else if (!ajm7.hasMemoryValue(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)) {
                ajm7.<Long>setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, long3);
            }
            if (this.path != null) {
                return true;
            }
            final Vec3 csi9 = RandomPos.getPosTowards((PathfinderMob)aiy, 10, 7, new Vec3(ew6));
            if (csi9 != null) {
                this.path = aiy.getNavigation().createPath(csi9.x, csi9.y, csi9.z, 0);
                return this.path != null;
            }
        }
        return false;
    }
    
    private boolean reachedTarget(final Mob aiy, final WalkTarget apl) {
        return apl.getTarget().getPos().distManhattan(new BlockPos(aiy)) <= apl.getCloseEnoughDist();
    }
}
