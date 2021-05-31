package net.minecraft.world.entity.ai.goal;

import net.minecraft.core.SectionPos;
import java.util.Random;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.core.Vec3i;
import net.minecraft.core.Position;
import net.minecraft.world.phys.Vec3;
import java.util.function.ToDoubleFunction;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;

public class StrollThroughVillageGoal extends Goal {
    private final PathfinderMob mob;
    private final int interval;
    @Nullable
    private BlockPos wantedPos;
    
    public StrollThroughVillageGoal(final PathfinderMob aje, final int integer) {
        this.mob = aje;
        this.interval = integer;
        this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE));
    }
    
    @Override
    public boolean canUse() {
        if (this.mob.isVehicle()) {
            return false;
        }
        if (this.mob.level.isDay()) {
            return false;
        }
        if (this.mob.getRandom().nextInt(this.interval) != 0) {
            return false;
        }
        final ServerLevel vk2 = (ServerLevel)this.mob.level;
        final BlockPos ew3 = new BlockPos(this.mob);
        if (!vk2.closeToVillage(ew3, 6)) {
            return false;
        }
        final Vec3 csi4 = RandomPos.getLandPos(this.mob, 15, 7, (ToDoubleFunction<BlockPos>)(ew -> -vk2.sectionsToVillage(SectionPos.of(ew))));
        this.wantedPos = ((csi4 == null) ? null : new BlockPos(csi4));
        return this.wantedPos != null;
    }
    
    @Override
    public boolean canContinueToUse() {
        return this.wantedPos != null && !this.mob.getNavigation().isDone() && this.mob.getNavigation().getTargetPos().equals(this.wantedPos);
    }
    
    @Override
    public void tick() {
        if (this.wantedPos == null) {
            return;
        }
        final PathNavigation app2 = this.mob.getNavigation();
        if (app2.isDone() && !this.wantedPos.closerThan(this.mob.position(), 10.0)) {
            Vec3 csi3 = new Vec3(this.wantedPos);
            final Vec3 csi4 = new Vec3(this.mob.x, this.mob.y, this.mob.z);
            final Vec3 csi5 = csi4.subtract(csi3);
            csi3 = csi5.scale(0.4).add(csi3);
            final Vec3 csi6 = csi3.subtract(csi4).normalize().scale(10.0).add(csi4);
            BlockPos ew7 = new BlockPos(csi6);
            ew7 = this.mob.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ew7);
            if (!app2.moveTo(ew7.getX(), ew7.getY(), ew7.getZ(), 1.0)) {
                this.moveRandomly();
            }
        }
    }
    
    private void moveRandomly() {
        final Random random2 = this.mob.getRandom();
        final BlockPos ew3 = this.mob.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(this.mob).offset(-8 + random2.nextInt(16), 0, -8 + random2.nextInt(16)));
        this.mob.getNavigation().moveTo(ew3.getX(), ew3.getY(), ew3.getZ(), 1.0);
    }
}
