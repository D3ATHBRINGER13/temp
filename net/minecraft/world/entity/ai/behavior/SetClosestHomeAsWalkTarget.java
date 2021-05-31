package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.level.pathfinder.Path;
import java.util.stream.Stream;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import java.util.Optional;
import net.minecraft.core.Vec3i;
import java.util.function.Predicate;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.server.level.ServerLevel;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import net.minecraft.world.entity.LivingEntity;

public class SetClosestHomeAsWalkTarget extends Behavior<LivingEntity> {
    private final float speed;
    private final Long2LongMap batchCache;
    private int triedCount;
    private long lastUpdate;
    
    public SetClosestHomeAsWalkTarget(final float float1) {
        super((Map)ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.HOME, MemoryStatus.VALUE_ABSENT));
        this.batchCache = (Long2LongMap)new Long2LongOpenHashMap();
        this.speed = float1;
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final LivingEntity aix) {
        if (vk.getGameTime() - this.lastUpdate < 20L) {
            return false;
        }
        final PathfinderMob aje4 = (PathfinderMob)aix;
        final PoiManager aqp5 = vk.getPoiManager();
        final Optional<BlockPos> optional6 = aqp5.findClosest(PoiType.HOME.getPredicate(), (Predicate<BlockPos>)(ew -> true), new BlockPos(aix), 48, PoiManager.Occupancy.ANY);
        return optional6.isPresent() && ((BlockPos)optional6.get()).distSqr(new Vec3i(aje4.x, aje4.y, aje4.z)) > 4.0;
    }
    
    @Override
    protected void start(final ServerLevel vk, final LivingEntity aix, final long long3) {
        this.triedCount = 0;
        this.lastUpdate = vk.getGameTime() + vk.getRandom().nextInt(20);
        final PathfinderMob aje6 = (PathfinderMob)aix;
        final PoiManager aqp7 = vk.getPoiManager();
        final Predicate<BlockPos> predicate8 = (Predicate<BlockPos>)(ew -> {
            final long long3 = ew.asLong();
            if (this.batchCache.containsKey(long3)) {
                return false;
            }
            if (++this.triedCount >= 5) {
                return false;
            }
            this.batchCache.put(long3, this.lastUpdate + 40L);
            return true;
        });
        final Stream<BlockPos> stream9 = aqp7.findAll(PoiType.HOME.getPredicate(), predicate8, new BlockPos(aix), 48, PoiManager.Occupancy.ANY);
        final Path cnr10 = aje6.getNavigation().createPath(stream9, PoiType.HOME.getValidRange());
        if (cnr10 != null && cnr10.canReach()) {
            final BlockPos ew11 = cnr10.getTarget();
            final Optional<PoiType> optional12 = aqp7.getType(ew11);
            if (optional12.isPresent()) {
                aix.getBrain().<WalkTarget>setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(ew11, this.speed, 1));
                DebugPackets.sendPoiTicketCountPacket(vk, ew11);
            }
        }
        else if (this.triedCount < 5) {
            this.batchCache.long2LongEntrySet().removeIf(entry -> entry.getLongValue() < this.lastUpdate);
        }
    }
}
