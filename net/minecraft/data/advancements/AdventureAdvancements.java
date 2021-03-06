package net.minecraft.data.advancements;

import net.minecraft.world.level.biome.Biomes;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.Registry;
import net.minecraft.advancements.critereon.EntityEquipmentPredicate;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.advancements.critereon.KilledByCrossbowTrigger;
import net.minecraft.advancements.critereon.ShotCrossbowTrigger;
import net.minecraft.advancements.critereon.UsedTotemTrigger;
import net.minecraft.advancements.critereon.DistancePredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SummonedEntityTrigger;
import net.minecraft.advancements.critereon.ChanneledLightningTrigger;
import net.minecraft.advancements.critereon.PlayerHurtEntityTrigger;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.DamagePredicate;
import net.minecraft.advancements.critereon.TradeTrigger;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.LocationTrigger;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ItemLike;
import net.minecraft.advancements.FrameType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.advancements.Advancement;
import java.util.function.Consumer;

public class AdventureAdvancements implements Consumer<Consumer<Advancement>> {
    private static final Biome[] EXPLORABLE_BIOMES;
    private static final EntityType<?>[] MOBS_TO_KILL;
    
    public void accept(final Consumer<Advancement> consumer) {
        final Advancement q3 = Advancement.Builder.advancement().display(Items.MAP, new TranslatableComponent("advancements.adventure.root.title", new Object[0]), new TranslatableComponent("advancements.adventure.root.description", new Object[0]), new ResourceLocation("textures/gui/advancements/backgrounds/adventure.png"), FrameType.TASK, false, false, false).requirements(RequirementsStrategy.OR).addCriterion("killed_something", (CriterionTriggerInstance)KilledTrigger.TriggerInstance.playerKilledEntity()).addCriterion("killed_by_something", (CriterionTriggerInstance)KilledTrigger.TriggerInstance.entityKilledPlayer()).save(consumer, "adventure/root");
        final Advancement q4 = Advancement.Builder.advancement().parent(q3).display(Blocks.RED_BED, new TranslatableComponent("advancements.adventure.sleep_in_bed.title", new Object[0]), new TranslatableComponent("advancements.adventure.sleep_in_bed.description", new Object[0]), null, FrameType.TASK, true, true, false).addCriterion("slept_in_bed", (CriterionTriggerInstance)LocationTrigger.TriggerInstance.sleptInBed()).save(consumer, "adventure/sleep_in_bed");
        final Advancement q5 = this.addBiomes(Advancement.Builder.advancement()).parent(q4).display(Items.DIAMOND_BOOTS, new TranslatableComponent("advancements.adventure.adventuring_time.title", new Object[0]), new TranslatableComponent("advancements.adventure.adventuring_time.description", new Object[0]), null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(500)).save(consumer, "adventure/adventuring_time");
        final Advancement q6 = Advancement.Builder.advancement().parent(q3).display(Items.EMERALD, new TranslatableComponent("advancements.adventure.trade.title", new Object[0]), new TranslatableComponent("advancements.adventure.trade.description", new Object[0]), null, FrameType.TASK, true, true, false).addCriterion("traded", (CriterionTriggerInstance)TradeTrigger.TriggerInstance.tradedWithVillager()).save(consumer, "adventure/trade");
        final Advancement q7 = this.addMobsToKill(Advancement.Builder.advancement()).parent(q3).display(Items.IRON_SWORD, new TranslatableComponent("advancements.adventure.kill_a_mob.title", new Object[0]), new TranslatableComponent("advancements.adventure.kill_a_mob.description", new Object[0]), null, FrameType.TASK, true, true, false).requirements(RequirementsStrategy.OR).save(consumer, "adventure/kill_a_mob");
        final Advancement q8 = this.addMobsToKill(Advancement.Builder.advancement()).parent(q7).display(Items.DIAMOND_SWORD, new TranslatableComponent("advancements.adventure.kill_all_mobs.title", new Object[0]), new TranslatableComponent("advancements.adventure.kill_all_mobs.description", new Object[0]), null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).save(consumer, "adventure/kill_all_mobs");
        final Advancement q9 = Advancement.Builder.advancement().parent(q7).display(Items.BOW, new TranslatableComponent("advancements.adventure.shoot_arrow.title", new Object[0]), new TranslatableComponent("advancements.adventure.shoot_arrow.description", new Object[0]), null, FrameType.TASK, true, true, false).addCriterion("shot_arrow", (CriterionTriggerInstance)PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntity(DamagePredicate.Builder.damageInstance().type(DamageSourcePredicate.Builder.damageType().isProjectile(true).direct(EntityPredicate.Builder.entity().of(EntityType.ARROW))))).save(consumer, "adventure/shoot_arrow");
        final Advancement q10 = Advancement.Builder.advancement().parent(q7).display(Items.TRIDENT, new TranslatableComponent("advancements.adventure.throw_trident.title", new Object[0]), new TranslatableComponent("advancements.adventure.throw_trident.description", new Object[0]), null, FrameType.TASK, true, true, false).addCriterion("shot_trident", (CriterionTriggerInstance)PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntity(DamagePredicate.Builder.damageInstance().type(DamageSourcePredicate.Builder.damageType().isProjectile(true).direct(EntityPredicate.Builder.entity().of(EntityType.TRIDENT))))).save(consumer, "adventure/throw_trident");
        final Advancement q11 = Advancement.Builder.advancement().parent(q10).display(Items.TRIDENT, new TranslatableComponent("advancements.adventure.very_very_frightening.title", new Object[0]), new TranslatableComponent("advancements.adventure.very_very_frightening.description", new Object[0]), null, FrameType.TASK, true, true, false).addCriterion("struck_villager", (CriterionTriggerInstance)ChanneledLightningTrigger.TriggerInstance.channeledLightning(EntityPredicate.Builder.entity().of(EntityType.VILLAGER).build())).save(consumer, "adventure/very_very_frightening");
        final Advancement q12 = Advancement.Builder.advancement().parent(q6).display(Blocks.CARVED_PUMPKIN, new TranslatableComponent("advancements.adventure.summon_iron_golem.title", new Object[0]), new TranslatableComponent("advancements.adventure.summon_iron_golem.description", new Object[0]), null, FrameType.GOAL, true, true, false).addCriterion("summoned_golem", (CriterionTriggerInstance)SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(EntityType.IRON_GOLEM))).save(consumer, "adventure/summon_iron_golem");
        final Advancement q13 = Advancement.Builder.advancement().parent(q9).display(Items.ARROW, new TranslatableComponent("advancements.adventure.sniper_duel.title", new Object[0]), new TranslatableComponent("advancements.adventure.sniper_duel.description", new Object[0]), null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).addCriterion("killed_skeleton", (CriterionTriggerInstance)KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(EntityType.SKELETON).distance(DistancePredicate.horizontal(MinMaxBounds.Floats.atLeast(50.0f))), DamageSourcePredicate.Builder.damageType().isProjectile(true))).save(consumer, "adventure/sniper_duel");
        final Advancement q14 = Advancement.Builder.advancement().parent(q7).display(Items.TOTEM_OF_UNDYING, new TranslatableComponent("advancements.adventure.totem_of_undying.title", new Object[0]), new TranslatableComponent("advancements.adventure.totem_of_undying.description", new Object[0]), null, FrameType.GOAL, true, true, false).addCriterion("used_totem", (CriterionTriggerInstance)UsedTotemTrigger.TriggerInstance.usedTotem(Items.TOTEM_OF_UNDYING)).save(consumer, "adventure/totem_of_undying");
        final Advancement q15 = Advancement.Builder.advancement().parent(q3).display(Items.CROSSBOW, new TranslatableComponent("advancements.adventure.ol_betsy.title", new Object[0]), new TranslatableComponent("advancements.adventure.ol_betsy.description", new Object[0]), null, FrameType.TASK, true, true, false).addCriterion("shot_crossbow", (CriterionTriggerInstance)ShotCrossbowTrigger.TriggerInstance.shotCrossbow(Items.CROSSBOW)).save(consumer, "adventure/ol_betsy");
        final Advancement q16 = Advancement.Builder.advancement().parent(q15).display(Items.CROSSBOW, new TranslatableComponent("advancements.adventure.whos_the_pillager_now.title", new Object[0]), new TranslatableComponent("advancements.adventure.whos_the_pillager_now.description", new Object[0]), null, FrameType.TASK, true, true, false).addCriterion("kill_pillager", (CriterionTriggerInstance)KilledByCrossbowTrigger.TriggerInstance.crossbowKilled(EntityPredicate.Builder.entity().of(EntityType.PILLAGER))).save(consumer, "adventure/whos_the_pillager_now");
        final Advancement q17 = Advancement.Builder.advancement().parent(q15).display(Items.CROSSBOW, new TranslatableComponent("advancements.adventure.two_birds_one_arrow.title", new Object[0]), new TranslatableComponent("advancements.adventure.two_birds_one_arrow.description", new Object[0]), null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(65)).addCriterion("two_birds", (CriterionTriggerInstance)KilledByCrossbowTrigger.TriggerInstance.crossbowKilled(EntityPredicate.Builder.entity().of(EntityType.PHANTOM), EntityPredicate.Builder.entity().of(EntityType.PHANTOM))).save(consumer, "adventure/two_birds_one_arrow");
        final Advancement q18 = Advancement.Builder.advancement().parent(q15).display(Items.CROSSBOW, new TranslatableComponent("advancements.adventure.arbalistic.title", new Object[0]), new TranslatableComponent("advancements.adventure.arbalistic.description", new Object[0]), null, FrameType.CHALLENGE, true, true, true).rewards(AdvancementRewards.Builder.experience(85)).addCriterion("arbalistic", (CriterionTriggerInstance)KilledByCrossbowTrigger.TriggerInstance.crossbowKilled(MinMaxBounds.Ints.exactly(5))).save(consumer, "adventure/arbalistic");
        final Advancement q19 = Advancement.Builder.advancement().parent(q3).display(Raid.getLeaderBannerInstance(), new TranslatableComponent("advancements.adventure.voluntary_exile.title", new Object[0]), new TranslatableComponent("advancements.adventure.voluntary_exile.description", new Object[0]), null, FrameType.TASK, true, true, true).addCriterion("voluntary_exile", (CriterionTriggerInstance)KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(EntityTypeTags.RAIDERS).equipment(EntityEquipmentPredicate.CAPTAIN))).save(consumer, "adventure/voluntary_exile");
        final Advancement q20 = Advancement.Builder.advancement().parent(q19).display(Raid.getLeaderBannerInstance(), new TranslatableComponent("advancements.adventure.hero_of_the_village.title", new Object[0]), new TranslatableComponent("advancements.adventure.hero_of_the_village.description", new Object[0]), null, FrameType.CHALLENGE, true, true, true).rewards(AdvancementRewards.Builder.experience(100)).addCriterion("hero_of_the_village", (CriterionTriggerInstance)LocationTrigger.TriggerInstance.raidWon()).save(consumer, "adventure/hero_of_the_village");
    }
    
    private Advancement.Builder addMobsToKill(final Advancement.Builder a) {
        for (final EntityType<?> ais6 : AdventureAdvancements.MOBS_TO_KILL) {
            a.addCriterion(Registry.ENTITY_TYPE.getKey(ais6).toString(), KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(ais6)));
        }
        return a;
    }
    
    private Advancement.Builder addBiomes(final Advancement.Builder a) {
        for (final Biome bio6 : AdventureAdvancements.EXPLORABLE_BIOMES) {
            a.addCriterion(Registry.BIOME.getKey(bio6).toString(), LocationTrigger.TriggerInstance.located(LocationPredicate.inBiome(bio6)));
        }
        return a;
    }
    
    static {
        EXPLORABLE_BIOMES = new Biome[] { Biomes.BIRCH_FOREST_HILLS, Biomes.RIVER, Biomes.SWAMP, Biomes.DESERT, Biomes.WOODED_HILLS, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.SNOWY_TAIGA, Biomes.BADLANDS, Biomes.FOREST, Biomes.STONE_SHORE, Biomes.SNOWY_TUNDRA, Biomes.TAIGA_HILLS, Biomes.SNOWY_MOUNTAINS, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.SAVANNA, Biomes.PLAINS, Biomes.FROZEN_RIVER, Biomes.GIANT_TREE_TAIGA, Biomes.SNOWY_BEACH, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.MUSHROOM_FIELD_SHORE, Biomes.MOUNTAINS, Biomes.DESERT_HILLS, Biomes.JUNGLE, Biomes.BEACH, Biomes.SAVANNA_PLATEAU, Biomes.SNOWY_TAIGA_HILLS, Biomes.BADLANDS_PLATEAU, Biomes.DARK_FOREST, Biomes.TAIGA, Biomes.BIRCH_FOREST, Biomes.MUSHROOM_FIELDS, Biomes.WOODED_MOUNTAINS, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.BAMBOO_JUNGLE, Biomes.BAMBOO_JUNGLE_HILLS };
        MOBS_TO_KILL = new EntityType[] { EntityType.CAVE_SPIDER, EntityType.SPIDER, EntityType.ZOMBIE_PIGMAN, EntityType.ENDERMAN, EntityType.BLAZE, EntityType.CREEPER, EntityType.EVOKER, EntityType.GHAST, EntityType.GUARDIAN, EntityType.HUSK, EntityType.MAGMA_CUBE, EntityType.SHULKER, EntityType.SILVERFISH, EntityType.SKELETON, EntityType.SLIME, EntityType.STRAY, EntityType.VINDICATOR, EntityType.WITCH, EntityType.WITHER_SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.PHANTOM, EntityType.DROWNED, EntityType.PILLAGER, EntityType.RAVAGER };
    }
}
