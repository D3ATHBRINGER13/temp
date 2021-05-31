package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;

public class GameMasterBlockItem extends BlockItem {
    public GameMasterBlockItem(final Block bmv, final Properties a) {
        super(bmv, a);
    }
    
    @Nullable
    @Override
    protected BlockState getPlacementState(final BlockPlaceContext ban) {
        final Player awg3 = ban.getPlayer();
        return (awg3 == null || awg3.canUseGameMasterBlocks()) ? super.getPlacementState(ban) : null;
    }
}
