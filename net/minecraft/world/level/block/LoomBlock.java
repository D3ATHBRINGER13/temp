package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.LoomMenu;
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

public class LoomBlock extends HorizontalDirectionalBlock {
    private static final TranslatableComponent CONTAINER_TITLE;
    
    protected LoomBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        if (bhr.isClientSide) {
            return true;
        }
        awg.openMenu(bvt.getMenuProvider(bhr, ew));
        awg.awardStat(Stats.INTERACT_WITH_LOOM);
        return true;
    }
    
    @Override
    public MenuProvider getMenuProvider(final BlockState bvt, final Level bhr, final BlockPos ew) {
        return new SimpleMenuProvider((integer, awf, awg) -> new LoomMenu(integer, awf, ContainerLevelAccess.create(bhr, ew)), LoomBlock.CONTAINER_TITLE);
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        return ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)LoomBlock.FACING, ban.getHorizontalDirection().getOpposite());
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(LoomBlock.FACING);
    }
    
    static {
        CONTAINER_TITLE = new TranslatableComponent("container.loom", new Object[0]);
    }
}
