package net.minecraft.world.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.resources.ResourceLocation;

public class ElytraItem extends Item {
    public ElytraItem(final Properties a) {
        super(a);
        this.addProperty(new ResourceLocation("broken"), (bcj, bhr, aix) -> isFlyEnabled(bcj) ? 0.0f : 1.0f);
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
    }
    
    public static boolean isFlyEnabled(final ItemStack bcj) {
        return bcj.getDamageValue() < bcj.getMaxDamage() - 1;
    }
    
    @Override
    public boolean isValidRepairItem(final ItemStack bcj1, final ItemStack bcj2) {
        return bcj2.getItem() == Items.PHANTOM_MEMBRANE;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(final Level bhr, final Player awg, final InteractionHand ahi) {
        final ItemStack bcj5 = awg.getItemInHand(ahi);
        final EquipmentSlot ait6 = Mob.getEquipmentSlotForItem(bcj5);
        final ItemStack bcj6 = awg.getItemBySlot(ait6);
        if (bcj6.isEmpty()) {
            awg.setItemSlot(ait6, bcj5.copy());
            bcj5.setCount(0);
            return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, bcj5);
        }
        return new InteractionResultHolder<ItemStack>(InteractionResult.FAIL, bcj5);
    }
}
