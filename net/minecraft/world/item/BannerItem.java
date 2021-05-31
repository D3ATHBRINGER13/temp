package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.network.chat.Component;
import java.util.List;
import org.apache.commons.lang3.Validate;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.Block;

public class BannerItem extends StandingAndWallBlockItem {
    public BannerItem(final Block bmv1, final Block bmv2, final Properties a) {
        super(bmv1, bmv2, a);
        Validate.isInstanceOf((Class)AbstractBannerBlock.class, bmv1);
        Validate.isInstanceOf((Class)AbstractBannerBlock.class, bmv2);
    }
    
    public static void appendHoverTextFromBannerBlockEntityTag(final ItemStack bcj, final List<Component> list) {
        final CompoundTag id3 = bcj.getTagElement("BlockEntityTag");
        if (id3 == null || !id3.contains("Patterns")) {
            return;
        }
        final ListTag ik4 = id3.getList("Patterns", 10);
        for (int integer5 = 0; integer5 < ik4.size() && integer5 < 6; ++integer5) {
            final CompoundTag id4 = ik4.getCompound(integer5);
            final DyeColor bbg7 = DyeColor.byId(id4.getInt("Color"));
            final BannerPattern btp8 = BannerPattern.byHash(id4.getString("Pattern"));
            if (btp8 != null) {
                list.add(new TranslatableComponent("block.minecraft.banner." + btp8.getFilename() + '.' + bbg7.getName(), new Object[0]).withStyle(ChatFormatting.GRAY));
            }
        }
    }
    
    public DyeColor getColor() {
        return ((AbstractBannerBlock)this.getBlock()).getColor();
    }
    
    @Override
    public void appendHoverText(final ItemStack bcj, @Nullable final Level bhr, final List<Component> list, final TooltipFlag bdr) {
        appendHoverTextFromBannerBlockEntityTag(bcj, list);
    }
}
