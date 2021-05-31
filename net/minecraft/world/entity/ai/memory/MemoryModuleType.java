package net.minecraft.world.entity.ai.memory;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Serializable;
import net.minecraft.core.Registry;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import java.util.Optional;
import net.minecraft.core.SerializableLong;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import java.util.Set;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.ai.behavior.PositionWrapper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import java.util.List;
import net.minecraft.core.GlobalPos;

public class MemoryModuleType<U> {
    public static final MemoryModuleType<Void> DUMMY;
    public static final MemoryModuleType<GlobalPos> HOME;
    public static final MemoryModuleType<GlobalPos> JOB_SITE;
    public static final MemoryModuleType<GlobalPos> MEETING_POINT;
    public static final MemoryModuleType<List<GlobalPos>> SECONDARY_JOB_SITE;
    public static final MemoryModuleType<List<LivingEntity>> LIVING_ENTITIES;
    public static final MemoryModuleType<List<LivingEntity>> VISIBLE_LIVING_ENTITIES;
    public static final MemoryModuleType<List<LivingEntity>> VISIBLE_VILLAGER_BABIES;
    public static final MemoryModuleType<List<Player>> NEAREST_PLAYERS;
    public static final MemoryModuleType<Player> NEAREST_VISIBLE_PLAYER;
    public static final MemoryModuleType<WalkTarget> WALK_TARGET;
    public static final MemoryModuleType<PositionWrapper> LOOK_TARGET;
    public static final MemoryModuleType<LivingEntity> INTERACTION_TARGET;
    public static final MemoryModuleType<Villager> BREED_TARGET;
    public static final MemoryModuleType<Path> PATH;
    public static final MemoryModuleType<List<GlobalPos>> INTERACTABLE_DOORS;
    public static final MemoryModuleType<Set<GlobalPos>> OPENED_DOORS;
    public static final MemoryModuleType<BlockPos> NEAREST_BED;
    public static final MemoryModuleType<DamageSource> HURT_BY;
    public static final MemoryModuleType<LivingEntity> HURT_BY_ENTITY;
    public static final MemoryModuleType<LivingEntity> NEAREST_HOSTILE;
    public static final MemoryModuleType<GlobalPos> HIDING_PLACE;
    public static final MemoryModuleType<Long> HEARD_BELL_TIME;
    public static final MemoryModuleType<Long> CANT_REACH_WALK_TARGET_SINCE;
    public static final MemoryModuleType<Long> GOLEM_LAST_SEEN_TIME;
    public static final MemoryModuleType<SerializableLong> LAST_SLEPT;
    public static final MemoryModuleType<SerializableLong> LAST_WORKED_AT_POI;
    private final Optional<Function<Dynamic<?>, U>> deserializer;
    
    private MemoryModuleType(final Optional<Function<Dynamic<?>, U>> optional) {
        this.deserializer = optional;
    }
    
    public String toString() {
        return Registry.MEMORY_MODULE_TYPE.getKey(this).toString();
    }
    
    public Optional<Function<Dynamic<?>, U>> getDeserializer() {
        return this.deserializer;
    }
    
    private static <U extends Serializable> MemoryModuleType<U> register(final String string, final Optional<Function<Dynamic<?>, U>> optional) {
        return Registry.<MemoryModuleType<U>>register(Registry.MEMORY_MODULE_TYPE, new ResourceLocation(string), new MemoryModuleType<U>(optional));
    }
    
    private static <U> MemoryModuleType<U> register(final String string) {
        return Registry.<MemoryModuleType<U>>register(Registry.MEMORY_MODULE_TYPE, new ResourceLocation(string), new MemoryModuleType<U>((java.util.Optional<java.util.function.Function<Dynamic<?>, U>>)Optional.empty()));
    }
    
    static {
        DUMMY = MemoryModuleType.<Void>register("dummy");
        HOME = MemoryModuleType.<GlobalPos>register("home", (java.util.Optional<java.util.function.Function<Dynamic<?>, GlobalPos>>)Optional.of(GlobalPos::of));
        JOB_SITE = MemoryModuleType.<GlobalPos>register("job_site", (java.util.Optional<java.util.function.Function<Dynamic<?>, GlobalPos>>)Optional.of(GlobalPos::of));
        MEETING_POINT = MemoryModuleType.<GlobalPos>register("meeting_point", (java.util.Optional<java.util.function.Function<Dynamic<?>, GlobalPos>>)Optional.of(GlobalPos::of));
        SECONDARY_JOB_SITE = MemoryModuleType.<List<GlobalPos>>register("secondary_job_site");
        LIVING_ENTITIES = MemoryModuleType.<List<LivingEntity>>register("mobs");
        VISIBLE_LIVING_ENTITIES = MemoryModuleType.<List<LivingEntity>>register("visible_mobs");
        VISIBLE_VILLAGER_BABIES = MemoryModuleType.<List<LivingEntity>>register("visible_villager_babies");
        NEAREST_PLAYERS = MemoryModuleType.<List<Player>>register("nearest_players");
        NEAREST_VISIBLE_PLAYER = MemoryModuleType.<Player>register("nearest_visible_player");
        WALK_TARGET = MemoryModuleType.<WalkTarget>register("walk_target");
        LOOK_TARGET = MemoryModuleType.<PositionWrapper>register("look_target");
        INTERACTION_TARGET = MemoryModuleType.<LivingEntity>register("interaction_target");
        BREED_TARGET = MemoryModuleType.<Villager>register("breed_target");
        PATH = MemoryModuleType.<Path>register("path");
        INTERACTABLE_DOORS = MemoryModuleType.<List<GlobalPos>>register("interactable_doors");
        OPENED_DOORS = MemoryModuleType.<Set<GlobalPos>>register("opened_doors");
        NEAREST_BED = MemoryModuleType.<BlockPos>register("nearest_bed");
        HURT_BY = MemoryModuleType.<DamageSource>register("hurt_by");
        HURT_BY_ENTITY = MemoryModuleType.<LivingEntity>register("hurt_by_entity");
        NEAREST_HOSTILE = MemoryModuleType.<LivingEntity>register("nearest_hostile");
        HIDING_PLACE = MemoryModuleType.<GlobalPos>register("hiding_place");
        HEARD_BELL_TIME = MemoryModuleType.<Long>register("heard_bell_time");
        CANT_REACH_WALK_TARGET_SINCE = MemoryModuleType.<Long>register("cant_reach_walk_target_since");
        GOLEM_LAST_SEEN_TIME = MemoryModuleType.<Long>register("golem_last_seen_time");
        LAST_SLEPT = MemoryModuleType.<SerializableLong>register("last_slept", (java.util.Optional<java.util.function.Function<Dynamic<?>, SerializableLong>>)Optional.of(SerializableLong::of));
        LAST_WORKED_AT_POI = MemoryModuleType.<SerializableLong>register("last_worked_at_poi", (java.util.Optional<java.util.function.Function<Dynamic<?>, SerializableLong>>)Optional.of(SerializableLong::of));
    }
}
