package net.minecraft.world.entity.ai.sensing;

import net.minecraft.world.entity.LivingEntity;
import java.util.Optional;
import net.minecraft.world.level.pathfinder.Path;
import java.util.stream.Stream;
import java.util.function.Predicate;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.server.level.ServerLevel;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import java.util.Set;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import net.minecraft.world.entity.Mob;

public class NearestBedSensor extends Sensor<Mob> {
    private final Long2LongMap batchCache;
    private int triedCount;
    private long lastUpdate;
    
    public NearestBedSensor() {
        super(20);
        this.batchCache = (Long2LongMap)new Long2LongOpenHashMap();
    }
    
    @Override
    public Set<MemoryModuleType<?>> requires() {
        return (Set<MemoryModuleType<?>>)ImmutableSet.of(MemoryModuleType.NEAREST_BED);
    }
    
    @Override
    protected void doTick(final ServerLevel vk, final Mob aiy) {
        if (!aiy.isBaby()) {
            return;
        }
        this.triedCount = 0;
        this.lastUpdate = vk.getGameTime() + vk.getRandom().nextInt(20);
        final PoiManager aqp4 = vk.getPoiManager();
        final Predicate<BlockPos> predicate5 = (Predicate<BlockPos>)(ew -> {
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
        final Stream<BlockPos> stream6 = aqp4.findAll(PoiType.HOME.getPredicate(), predicate5, new BlockPos(aiy), 48, PoiManager.Occupancy.ANY);
        final Path cnr7 = aiy.getNavigation().createPath(stream6, PoiType.HOME.getValidRange());
        if (cnr7 != null && cnr7.canReach()) {
            final BlockPos ew8 = cnr7.getTarget();
            final Optional<PoiType> optional9 = aqp4.getType(ew8);
            if (optional9.isPresent()) {
                aiy.getBrain().<BlockPos>setMemory(MemoryModuleType.NEAREST_BED, ew8);
            }
        }
        else if (this.triedCount < 5) {
            this.batchCache.long2LongEntrySet().removeIf(entry -> entry.getLongValue() < this.lastUpdate);
        }
    }
}
