package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

public class LootBonusEnchantment extends Enchantment {
    protected LootBonusEnchantment(final Rarity a, final EnchantmentCategory bft, final EquipmentSlot... arr) {
        super(a, bft, arr);
    }
    
    @Override
    public int getMinCost(final int integer) {
        return 15 + (integer - 1) * 9;
    }
    
    @Override
    public int getMaxCost(final int integer) {
        return super.getMinCost(integer) + 50;
    }
    
    @Override
    public int getMaxLevel() {
        return 3;
    }
    
    public boolean checkCompatibility(final Enchantment bfs) {
        return super.checkCompatibility(bfs) && bfs != Enchantments.SILK_TOUCH;
    }
}
