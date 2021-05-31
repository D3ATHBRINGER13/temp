package net.minecraft.world.item;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class NameTagItem extends Item {
    public NameTagItem(final Properties a) {
        super(a);
    }
    
    @Override
    public boolean interactEnemy(final ItemStack bcj, final Player awg, final LivingEntity aix, final InteractionHand ahi) {
        if (bcj.hasCustomHoverName() && !(aix instanceof Player)) {
            if (aix.isAlive()) {
                aix.setCustomName(bcj.getHoverName());
                if (aix instanceof Mob) {
                    ((Mob)aix).setPersistenceRequired();
                }
                bcj.shrink(1);
            }
            return true;
        }
        return false;
    }
}
