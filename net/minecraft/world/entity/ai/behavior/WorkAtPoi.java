package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.core.SerializableLong;
import net.minecraft.core.Position;
import java.util.Objects;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;

public class WorkAtPoi extends Behavior<Villager> {
    private long lastCheck;
    
    public WorkAtPoi() {
        super((Map)ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED));
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final Villager avt) {
        if (vk.getGameTime() - this.lastCheck < 300L) {
            return false;
        }
        if (vk.random.nextInt(2) != 0) {
            return false;
        }
        this.lastCheck = vk.getGameTime();
        final GlobalPos fd4 = (GlobalPos)avt.getBrain().<GlobalPos>getMemory(MemoryModuleType.JOB_SITE).get();
        return Objects.equals(fd4.dimension(), vk.getDimension().getType()) && fd4.pos().closerThan(avt.position(), 1.73);
    }
    
    @Override
    protected void start(final ServerLevel vk, final Villager avt, final long long3) {
        final Brain<Villager> ajm6 = avt.getBrain();
        ajm6.<SerializableLong>setMemory(MemoryModuleType.LAST_WORKED_AT_POI, SerializableLong.of(long3));
        ajm6.<GlobalPos>getMemory(MemoryModuleType.JOB_SITE).ifPresent(fd -> ajm6.<BlockPosWrapper>setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosWrapper(fd.pos())));
        avt.playWorkSound();
        if (avt.shouldRestock()) {
            avt.restock();
        }
    }
}
