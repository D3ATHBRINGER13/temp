package net.minecraft.world.entity.ai.navigation;

import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Mob;
import net.minecraft.core.BlockPos;

public class WallClimberNavigation extends GroundPathNavigation {
    private BlockPos pathToPosition;
    
    public WallClimberNavigation(final Mob aiy, final Level bhr) {
        super(aiy, bhr);
    }
    
    @Override
    public Path createPath(final BlockPos ew, final int integer) {
        this.pathToPosition = ew;
        return super.createPath(ew, integer);
    }
    
    @Override
    public Path createPath(final Entity aio, final int integer) {
        this.pathToPosition = new BlockPos(aio);
        return super.createPath(aio, integer);
    }
    
    @Override
    public boolean moveTo(final Entity aio, final double double2) {
        final Path cnr5 = this.createPath(aio, 0);
        if (cnr5 != null) {
            return this.moveTo(cnr5, double2);
        }
        this.pathToPosition = new BlockPos(aio);
        this.speedModifier = double2;
        return true;
    }
    
    @Override
    public void tick() {
        if (this.isDone()) {
            if (this.pathToPosition != null) {
                if (this.pathToPosition.closerThan(this.mob.position(), this.mob.getBbWidth()) || (this.mob.y > this.pathToPosition.getY() && new BlockPos(this.pathToPosition.getX(), this.mob.y, this.pathToPosition.getZ()).closerThan(this.mob.position(), this.mob.getBbWidth()))) {
                    this.pathToPosition = null;
                }
                else {
                    this.mob.getMoveControl().setWantedPosition(this.pathToPosition.getX(), this.pathToPosition.getY(), this.pathToPosition.getZ(), this.speedModifier);
                }
            }
            return;
        }
        super.tick();
    }
}
