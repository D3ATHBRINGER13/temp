package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.pathfinder.Path;

public class DragonTakeoffPhase extends AbstractDragonPhaseInstance {
    private boolean firstTick;
    private Path currentPath;
    private Vec3 targetLocation;
    
    public DragonTakeoffPhase(final EnderDragon asp) {
        super(asp);
    }
    
    @Override
    public void doServerTick() {
        if (this.firstTick || this.currentPath == null) {
            this.firstTick = false;
            this.findNewTarget();
        }
        else {
            final BlockPos ew2 = this.dragon.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION);
            if (!ew2.closerThan(this.dragon.position(), 10.0)) {
                this.dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
            }
        }
    }
    
    @Override
    public void begin() {
        this.firstTick = true;
        this.currentPath = null;
        this.targetLocation = null;
    }
    
    private void findNewTarget() {
        final int integer2 = this.dragon.findClosestNode();
        final Vec3 csi3 = this.dragon.getHeadLookVector(1.0f);
        int integer3 = this.dragon.findClosestNode(-csi3.x * 40.0, 105.0, -csi3.z * 40.0);
        if (this.dragon.getDragonFight() == null || this.dragon.getDragonFight().getCrystalsAlive() <= 0) {
            integer3 -= 12;
            integer3 &= 0x7;
            integer3 += 12;
        }
        else {
            integer3 %= 12;
            if (integer3 < 0) {
                integer3 += 12;
            }
        }
        this.currentPath = this.dragon.findPath(integer2, integer3, null);
        this.navigateToNextPathNode();
    }
    
    private void navigateToNextPathNode() {
        if (this.currentPath != null) {
            this.currentPath.next();
            if (!this.currentPath.isDone()) {
                final Vec3 csi2 = this.currentPath.currentPos();
                this.currentPath.next();
                double double3;
                do {
                    double3 = csi2.y + this.dragon.getRandom().nextFloat() * 20.0f;
                } while (double3 < csi2.y);
                this.targetLocation = new Vec3(csi2.x, double3, csi2.z);
            }
        }
    }
    
    @Nullable
    @Override
    public Vec3 getFlyTargetLocation() {
        return this.targetLocation;
    }
    
    public EnderDragonPhase<DragonTakeoffPhase> getPhase() {
        return EnderDragonPhase.TAKEOFF;
    }
}
