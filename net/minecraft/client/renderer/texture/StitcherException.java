package net.minecraft.client.renderer.texture;

import java.util.Collection;

public class StitcherException extends RuntimeException {
    private final Collection<TextureAtlasSprite> allSprites;
    
    public StitcherException(final TextureAtlasSprite dxb, final Collection<TextureAtlasSprite> collection) {
        super(String.format("Unable to fit: %s - size: %dx%d - Maybe try a lower resolution resourcepack?", new Object[] { dxb.getName(), dxb.getWidth(), dxb.getHeight() }));
        this.allSprites = collection;
    }
    
    public Collection<TextureAtlasSprite> getAllSprites() {
        return this.allSprites;
    }
}
