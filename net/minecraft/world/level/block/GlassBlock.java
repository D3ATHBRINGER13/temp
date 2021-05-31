package net.minecraft.world.level.block;

import net.minecraft.world.level.BlockLayer;

public class GlassBlock extends AbstractGlassBlock {
    public GlassBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
}
