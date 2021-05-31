package net.minecraft.world.item.enchantment;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.EquipmentSlot;

public class DamageEnchantment extends Enchantment {
    private static final String[] NAMES;
    private static final int[] MIN_COST;
    private static final int[] LEVEL_COST;
    private static final int[] LEVEL_COST_SPAN;
    public final int type;
    
    public DamageEnchantment(final Rarity a, final int integer, final EquipmentSlot... arr) {
        super(a, EnchantmentCategory.WEAPON, arr);
        this.type = integer;
    }
    
    @Override
    public int getMinCost(final int integer) {
        return DamageEnchantment.MIN_COST[this.type] + (integer - 1) * DamageEnchantment.LEVEL_COST[this.type];
    }
    
    @Override
    public int getMaxCost(final int integer) {
        return this.getMinCost(integer) + DamageEnchantment.LEVEL_COST_SPAN[this.type];
    }
    
    @Override
    public int getMaxLevel() {
        return 5;
    }
    
    @Override
    public float getDamageBonus(final int integer, final MobType ajb) {
        if (this.type == 0) {
            return 1.0f + Math.max(0, integer - 1) * 0.5f;
        }
        if (this.type == 1 && ajb == MobType.UNDEAD) {
            return integer * 2.5f;
        }
        if (this.type == 2 && ajb == MobType.ARTHROPOD) {
            return integer * 2.5f;
        }
        return 0.0f;
    }
    
    public boolean checkCompatibility(final Enchantment bfs) {
        return !(bfs instanceof DamageEnchantment);
    }
    
    @Override
    public boolean canEnchant(final ItemStack bcj) {
        return bcj.getItem() instanceof AxeItem || super.canEnchant(bcj);
    }
    
    @Override
    public void doPostAttack(final LivingEntity aix, final Entity aio, final int integer) {
        if (aio instanceof LivingEntity) {
            final LivingEntity aix2 = (LivingEntity)aio;
            if (this.type == 2 && aix2.getMobType() == MobType.ARTHROPOD) {
                final int integer2 = 20 + aix.getRandom().nextInt(10 * integer);
                aix2.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, integer2, 3));
            }
        }
    }
    
    static {
        NAMES = new String[] { "all", "undead", "arthropods" };
        MIN_COST = new int[] { 1, 5, 5 };
        LEVEL_COST = new int[] { 11, 8, 8 };
        LEVEL_COST_SPAN = new int[] { 20, 20, 20 };
    }
}
