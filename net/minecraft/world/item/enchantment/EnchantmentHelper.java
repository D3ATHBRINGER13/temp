package net.minecraft.world.item.enchantment;

import java.util.Collection;
import net.minecraft.Util;
import net.minecraft.util.WeighedRandom;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Item;
import java.util.Random;
import javax.annotation.Nullable;
import java.util.List;
import com.google.common.collect.Lists;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.apache.commons.lang3.mutable.MutableFloat;
import net.minecraft.world.entity.MobType;
import org.apache.commons.lang3.mutable.MutableInt;
import net.minecraft.world.damagesource.DamageSource;
import java.util.Iterator;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Items;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;

public class EnchantmentHelper {
    public static int getItemEnchantmentLevel(final Enchantment bfs, final ItemStack bcj) {
        if (bcj.isEmpty()) {
            return 0;
        }
        final ResourceLocation qv3 = Registry.ENCHANTMENT.getKey(bfs);
        final ListTag ik4 = bcj.getEnchantmentTags();
        for (int integer5 = 0; integer5 < ik4.size(); ++integer5) {
            final CompoundTag id6 = ik4.getCompound(integer5);
            final ResourceLocation qv4 = ResourceLocation.tryParse(id6.getString("id"));
            if (qv4 != null && qv4.equals(qv3)) {
                return id6.getInt("lvl");
            }
        }
        return 0;
    }
    
    public static Map<Enchantment, Integer> getEnchantments(final ItemStack bcj) {
        final Map<Enchantment, Integer> map2 = (Map<Enchantment, Integer>)Maps.newLinkedHashMap();
        final ListTag ik3 = (bcj.getItem() == Items.ENCHANTED_BOOK) ? EnchantedBookItem.getEnchantments(bcj) : bcj.getEnchantmentTags();
        for (int integer4 = 0; integer4 < ik3.size(); ++integer4) {
            final CompoundTag id5 = ik3.getCompound(integer4);
            Registry.ENCHANTMENT.getOptional(ResourceLocation.tryParse(id5.getString("id"))).ifPresent(bfs -> {
                final Integer n = (Integer)map2.put(bfs, id5.getInt("lvl"));
            });
        }
        return map2;
    }
    
    public static void setEnchantments(final Map<Enchantment, Integer> map, final ItemStack bcj) {
        final ListTag ik3 = new ListTag();
        for (final Map.Entry<Enchantment, Integer> entry5 : map.entrySet()) {
            final Enchantment bfs6 = (Enchantment)entry5.getKey();
            if (bfs6 == null) {
                continue;
            }
            final int integer7 = (int)entry5.getValue();
            final CompoundTag id8 = new CompoundTag();
            id8.putString("id", String.valueOf(Registry.ENCHANTMENT.getKey(bfs6)));
            id8.putShort("lvl", (short)integer7);
            ik3.add(id8);
            if (bcj.getItem() != Items.ENCHANTED_BOOK) {
                continue;
            }
            EnchantedBookItem.addEnchantment(bcj, new EnchantmentInstance(bfs6, integer7));
        }
        if (ik3.isEmpty()) {
            bcj.removeTagKey("Enchantments");
        }
        else if (bcj.getItem() != Items.ENCHANTED_BOOK) {
            bcj.addTagElement("Enchantments", (Tag)ik3);
        }
    }
    
    private static void runIterationOnItem(final EnchantmentVisitor a, final ItemStack bcj) {
        if (bcj.isEmpty()) {
            return;
        }
        final ListTag ik3 = bcj.getEnchantmentTags();
        for (int integer4 = 0; integer4 < ik3.size(); ++integer4) {
            final String string5 = ik3.getCompound(integer4).getString("id");
            final int integer5 = ik3.getCompound(integer4).getInt("lvl");
            Registry.ENCHANTMENT.getOptional(ResourceLocation.tryParse(string5)).ifPresent(bfs -> a.accept(bfs, integer5));
        }
    }
    
    private static void runIterationOnInventory(final EnchantmentVisitor a, final Iterable<ItemStack> iterable) {
        for (final ItemStack bcj4 : iterable) {
            runIterationOnItem(a, bcj4);
        }
    }
    
    public static int getDamageProtection(final Iterable<ItemStack> iterable, final DamageSource ahx) {
        final MutableInt mutableInt3 = new MutableInt();
        runIterationOnInventory((bfs, integer) -> mutableInt3.add(bfs.getDamageProtection(integer, ahx)), iterable);
        return mutableInt3.intValue();
    }
    
    public static float getDamageBonus(final ItemStack bcj, final MobType ajb) {
        final MutableFloat mutableFloat3 = new MutableFloat();
        runIterationOnItem((bfs, integer) -> mutableFloat3.add(bfs.getDamageBonus(integer, ajb)), bcj);
        return mutableFloat3.floatValue();
    }
    
    public static float getSweepingDamageRatio(final LivingEntity aix) {
        final int integer2 = getEnchantmentLevel(Enchantments.SWEEPING_EDGE, aix);
        if (integer2 > 0) {
            return SweepingEdgeEnchantment.getSweepingDamageRatio(integer2);
        }
        return 0.0f;
    }
    
    public static void doPostHurtEffects(final LivingEntity aix, final Entity aio) {
        final EnchantmentVisitor a3 = (bfs, integer) -> bfs.doPostHurt(aix, aio, integer);
        if (aix != null) {
            runIterationOnInventory(a3, aix.getAllSlots());
        }
        if (aio instanceof Player) {
            runIterationOnItem(a3, aix.getMainHandItem());
        }
    }
    
    public static void doPostDamageEffects(final LivingEntity aix, final Entity aio) {
        final EnchantmentVisitor a3 = (bfs, integer) -> bfs.doPostAttack(aix, aio, integer);
        if (aix != null) {
            runIterationOnInventory(a3, aix.getAllSlots());
        }
        if (aix instanceof Player) {
            runIterationOnItem(a3, aix.getMainHandItem());
        }
    }
    
    public static int getEnchantmentLevel(final Enchantment bfs, final LivingEntity aix) {
        final Iterable<ItemStack> iterable3 = (Iterable<ItemStack>)bfs.getSlotItems(aix).values();
        if (iterable3 == null) {
            return 0;
        }
        int integer4 = 0;
        for (final ItemStack bcj6 : iterable3) {
            final int integer5 = getItemEnchantmentLevel(bfs, bcj6);
            if (integer5 > integer4) {
                integer4 = integer5;
            }
        }
        return integer4;
    }
    
    public static int getKnockbackBonus(final LivingEntity aix) {
        return getEnchantmentLevel(Enchantments.KNOCKBACK, aix);
    }
    
    public static int getFireAspect(final LivingEntity aix) {
        return getEnchantmentLevel(Enchantments.FIRE_ASPECT, aix);
    }
    
    public static int getRespiration(final LivingEntity aix) {
        return getEnchantmentLevel(Enchantments.RESPIRATION, aix);
    }
    
    public static int getDepthStrider(final LivingEntity aix) {
        return getEnchantmentLevel(Enchantments.DEPTH_STRIDER, aix);
    }
    
    public static int getBlockEfficiency(final LivingEntity aix) {
        return getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, aix);
    }
    
    public static int getFishingLuckBonus(final ItemStack bcj) {
        return getItemEnchantmentLevel(Enchantments.FISHING_LUCK, bcj);
    }
    
    public static int getFishingSpeedBonus(final ItemStack bcj) {
        return getItemEnchantmentLevel(Enchantments.FISHING_SPEED, bcj);
    }
    
    public static int getMobLooting(final LivingEntity aix) {
        return getEnchantmentLevel(Enchantments.MOB_LOOTING, aix);
    }
    
    public static boolean hasAquaAffinity(final LivingEntity aix) {
        return getEnchantmentLevel(Enchantments.AQUA_AFFINITY, aix) > 0;
    }
    
    public static boolean hasFrostWalker(final LivingEntity aix) {
        return getEnchantmentLevel(Enchantments.FROST_WALKER, aix) > 0;
    }
    
    public static boolean hasBindingCurse(final ItemStack bcj) {
        return getItemEnchantmentLevel(Enchantments.BINDING_CURSE, bcj) > 0;
    }
    
    public static boolean hasVanishingCurse(final ItemStack bcj) {
        return getItemEnchantmentLevel(Enchantments.VANISHING_CURSE, bcj) > 0;
    }
    
    public static int getLoyalty(final ItemStack bcj) {
        return getItemEnchantmentLevel(Enchantments.LOYALTY, bcj);
    }
    
    public static int getRiptide(final ItemStack bcj) {
        return getItemEnchantmentLevel(Enchantments.RIPTIDE, bcj);
    }
    
    public static boolean hasChanneling(final ItemStack bcj) {
        return getItemEnchantmentLevel(Enchantments.CHANNELING, bcj) > 0;
    }
    
    @Nullable
    public static Map.Entry<EquipmentSlot, ItemStack> getRandomItemWith(final Enchantment bfs, final LivingEntity aix) {
        final Map<EquipmentSlot, ItemStack> map3 = bfs.getSlotItems(aix);
        if (map3.isEmpty()) {
            return null;
        }
        final List<Map.Entry<EquipmentSlot, ItemStack>> list4 = (List<Map.Entry<EquipmentSlot, ItemStack>>)Lists.newArrayList();
        for (final Map.Entry<EquipmentSlot, ItemStack> entry6 : map3.entrySet()) {
            final ItemStack bcj7 = (ItemStack)entry6.getValue();
            if (!bcj7.isEmpty() && getItemEnchantmentLevel(bfs, bcj7) > 0) {
                list4.add(entry6);
            }
        }
        return (Map.Entry<EquipmentSlot, ItemStack>)(list4.isEmpty() ? null : ((Map.Entry)list4.get(aix.getRandom().nextInt(list4.size()))));
    }
    
    public static int getEnchantmentCost(final Random random, final int integer2, int integer3, final ItemStack bcj) {
        final Item bce5 = bcj.getItem();
        final int integer4 = bce5.getEnchantmentValue();
        if (integer4 <= 0) {
            return 0;
        }
        if (integer3 > 15) {
            integer3 = 15;
        }
        final int integer5 = random.nextInt(8) + 1 + (integer3 >> 1) + random.nextInt(integer3 + 1);
        if (integer2 == 0) {
            return Math.max(integer5 / 3, 1);
        }
        if (integer2 == 1) {
            return integer5 * 2 / 3 + 1;
        }
        return Math.max(integer5, integer3 * 2);
    }
    
    public static ItemStack enchantItem(final Random random, ItemStack bcj, final int integer, final boolean boolean4) {
        final List<EnchantmentInstance> list5 = selectEnchantment(random, bcj, integer, boolean4);
        final boolean boolean5 = bcj.getItem() == Items.BOOK;
        if (boolean5) {
            bcj = new ItemStack(Items.ENCHANTED_BOOK);
        }
        for (final EnchantmentInstance bfv8 : list5) {
            if (boolean5) {
                EnchantedBookItem.addEnchantment(bcj, bfv8);
            }
            else {
                bcj.enchant(bfv8.enchantment, bfv8.level);
            }
        }
        return bcj;
    }
    
    public static List<EnchantmentInstance> selectEnchantment(final Random random, final ItemStack bcj, int integer, final boolean boolean4) {
        final List<EnchantmentInstance> list5 = (List<EnchantmentInstance>)Lists.newArrayList();
        final Item bce6 = bcj.getItem();
        final int integer2 = bce6.getEnchantmentValue();
        if (integer2 <= 0) {
            return list5;
        }
        integer += 1 + random.nextInt(integer2 / 4 + 1) + random.nextInt(integer2 / 4 + 1);
        final float float8 = (random.nextFloat() + random.nextFloat() - 1.0f) * 0.15f;
        integer = Mth.clamp(Math.round(integer + integer * float8), 1, Integer.MAX_VALUE);
        final List<EnchantmentInstance> list6 = getAvailableEnchantmentResults(integer, bcj, boolean4);
        if (!list6.isEmpty()) {
            list5.add(WeighedRandom.<EnchantmentInstance>getRandomItem(random, list6));
            while (random.nextInt(50) <= integer) {
                filterCompatibleEnchantments(list6, Util.<EnchantmentInstance>lastOf(list5));
                if (list6.isEmpty()) {
                    break;
                }
                list5.add(WeighedRandom.<EnchantmentInstance>getRandomItem(random, list6));
                integer /= 2;
            }
        }
        return list5;
    }
    
    public static void filterCompatibleEnchantments(final List<EnchantmentInstance> list, final EnchantmentInstance bfv) {
        final Iterator<EnchantmentInstance> iterator3 = (Iterator<EnchantmentInstance>)list.iterator();
        while (iterator3.hasNext()) {
            if (!bfv.enchantment.isCompatibleWith(((EnchantmentInstance)iterator3.next()).enchantment)) {
                iterator3.remove();
            }
        }
    }
    
    public static boolean isEnchantmentCompatible(final Collection<Enchantment> collection, final Enchantment bfs) {
        for (final Enchantment bfs2 : collection) {
            if (!bfs2.isCompatibleWith(bfs)) {
                return false;
            }
        }
        return true;
    }
    
    public static List<EnchantmentInstance> getAvailableEnchantmentResults(final int integer, final ItemStack bcj, final boolean boolean3) {
        final List<EnchantmentInstance> list4 = (List<EnchantmentInstance>)Lists.newArrayList();
        final Item bce5 = bcj.getItem();
        final boolean boolean4 = bcj.getItem() == Items.BOOK;
        for (final Enchantment bfs8 : Registry.ENCHANTMENT) {
            if (bfs8.isTreasureOnly() && !boolean3) {
                continue;
            }
            if (!bfs8.category.canEnchant(bce5) && !boolean4) {
                continue;
            }
            for (int integer2 = bfs8.getMaxLevel(); integer2 > bfs8.getMinLevel() - 1; --integer2) {
                if (integer >= bfs8.getMinCost(integer2) && integer <= bfs8.getMaxCost(integer2)) {
                    list4.add(new EnchantmentInstance(bfs8, integer2));
                    break;
                }
            }
        }
        return list4;
    }
    
    @FunctionalInterface
    interface EnchantmentVisitor {
        void accept(final Enchantment bfs, final int integer);
    }
}
