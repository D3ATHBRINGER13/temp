package net.minecraft.client.particle;

import net.minecraft.world.level.Level;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public abstract class TextureSheetParticle extends SingleQuadParticle {
    protected TextureAtlasSprite sprite;
    
    protected TextureSheetParticle(final Level bhr, final double double2, final double double3, final double double4) {
        super(bhr, double2, double3, double4);
    }
    
    protected TextureSheetParticle(final Level bhr, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7) {
        super(bhr, double2, double3, double4, double5, double6, double7);
    }
    
    protected void setSprite(final TextureAtlasSprite dxb) {
        this.sprite = dxb;
    }
    
    @Override
    protected float getU0() {
        return this.sprite.getU0();
    }
    
    @Override
    protected float getU1() {
        return this.sprite.getU1();
    }
    
    @Override
    protected float getV0() {
        return this.sprite.getV0();
    }
    
    @Override
    protected float getV1() {
        return this.sprite.getV1();
    }
    
    public void pickSprite(final SpriteSet dma) {
        this.setSprite(dma.get(this.random));
    }
    
    public void setSpriteFromAge(final SpriteSet dma) {
        this.setSprite(dma.get(this.age, this.lifetime));
    }
}
