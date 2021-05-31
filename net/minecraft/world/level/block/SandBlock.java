package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.BlockState;

public class SandBlock extends FallingBlock {
    private final int dustColor;
    
    public SandBlock(final int integer, final Properties c) {
        super(c);
        this.dustColor = integer;
    }
    
    @Override
    public int getDustColor(final BlockState bvt) {
        return this.dustColor;
    }
}
