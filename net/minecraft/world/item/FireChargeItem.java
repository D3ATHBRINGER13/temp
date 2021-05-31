package net.minecraft.world.item;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.InteractionResult;

public class FireChargeItem extends Item {
    public FireChargeItem(final Properties a) {
        super(a);
    }
    
    @Override
    public InteractionResult useOn(final UseOnContext bdu) {
        final Level bhr3 = bdu.getLevel();
        if (bhr3.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockPos ew4 = bdu.getClickedPos();
        final BlockState bvt5 = bhr3.getBlockState(ew4);
        if (bvt5.getBlock() == Blocks.CAMPFIRE) {
            if (!bvt5.<Boolean>getValue((Property<Boolean>)CampfireBlock.LIT) && !bvt5.<Boolean>getValue((Property<Boolean>)CampfireBlock.WATERLOGGED)) {
                this.playSound(bhr3, ew4);
                bhr3.setBlockAndUpdate(ew4, ((AbstractStateHolder<O, BlockState>)bvt5).<Comparable, Boolean>setValue((Property<Comparable>)CampfireBlock.LIT, true));
            }
        }
        else {
            ew4 = ew4.relative(bdu.getClickedFace());
            if (bhr3.getBlockState(ew4).isAir()) {
                this.playSound(bhr3, ew4);
                bhr3.setBlockAndUpdate(ew4, ((FireBlock)Blocks.FIRE).getStateForPlacement(bhr3, ew4));
            }
        }
        bdu.getItemInHand().shrink(1);
        return InteractionResult.SUCCESS;
    }
    
    private void playSound(final Level bhr, final BlockPos ew) {
        bhr.playSound(null, ew, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0f, (FireChargeItem.random.nextFloat() - FireChargeItem.random.nextFloat()) * 0.2f + 1.0f);
    }
}
