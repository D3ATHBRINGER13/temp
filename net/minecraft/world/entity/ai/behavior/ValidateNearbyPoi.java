package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.core.Position;
import java.util.Objects;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import java.util.function.Predicate;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.LivingEntity;

public class ValidateNearbyPoi extends Behavior<LivingEntity> {
    private final MemoryModuleType<GlobalPos> memoryType;
    private final Predicate<PoiType> poiPredicate;
    
    public ValidateNearbyPoi(final PoiType aqs, final MemoryModuleType<GlobalPos> apj) {
        super((Map)ImmutableMap.of(apj, MemoryStatus.VALUE_PRESENT));
        this.poiPredicate = aqs.getPredicate();
        this.memoryType = apj;
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final LivingEntity aix) {
        final GlobalPos fd4 = (GlobalPos)aix.getBrain().<GlobalPos>getMemory(this.memoryType).get();
        return Objects.equals(vk.getDimension().getType(), fd4.dimension()) && fd4.pos().closerThan(aix.position(), 5.0);
    }
    
    @Override
    protected void start(final ServerLevel vk, final LivingEntity aix, final long long3) {
        final Brain<?> ajm6 = aix.getBrain();
        final GlobalPos fd7 = (GlobalPos)ajm6.<GlobalPos>getMemory(this.memoryType).get();
        final ServerLevel vk2 = vk.getServer().getLevel(fd7.dimension());
        if (this.poiDoesntExist(vk2, fd7.pos()) || this.bedIsOccupied(vk2, fd7.pos(), aix)) {
            ajm6.<GlobalPos>eraseMemory(this.memoryType);
        }
    }
    
    private boolean bedIsOccupied(final ServerLevel vk, final BlockPos ew, final LivingEntity aix) {
        final BlockState bvt5 = vk.getBlockState(ew);
        return bvt5.getBlock().is(BlockTags.BEDS) && bvt5.<Boolean>getValue((Property<Boolean>)BedBlock.OCCUPIED) && !aix.isSleeping();
    }
    
    private boolean poiDoesntExist(final ServerLevel vk, final BlockPos ew) {
        return !vk.getPoiManager().exists(ew, this.poiPredicate);
    }
}
