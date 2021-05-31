package net.minecraft.data.advancements;

import net.minecraft.world.entity.animal.Cat;
import net.minecraft.advancements.critereon.FishingRodHookedTrigger;
import net.minecraft.advancements.critereon.FilledBucketTrigger;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.Registry;
import net.minecraft.advancements.critereon.TameAnimalTrigger;
import net.minecraft.advancements.critereon.ItemDurabilityTrigger;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.BredAnimalsTrigger;
import net.minecraft.advancements.critereon.PlacedBlockTrigger;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.world.item.Items;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.ConsumeItemTrigger;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ItemLike;
import net.minecraft.advancements.FrameType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.EntityType;
import net.minecraft.advancements.Advancement;
import java.util.function.Consumer;

public class HusbandryAdvancements implements Consumer<Consumer<Advancement>> {
    private static final EntityType<?>[] BREEDABLE_ANIMALS;
    private static final Item[] FISH;
    private static final Item[] FISH_BUCKETS;
    private static final Item[] EDIBLE_ITEMS;
    
    public void accept(final Consumer<Advancement> consumer) {
        final Advancement q3 = Advancement.Builder.advancement().display(Blocks.HAY_BLOCK, new TranslatableComponent("advancements.husbandry.root.title", new Object[0]), new TranslatableComponent("advancements.husbandry.root.description", new Object[0]), new ResourceLocation("textures/gui/advancements/backgrounds/husbandry.png"), FrameType.TASK, false, false, false).addCriterion("consumed_item", (CriterionTriggerInstance)ConsumeItemTrigger.TriggerInstance.usedItem()).save(consumer, "husbandry/root");
        final Advancement q4 = Advancement.Builder.advancement().parent(q3).display(Items.WHEAT, new TranslatableComponent("advancements.husbandry.plant_seed.title", new Object[0]), new TranslatableComponent("advancements.husbandry.plant_seed.description", new Object[0]), null, FrameType.TASK, true, true, false).requirements(RequirementsStrategy.OR).addCriterion("wheat", (CriterionTriggerInstance)PlacedBlockTrigger.TriggerInstance.placedBlock(Blocks.WHEAT)).addCriterion("pumpkin_stem", (CriterionTriggerInstance)PlacedBlockTrigger.TriggerInstance.placedBlock(Blocks.PUMPKIN_STEM)).addCriterion("melon_stem", (CriterionTriggerInstance)PlacedBlockTrigger.TriggerInstance.placedBlock(Blocks.MELON_STEM)).addCriterion("beetroots", (CriterionTriggerInstance)PlacedBlockTrigger.TriggerInstance.placedBlock(Blocks.BEETROOTS)).addCriterion("nether_wart", (CriterionTriggerInstance)PlacedBlockTrigger.TriggerInstance.placedBlock(Blocks.NETHER_WART)).save(consumer, "husbandry/plant_seed");
        final Advancement q5 = Advancement.Builder.advancement().parent(q3).display(Items.WHEAT, new TranslatableComponent("advancements.husbandry.breed_an_animal.title", new Object[0]), new TranslatableComponent("advancements.husbandry.breed_an_animal.description", new Object[0]), null, FrameType.TASK, true, true, false).requirements(RequirementsStrategy.OR).addCriterion("bred", (CriterionTriggerInstance)BredAnimalsTrigger.TriggerInstance.bredAnimals()).save(consumer, "husbandry/breed_an_animal");
        final Advancement q6 = this.addFood(Advancement.Builder.advancement()).parent(q4).display(Items.APPLE, new TranslatableComponent("advancements.husbandry.balanced_diet.title", new Object[0]), new TranslatableComponent("advancements.husbandry.balanced_diet.description", new Object[0]), null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).save(consumer, "husbandry/balanced_diet");
        final Advancement q7 = Advancement.Builder.advancement().parent(q4).display(Items.DIAMOND_HOE, new TranslatableComponent("advancements.husbandry.break_diamond_hoe.title", new Object[0]), new TranslatableComponent("advancements.husbandry.break_diamond_hoe.description", new Object[0]), null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).addCriterion("broke_hoe", (CriterionTriggerInstance)ItemDurabilityTrigger.TriggerInstance.changedDurability(ItemPredicate.Builder.item().of(Items.DIAMOND_HOE).build(), MinMaxBounds.Ints.exactly(0))).save(consumer, "husbandry/break_diamond_hoe");
        final Advancement q8 = Advancement.Builder.advancement().parent(q3).display(Items.LEAD, new TranslatableComponent("advancements.husbandry.tame_an_animal.title", new Object[0]), new TranslatableComponent("advancements.husbandry.tame_an_animal.description", new Object[0]), null, FrameType.TASK, true, true, false).addCriterion("tamed_animal", (CriterionTriggerInstance)TameAnimalTrigger.TriggerInstance.tamedAnimal()).save(consumer, "husbandry/tame_an_animal");
        final Advancement q9 = this.addBreedable(Advancement.Builder.advancement()).parent(q5).display(Items.GOLDEN_CARROT, new TranslatableComponent("advancements.husbandry.breed_all_animals.title", new Object[0]), new TranslatableComponent("advancements.husbandry.breed_all_animals.description", new Object[0]), null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).save(consumer, "husbandry/bred_all_animals");
        final Advancement q10 = this.addFish(Advancement.Builder.advancement()).parent(q3).requirements(RequirementsStrategy.OR).display(Items.FISHING_ROD, new TranslatableComponent("advancements.husbandry.fishy_business.title", new Object[0]), new TranslatableComponent("advancements.husbandry.fishy_business.description", new Object[0]), null, FrameType.TASK, true, true, false).save(consumer, "husbandry/fishy_business");
        final Advancement q11 = this.addFishBuckets(Advancement.Builder.advancement()).parent(q10).requirements(RequirementsStrategy.OR).display(Items.PUFFERFISH_BUCKET, new TranslatableComponent("advancements.husbandry.tactical_fishing.title", new Object[0]), new TranslatableComponent("advancements.husbandry.tactical_fishing.description", new Object[0]), null, FrameType.TASK, true, true, false).save(consumer, "husbandry/tactical_fishing");
        final Advancement q12 = this.addCatVariants(Advancement.Builder.advancement()).parent(q8).display(Items.COD, new TranslatableComponent("advancements.husbandry.complete_catalogue.title", new Object[0]), new TranslatableComponent("advancements.husbandry.complete_catalogue.description", new Object[0]), null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).save(consumer, "husbandry/complete_catalogue");
    }
    
    private Advancement.Builder addFood(final Advancement.Builder a) {
        for (final Item bce6 : HusbandryAdvancements.EDIBLE_ITEMS) {
            a.addCriterion(Registry.ITEM.getKey(bce6).getPath(), ConsumeItemTrigger.TriggerInstance.usedItem(bce6));
        }
        return a;
    }
    
    private Advancement.Builder addBreedable(final Advancement.Builder a) {
        for (final EntityType<?> ais6 : HusbandryAdvancements.BREEDABLE_ANIMALS) {
            a.addCriterion(EntityType.getKey(ais6).toString(), BredAnimalsTrigger.TriggerInstance.bredAnimals(EntityPredicate.Builder.entity().of(ais6)));
        }
        return a;
    }
    
    private Advancement.Builder addFishBuckets(final Advancement.Builder a) {
        for (final Item bce6 : HusbandryAdvancements.FISH_BUCKETS) {
            a.addCriterion(Registry.ITEM.getKey(bce6).getPath(), FilledBucketTrigger.TriggerInstance.filledBucket(ItemPredicate.Builder.item().of(bce6).build()));
        }
        return a;
    }
    
    private Advancement.Builder addFish(final Advancement.Builder a) {
        for (final Item bce6 : HusbandryAdvancements.FISH) {
            a.addCriterion(Registry.ITEM.getKey(bce6).getPath(), FishingRodHookedTrigger.TriggerInstance.fishedItem(ItemPredicate.ANY, EntityPredicate.ANY, ItemPredicate.Builder.item().of(bce6).build()));
        }
        return a;
    }
    
    private Advancement.Builder addCatVariants(final Advancement.Builder a) {
        Cat.TEXTURE_BY_TYPE.forEach((integer, qv) -> a.addCriterion(qv.getPath(), TameAnimalTrigger.TriggerInstance.tamedAnimal(EntityPredicate.Builder.entity().of(qv).build())));
        return a;
    }
    
    static {
        BREEDABLE_ANIMALS = new EntityType[] { EntityType.HORSE, EntityType.SHEEP, EntityType.COW, EntityType.MOOSHROOM, EntityType.PIG, EntityType.CHICKEN, EntityType.WOLF, EntityType.OCELOT, EntityType.RABBIT, EntityType.LLAMA, EntityType.TURTLE, EntityType.CAT, EntityType.PANDA, EntityType.FOX };
        FISH = new Item[] { Items.COD, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.SALMON };
        FISH_BUCKETS = new Item[] { Items.COD_BUCKET, Items.TROPICAL_FISH_BUCKET, Items.PUFFERFISH_BUCKET, Items.SALMON_BUCKET };
        EDIBLE_ITEMS = new Item[] { Items.APPLE, Items.MUSHROOM_STEW, Items.BREAD, Items.PORKCHOP, Items.COOKED_PORKCHOP, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.COOKED_COD, Items.COOKED_SALMON, Items.COOKIE, Items.MELON_SLICE, Items.BEEF, Items.COOKED_BEEF, Items.CHICKEN, Items.COOKED_CHICKEN, Items.ROTTEN_FLESH, Items.SPIDER_EYE, Items.CARROT, Items.POTATO, Items.BAKED_POTATO, Items.POISONOUS_POTATO, Items.GOLDEN_CARROT, Items.PUMPKIN_PIE, Items.RABBIT, Items.COOKED_RABBIT, Items.RABBIT_STEW, Items.MUTTON, Items.COOKED_MUTTON, Items.CHORUS_FRUIT, Items.BEETROOT, Items.BEETROOT_SOUP, Items.DRIED_KELP, Items.SUSPICIOUS_STEW, Items.SWEET_BERRIES };
    }
}
