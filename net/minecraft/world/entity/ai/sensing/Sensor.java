package net.minecraft.world.entity.ai.sensing;

import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import java.util.Random;
import net.minecraft.world.entity.LivingEntity;

public abstract class Sensor<E extends LivingEntity> {
    private static final Random RANDOM;
    private final int scanRate;
    private long timeToTick;
    
    public Sensor(final int integer) {
        this.scanRate = integer;
        this.timeToTick = Sensor.RANDOM.nextInt(integer);
    }
    
    public Sensor() {
        this(20);
    }
    
    public final void tick(final ServerLevel vk, final E aix) {
        final long timeToTick = this.timeToTick - 1L;
        this.timeToTick = timeToTick;
        if (timeToTick <= 0L) {
            this.timeToTick = this.scanRate;
            this.doTick(vk, aix);
        }
    }
    
    protected abstract void doTick(final ServerLevel vk, final E aix);
    
    public abstract Set<MemoryModuleType<?>> requires();
    
    static {
        RANDOM = new Random();
    }
}
