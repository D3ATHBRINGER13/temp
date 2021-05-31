package net.minecraft.world.entity.boss.enderdragon.phases;

import org.apache.logging.log4j.LogManager;
import javax.annotation.Nullable;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.pathfinder.Path;
import org.apache.logging.log4j.Logger;

public class DragonStrafePlayerPhase extends AbstractDragonPhaseInstance {
    private static final Logger LOGGER;
    private int fireballCharge;
    private Path currentPath;
    private Vec3 targetLocation;
    private LivingEntity attackTarget;
    private boolean holdingPatternClockwise;
    
    public DragonStrafePlayerPhase(final EnderDragon asp) {
        super(asp);
    }
    
    @Override
    public void doServerTick() {
        if (this.attackTarget == null) {
            DragonStrafePlayerPhase.LOGGER.warn("Skipping player strafe phase because no player was found");
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
            return;
        }
        if (this.currentPath != null && this.currentPath.isDone()) {
            final double double2 = this.attackTarget.x;
            final double double3 = this.attackTarget.z;
            final double double4 = double2 - this.dragon.x;
            final double double5 = double3 - this.dragon.z;
            final double double6 = Mth.sqrt(double4 * double4 + double5 * double5);
            final double double7 = Math.min(0.4000000059604645 + double6 / 80.0 - 1.0, 10.0);
            this.targetLocation = new Vec3(double2, this.attackTarget.y + double7, double3);
        }
        final double double2 = (this.targetLocation == null) ? 0.0 : this.targetLocation.distanceToSqr(this.dragon.x, this.dragon.y, this.dragon.z);
        if (double2 < 100.0 || double2 > 22500.0) {
            this.findNewTarget();
        }
        final double double3 = 64.0;
        if (this.attackTarget.distanceToSqr(this.dragon) < 4096.0) {
            if (this.dragon.canSee(this.attackTarget)) {
                ++this.fireballCharge;
                final Vec3 csi6 = new Vec3(this.attackTarget.x - this.dragon.x, 0.0, this.attackTarget.z - this.dragon.z).normalize();
                final Vec3 csi7 = new Vec3(Mth.sin(this.dragon.yRot * 0.017453292f), 0.0, -Mth.cos(this.dragon.yRot * 0.017453292f)).normalize();
                final float float8 = (float)csi7.dot(csi6);
                float float9 = (float)(Math.acos((double)float8) * 57.2957763671875);
                float9 += 0.5f;
                if (this.fireballCharge >= 5 && float9 >= 0.0f && float9 < 10.0f) {
                    final double double6 = 1.0;
                    final Vec3 csi8 = this.dragon.getViewVector(1.0f);
                    final double double8 = this.dragon.head.x - csi8.x * 1.0;
                    final double double9 = this.dragon.head.y + this.dragon.head.getBbHeight() / 2.0f + 0.5;
                    final double double10 = this.dragon.head.z - csi8.z * 1.0;
                    final double double11 = this.attackTarget.x - double8;
                    final double double12 = this.attackTarget.y + this.attackTarget.getBbHeight() / 2.0f - (double9 + this.dragon.head.getBbHeight() / 2.0f);
                    final double double13 = this.attackTarget.z - double10;
                    this.dragon.level.levelEvent(null, 1017, new BlockPos(this.dragon), 0);
                    final DragonFireball awn25 = new DragonFireball(this.dragon.level, this.dragon, double11, double12, double13);
                    awn25.moveTo(double8, double9, double10, 0.0f, 0.0f);
                    this.dragon.level.addFreshEntity(awn25);
                    this.fireballCharge = 0;
                    if (this.currentPath != null) {
                        while (!this.currentPath.isDone()) {
                            this.currentPath.next();
                        }
                    }
                    this.dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
                }
            }
            else if (this.fireballCharge > 0) {
                --this.fireballCharge;
            }
        }
        else if (this.fireballCharge > 0) {
            --this.fireballCharge;
        }
    }
    
    private void findNewTarget() {
        if (this.currentPath == null || this.currentPath.isDone()) {
            int integer3;
            final int integer2 = integer3 = this.dragon.findClosestNode();
            if (this.dragon.getRandom().nextInt(8) == 0) {
                this.holdingPatternClockwise = !this.holdingPatternClockwise;
                integer3 += 6;
            }
            if (this.holdingPatternClockwise) {
                ++integer3;
            }
            else {
                --integer3;
            }
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
            if (this.currentPath != null) {
                this.currentPath.next();
            }
        }
        this.navigateToNextPathNode();
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
    public void begin() {
        this.fireballCharge = 0;
        this.targetLocation = null;
        this.currentPath = null;
        this.attackTarget = null;
    }
    
    public void setTarget(final LivingEntity aix) {
        this.attackTarget = aix;
        final int integer3 = this.dragon.findClosestNode();
        final int integer4 = this.dragon.findClosestNode(this.attackTarget.x, this.attackTarget.y, this.attackTarget.z);
        final int integer5 = Mth.floor(this.attackTarget.x);
        final int integer6 = Mth.floor(this.attackTarget.z);
        final double double7 = integer5 - this.dragon.x;
        final double double8 = integer6 - this.dragon.z;
        final double double9 = Mth.sqrt(double7 * double7 + double8 * double8);
        final double double10 = Math.min(0.4000000059604645 + double9 / 80.0 - 1.0, 10.0);
        final int integer7 = Mth.floor(this.attackTarget.y + double10);
        final Node cnp16 = new Node(integer5, integer7, integer6);
        this.currentPath = this.dragon.findPath(integer3, integer4, cnp16);
        if (this.currentPath != null) {
            this.currentPath.next();
            this.navigateToNextPathNode();
        }
    }
    
    @Nullable
    @Override
    public Vec3 getFlyTargetLocation() {
        return this.targetLocation;
    }
    
    public EnderDragonPhase<DragonStrafePlayerPhase> getPhase() {
        return EnderDragonPhase.STRAFE_PLAYER;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
