package net.minecraft.advancements;

import com.google.common.collect.Maps;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.KilledByCrossbowTrigger;
import net.minecraft.advancements.critereon.ShotCrossbowTrigger;
import net.minecraft.advancements.critereon.ChanneledLightningTrigger;
import net.minecraft.advancements.critereon.FishingRodHookedTrigger;
import net.minecraft.advancements.critereon.NetherTravelTrigger;
import net.minecraft.advancements.critereon.UsedTotemTrigger;
import net.minecraft.advancements.critereon.EffectsChangedTrigger;
import net.minecraft.advancements.critereon.ConsumeItemTrigger;
import net.minecraft.advancements.critereon.PlacedBlockTrigger;
import net.minecraft.advancements.critereon.TameAnimalTrigger;
import net.minecraft.advancements.critereon.TickTrigger;
import net.minecraft.advancements.critereon.ChangeDimensionTrigger;
import net.minecraft.advancements.critereon.LevitationTrigger;
import net.minecraft.advancements.critereon.ItemDurabilityTrigger;
import net.minecraft.advancements.critereon.TradeTrigger;
import net.minecraft.advancements.critereon.CuredZombieVillagerTrigger;
import net.minecraft.advancements.critereon.LocationTrigger;
import net.minecraft.advancements.critereon.BredAnimalsTrigger;
import net.minecraft.advancements.critereon.SummonedEntityTrigger;
import net.minecraft.advancements.critereon.UsedEnderEyeTrigger;
import net.minecraft.advancements.critereon.ConstructBeaconTrigger;
import net.minecraft.advancements.critereon.BrewedPotionTrigger;
import net.minecraft.advancements.critereon.FilledBucketTrigger;
import net.minecraft.advancements.critereon.EnchantedItemTrigger;
import net.minecraft.advancements.critereon.EntityHurtPlayerTrigger;
import net.minecraft.advancements.critereon.PlayerHurtEntityTrigger;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;

public class CriteriaTriggers {
    private static final Map<ResourceLocation, CriterionTrigger<?>> CRITERIA;
    public static final ImpossibleTrigger IMPOSSIBLE;
    public static final KilledTrigger PLAYER_KILLED_ENTITY;
    public static final KilledTrigger ENTITY_KILLED_PLAYER;
    public static final EnterBlockTrigger ENTER_BLOCK;
    public static final InventoryChangeTrigger INVENTORY_CHANGED;
    public static final RecipeUnlockedTrigger RECIPE_UNLOCKED;
    public static final PlayerHurtEntityTrigger PLAYER_HURT_ENTITY;
    public static final EntityHurtPlayerTrigger ENTITY_HURT_PLAYER;
    public static final EnchantedItemTrigger ENCHANTED_ITEM;
    public static final FilledBucketTrigger FILLED_BUCKET;
    public static final BrewedPotionTrigger BREWED_POTION;
    public static final ConstructBeaconTrigger CONSTRUCT_BEACON;
    public static final UsedEnderEyeTrigger USED_ENDER_EYE;
    public static final SummonedEntityTrigger SUMMONED_ENTITY;
    public static final BredAnimalsTrigger BRED_ANIMALS;
    public static final LocationTrigger LOCATION;
    public static final LocationTrigger SLEPT_IN_BED;
    public static final CuredZombieVillagerTrigger CURED_ZOMBIE_VILLAGER;
    public static final TradeTrigger TRADE;
    public static final ItemDurabilityTrigger ITEM_DURABILITY_CHANGED;
    public static final LevitationTrigger LEVITATION;
    public static final ChangeDimensionTrigger CHANGED_DIMENSION;
    public static final TickTrigger TICK;
    public static final TameAnimalTrigger TAME_ANIMAL;
    public static final PlacedBlockTrigger PLACED_BLOCK;
    public static final ConsumeItemTrigger CONSUME_ITEM;
    public static final EffectsChangedTrigger EFFECTS_CHANGED;
    public static final UsedTotemTrigger USED_TOTEM;
    public static final NetherTravelTrigger NETHER_TRAVEL;
    public static final FishingRodHookedTrigger FISHING_ROD_HOOKED;
    public static final ChanneledLightningTrigger CHANNELED_LIGHTNING;
    public static final ShotCrossbowTrigger SHOT_CROSSBOW;
    public static final KilledByCrossbowTrigger KILLED_BY_CROSSBOW;
    public static final LocationTrigger RAID_WIN;
    public static final LocationTrigger BAD_OMEN;
    
    private static <T extends CriterionTrigger<?>> T register(final T x) {
        if (CriteriaTriggers.CRITERIA.containsKey(x.getId())) {
            throw new IllegalArgumentException(new StringBuilder().append("Duplicate criterion id ").append(x.getId()).toString());
        }
        CriteriaTriggers.CRITERIA.put(x.getId(), x);
        return x;
    }
    
    @Nullable
    public static <T extends CriterionTriggerInstance> CriterionTrigger<T> getCriterion(final ResourceLocation qv) {
        return (CriterionTrigger<T>)CriteriaTriggers.CRITERIA.get(qv);
    }
    
    public static Iterable<? extends CriterionTrigger<?>> all() {
        return CriteriaTriggers.CRITERIA.values();
    }
    
    static {
        CRITERIA = (Map)Maps.newHashMap();
        IMPOSSIBLE = CriteriaTriggers.<ImpossibleTrigger>register(new ImpossibleTrigger());
        PLAYER_KILLED_ENTITY = CriteriaTriggers.<KilledTrigger>register(new KilledTrigger(new ResourceLocation("player_killed_entity")));
        ENTITY_KILLED_PLAYER = CriteriaTriggers.<KilledTrigger>register(new KilledTrigger(new ResourceLocation("entity_killed_player")));
        ENTER_BLOCK = CriteriaTriggers.<EnterBlockTrigger>register(new EnterBlockTrigger());
        INVENTORY_CHANGED = CriteriaTriggers.<InventoryChangeTrigger>register(new InventoryChangeTrigger());
        RECIPE_UNLOCKED = CriteriaTriggers.<RecipeUnlockedTrigger>register(new RecipeUnlockedTrigger());
        PLAYER_HURT_ENTITY = CriteriaTriggers.<PlayerHurtEntityTrigger>register(new PlayerHurtEntityTrigger());
        ENTITY_HURT_PLAYER = CriteriaTriggers.<EntityHurtPlayerTrigger>register(new EntityHurtPlayerTrigger());
        ENCHANTED_ITEM = CriteriaTriggers.<EnchantedItemTrigger>register(new EnchantedItemTrigger());
        FILLED_BUCKET = CriteriaTriggers.<FilledBucketTrigger>register(new FilledBucketTrigger());
        BREWED_POTION = CriteriaTriggers.<BrewedPotionTrigger>register(new BrewedPotionTrigger());
        CONSTRUCT_BEACON = CriteriaTriggers.<ConstructBeaconTrigger>register(new ConstructBeaconTrigger());
        USED_ENDER_EYE = CriteriaTriggers.<UsedEnderEyeTrigger>register(new UsedEnderEyeTrigger());
        SUMMONED_ENTITY = CriteriaTriggers.<SummonedEntityTrigger>register(new SummonedEntityTrigger());
        BRED_ANIMALS = CriteriaTriggers.<BredAnimalsTrigger>register(new BredAnimalsTrigger());
        LOCATION = CriteriaTriggers.<LocationTrigger>register(new LocationTrigger(new ResourceLocation("location")));
        SLEPT_IN_BED = CriteriaTriggers.<LocationTrigger>register(new LocationTrigger(new ResourceLocation("slept_in_bed")));
        CURED_ZOMBIE_VILLAGER = CriteriaTriggers.<CuredZombieVillagerTrigger>register(new CuredZombieVillagerTrigger());
        TRADE = CriteriaTriggers.<TradeTrigger>register(new TradeTrigger());
        ITEM_DURABILITY_CHANGED = CriteriaTriggers.<ItemDurabilityTrigger>register(new ItemDurabilityTrigger());
        LEVITATION = CriteriaTriggers.<LevitationTrigger>register(new LevitationTrigger());
        CHANGED_DIMENSION = CriteriaTriggers.<ChangeDimensionTrigger>register(new ChangeDimensionTrigger());
        TICK = CriteriaTriggers.<TickTrigger>register(new TickTrigger());
        TAME_ANIMAL = CriteriaTriggers.<TameAnimalTrigger>register(new TameAnimalTrigger());
        PLACED_BLOCK = CriteriaTriggers.<PlacedBlockTrigger>register(new PlacedBlockTrigger());
        CONSUME_ITEM = CriteriaTriggers.<ConsumeItemTrigger>register(new ConsumeItemTrigger());
        EFFECTS_CHANGED = CriteriaTriggers.<EffectsChangedTrigger>register(new EffectsChangedTrigger());
        USED_TOTEM = CriteriaTriggers.<UsedTotemTrigger>register(new UsedTotemTrigger());
        NETHER_TRAVEL = CriteriaTriggers.<NetherTravelTrigger>register(new NetherTravelTrigger());
        FISHING_ROD_HOOKED = CriteriaTriggers.<FishingRodHookedTrigger>register(new FishingRodHookedTrigger());
        CHANNELED_LIGHTNING = CriteriaTriggers.<ChanneledLightningTrigger>register(new ChanneledLightningTrigger());
        SHOT_CROSSBOW = CriteriaTriggers.<ShotCrossbowTrigger>register(new ShotCrossbowTrigger());
        KILLED_BY_CROSSBOW = CriteriaTriggers.<KilledByCrossbowTrigger>register(new KilledByCrossbowTrigger());
        RAID_WIN = CriteriaTriggers.<LocationTrigger>register(new LocationTrigger(new ResourceLocation("hero_of_the_village")));
        BAD_OMEN = CriteriaTriggers.<LocationTrigger>register(new LocationTrigger(new ResourceLocation("voluntary_exile")));
    }
}
