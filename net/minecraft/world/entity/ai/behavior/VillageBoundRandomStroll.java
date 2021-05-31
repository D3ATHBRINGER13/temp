package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import java.util.Optional;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.PathfinderMob;

public class VillageBoundRandomStroll extends Behavior<PathfinderMob> {
    private final float speed;
    private final int maxXyDist;
    private final int maxYDist;
    
    public VillageBoundRandomStroll(final float float1) {
        this(float1, 10, 7);
    }
    
    public VillageBoundRandomStroll(final float float1, final int integer2, final int integer3) {
        super((Map)ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
        this.speed = float1;
        this.maxXyDist = integer2;
        this.maxYDist = integer3;
    }
    
    @Override
    protected void start(final ServerLevel vk, final PathfinderMob aje, final long long3) {
        final BlockPos ew6 = new BlockPos(aje);
        if (vk.isVillage(ew6)) {
            this.setRandomPos(aje);
        }
        else {
            final SectionPos fp7 = SectionPos.of(ew6);
            final SectionPos fp8 = BehaviorUtils.findSectionClosestToVillage(vk, fp7, 2);
            if (fp8 != fp7) {
                this.setTargetedPos(aje, fp8);
            }
            else {
                this.setRandomPos(aje);
            }
        }
    }
    
    private void setTargetedPos(final PathfinderMob aje, final SectionPos fp) {
        final BlockPos ew4 = fp.center();
        final Optional<Vec3> optional5 = (Optional<Vec3>)Optional.ofNullable(RandomPos.getPosTowards(aje, this.maxXyDist, this.maxYDist, new Vec3(ew4.getX(), ew4.getY(), ew4.getZ())));
        aje.getBrain().<WalkTarget>setMemory(MemoryModuleType.WALK_TARGET, (java.util.Optional<WalkTarget>)optional5.map(csi -> new WalkTarget(csi, this.speed, 0)));
    }
    
    private void setRandomPos(final PathfinderMob aje) {
        final Optional<Vec3> optional3 = (Optional<Vec3>)Optional.ofNullable(RandomPos.getLandPos(aje, this.maxXyDist, this.maxYDist));
        aje.getBrain().<WalkTarget>setMemory(MemoryModuleType.WALK_TARGET, (java.util.Optional<WalkTarget>)optional3.map(csi -> new WalkTarget(csi, this.speed, 0)));
    }
}
