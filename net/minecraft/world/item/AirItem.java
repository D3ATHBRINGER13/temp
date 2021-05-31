package net.minecraft.world.item;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.network.chat.Component;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class AirItem extends Item {
    private final Block block;
    
    public AirItem(final Block bmv, final Properties a) {
        super(a);
        this.block = bmv;
    }
    
    @Override
    public String getDescriptionId() {
        return this.block.getDescriptionId();
    }
    
    @Override
    public void appendHoverText(final ItemStack bcj, @Nullable final Level bhr, final List<Component> list, final TooltipFlag bdr) {
        super.appendHoverText(bcj, bhr, list, bdr);
        this.block.appendHoverText(bcj, bhr, list, bdr);
    }
}
