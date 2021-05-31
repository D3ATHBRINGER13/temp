package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;

public class SetWalkTargetFromBlockMemory extends Behavior<Villager> {
    private final MemoryModuleType<GlobalPos> memoryType;
    private final float speed;
    private final int closeEnoughDist;
    private final int tooFarDistance;
    private final int tooLongUnreachableDuration;
    
    public SetWalkTargetFromBlockMemory(final MemoryModuleType<GlobalPos> apj, final float float2, final int integer3, final int integer4, final int integer5) {
        super((Map)ImmutableMap.of(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, apj, MemoryStatus.VALUE_PRESENT));
        this.memoryType = apj;
        this.speed = float2;
        this.closeEnoughDist = integer3;
        this.tooFarDistance = integer4;
        this.tooLongUnreachableDuration = integer5;
    }
    
    private void dropPOI(final Villager avt, final long long2) {
        final Brain<?> ajm5 = avt.getBrain();
        avt.releasePoi(this.memoryType);
        ajm5.<GlobalPos>eraseMemory(this.memoryType);
        ajm5.<Long>setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, long2);
    }
    
    @Override
    protected void start(final ServerLevel vk, final Villager avt, final long long3) {
        final Brain<?> ajm6 = avt.getBrain();
        ajm6.<GlobalPos>getMemory(this.memoryType).ifPresent(fd -> {
            if (this.tiredOfTryingToFindTarget(vk, avt)) {
                this.dropPOI(avt, long3);
            }
            else if (this.tooFar(vk, avt, fd)) {
                Vec3 csi8 = null;
                int integer9 = 0;
                final int integer10 = 1000;
                while (integer9 < 1000 && (csi8 == null || this.tooFar(vk, avt, GlobalPos.of(avt.dimension, new BlockPos(csi8))))) {
                    csi8 = RandomPos.getPosTowards(avt, 15, 7, new Vec3(fd.pos()));
                    ++integer9;
                }
                if (integer9 == 1000) {
                    this.dropPOI(avt, long3);
                    return;
                }
                ajm6.<WalkTarget>setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(csi8, this.speed, this.closeEnoughDist));
            }
            else if (!this.closeEnough(vk, avt, fd)) {
                ajm6.<WalkTarget>setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(fd.pos(), this.speed, this.closeEnoughDist));
            }
        });
    }
    
    private boolean tiredOfTryingToFindTarget(final ServerLevel vk, final Villager avt) {
        final Optional<Long> optional4 = avt.getBrain().<Long>getMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        return optional4.isPresent() && vk.getGameTime() - (long)optional4.get() > this.tooLongUnreachableDuration;
    }
    
    private boolean tooFar(final ServerLevel vk, final Villager avt, final GlobalPos fd) {
        return fd.dimension() != vk.getDimension().getType() || fd.pos().distManhattan(new BlockPos(avt)) > this.tooFarDistance;
    }
    
    private boolean closeEnough(final ServerLevel vk, final Villager avt, final GlobalPos fd) {
        return fd.dimension() == vk.getDimension().getType() && fd.pos().distManhattan(new BlockPos(avt)) <= this.closeEnoughDist;
    }
}
