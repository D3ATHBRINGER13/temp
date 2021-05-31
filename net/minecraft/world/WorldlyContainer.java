package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;

public interface WorldlyContainer extends Container {
    int[] getSlotsForFace(final Direction fb);
    
    boolean canPlaceItemThroughFace(final int integer, final ItemStack bcj, @Nullable final Direction fb);
    
    boolean canTakeItemThroughFace(final int integer, final ItemStack bcj, final Direction fb);
}
