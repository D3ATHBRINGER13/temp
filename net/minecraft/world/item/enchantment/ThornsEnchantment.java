package net.minecraft.world.item.enchantment;

import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;

public class ThornsEnchantment extends Enchantment {
    public ThornsEnchantment(final Rarity a, final EquipmentSlot... arr) {
        super(a, EnchantmentCategory.ARMOR_CHEST, arr);
    }
    
    @Override
    public int getMinCost(final int integer) {
        return 10 + 20 * (integer - 1);
    }
    
    @Override
    public int getMaxCost(final int integer) {
        return super.getMinCost(integer) + 50;
    }
    
    @Override
    public int getMaxLevel() {
        return 3;
    }
    
    @Override
    public boolean canEnchant(final ItemStack bcj) {
        return bcj.getItem() instanceof ArmorItem || super.canEnchant(bcj);
    }
    
    @Override
    public void doPostHurt(final LivingEntity aix, final Entity aio, final int integer) {
        final Random random5 = aix.getRandom();
        final Map.Entry<EquipmentSlot, ItemStack> entry6 = EnchantmentHelper.getRandomItemWith(Enchantments.THORNS, aix);
        if (shouldHit(integer, random5)) {
            if (aio != null) {
                aio.hurt(DamageSource.thorns(aix), (float)getDamage(integer, random5));
            }
            if (entry6 != null) {
                ((ItemStack)entry6.getValue()).<LivingEntity>hurtAndBreak(3, aix, (java.util.function.Consumer<LivingEntity>)(aix -> aix.broadcastBreakEvent((EquipmentSlot)entry6.getKey())));
            }
        }
        else if (entry6 != null) {
            ((ItemStack)entry6.getValue()).<LivingEntity>hurtAndBreak(1, aix, (java.util.function.Consumer<LivingEntity>)(aix -> aix.broadcastBreakEvent((EquipmentSlot)entry6.getKey())));
        }
    }
    
    public static boolean shouldHit(final int integer, final Random random) {
        return integer > 0 && random.nextFloat() < 0.15f * integer;
    }
    
    public static int getDamage(final int integer, final Random random) {
        if (integer > 10) {
            return integer - 10;
        }
        return 1 + random.nextInt(4);
    }
}
