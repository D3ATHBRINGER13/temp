package net.minecraft.world.item;

import net.minecraft.network.chat.Component;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.level.Level;
import java.util.Iterator;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.core.Registry;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

public class TippedArrowItem extends ArrowItem {
    public TippedArrowItem(final Properties a) {
        super(a);
    }
    
    @Override
    public ItemStack getDefaultInstance() {
        return PotionUtils.setPotion(super.getDefaultInstance(), Potions.POISON);
    }
    
    @Override
    public void fillItemCategory(final CreativeModeTab bba, final NonNullList<ItemStack> fk) {
        if (this.allowdedIn(bba)) {
            for (final Potion bdy5 : Registry.POTION) {
                if (!bdy5.getEffects().isEmpty()) {
                    fk.add(PotionUtils.setPotion(new ItemStack(this), bdy5));
                }
            }
        }
    }
    
    @Override
    public void appendHoverText(final ItemStack bcj, @Nullable final Level bhr, final List<Component> list, final TooltipFlag bdr) {
        PotionUtils.addPotionTooltip(bcj, list, 0.125f);
    }
    
    @Override
    public String getDescriptionId(final ItemStack bcj) {
        return PotionUtils.getPotion(bcj).getName(this.getDescriptionId() + ".effect.");
    }
}
