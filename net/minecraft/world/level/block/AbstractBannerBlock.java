package net.minecraft.world.level.block;

import net.minecraft.world.item.ItemStack;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.DyeColor;

public abstract class AbstractBannerBlock extends BaseEntityBlock {
    private final DyeColor color;
    
    protected AbstractBannerBlock(final DyeColor bbg, final Properties c) {
        super(c);
        this.color = bbg;
    }
    
    @Override
    public boolean isPossibleToRespawnInThis() {
        return true;
    }
    
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        return new BannerBlockEntity(this.color);
    }
    
    @Override
    public void setPlacedBy(final Level bhr, final BlockPos ew, final BlockState bvt, @Nullable final LivingEntity aix, final ItemStack bcj) {
        if (bcj.hasCustomHoverName()) {
            final BlockEntity btw7 = bhr.getBlockEntity(ew);
            if (btw7 instanceof BannerBlockEntity) {
                ((BannerBlockEntity)btw7).setCustomName(bcj.getHoverName());
            }
        }
    }
    
    @Override
    public ItemStack getCloneItemStack(final BlockGetter bhb, final BlockPos ew, final BlockState bvt) {
        final BlockEntity btw5 = bhb.getBlockEntity(ew);
        if (btw5 instanceof BannerBlockEntity) {
            return ((BannerBlockEntity)btw5).getItem(bvt);
        }
        return super.getCloneItemStack(bhb, ew, bvt);
    }
    
    public DyeColor getColor() {
        return this.color;
    }
}
