package net.minecraft.world.item;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.InteractionResult;

public class EnderEyeItem extends Item {
    public EnderEyeItem(final Properties a) {
        super(a);
    }
    
    @Override
    public InteractionResult useOn(final UseOnContext bdu) {
        final Level bhr3 = bdu.getLevel();
        final BlockPos ew4 = bdu.getClickedPos();
        final BlockState bvt5 = bhr3.getBlockState(ew4);
        if (bvt5.getBlock() != Blocks.END_PORTAL_FRAME || bvt5.<Boolean>getValue((Property<Boolean>)EndPortalFrameBlock.HAS_EYE)) {
            return InteractionResult.PASS;
        }
        if (bhr3.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        final BlockState bvt6 = ((AbstractStateHolder<O, BlockState>)bvt5).<Comparable, Boolean>setValue((Property<Comparable>)EndPortalFrameBlock.HAS_EYE, true);
        Block.pushEntitiesUp(bvt5, bvt6, bhr3, ew4);
        bhr3.setBlock(ew4, bvt6, 2);
        bhr3.updateNeighbourForOutputSignal(ew4, Blocks.END_PORTAL_FRAME);
        bdu.getItemInHand().shrink(1);
        bhr3.levelEvent(1503, ew4, 0);
        final BlockPattern.BlockPatternMatch b7 = EndPortalFrameBlock.getOrCreatePortalShape().find(bhr3, ew4);
        if (b7 != null) {
            final BlockPos ew5 = b7.getFrontTopLeft().offset(-3, 0, -3);
            for (int integer9 = 0; integer9 < 3; ++integer9) {
                for (int integer10 = 0; integer10 < 3; ++integer10) {
                    bhr3.setBlock(ew5.offset(integer9, 0, integer10), Blocks.END_PORTAL.defaultBlockState(), 2);
                }
            }
            bhr3.globalLevelEvent(1038, ew5.offset(1, 0, 1), 0);
        }
        return InteractionResult.SUCCESS;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(final Level bhr, final Player awg, final InteractionHand ahi) {
        final ItemStack bcj5 = awg.getItemInHand(ahi);
        final HitResult csf6 = Item.getPlayerPOVHitResult(bhr, awg, ClipContext.Fluid.NONE);
        if (csf6.getType() == HitResult.Type.BLOCK && bhr.getBlockState(((BlockHitResult)csf6).getBlockPos()).getBlock() == Blocks.END_PORTAL_FRAME) {
            return new InteractionResultHolder<ItemStack>(InteractionResult.PASS, bcj5);
        }
        awg.startUsingItem(ahi);
        if (!bhr.isClientSide) {
            final BlockPos ew7 = bhr.getChunkSource().getGenerator().findNearestMapFeature(bhr, "Stronghold", new BlockPos(awg), 100, false);
            if (ew7 != null) {
                final EyeOfEnder awp8 = new EyeOfEnder(bhr, awg.x, awg.y + awg.getBbHeight() / 2.0f, awg.z);
                awp8.setItem(bcj5);
                awp8.signalTo(ew7);
                bhr.addFreshEntity(awp8);
                if (awg instanceof ServerPlayer) {
                    CriteriaTriggers.USED_ENDER_EYE.trigger((ServerPlayer)awg, ew7);
                }
                bhr.playSound(null, awg.x, awg.y, awg.z, SoundEvents.ENDER_EYE_LAUNCH, SoundSource.NEUTRAL, 0.5f, 0.4f / (EnderEyeItem.random.nextFloat() * 0.4f + 0.8f));
                bhr.levelEvent(null, 1003, new BlockPos(awg), 0);
                if (!awg.abilities.instabuild) {
                    bcj5.shrink(1);
                }
                awg.awardStat(Stats.ITEM_USED.get(this));
                return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, bcj5);
            }
        }
        return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, bcj5);
    }
}
