package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import java.util.function.ToDoubleFunction;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;

public class GoToClosestVillage extends Behavior<Villager> {
    private final float speed;
    private final int closeEnoughDistance;
    
    public GoToClosestVillage(final float float1, final int integer) {
        super((Map)ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
        this.speed = float1;
        this.closeEnoughDistance = integer;
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final Villager avt) {
        return !vk.isVillage(new BlockPos(avt));
    }
    
    @Override
    protected void start(final ServerLevel vk, final Villager avt, final long long3) {
        final PoiManager aqp6 = vk.getPoiManager();
        final int integer7 = aqp6.sectionsToVillage(SectionPos.of(new BlockPos(avt)));
        Vec3 csi8 = null;
        for (int integer8 = 0; integer8 < 5; ++integer8) {
            final Vec3 csi9 = RandomPos.getLandPos(avt, 15, 7, (ToDoubleFunction<BlockPos>)(ew -> -vk.sectionsToVillage(SectionPos.of(ew))));
            if (csi9 != null) {
                final int integer9 = aqp6.sectionsToVillage(SectionPos.of(new BlockPos(csi9)));
                if (integer9 < integer7) {
                    csi8 = csi9;
                    break;
                }
                if (integer9 == integer7) {
                    csi8 = csi9;
                }
            }
        }
        if (csi8 != null) {
            avt.getBrain().<WalkTarget>setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(csi8, this.speed, this.closeEnoughDistance));
        }
    }
}
