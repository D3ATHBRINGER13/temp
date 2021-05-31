package net.minecraft.world.item;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPattern;

public class BannerPatternItem extends Item {
    private final BannerPattern bannerPattern;
    
    public BannerPatternItem(final BannerPattern btp, final Properties a) {
        super(a);
        this.bannerPattern = btp;
    }
    
    public BannerPattern getBannerPattern() {
        return this.bannerPattern;
    }
    
    @Override
    public void appendHoverText(final ItemStack bcj, @Nullable final Level bhr, final List<Component> list, final TooltipFlag bdr) {
        list.add(this.getDisplayName().withStyle(ChatFormatting.GRAY));
    }
    
    public Component getDisplayName() {
        return new TranslatableComponent(this.getDescriptionId() + ".desc", new Object[0]);
    }
}
