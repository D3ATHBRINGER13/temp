package net.minecraft.world.entity.vehicle;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

public class Minecart extends AbstractMinecart {
    public Minecart(final EntityType<?> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    public Minecart(final Level bhr, final double double2, final double double3, final double double4) {
        super(EntityType.MINECART, bhr, double2, double3, double4);
    }
    
    @Override
    public boolean interact(final Player awg, final InteractionHand ahi) {
        if (awg.isSneaking()) {
            return false;
        }
        if (this.isVehicle()) {
            return true;
        }
        if (!this.level.isClientSide) {
            awg.startRiding(this);
        }
        return true;
    }
    
    @Override
    public void activateMinecart(final int integer1, final int integer2, final int integer3, final boolean boolean4) {
        if (boolean4) {
            if (this.isVehicle()) {
                this.ejectPassengers();
            }
            if (this.getHurtTime() == 0) {
                this.setHurtDir(-this.getHurtDir());
                this.setHurtTime(10);
                this.setDamage(50.0f);
                this.markHurt();
            }
        }
    }
    
    @Override
    public Type getMinecartType() {
        return Type.RIDEABLE;
    }
}
