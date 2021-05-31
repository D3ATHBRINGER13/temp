package net.minecraft.world.item.enchantment;

import net.minecraft.util.WeighedRandom;

public class EnchantmentInstance extends WeighedRandom.WeighedRandomItem {
    public final Enchantment enchantment;
    public final int level;
    
    public EnchantmentInstance(final Enchantment bfs, final int integer) {
        super(bfs.getRarity().getWeight());
        this.enchantment = bfs;
        this.level = integer;
    }
}
