package net.minecraft.world.level.block;

import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.item.DyeColor;

public class StainedGlassBlock extends AbstractGlassBlock implements BeaconBeamBlock {
    private final DyeColor color;
    
    public StainedGlassBlock(final DyeColor bbg, final Properties c) {
        super(c);
        this.color = bbg;
    }
    
    @Override
    public DyeColor getColor() {
        return this.color;
    }
    
    public BlockLayer getRenderLayer() {
        return BlockLayer.TRANSLUCENT;
    }
}
