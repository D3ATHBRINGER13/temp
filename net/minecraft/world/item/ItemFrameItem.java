package net.minecraft.world.item;

import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.EntityType;

public class ItemFrameItem extends HangingEntityItem {
    public ItemFrameItem(final Properties a) {
        super(EntityType.ITEM_FRAME, a);
    }
    
    @Override
    protected boolean mayPlace(final Player awg, final Direction fb, final ItemStack bcj, final BlockPos ew) {
        return !Level.isOutsideBuildHeight(ew) && awg.mayUseItemAt(ew, fb, bcj);
    }
}
