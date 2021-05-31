package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.LivingEntity;
import java.util.Optional;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.PathfinderMob;

public class InsideBrownianWalk extends Behavior<PathfinderMob> {
    private final float speed;
    
    public InsideBrownianWalk(final float float1) {
        super((Map)ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
        this.speed = float1;
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final PathfinderMob aje) {
        return !vk.canSeeSky(new BlockPos(aje));
    }
    
    @Override
    protected void start(final ServerLevel vk, final PathfinderMob aje, final long long3) {
        final BlockPos ew6 = new BlockPos(aje);
        final List<BlockPos> list7 = (List<BlockPos>)BlockPos.betweenClosedStream(ew6.offset(-1, -1, -1), ew6.offset(1, 1, 1)).map(BlockPos::immutable).collect(Collectors.toList());
        Collections.shuffle((List)list7);
        final Optional<BlockPos> optional8 = (Optional<BlockPos>)list7.stream().filter(ew -> !vk.canSeeSky(ew)).filter(ew -> vk.loadedAndEntityCanStandOn(ew, aje)).filter(ew -> vk.noCollision(aje)).findFirst();
        optional8.ifPresent(ew -> aje.getBrain().<WalkTarget>setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(ew, this.speed, 0)));
    }
}
