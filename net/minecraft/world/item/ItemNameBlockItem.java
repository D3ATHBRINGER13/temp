package net.minecraft.world.item;

import net.minecraft.world.level.block.Block;

public class ItemNameBlockItem extends BlockItem {
    public ItemNameBlockItem(final Block bmv, final Properties a) {
        super(bmv, a);
    }
    
    @Override
    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }
}
