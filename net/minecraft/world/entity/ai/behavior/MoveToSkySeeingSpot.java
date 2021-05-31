package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.entity.Entity;
import javax.annotation.Nullable;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.LivingEntity;

public class MoveToSkySeeingSpot extends Behavior<LivingEntity> {
    private final float speed;
    
    public MoveToSkySeeingSpot(final float float1) {
        super((Map)ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
        this.speed = float1;
    }
    
    @Override
    protected void start(final ServerLevel vk, final LivingEntity aix, final long long3) {
        final Optional<Vec3> optional6 = (Optional<Vec3>)Optional.ofNullable(this.getOutdoorPosition(vk, aix));
        if (optional6.isPresent()) {
            aix.getBrain().<WalkTarget>setMemory(MemoryModuleType.WALK_TARGET, (java.util.Optional<WalkTarget>)optional6.map(csi -> new WalkTarget(csi, this.speed, 0)));
        }
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final LivingEntity aix) {
        return !vk.canSeeSky(new BlockPos(aix.x, aix.getBoundingBox().minY, aix.z));
    }
    
    @Nullable
    private Vec3 getOutdoorPosition(final ServerLevel vk, final LivingEntity aix) {
        final Random random4 = aix.getRandom();
        final BlockPos ew5 = new BlockPos(aix.x, aix.getBoundingBox().minY, aix.z);
        for (int integer6 = 0; integer6 < 10; ++integer6) {
            final BlockPos ew6 = ew5.offset(random4.nextInt(20) - 10, random4.nextInt(6) - 3, random4.nextInt(20) - 10);
            if (hasNoBlocksAbove(vk, aix)) {
                return new Vec3(ew6.getX(), ew6.getY(), ew6.getZ());
            }
        }
        return null;
    }
    
    public static boolean hasNoBlocksAbove(final ServerLevel vk, final LivingEntity aix) {
        return vk.canSeeSky(new BlockPos(aix)) && vk.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(aix)).getY() <= aix.y;
    }
}
