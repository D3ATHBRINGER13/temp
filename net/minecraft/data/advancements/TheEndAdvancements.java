package net.minecraft.data.advancements;

import net.minecraft.advancements.critereon.LevitationTrigger;
import net.minecraft.advancements.critereon.DistancePredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.LocationTrigger;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.advancements.critereon.SummonedEntityTrigger;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.world.item.Items;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.world.entity.EntityType;
import net.minecraft.advancements.critereon.EntityPredicate;
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

public class TheEndAdvancements implements Consumer<Consumer<Advancement>> {
    public void accept(final Consumer<Advancement> consumer) {
        final Advancement q3 = Advancement.Builder.advancement().display(Blocks.END_STONE, new TranslatableComponent("advancements.end.root.title", new Object[0]), new TranslatableComponent("advancements.end.root.description", new Object[0]), new ResourceLocation("textures/gui/advancements/backgrounds/end.png"), FrameType.TASK, false, false, false).addCriterion("entered_end", (CriterionTriggerInstance)ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(DimensionType.THE_END)).save(consumer, "end/root");
        final Advancement q4 = Advancement.Builder.advancement().parent(q3).display(Blocks.DRAGON_HEAD, new TranslatableComponent("advancements.end.kill_dragon.title", new Object[0]), new TranslatableComponent("advancements.end.kill_dragon.description", new Object[0]), null, FrameType.TASK, true, true, false).addCriterion("killed_dragon", (CriterionTriggerInstance)KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(EntityType.ENDER_DRAGON))).save(consumer, "end/kill_dragon");
        final Advancement q5 = Advancement.Builder.advancement().parent(q4).display(Items.ENDER_PEARL, new TranslatableComponent("advancements.end.enter_end_gateway.title", new Object[0]), new TranslatableComponent("advancements.end.enter_end_gateway.description", new Object[0]), null, FrameType.TASK, true, true, false).addCriterion("entered_end_gateway", (CriterionTriggerInstance)EnterBlockTrigger.TriggerInstance.entersBlock(Blocks.END_GATEWAY)).save(consumer, "end/enter_end_gateway");
        final Advancement q6 = Advancement.Builder.advancement().parent(q4).display(Items.END_CRYSTAL, new TranslatableComponent("advancements.end.respawn_dragon.title", new Object[0]), new TranslatableComponent("advancements.end.respawn_dragon.description", new Object[0]), null, FrameType.GOAL, true, true, false).addCriterion("summoned_dragon", (CriterionTriggerInstance)SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(EntityType.ENDER_DRAGON))).save(consumer, "end/respawn_dragon");
        final Advancement q7 = Advancement.Builder.advancement().parent(q5).display(Blocks.PURPUR_BLOCK, new TranslatableComponent("advancements.end.find_end_city.title", new Object[0]), new TranslatableComponent("advancements.end.find_end_city.description", new Object[0]), null, FrameType.TASK, true, true, false).addCriterion("in_city", (CriterionTriggerInstance)LocationTrigger.TriggerInstance.located(LocationPredicate.inFeature(Feature.END_CITY))).save(consumer, "end/find_end_city");
        final Advancement q8 = Advancement.Builder.advancement().parent(q4).display(Items.DRAGON_BREATH, new TranslatableComponent("advancements.end.dragon_breath.title", new Object[0]), new TranslatableComponent("advancements.end.dragon_breath.description", new Object[0]), null, FrameType.GOAL, true, true, false).addCriterion("dragon_breath", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItem(Items.DRAGON_BREATH)).save(consumer, "end/dragon_breath");
        final Advancement q9 = Advancement.Builder.advancement().parent(q7).display(Items.SHULKER_SHELL, new TranslatableComponent("advancements.end.levitate.title", new Object[0]), new TranslatableComponent("advancements.end.levitate.description", new Object[0]), null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).addCriterion("levitated", (CriterionTriggerInstance)LevitationTrigger.TriggerInstance.levitated(DistancePredicate.vertical(MinMaxBounds.Floats.atLeast(50.0f)))).save(consumer, "end/levitate");
        final Advancement q10 = Advancement.Builder.advancement().parent(q7).display(Items.ELYTRA, new TranslatableComponent("advancements.end.elytra.title", new Object[0]), new TranslatableComponent("advancements.end.elytra.description", new Object[0]), null, FrameType.GOAL, true, true, false).addCriterion("elytra", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItem(Items.ELYTRA)).save(consumer, "end/elytra");
        final Advancement q11 = Advancement.Builder.advancement().parent(q4).display(Blocks.DRAGON_EGG, new TranslatableComponent("advancements.end.dragon_egg.title", new Object[0]), new TranslatableComponent("advancements.end.dragon_egg.description", new Object[0]), null, FrameType.GOAL, true, true, false).addCriterion("dragon_egg", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItem(Blocks.DRAGON_EGG)).save(consumer, "end/dragon_egg");
    }
}
