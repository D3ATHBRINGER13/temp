package net.minecraft.world.level;

import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;

public class BlockEventData {
    private final BlockPos pos;
    private final Block block;
    private final int paramA;
    private final int paramB;
    
    public BlockEventData(final BlockPos ew, final Block bmv, final int integer3, final int integer4) {
        this.pos = ew;
        this.block = bmv;
        this.paramA = integer3;
        this.paramB = integer4;
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public Block getBlock() {
        return this.block;
    }
    
    public int getParamA() {
        return this.paramA;
    }
    
    public int getParamB() {
        return this.paramB;
    }
    
    public boolean equals(final Object object) {
        if (object instanceof BlockEventData) {
            final BlockEventData bha3 = (BlockEventData)object;
            return this.pos.equals(bha3.pos) && this.paramA == bha3.paramA && this.paramB == bha3.paramB && this.block == bha3.block;
        }
        return false;
    }
    
    public String toString() {
        return new StringBuilder().append("TE(").append(this.pos).append("),").append(this.paramA).append(",").append(this.paramB).append(",").append(this.block).toString();
    }
}
