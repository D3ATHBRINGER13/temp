package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.npc.AbstractVillager;

public class LookAtTradingPlayerGoal extends LookAtPlayerGoal {
    private final AbstractVillager villager;
    
    public LookAtTradingPlayerGoal(final AbstractVillager avp) {
        super(avp, Player.class, 8.0f);
        this.villager = avp;
    }
    
    @Override
    public boolean canUse() {
        if (this.villager.isTrading()) {
            this.lookAt = this.villager.getTradingPlayer();
            return true;
        }
        return false;
    }
}
