package net.minecraft.world.entity.boss.enderdragon.phases;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.util.Mth;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.levelgen.Heightmap;
import javax.annotation.Nullable;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class DragonHoldingPatternPhase extends AbstractDragonPhaseInstance {
    private static final TargetingConditions NEW_TARGET_TARGETING;
    private Path currentPath;
    private Vec3 targetLocation;
    private boolean clockwise;
    
    public DragonHoldingPatternPhase(final EnderDragon asp) {
        super(asp);
    }
    
    public EnderDragonPhase<DragonHoldingPatternPhase> getPhase() {
        return EnderDragonPhase.HOLDING_PATTERN;
    }
    
    @Override
    public void doServerTick() {
        final double double2 = (this.targetLocation == null) ? 0.0 : this.targetLocation.distanceToSqr(this.dragon.x, this.dragon.y, this.dragon.z);
        if (double2 < 100.0 || double2 > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
            this.findNewTarget();
        }
    }
    
    @Override
    public void begin() {
        this.currentPath = null;
        this.targetLocation = null;
    }
    
    @Nullable
    @Override
    public Vec3 getFlyTargetLocation() {
        return this.targetLocation;
    }
    
    private void findNewTarget() {
        if (this.currentPath != null && this.currentPath.isDone()) {
            final BlockPos ew2 = this.dragon.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(EndPodiumFeature.END_PODIUM_LOCATION));
            final int integer3 = (this.dragon.getDragonFight() == null) ? 0 : this.dragon.getDragonFight().getCrystalsAlive();
            if (this.dragon.getRandom().nextInt(integer3 + 3) == 0) {
                this.dragon.getPhaseManager().setPhase(EnderDragonPhase.LANDING_APPROACH);
                return;
            }
            double double4 = 64.0;
            final Player awg6 = this.dragon.level.getNearestPlayer(DragonHoldingPatternPhase.NEW_TARGET_TARGETING, ew2.getX(), ew2.getY(), ew2.getZ());
            if (awg6 != null) {
                double4 = ew2.distSqr(awg6.position(), true) / 512.0;
            }
            if (awg6 != null && !awg6.abilities.invulnerable && (this.dragon.getRandom().nextInt(Mth.abs((int)double4) + 2) == 0 || this.dragon.getRandom().nextInt(integer3 + 2) == 0)) {
                this.strafePlayer(awg6);
                return;
            }
        }
        if (this.currentPath == null || this.currentPath.isDone()) {
            int integer3;
            final int integer4 = integer3 = this.dragon.findClosestNode();
            if (this.dragon.getRandom().nextInt(8) == 0) {
                this.clockwise = !this.clockwise;
                integer3 += 6;
            }
            if (this.clockwise) {
                ++integer3;
            }
            else {
                --integer3;
            }
            if (this.dragon.getDragonFight() == null || this.dragon.getDragonFight().getCrystalsAlive() < 0) {
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
            this.currentPath = this.dragon.findPath(integer4, integer3, null);
            if (this.currentPath != null) {
                this.currentPath.next();
            }
        }
        this.navigateToNextPathNode();
    }
    
    private void strafePlayer(final Player awg) {
        this.dragon.getPhaseManager().setPhase(EnderDragonPhase.STRAFE_PLAYER);
        this.dragon.getPhaseManager().<DragonStrafePlayerPhase>getPhase(EnderDragonPhase.STRAFE_PLAYER).setTarget(awg);
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
    
    @Override
    public void onCrystalDestroyed(final EndCrystal aso, final BlockPos ew, final DamageSource ahx, @Nullable final Player awg) {
        if (awg != null && !awg.abilities.invulnerable) {
            this.strafePlayer(awg);
        }
    }
    
    static {
        NEW_TARGET_TARGETING = new TargetingConditions().range(64.0);
    }
}
