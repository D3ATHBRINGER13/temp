package net.minecraft.client.renderer.chunk;

import net.minecraft.core.Direction;
import com.google.common.collect.Lists;
import net.minecraft.world.level.BlockLayer;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.List;

public class CompiledChunk {
    public static final CompiledChunk UNCOMPILED;
    private final boolean[] hasBlocks;
    private final boolean[] hasLayer;
    private boolean isCompletelyEmpty;
    private final List<BlockEntity> renderableBlockEntities;
    private VisibilitySet visibilitySet;
    private BufferBuilder.State transparencyState;
    
    public CompiledChunk() {
        this.hasBlocks = new boolean[BlockLayer.values().length];
        this.hasLayer = new boolean[BlockLayer.values().length];
        this.isCompletelyEmpty = true;
        this.renderableBlockEntities = (List<BlockEntity>)Lists.newArrayList();
        this.visibilitySet = new VisibilitySet();
    }
    
    public boolean hasNoRenderableLayers() {
        return this.isCompletelyEmpty;
    }
    
    protected void setChanged(final BlockLayer bhc) {
        this.isCompletelyEmpty = false;
        this.hasBlocks[bhc.ordinal()] = true;
    }
    
    public boolean isEmpty(final BlockLayer bhc) {
        return !this.hasBlocks[bhc.ordinal()];
    }
    
    public void layerIsPresent(final BlockLayer bhc) {
        this.hasLayer[bhc.ordinal()] = true;
    }
    
    public boolean hasLayer(final BlockLayer bhc) {
        return this.hasLayer[bhc.ordinal()];
    }
    
    public List<BlockEntity> getRenderableBlockEntities() {
        return this.renderableBlockEntities;
    }
    
    public void addRenderableBlockEntity(final BlockEntity btw) {
        this.renderableBlockEntities.add(btw);
    }
    
    public boolean facesCanSeeEachother(final Direction fb1, final Direction fb2) {
        return this.visibilitySet.visibilityBetween(fb1, fb2);
    }
    
    public void setVisibilitySet(final VisibilitySet dqc) {
        this.visibilitySet = dqc;
    }
    
    public BufferBuilder.State getTransparencyState() {
        return this.transparencyState;
    }
    
    public void setTransparencyState(final BufferBuilder.State a) {
        this.transparencyState = a;
    }
    
    static {
        UNCOMPILED = new CompiledChunk() {
            @Override
            protected void setChanged(final BlockLayer bhc) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void layerIsPresent(final BlockLayer bhc) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public boolean facesCanSeeEachother(final Direction fb1, final Direction fb2) {
                return false;
            }
        };
    }
}
