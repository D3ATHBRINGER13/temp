package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.Entity;
import java.util.function.Predicate;
import java.util.Set;
import com.google.common.collect.ImmutableSet;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import java.util.List;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import com.mojang.datafixers.util.Pair;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.npc.VillagerProfession;

public class VillagerGoalPackages {
    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getCorePackage(final VillagerProfession avw, final float float2) {
        return (ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>>)ImmutableList.of(Pair.of((Object)0, (Object)new Swim(0.4f, 0.8f)), Pair.of((Object)0, (Object)new InteractWithDoor()), Pair.of((Object)0, (Object)new LookAtTargetSink(45, 90)), Pair.of((Object)0, (Object)new VillagerPanicTrigger()), Pair.of((Object)0, (Object)new WakeUp()), Pair.of((Object)0, (Object)new ReactToBell()), Pair.of((Object)0, (Object)new SetRaidStatus()), Pair.of((Object)1, (Object)new MoveToTargetSink(200)), Pair.of((Object)2, (Object)new LookAndFollowTradingPlayerSink(float2)), Pair.of((Object)5, (Object)new PickUpItems()), Pair.of((Object)10, (Object)new AcquirePoi(avw.getJobPoiType(), MemoryModuleType.JOB_SITE, true)), Pair.of((Object)10, (Object)new AcquirePoi(PoiType.HOME, MemoryModuleType.HOME, false)), (Object[])new Pair[] { Pair.of(10, new AcquirePoi(PoiType.MEETING, MemoryModuleType.MEETING_POINT, true)), Pair.of(10, new AssignProfessionFromJobSite()), Pair.of(10, new ResetProfession()) });
    }
    
    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getWorkPackage(final VillagerProfession avw, final float float2) {
        return (ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>>)ImmutableList.of(getMinimalLookBehavior(), Pair.of((Object)5, (Object)new RunOne((java.util.List<com.mojang.datafixers.util.Pair<Behavior<? super LivingEntity>, Integer>>)ImmutableList.of((Object)Pair.of((Object)new WorkAtPoi(), (Object)7), (Object)Pair.of((Object)new StrollAroundPoi(MemoryModuleType.JOB_SITE, 4), (Object)2), (Object)Pair.of((Object)new StrollToPoi(MemoryModuleType.JOB_SITE, 1, 10), (Object)5), (Object)Pair.of((Object)new StrollToPoiList(MemoryModuleType.SECONDARY_JOB_SITE, 0.4f, 1, 6, MemoryModuleType.JOB_SITE), (Object)5), (Object)Pair.of((Object)new HarvestFarmland(), (Object)((avw == VillagerProfession.FARMER) ? 2 : 5))))), Pair.of((Object)10, (Object)new ShowTradesToPlayer(400, 1600)), Pair.of((Object)10, (Object)new SetLookAndInteract(EntityType.PLAYER, 4)), Pair.of((Object)2, (Object)new SetWalkTargetFromBlockMemory(MemoryModuleType.JOB_SITE, float2, 9, 100, 1200)), Pair.of((Object)3, (Object)new GiveGiftToHero(100)), Pair.of((Object)3, (Object)new ValidateNearbyPoi(avw.getJobPoiType(), MemoryModuleType.JOB_SITE)), Pair.of((Object)99, (Object)new UpdateActivityFromSchedule()));
    }
    
    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getPlayPackage(final float float1) {
        return (ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>>)ImmutableList.of(Pair.of((Object)0, (Object)new MoveToTargetSink(100)), getFullLookBehavior(), Pair.of((Object)5, (Object)new PlayTagWithOtherKids()), Pair.of((Object)5, (Object)new RunOne((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of((Object)MemoryModuleType.VISIBLE_VILLAGER_BABIES, (Object)MemoryStatus.VALUE_ABSENT), (java.util.List<com.mojang.datafixers.util.Pair<Behavior<? super LivingEntity>, Integer>>)ImmutableList.of((Object)Pair.of((Object)InteractWith.<LivingEntity>of(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, float1, 2), (Object)2), (Object)Pair.of((Object)InteractWith.<LivingEntity>of(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, float1, 2), (Object)1), (Object)Pair.of((Object)new VillageBoundRandomStroll(float1), (Object)1), (Object)Pair.of((Object)new SetWalkTargetFromLookTarget(float1, 2), (Object)1), (Object)Pair.of((Object)new JumpOnBed(float1), (Object)2), (Object)Pair.of((Object)new DoNothing(20, 40), (Object)2)))), Pair.of((Object)99, (Object)new UpdateActivityFromSchedule()));
    }
    
    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getRestPackage(final VillagerProfession avw, final float float2) {
        return (ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>>)ImmutableList.of(Pair.of((Object)2, (Object)new SetWalkTargetFromBlockMemory(MemoryModuleType.HOME, float2, 1, 150, 1200)), Pair.of((Object)3, (Object)new ValidateNearbyPoi(PoiType.HOME, MemoryModuleType.HOME)), Pair.of((Object)3, (Object)new SleepInBed()), Pair.of((Object)5, (Object)new RunOne((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of((Object)MemoryModuleType.HOME, (Object)MemoryStatus.VALUE_ABSENT), (java.util.List<com.mojang.datafixers.util.Pair<Behavior<? super LivingEntity>, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SetClosestHomeAsWalkTarget(float2), (Object)1), (Object)Pair.of((Object)new InsideBrownianWalk(float2), (Object)4), (Object)Pair.of((Object)new GoToClosestVillage(float2, 4), (Object)2), (Object)Pair.of((Object)new DoNothing(20, 40), (Object)2)))), getMinimalLookBehavior(), Pair.of((Object)99, (Object)new UpdateActivityFromSchedule()));
    }
    
    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getMeetPackage(final VillagerProfession avw, final float float2) {
        return (ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>>)ImmutableList.of(Pair.of((Object)2, (Object)new RunOne((java.util.List<com.mojang.datafixers.util.Pair<Behavior<? super LivingEntity>, Integer>>)ImmutableList.of((Object)Pair.of((Object)new StrollAroundPoi(MemoryModuleType.MEETING_POINT, 40), (Object)2), (Object)Pair.of((Object)new SocializeAtBell(), (Object)2)))), Pair.of((Object)10, (Object)new ShowTradesToPlayer(400, 1600)), Pair.of((Object)10, (Object)new SetLookAndInteract(EntityType.PLAYER, 4)), Pair.of((Object)2, (Object)new SetWalkTargetFromBlockMemory(MemoryModuleType.MEETING_POINT, float2, 6, 100, 200)), Pair.of((Object)3, (Object)new GiveGiftToHero(100)), Pair.of((Object)3, (Object)new ValidateNearbyPoi(PoiType.MEETING, MemoryModuleType.MEETING_POINT)), Pair.of((Object)3, (Object)new GateBehavior((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(), (Set<MemoryModuleType<?>>)ImmutableSet.of((Object)MemoryModuleType.INTERACTION_TARGET), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.RUN_ONE, (java.util.List<com.mojang.datafixers.util.Pair<Behavior<? super LivingEntity>, Integer>>)ImmutableList.of((Object)Pair.of((Object)new TradeWithVillager(), (Object)1)))), getFullLookBehavior(), Pair.of((Object)99, (Object)new UpdateActivityFromSchedule()));
    }
    
    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getIdlePackage(final VillagerProfession avw, final float float2) {
        return (ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>>)ImmutableList.of(Pair.of((Object)2, (Object)new RunOne((java.util.List<com.mojang.datafixers.util.Pair<Behavior<? super LivingEntity>, Integer>>)ImmutableList.of((Object)Pair.of((Object)InteractWith.<LivingEntity>of(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, float2, 2), (Object)2), (Object)Pair.of((Object)new InteractWith(EntityType.VILLAGER, 8, (java.util.function.Predicate<LivingEntity>)Villager::canBreed, (java.util.function.Predicate<LivingEntity>)Villager::canBreed, (MemoryModuleType<LivingEntity>)MemoryModuleType.BREED_TARGET, float2, 2), (Object)1), (Object)Pair.of((Object)InteractWith.<LivingEntity>of(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, float2, 2), (Object)1), (Object)Pair.of((Object)new VillageBoundRandomStroll(float2), (Object)1), (Object)Pair.of((Object)new SetWalkTargetFromLookTarget(float2, 2), (Object)1), (Object)Pair.of((Object)new JumpOnBed(float2), (Object)1), (Object)Pair.of((Object)new DoNothing(30, 60), (Object)1)))), Pair.of((Object)3, (Object)new GiveGiftToHero(100)), Pair.of((Object)3, (Object)new SetLookAndInteract(EntityType.PLAYER, 4)), Pair.of((Object)3, (Object)new ShowTradesToPlayer(400, 1600)), Pair.of((Object)3, (Object)new GateBehavior((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(), (Set<MemoryModuleType<?>>)ImmutableSet.of((Object)MemoryModuleType.INTERACTION_TARGET), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.RUN_ONE, (java.util.List<com.mojang.datafixers.util.Pair<Behavior<? super LivingEntity>, Integer>>)ImmutableList.of((Object)Pair.of((Object)new TradeWithVillager(), (Object)1)))), Pair.of((Object)3, (Object)new GateBehavior((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(), (Set<MemoryModuleType<?>>)ImmutableSet.of((Object)MemoryModuleType.BREED_TARGET), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.RUN_ONE, (java.util.List<com.mojang.datafixers.util.Pair<Behavior<? super LivingEntity>, Integer>>)ImmutableList.of((Object)Pair.of((Object)new MakeLove(), (Object)1)))), getFullLookBehavior(), Pair.of((Object)99, (Object)new UpdateActivityFromSchedule()));
    }
    
    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getPanicPackage(final VillagerProfession avw, final float float2) {
        final float float3 = float2 * 1.5f;
        return (ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>>)ImmutableList.of(Pair.of((Object)0, (Object)new VillagerCalmDown()), Pair.of((Object)1, (Object)new SetWalkTargetAwayFromEntity(MemoryModuleType.NEAREST_HOSTILE, float3)), Pair.of((Object)1, (Object)new SetWalkTargetAwayFromEntity(MemoryModuleType.HURT_BY_ENTITY, float3)), Pair.of((Object)3, (Object)new VillageBoundRandomStroll(float3, 2, 2)), getMinimalLookBehavior());
    }
    
    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getPreRaidPackage(final VillagerProfession avw, final float float2) {
        return (ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>>)ImmutableList.of(Pair.of((Object)0, (Object)new RingBell()), Pair.of((Object)0, (Object)new RunOne((java.util.List<com.mojang.datafixers.util.Pair<Behavior<? super LivingEntity>, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SetWalkTargetFromBlockMemory(MemoryModuleType.MEETING_POINT, float2 * 1.5f, 2, 150, 200), (Object)6), (Object)Pair.of((Object)new VillageBoundRandomStroll(float2 * 1.5f), (Object)2)))), getMinimalLookBehavior(), Pair.of((Object)99, (Object)new ResetRaidStatus()));
    }
    
    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getRaidPackage(final VillagerProfession avw, final float float2) {
        return (ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>>)ImmutableList.of(Pair.of((Object)0, (Object)new RunOne((java.util.List<com.mojang.datafixers.util.Pair<Behavior<? super LivingEntity>, Integer>>)ImmutableList.of((Object)Pair.of((Object)new GoOutsideToCelebrate(float2), (Object)5), (Object)Pair.of((Object)new VictoryStroll(float2 * 1.1f), (Object)2)))), Pair.of((Object)0, (Object)new Celebrate(600, 600)), Pair.of((Object)2, (Object)new LocateHidingPlaceDuringRaid(24, float2 * 1.4f)), getMinimalLookBehavior(), Pair.of((Object)99, (Object)new ResetRaidStatus()));
    }
    
    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getHidePackage(final VillagerProfession avw, final float float2) {
        final int integer3 = 2;
        return (ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>>)ImmutableList.of(Pair.of((Object)0, (Object)new SetHiddenState(15, 2)), Pair.of((Object)1, (Object)new LocateHidingPlace(32, float2 * 1.25f, 2)), getMinimalLookBehavior());
    }
    
    private static Pair<Integer, Behavior<LivingEntity>> getFullLookBehavior() {
        return (Pair<Integer, Behavior<LivingEntity>>)Pair.of(5, new RunOne((java.util.List<com.mojang.datafixers.util.Pair<Behavior<? super LivingEntity>, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SetEntityLookTarget(EntityType.CAT, 8.0f), (Object)8), (Object)Pair.of((Object)new SetEntityLookTarget(EntityType.VILLAGER, 8.0f), (Object)2), (Object)Pair.of((Object)new SetEntityLookTarget(EntityType.PLAYER, 8.0f), (Object)2), (Object)Pair.of((Object)new SetEntityLookTarget(MobCategory.CREATURE, 8.0f), (Object)1), (Object)Pair.of((Object)new SetEntityLookTarget(MobCategory.WATER_CREATURE, 8.0f), (Object)1), (Object)Pair.of((Object)new SetEntityLookTarget(MobCategory.MONSTER, 8.0f), (Object)1), (Object)Pair.of((Object)new DoNothing(30, 60), (Object)2))));
    }
    
    private static Pair<Integer, Behavior<LivingEntity>> getMinimalLookBehavior() {
        return (Pair<Integer, Behavior<LivingEntity>>)Pair.of(5, new RunOne((java.util.List<com.mojang.datafixers.util.Pair<Behavior<? super LivingEntity>, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SetEntityLookTarget(EntityType.VILLAGER, 8.0f), (Object)2), (Object)Pair.of((Object)new SetEntityLookTarget(EntityType.PLAYER, 8.0f), (Object)2), (Object)Pair.of((Object)new DoNothing(30, 60), (Object)8))));
    }
}
