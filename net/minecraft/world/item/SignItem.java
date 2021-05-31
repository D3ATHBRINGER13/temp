package net.minecraft.world.item;

import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;

public class SignItem extends StandingAndWallBlockItem {
    public SignItem(final Properties a, final Block bmv2, final Block bmv3) {
        super(bmv2, bmv3, a);
    }
    
    @Override
    protected boolean updateCustomBlockEntityTag(final BlockPos ew, final Level bhr, @Nullable final Player awg, final ItemStack bcj, final BlockState bvt) {
        final boolean boolean7 = super.updateCustomBlockEntityTag(ew, bhr, awg, bcj, bvt);
        if (!bhr.isClientSide && !boolean7 && awg != null) {
            awg.openTextEdit((SignBlockEntity)bhr.getBlockEntity(ew));
        }
        return boolean7;
    }
}
