package net.minecraft.world.level.block;

import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class PoweredBlock extends Block {
    public PoweredBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public boolean isSignalSource(final BlockState bvt) {
        return true;
    }
    
    @Override
    public int getSignal(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        return 15;
    }
}
