package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.DyeColor;

public class StainedGlassPaneBlock extends IronBarsBlock implements BeaconBeamBlock {
    private final DyeColor color;
    
    public StainedGlassPaneBlock(final DyeColor bbg, final Properties c) {
        super(c);
        this.color = bbg;
        this.registerDefaultState(((((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)StainedGlassPaneBlock.NORTH, false)).setValue((Property<Comparable>)StainedGlassPaneBlock.EAST, false)).setValue((Property<Comparable>)StainedGlassPaneBlock.SOUTH, false)).setValue((Property<Comparable>)StainedGlassPaneBlock.WEST, false)).<Comparable, Boolean>setValue((Property<Comparable>)StainedGlassPaneBlock.WATERLOGGED, false));
    }
    
    @Override
    public DyeColor getColor() {
        return this.color;
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.TRANSLUCENT;
    }
}
