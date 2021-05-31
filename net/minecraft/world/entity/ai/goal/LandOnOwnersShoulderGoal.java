package net.minecraft.world.entity.ai.goal;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;

public class LandOnOwnersShoulderGoal extends Goal {
    private final ShoulderRidingEntity entity;
    private ServerPlayer owner;
    private boolean isSittingOnShoulder;
    
    public LandOnOwnersShoulderGoal(final ShoulderRidingEntity art) {
        this.entity = art;
    }
    
    @Override
    public boolean canUse() {
        final ServerPlayer vl2 = (ServerPlayer)this.entity.getOwner();
        final boolean boolean3 = vl2 != null && !vl2.isSpectator() && !vl2.abilities.flying && !vl2.isInWater();
        return !this.entity.isSitting() && boolean3 && this.entity.canSitOnShoulder();
    }
    
    @Override
    public boolean isInterruptable() {
        return !this.isSittingOnShoulder;
    }
    
    @Override
    public void start() {
        this.owner = (ServerPlayer)this.entity.getOwner();
        this.isSittingOnShoulder = false;
    }
    
    @Override
    public void tick() {
        if (this.isSittingOnShoulder || this.entity.isSitting() || this.entity.isLeashed()) {
            return;
        }
        if (this.entity.getBoundingBox().intersects(this.owner.getBoundingBox())) {
            this.isSittingOnShoulder = this.entity.setEntityOnShoulder(this.owner);
        }
    }
}
