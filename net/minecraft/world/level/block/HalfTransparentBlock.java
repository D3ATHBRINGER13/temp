package net.minecraft.world.level.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class HalfTransparentBlock extends Block {
    protected HalfTransparentBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public boolean skipRendering(final BlockState bvt1, final BlockState bvt2, final Direction fb) {
        return bvt2.getBlock() == this || super.skipRendering(bvt1, bvt2, fb);
    }
}
