package net.minecraft.world.level.block;

import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;

public class SpawnerBlock extends BaseEntityBlock {
    protected SpawnerBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        return new SpawnerBlockEntity();
    }
    
    @Override
    public void spawnAfterBreak(final BlockState bvt, final Level bhr, final BlockPos ew, final ItemStack bcj) {
        super.spawnAfterBreak(bvt, bhr, ew, bcj);
        final int integer6 = 15 + bhr.random.nextInt(15) + bhr.random.nextInt(15);
        this.popExperience(bhr, ew, integer6);
    }
    
    @Override
    public RenderShape getRenderShape(final BlockState bvt) {
        return RenderShape.MODEL;
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    @Override
    public ItemStack getCloneItemStack(final BlockGetter bhb, final BlockPos ew, final BlockState bvt) {
        return ItemStack.EMPTY;
    }
}
