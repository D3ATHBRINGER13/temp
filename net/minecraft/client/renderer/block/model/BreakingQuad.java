package net.minecraft.client.renderer.block.model;

import java.util.Arrays;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class BreakingQuad extends BakedQuad {
    private final TextureAtlasSprite breakingIcon;
    
    public BreakingQuad(final BakedQuad dnz, final TextureAtlasSprite dxb) {
        super(Arrays.copyOf(dnz.getVertices(), dnz.getVertices().length), dnz.tintIndex, FaceBakery.calculateFacing(dnz.getVertices()), dnz.getSprite());
        this.breakingIcon = dxb;
        this.calculateBreakingUVs();
    }
    
    private void calculateBreakingUVs() {
        for (int integer2 = 0; integer2 < 4; ++integer2) {
            final int integer3 = 7 * integer2;
            this.vertices[integer3 + 4] = Float.floatToRawIntBits(this.breakingIcon.getU(this.sprite.getUOffset(Float.intBitsToFloat(this.vertices[integer3 + 4]))));
            this.vertices[integer3 + 4 + 1] = Float.floatToRawIntBits(this.breakingIcon.getV(this.sprite.getVOffset(Float.intBitsToFloat(this.vertices[integer3 + 4 + 1]))));
        }
    }
}
