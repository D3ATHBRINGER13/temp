package net.minecraft.world.level.block;

import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.stats.Stats;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.DyeColor;

public class BeaconBlock extends BaseEntityBlock implements BeaconBeamBlock {
    public BeaconBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public DyeColor getColor() {
        return DyeColor.WHITE;
    }
    
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        return new BeaconBlockEntity();
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        if (bhr.isClientSide) {
            return true;
        }
        final BlockEntity btw8 = bhr.getBlockEntity(ew);
        if (btw8 instanceof BeaconBlockEntity) {
            awg.openMenu((MenuProvider)btw8);
            awg.awardStat(Stats.INTERACT_WITH_BEACON);
        }
        return true;
    }
    
    @Override
    public boolean isRedstoneConductor(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return false;
    }
    
    @Override
    public RenderShape getRenderShape(final BlockState bvt) {
        return RenderShape.MODEL;
    }
    
    @Override
    public void setPlacedBy(final Level bhr, final BlockPos ew, final BlockState bvt, final LivingEntity aix, final ItemStack bcj) {
        if (bcj.hasCustomHoverName()) {
            final BlockEntity btw7 = bhr.getBlockEntity(ew);
            if (btw7 instanceof BeaconBlockEntity) {
                ((BeaconBlockEntity)btw7).setCustomName(bcj.getHoverName());
            }
        }
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
}
