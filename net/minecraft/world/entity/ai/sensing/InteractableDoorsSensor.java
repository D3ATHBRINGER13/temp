package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.level.dimension.DimensionType;
import java.util.List;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.core.GlobalPos;
import net.minecraft.tags.BlockTags;
import com.google.common.collect.Lists;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public class InteractableDoorsSensor extends Sensor<LivingEntity> {
    @Override
    protected void doTick(final ServerLevel vk, final LivingEntity aix) {
        final DimensionType byn4 = vk.getDimension().getType();
        final BlockPos ew5 = new BlockPos(aix);
        final List<GlobalPos> list6 = (List<GlobalPos>)Lists.newArrayList();
        for (int integer7 = -1; integer7 <= 1; ++integer7) {
            for (int integer8 = -1; integer8 <= 1; ++integer8) {
                for (int integer9 = -1; integer9 <= 1; ++integer9) {
                    final BlockPos ew6 = ew5.offset(integer7, integer8, integer9);
                    if (vk.getBlockState(ew6).is(BlockTags.WOODEN_DOORS)) {
                        list6.add(GlobalPos.of(byn4, ew6));
                    }
                }
            }
        }
        final Brain<?> ajm7 = aix.getBrain();
        if (!list6.isEmpty()) {
            ajm7.<List<GlobalPos>>setMemory(MemoryModuleType.INTERACTABLE_DOORS, list6);
        }
        else {
            ajm7.<List<GlobalPos>>eraseMemory(MemoryModuleType.INTERACTABLE_DOORS);
        }
    }
    
    @Override
    public Set<MemoryModuleType<?>> requires() {
        return (Set<MemoryModuleType<?>>)ImmutableSet.of(MemoryModuleType.INTERACTABLE_DOORS);
    }
}
