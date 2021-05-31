package net.minecraft.world.entity.ambient;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

public abstract class AmbientCreature extends Mob {
    protected AmbientCreature(final EntityType<? extends AmbientCreature> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    @Override
    public boolean canBeLeashed(final Player awg) {
        return false;
    }
}
