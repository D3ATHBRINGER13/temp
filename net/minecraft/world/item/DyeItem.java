package net.minecraft.world.item;

import com.google.common.collect.Maps;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import java.util.Map;

public class DyeItem extends Item {
    private static final Map<DyeColor, DyeItem> ITEM_BY_COLOR;
    private final DyeColor dyeColor;
    
    public DyeItem(final DyeColor bbg, final Properties a) {
        super(a);
        this.dyeColor = bbg;
        DyeItem.ITEM_BY_COLOR.put(bbg, this);
    }
    
    @Override
    public boolean interactEnemy(final ItemStack bcj, final Player awg, final LivingEntity aix, final InteractionHand ahi) {
        if (aix instanceof Sheep) {
            final Sheep ars6 = (Sheep)aix;
            if (ars6.isAlive() && !ars6.isSheared() && ars6.getColor() != this.dyeColor) {
                ars6.setColor(this.dyeColor);
                bcj.shrink(1);
            }
            return true;
        }
        return false;
    }
    
    public DyeColor getDyeColor() {
        return this.dyeColor;
    }
    
    public static DyeItem byColor(final DyeColor bbg) {
        return (DyeItem)DyeItem.ITEM_BY_COLOR.get(bbg);
    }
    
    static {
        ITEM_BY_COLOR = (Map)Maps.newEnumMap((Class)DyeColor.class);
    }
}
