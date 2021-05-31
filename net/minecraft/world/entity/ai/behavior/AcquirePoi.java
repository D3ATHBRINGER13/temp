package net.minecraft.world.entity.ai.behavior;

import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.pathfinder.Path;
import java.util.stream.Stream;
import java.util.function.Predicate;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.PathfinderMob;

public class AcquirePoi extends Behavior<PathfinderMob> {
    private final PoiType poiType;
    private final MemoryModuleType<GlobalPos> memoryType;
    private final boolean onlyIfAdult;
    private long lastUpdate;
    private final Long2LongMap batchCache;
    private int triedCount;
    
    public AcquirePoi(final PoiType aqs, final MemoryModuleType<GlobalPos> apj, final boolean boolean3) {
        super((Map)ImmutableMap.of(apj, MemoryStatus.VALUE_ABSENT));
        this.batchCache = (Long2LongMap)new Long2LongOpenHashMap();
        this.poiType = aqs;
        this.memoryType = apj;
        this.onlyIfAdult = boolean3;
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final PathfinderMob aje) {
        return (!this.onlyIfAdult || !aje.isBaby()) && vk.getGameTime() - this.lastUpdate >= 20L;
    }
    
    @Override
    protected void start(final ServerLevel vk, final PathfinderMob aje, final long long3) {
        this.triedCount = 0;
        this.lastUpdate = vk.getGameTime() + vk.getRandom().nextInt(20);
        final PoiManager aqp6 = vk.getPoiManager();
        final Predicate<BlockPos> predicate7 = (Predicate<BlockPos>)(ew -> {
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
        final Stream<BlockPos> stream8 = aqp6.findAll(this.poiType.getPredicate(), predicate7, new BlockPos(aje), 48, PoiManager.Occupancy.HAS_SPACE);
        final Path cnr9 = aje.getNavigation().createPath(stream8, this.poiType.getValidRange());
        if (cnr9 != null && cnr9.canReach()) {
            final BlockPos ew10 = cnr9.getTarget();
            aqp6.getType(ew10).ifPresent(aqs -> {
                aqp6.take(this.poiType.getPredicate(), (Predicate<BlockPos>)(ew2 -> ew2.equals(ew10)), ew10, 1);
                aje.getBrain().<GlobalPos>setMemory(this.memoryType, GlobalPos.of(vk.getDimension().getType(), ew10));
                DebugPackets.sendPoiTicketCountPacket(vk, ew10);
            });
        }
        else if (this.triedCount < 5) {
            this.batchCache.long2LongEntrySet().removeIf(entry -> entry.getLongValue() < this.lastUpdate);
        }
    }
}
