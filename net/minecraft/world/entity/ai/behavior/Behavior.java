package net.minecraft.world.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import java.util.Map;
import net.minecraft.world.entity.LivingEntity;

public abstract class Behavior<E extends LivingEntity> {
    private final Map<MemoryModuleType<?>, MemoryStatus> entryCondition;
    private Status status;
    private long endTimestamp;
    private final int minDuration;
    private final int maxDuration;
    
    public Behavior(final Map<MemoryModuleType<?>, MemoryStatus> map) {
        this(map, 60);
    }
    
    public Behavior(final Map<MemoryModuleType<?>, MemoryStatus> map, final int integer) {
        this(map, integer, integer);
    }
    
    public Behavior(final Map<MemoryModuleType<?>, MemoryStatus> map, final int integer2, final int integer3) {
        this.status = Status.STOPPED;
        this.minDuration = integer2;
        this.maxDuration = integer3;
        this.entryCondition = map;
    }
    
    public Status getStatus() {
        return this.status;
    }
    
    public final boolean tryStart(final ServerLevel vk, final E aix, final long long3) {
        if (this.hasRequiredMemories(aix) && this.checkExtraStartConditions(vk, aix)) {
            this.status = Status.RUNNING;
            final int integer6 = this.minDuration + vk.getRandom().nextInt(this.maxDuration + 1 - this.minDuration);
            this.endTimestamp = long3 + integer6;
            this.start(vk, aix, long3);
            return true;
        }
        return false;
    }
    
    protected void start(final ServerLevel vk, final E aix, final long long3) {
    }
    
    public final void tickOrStop(final ServerLevel vk, final E aix, final long long3) {
        if (!this.timedOut(long3) && this.canStillUse(vk, aix, long3)) {
            this.tick(vk, aix, long3);
        }
        else {
            this.doStop(vk, aix, long3);
        }
    }
    
    protected void tick(final ServerLevel vk, final E aix, final long long3) {
    }
    
    public final void doStop(final ServerLevel vk, final E aix, final long long3) {
        this.status = Status.STOPPED;
        this.stop(vk, aix, long3);
    }
    
    protected void stop(final ServerLevel vk, final E aix, final long long3) {
    }
    
    protected boolean canStillUse(final ServerLevel vk, final E aix, final long long3) {
        return false;
    }
    
    protected boolean timedOut(final long long1) {
        return long1 > this.endTimestamp;
    }
    
    protected boolean checkExtraStartConditions(final ServerLevel vk, final E aix) {
        return true;
    }
    
    public String toString() {
        return this.getClass().getSimpleName();
    }
    
    private boolean hasRequiredMemories(final E aix) {
        return this.entryCondition.entrySet().stream().allMatch(entry -> {
            final MemoryModuleType<?> apj3 = entry.getKey();
            final MemoryStatus apk4 = (MemoryStatus)entry.getValue();
            return aix.getBrain().checkMemory(apj3, apk4);
        });
    }
    
    public enum Status {
        STOPPED, 
        RUNNING;
    }
}
