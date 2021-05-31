package net.minecraft.world.entity.ai.behavior;

import java.util.Iterator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.LivingEntity;

public class RingBell extends Behavior<LivingEntity> {
    public RingBell() {
        super((Map)ImmutableMap.of(MemoryModuleType.MEETING_POINT, MemoryStatus.VALUE_PRESENT));
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final LivingEntity aix) {
        return vk.random.nextFloat() > 0.95f;
    }
    
    @Override
    protected void start(final ServerLevel vk, final LivingEntity aix, final long long3) {
        final Brain<?> ajm6 = aix.getBrain();
        final BlockPos ew7 = ((GlobalPos)ajm6.<GlobalPos>getMemory(MemoryModuleType.MEETING_POINT).get()).pos();
        if (ew7.closerThan(new BlockPos(aix), 3.0)) {
            final BlockState bvt8 = vk.getBlockState(ew7);
            if (bvt8.getBlock() == Blocks.BELL) {
                final BellBlock bmt9 = (BellBlock)bvt8.getBlock();
                for (final Direction fb11 : Direction.Plane.HORIZONTAL) {
                    if (bmt9.onHit(vk, bvt8, vk.getBlockEntity(ew7), new BlockHitResult(new Vec3(0.5, 0.5, 0.5), fb11, ew7, false), null, false)) {
                        break;
                    }
                }
            }
        }
    }
}
