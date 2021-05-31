package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.BlockState;

public class GravelBlock extends FallingBlock {
    public GravelBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public int getDustColor(final BlockState bvt) {
        return -8356741;
    }
}
