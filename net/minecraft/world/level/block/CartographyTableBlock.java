package net.minecraft.world.level.block;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.CartographyMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.MenuProvider;
import net.minecraft.stats.Stats;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.chat.TranslatableComponent;

public class CartographyTableBlock extends Block {
    private static final TranslatableComponent CONTAINER_TITLE;
    
    protected CartographyTableBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        awg.openMenu(bvt.getMenuProvider(bhr, ew));
        awg.awardStat(Stats.INTERACT_WITH_CARTOGRAPHY_TABLE);
        return true;
    }
    
    @Nullable
    @Override
    public MenuProvider getMenuProvider(final BlockState bvt, final Level bhr, final BlockPos ew) {
        return new SimpleMenuProvider((integer, awf, awg) -> new CartographyMenu(integer, awf, ContainerLevelAccess.create(bhr, ew)), CartographyTableBlock.CONTAINER_TITLE);
    }
    
    static {
        CONTAINER_TITLE = new TranslatableComponent("container.cartography_table", new Object[0]);
    }
}
