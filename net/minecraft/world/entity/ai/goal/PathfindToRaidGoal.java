package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.entity.raid.Raids;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Collection;
import java.util.function.Predicate;
import com.google.common.collect.Sets;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import java.util.EnumSet;
import net.minecraft.world.entity.raid.Raider;

public class PathfindToRaidGoal<T extends Raider> extends Goal {
    private final T mob;
    
    public PathfindToRaidGoal(final T axl) {
        this.mob = axl;
        this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE));
    }
    
    @Override
    public boolean canUse() {
        return this.mob.getTarget() == null && !this.mob.isVehicle() && this.mob.hasActiveRaid() && !this.mob.getCurrentRaid().isOver() && !((ServerLevel)this.mob.level).isVillage(new BlockPos(this.mob));
    }
    
    @Override
    public boolean canContinueToUse() {
        return this.mob.hasActiveRaid() && !this.mob.getCurrentRaid().isOver() && this.mob.level instanceof ServerLevel && !((ServerLevel)this.mob.level).isVillage(new BlockPos(this.mob));
    }
    
    @Override
    public void tick() {
        if (this.mob.hasActiveRaid()) {
            final Raid axk2 = this.mob.getCurrentRaid();
            if (this.mob.tickCount % 20 == 0) {
                this.recruitNearby(axk2);
            }
            if (!this.mob.isPathFinding()) {
                final Vec3 csi3 = RandomPos.getPosTowards(this.mob, 15, 4, new Vec3(axk2.getCenter()));
                if (csi3 != null) {
                    this.mob.getNavigation().moveTo(csi3.x, csi3.y, csi3.z, 1.0);
                }
            }
        }
    }
    
    private void recruitNearby(final Raid axk) {
        if (axk.isActive()) {
            final Set<Raider> set3 = (Set<Raider>)Sets.newHashSet();
            final List<Raider> list4 = this.mob.level.<Raider>getEntitiesOfClass((java.lang.Class<? extends Raider>)Raider.class, this.mob.getBoundingBox().inflate(16.0), (java.util.function.Predicate<? super Raider>)(axl -> !axl.hasActiveRaid() && Raids.canJoinRaid(axl, axk)));
            set3.addAll((Collection)list4);
            for (final Raider axl6 : set3) {
                axk.joinRaid(axk.getGroupsSpawned(), axl6, null, true);
            }
        }
    }
}
