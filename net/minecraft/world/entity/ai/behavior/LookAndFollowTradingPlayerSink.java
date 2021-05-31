package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;

public class LookAndFollowTradingPlayerSink extends Behavior<Villager> {
    private final float speed;
    
    public LookAndFollowTradingPlayerSink(final float float1) {
        super((Map)ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED), Integer.MAX_VALUE);
        this.speed = float1;
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final Villager avt) {
        final Player awg4 = avt.getTradingPlayer();
        return avt.isAlive() && awg4 != null && !avt.isInWater() && !avt.hurtMarked && avt.distanceToSqr(awg4) <= 16.0 && awg4.containerMenu != null;
    }
    
    @Override
    protected boolean canStillUse(final ServerLevel vk, final Villager avt, final long long3) {
        return this.checkExtraStartConditions(vk, avt);
    }
    
    @Override
    protected void start(final ServerLevel vk, final Villager avt, final long long3) {
        this.followPlayer(avt);
    }
    
    @Override
    protected void stop(final ServerLevel vk, final Villager avt, final long long3) {
        final Brain<?> ajm6 = avt.getBrain();
        ajm6.<WalkTarget>eraseMemory(MemoryModuleType.WALK_TARGET);
        ajm6.<PositionWrapper>eraseMemory(MemoryModuleType.LOOK_TARGET);
    }
    
    @Override
    protected void tick(final ServerLevel vk, final Villager avt, final long long3) {
        this.followPlayer(avt);
    }
    
    @Override
    protected boolean timedOut(final long long1) {
        return false;
    }
    
    private void followPlayer(final Villager avt) {
        final EntityPosWrapper akd3 = new EntityPosWrapper(avt.getTradingPlayer());
        final Brain<?> ajm4 = avt.getBrain();
        ajm4.<WalkTarget>setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(akd3, this.speed, 2));
        ajm4.<PositionWrapper>setMemory(MemoryModuleType.LOOK_TARGET, akd3);
    }
}
