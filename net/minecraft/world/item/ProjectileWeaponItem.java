package net.minecraft.world.item;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import java.util.function.Predicate;

public abstract class ProjectileWeaponItem extends Item {
    public static final Predicate<ItemStack> ARROW_ONLY;
    public static final Predicate<ItemStack> ARROW_OR_FIREWORK;
    
    public ProjectileWeaponItem(final Properties a) {
        super(a);
    }
    
    public Predicate<ItemStack> getSupportedHeldProjectiles() {
        return this.getAllSupportedProjectiles();
    }
    
    public abstract Predicate<ItemStack> getAllSupportedProjectiles();
    
    public static ItemStack getHeldProjectile(final LivingEntity aix, final Predicate<ItemStack> predicate) {
        if (predicate.test(aix.getItemInHand(InteractionHand.OFF_HAND))) {
            return aix.getItemInHand(InteractionHand.OFF_HAND);
        }
        if (predicate.test(aix.getItemInHand(InteractionHand.MAIN_HAND))) {
            return aix.getItemInHand(InteractionHand.MAIN_HAND);
        }
        return ItemStack.EMPTY;
    }
    
    @Override
    public int getEnchantmentValue() {
        return 1;
    }
    
    static {
        ARROW_ONLY = (bcj -> bcj.getItem().is(ItemTags.ARROWS));
        ARROW_OR_FIREWORK = ProjectileWeaponItem.ARROW_ONLY.or(bcj -> bcj.getItem() == Items.FIREWORK_ROCKET);
    }
}
