package net.minecraft.world.item;

import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Block;

public class WaterLilyBlockItem extends BlockItem {
    public WaterLilyBlockItem(final Block bmv, final Properties a) {
        super(bmv, a);
    }
    
    @Override
    public InteractionResult useOn(final UseOnContext bdu) {
        return InteractionResult.PASS;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(final Level bhr, final Player awg, final InteractionHand ahi) {
        final ItemStack bcj5 = awg.getItemInHand(ahi);
        final HitResult csf6 = Item.getPlayerPOVHitResult(bhr, awg, ClipContext.Fluid.SOURCE_ONLY);
        if (csf6.getType() == HitResult.Type.MISS) {
            return new InteractionResultHolder<ItemStack>(InteractionResult.PASS, bcj5);
        }
        if (csf6.getType() == HitResult.Type.BLOCK) {
            final BlockHitResult csd7 = (BlockHitResult)csf6;
            final BlockPos ew8 = csd7.getBlockPos();
            final Direction fb9 = csd7.getDirection();
            if (!bhr.mayInteract(awg, ew8) || !awg.mayUseItemAt(ew8.relative(fb9), fb9, bcj5)) {
                return new InteractionResultHolder<ItemStack>(InteractionResult.FAIL, bcj5);
            }
            final BlockPos ew9 = ew8.above();
            final BlockState bvt11 = bhr.getBlockState(ew8);
            final Material clo12 = bvt11.getMaterial();
            final FluidState clk13 = bhr.getFluidState(ew8);
            if ((clk13.getType() == Fluids.WATER || clo12 == Material.ICE) && bhr.isEmptyBlock(ew9)) {
                bhr.setBlock(ew9, Blocks.LILY_PAD.defaultBlockState(), 11);
                if (awg instanceof ServerPlayer) {
                    CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)awg, ew9, bcj5);
                }
                if (!awg.abilities.instabuild) {
                    bcj5.shrink(1);
                }
                awg.awardStat(Stats.ITEM_USED.get(this));
                bhr.playSound(awg, ew8, SoundEvents.LILY_PAD_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
                return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, bcj5);
            }
        }
        return new InteractionResultHolder<ItemStack>(InteractionResult.FAIL, bcj5);
    }
}
