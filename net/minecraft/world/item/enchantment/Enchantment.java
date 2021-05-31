package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.Entity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.Util;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.damagesource.DamageSource;
import com.google.common.collect.Maps;
import net.minecraft.world.item.ItemStack;
import java.util.Map;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.Registry;
import javax.annotation.Nullable;
import net.minecraft.world.entity.EquipmentSlot;

public abstract class Enchantment {
    private final EquipmentSlot[] slots;
    private final Rarity rarity;
    @Nullable
    public EnchantmentCategory category;
    @Nullable
    protected String descriptionId;
    
    @Nullable
    public static Enchantment byId(final int integer) {
        return Registry.ENCHANTMENT.byId(integer);
    }
    
    protected Enchantment(final Rarity a, final EnchantmentCategory bft, final EquipmentSlot[] arr) {
        this.rarity = a;
        this.category = bft;
        this.slots = arr;
    }
    
    public Map<EquipmentSlot, ItemStack> getSlotItems(final LivingEntity aix) {
        final Map<EquipmentSlot, ItemStack> map3 = (Map<EquipmentSlot, ItemStack>)Maps.newEnumMap((Class)EquipmentSlot.class);
        for (final EquipmentSlot ait7 : this.slots) {
            final ItemStack bcj8 = aix.getItemBySlot(ait7);
            if (!bcj8.isEmpty()) {
                map3.put(ait7, bcj8);
            }
        }
        return map3;
    }
    
    public Rarity getRarity() {
        return this.rarity;
    }
    
    public int getMinLevel() {
        return 1;
    }
    
    public int getMaxLevel() {
        return 1;
    }
    
    public int getMinCost(final int integer) {
        return 1 + integer * 10;
    }
    
    public int getMaxCost(final int integer) {
        return this.getMinCost(integer) + 5;
    }
    
    public int getDamageProtection(final int integer, final DamageSource ahx) {
        return 0;
    }
    
    public float getDamageBonus(final int integer, final MobType ajb) {
        return 0.0f;
    }
    
    public final boolean isCompatibleWith(final Enchantment bfs) {
        return this.checkCompatibility(bfs) && bfs.checkCompatibility(this);
    }
    
    protected boolean checkCompatibility(final Enchantment bfs) {
        return this != bfs;
    }
    
    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("enchantment", Registry.ENCHANTMENT.getKey(this));
        }
        return this.descriptionId;
    }
    
    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }
    
    public Component getFullname(final int integer) {
        final Component jo3 = new TranslatableComponent(this.getDescriptionId(), new Object[0]);
        if (this.isCurse()) {
            jo3.withStyle(ChatFormatting.RED);
        }
        else {
            jo3.withStyle(ChatFormatting.GRAY);
        }
        if (integer != 1 || this.getMaxLevel() != 1) {
            jo3.append(" ").append(new TranslatableComponent(new StringBuilder().append("enchantment.level.").append(integer).toString(), new Object[0]));
        }
        return jo3;
    }
    
    public boolean canEnchant(final ItemStack bcj) {
        return this.category.canEnchant(bcj.getItem());
    }
    
    public void doPostAttack(final LivingEntity aix, final Entity aio, final int integer) {
    }
    
    public void doPostHurt(final LivingEntity aix, final Entity aio, final int integer) {
    }
    
    public boolean isTreasureOnly() {
        return false;
    }
    
    public boolean isCurse() {
        return false;
    }
    
    public enum Rarity {
        COMMON(10), 
        UNCOMMON(5), 
        RARE(2), 
        VERY_RARE(1);
        
        private final int weight;
        
        private Rarity(final int integer3) {
            this.weight = integer3;
        }
        
        public int getWeight() {
            return this.weight;
        }
    }
}
