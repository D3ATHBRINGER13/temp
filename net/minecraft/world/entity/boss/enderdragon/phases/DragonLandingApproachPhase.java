package net.minecraft.world.entity.boss.enderdragon.phases;

import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.levelgen.Heightmap;
import javax.annotation.Nullable;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class DragonLandingApproachPhase extends AbstractDragonPhaseInstance {
    private static final TargetingConditions NEAR_EGG_TARGETING;
    private Path currentPath;
    private Vec3 targetLocation;
    
    public DragonLandingApproachPhase(final EnderDragon asp) {
        super(asp);
    }
    
    public EnderDragonPhase<DragonLandingApproachPhase> getPhase() {
        return EnderDragonPhase.LANDING_APPROACH;
    }
    
    @Override
    public void begin() {
        this.currentPath = null;
        this.targetLocation = null;
    }
    
    @Override
    public void doServerTick() {
        final double double2 = (this.targetLocation == null) ? 0.0 : this.targetLocation.distanceToSqr(this.dragon.x, this.dragon.y, this.dragon.z);
        if (double2 < 100.0 || double2 > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
            this.findNewTarget();
        }
    }
    
    @Nullable
    @Override
    public Vec3 getFlyTargetLocation() {
        return this.targetLocation;
    }
    
    private void findNewTarget() {
        if (this.currentPath == null || this.currentPath.isDone()) {
            final int integer2 = this.dragon.findClosestNode();
            final BlockPos ew3 = this.dragon.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION);
            final Player awg4 = this.dragon.level.getNearestPlayer(DragonLandingApproachPhase.NEAR_EGG_TARGETING, ew3.getX(), ew3.getY(), ew3.getZ());
            int integer3;
            if (awg4 != null) {
                final Vec3 csi6 = new Vec3(awg4.x, 0.0, awg4.z).normalize();
                integer3 = this.dragon.findClosestNode(-csi6.x * 40.0, 105.0, -csi6.z * 40.0);
            }
            else {
                integer3 = this.dragon.findClosestNode(40.0, ew3.getY(), 0.0);
            }
            final Node cnp6 = new Node(ew3.getX(), ew3.getY(), ew3.getZ());
            this.currentPath = this.dragon.findPath(integer2, integer3, cnp6);
            if (this.currentPath != null) {
                this.currentPath.next();
            }
        }
        this.navigateToNextPathNode();
        if (this.currentPath != null && this.currentPath.isDone()) {
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.LANDING);
        }
    }
    
    private void navigateToNextPathNode() {
        if (this.currentPath != null && !this.currentPath.isDone()) {
            final Vec3 csi2 = this.currentPath.currentPos();
            this.currentPath.next();
            final double double3 = csi2.x;
            final double double4 = csi2.z;
            double double5;
            do {
                double5 = csi2.y + this.dragon.getRandom().nextFloat() * 20.0f;
            } while (double5 < csi2.y);
            this.targetLocation = new Vec3(double3, double5, double4);
        }
    }
    
    static {
        NEAR_EGG_TARGETING = new TargetingConditions().range(128.0);
    }
}
