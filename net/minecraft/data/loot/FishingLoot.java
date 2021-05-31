package net.minecraft.data.loot;

import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.resources.ResourceLocation;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FishingLoot implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
    public static final LootItemCondition.Builder IN_JUNGLE;
    public static final LootItemCondition.Builder IN_JUNGLE_HILLS;
    public static final LootItemCondition.Builder IN_JUNGLE_EDGE;
    public static final LootItemCondition.Builder IN_BAMBOO_JUNGLE;
    public static final LootItemCondition.Builder IN_MODIFIED_JUNGLE;
    public static final LootItemCondition.Builder IN_MODIFIED_JUNGLE_EDGE;
    public static final LootItemCondition.Builder IN_BAMBOO_JUNGLE_HILLS;
    
    public void accept(final BiConsumer<ResourceLocation, LootTable.Builder> biConsumer) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getstatic       net/minecraft/world/level/storage/loot/BuiltInLootTables.FISHING:Lnet/minecraft/resources/ResourceLocation;
        //     4: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //     7: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //    10: iconst_1       
        //    11: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //    14: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //    17: getstatic       net/minecraft/world/level/storage/loot/BuiltInLootTables.FISHING_JUNK:Lnet/minecraft/resources/ResourceLocation;
        //    20: invokestatic    net/minecraft/world/level/storage/loot/entries/LootTableReference.lootTableReference:(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //    23: bipush          10
        //    25: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.setWeight:(I)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //    28: bipush          -2
        //    30: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.setQuality:(I)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //    33: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //    36: getstatic       net/minecraft/world/level/storage/loot/BuiltInLootTables.FISHING_TREASURE:Lnet/minecraft/resources/ResourceLocation;
        //    39: invokestatic    net/minecraft/world/level/storage/loot/entries/LootTableReference.lootTableReference:(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //    42: iconst_5       
        //    43: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.setWeight:(I)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //    46: iconst_2       
        //    47: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.setQuality:(I)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //    50: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //    53: getstatic       net/minecraft/world/level/storage/loot/BuiltInLootTables.FISHING_FISH:Lnet/minecraft/resources/ResourceLocation;
        //    56: invokestatic    net/minecraft/world/level/storage/loot/entries/LootTableReference.lootTableReference:(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //    59: bipush          85
        //    61: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.setWeight:(I)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //    64: iconst_m1      
        //    65: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.setQuality:(I)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //    68: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //    71: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //    74: invokeinterface java/util/function/BiConsumer.accept:(Ljava/lang/Object;Ljava/lang/Object;)V
        //    79: aload_1         /* biConsumer */
        //    80: getstatic       net/minecraft/world/level/storage/loot/BuiltInLootTables.FISHING_FISH:Lnet/minecraft/resources/ResourceLocation;
        //    83: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //    86: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //    89: getstatic       net/minecraft/world/item/Items.COD:Lnet/minecraft/world/item/Item;
        //    92: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //    95: bipush          60
        //    97: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.setWeight:(I)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   100: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   103: getstatic       net/minecraft/world/item/Items.SALMON:Lnet/minecraft/world/item/Item;
        //   106: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   109: bipush          25
        //   111: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.setWeight:(I)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   114: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   117: getstatic       net/minecraft/world/item/Items.TROPICAL_FISH:Lnet/minecraft/world/item/Item;
        //   120: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   123: iconst_2       
        //   124: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.setWeight:(I)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   127: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   130: getstatic       net/minecraft/world/item/Items.PUFFERFISH:Lnet/minecraft/world/item/Item;
        //   133: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   136: bipush          13
        //   138: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.setWeight:(I)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   141: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   144: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   147: invokeinterface java/util/function/BiConsumer.accept:(Ljava/lang/Object;Ljava/lang/Object;)V
        //   152: aload_1         /* biConsumer */
        //   153: getstatic       net/minecraft/world/level/storage/loot/BuiltInLootTables.FISHING_JUNK:Lnet/minecraft/resources/ResourceLocation;
        //   156: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   159: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   162: getstatic       net/minecraft/world/item/Items.LEATHER_BOOTS:Lnet/minecraft/world/item/Item;
        //   165: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   168: bipush          10
        //   170: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.setWeight:(I)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   173: fconst_0       
        //   174: ldc             0.9
        //   176: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //   179: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemDamageFunction.setDamage:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //   182: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   185: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   188: getstatic       net/minecraft/world/item/Items.LEATHER:Lnet/minecraft/world/item/Item;
        //   191: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   194: bipush          10
        //   196: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.setWeight:(I)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   199: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   202: getstatic       net/minecraft/world/item/Items.BONE:Lnet/minecraft/world/item/Item;
        //   205: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   208: bipush          10
        //   210: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.setWeight:(I)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   213: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   216: getstatic       net/minecraft/world/item/Items.POTION:Lnet/minecraft/world/item/Item;
        //   219: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   222: bipush          10
        //   224: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.setWeight:(I)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   227: new             Lnet/minecraft/nbt/CompoundTag;
        //   230: dup            
        //   231: invokespecial   net/minecraft/nbt/CompoundTag.<init>:()V
        //   234: invokedynamic   BootstrapMethod #0, accept:()Ljava/util/function/Consumer;
        //   239: invokestatic    net/minecraft/Util.make:(Ljava/lang/Object;Ljava/util/function/Consumer;)Ljava/lang/Object;
        //   242: checkcast       Lnet/minecraft/nbt/CompoundTag;
        //   245: invokestatic    net/minecraft/world/level/storage/loot/functions/SetNbtFunction.setTag:(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //   248: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   251: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   254: getstatic       net/minecraft/world/item/Items.STRING:Lnet/minecraft/world/item/Item;
        //   257: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   260: iconst_5       
        //   261: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.setWeight:(I)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   264: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   267: getstatic       net/minecraft/world/item/Items.FISHING_ROD:Lnet/minecraft/world/item/Item;
        //   270: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   273: iconst_2       
        //   274: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.setWeight:(I)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   277: fconst_0       
        //   278: ldc             0.9
        //   280: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //   283: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemDamageFunction.setDamage:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //   286: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   289: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   292: getstatic       net/minecraft/world/item/Items.BOWL:Lnet/minecraft/world/item/Item;
        //   295: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   298: bipush          10
        //   300: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.setWeight:(I)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   303: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   306: getstatic       net/minecraft/world/item/Items.STICK:Lnet/minecraft/world/item/Item;
        //   309: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   312: iconst_5       
        //   313: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.setWeight:(I)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   316: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   319: getstatic       net/minecraft/world/item/Items.INK_SAC:Lnet/minecraft/world/item/Item;
        //   322: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   325: iconst_1       
        //   326: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.setWeight:(I)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   329: bipush          10
        //   331: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //   334: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //   337: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   340: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   343: getstatic       net/minecraft/world/level/block/Blocks.TRIPWIRE_HOOK:Lnet/minecraft/world/level/block/Block;
        //   346: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   349: bipush          10
        //   351: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.setWeight:(I)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   354: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   357: getstatic       net/minecraft/world/item/Items.ROTTEN_FLESH:Lnet/minecraft/world/item/Item;
        //   360: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   363: bipush          10
        //   365: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.setWeight:(I)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   368: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   371: getstatic       net/minecraft/world/level/block/Blocks.BAMBOO:Lnet/minecraft/world/level/block/Block;
        //   374: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   377: getstatic       net/minecraft/data/loot/FishingLoot.IN_JUNGLE:Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //   380: getstatic       net/minecraft/data/loot/FishingLoot.IN_JUNGLE_HILLS:Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //   383: invokeinterface net/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder.or:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/predicates/AlternativeLootItemCondition$Builder;
        //   388: getstatic       net/minecraft/data/loot/FishingLoot.IN_JUNGLE_EDGE:Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //   391: invokevirtual   net/minecraft/world/level/storage/loot/predicates/AlternativeLootItemCondition$Builder.or:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/predicates/AlternativeLootItemCondition$Builder;
        //   394: getstatic       net/minecraft/data/loot/FishingLoot.IN_BAMBOO_JUNGLE:Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //   397: invokevirtual   net/minecraft/world/level/storage/loot/predicates/AlternativeLootItemCondition$Builder.or:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/predicates/AlternativeLootItemCondition$Builder;
        //   400: getstatic       net/minecraft/data/loot/FishingLoot.IN_MODIFIED_JUNGLE:Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //   403: invokevirtual   net/minecraft/world/level/storage/loot/predicates/AlternativeLootItemCondition$Builder.or:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/predicates/AlternativeLootItemCondition$Builder;
        //   406: getstatic       net/minecraft/data/loot/FishingLoot.IN_MODIFIED_JUNGLE_EDGE:Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //   409: invokevirtual   net/minecraft/world/level/storage/loot/predicates/AlternativeLootItemCondition$Builder.or:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/predicates/AlternativeLootItemCondition$Builder;
        //   412: getstatic       net/minecraft/data/loot/FishingLoot.IN_BAMBOO_JUNGLE_HILLS:Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //   415: invokevirtual   net/minecraft/world/level/storage/loot/predicates/AlternativeLootItemCondition$Builder.or:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/predicates/AlternativeLootItemCondition$Builder;
        //   418: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;
        //   421: checkcast       Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   424: bipush          10
        //   426: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.setWeight:(I)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   429: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   432: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   435: invokeinterface java/util/function/BiConsumer.accept:(Ljava/lang/Object;Ljava/lang/Object;)V
        //   440: aload_1         /* biConsumer */
        //   441: getstatic       net/minecraft/world/level/storage/loot/BuiltInLootTables.FISHING_TREASURE:Lnet/minecraft/resources/ResourceLocation;
        //   444: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   447: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   450: getstatic       net/minecraft/world/level/block/Blocks.LILY_PAD:Lnet/minecraft/world/level/block/Block;
        //   453: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   456: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   459: getstatic       net/minecraft/world/item/Items.NAME_TAG:Lnet/minecraft/world/item/Item;
        //   462: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   465: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   468: getstatic       net/minecraft/world/item/Items.SADDLE:Lnet/minecraft/world/item/Item;
        //   471: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   474: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   477: getstatic       net/minecraft/world/item/Items.BOW:Lnet/minecraft/world/item/Item;
        //   480: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   483: fconst_0       
        //   484: ldc_w           0.25
        //   487: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //   490: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemDamageFunction.setDamage:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //   493: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   496: bipush          30
        //   498: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //   501: invokestatic    net/minecraft/world/level/storage/loot/functions/EnchantWithLevelsFunction.enchantWithLevels:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/EnchantWithLevelsFunction$Builder;
        //   504: invokevirtual   net/minecraft/world/level/storage/loot/functions/EnchantWithLevelsFunction$Builder.allowTreasure:()Lnet/minecraft/world/level/storage/loot/functions/EnchantWithLevelsFunction$Builder;
        //   507: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   510: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   513: getstatic       net/minecraft/world/item/Items.FISHING_ROD:Lnet/minecraft/world/item/Item;
        //   516: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   519: fconst_0       
        //   520: ldc_w           0.25
        //   523: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //   526: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemDamageFunction.setDamage:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //   529: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   532: bipush          30
        //   534: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //   537: invokestatic    net/minecraft/world/level/storage/loot/functions/EnchantWithLevelsFunction.enchantWithLevels:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/EnchantWithLevelsFunction$Builder;
        //   540: invokevirtual   net/minecraft/world/level/storage/loot/functions/EnchantWithLevelsFunction$Builder.allowTreasure:()Lnet/minecraft/world/level/storage/loot/functions/EnchantWithLevelsFunction$Builder;
        //   543: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   546: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   549: getstatic       net/minecraft/world/item/Items.BOOK:Lnet/minecraft/world/item/Item;
        //   552: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   555: bipush          30
        //   557: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //   560: invokestatic    net/minecraft/world/level/storage/loot/functions/EnchantWithLevelsFunction.enchantWithLevels:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/EnchantWithLevelsFunction$Builder;
        //   563: invokevirtual   net/minecraft/world/level/storage/loot/functions/EnchantWithLevelsFunction$Builder.allowTreasure:()Lnet/minecraft/world/level/storage/loot/functions/EnchantWithLevelsFunction$Builder;
        //   566: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   569: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   572: getstatic       net/minecraft/world/item/Items.NAUTILUS_SHELL:Lnet/minecraft/world/item/Item;
        //   575: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   578: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   581: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   584: invokeinterface java/util/function/BiConsumer.accept:(Ljava/lang/Object;Ljava/lang/Object;)V
        //   589: return         
        //    Signature:
        //  (Ljava/util/function/BiConsumer<Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;>;)V
        //    MethodParameters:
        //  Name        Flags  
        //  ----------  -----
        //  biConsumer  
        // 
        // The error that occurred was:
        // 
        // java.lang.UnsupportedOperationException: The requested operation is not supported.
        //     at com.strobel.util.ContractUtils.unsupported(ContractUtils.java:27)
        //     at com.strobel.assembler.metadata.TypeReference.getRawType(TypeReference.java:276)
        //     at com.strobel.assembler.metadata.TypeReference.getRawType(TypeReference.java:271)
        //     at com.strobel.assembler.metadata.TypeReference.makeGenericType(TypeReference.java:150)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:187)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:39)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitMethod(TypeSubstitutionVisitor.java:276)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2591)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2695)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:770)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:766)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2463)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:770)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:766)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2463)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:770)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:766)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2463)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:770)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:766)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2463)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:770)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:766)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2463)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:770)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:766)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2463)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:770)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:766)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2463)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:770)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:766)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2463)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2695)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2695)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:672)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypesForVariables(TypeAnalysis.java:586)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:397)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:96)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:109)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at cuchaz.enigma.source.procyon.ProcyonDecompiler.getSource(ProcyonDecompiler.java:77)
        //     at cuchaz.enigma.EnigmaProject$JarExport.decompileClass(EnigmaProject.java:298)
        //     at cuchaz.enigma.EnigmaProject$JarExport.lambda$decompileStream$1(EnigmaProject.java:274)
        //     at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:195)
        //     at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1655)
        //     at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:484)
        //     at java.base/java.util.stream.ForEachOps$ForEachTask.compute(ForEachOps.java:290)
        //     at java.base/java.util.concurrent.CountedCompleter.exec(CountedCompleter.java:746)
        //     at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:290)
        //     at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1020)
        //     at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1656)
        //     at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1594)
        //     at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:183)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    static {
        IN_JUNGLE = LocationCheck.checkLocation(new LocationPredicate.Builder().setBiome(Biomes.JUNGLE));
        IN_JUNGLE_HILLS = LocationCheck.checkLocation(new LocationPredicate.Builder().setBiome(Biomes.JUNGLE_HILLS));
        IN_JUNGLE_EDGE = LocationCheck.checkLocation(new LocationPredicate.Builder().setBiome(Biomes.JUNGLE_EDGE));
        IN_BAMBOO_JUNGLE = LocationCheck.checkLocation(new LocationPredicate.Builder().setBiome(Biomes.BAMBOO_JUNGLE));
        IN_MODIFIED_JUNGLE = LocationCheck.checkLocation(new LocationPredicate.Builder().setBiome(Biomes.MODIFIED_JUNGLE));
        IN_MODIFIED_JUNGLE_EDGE = LocationCheck.checkLocation(new LocationPredicate.Builder().setBiome(Biomes.MODIFIED_JUNGLE_EDGE));
        IN_BAMBOO_JUNGLE_HILLS = LocationCheck.checkLocation(new LocationPredicate.Builder().setBiome(Biomes.BAMBOO_JUNGLE_HILLS));
    }
}
