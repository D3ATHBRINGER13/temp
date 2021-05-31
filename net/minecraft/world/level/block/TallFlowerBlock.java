package net.minecraft.world.level.block;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;

public class TallFlowerBlock extends DoublePlantBlock implements BonemealableBlock {
    public TallFlowerBlock(final Properties c) {
        super(c);
    }
    
    public boolean canBeReplaced(final BlockState bvt, final BlockPlaceContext ban) {
        return false;
    }
    
    @Override
    public boolean isValidBonemealTarget(final BlockGetter bhb, final BlockPos ew, final BlockState bvt, final boolean boolean4) {
        return true;
    }
    
    @Override
    public boolean isBonemealSuccess(final Level bhr, final Random random, final BlockPos ew, final BlockState bvt) {
        return true;
    }
    
    @Override
    public void performBonemeal(final Level bhr, final Random random, final BlockPos ew, final BlockState bvt) {
        Block.popResource(bhr, ew, new ItemStack(this));
    }
}
