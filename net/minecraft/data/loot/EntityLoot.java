package net.minecraft.data.loot;

import com.google.common.collect.ImmutableSet;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;
import net.minecraft.world.level.storage.loot.ConstantIntValue;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.ItemLike;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.world.entity.EntityType;
import java.util.Set;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.resources.ResourceLocation;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EntityLoot implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
    private static final EntityPredicate.Builder ENTITY_ON_FIRE;
    private static final Set<EntityType<?>> SPECIAL_LOOT_TABLE_TYPES;
    private final Map<ResourceLocation, LootTable.Builder> map;
    
    public EntityLoot() {
        this.map = (Map<ResourceLocation, LootTable.Builder>)Maps.newHashMap();
    }
    
    private static LootTable.Builder createSheepTable(final ItemLike bhq) {
        return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(bhq))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootTableReference.lootTableReference(EntityType.SHEEP.getDefaultLootTable())));
    }
    
    public void accept(final BiConsumer<ResourceLocation, LootTable.Builder> biConsumer) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getstatic       net/minecraft/world/entity/EntityType.ARMOR_STAND:Lnet/minecraft/world/entity/EntityType;
        //     4: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //     7: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //    10: aload_0         /* this */
        //    11: getstatic       net/minecraft/world/entity/EntityType.BAT:Lnet/minecraft/world/entity/EntityType;
        //    14: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //    17: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //    20: aload_0         /* this */
        //    21: getstatic       net/minecraft/world/entity/EntityType.BLAZE:Lnet/minecraft/world/entity/EntityType;
        //    24: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //    27: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //    30: iconst_1       
        //    31: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //    34: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //    37: getstatic       net/minecraft/world/item/Items.BLAZE_ROD:Lnet/minecraft/world/item/Item;
        //    40: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //    43: fconst_0       
        //    44: fconst_1       
        //    45: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //    48: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //    51: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //    54: fconst_0       
        //    55: fconst_1       
        //    56: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //    59: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //    62: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //    65: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //    68: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemKilledByPlayerCondition.killedByPlayer:()Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //    71: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //    74: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //    77: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //    80: aload_0         /* this */
        //    81: getstatic       net/minecraft/world/entity/EntityType.CAT:Lnet/minecraft/world/entity/EntityType;
        //    84: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //    87: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //    90: iconst_1       
        //    91: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //    94: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //    97: getstatic       net/minecraft/world/item/Items.STRING:Lnet/minecraft/world/item/Item;
        //   100: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   103: fconst_0       
        //   104: fconst_2       
        //   105: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //   108: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //   111: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   114: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   117: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   120: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //   123: aload_0         /* this */
        //   124: getstatic       net/minecraft/world/entity/EntityType.CAVE_SPIDER:Lnet/minecraft/world/entity/EntityType;
        //   127: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   130: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   133: iconst_1       
        //   134: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //   137: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   140: getstatic       net/minecraft/world/item/Items.STRING:Lnet/minecraft/world/item/Item;
        //   143: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   146: fconst_0       
        //   147: fconst_2       
        //   148: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //   151: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //   154: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   157: fconst_0       
        //   158: fconst_1       
        //   159: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //   162: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //   165: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   168: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   171: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   174: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   177: iconst_1       
        //   178: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //   181: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   184: getstatic       net/minecraft/world/item/Items.SPIDER_EYE:Lnet/minecraft/world/item/Item;
        //   187: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   190: ldc             -1.0
        //   192: fconst_1       
        //   193: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //   196: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //   199: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   202: fconst_0       
        //   203: fconst_1       
        //   204: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //   207: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //   210: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   213: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   216: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemKilledByPlayerCondition.killedByPlayer:()Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //   219: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   222: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   225: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //   228: aload_0         /* this */
        //   229: getstatic       net/minecraft/world/entity/EntityType.CHICKEN:Lnet/minecraft/world/entity/EntityType;
        //   232: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   235: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   238: iconst_1       
        //   239: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //   242: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   245: getstatic       net/minecraft/world/item/Items.FEATHER:Lnet/minecraft/world/item/Item;
        //   248: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   251: fconst_0       
        //   252: fconst_2       
        //   253: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //   256: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //   259: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   262: fconst_0       
        //   263: fconst_1       
        //   264: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //   267: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //   270: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   273: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   276: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   279: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   282: iconst_1       
        //   283: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //   286: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   289: getstatic       net/minecraft/world/item/Items.CHICKEN:Lnet/minecraft/world/item/Item;
        //   292: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   295: invokestatic    net/minecraft/world/level/storage/loot/functions/SmeltItemFunction.smelted:()Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //   298: getstatic       net/minecraft/world/level/storage/loot/LootContext$EntityTarget.THIS:Lnet/minecraft/world/level/storage/loot/LootContext$EntityTarget;
        //   301: getstatic       net/minecraft/data/loot/EntityLoot.ENTITY_ON_FIRE:Lnet/minecraft/advancements/critereon/EntityPredicate$Builder;
        //   304: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemEntityPropertyCondition.hasProperties:(Lnet/minecraft/world/level/storage/loot/LootContext$EntityTarget;Lnet/minecraft/advancements/critereon/EntityPredicate$Builder;)Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //   307: invokevirtual   net/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //   310: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   313: fconst_0       
        //   314: fconst_1       
        //   315: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //   318: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //   321: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   324: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   327: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   330: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //   333: aload_0         /* this */
        //   334: getstatic       net/minecraft/world/entity/EntityType.COD:Lnet/minecraft/world/entity/EntityType;
        //   337: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   340: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   343: iconst_1       
        //   344: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //   347: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   350: getstatic       net/minecraft/world/item/Items.COD:Lnet/minecraft/world/item/Item;
        //   353: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   356: invokestatic    net/minecraft/world/level/storage/loot/functions/SmeltItemFunction.smelted:()Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //   359: getstatic       net/minecraft/world/level/storage/loot/LootContext$EntityTarget.THIS:Lnet/minecraft/world/level/storage/loot/LootContext$EntityTarget;
        //   362: getstatic       net/minecraft/data/loot/EntityLoot.ENTITY_ON_FIRE:Lnet/minecraft/advancements/critereon/EntityPredicate$Builder;
        //   365: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemEntityPropertyCondition.hasProperties:(Lnet/minecraft/world/level/storage/loot/LootContext$EntityTarget;Lnet/minecraft/advancements/critereon/EntityPredicate$Builder;)Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //   368: invokevirtual   net/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //   371: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   374: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   377: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   380: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   383: iconst_1       
        //   384: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //   387: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   390: getstatic       net/minecraft/world/item/Items.BONE_MEAL:Lnet/minecraft/world/item/Item;
        //   393: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   396: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   399: ldc             0.05
        //   401: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemRandomChanceCondition.randomChance:(F)Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //   404: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   407: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   410: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //   413: aload_0         /* this */
        //   414: getstatic       net/minecraft/world/entity/EntityType.COW:Lnet/minecraft/world/entity/EntityType;
        //   417: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   420: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   423: iconst_1       
        //   424: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //   427: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   430: getstatic       net/minecraft/world/item/Items.LEATHER:Lnet/minecraft/world/item/Item;
        //   433: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   436: fconst_0       
        //   437: fconst_2       
        //   438: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //   441: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //   444: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   447: fconst_0       
        //   448: fconst_1       
        //   449: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //   452: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //   455: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   458: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   461: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   464: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   467: iconst_1       
        //   468: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //   471: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   474: getstatic       net/minecraft/world/item/Items.BEEF:Lnet/minecraft/world/item/Item;
        //   477: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   480: fconst_1       
        //   481: ldc_w           3.0
        //   484: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //   487: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //   490: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   493: invokestatic    net/minecraft/world/level/storage/loot/functions/SmeltItemFunction.smelted:()Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //   496: getstatic       net/minecraft/world/level/storage/loot/LootContext$EntityTarget.THIS:Lnet/minecraft/world/level/storage/loot/LootContext$EntityTarget;
        //   499: getstatic       net/minecraft/data/loot/EntityLoot.ENTITY_ON_FIRE:Lnet/minecraft/advancements/critereon/EntityPredicate$Builder;
        //   502: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemEntityPropertyCondition.hasProperties:(Lnet/minecraft/world/level/storage/loot/LootContext$EntityTarget;Lnet/minecraft/advancements/critereon/EntityPredicate$Builder;)Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //   505: invokevirtual   net/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //   508: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   511: fconst_0       
        //   512: fconst_1       
        //   513: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //   516: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //   519: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   522: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   525: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   528: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //   531: aload_0         /* this */
        //   532: getstatic       net/minecraft/world/entity/EntityType.CREEPER:Lnet/minecraft/world/entity/EntityType;
        //   535: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   538: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   541: iconst_1       
        //   542: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //   545: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   548: getstatic       net/minecraft/world/item/Items.GUNPOWDER:Lnet/minecraft/world/item/Item;
        //   551: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   554: fconst_0       
        //   555: fconst_2       
        //   556: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //   559: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //   562: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   565: fconst_0       
        //   566: fconst_1       
        //   567: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //   570: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //   573: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   576: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   579: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   582: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   585: getstatic       net/minecraft/tags/ItemTags.MUSIC_DISCS:Lnet/minecraft/tags/Tag;
        //   588: invokestatic    net/minecraft/world/level/storage/loot/entries/TagEntry.expandTag:(Lnet/minecraft/tags/Tag;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   591: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   594: getstatic       net/minecraft/world/level/storage/loot/LootContext$EntityTarget.KILLER:Lnet/minecraft/world/level/storage/loot/LootContext$EntityTarget;
        //   597: invokestatic    net/minecraft/advancements/critereon/EntityPredicate$Builder.entity:()Lnet/minecraft/advancements/critereon/EntityPredicate$Builder;
        //   600: getstatic       net/minecraft/tags/EntityTypeTags.SKELETONS:Lnet/minecraft/tags/Tag;
        //   603: invokevirtual   net/minecraft/advancements/critereon/EntityPredicate$Builder.of:(Lnet/minecraft/tags/Tag;)Lnet/minecraft/advancements/critereon/EntityPredicate$Builder;
        //   606: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemEntityPropertyCondition.hasProperties:(Lnet/minecraft/world/level/storage/loot/LootContext$EntityTarget;Lnet/minecraft/advancements/critereon/EntityPredicate$Builder;)Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //   609: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   612: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   615: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //   618: aload_0         /* this */
        //   619: getstatic       net/minecraft/world/entity/EntityType.DOLPHIN:Lnet/minecraft/world/entity/EntityType;
        //   622: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   625: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   628: iconst_1       
        //   629: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //   632: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   635: getstatic       net/minecraft/world/item/Items.COD:Lnet/minecraft/world/item/Item;
        //   638: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   641: fconst_0       
        //   642: fconst_1       
        //   643: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //   646: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //   649: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   652: fconst_0       
        //   653: fconst_1       
        //   654: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //   657: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //   660: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   663: invokestatic    net/minecraft/world/level/storage/loot/functions/SmeltItemFunction.smelted:()Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //   666: getstatic       net/minecraft/world/level/storage/loot/LootContext$EntityTarget.THIS:Lnet/minecraft/world/level/storage/loot/LootContext$EntityTarget;
        //   669: getstatic       net/minecraft/data/loot/EntityLoot.ENTITY_ON_FIRE:Lnet/minecraft/advancements/critereon/EntityPredicate$Builder;
        //   672: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemEntityPropertyCondition.hasProperties:(Lnet/minecraft/world/level/storage/loot/LootContext$EntityTarget;Lnet/minecraft/advancements/critereon/EntityPredicate$Builder;)Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //   675: invokevirtual   net/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //   678: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   681: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   684: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   687: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //   690: aload_0         /* this */
        //   691: getstatic       net/minecraft/world/entity/EntityType.DONKEY:Lnet/minecraft/world/entity/EntityType;
        //   694: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   697: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   700: iconst_1       
        //   701: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //   704: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   707: getstatic       net/minecraft/world/item/Items.LEATHER:Lnet/minecraft/world/item/Item;
        //   710: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   713: fconst_0       
        //   714: fconst_2       
        //   715: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //   718: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //   721: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   724: fconst_0       
        //   725: fconst_1       
        //   726: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //   729: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //   732: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   735: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   738: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   741: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //   744: aload_0         /* this */
        //   745: getstatic       net/minecraft/world/entity/EntityType.DROWNED:Lnet/minecraft/world/entity/EntityType;
        //   748: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   751: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   754: iconst_1       
        //   755: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //   758: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   761: getstatic       net/minecraft/world/item/Items.ROTTEN_FLESH:Lnet/minecraft/world/item/Item;
        //   764: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   767: fconst_0       
        //   768: fconst_2       
        //   769: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //   772: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //   775: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   778: fconst_0       
        //   779: fconst_1       
        //   780: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //   783: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //   786: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   789: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   792: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   795: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   798: iconst_1       
        //   799: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //   802: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   805: getstatic       net/minecraft/world/item/Items.GOLD_INGOT:Lnet/minecraft/world/item/Item;
        //   808: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   811: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   814: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemKilledByPlayerCondition.killedByPlayer:()Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //   817: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   820: ldc             0.05
        //   822: ldc_w           0.01
        //   825: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost:(FF)Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //   828: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   831: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   834: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //   837: aload_0         /* this */
        //   838: getstatic       net/minecraft/world/entity/EntityType.ELDER_GUARDIAN:Lnet/minecraft/world/entity/EntityType;
        //   841: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   844: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   847: iconst_1       
        //   848: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //   851: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   854: getstatic       net/minecraft/world/item/Items.PRISMARINE_SHARD:Lnet/minecraft/world/item/Item;
        //   857: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   860: fconst_0       
        //   861: fconst_2       
        //   862: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //   865: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //   868: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   871: fconst_0       
        //   872: fconst_1       
        //   873: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //   876: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //   879: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   882: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   885: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   888: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   891: iconst_1       
        //   892: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //   895: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   898: getstatic       net/minecraft/world/item/Items.COD:Lnet/minecraft/world/item/Item;
        //   901: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   904: iconst_3       
        //   905: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.setWeight:(I)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   908: fconst_0       
        //   909: fconst_1       
        //   910: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //   913: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //   916: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   919: invokestatic    net/minecraft/world/level/storage/loot/functions/SmeltItemFunction.smelted:()Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //   922: getstatic       net/minecraft/world/level/storage/loot/LootContext$EntityTarget.THIS:Lnet/minecraft/world/level/storage/loot/LootContext$EntityTarget;
        //   925: getstatic       net/minecraft/data/loot/EntityLoot.ENTITY_ON_FIRE:Lnet/minecraft/advancements/critereon/EntityPredicate$Builder;
        //   928: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemEntityPropertyCondition.hasProperties:(Lnet/minecraft/world/level/storage/loot/LootContext$EntityTarget;Lnet/minecraft/advancements/critereon/EntityPredicate$Builder;)Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //   931: invokevirtual   net/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //   934: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   937: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   940: getstatic       net/minecraft/world/item/Items.PRISMARINE_CRYSTALS:Lnet/minecraft/world/item/Item;
        //   943: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   946: iconst_2       
        //   947: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.setWeight:(I)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   950: fconst_0       
        //   951: fconst_1       
        //   952: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //   955: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //   958: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   961: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   964: invokestatic    net/minecraft/world/level/storage/loot/entries/EmptyLootItem.emptyItem:()Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   967: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   970: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //   973: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   976: iconst_1       
        //   977: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //   980: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   983: getstatic       net/minecraft/world/level/block/Blocks.WET_SPONGE:Lnet/minecraft/world/level/block/Block;
        //   986: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //   989: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   992: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemKilledByPlayerCondition.killedByPlayer:()Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //   995: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //   998: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1001: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1004: iconst_1       
        //  1005: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  1008: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1011: getstatic       net/minecraft/world/level/storage/loot/BuiltInLootTables.FISHING_FISH:Lnet/minecraft/resources/ResourceLocation;
        //  1014: invokestatic    net/minecraft/world/level/storage/loot/entries/LootTableReference.lootTableReference:(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1017: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1020: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemKilledByPlayerCondition.killedByPlayer:()Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  1023: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1026: ldc_w           0.025
        //  1029: ldc_w           0.01
        //  1032: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost:(FF)Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  1035: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1038: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1041: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  1044: aload_0         /* this */
        //  1045: getstatic       net/minecraft/world/entity/EntityType.ENDER_DRAGON:Lnet/minecraft/world/entity/EntityType;
        //  1048: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1051: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  1054: aload_0         /* this */
        //  1055: getstatic       net/minecraft/world/entity/EntityType.ENDERMAN:Lnet/minecraft/world/entity/EntityType;
        //  1058: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1061: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1064: iconst_1       
        //  1065: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  1068: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1071: getstatic       net/minecraft/world/item/Items.ENDER_PEARL:Lnet/minecraft/world/item/Item;
        //  1074: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1077: fconst_0       
        //  1078: fconst_1       
        //  1079: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  1082: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  1085: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1088: fconst_0       
        //  1089: fconst_1       
        //  1090: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  1093: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  1096: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1099: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1102: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1105: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  1108: aload_0         /* this */
        //  1109: getstatic       net/minecraft/world/entity/EntityType.ENDERMITE:Lnet/minecraft/world/entity/EntityType;
        //  1112: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1115: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  1118: aload_0         /* this */
        //  1119: getstatic       net/minecraft/world/entity/EntityType.EVOKER:Lnet/minecraft/world/entity/EntityType;
        //  1122: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1125: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1128: iconst_1       
        //  1129: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  1132: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1135: getstatic       net/minecraft/world/item/Items.TOTEM_OF_UNDYING:Lnet/minecraft/world/item/Item;
        //  1138: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1141: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1144: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1147: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1150: iconst_1       
        //  1151: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  1154: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1157: getstatic       net/minecraft/world/item/Items.EMERALD:Lnet/minecraft/world/item/Item;
        //  1160: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1163: fconst_0       
        //  1164: fconst_1       
        //  1165: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  1168: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  1171: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1174: fconst_0       
        //  1175: fconst_1       
        //  1176: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  1179: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  1182: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1185: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1188: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemKilledByPlayerCondition.killedByPlayer:()Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  1191: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1194: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1197: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  1200: aload_0         /* this */
        //  1201: getstatic       net/minecraft/world/entity/EntityType.FOX:Lnet/minecraft/world/entity/EntityType;
        //  1204: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1207: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  1210: aload_0         /* this */
        //  1211: getstatic       net/minecraft/world/entity/EntityType.GHAST:Lnet/minecraft/world/entity/EntityType;
        //  1214: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1217: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1220: iconst_1       
        //  1221: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  1224: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1227: getstatic       net/minecraft/world/item/Items.GHAST_TEAR:Lnet/minecraft/world/item/Item;
        //  1230: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1233: fconst_0       
        //  1234: fconst_1       
        //  1235: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  1238: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  1241: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1244: fconst_0       
        //  1245: fconst_1       
        //  1246: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  1249: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  1252: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1255: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1258: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1261: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1264: iconst_1       
        //  1265: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  1268: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1271: getstatic       net/minecraft/world/item/Items.GUNPOWDER:Lnet/minecraft/world/item/Item;
        //  1274: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1277: fconst_0       
        //  1278: fconst_2       
        //  1279: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  1282: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  1285: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1288: fconst_0       
        //  1289: fconst_1       
        //  1290: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  1293: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  1296: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1299: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1302: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1305: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  1308: aload_0         /* this */
        //  1309: getstatic       net/minecraft/world/entity/EntityType.GIANT:Lnet/minecraft/world/entity/EntityType;
        //  1312: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1315: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  1318: aload_0         /* this */
        //  1319: getstatic       net/minecraft/world/entity/EntityType.GUARDIAN:Lnet/minecraft/world/entity/EntityType;
        //  1322: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1325: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1328: iconst_1       
        //  1329: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  1332: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1335: getstatic       net/minecraft/world/item/Items.PRISMARINE_SHARD:Lnet/minecraft/world/item/Item;
        //  1338: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1341: fconst_0       
        //  1342: fconst_2       
        //  1343: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  1346: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  1349: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1352: fconst_0       
        //  1353: fconst_1       
        //  1354: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  1357: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  1360: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1363: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1366: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1369: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1372: iconst_1       
        //  1373: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  1376: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1379: getstatic       net/minecraft/world/item/Items.COD:Lnet/minecraft/world/item/Item;
        //  1382: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1385: iconst_2       
        //  1386: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.setWeight:(I)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1389: fconst_0       
        //  1390: fconst_1       
        //  1391: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  1394: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  1397: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1400: invokestatic    net/minecraft/world/level/storage/loot/functions/SmeltItemFunction.smelted:()Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  1403: getstatic       net/minecraft/world/level/storage/loot/LootContext$EntityTarget.THIS:Lnet/minecraft/world/level/storage/loot/LootContext$EntityTarget;
        //  1406: getstatic       net/minecraft/data/loot/EntityLoot.ENTITY_ON_FIRE:Lnet/minecraft/advancements/critereon/EntityPredicate$Builder;
        //  1409: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemEntityPropertyCondition.hasProperties:(Lnet/minecraft/world/level/storage/loot/LootContext$EntityTarget;Lnet/minecraft/advancements/critereon/EntityPredicate$Builder;)Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  1412: invokevirtual   net/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  1415: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1418: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1421: getstatic       net/minecraft/world/item/Items.PRISMARINE_CRYSTALS:Lnet/minecraft/world/item/Item;
        //  1424: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1427: iconst_2       
        //  1428: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.setWeight:(I)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1431: fconst_0       
        //  1432: fconst_1       
        //  1433: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  1436: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  1439: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1442: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1445: invokestatic    net/minecraft/world/level/storage/loot/entries/EmptyLootItem.emptyItem:()Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1448: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1451: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1454: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1457: iconst_1       
        //  1458: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  1461: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1464: getstatic       net/minecraft/world/level/storage/loot/BuiltInLootTables.FISHING_FISH:Lnet/minecraft/resources/ResourceLocation;
        //  1467: invokestatic    net/minecraft/world/level/storage/loot/entries/LootTableReference.lootTableReference:(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1470: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1473: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemKilledByPlayerCondition.killedByPlayer:()Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  1476: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1479: ldc_w           0.025
        //  1482: ldc_w           0.01
        //  1485: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost:(FF)Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  1488: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1491: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1494: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  1497: aload_0         /* this */
        //  1498: getstatic       net/minecraft/world/entity/EntityType.HORSE:Lnet/minecraft/world/entity/EntityType;
        //  1501: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1504: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1507: iconst_1       
        //  1508: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  1511: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1514: getstatic       net/minecraft/world/item/Items.LEATHER:Lnet/minecraft/world/item/Item;
        //  1517: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1520: fconst_0       
        //  1521: fconst_2       
        //  1522: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  1525: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  1528: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1531: fconst_0       
        //  1532: fconst_1       
        //  1533: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  1536: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  1539: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1542: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1545: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1548: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  1551: aload_0         /* this */
        //  1552: getstatic       net/minecraft/world/entity/EntityType.HUSK:Lnet/minecraft/world/entity/EntityType;
        //  1555: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1558: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1561: iconst_1       
        //  1562: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  1565: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1568: getstatic       net/minecraft/world/item/Items.ROTTEN_FLESH:Lnet/minecraft/world/item/Item;
        //  1571: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1574: fconst_0       
        //  1575: fconst_2       
        //  1576: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  1579: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  1582: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1585: fconst_0       
        //  1586: fconst_1       
        //  1587: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  1590: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  1593: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1596: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1599: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1602: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1605: iconst_1       
        //  1606: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  1609: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1612: getstatic       net/minecraft/world/item/Items.IRON_INGOT:Lnet/minecraft/world/item/Item;
        //  1615: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1618: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1621: getstatic       net/minecraft/world/item/Items.CARROT:Lnet/minecraft/world/item/Item;
        //  1624: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1627: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1630: getstatic       net/minecraft/world/item/Items.POTATO:Lnet/minecraft/world/item/Item;
        //  1633: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1636: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1639: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemKilledByPlayerCondition.killedByPlayer:()Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  1642: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1645: ldc_w           0.025
        //  1648: ldc_w           0.01
        //  1651: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost:(FF)Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  1654: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1657: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1660: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  1663: aload_0         /* this */
        //  1664: getstatic       net/minecraft/world/entity/EntityType.RAVAGER:Lnet/minecraft/world/entity/EntityType;
        //  1667: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1670: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1673: iconst_1       
        //  1674: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  1677: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1680: getstatic       net/minecraft/world/item/Items.SADDLE:Lnet/minecraft/world/item/Item;
        //  1683: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1686: iconst_1       
        //  1687: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  1690: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  1693: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1696: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1699: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1702: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  1705: aload_0         /* this */
        //  1706: getstatic       net/minecraft/world/entity/EntityType.ILLUSIONER:Lnet/minecraft/world/entity/EntityType;
        //  1709: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1712: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  1715: aload_0         /* this */
        //  1716: getstatic       net/minecraft/world/entity/EntityType.IRON_GOLEM:Lnet/minecraft/world/entity/EntityType;
        //  1719: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1722: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1725: iconst_1       
        //  1726: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  1729: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1732: getstatic       net/minecraft/world/level/block/Blocks.POPPY:Lnet/minecraft/world/level/block/Block;
        //  1735: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1738: fconst_0       
        //  1739: fconst_2       
        //  1740: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  1743: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  1746: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1749: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1752: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1755: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1758: iconst_1       
        //  1759: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  1762: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1765: getstatic       net/minecraft/world/item/Items.IRON_INGOT:Lnet/minecraft/world/item/Item;
        //  1768: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1771: ldc_w           3.0
        //  1774: ldc_w           5.0
        //  1777: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  1780: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  1783: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1786: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1789: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1792: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  1795: aload_0         /* this */
        //  1796: getstatic       net/minecraft/world/entity/EntityType.LLAMA:Lnet/minecraft/world/entity/EntityType;
        //  1799: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1802: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1805: iconst_1       
        //  1806: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  1809: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1812: getstatic       net/minecraft/world/item/Items.LEATHER:Lnet/minecraft/world/item/Item;
        //  1815: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1818: fconst_0       
        //  1819: fconst_2       
        //  1820: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  1823: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  1826: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1829: fconst_0       
        //  1830: fconst_1       
        //  1831: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  1834: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  1837: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1840: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1843: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1846: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  1849: aload_0         /* this */
        //  1850: getstatic       net/minecraft/world/entity/EntityType.MAGMA_CUBE:Lnet/minecraft/world/entity/EntityType;
        //  1853: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1856: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1859: iconst_1       
        //  1860: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  1863: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1866: getstatic       net/minecraft/world/item/Items.MAGMA_CREAM:Lnet/minecraft/world/item/Item;
        //  1869: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1872: ldc_w           -2.0
        //  1875: fconst_1       
        //  1876: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  1879: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  1882: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1885: fconst_0       
        //  1886: fconst_1       
        //  1887: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  1890: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  1893: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1896: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1899: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1902: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  1905: aload_0         /* this */
        //  1906: getstatic       net/minecraft/world/entity/EntityType.MULE:Lnet/minecraft/world/entity/EntityType;
        //  1909: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1912: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1915: iconst_1       
        //  1916: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  1919: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1922: getstatic       net/minecraft/world/item/Items.LEATHER:Lnet/minecraft/world/item/Item;
        //  1925: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1928: fconst_0       
        //  1929: fconst_2       
        //  1930: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  1933: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  1936: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1939: fconst_0       
        //  1940: fconst_1       
        //  1941: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  1944: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  1947: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1950: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1953: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1956: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  1959: aload_0         /* this */
        //  1960: getstatic       net/minecraft/world/entity/EntityType.MOOSHROOM:Lnet/minecraft/world/entity/EntityType;
        //  1963: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  1966: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1969: iconst_1       
        //  1970: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  1973: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  1976: getstatic       net/minecraft/world/item/Items.LEATHER:Lnet/minecraft/world/item/Item;
        //  1979: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1982: fconst_0       
        //  1983: fconst_2       
        //  1984: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  1987: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  1990: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  1993: fconst_0       
        //  1994: fconst_1       
        //  1995: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  1998: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  2001: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2004: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2007: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2010: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2013: iconst_1       
        //  2014: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  2017: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2020: getstatic       net/minecraft/world/item/Items.BEEF:Lnet/minecraft/world/item/Item;
        //  2023: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2026: fconst_1       
        //  2027: ldc_w           3.0
        //  2030: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  2033: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  2036: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2039: invokestatic    net/minecraft/world/level/storage/loot/functions/SmeltItemFunction.smelted:()Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  2042: getstatic       net/minecraft/world/level/storage/loot/LootContext$EntityTarget.THIS:Lnet/minecraft/world/level/storage/loot/LootContext$EntityTarget;
        //  2045: getstatic       net/minecraft/data/loot/EntityLoot.ENTITY_ON_FIRE:Lnet/minecraft/advancements/critereon/EntityPredicate$Builder;
        //  2048: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemEntityPropertyCondition.hasProperties:(Lnet/minecraft/world/level/storage/loot/LootContext$EntityTarget;Lnet/minecraft/advancements/critereon/EntityPredicate$Builder;)Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  2051: invokevirtual   net/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  2054: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2057: fconst_0       
        //  2058: fconst_1       
        //  2059: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  2062: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  2065: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2068: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2071: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2074: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  2077: aload_0         /* this */
        //  2078: getstatic       net/minecraft/world/entity/EntityType.OCELOT:Lnet/minecraft/world/entity/EntityType;
        //  2081: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2084: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  2087: aload_0         /* this */
        //  2088: getstatic       net/minecraft/world/entity/EntityType.PANDA:Lnet/minecraft/world/entity/EntityType;
        //  2091: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2094: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2097: iconst_1       
        //  2098: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  2101: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2104: getstatic       net/minecraft/world/level/block/Blocks.BAMBOO:Lnet/minecraft/world/level/block/Block;
        //  2107: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2110: iconst_1       
        //  2111: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  2114: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  2117: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2120: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2123: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2126: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  2129: aload_0         /* this */
        //  2130: getstatic       net/minecraft/world/entity/EntityType.PARROT:Lnet/minecraft/world/entity/EntityType;
        //  2133: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2136: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2139: iconst_1       
        //  2140: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  2143: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2146: getstatic       net/minecraft/world/item/Items.FEATHER:Lnet/minecraft/world/item/Item;
        //  2149: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2152: fconst_1       
        //  2153: fconst_2       
        //  2154: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  2157: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  2160: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2163: fconst_0       
        //  2164: fconst_1       
        //  2165: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  2168: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  2171: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2174: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2177: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2180: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  2183: aload_0         /* this */
        //  2184: getstatic       net/minecraft/world/entity/EntityType.PHANTOM:Lnet/minecraft/world/entity/EntityType;
        //  2187: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2190: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2193: iconst_1       
        //  2194: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  2197: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2200: getstatic       net/minecraft/world/item/Items.PHANTOM_MEMBRANE:Lnet/minecraft/world/item/Item;
        //  2203: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2206: fconst_0       
        //  2207: fconst_1       
        //  2208: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  2211: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  2214: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2217: fconst_0       
        //  2218: fconst_1       
        //  2219: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  2222: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  2225: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2228: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2231: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemKilledByPlayerCondition.killedByPlayer:()Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  2234: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2237: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2240: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  2243: aload_0         /* this */
        //  2244: getstatic       net/minecraft/world/entity/EntityType.PIG:Lnet/minecraft/world/entity/EntityType;
        //  2247: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2250: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2253: iconst_1       
        //  2254: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  2257: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2260: getstatic       net/minecraft/world/item/Items.PORKCHOP:Lnet/minecraft/world/item/Item;
        //  2263: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2266: fconst_1       
        //  2267: ldc_w           3.0
        //  2270: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  2273: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  2276: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2279: invokestatic    net/minecraft/world/level/storage/loot/functions/SmeltItemFunction.smelted:()Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  2282: getstatic       net/minecraft/world/level/storage/loot/LootContext$EntityTarget.THIS:Lnet/minecraft/world/level/storage/loot/LootContext$EntityTarget;
        //  2285: getstatic       net/minecraft/data/loot/EntityLoot.ENTITY_ON_FIRE:Lnet/minecraft/advancements/critereon/EntityPredicate$Builder;
        //  2288: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemEntityPropertyCondition.hasProperties:(Lnet/minecraft/world/level/storage/loot/LootContext$EntityTarget;Lnet/minecraft/advancements/critereon/EntityPredicate$Builder;)Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  2291: invokevirtual   net/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  2294: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2297: fconst_0       
        //  2298: fconst_1       
        //  2299: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  2302: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  2305: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2308: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2311: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2314: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  2317: aload_0         /* this */
        //  2318: getstatic       net/minecraft/world/entity/EntityType.PILLAGER:Lnet/minecraft/world/entity/EntityType;
        //  2321: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2324: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  2327: aload_0         /* this */
        //  2328: getstatic       net/minecraft/world/entity/EntityType.PLAYER:Lnet/minecraft/world/entity/EntityType;
        //  2331: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2334: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  2337: aload_0         /* this */
        //  2338: getstatic       net/minecraft/world/entity/EntityType.POLAR_BEAR:Lnet/minecraft/world/entity/EntityType;
        //  2341: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2344: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2347: iconst_1       
        //  2348: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  2351: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2354: getstatic       net/minecraft/world/item/Items.COD:Lnet/minecraft/world/item/Item;
        //  2357: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2360: iconst_3       
        //  2361: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.setWeight:(I)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2364: fconst_0       
        //  2365: fconst_2       
        //  2366: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  2369: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  2372: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2375: fconst_0       
        //  2376: fconst_1       
        //  2377: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  2380: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  2383: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2386: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2389: getstatic       net/minecraft/world/item/Items.SALMON:Lnet/minecraft/world/item/Item;
        //  2392: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2395: fconst_0       
        //  2396: fconst_2       
        //  2397: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  2400: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  2403: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2406: fconst_0       
        //  2407: fconst_1       
        //  2408: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  2411: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  2414: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2417: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2420: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2423: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  2426: aload_0         /* this */
        //  2427: getstatic       net/minecraft/world/entity/EntityType.PUFFERFISH:Lnet/minecraft/world/entity/EntityType;
        //  2430: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2433: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2436: iconst_1       
        //  2437: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  2440: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2443: getstatic       net/minecraft/world/item/Items.PUFFERFISH:Lnet/minecraft/world/item/Item;
        //  2446: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2449: iconst_1       
        //  2450: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  2453: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  2456: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2459: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2462: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2465: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2468: iconst_1       
        //  2469: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  2472: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2475: getstatic       net/minecraft/world/item/Items.BONE_MEAL:Lnet/minecraft/world/item/Item;
        //  2478: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2481: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2484: ldc             0.05
        //  2486: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemRandomChanceCondition.randomChance:(F)Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  2489: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2492: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2495: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  2498: aload_0         /* this */
        //  2499: getstatic       net/minecraft/world/entity/EntityType.RABBIT:Lnet/minecraft/world/entity/EntityType;
        //  2502: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2505: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2508: iconst_1       
        //  2509: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  2512: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2515: getstatic       net/minecraft/world/item/Items.RABBIT_HIDE:Lnet/minecraft/world/item/Item;
        //  2518: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2521: fconst_0       
        //  2522: fconst_1       
        //  2523: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  2526: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  2529: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2532: fconst_0       
        //  2533: fconst_1       
        //  2534: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  2537: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  2540: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2543: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2546: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2549: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2552: iconst_1       
        //  2553: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  2556: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2559: getstatic       net/minecraft/world/item/Items.RABBIT:Lnet/minecraft/world/item/Item;
        //  2562: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2565: fconst_0       
        //  2566: fconst_1       
        //  2567: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  2570: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  2573: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2576: invokestatic    net/minecraft/world/level/storage/loot/functions/SmeltItemFunction.smelted:()Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  2579: getstatic       net/minecraft/world/level/storage/loot/LootContext$EntityTarget.THIS:Lnet/minecraft/world/level/storage/loot/LootContext$EntityTarget;
        //  2582: getstatic       net/minecraft/data/loot/EntityLoot.ENTITY_ON_FIRE:Lnet/minecraft/advancements/critereon/EntityPredicate$Builder;
        //  2585: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemEntityPropertyCondition.hasProperties:(Lnet/minecraft/world/level/storage/loot/LootContext$EntityTarget;Lnet/minecraft/advancements/critereon/EntityPredicate$Builder;)Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  2588: invokevirtual   net/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  2591: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2594: fconst_0       
        //  2595: fconst_1       
        //  2596: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  2599: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  2602: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2605: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2608: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2611: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2614: iconst_1       
        //  2615: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  2618: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2621: getstatic       net/minecraft/world/item/Items.RABBIT_FOOT:Lnet/minecraft/world/item/Item;
        //  2624: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2627: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2630: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemKilledByPlayerCondition.killedByPlayer:()Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  2633: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2636: ldc_w           0.1
        //  2639: ldc_w           0.03
        //  2642: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost:(FF)Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  2645: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2648: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2651: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  2654: aload_0         /* this */
        //  2655: getstatic       net/minecraft/world/entity/EntityType.SALMON:Lnet/minecraft/world/entity/EntityType;
        //  2658: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2661: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2664: iconst_1       
        //  2665: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  2668: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2671: getstatic       net/minecraft/world/item/Items.SALMON:Lnet/minecraft/world/item/Item;
        //  2674: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2677: invokestatic    net/minecraft/world/level/storage/loot/functions/SmeltItemFunction.smelted:()Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  2680: getstatic       net/minecraft/world/level/storage/loot/LootContext$EntityTarget.THIS:Lnet/minecraft/world/level/storage/loot/LootContext$EntityTarget;
        //  2683: getstatic       net/minecraft/data/loot/EntityLoot.ENTITY_ON_FIRE:Lnet/minecraft/advancements/critereon/EntityPredicate$Builder;
        //  2686: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemEntityPropertyCondition.hasProperties:(Lnet/minecraft/world/level/storage/loot/LootContext$EntityTarget;Lnet/minecraft/advancements/critereon/EntityPredicate$Builder;)Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  2689: invokevirtual   net/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  2692: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2695: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2698: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2701: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2704: iconst_1       
        //  2705: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  2708: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2711: getstatic       net/minecraft/world/item/Items.BONE_MEAL:Lnet/minecraft/world/item/Item;
        //  2714: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2717: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2720: ldc             0.05
        //  2722: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemRandomChanceCondition.randomChance:(F)Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  2725: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2728: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2731: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  2734: aload_0         /* this */
        //  2735: getstatic       net/minecraft/world/entity/EntityType.SHEEP:Lnet/minecraft/world/entity/EntityType;
        //  2738: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2741: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2744: iconst_1       
        //  2745: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  2748: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2751: getstatic       net/minecraft/world/item/Items.MUTTON:Lnet/minecraft/world/item/Item;
        //  2754: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2757: fconst_1       
        //  2758: fconst_2       
        //  2759: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  2762: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  2765: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2768: invokestatic    net/minecraft/world/level/storage/loot/functions/SmeltItemFunction.smelted:()Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  2771: getstatic       net/minecraft/world/level/storage/loot/LootContext$EntityTarget.THIS:Lnet/minecraft/world/level/storage/loot/LootContext$EntityTarget;
        //  2774: getstatic       net/minecraft/data/loot/EntityLoot.ENTITY_ON_FIRE:Lnet/minecraft/advancements/critereon/EntityPredicate$Builder;
        //  2777: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemEntityPropertyCondition.hasProperties:(Lnet/minecraft/world/level/storage/loot/LootContext$EntityTarget;Lnet/minecraft/advancements/critereon/EntityPredicate$Builder;)Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  2780: invokevirtual   net/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  2783: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2786: fconst_0       
        //  2787: fconst_1       
        //  2788: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  2791: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  2794: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  2797: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  2800: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2803: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  2806: aload_0         /* this */
        //  2807: getstatic       net/minecraft/world/level/storage/loot/BuiltInLootTables.SHEEP_BLACK:Lnet/minecraft/resources/ResourceLocation;
        //  2810: getstatic       net/minecraft/world/level/block/Blocks.BLACK_WOOL:Lnet/minecraft/world/level/block/Block;
        //  2813: invokestatic    net/minecraft/data/loot/EntityLoot.createSheepTable:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2816: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  2819: aload_0         /* this */
        //  2820: getstatic       net/minecraft/world/level/storage/loot/BuiltInLootTables.SHEEP_BLUE:Lnet/minecraft/resources/ResourceLocation;
        //  2823: getstatic       net/minecraft/world/level/block/Blocks.BLUE_WOOL:Lnet/minecraft/world/level/block/Block;
        //  2826: invokestatic    net/minecraft/data/loot/EntityLoot.createSheepTable:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2829: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  2832: aload_0         /* this */
        //  2833: getstatic       net/minecraft/world/level/storage/loot/BuiltInLootTables.SHEEP_BROWN:Lnet/minecraft/resources/ResourceLocation;
        //  2836: getstatic       net/minecraft/world/level/block/Blocks.BROWN_WOOL:Lnet/minecraft/world/level/block/Block;
        //  2839: invokestatic    net/minecraft/data/loot/EntityLoot.createSheepTable:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2842: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  2845: aload_0         /* this */
        //  2846: getstatic       net/minecraft/world/level/storage/loot/BuiltInLootTables.SHEEP_CYAN:Lnet/minecraft/resources/ResourceLocation;
        //  2849: getstatic       net/minecraft/world/level/block/Blocks.CYAN_WOOL:Lnet/minecraft/world/level/block/Block;
        //  2852: invokestatic    net/minecraft/data/loot/EntityLoot.createSheepTable:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2855: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  2858: aload_0         /* this */
        //  2859: getstatic       net/minecraft/world/level/storage/loot/BuiltInLootTables.SHEEP_GRAY:Lnet/minecraft/resources/ResourceLocation;
        //  2862: getstatic       net/minecraft/world/level/block/Blocks.GRAY_WOOL:Lnet/minecraft/world/level/block/Block;
        //  2865: invokestatic    net/minecraft/data/loot/EntityLoot.createSheepTable:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2868: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  2871: aload_0         /* this */
        //  2872: getstatic       net/minecraft/world/level/storage/loot/BuiltInLootTables.SHEEP_GREEN:Lnet/minecraft/resources/ResourceLocation;
        //  2875: getstatic       net/minecraft/world/level/block/Blocks.GREEN_WOOL:Lnet/minecraft/world/level/block/Block;
        //  2878: invokestatic    net/minecraft/data/loot/EntityLoot.createSheepTable:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2881: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  2884: aload_0         /* this */
        //  2885: getstatic       net/minecraft/world/level/storage/loot/BuiltInLootTables.SHEEP_LIGHT_BLUE:Lnet/minecraft/resources/ResourceLocation;
        //  2888: getstatic       net/minecraft/world/level/block/Blocks.LIGHT_BLUE_WOOL:Lnet/minecraft/world/level/block/Block;
        //  2891: invokestatic    net/minecraft/data/loot/EntityLoot.createSheepTable:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2894: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  2897: aload_0         /* this */
        //  2898: getstatic       net/minecraft/world/level/storage/loot/BuiltInLootTables.SHEEP_LIGHT_GRAY:Lnet/minecraft/resources/ResourceLocation;
        //  2901: getstatic       net/minecraft/world/level/block/Blocks.LIGHT_GRAY_WOOL:Lnet/minecraft/world/level/block/Block;
        //  2904: invokestatic    net/minecraft/data/loot/EntityLoot.createSheepTable:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2907: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  2910: aload_0         /* this */
        //  2911: getstatic       net/minecraft/world/level/storage/loot/BuiltInLootTables.SHEEP_LIME:Lnet/minecraft/resources/ResourceLocation;
        //  2914: getstatic       net/minecraft/world/level/block/Blocks.LIME_WOOL:Lnet/minecraft/world/level/block/Block;
        //  2917: invokestatic    net/minecraft/data/loot/EntityLoot.createSheepTable:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2920: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  2923: aload_0         /* this */
        //  2924: getstatic       net/minecraft/world/level/storage/loot/BuiltInLootTables.SHEEP_MAGENTA:Lnet/minecraft/resources/ResourceLocation;
        //  2927: getstatic       net/minecraft/world/level/block/Blocks.MAGENTA_WOOL:Lnet/minecraft/world/level/block/Block;
        //  2930: invokestatic    net/minecraft/data/loot/EntityLoot.createSheepTable:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2933: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  2936: aload_0         /* this */
        //  2937: getstatic       net/minecraft/world/level/storage/loot/BuiltInLootTables.SHEEP_ORANGE:Lnet/minecraft/resources/ResourceLocation;
        //  2940: getstatic       net/minecraft/world/level/block/Blocks.ORANGE_WOOL:Lnet/minecraft/world/level/block/Block;
        //  2943: invokestatic    net/minecraft/data/loot/EntityLoot.createSheepTable:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2946: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  2949: aload_0         /* this */
        //  2950: getstatic       net/minecraft/world/level/storage/loot/BuiltInLootTables.SHEEP_PINK:Lnet/minecraft/resources/ResourceLocation;
        //  2953: getstatic       net/minecraft/world/level/block/Blocks.PINK_WOOL:Lnet/minecraft/world/level/block/Block;
        //  2956: invokestatic    net/minecraft/data/loot/EntityLoot.createSheepTable:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2959: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  2962: aload_0         /* this */
        //  2963: getstatic       net/minecraft/world/level/storage/loot/BuiltInLootTables.SHEEP_PURPLE:Lnet/minecraft/resources/ResourceLocation;
        //  2966: getstatic       net/minecraft/world/level/block/Blocks.PURPLE_WOOL:Lnet/minecraft/world/level/block/Block;
        //  2969: invokestatic    net/minecraft/data/loot/EntityLoot.createSheepTable:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2972: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  2975: aload_0         /* this */
        //  2976: getstatic       net/minecraft/world/level/storage/loot/BuiltInLootTables.SHEEP_RED:Lnet/minecraft/resources/ResourceLocation;
        //  2979: getstatic       net/minecraft/world/level/block/Blocks.RED_WOOL:Lnet/minecraft/world/level/block/Block;
        //  2982: invokestatic    net/minecraft/data/loot/EntityLoot.createSheepTable:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2985: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  2988: aload_0         /* this */
        //  2989: getstatic       net/minecraft/world/level/storage/loot/BuiltInLootTables.SHEEP_WHITE:Lnet/minecraft/resources/ResourceLocation;
        //  2992: getstatic       net/minecraft/world/level/block/Blocks.WHITE_WOOL:Lnet/minecraft/world/level/block/Block;
        //  2995: invokestatic    net/minecraft/data/loot/EntityLoot.createSheepTable:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  2998: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  3001: aload_0         /* this */
        //  3002: getstatic       net/minecraft/world/level/storage/loot/BuiltInLootTables.SHEEP_YELLOW:Lnet/minecraft/resources/ResourceLocation;
        //  3005: getstatic       net/minecraft/world/level/block/Blocks.YELLOW_WOOL:Lnet/minecraft/world/level/block/Block;
        //  3008: invokestatic    net/minecraft/data/loot/EntityLoot.createSheepTable:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3011: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  3014: aload_0         /* this */
        //  3015: getstatic       net/minecraft/world/entity/EntityType.SHULKER:Lnet/minecraft/world/entity/EntityType;
        //  3018: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3021: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3024: iconst_1       
        //  3025: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  3028: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3031: getstatic       net/minecraft/world/item/Items.SHULKER_SHELL:Lnet/minecraft/world/item/Item;
        //  3034: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3037: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3040: ldc_w           0.5
        //  3043: ldc_w           0.0625
        //  3046: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost:(FF)Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  3049: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3052: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3055: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  3058: aload_0         /* this */
        //  3059: getstatic       net/minecraft/world/entity/EntityType.SILVERFISH:Lnet/minecraft/world/entity/EntityType;
        //  3062: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3065: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  3068: aload_0         /* this */
        //  3069: getstatic       net/minecraft/world/entity/EntityType.SKELETON:Lnet/minecraft/world/entity/EntityType;
        //  3072: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3075: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3078: iconst_1       
        //  3079: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  3082: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3085: getstatic       net/minecraft/world/item/Items.ARROW:Lnet/minecraft/world/item/Item;
        //  3088: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3091: fconst_0       
        //  3092: fconst_2       
        //  3093: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  3096: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  3099: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3102: fconst_0       
        //  3103: fconst_1       
        //  3104: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  3107: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  3110: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3113: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3116: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3119: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3122: iconst_1       
        //  3123: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  3126: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3129: getstatic       net/minecraft/world/item/Items.BONE:Lnet/minecraft/world/item/Item;
        //  3132: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3135: fconst_0       
        //  3136: fconst_2       
        //  3137: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  3140: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  3143: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3146: fconst_0       
        //  3147: fconst_1       
        //  3148: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  3151: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  3154: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3157: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3160: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3163: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  3166: aload_0         /* this */
        //  3167: getstatic       net/minecraft/world/entity/EntityType.SKELETON_HORSE:Lnet/minecraft/world/entity/EntityType;
        //  3170: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3173: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3176: iconst_1       
        //  3177: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  3180: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3183: getstatic       net/minecraft/world/item/Items.BONE:Lnet/minecraft/world/item/Item;
        //  3186: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3189: fconst_0       
        //  3190: fconst_2       
        //  3191: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  3194: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  3197: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3200: fconst_0       
        //  3201: fconst_1       
        //  3202: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  3205: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  3208: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3211: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3214: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3217: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  3220: aload_0         /* this */
        //  3221: getstatic       net/minecraft/world/entity/EntityType.SLIME:Lnet/minecraft/world/entity/EntityType;
        //  3224: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3227: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3230: iconst_1       
        //  3231: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  3234: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3237: getstatic       net/minecraft/world/item/Items.SLIME_BALL:Lnet/minecraft/world/item/Item;
        //  3240: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3243: fconst_0       
        //  3244: fconst_2       
        //  3245: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  3248: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  3251: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3254: fconst_0       
        //  3255: fconst_1       
        //  3256: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  3259: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  3262: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3265: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3268: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3271: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  3274: aload_0         /* this */
        //  3275: getstatic       net/minecraft/world/entity/EntityType.SNOW_GOLEM:Lnet/minecraft/world/entity/EntityType;
        //  3278: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3281: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3284: iconst_1       
        //  3285: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  3288: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3291: getstatic       net/minecraft/world/item/Items.SNOWBALL:Lnet/minecraft/world/item/Item;
        //  3294: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3297: fconst_0       
        //  3298: ldc_w           15.0
        //  3301: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  3304: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  3307: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3310: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3313: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3316: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  3319: aload_0         /* this */
        //  3320: getstatic       net/minecraft/world/entity/EntityType.SPIDER:Lnet/minecraft/world/entity/EntityType;
        //  3323: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3326: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3329: iconst_1       
        //  3330: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  3333: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3336: getstatic       net/minecraft/world/item/Items.STRING:Lnet/minecraft/world/item/Item;
        //  3339: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3342: fconst_0       
        //  3343: fconst_2       
        //  3344: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  3347: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  3350: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3353: fconst_0       
        //  3354: fconst_1       
        //  3355: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  3358: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  3361: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3364: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3367: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3370: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3373: iconst_1       
        //  3374: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  3377: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3380: getstatic       net/minecraft/world/item/Items.SPIDER_EYE:Lnet/minecraft/world/item/Item;
        //  3383: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3386: ldc             -1.0
        //  3388: fconst_1       
        //  3389: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  3392: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  3395: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3398: fconst_0       
        //  3399: fconst_1       
        //  3400: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  3403: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  3406: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3409: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3412: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemKilledByPlayerCondition.killedByPlayer:()Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  3415: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3418: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3421: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  3424: aload_0         /* this */
        //  3425: getstatic       net/minecraft/world/entity/EntityType.SQUID:Lnet/minecraft/world/entity/EntityType;
        //  3428: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3431: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3434: iconst_1       
        //  3435: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  3438: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3441: getstatic       net/minecraft/world/item/Items.INK_SAC:Lnet/minecraft/world/item/Item;
        //  3444: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3447: fconst_1       
        //  3448: ldc_w           3.0
        //  3451: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  3454: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  3457: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3460: fconst_0       
        //  3461: fconst_1       
        //  3462: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  3465: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  3468: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3471: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3474: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3477: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  3480: aload_0         /* this */
        //  3481: getstatic       net/minecraft/world/entity/EntityType.STRAY:Lnet/minecraft/world/entity/EntityType;
        //  3484: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3487: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3490: iconst_1       
        //  3491: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  3494: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3497: getstatic       net/minecraft/world/item/Items.ARROW:Lnet/minecraft/world/item/Item;
        //  3500: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3503: fconst_0       
        //  3504: fconst_2       
        //  3505: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  3508: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  3511: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3514: fconst_0       
        //  3515: fconst_1       
        //  3516: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  3519: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  3522: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3525: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3528: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3531: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3534: iconst_1       
        //  3535: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  3538: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3541: getstatic       net/minecraft/world/item/Items.BONE:Lnet/minecraft/world/item/Item;
        //  3544: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3547: fconst_0       
        //  3548: fconst_2       
        //  3549: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  3552: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  3555: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3558: fconst_0       
        //  3559: fconst_1       
        //  3560: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  3563: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  3566: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3569: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3572: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3575: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3578: iconst_1       
        //  3579: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  3582: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3585: getstatic       net/minecraft/world/item/Items.TIPPED_ARROW:Lnet/minecraft/world/item/Item;
        //  3588: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3591: fconst_0       
        //  3592: fconst_1       
        //  3593: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  3596: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  3599: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3602: fconst_0       
        //  3603: fconst_1       
        //  3604: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  3607: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  3610: iconst_1       
        //  3611: invokevirtual   net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder.setLimit:(I)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  3614: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3617: new             Lnet/minecraft/nbt/CompoundTag;
        //  3620: dup            
        //  3621: invokespecial   net/minecraft/nbt/CompoundTag.<init>:()V
        //  3624: invokedynamic   BootstrapMethod #0, accept:()Ljava/util/function/Consumer;
        //  3629: invokestatic    net/minecraft/Util.make:(Ljava/lang/Object;Ljava/util/function/Consumer;)Ljava/lang/Object;
        //  3632: checkcast       Lnet/minecraft/nbt/CompoundTag;
        //  3635: invokestatic    net/minecraft/world/level/storage/loot/functions/SetNbtFunction.setTag:(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  3638: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3641: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3644: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemKilledByPlayerCondition.killedByPlayer:()Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  3647: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3650: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3653: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  3656: aload_0         /* this */
        //  3657: getstatic       net/minecraft/world/entity/EntityType.TRADER_LLAMA:Lnet/minecraft/world/entity/EntityType;
        //  3660: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3663: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3666: iconst_1       
        //  3667: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  3670: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3673: getstatic       net/minecraft/world/item/Items.LEATHER:Lnet/minecraft/world/item/Item;
        //  3676: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3679: fconst_0       
        //  3680: fconst_2       
        //  3681: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  3684: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  3687: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3690: fconst_0       
        //  3691: fconst_1       
        //  3692: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  3695: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  3698: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3701: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3704: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3707: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  3710: aload_0         /* this */
        //  3711: getstatic       net/minecraft/world/entity/EntityType.TROPICAL_FISH:Lnet/minecraft/world/entity/EntityType;
        //  3714: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3717: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3720: iconst_1       
        //  3721: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  3724: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3727: getstatic       net/minecraft/world/item/Items.TROPICAL_FISH:Lnet/minecraft/world/item/Item;
        //  3730: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3733: iconst_1       
        //  3734: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  3737: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  3740: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3743: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3746: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3749: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3752: iconst_1       
        //  3753: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  3756: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3759: getstatic       net/minecraft/world/item/Items.BONE_MEAL:Lnet/minecraft/world/item/Item;
        //  3762: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3765: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3768: ldc             0.05
        //  3770: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemRandomChanceCondition.randomChance:(F)Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  3773: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3776: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3779: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  3782: aload_0         /* this */
        //  3783: getstatic       net/minecraft/world/entity/EntityType.TURTLE:Lnet/minecraft/world/entity/EntityType;
        //  3786: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3789: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3792: iconst_1       
        //  3793: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  3796: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3799: getstatic       net/minecraft/world/level/block/Blocks.SEAGRASS:Lnet/minecraft/world/level/block/Block;
        //  3802: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3805: iconst_3       
        //  3806: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.setWeight:(I)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3809: fconst_0       
        //  3810: fconst_2       
        //  3811: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  3814: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  3817: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3820: fconst_0       
        //  3821: fconst_1       
        //  3822: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  3825: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  3828: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3831: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3834: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3837: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3840: iconst_1       
        //  3841: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  3844: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3847: getstatic       net/minecraft/world/item/Items.BOWL:Lnet/minecraft/world/item/Item;
        //  3850: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3853: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3856: invokestatic    net/minecraft/advancements/critereon/DamageSourcePredicate$Builder.damageType:()Lnet/minecraft/advancements/critereon/DamageSourcePredicate$Builder;
        //  3859: iconst_1       
        //  3860: invokestatic    java/lang/Boolean.valueOf:(Z)Ljava/lang/Boolean;
        //  3863: invokevirtual   net/minecraft/advancements/critereon/DamageSourcePredicate$Builder.isLightning:(Ljava/lang/Boolean;)Lnet/minecraft/advancements/critereon/DamageSourcePredicate$Builder;
        //  3866: invokestatic    net/minecraft/world/level/storage/loot/predicates/DamageSourceCondition.hasDamageSource:(Lnet/minecraft/advancements/critereon/DamageSourcePredicate$Builder;)Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  3869: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3872: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3875: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  3878: aload_0         /* this */
        //  3879: getstatic       net/minecraft/world/entity/EntityType.VEX:Lnet/minecraft/world/entity/EntityType;
        //  3882: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3885: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  3888: aload_0         /* this */
        //  3889: getstatic       net/minecraft/world/entity/EntityType.VILLAGER:Lnet/minecraft/world/entity/EntityType;
        //  3892: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3895: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  3898: aload_0         /* this */
        //  3899: getstatic       net/minecraft/world/entity/EntityType.WANDERING_TRADER:Lnet/minecraft/world/entity/EntityType;
        //  3902: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3905: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  3908: aload_0         /* this */
        //  3909: getstatic       net/minecraft/world/entity/EntityType.VINDICATOR:Lnet/minecraft/world/entity/EntityType;
        //  3912: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3915: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3918: iconst_1       
        //  3919: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  3922: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3925: getstatic       net/minecraft/world/item/Items.EMERALD:Lnet/minecraft/world/item/Item;
        //  3928: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3931: fconst_0       
        //  3932: fconst_1       
        //  3933: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  3936: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  3939: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3942: fconst_0       
        //  3943: fconst_1       
        //  3944: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  3947: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  3950: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3953: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3956: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemKilledByPlayerCondition.killedByPlayer:()Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  3959: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3962: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3965: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  3968: aload_0         /* this */
        //  3969: getstatic       net/minecraft/world/entity/EntityType.WITCH:Lnet/minecraft/world/entity/EntityType;
        //  3972: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  3975: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3978: fconst_1       
        //  3979: ldc_w           3.0
        //  3982: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  3985: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  3988: getstatic       net/minecraft/world/item/Items.GLOWSTONE_DUST:Lnet/minecraft/world/item/Item;
        //  3991: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  3994: fconst_0       
        //  3995: fconst_2       
        //  3996: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  3999: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  4002: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4005: fconst_0       
        //  4006: fconst_1       
        //  4007: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  4010: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  4013: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4016: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4019: getstatic       net/minecraft/world/item/Items.SUGAR:Lnet/minecraft/world/item/Item;
        //  4022: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4025: fconst_0       
        //  4026: fconst_2       
        //  4027: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  4030: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  4033: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4036: fconst_0       
        //  4037: fconst_1       
        //  4038: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  4041: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  4044: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4047: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4050: getstatic       net/minecraft/world/item/Items.REDSTONE:Lnet/minecraft/world/item/Item;
        //  4053: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4056: fconst_0       
        //  4057: fconst_2       
        //  4058: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  4061: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  4064: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4067: fconst_0       
        //  4068: fconst_1       
        //  4069: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  4072: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  4075: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4078: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4081: getstatic       net/minecraft/world/item/Items.SPIDER_EYE:Lnet/minecraft/world/item/Item;
        //  4084: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4087: fconst_0       
        //  4088: fconst_2       
        //  4089: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  4092: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  4095: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4098: fconst_0       
        //  4099: fconst_1       
        //  4100: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  4103: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  4106: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4109: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4112: getstatic       net/minecraft/world/item/Items.GLASS_BOTTLE:Lnet/minecraft/world/item/Item;
        //  4115: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4118: fconst_0       
        //  4119: fconst_2       
        //  4120: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  4123: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  4126: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4129: fconst_0       
        //  4130: fconst_1       
        //  4131: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  4134: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  4137: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4140: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4143: getstatic       net/minecraft/world/item/Items.GUNPOWDER:Lnet/minecraft/world/item/Item;
        //  4146: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4149: fconst_0       
        //  4150: fconst_2       
        //  4151: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  4154: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  4157: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4160: fconst_0       
        //  4161: fconst_1       
        //  4162: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  4165: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  4168: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4171: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4174: getstatic       net/minecraft/world/item/Items.STICK:Lnet/minecraft/world/item/Item;
        //  4177: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4180: iconst_2       
        //  4181: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.setWeight:(I)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4184: fconst_0       
        //  4185: fconst_2       
        //  4186: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  4189: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  4192: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4195: fconst_0       
        //  4196: fconst_1       
        //  4197: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  4200: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  4203: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4206: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4209: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  4212: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  4215: aload_0         /* this */
        //  4216: getstatic       net/minecraft/world/entity/EntityType.WITHER:Lnet/minecraft/world/entity/EntityType;
        //  4219: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  4222: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  4225: aload_0         /* this */
        //  4226: getstatic       net/minecraft/world/entity/EntityType.WITHER_SKELETON:Lnet/minecraft/world/entity/EntityType;
        //  4229: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  4232: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4235: iconst_1       
        //  4236: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  4239: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4242: getstatic       net/minecraft/world/item/Items.COAL:Lnet/minecraft/world/item/Item;
        //  4245: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4248: ldc             -1.0
        //  4250: fconst_1       
        //  4251: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  4254: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  4257: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4260: fconst_0       
        //  4261: fconst_1       
        //  4262: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  4265: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  4268: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4271: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4274: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  4277: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4280: iconst_1       
        //  4281: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  4284: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4287: getstatic       net/minecraft/world/item/Items.BONE:Lnet/minecraft/world/item/Item;
        //  4290: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4293: fconst_0       
        //  4294: fconst_2       
        //  4295: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  4298: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  4301: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4304: fconst_0       
        //  4305: fconst_1       
        //  4306: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  4309: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  4312: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4315: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4318: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  4321: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4324: iconst_1       
        //  4325: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  4328: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4331: getstatic       net/minecraft/world/level/block/Blocks.WITHER_SKELETON_SKULL:Lnet/minecraft/world/level/block/Block;
        //  4334: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4337: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4340: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemKilledByPlayerCondition.killedByPlayer:()Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  4343: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4346: ldc_w           0.025
        //  4349: ldc_w           0.01
        //  4352: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost:(FF)Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  4355: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4358: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  4361: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  4364: aload_0         /* this */
        //  4365: getstatic       net/minecraft/world/entity/EntityType.WOLF:Lnet/minecraft/world/entity/EntityType;
        //  4368: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  4371: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  4374: aload_0         /* this */
        //  4375: getstatic       net/minecraft/world/entity/EntityType.ZOMBIE:Lnet/minecraft/world/entity/EntityType;
        //  4378: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  4381: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4384: iconst_1       
        //  4385: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  4388: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4391: getstatic       net/minecraft/world/item/Items.ROTTEN_FLESH:Lnet/minecraft/world/item/Item;
        //  4394: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4397: fconst_0       
        //  4398: fconst_2       
        //  4399: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  4402: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  4405: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4408: fconst_0       
        //  4409: fconst_1       
        //  4410: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  4413: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  4416: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4419: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4422: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  4425: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4428: iconst_1       
        //  4429: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  4432: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4435: getstatic       net/minecraft/world/item/Items.IRON_INGOT:Lnet/minecraft/world/item/Item;
        //  4438: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4441: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4444: getstatic       net/minecraft/world/item/Items.CARROT:Lnet/minecraft/world/item/Item;
        //  4447: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4450: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4453: getstatic       net/minecraft/world/item/Items.POTATO:Lnet/minecraft/world/item/Item;
        //  4456: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4459: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4462: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemKilledByPlayerCondition.killedByPlayer:()Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  4465: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4468: ldc_w           0.025
        //  4471: ldc_w           0.01
        //  4474: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost:(FF)Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  4477: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4480: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  4483: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  4486: aload_0         /* this */
        //  4487: getstatic       net/minecraft/world/entity/EntityType.ZOMBIE_HORSE:Lnet/minecraft/world/entity/EntityType;
        //  4490: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  4493: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4496: iconst_1       
        //  4497: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  4500: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4503: getstatic       net/minecraft/world/item/Items.ROTTEN_FLESH:Lnet/minecraft/world/item/Item;
        //  4506: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4509: fconst_0       
        //  4510: fconst_2       
        //  4511: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  4514: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  4517: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4520: fconst_0       
        //  4521: fconst_1       
        //  4522: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  4525: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  4528: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4531: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4534: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  4537: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  4540: aload_0         /* this */
        //  4541: getstatic       net/minecraft/world/entity/EntityType.ZOMBIE_PIGMAN:Lnet/minecraft/world/entity/EntityType;
        //  4544: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  4547: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4550: iconst_1       
        //  4551: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  4554: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4557: getstatic       net/minecraft/world/item/Items.ROTTEN_FLESH:Lnet/minecraft/world/item/Item;
        //  4560: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4563: fconst_0       
        //  4564: fconst_1       
        //  4565: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  4568: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  4571: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4574: fconst_0       
        //  4575: fconst_1       
        //  4576: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  4579: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  4582: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4585: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4588: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  4591: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4594: iconst_1       
        //  4595: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  4598: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4601: getstatic       net/minecraft/world/item/Items.GOLD_NUGGET:Lnet/minecraft/world/item/Item;
        //  4604: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4607: fconst_0       
        //  4608: fconst_1       
        //  4609: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  4612: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  4615: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4618: fconst_0       
        //  4619: fconst_1       
        //  4620: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  4623: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  4626: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4629: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4632: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  4635: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4638: iconst_1       
        //  4639: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  4642: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4645: getstatic       net/minecraft/world/item/Items.GOLD_INGOT:Lnet/minecraft/world/item/Item;
        //  4648: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4651: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4654: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemKilledByPlayerCondition.killedByPlayer:()Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  4657: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4660: ldc_w           0.025
        //  4663: ldc_w           0.01
        //  4666: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost:(FF)Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  4669: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4672: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  4675: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  4678: aload_0         /* this */
        //  4679: getstatic       net/minecraft/world/entity/EntityType.ZOMBIE_VILLAGER:Lnet/minecraft/world/entity/EntityType;
        //  4682: invokestatic    net/minecraft/world/level/storage/loot/LootTable.lootTable:()Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  4685: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4688: iconst_1       
        //  4689: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  4692: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4695: getstatic       net/minecraft/world/item/Items.ROTTEN_FLESH:Lnet/minecraft/world/item/Item;
        //  4698: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4701: fconst_0       
        //  4702: fconst_2       
        //  4703: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  4706: invokestatic    net/minecraft/world/level/storage/loot/functions/SetItemCountFunction.setCount:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/functions/LootItemConditionalFunction$Builder;
        //  4709: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4712: fconst_0       
        //  4713: fconst_1       
        //  4714: invokestatic    net/minecraft/world/level/storage/loot/RandomValueBounds.between:(FF)Lnet/minecraft/world/level/storage/loot/RandomValueBounds;
        //  4717: invokestatic    net/minecraft/world/level/storage/loot/functions/LootingEnchantFunction.lootingMultiplier:(Lnet/minecraft/world/level/storage/loot/RandomValueBounds;)Lnet/minecraft/world/level/storage/loot/functions/LootingEnchantFunction$Builder;
        //  4720: invokevirtual   net/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder.apply:(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4723: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4726: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  4729: invokestatic    net/minecraft/world/level/storage/loot/LootPool.lootPool:()Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4732: iconst_1       
        //  4733: invokestatic    net/minecraft/world/level/storage/loot/ConstantIntValue.exactly:(I)Lnet/minecraft/world/level/storage/loot/ConstantIntValue;
        //  4736: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.setRolls:(Lnet/minecraft/world/level/storage/loot/RandomIntGenerator;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4739: getstatic       net/minecraft/world/item/Items.IRON_INGOT:Lnet/minecraft/world/item/Item;
        //  4742: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4745: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4748: getstatic       net/minecraft/world/item/Items.CARROT:Lnet/minecraft/world/item/Item;
        //  4751: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4754: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4757: getstatic       net/minecraft/world/item/Items.POTATO:Lnet/minecraft/world/item/Item;
        //  4760: invokestatic    net/minecraft/world/level/storage/loot/entries/LootItem.lootTableItem:(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;
        //  4763: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.add:(Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntryContainer$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4766: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemKilledByPlayerCondition.killedByPlayer:()Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  4769: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4772: ldc_w           0.025
        //  4775: ldc_w           0.01
        //  4778: invokestatic    net/minecraft/world/level/storage/loot/predicates/LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost:(FF)Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;
        //  4781: invokevirtual   net/minecraft/world/level/storage/loot/LootPool$Builder.when:(Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition$Builder;)Lnet/minecraft/world/level/storage/loot/LootPool$Builder;
        //  4784: invokevirtual   net/minecraft/world/level/storage/loot/LootTable$Builder.withPool:(Lnet/minecraft/world/level/storage/loot/LootPool$Builder;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  4787: invokespecial   net/minecraft/data/loot/EntityLoot.add:(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V
        //  4790: invokestatic    com/google/common/collect/Sets.newHashSet:()Ljava/util/HashSet;
        //  4793: astore_2        /* set3 */
        //  4794: getstatic       net/minecraft/core/Registry.ENTITY_TYPE:Lnet/minecraft/core/DefaultedRegistry;
        //  4797: invokevirtual   net/minecraft/core/DefaultedRegistry.iterator:()Ljava/util/Iterator;
        //  4800: astore_3       
        //  4801: aload_3        
        //  4802: invokeinterface java/util/Iterator.hasNext:()Z
        //  4807: ifeq            4998
        //  4810: aload_3        
        //  4811: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //  4816: checkcast       Lnet/minecraft/world/entity/EntityType;
        //  4819: astore          ais5
        //  4821: aload           ais5
        //  4823: invokevirtual   net/minecraft/world/entity/EntityType.getDefaultLootTable:()Lnet/minecraft/resources/ResourceLocation;
        //  4826: astore          qv6
        //  4828: getstatic       net/minecraft/data/loot/EntityLoot.SPECIAL_LOOT_TABLE_TYPES:Ljava/util/Set;
        //  4831: aload           ais5
        //  4833: invokeinterface java/util/Set.contains:(Ljava/lang/Object;)Z
        //  4838: ifne            4852
        //  4841: aload           ais5
        //  4843: invokevirtual   net/minecraft/world/entity/EntityType.getCategory:()Lnet/minecraft/world/entity/MobCategory;
        //  4846: getstatic       net/minecraft/world/entity/MobCategory.MISC:Lnet/minecraft/world/entity/MobCategory;
        //  4849: if_acmpeq       4939
        //  4852: aload           qv6
        //  4854: getstatic       net/minecraft/world/level/storage/loot/BuiltInLootTables.EMPTY:Lnet/minecraft/resources/ResourceLocation;
        //  4857: if_acmpeq       4995
        //  4860: aload_2         /* set3 */
        //  4861: aload           qv6
        //  4863: invokeinterface java/util/Set.add:(Ljava/lang/Object;)Z
        //  4868: ifeq            4995
        //  4871: aload_0         /* this */
        //  4872: getfield        net/minecraft/data/loot/EntityLoot.map:Ljava/util/Map;
        //  4875: aload           qv6
        //  4877: invokeinterface java/util/Map.remove:(Ljava/lang/Object;)Ljava/lang/Object;
        //  4882: checkcast       Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
        //  4885: astore          a7
        //  4887: aload           a7
        //  4889: ifnonnull       4926
        //  4892: new             Ljava/lang/IllegalStateException;
        //  4895: dup            
        //  4896: ldc_w           "Missing loottable '%s' for '%s'"
        //  4899: iconst_2       
        //  4900: anewarray       Ljava/lang/Object;
        //  4903: dup            
        //  4904: iconst_0       
        //  4905: aload           qv6
        //  4907: aastore        
        //  4908: dup            
        //  4909: iconst_1       
        //  4910: getstatic       net/minecraft/core/Registry.ENTITY_TYPE:Lnet/minecraft/core/DefaultedRegistry;
        //  4913: aload           ais5
        //  4915: invokevirtual   net/minecraft/core/DefaultedRegistry.getKey:(Ljava/lang/Object;)Lnet/minecraft/resources/ResourceLocation;
        //  4918: aastore        
        //  4919: invokestatic    java/lang/String.format:(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //  4922: invokespecial   java/lang/IllegalStateException.<init>:(Ljava/lang/String;)V
        //  4925: athrow         
        //  4926: aload_1         /* biConsumer */
        //  4927: aload           qv6
        //  4929: aload           a7
        //  4931: invokeinterface java/util/function/BiConsumer.accept:(Ljava/lang/Object;Ljava/lang/Object;)V
        //  4936: goto            4995
        //  4939: aload           qv6
        //  4941: getstatic       net/minecraft/world/level/storage/loot/BuiltInLootTables.EMPTY:Lnet/minecraft/resources/ResourceLocation;
        //  4944: if_acmpeq       4995
        //  4947: aload_0         /* this */
        //  4948: getfield        net/minecraft/data/loot/EntityLoot.map:Ljava/util/Map;
        //  4951: aload           qv6
        //  4953: invokeinterface java/util/Map.remove:(Ljava/lang/Object;)Ljava/lang/Object;
        //  4958: ifnull          4995
        //  4961: new             Ljava/lang/IllegalStateException;
        //  4964: dup            
        //  4965: ldc_w           "Weird loottable '%s' for '%s', not a LivingEntity so should not have loot"
        //  4968: iconst_2       
        //  4969: anewarray       Ljava/lang/Object;
        //  4972: dup            
        //  4973: iconst_0       
        //  4974: aload           qv6
        //  4976: aastore        
        //  4977: dup            
        //  4978: iconst_1       
        //  4979: getstatic       net/minecraft/core/Registry.ENTITY_TYPE:Lnet/minecraft/core/DefaultedRegistry;
        //  4982: aload           ais5
        //  4984: invokevirtual   net/minecraft/core/DefaultedRegistry.getKey:(Ljava/lang/Object;)Lnet/minecraft/resources/ResourceLocation;
        //  4987: aastore        
        //  4988: invokestatic    java/lang/String.format:(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //  4991: invokespecial   java/lang/IllegalStateException.<init>:(Ljava/lang/String;)V
        //  4994: athrow         
        //  4995: goto            4801
        //  4998: aload_0         /* this */
        //  4999: getfield        net/minecraft/data/loot/EntityLoot.map:Ljava/util/Map;
        //  5002: aload_1         /* biConsumer */
        //  5003: invokedynamic   BootstrapMethod #1, accept:(Ljava/util/function/BiConsumer;)Ljava/util/function/BiConsumer;
        //  5008: invokeinterface java/util/Map.forEach:(Ljava/util/function/BiConsumer;)V
        //  5013: return         
        //    Signature:
        //  (Ljava/util/function/BiConsumer<Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;>;)V
        //    MethodParameters:
        //  Name        Flags  
        //  ----------  -----
        //  biConsumer  
        //    StackMapTable: 00 06 FD 12 C1 07 03 18 07 03 1A FD 00 32 07 00 7A 07 03 36 FF 00 49 00 07 07 00 02 07 03 54 07 03 18 07 03 1A 00 07 03 36 07 00 0F 00 00 FF 00 0C 00 06 07 00 02 07 03 54 07 03 18 07 03 1A 07 00 7A 07 03 36 00 00 F9 00 37 F9 00 02
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
    
    private void add(final EntityType<?> ais, final LootTable.Builder a) {
        this.add(ais.getDefaultLootTable(), a);
    }
    
    private void add(final ResourceLocation qv, final LootTable.Builder a) {
        this.map.put(qv, a);
    }
    
    static {
        ENTITY_ON_FIRE = EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnFire(true).build());
        SPECIAL_LOOT_TABLE_TYPES = (Set)ImmutableSet.of(EntityType.PLAYER, EntityType.ARMOR_STAND, EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.VILLAGER);
    }
}
