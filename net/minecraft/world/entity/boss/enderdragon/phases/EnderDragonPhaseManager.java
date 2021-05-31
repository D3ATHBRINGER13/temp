package net.minecraft.world.entity.boss.enderdragon.phases;

import org.apache.logging.log4j.LogManager;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.apache.logging.log4j.Logger;

public class EnderDragonPhaseManager {
    private static final Logger LOGGER;
    private final EnderDragon dragon;
    private final DragonPhaseInstance[] phases;
    private DragonPhaseInstance currentPhase;
    
    public EnderDragonPhaseManager(final EnderDragon asp) {
        this.phases = new DragonPhaseInstance[EnderDragonPhase.getCount()];
        this.dragon = asp;
        this.setPhase(EnderDragonPhase.HOVERING);
    }
    
    public void setPhase(final EnderDragonPhase<?> atf) {
        if (this.currentPhase != null && atf == this.currentPhase.getPhase()) {
            return;
        }
        if (this.currentPhase != null) {
            this.currentPhase.end();
        }
        this.currentPhase = this.<DragonPhaseInstance>getPhase(atf);
        if (!this.dragon.level.isClientSide) {
            this.dragon.getEntityData().<Integer>set(EnderDragon.DATA_PHASE, atf.getId());
        }
        EnderDragonPhaseManager.LOGGER.debug("Dragon is now in phase {} on the {}", atf, this.dragon.level.isClientSide ? "client" : "server");
        this.currentPhase.begin();
    }
    
    public DragonPhaseInstance getCurrentPhase() {
        return this.currentPhase;
    }
    
    public <T extends DragonPhaseInstance> T getPhase(final EnderDragonPhase<T> atf) {
        final int integer3 = atf.getId();
        if (this.phases[integer3] == null) {
            this.phases[integer3] = atf.createInstance(this.dragon);
        }
        return (T)this.phases[integer3];
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
