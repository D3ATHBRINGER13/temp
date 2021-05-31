package net.minecraft.world.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.resources.ResourceLocation;

public class ShieldItem extends Item {
    public ShieldItem(final Properties a) {
        super(a);
        this.addProperty(new ResourceLocation("blocking"), (bcj, bhr, aix) -> (aix != null && aix.isUsingItem() && aix.getUseItem() == bcj) ? 1.0f : 0.0f);
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
    }
    
    @Override
    public String getDescriptionId(final ItemStack bcj) {
        if (bcj.getTagElement("BlockEntityTag") != null) {
            return this.getDescriptionId() + '.' + getColor(bcj).getName();
        }
        return super.getDescriptionId(bcj);
    }
    
    @Override
    public void appendHoverText(final ItemStack bcj, @Nullable final Level bhr, final List<Component> list, final TooltipFlag bdr) {
        BannerItem.appendHoverTextFromBannerBlockEntityTag(bcj, list);
    }
    
    @Override
    public UseAnim getUseAnimation(final ItemStack bcj) {
        return UseAnim.BLOCK;
    }
    
    @Override
    public int getUseDuration(final ItemStack bcj) {
        return 72000;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(final Level bhr, final Player awg, final InteractionHand ahi) {
        final ItemStack bcj5 = awg.getItemInHand(ahi);
        awg.startUsingItem(ahi);
        return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, bcj5);
    }
    
    @Override
    public boolean isValidRepairItem(final ItemStack bcj1, final ItemStack bcj2) {
        return ItemTags.PLANKS.contains(bcj2.getItem()) || super.isValidRepairItem(bcj1, bcj2);
    }
    
    public static DyeColor getColor(final ItemStack bcj) {
        return DyeColor.byId(bcj.getOrCreateTagElement("BlockEntityTag").getInt("Base"));
    }
}
