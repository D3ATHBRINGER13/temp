package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

public interface BonemealableBlock {
    boolean isValidBonemealTarget(final BlockGetter bhb, final BlockPos ew, final BlockState bvt, final boolean boolean4);
    
    boolean isBonemealSuccess(final Level bhr, final Random random, final BlockPos ew, final BlockState bvt);
    
    void performBonemeal(final Level bhr, final Random random, final BlockPos ew, final BlockState bvt);
}
