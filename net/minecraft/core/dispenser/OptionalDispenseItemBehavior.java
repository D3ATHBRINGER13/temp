package net.minecraft.core.dispenser;

import net.minecraft.core.BlockSource;

public abstract class OptionalDispenseItemBehavior extends DefaultDispenseItemBehavior {
    protected boolean success;
    
    public OptionalDispenseItemBehavior() {
        this.success = true;
    }
    
    @Override
    protected void playSound(final BlockSource ex) {
        ex.getLevel().levelEvent(this.success ? 1000 : 1001, ex.getPos(), 0);
    }
}
