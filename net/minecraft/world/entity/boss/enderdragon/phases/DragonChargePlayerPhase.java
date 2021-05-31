package net.minecraft.world.entity.boss.enderdragon.phases;

import org.apache.logging.log4j.LogManager;
import javax.annotation.Nullable;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.Logger;

public class DragonChargePlayerPhase extends AbstractDragonPhaseInstance {
    private static final Logger LOGGER;
    private Vec3 targetLocation;
    private int timeSinceCharge;
    
    public DragonChargePlayerPhase(final EnderDragon asp) {
        super(asp);
    }
    
    @Override
    public void doServerTick() {
        if (this.targetLocation == null) {
            DragonChargePlayerPhase.LOGGER.warn("Aborting charge player as no target was set.");
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
            return;
        }
        if (this.timeSinceCharge > 0 && this.timeSinceCharge++ >= 10) {
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
            return;
        }
        final double double2 = this.targetLocation.distanceToSqr(this.dragon.x, this.dragon.y, this.dragon.z);
        if (double2 < 100.0 || double2 > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
            ++this.timeSinceCharge;
        }
    }
    
    @Override
    public void begin() {
        this.targetLocation = null;
        this.timeSinceCharge = 0;
    }
    
    public void setTarget(final Vec3 csi) {
        this.targetLocation = csi;
    }
    
    @Override
    public float getFlySpeed() {
        return 3.0f;
    }
    
    @Nullable
    @Override
    public Vec3 getFlyTargetLocation() {
        return this.targetLocation;
    }
    
    public EnderDragonPhase<DragonChargePlayerPhase> getPhase() {
        return EnderDragonPhase.CHARGING_PLAYER;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
