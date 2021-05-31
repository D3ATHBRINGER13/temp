package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import java.util.function.Predicate;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.core.BlockPos;
import java.util.Optional;
import net.minecraft.world.entity.LivingEntity;

public class LocateHidingPlace extends Behavior<LivingEntity> {
    private final float speed;
    private final int radius;
    private final int closeEnoughDist;
    private Optional<BlockPos> currentPos;
    
    public LocateHidingPlace(final int integer1, final float float2, final int integer3) {
        super((Map)ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.HOME, MemoryStatus.REGISTERED, MemoryModuleType.HIDING_PLACE, MemoryStatus.REGISTERED));
        this.currentPos = (Optional<BlockPos>)Optional.empty();
        this.radius = integer1;
        this.speed = float2;
        this.closeEnoughDist = integer3;
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final LivingEntity aix) {
        final Optional<BlockPos> optional4 = vk.getPoiManager().find((Predicate<PoiType>)(aqs -> aqs == PoiType.HOME), (Predicate<BlockPos>)(ew -> true), new BlockPos(aix), this.closeEnoughDist + 1, PoiManager.Occupancy.ANY);
        if (optional4.isPresent() && ((BlockPos)optional4.get()).closerThan(aix.position(), this.closeEnoughDist)) {
            this.currentPos = optional4;
        }
        else {
            this.currentPos = (Optional<BlockPos>)Optional.empty();
        }
        return true;
    }
    
    @Override
    protected void start(final ServerLevel vk, final LivingEntity aix, final long long3) {
        final Brain<?> ajm6 = aix.getBrain();
        Optional<BlockPos> optional7 = this.currentPos;
        if (!optional7.isPresent()) {
            optional7 = vk.getPoiManager().getRandom((Predicate<PoiType>)(aqs -> aqs == PoiType.HOME), (Predicate<BlockPos>)(ew -> true), PoiManager.Occupancy.ANY, new BlockPos(aix), this.radius, aix.getRandom());
            if (!optional7.isPresent()) {
                final Optional<GlobalPos> optional8 = ajm6.<GlobalPos>getMemory(MemoryModuleType.HOME);
                if (optional8.isPresent()) {
                    optional7 = (Optional<BlockPos>)Optional.of(((GlobalPos)optional8.get()).pos());
                }
            }
        }
        if (optional7.isPresent()) {
            ajm6.<Path>eraseMemory(MemoryModuleType.PATH);
            ajm6.<PositionWrapper>eraseMemory(MemoryModuleType.LOOK_TARGET);
            ajm6.<Villager>eraseMemory(MemoryModuleType.BREED_TARGET);
            ajm6.<LivingEntity>eraseMemory(MemoryModuleType.INTERACTION_TARGET);
            ajm6.<GlobalPos>setMemory(MemoryModuleType.HIDING_PLACE, GlobalPos.of(vk.getDimension().getType(), (BlockPos)optional7.get()));
            if (!((BlockPos)optional7.get()).closerThan(aix.position(), this.closeEnoughDist)) {
                ajm6.<WalkTarget>setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget((BlockPos)optional7.get(), this.speed, this.closeEnoughDist));
            }
        }
    }
}
