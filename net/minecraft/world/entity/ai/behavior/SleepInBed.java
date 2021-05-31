package net.minecraft.world.entity.ai.behavior;

import java.util.List;
import com.google.common.collect.ImmutableList;
import java.util.Set;
import net.minecraft.core.BlockPos;
import java.util.Optional;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.Position;
import java.util.Objects;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.LivingEntity;

public class SleepInBed extends Behavior<LivingEntity> {
    private long nextOkStartTime;
    
    public SleepInBed() {
        super((Map)ImmutableMap.of(MemoryModuleType.HOME, MemoryStatus.VALUE_PRESENT));
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final LivingEntity aix) {
        if (aix.isPassenger()) {
            return false;
        }
        final GlobalPos fd4 = (GlobalPos)aix.getBrain().<GlobalPos>getMemory(MemoryModuleType.HOME).get();
        if (!Objects.equals(vk.getDimension().getType(), fd4.dimension())) {
            return false;
        }
        final BlockState bvt5 = vk.getBlockState(fd4.pos());
        return fd4.pos().closerThan(aix.position(), 2.0) && bvt5.getBlock().is(BlockTags.BEDS) && !bvt5.<Boolean>getValue((Property<Boolean>)BedBlock.OCCUPIED);
    }
    
    @Override
    protected boolean canStillUse(final ServerLevel vk, final LivingEntity aix, final long long3) {
        final Optional<GlobalPos> optional6 = aix.getBrain().<GlobalPos>getMemory(MemoryModuleType.HOME);
        if (!optional6.isPresent()) {
            return false;
        }
        final BlockPos ew7 = ((GlobalPos)optional6.get()).pos();
        return aix.getBrain().isActive(Activity.REST) && aix.y > ew7.getY() + 0.4 && ew7.closerThan(aix.position(), 1.14);
    }
    
    @Override
    protected void start(final ServerLevel vk, final LivingEntity aix, final long long3) {
        if (long3 > this.nextOkStartTime) {
            aix.getBrain().<Set<GlobalPos>>getMemory(MemoryModuleType.OPENED_DOORS).ifPresent(set -> InteractWithDoor.closeAllOpenedDoors(vk, (List<BlockPos>)ImmutableList.of(), 0, aix, aix.getBrain()));
            aix.startSleeping(((GlobalPos)aix.getBrain().<GlobalPos>getMemory(MemoryModuleType.HOME).get()).pos());
        }
    }
    
    @Override
    protected boolean timedOut(final long long1) {
        return false;
    }
    
    @Override
    protected void stop(final ServerLevel vk, final LivingEntity aix, final long long3) {
        if (aix.isSleeping()) {
            aix.stopSleeping();
            this.nextOkStartTime = long3 + 40L;
        }
    }
}
