package net.minecraft.world.item;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.network.chat.ChatType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.Util;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import java.util.Collection;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.core.Registry;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class DebugStickItem extends Item {
    public DebugStickItem(final Properties a) {
        super(a);
    }
    
    @Override
    public boolean isFoil(final ItemStack bcj) {
        return true;
    }
    
    @Override
    public boolean canAttackBlock(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg) {
        if (!bhr.isClientSide) {
            this.handleInteraction(awg, bvt, bhr, ew, false, awg.getItemInHand(InteractionHand.MAIN_HAND));
        }
        return false;
    }
    
    @Override
    public InteractionResult useOn(final UseOnContext bdu) {
        final Player awg3 = bdu.getPlayer();
        final Level bhr4 = bdu.getLevel();
        if (!bhr4.isClientSide && awg3 != null) {
            final BlockPos ew5 = bdu.getClickedPos();
            this.handleInteraction(awg3, bhr4.getBlockState(ew5), bhr4, ew5, true, bdu.getItemInHand());
        }
        return InteractionResult.SUCCESS;
    }
    
    private void handleInteraction(final Player awg, final BlockState bvt, final LevelAccessor bhs, final BlockPos ew, final boolean boolean5, final ItemStack bcj) {
        if (!awg.canUseGameMasterBlocks()) {
            return;
        }
        final Block bmv8 = bvt.getBlock();
        final StateDefinition<Block, BlockState> bvu9 = bmv8.getStateDefinition();
        final Collection<Property<?>> collection10 = bvu9.getProperties();
        final String string11 = Registry.BLOCK.getKey(bmv8).toString();
        if (collection10.isEmpty()) {
            message(awg, new TranslatableComponent(this.getDescriptionId() + ".empty", new Object[] { string11 }));
            return;
        }
        final CompoundTag id12 = bcj.getOrCreateTagElement("DebugProperty");
        final String string12 = id12.getString(string11);
        Property<?> bww14 = bvu9.getProperty(string12);
        if (boolean5) {
            if (bww14 == null) {
                bww14 = collection10.iterator().next();
            }
            final BlockState bvt2 = DebugStickItem.cycleState(bvt, bww14, awg.isSneaking());
            bhs.setBlock(ew, bvt2, 18);
            message(awg, new TranslatableComponent(this.getDescriptionId() + ".update", new Object[] { bww14.getName(), DebugStickItem.getNameHelper(bvt2, bww14) }));
        }
        else {
            bww14 = DebugStickItem.<Property<?>>getRelative((java.lang.Iterable<Property<?>>)collection10, bww14, awg.isSneaking());
            final String string13 = bww14.getName();
            id12.putString(string11, string13);
            message(awg, new TranslatableComponent(this.getDescriptionId() + ".select", new Object[] { string13, DebugStickItem.getNameHelper(bvt, bww14) }));
        }
    }
    
    private static <T extends Comparable<T>> BlockState cycleState(final BlockState bvt, final Property<T> bww, final boolean boolean3) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<T, Comparable>setValue(bww, (Comparable)DebugStickItem.<V>getRelative((java.lang.Iterable<V>)bww.getPossibleValues(), (V)bvt.<T>getValue((Property<T>)bww), boolean3));
    }
    
    private static <T> T getRelative(final Iterable<T> iterable, @Nullable final T object, final boolean boolean3) {
        return boolean3 ? Util.<T>findPreviousInIterable(iterable, object) : Util.<T>findNextInIterable(iterable, object);
    }
    
    private static void message(final Player awg, final Component jo) {
        ((ServerPlayer)awg).sendMessage(jo, ChatType.GAME_INFO);
    }
    
    private static <T extends Comparable<T>> String getNameHelper(final BlockState bvt, final Property<T> bww) {
        return bww.getName(bvt.<T>getValue(bww));
    }
}
