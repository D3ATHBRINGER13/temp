package net.minecraft.world.level.block;

import java.util.List;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.item.ItemStack;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class PlayerWallHeadBlock extends WallSkullBlock {
    protected PlayerWallHeadBlock(final Properties c) {
        super(SkullBlock.Types.PLAYER, c);
    }
    
    @Override
    public void setPlacedBy(final Level bhr, final BlockPos ew, final BlockState bvt, @Nullable final LivingEntity aix, final ItemStack bcj) {
        Blocks.PLAYER_HEAD.setPlacedBy(bhr, ew, bvt, aix, bcj);
    }
    
    @Override
    public List<ItemStack> getDrops(final BlockState bvt, final LootContext.Builder a) {
        return Blocks.PLAYER_HEAD.getDrops(bvt, a);
    }
}
