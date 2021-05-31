package net.minecraft.world.item.alchemy;

import com.google.common.collect.Lists;
import java.util.Iterator;
import net.minecraft.core.Registry;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import java.util.function.Predicate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Item;
import java.util.List;

public class PotionBrewing {
    private static final List<Mix<Potion>> POTION_MIXES;
    private static final List<Mix<Item>> CONTAINER_MIXES;
    private static final List<Ingredient> ALLOWED_CONTAINERS;
    private static final Predicate<ItemStack> ALLOWED_CONTAINER;
    
    public static boolean isIngredient(final ItemStack bcj) {
        return isContainerIngredient(bcj) || isPotionIngredient(bcj);
    }
    
    protected static boolean isContainerIngredient(final ItemStack bcj) {
        for (int integer2 = 0, integer3 = PotionBrewing.CONTAINER_MIXES.size(); integer2 < integer3; ++integer2) {
            if (((Mix<Object>)PotionBrewing.CONTAINER_MIXES.get(integer2)).ingredient.test(bcj)) {
                return true;
            }
        }
        return false;
    }
    
    protected static boolean isPotionIngredient(final ItemStack bcj) {
        for (int integer2 = 0, integer3 = PotionBrewing.POTION_MIXES.size(); integer2 < integer3; ++integer2) {
            if (((Mix<Object>)PotionBrewing.POTION_MIXES.get(integer2)).ingredient.test(bcj)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isBrewablePotion(final Potion bdy) {
        for (int integer2 = 0, integer3 = PotionBrewing.POTION_MIXES.size(); integer2 < integer3; ++integer2) {
            if (((Mix<Object>)PotionBrewing.POTION_MIXES.get(integer2)).to == bdy) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean hasMix(final ItemStack bcj1, final ItemStack bcj2) {
        return PotionBrewing.ALLOWED_CONTAINER.test(bcj1) && (hasContainerMix(bcj1, bcj2) || hasPotionMix(bcj1, bcj2));
    }
    
    protected static boolean hasContainerMix(final ItemStack bcj1, final ItemStack bcj2) {
        final Item bce3 = bcj1.getItem();
        for (int integer4 = 0, integer5 = PotionBrewing.CONTAINER_MIXES.size(); integer4 < integer5; ++integer4) {
            final Mix<Item> a6 = (Mix<Item>)PotionBrewing.CONTAINER_MIXES.get(integer4);
            if (((Mix<Object>)a6).from == bce3 && ((Mix<Object>)a6).ingredient.test(bcj2)) {
                return true;
            }
        }
        return false;
    }
    
    protected static boolean hasPotionMix(final ItemStack bcj1, final ItemStack bcj2) {
        final Potion bdy3 = PotionUtils.getPotion(bcj1);
        for (int integer4 = 0, integer5 = PotionBrewing.POTION_MIXES.size(); integer4 < integer5; ++integer4) {
            final Mix<Potion> a6 = (Mix<Potion>)PotionBrewing.POTION_MIXES.get(integer4);
            if (((Mix<Object>)a6).from == bdy3 && ((Mix<Object>)a6).ingredient.test(bcj2)) {
                return true;
            }
        }
        return false;
    }
    
    public static ItemStack mix(final ItemStack bcj1, final ItemStack bcj2) {
        if (!bcj2.isEmpty()) {
            final Potion bdy3 = PotionUtils.getPotion(bcj2);
            final Item bce4 = bcj2.getItem();
            for (int integer5 = 0, integer6 = PotionBrewing.CONTAINER_MIXES.size(); integer5 < integer6; ++integer5) {
                final Mix<Item> a7 = (Mix<Item>)PotionBrewing.CONTAINER_MIXES.get(integer5);
                if (((Mix<Object>)a7).from == bce4 && ((Mix<Object>)a7).ingredient.test(bcj1)) {
                    return PotionUtils.setPotion(new ItemStack((ItemLike)((Mix<Object>)a7).to), bdy3);
                }
            }
            for (int integer5 = 0, integer6 = PotionBrewing.POTION_MIXES.size(); integer5 < integer6; ++integer5) {
                final Mix<Potion> a8 = (Mix<Potion>)PotionBrewing.POTION_MIXES.get(integer5);
                if (((Mix<Object>)a8).from == bdy3 && ((Mix<Object>)a8).ingredient.test(bcj1)) {
                    return PotionUtils.setPotion(new ItemStack(bce4), (Potion)((Mix<Object>)a8).to);
                }
            }
        }
        return bcj2;
    }
    
    public static void bootStrap() {
        addContainer(Items.POTION);
        addContainer(Items.SPLASH_POTION);
        addContainer(Items.LINGERING_POTION);
        addContainerRecipe(Items.POTION, Items.GUNPOWDER, Items.SPLASH_POTION);
        addContainerRecipe(Items.SPLASH_POTION, Items.DRAGON_BREATH, Items.LINGERING_POTION);
        addMix(Potions.WATER, Items.GLISTERING_MELON_SLICE, Potions.MUNDANE);
        addMix(Potions.WATER, Items.GHAST_TEAR, Potions.MUNDANE);
        addMix(Potions.WATER, Items.RABBIT_FOOT, Potions.MUNDANE);
        addMix(Potions.WATER, Items.BLAZE_POWDER, Potions.MUNDANE);
        addMix(Potions.WATER, Items.SPIDER_EYE, Potions.MUNDANE);
        addMix(Potions.WATER, Items.SUGAR, Potions.MUNDANE);
        addMix(Potions.WATER, Items.MAGMA_CREAM, Potions.MUNDANE);
        addMix(Potions.WATER, Items.GLOWSTONE_DUST, Potions.THICK);
        addMix(Potions.WATER, Items.REDSTONE, Potions.MUNDANE);
        addMix(Potions.WATER, Items.NETHER_WART, Potions.AWKWARD);
        addMix(Potions.AWKWARD, Items.GOLDEN_CARROT, Potions.NIGHT_VISION);
        addMix(Potions.NIGHT_VISION, Items.REDSTONE, Potions.LONG_NIGHT_VISION);
        addMix(Potions.NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.INVISIBILITY);
        addMix(Potions.LONG_NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.LONG_INVISIBILITY);
        addMix(Potions.INVISIBILITY, Items.REDSTONE, Potions.LONG_INVISIBILITY);
        addMix(Potions.AWKWARD, Items.MAGMA_CREAM, Potions.FIRE_RESISTANCE);
        addMix(Potions.FIRE_RESISTANCE, Items.REDSTONE, Potions.LONG_FIRE_RESISTANCE);
        addMix(Potions.AWKWARD, Items.RABBIT_FOOT, Potions.LEAPING);
        addMix(Potions.LEAPING, Items.REDSTONE, Potions.LONG_LEAPING);
        addMix(Potions.LEAPING, Items.GLOWSTONE_DUST, Potions.STRONG_LEAPING);
        addMix(Potions.LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
        addMix(Potions.LONG_LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
        addMix(Potions.SLOWNESS, Items.REDSTONE, Potions.LONG_SLOWNESS);
        addMix(Potions.SLOWNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SLOWNESS);
        addMix(Potions.AWKWARD, Items.TURTLE_HELMET, Potions.TURTLE_MASTER);
        addMix(Potions.TURTLE_MASTER, Items.REDSTONE, Potions.LONG_TURTLE_MASTER);
        addMix(Potions.TURTLE_MASTER, Items.GLOWSTONE_DUST, Potions.STRONG_TURTLE_MASTER);
        addMix(Potions.SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
        addMix(Potions.LONG_SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
        addMix(Potions.AWKWARD, Items.SUGAR, Potions.SWIFTNESS);
        addMix(Potions.SWIFTNESS, Items.REDSTONE, Potions.LONG_SWIFTNESS);
        addMix(Potions.SWIFTNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SWIFTNESS);
        addMix(Potions.AWKWARD, Items.PUFFERFISH, Potions.WATER_BREATHING);
        addMix(Potions.WATER_BREATHING, Items.REDSTONE, Potions.LONG_WATER_BREATHING);
        addMix(Potions.AWKWARD, Items.GLISTERING_MELON_SLICE, Potions.HEALING);
        addMix(Potions.HEALING, Items.GLOWSTONE_DUST, Potions.STRONG_HEALING);
        addMix(Potions.HEALING, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
        addMix(Potions.STRONG_HEALING, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
        addMix(Potions.HARMING, Items.GLOWSTONE_DUST, Potions.STRONG_HARMING);
        addMix(Potions.POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
        addMix(Potions.LONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
        addMix(Potions.STRONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
        addMix(Potions.AWKWARD, Items.SPIDER_EYE, Potions.POISON);
        addMix(Potions.POISON, Items.REDSTONE, Potions.LONG_POISON);
        addMix(Potions.POISON, Items.GLOWSTONE_DUST, Potions.STRONG_POISON);
        addMix(Potions.AWKWARD, Items.GHAST_TEAR, Potions.REGENERATION);
        addMix(Potions.REGENERATION, Items.REDSTONE, Potions.LONG_REGENERATION);
        addMix(Potions.REGENERATION, Items.GLOWSTONE_DUST, Potions.STRONG_REGENERATION);
        addMix(Potions.AWKWARD, Items.BLAZE_POWDER, Potions.STRENGTH);
        addMix(Potions.STRENGTH, Items.REDSTONE, Potions.LONG_STRENGTH);
        addMix(Potions.STRENGTH, Items.GLOWSTONE_DUST, Potions.STRONG_STRENGTH);
        addMix(Potions.WATER, Items.FERMENTED_SPIDER_EYE, Potions.WEAKNESS);
        addMix(Potions.WEAKNESS, Items.REDSTONE, Potions.LONG_WEAKNESS);
        addMix(Potions.AWKWARD, Items.PHANTOM_MEMBRANE, Potions.SLOW_FALLING);
        addMix(Potions.SLOW_FALLING, Items.REDSTONE, Potions.LONG_SLOW_FALLING);
    }
    
    private static void addContainerRecipe(final Item bce1, final Item bce2, final Item bce3) {
        if (!(bce1 instanceof PotionItem)) {
            throw new IllegalArgumentException(new StringBuilder().append("Expected a potion, got: ").append(Registry.ITEM.getKey(bce1)).toString());
        }
        if (!(bce3 instanceof PotionItem)) {
            throw new IllegalArgumentException(new StringBuilder().append("Expected a potion, got: ").append(Registry.ITEM.getKey(bce3)).toString());
        }
        PotionBrewing.CONTAINER_MIXES.add(new Mix(bce1, Ingredient.of(bce2), bce3));
    }
    
    private static void addContainer(final Item bce) {
        if (!(bce instanceof PotionItem)) {
            throw new IllegalArgumentException(new StringBuilder().append("Expected a potion, got: ").append(Registry.ITEM.getKey(bce)).toString());
        }
        PotionBrewing.ALLOWED_CONTAINERS.add(Ingredient.of(bce));
    }
    
    private static void addMix(final Potion bdy1, final Item bce, final Potion bdy3) {
        PotionBrewing.POTION_MIXES.add(new Mix(bdy1, Ingredient.of(bce), bdy3));
    }
    
    static {
        POTION_MIXES = (List)Lists.newArrayList();
        CONTAINER_MIXES = (List)Lists.newArrayList();
        ALLOWED_CONTAINERS = (List)Lists.newArrayList();
        ALLOWED_CONTAINER = (bcj -> {
            for (final Ingredient beo3 : PotionBrewing.ALLOWED_CONTAINERS) {
                if (beo3.test(bcj)) {
                    return true;
                }
            }
            return false;
        });
    }
    
    static class Mix<T> {
        private final T from;
        private final Ingredient ingredient;
        private final T to;
        
        public Mix(final T object1, final Ingredient beo, final T object3) {
            this.from = object1;
            this.ingredient = beo;
            this.to = object3;
        }
    }
}
