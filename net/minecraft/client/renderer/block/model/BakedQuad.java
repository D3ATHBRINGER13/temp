package net.minecraft.client.renderer.block.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

public class BakedQuad {
    protected final int[] vertices;
    protected final int tintIndex;
    protected final Direction direction;
    protected final TextureAtlasSprite sprite;
    
    public BakedQuad(final int[] arr, final int integer, final Direction fb, final TextureAtlasSprite dxb) {
        this.vertices = arr;
        this.tintIndex = integer;
        this.direction = fb;
        this.sprite = dxb;
    }
    
    public TextureAtlasSprite getSprite() {
        return this.sprite;
    }
    
    public int[] getVertices() {
        return this.vertices;
    }
    
    public boolean isTinted() {
        return this.tintIndex != -1;
    }
    
    public int getTintIndex() {
        return this.tintIndex;
    }
    
    public Direction getDirection() {
        return this.direction;
    }
}
