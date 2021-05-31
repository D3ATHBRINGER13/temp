package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.LivingEntity;
import java.util.Optional;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;

public class JumpOnBed extends Behavior<Mob> {
    private final float speed;
    @Nullable
    private BlockPos targetBed;
    private int remainingTimeToReachBed;
    private int remainingJumps;
    private int remainingCooldownUntilNextJump;
    
    public JumpOnBed(final float float1) {
        super((Map)ImmutableMap.of(MemoryModuleType.NEAREST_BED, MemoryStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
        this.speed = float1;
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final Mob aiy) {
        return aiy.isBaby() && this.nearBed(vk, aiy);
    }
    
    @Override
    protected void start(final ServerLevel vk, final Mob aiy, final long long3) {
        super.start(vk, aiy, long3);
        this.getNearestBed(aiy).ifPresent(ew -> {
            this.targetBed = ew;
            this.remainingTimeToReachBed = 100;
            this.remainingJumps = 3 + vk.random.nextInt(4);
            this.remainingCooldownUntilNextJump = 0;
            this.startWalkingTowardsBed(aiy, ew);
        });
    }
    
    @Override
    protected void stop(final ServerLevel vk, final Mob aiy, final long long3) {
        super.stop(vk, aiy, long3);
        this.targetBed = null;
        this.remainingTimeToReachBed = 0;
        this.remainingJumps = 0;
        this.remainingCooldownUntilNextJump = 0;
    }
    
    @Override
    protected boolean canStillUse(final ServerLevel vk, final Mob aiy, final long long3) {
        return aiy.isBaby() && this.targetBed != null && this.isBed(vk, this.targetBed) && !this.tiredOfWalking(vk, aiy) && !this.tiredOfJumping(vk, aiy);
    }
    
    @Override
    protected boolean timedOut(final long long1) {
        return false;
    }
    
    @Override
    protected void tick(final ServerLevel vk, final Mob aiy, final long long3) {
        if (!this.onOrOverBed(vk, aiy)) {
            --this.remainingTimeToReachBed;
            return;
        }
        if (this.remainingCooldownUntilNextJump > 0) {
            --this.remainingCooldownUntilNextJump;
            return;
        }
        if (this.onBedSurface(vk, aiy)) {
            aiy.getJumpControl().jump();
            --this.remainingJumps;
            this.remainingCooldownUntilNextJump = 5;
        }
    }
    
    private void startWalkingTowardsBed(final Mob aiy, final BlockPos ew) {
        aiy.getBrain().<WalkTarget>setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(ew, this.speed, 0));
    }
    
    private boolean nearBed(final ServerLevel vk, final Mob aiy) {
        return this.onOrOverBed(vk, aiy) || this.getNearestBed(aiy).isPresent();
    }
    
    private boolean onOrOverBed(final ServerLevel vk, final Mob aiy) {
        final BlockPos ew4 = new BlockPos(aiy);
        final BlockPos ew5 = ew4.below();
        return this.isBed(vk, ew4) || this.isBed(vk, ew5);
    }
    
    private boolean onBedSurface(final ServerLevel vk, final Mob aiy) {
        return this.isBed(vk, new BlockPos(aiy));
    }
    
    private boolean isBed(final ServerLevel vk, final BlockPos ew) {
        return vk.getBlockState(ew).is(BlockTags.BEDS);
    }
    
    private Optional<BlockPos> getNearestBed(final Mob aiy) {
        return aiy.getBrain().<BlockPos>getMemory(MemoryModuleType.NEAREST_BED);
    }
    
    private boolean tiredOfWalking(final ServerLevel vk, final Mob aiy) {
        return !this.onOrOverBed(vk, aiy) && this.remainingTimeToReachBed <= 0;
    }
    
    private boolean tiredOfJumping(final ServerLevel vk, final Mob aiy) {
        return this.onOrOverBed(vk, aiy) && this.remainingJumps <= 0;
    }
}
