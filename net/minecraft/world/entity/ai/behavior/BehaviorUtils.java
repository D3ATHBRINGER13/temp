package net.minecraft.world.entity.ai.behavior;

import java.util.Comparator;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import java.util.List;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.LivingEntity;

public class BehaviorUtils {
    public static void lockGazeAndWalkToEachOther(final LivingEntity aix1, final LivingEntity aix2) {
        lookAtEachOther(aix1, aix2);
        walkToEachOther(aix1, aix2);
    }
    
    public static boolean entityIsVisible(final Brain<?> ajm, final LivingEntity aix) {
        return ajm.<List<LivingEntity>>getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).filter(list -> list.contains(aix)).isPresent();
    }
    
    public static boolean targetIsValid(final Brain<?> ajm, final MemoryModuleType<? extends LivingEntity> apj, final EntityType<?> ais) {
        return ajm.getMemory(apj).filter(aix -> aix.getType() == ais).filter(LivingEntity::isAlive).filter(aix -> entityIsVisible(ajm, aix)).isPresent();
    }
    
    public static void lookAtEachOther(final LivingEntity aix1, final LivingEntity aix2) {
        lookAtEntity(aix1, aix2);
        lookAtEntity(aix2, aix1);
    }
    
    public static void lookAtEntity(final LivingEntity aix1, final LivingEntity aix2) {
        aix1.getBrain().<EntityPosWrapper>setMemory((MemoryModuleType<EntityPosWrapper>)MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(aix2));
    }
    
    public static void walkToEachOther(final LivingEntity aix1, final LivingEntity aix2) {
        final int integer3 = 2;
        walkToEntity(aix1, aix2, 2);
        walkToEntity(aix2, aix1, 2);
    }
    
    public static void walkToEntity(final LivingEntity aix1, final LivingEntity aix2, final int integer) {
        final float float4 = (float)aix1.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue();
        final EntityPosWrapper akd5 = new EntityPosWrapper(aix2);
        final WalkTarget apl6 = new WalkTarget(akd5, float4, integer);
        aix1.getBrain().<PositionWrapper>setMemory(MemoryModuleType.LOOK_TARGET, akd5);
        aix1.getBrain().<WalkTarget>setMemory(MemoryModuleType.WALK_TARGET, apl6);
    }
    
    public static void throwItem(final LivingEntity aix1, final ItemStack bcj, final LivingEntity aix3) {
        final double double4 = aix1.y - 0.30000001192092896 + aix1.getEyeHeight();
        final ItemEntity atx6 = new ItemEntity(aix1.level, aix1.x, double4, aix1.z, bcj);
        final BlockPos ew7 = new BlockPos(aix3);
        final BlockPos ew8 = new BlockPos(aix1);
        final float float9 = 0.3f;
        Vec3 csi10 = new Vec3(ew7.subtract(ew8));
        csi10 = csi10.normalize().scale(0.30000001192092896);
        atx6.setDeltaMovement(csi10);
        atx6.setDefaultPickUpDelay();
        aix1.level.addFreshEntity(atx6);
    }
    
    public static SectionPos findSectionClosestToVillage(final ServerLevel vk, final SectionPos fp, final int integer) {
        final int integer2 = vk.sectionsToVillage(fp);
        return (SectionPos)SectionPos.cube(fp, integer).filter(fp -> vk.sectionsToVillage(fp) < integer2).min(Comparator.comparingInt(vk::sectionsToVillage)).orElse(fp);
    }
}
