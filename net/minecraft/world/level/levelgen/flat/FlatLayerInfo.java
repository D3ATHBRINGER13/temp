package net.minecraft.world.level.levelgen.flat;

import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class FlatLayerInfo {
    private final BlockState blockState;
    private final int height;
    private int start;
    
    public FlatLayerInfo(final int integer, final Block bmv) {
        this.height = integer;
        this.blockState = bmv.defaultBlockState();
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public BlockState getBlockState() {
        return this.blockState;
    }
    
    public int getStart() {
        return this.start;
    }
    
    public void setStart(final int integer) {
        this.start = integer;
    }
    
    public String toString() {
        return new StringBuilder().append((this.height != 1) ? new StringBuilder().append(this.height).append("*").toString() : "").append(Registry.BLOCK.getKey(this.blockState.getBlock())).toString();
    }
}
