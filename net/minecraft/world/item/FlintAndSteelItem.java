package net.minecraft.world.item;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import java.util.Iterator;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.BlockState;
import java.util.function.Consumer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;

public class FlintAndSteelItem extends Item {
    public FlintAndSteelItem(final Properties a) {
        super(a);
    }
    
    @Override
    public InteractionResult useOn(final UseOnContext bdu) {
        final Player awg3 = bdu.getPlayer();
        final LevelAccessor bhs4 = bdu.getLevel();
        final BlockPos ew5 = bdu.getClickedPos();
        final BlockPos ew6 = ew5.relative(bdu.getClickedFace());
        if (canUse(bhs4.getBlockState(ew6), bhs4, ew6)) {
            bhs4.playSound(awg3, ew6, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0f, FlintAndSteelItem.random.nextFloat() * 0.4f + 0.8f);
            final BlockState bvt7 = ((FireBlock)Blocks.FIRE).getStateForPlacement(bhs4, ew6);
            bhs4.setBlock(ew6, bvt7, 11);
            final ItemStack bcj8 = bdu.getItemInHand();
            if (awg3 instanceof ServerPlayer) {
                CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)awg3, ew6, bcj8);
                bcj8.<Player>hurtAndBreak(1, awg3, (java.util.function.Consumer<Player>)(awg -> awg.broadcastBreakEvent(bdu.getHand())));
            }
            return InteractionResult.SUCCESS;
        }
        final BlockState bvt7 = bhs4.getBlockState(ew5);
        if (canLightCampFire(bvt7)) {
            bhs4.playSound(awg3, ew5, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0f, FlintAndSteelItem.random.nextFloat() * 0.4f + 0.8f);
            bhs4.setBlock(ew5, ((AbstractStateHolder<O, BlockState>)bvt7).<Comparable, Boolean>setValue((Property<Comparable>)BlockStateProperties.LIT, true), 11);
            if (awg3 != null) {
                bdu.getItemInHand().<Player>hurtAndBreak(1, awg3, (java.util.function.Consumer<Player>)(awg -> awg.broadcastBreakEvent(bdu.getHand())));
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }
    
    public static boolean canLightCampFire(final BlockState bvt) {
        return bvt.getBlock() == Blocks.CAMPFIRE && !bvt.<Boolean>getValue((Property<Boolean>)BlockStateProperties.WATERLOGGED) && !bvt.<Boolean>getValue((Property<Boolean>)BlockStateProperties.LIT);
    }
    
    public static boolean canUse(final BlockState bvt, final LevelAccessor bhs, final BlockPos ew) {
        final BlockState bvt2 = ((FireBlock)Blocks.FIRE).getStateForPlacement(bhs, ew);
        boolean boolean5 = false;
        for (final Direction fb7 : Direction.Plane.HORIZONTAL) {
            if (bhs.getBlockState(ew.relative(fb7)).getBlock() == Blocks.OBSIDIAN && ((NetherPortalBlock)Blocks.NETHER_PORTAL).isPortal(bhs, ew) != null) {
                boolean5 = true;
            }
        }
        return bvt.isAir() && (bvt2.canSurvive(bhs, ew) || boolean5);
    }
}
