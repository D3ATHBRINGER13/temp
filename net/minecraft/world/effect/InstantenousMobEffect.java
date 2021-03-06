package net.minecraft.world.effect;

public class InstantenousMobEffect extends MobEffect {
    public InstantenousMobEffect(final MobEffectCategory aih, final int integer) {
        super(aih, integer);
    }
    
    @Override
    public boolean isInstantenous() {
        return true;
    }
    
    @Override
    public boolean isDurationEffectTick(final int integer1, final int integer2) {
        return integer1 >= 1;
    }
}
