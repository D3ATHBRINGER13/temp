package net.minecraft.data.advancements;

import net.minecraft.advancements.critereon.EffectsChangedTrigger;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.advancements.critereon.BrewedPotionTrigger;
import net.minecraft.advancements.critereon.ConstructBeaconTrigger;
import net.minecraft.advancements.critereon.SummonedEntityTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.NetherTravelTrigger;
import net.minecraft.advancements.critereon.DistancePredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.LocationTrigger;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.world.entity.EntityType;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.world.item.Items;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.ChangeDimensionTrigger;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ItemLike;
import net.minecraft.advancements.FrameType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.advancements.Advancement;
import java.util.function.Consumer;

public class NetherAdvancements implements Consumer<Consumer<Advancement>> {
    public void accept(final Consumer<Advancement> consumer) {
        final Advancement q3 = Advancement.Builder.advancement().display(Blocks.RED_NETHER_BRICKS, new TranslatableComponent("advancements.nether.root.title", new Object[0]), new TranslatableComponent("advancements.nether.root.description", new Object[0]), new ResourceLocation("textures/gui/advancements/backgrounds/nether.png"), FrameType.TASK, false, false, false).addCriterion("entered_nether", (CriterionTriggerInstance)ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(DimensionType.NETHER)).save(consumer, "nether/root");
        final Advancement q4 = Advancement.Builder.advancement().parent(q3).display(Items.FIRE_CHARGE, new TranslatableComponent("advancements.nether.return_to_sender.title", new Object[0]), new TranslatableComponent("advancements.nether.return_to_sender.description", new Object[0]), null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).addCriterion("killed_ghast", (CriterionTriggerInstance)KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(EntityType.GHAST), DamageSourcePredicate.Builder.damageType().isProjectile(true).direct(EntityPredicate.Builder.entity().of(EntityType.FIREBALL)))).save(consumer, "nether/return_to_sender");
        final Advancement q5 = Advancement.Builder.advancement().parent(q3).display(Blocks.NETHER_BRICKS, new TranslatableComponent("advancements.nether.find_fortress.title", new Object[0]), new TranslatableComponent("advancements.nether.find_fortress.description", new Object[0]), null, FrameType.TASK, true, true, false).addCriterion("fortress", (CriterionTriggerInstance)LocationTrigger.TriggerInstance.located(LocationPredicate.inFeature(Feature.NETHER_BRIDGE))).save(consumer, "nether/find_fortress");
        final Advancement q6 = Advancement.Builder.advancement().parent(q3).display(Items.MAP, new TranslatableComponent("advancements.nether.fast_travel.title", new Object[0]), new TranslatableComponent("advancements.nether.fast_travel.description", new Object[0]), null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).addCriterion("travelled", (CriterionTriggerInstance)NetherTravelTrigger.TriggerInstance.travelledThroughNether(DistancePredicate.horizontal(MinMaxBounds.Floats.atLeast(7000.0f)))).save(consumer, "nether/fast_travel");
        final Advancement q7 = Advancement.Builder.advancement().parent(q4).display(Items.GHAST_TEAR, new TranslatableComponent("advancements.nether.uneasy_alliance.title", new Object[0]), new TranslatableComponent("advancements.nether.uneasy_alliance.description", new Object[0]), null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).addCriterion("killed_ghast", (CriterionTriggerInstance)KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(EntityType.GHAST).located(LocationPredicate.inDimension(DimensionType.OVERWORLD)))).save(consumer, "nether/uneasy_alliance");
        final Advancement q8 = Advancement.Builder.advancement().parent(q5).display(Blocks.WITHER_SKELETON_SKULL, new TranslatableComponent("advancements.nether.get_wither_skull.title", new Object[0]), new TranslatableComponent("advancements.nether.get_wither_skull.description", new Object[0]), null, FrameType.TASK, true, true, false).addCriterion("wither_skull", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItem(Blocks.WITHER_SKELETON_SKULL)).save(consumer, "nether/get_wither_skull");
        final Advancement q9 = Advancement.Builder.advancement().parent(q8).display(Items.NETHER_STAR, new TranslatableComponent("advancements.nether.summon_wither.title", new Object[0]), new TranslatableComponent("advancements.nether.summon_wither.description", new Object[0]), null, FrameType.TASK, true, true, false).addCriterion("summoned", (CriterionTriggerInstance)SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(EntityType.WITHER))).save(consumer, "nether/summon_wither");
        final Advancement q10 = Advancement.Builder.advancement().parent(q5).display(Items.BLAZE_ROD, new TranslatableComponent("advancements.nether.obtain_blaze_rod.title", new Object[0]), new TranslatableComponent("advancements.nether.obtain_blaze_rod.description", new Object[0]), null, FrameType.TASK, true, true, false).addCriterion("blaze_rod", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItem(Items.BLAZE_ROD)).save(consumer, "nether/obtain_blaze_rod");
        final Advancement q11 = Advancement.Builder.advancement().parent(q9).display(Blocks.BEACON, new TranslatableComponent("advancements.nether.create_beacon.title", new Object[0]), new TranslatableComponent("advancements.nether.create_beacon.description", new Object[0]), null, FrameType.TASK, true, true, false).addCriterion("beacon", (CriterionTriggerInstance)ConstructBeaconTrigger.TriggerInstance.constructedBeacon(MinMaxBounds.Ints.atLeast(1))).save(consumer, "nether/create_beacon");
        final Advancement q12 = Advancement.Builder.advancement().parent(q11).display(Blocks.BEACON, new TranslatableComponent("advancements.nether.create_full_beacon.title", new Object[0]), new TranslatableComponent("advancements.nether.create_full_beacon.description", new Object[0]), null, FrameType.GOAL, true, true, false).addCriterion("beacon", (CriterionTriggerInstance)ConstructBeaconTrigger.TriggerInstance.constructedBeacon(MinMaxBounds.Ints.exactly(4))).save(consumer, "nether/create_full_beacon");
        final Advancement q13 = Advancement.Builder.advancement().parent(q10).display(Items.POTION, new TranslatableComponent("advancements.nether.brew_potion.title", new Object[0]), new TranslatableComponent("advancements.nether.brew_potion.description", new Object[0]), null, FrameType.TASK, true, true, false).addCriterion("potion", (CriterionTriggerInstance)BrewedPotionTrigger.TriggerInstance.brewedPotion()).save(consumer, "nether/brew_potion");
        final Advancement q14 = Advancement.Builder.advancement().parent(q13).display(Items.MILK_BUCKET, new TranslatableComponent("advancements.nether.all_potions.title", new Object[0]), new TranslatableComponent("advancements.nether.all_potions.description", new Object[0]), null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).addCriterion("all_effects", (CriterionTriggerInstance)EffectsChangedTrigger.TriggerInstance.hasEffects(MobEffectsPredicate.effects().and(MobEffects.MOVEMENT_SPEED).and(MobEffects.MOVEMENT_SLOWDOWN).and(MobEffects.DAMAGE_BOOST).and(MobEffects.JUMP).and(MobEffects.REGENERATION).and(MobEffects.FIRE_RESISTANCE).and(MobEffects.WATER_BREATHING).and(MobEffects.INVISIBILITY).and(MobEffects.NIGHT_VISION).and(MobEffects.WEAKNESS).and(MobEffects.POISON).and(MobEffects.SLOW_FALLING).and(MobEffects.DAMAGE_RESISTANCE))).save(consumer, "nether/all_potions");
        final Advancement q15 = Advancement.Builder.advancement().parent(q14).display(Items.BUCKET, new TranslatableComponent("advancements.nether.all_effects.title", new Object[0]), new TranslatableComponent("advancements.nether.all_effects.description", new Object[0]), null, FrameType.CHALLENGE, true, true, true).rewards(AdvancementRewards.Builder.experience(1000)).addCriterion("all_effects", (CriterionTriggerInstance)EffectsChangedTrigger.TriggerInstance.hasEffects(MobEffectsPredicate.effects().and(MobEffects.MOVEMENT_SPEED).and(MobEffects.MOVEMENT_SLOWDOWN).and(MobEffects.DAMAGE_BOOST).and(MobEffects.JUMP).and(MobEffects.REGENERATION).and(MobEffects.FIRE_RESISTANCE).and(MobEffects.WATER_BREATHING).and(MobEffects.INVISIBILITY).and(MobEffects.NIGHT_VISION).and(MobEffects.WEAKNESS).and(MobEffects.POISON).and(MobEffects.WITHER).and(MobEffects.DIG_SPEED).and(MobEffects.DIG_SLOWDOWN).and(MobEffects.LEVITATION).and(MobEffects.GLOWING).and(MobEffects.ABSORPTION).and(MobEffects.HUNGER).and(MobEffects.CONFUSION).and(MobEffects.DAMAGE_RESISTANCE).and(MobEffects.SLOW_FALLING).and(MobEffects.CONDUIT_POWER).and(MobEffects.DOLPHINS_GRACE).and(MobEffects.BLINDNESS).and(MobEffects.BAD_OMEN).and(MobEffects.HERO_OF_THE_VILLAGE))).save(consumer, "nether/all_effects");
    }
}
