package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.Mob;

public class Swim extends Behavior<Mob> {
    private final float height;
    private final float chance;
    
    public Swim(final float float1, final float float2) {
        super((Map)ImmutableMap.of());
        this.height = float1;
        this.chance = float2;
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final Mob aiy) {
        return (aiy.isInWater() && aiy.getWaterHeight() > this.height) || aiy.isInLava();
    }
    
    @Override
    protected boolean canStillUse(final ServerLevel vk, final Mob aiy, final long long3) {
        return this.checkExtraStartConditions(vk, aiy);
    }
    
    @Override
    protected void tick(final ServerLevel vk, final Mob aiy, final long long3) {
        if (aiy.getRandom().nextFloat() < this.chance) {
            aiy.getJumpControl().jump();
        }
    }
}
