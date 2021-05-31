package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;
import java.util.EnumSet;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import java.util.function.Predicate;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.LivingEntity;

public class AvoidEntityGoal<T extends LivingEntity> extends Goal {
    protected final PathfinderMob mob;
    private final double walkSpeedModifier;
    private final double sprintSpeedModifier;
    protected T toAvoid;
    protected final float maxDist;
    protected Path path;
    protected final PathNavigation pathNav;
    protected final Class<T> avoidClass;
    protected final Predicate<LivingEntity> avoidPredicate;
    protected final Predicate<LivingEntity> predicateOnAvoidEntity;
    private final TargetingConditions avoidEntityTargeting;
    
    public AvoidEntityGoal(final PathfinderMob aje, final Class<T> class2, final float float3, final double double4, final double double5) {
        this(aje, (Class)class2, aix -> true, float3, double4, double5, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test);
    }
    
    public AvoidEntityGoal(final PathfinderMob aje, final Class<T> class2, final Predicate<LivingEntity> predicate3, final float float4, final double double5, final double double6, final Predicate<LivingEntity> predicate7) {
        this.mob = aje;
        this.avoidClass = class2;
        this.avoidPredicate = predicate3;
        this.maxDist = float4;
        this.walkSpeedModifier = double5;
        this.sprintSpeedModifier = double6;
        this.predicateOnAvoidEntity = predicate7;
        this.pathNav = aje.getNavigation();
        this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE));
        this.avoidEntityTargeting = new TargetingConditions().range(float4).selector((Predicate<LivingEntity>)predicate7.and((Predicate)predicate3));
    }
    
    public AvoidEntityGoal(final PathfinderMob aje, final Class<T> class2, final float float3, final double double4, final double double5, final Predicate<LivingEntity> predicate) {
        this(aje, (Class)class2, aix -> true, float3, double4, double5, (Predicate)predicate);
    }
    
    @Override
    public boolean canUse() {
        this.toAvoid = this.mob.level.<T>getNearestLoadedEntity((java.lang.Class<? extends T>)this.avoidClass, this.avoidEntityTargeting, (LivingEntity)this.mob, this.mob.x, this.mob.y, this.mob.z, this.mob.getBoundingBox().inflate(this.maxDist, 3.0, this.maxDist));
        if (this.toAvoid == null) {
            return false;
        }
        final Vec3 csi2 = RandomPos.getPosAvoid(this.mob, 16, 7, new Vec3(this.toAvoid.x, this.toAvoid.y, this.toAvoid.z));
        if (csi2 == null) {
            return false;
        }
        if (this.toAvoid.distanceToSqr(csi2.x, csi2.y, csi2.z) < this.toAvoid.distanceToSqr(this.mob)) {
            return false;
        }
        this.path = this.pathNav.createPath(csi2.x, csi2.y, csi2.z, 0);
        return this.path != null;
    }
    
    @Override
    public boolean canContinueToUse() {
        return !this.pathNav.isDone();
    }
    
    @Override
    public void start() {
        this.pathNav.moveTo(this.path, this.walkSpeedModifier);
    }
    
    @Override
    public void stop() {
        this.toAvoid = null;
    }
    
    @Override
    public void tick() {
        if (this.mob.distanceToSqr(this.toAvoid) < 49.0) {
            this.mob.getNavigation().setSpeedModifier(this.sprintSpeedModifier);
        }
        else {
            this.mob.getNavigation().setSpeedModifier(this.walkSpeedModifier);
        }
    }
}
