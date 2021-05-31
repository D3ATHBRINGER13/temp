package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import java.util.EnumSet;
import net.minecraft.world.entity.animal.horse.Llama;

public class LlamaFollowCaravanGoal extends Goal {
    public final Llama llama;
    private double speedModifier;
    private int distCheckCounter;
    
    public LlamaFollowCaravanGoal(final Llama ase, final double double2) {
        this.llama = ase;
        this.speedModifier = double2;
        this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE));
    }
    
    @Override
    public boolean canUse() {
        if (this.llama.isLeashed() || this.llama.inCaravan()) {
            return false;
        }
        final List<Entity> list2 = this.llama.level.getEntities(this.llama, this.llama.getBoundingBox().inflate(9.0, 4.0, 9.0), (aio -> {
            final EntityType<?> ais2 = aio.getType();
            return ais2 == EntityType.LLAMA || ais2 == EntityType.TRADER_LLAMA;
        }));
        Llama ase3 = null;
        double double4 = Double.MAX_VALUE;
        for (final Entity aio7 : list2) {
            final Llama ase4 = (Llama)aio7;
            if (ase4.inCaravan()) {
                if (ase4.hasCaravanTail()) {
                    continue;
                }
                final double double5 = this.llama.distanceToSqr(ase4);
                if (double5 > double4) {
                    continue;
                }
                double4 = double5;
                ase3 = ase4;
            }
        }
        if (ase3 == null) {
            for (final Entity aio7 : list2) {
                final Llama ase4 = (Llama)aio7;
                if (!ase4.isLeashed()) {
                    continue;
                }
                if (ase4.hasCaravanTail()) {
                    continue;
                }
                final double double5 = this.llama.distanceToSqr(ase4);
                if (double5 > double4) {
                    continue;
                }
                double4 = double5;
                ase3 = ase4;
            }
        }
        if (ase3 == null) {
            return false;
        }
        if (double4 < 4.0) {
            return false;
        }
        if (!ase3.isLeashed() && !this.firstIsLeashed(ase3, 1)) {
            return false;
        }
        this.llama.joinCaravan(ase3);
        return true;
    }
    
    @Override
    public boolean canContinueToUse() {
        if (!this.llama.inCaravan() || !this.llama.getCaravanHead().isAlive() || !this.firstIsLeashed(this.llama, 0)) {
            return false;
        }
        final double double2 = this.llama.distanceToSqr(this.llama.getCaravanHead());
        if (double2 > 676.0) {
            if (this.speedModifier <= 3.0) {
                this.speedModifier *= 1.2;
                this.distCheckCounter = 40;
                return true;
            }
            if (this.distCheckCounter == 0) {
                return false;
            }
        }
        if (this.distCheckCounter > 0) {
            --this.distCheckCounter;
        }
        return true;
    }
    
    @Override
    public void stop() {
        this.llama.leaveCaravan();
        this.speedModifier = 2.1;
    }
    
    @Override
    public void tick() {
        if (!this.llama.inCaravan()) {
            return;
        }
        final Llama ase2 = this.llama.getCaravanHead();
        final double double3 = this.llama.distanceTo(ase2);
        final float float5 = 2.0f;
        final Vec3 csi6 = new Vec3(ase2.x - this.llama.x, ase2.y - this.llama.y, ase2.z - this.llama.z).normalize().scale(Math.max(double3 - 2.0, 0.0));
        this.llama.getNavigation().moveTo(this.llama.x + csi6.x, this.llama.y + csi6.y, this.llama.z + csi6.z, this.speedModifier);
    }
    
    private boolean firstIsLeashed(final Llama ase, int integer) {
        return integer <= 8 && ase.inCaravan() && (ase.getCaravanHead().isLeashed() || this.firstIsLeashed(ase.getCaravanHead(), ++integer));
    }
}
