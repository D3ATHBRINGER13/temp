package net.minecraft.world.level.block;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LightLayer;
import java.util.Random;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockLayer;

public class IceBlock extends HalfTransparentBlock {
    public IceBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.TRANSLUCENT;
    }
    
    @Override
    public void playerDestroy(final Level bhr, final Player awg, final BlockPos ew, final BlockState bvt, @Nullable final BlockEntity btw, final ItemStack bcj) {
        super.playerDestroy(bhr, awg, ew, bvt, btw, bcj);
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, bcj) == 0) {
            if (bhr.dimension.isUltraWarm()) {
                bhr.removeBlock(ew, false);
                return;
            }
            final Material clo8 = bhr.getBlockState(ew.below()).getMaterial();
            if (clo8.blocksMotion() || clo8.isLiquid()) {
                bhr.setBlockAndUpdate(ew, Blocks.WATER.defaultBlockState());
            }
        }
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (bhr.getBrightness(LightLayer.BLOCK, ew) > 11 - bvt.getLightBlock(bhr, ew)) {
            this.melt(bvt, bhr, ew);
        }
    }
    
    protected void melt(final BlockState bvt, final Level bhr, final BlockPos ew) {
        if (bhr.dimension.isUltraWarm()) {
            bhr.removeBlock(ew, false);
            return;
        }
        bhr.setBlockAndUpdate(ew, Blocks.WATER.defaultBlockState());
        bhr.neighborChanged(ew, Blocks.WATER, ew);
    }
    
    @Override
    public PushReaction getPistonPushReaction(final BlockState bvt) {
        return PushReaction.NORMAL;
    }
    
    @Override
    public boolean isValidSpawn(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final EntityType<?> ais) {
        return ais == EntityType.POLAR_BEAR;
    }
}
