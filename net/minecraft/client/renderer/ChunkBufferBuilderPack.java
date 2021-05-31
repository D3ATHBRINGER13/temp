package net.minecraft.client.renderer;

import net.minecraft.world.level.BlockLayer;
import com.mojang.blaze3d.vertex.BufferBuilder;

public class ChunkBufferBuilderPack {
    private final BufferBuilder[] builders;
    
    public ChunkBufferBuilderPack() {
        (this.builders = new BufferBuilder[BlockLayer.values().length])[BlockLayer.SOLID.ordinal()] = new BufferBuilder(2097152);
        this.builders[BlockLayer.CUTOUT.ordinal()] = new BufferBuilder(131072);
        this.builders[BlockLayer.CUTOUT_MIPPED.ordinal()] = new BufferBuilder(131072);
        this.builders[BlockLayer.TRANSLUCENT.ordinal()] = new BufferBuilder(262144);
    }
    
    public BufferBuilder builder(final BlockLayer bhc) {
        return this.builders[bhc.ordinal()];
    }
    
    public BufferBuilder builder(final int integer) {
        return this.builders[integer];
    }
}
