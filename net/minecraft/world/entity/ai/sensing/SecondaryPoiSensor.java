package net.minecraft.world.entity.ai.sensing;

import net.minecraft.world.entity.LivingEntity;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.level.dimension.DimensionType;
import java.util.List;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.core.GlobalPos;
import com.google.common.collect.Lists;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.Villager;

public class SecondaryPoiSensor extends Sensor<Villager> {
    public SecondaryPoiSensor() {
        super(40);
    }
    
    @Override
    protected void doTick(final ServerLevel vk, final Villager avt) {
        final DimensionType byn4 = vk.getDimension().getType();
        final BlockPos ew5 = new BlockPos(avt);
        final List<GlobalPos> list6 = (List<GlobalPos>)Lists.newArrayList();
        final int integer7 = 4;
        for (int integer8 = -4; integer8 <= 4; ++integer8) {
            for (int integer9 = -2; integer9 <= 2; ++integer9) {
                for (int integer10 = -4; integer10 <= 4; ++integer10) {
                    final BlockPos ew6 = ew5.offset(integer8, integer9, integer10);
                    if (avt.getVillagerData().getProfession().getSecondaryPoi().contains(vk.getBlockState(ew6).getBlock())) {
                        list6.add(GlobalPos.of(byn4, ew6));
                    }
                }
            }
        }
        final Brain<?> ajm8 = avt.getBrain();
        if (!list6.isEmpty()) {
            ajm8.<List<GlobalPos>>setMemory(MemoryModuleType.SECONDARY_JOB_SITE, list6);
        }
        else {
            ajm8.<List<GlobalPos>>eraseMemory(MemoryModuleType.SECONDARY_JOB_SITE);
        }
    }
    
    @Override
    public Set<MemoryModuleType<?>> requires() {
        return (Set<MemoryModuleType<?>>)ImmutableSet.of(MemoryModuleType.SECONDARY_JOB_SITE);
    }
}
