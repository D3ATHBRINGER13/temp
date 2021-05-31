package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.LivingEntity;

public class SetHiddenState extends Behavior<LivingEntity> {
    private final int closeEnoughDist;
    private final int stayHiddenTicks;
    private int ticksHidden;
    
    public SetHiddenState(final int integer1, final int integer2) {
        super((Map)ImmutableMap.of(MemoryModuleType.HIDING_PLACE, MemoryStatus.VALUE_PRESENT, MemoryModuleType.HEARD_BELL_TIME, MemoryStatus.VALUE_PRESENT));
        this.stayHiddenTicks = integer1 * 20;
        this.ticksHidden = 0;
        this.closeEnoughDist = integer2;
    }
    
    @Override
    protected void start(final ServerLevel vk, final LivingEntity aix, final long long3) {
        final Brain<?> ajm6 = aix.getBrain();
        final Optional<Long> optional7 = ajm6.<Long>getMemory(MemoryModuleType.HEARD_BELL_TIME);
        final boolean boolean8 = (long)optional7.get() + 300L <= long3;
        if (this.ticksHidden > this.stayHiddenTicks || boolean8) {
            ajm6.<Long>eraseMemory(MemoryModuleType.HEARD_BELL_TIME);
            ajm6.<GlobalPos>eraseMemory(MemoryModuleType.HIDING_PLACE);
            ajm6.updateActivity(vk.getDayTime(), vk.getGameTime());
            this.ticksHidden = 0;
            return;
        }
        final BlockPos ew9 = ((GlobalPos)ajm6.<GlobalPos>getMemory(MemoryModuleType.HIDING_PLACE).get()).pos();
        if (ew9.closerThan(new BlockPos(aix), this.closeEnoughDist + 1)) {
            ++this.ticksHidden;
        }
    }
}
