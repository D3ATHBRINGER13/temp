package net.minecraft.world.level.block;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.MenuProvider;
import net.minecraft.stats.Stats;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.chat.Component;

public class CraftingTableBlock extends Block {
    private static final Component CONTAINER_TITLE;
    
    protected CraftingTableBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        awg.openMenu(bvt.getMenuProvider(bhr, ew));
        awg.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
        return true;
    }
    
    @Override
    public MenuProvider getMenuProvider(final BlockState bvt, final Level bhr, final BlockPos ew) {
        return new SimpleMenuProvider((integer, awf, awg) -> new CraftingMenu(integer, awf, ContainerLevelAccess.create(bhr, ew)), CraftingTableBlock.CONTAINER_TITLE);
    }
    
    static {
        CONTAINER_TITLE = new TranslatableComponent("container.crafting", new Object[0]);
    }
}
