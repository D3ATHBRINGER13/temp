package net.minecraft.world.item.enchantment;

import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.level.block.PumpkinBlock;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

public enum EnchantmentCategory {
    ALL {
        @Override
        public boolean canEnchant(final Item bce) {
            for (final EnchantmentCategory bft6 : EnchantmentCategory.values()) {
                if (bft6 != EnchantmentCategory.ALL) {
                    if (bft6.canEnchant(bce)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }, 
    ARMOR {
        @Override
        public boolean canEnchant(final Item bce) {
            return bce instanceof ArmorItem;
        }
    }, 
    ARMOR_FEET {
        @Override
        public boolean canEnchant(final Item bce) {
            return bce instanceof ArmorItem && ((ArmorItem)bce).getSlot() == EquipmentSlot.FEET;
        }
    }, 
    ARMOR_LEGS {
        @Override
        public boolean canEnchant(final Item bce) {
            return bce instanceof ArmorItem && ((ArmorItem)bce).getSlot() == EquipmentSlot.LEGS;
        }
    }, 
    ARMOR_CHEST {
        @Override
        public boolean canEnchant(final Item bce) {
            return bce instanceof ArmorItem && ((ArmorItem)bce).getSlot() == EquipmentSlot.CHEST;
        }
    }, 
    ARMOR_HEAD {
        @Override
        public boolean canEnchant(final Item bce) {
            return bce instanceof ArmorItem && ((ArmorItem)bce).getSlot() == EquipmentSlot.HEAD;
        }
    }, 
    WEAPON {
        @Override
        public boolean canEnchant(final Item bce) {
            return bce instanceof SwordItem;
        }
    }, 
    DIGGER {
        @Override
        public boolean canEnchant(final Item bce) {
            return bce instanceof DiggerItem;
        }
    }, 
    FISHING_ROD {
        @Override
        public boolean canEnchant(final Item bce) {
            return bce instanceof FishingRodItem;
        }
    }, 
    TRIDENT {
        @Override
        public boolean canEnchant(final Item bce) {
            return bce instanceof TridentItem;
        }
    }, 
    BREAKABLE {
        @Override
        public boolean canEnchant(final Item bce) {
            return bce.canBeDepleted();
        }
    }, 
    BOW {
        @Override
        public boolean canEnchant(final Item bce) {
            return bce instanceof BowItem;
        }
    }, 
    WEARABLE {
        @Override
        public boolean canEnchant(final Item bce) {
            final Block bmv3 = Block.byItem(bce);
            return bce instanceof ArmorItem || bce instanceof ElytraItem || bmv3 instanceof AbstractSkullBlock || bmv3 instanceof PumpkinBlock;
        }
    }, 
    CROSSBOW {
        @Override
        public boolean canEnchant(final Item bce) {
            return bce instanceof CrossbowItem;
        }
    };
    
    public abstract boolean canEnchant(final Item bce);
}
