package net.minecraft.client.player;

import net.minecraft.client.Options;

public class KeyboardInput extends Input {
    private final Options options;
    
    public KeyboardInput(final Options cyg) {
        this.options = cyg;
    }
    
    @Override
    public void tick(final boolean boolean1, final boolean boolean2) {
        this.up = this.options.keyUp.isDown();
        this.down = this.options.keyDown.isDown();
        this.left = this.options.keyLeft.isDown();
        this.right = this.options.keyRight.isDown();
        this.forwardImpulse = ((this.up == this.down) ? 0.0f : ((float)(this.up ? 1 : -1)));
        this.leftImpulse = ((this.left == this.right) ? 0.0f : ((float)(this.left ? 1 : -1)));
        this.jumping = this.options.keyJump.isDown();
        this.sneakKeyDown = this.options.keySneak.isDown();
        if (!boolean2 && (this.sneakKeyDown || boolean1)) {
            this.leftImpulse *= (float)0.3;
            this.forwardImpulse *= (float)0.3;
        }
    }
}
