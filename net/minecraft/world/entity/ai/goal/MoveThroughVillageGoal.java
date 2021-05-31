package net.minecraft.world.entity.ai.goal;

import net.minecraft.core.Vec3i;
import java.util.Iterator;
import java.util.Objects;
import net.minecraft.core.Position;
import net.minecraft.world.level.pathfinder.Node;
import java.util.Optional;
import net.minecraft.world.phys.Vec3;
import java.util.function.Predicate;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import java.util.function.ToDoubleFunction;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import java.util.EnumSet;
import com.google.common.collect.Lists;
import java.util.function.BooleanSupplier;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.entity.PathfinderMob;

public class MoveThroughVillageGoal extends Goal {
    protected final PathfinderMob mob;
    private final double speedModifier;
    private Path path;
    private BlockPos poiPos;
    private final boolean onlyAtNight;
    private final List<BlockPos> visited;
    private final int distanceToPoi;
    private final BooleanSupplier canDealWithDoors;
    
    public MoveThroughVillageGoal(final PathfinderMob aje, final double double2, final boolean boolean3, final int integer, final BooleanSupplier booleanSupplier) {
        this.visited = (List<BlockPos>)Lists.newArrayList();
        this.mob = aje;
        this.speedModifier = double2;
        this.onlyAtNight = boolean3;
        this.distanceToPoi = integer;
        this.canDealWithDoors = booleanSupplier;
        this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE));
        if (!(aje.getNavigation() instanceof GroundPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
        }
    }
    
    @Override
    public boolean canUse() {
        this.updateVisited();
        if (this.onlyAtNight && this.mob.level.isDay()) {
            return false;
        }
        final ServerLevel vk2 = (ServerLevel)this.mob.level;
        final BlockPos ew3 = new BlockPos(this.mob);
        if (!vk2.closeToVillage(ew3, 6)) {
            return false;
        }
        final Vec3 csi4 = RandomPos.getLandPos(this.mob, 15, 7, (ToDoubleFunction<BlockPos>)(ew3 -> {
            if (!vk2.isVillage(ew3)) {
                return Double.NEGATIVE_INFINITY;
            }
            final Optional<BlockPos> optional5 = vk2.getPoiManager().find(PoiType.ALL, (Predicate<BlockPos>)this::hasNotVisited, ew3, 10, PoiManager.Occupancy.IS_OCCUPIED);
            if (!optional5.isPresent()) {
                return Double.NEGATIVE_INFINITY;
            }
            return -((BlockPos)optional5.get()).distSqr(ew3);
        }));
        if (csi4 == null) {
            return false;
        }
        final Optional<BlockPos> optional5 = vk2.getPoiManager().find(PoiType.ALL, (Predicate<BlockPos>)this::hasNotVisited, new BlockPos(csi4), 10, PoiManager.Occupancy.IS_OCCUPIED);
        if (!optional5.isPresent()) {
            return false;
        }
        this.poiPos = ((BlockPos)optional5.get()).immutable();
        final GroundPathNavigation apo6 = (GroundPathNavigation)this.mob.getNavigation();
        final boolean boolean7 = apo6.canOpenDoors();
        apo6.setCanOpenDoors(this.canDealWithDoors.getAsBoolean());
        this.path = apo6.createPath(this.poiPos, 0);
        apo6.setCanOpenDoors(boolean7);
        if (this.path == null) {
            final Vec3 csi5 = RandomPos.getPosTowards(this.mob, 10, 7, new Vec3(this.poiPos.getX(), this.poiPos.getY(), this.poiPos.getZ()));
            if (csi5 == null) {
                return false;
            }
            apo6.setCanOpenDoors(this.canDealWithDoors.getAsBoolean());
            this.path = this.mob.getNavigation().createPath(csi5.x, csi5.y, csi5.z, 0);
            apo6.setCanOpenDoors(boolean7);
            if (this.path == null) {
                return false;
            }
        }
        for (int integer8 = 0; integer8 < this.path.getSize(); ++integer8) {
            final Node cnp9 = this.path.get(integer8);
            final BlockPos ew4 = new BlockPos(cnp9.x, cnp9.y + 1, cnp9.z);
            if (DoorInteractGoal.isDoor(this.mob.level, ew4)) {
                this.path = this.mob.getNavigation().createPath(cnp9.x, cnp9.y, cnp9.z, 0);
                break;
            }
        }
        return this.path != null;
    }
    
    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone() && !this.poiPos.closerThan(this.mob.position(), this.mob.getBbWidth() + this.distanceToPoi);
    }
    
    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.path, this.speedModifier);
    }
    
    @Override
    public void stop() {
        if (this.mob.getNavigation().isDone() || this.poiPos.closerThan(this.mob.position(), this.distanceToPoi)) {
            this.visited.add(this.poiPos);
        }
    }
    
    private boolean hasNotVisited(final BlockPos ew) {
        for (final BlockPos ew2 : this.visited) {
            if (Objects.equals(ew, ew2)) {
                return false;
            }
        }
        return true;
    }
    
    private void updateVisited() {
        if (this.visited.size() > 15) {
            this.visited.remove(0);
        }
    }
}
