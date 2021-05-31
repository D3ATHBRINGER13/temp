package net.minecraft.world.level.levelgen.flat;

import net.minecraft.world.level.levelgen.placement.LakeChanceDecoratorConfig;
import net.minecraft.world.level.levelgen.feature.LakeConfiguration;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.feature.Feature;
import org.apache.logging.log4j.LogManager;
import net.minecraft.world.level.levelgen.feature.PillagerOutpostConfiguration;
import net.minecraft.world.level.levelgen.feature.ShipwreckConfiguration;
import net.minecraft.world.level.levelgen.feature.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.structure.OceanRuinFeature;
import net.minecraft.world.level.levelgen.feature.VillageConfiguration;
import net.minecraft.world.level.levelgen.feature.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.feature.MineshaftFeature;
import java.util.HashMap;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biomes;
import com.google.common.base.Splitter;
import java.util.Collection;
import net.minecraft.world.level.chunk.ChunkGeneratorType;
import com.google.common.collect.ImmutableMap;
import java.util.stream.Collectors;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Collections;
import java.util.Locale;
import java.util.Iterator;
import net.minecraft.world.level.block.Blocks;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.biome.Biome;
import java.util.List;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.GenerationStep;
import java.util.Map;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public class FlatLevelGeneratorSettings extends ChunkGeneratorSettings {
    private static final Logger LOGGER;
    private static final ConfiguredFeature<?> MINESHAFT_COMPOSITE_FEATURE;
    private static final ConfiguredFeature<?> VILLAGE_COMPOSITE_FEATURE;
    private static final ConfiguredFeature<?> STRONGHOLD_COMPOSITE_FEATURE;
    private static final ConfiguredFeature<?> SWAMPHUT_COMPOSITE_FEATURE;
    private static final ConfiguredFeature<?> DESERT_PYRAMID_COMPOSITE_FEATURE;
    private static final ConfiguredFeature<?> JUNGLE_PYRAMID_COMPOSITE_FEATURE;
    private static final ConfiguredFeature<?> IGLOO_COMPOSITE_FEATURE;
    private static final ConfiguredFeature<?> SHIPWRECK_COMPOSITE_FEATURE;
    private static final ConfiguredFeature<?> OCEAN_MONUMENT_COMPOSITE_FEATURE;
    private static final ConfiguredFeature<?> WATER_LAKE_COMPOSITE_FEATURE;
    private static final ConfiguredFeature<?> LAVA_LAKE_COMPOSITE_FEATURE;
    private static final ConfiguredFeature<?> ENDCITY_COMPOSITE_FEATURE;
    private static final ConfiguredFeature<?> WOOLAND_MANSION_COMPOSITE_FEATURE;
    private static final ConfiguredFeature<?> FORTRESS_COMPOSITE_FEATURE;
    private static final ConfiguredFeature<?> OCEAN_RUIN_COMPOSITE_FEATURE;
    private static final ConfiguredFeature<?> PILLAGER_OUTPOST_COMPOSITE_FEATURE;
    public static final Map<ConfiguredFeature<?>, GenerationStep.Decoration> STRUCTURE_FEATURES_STEP;
    public static final Map<String, ConfiguredFeature<?>[]> STRUCTURE_FEATURES;
    public static final Map<ConfiguredFeature<?>, FeatureConfiguration> STRUCTURE_FEATURES_DEFAULT;
    private final List<FlatLayerInfo> layersInfo;
    private final Map<String, Map<String, String>> structuresOptions;
    private Biome biome;
    private final BlockState[] layers;
    private boolean voidGen;
    private int seaLevel;
    
    public FlatLevelGeneratorSettings() {
        this.layersInfo = (List<FlatLayerInfo>)Lists.newArrayList();
        this.structuresOptions = (Map<String, Map<String, String>>)Maps.newHashMap();
        this.layers = new BlockState[256];
    }
    
    @Nullable
    public static Block byString(final String string) {
        try {
            final ResourceLocation qv2 = new ResourceLocation(string);
            return (Block)Registry.BLOCK.getOptional(qv2).orElse(null);
        }
        catch (IllegalArgumentException illegalArgumentException2) {
            FlatLevelGeneratorSettings.LOGGER.warn("Invalid blockstate: {}", string, illegalArgumentException2);
            return null;
        }
    }
    
    public Biome getBiome() {
        return this.biome;
    }
    
    public void setBiome(final Biome bio) {
        this.biome = bio;
    }
    
    public Map<String, Map<String, String>> getStructuresOptions() {
        return this.structuresOptions;
    }
    
    public List<FlatLayerInfo> getLayersInfo() {
        return this.layersInfo;
    }
    
    public void updateLayers() {
        int integer2 = 0;
        for (final FlatLayerInfo cfw4 : this.layersInfo) {
            cfw4.setStart(integer2);
            integer2 += cfw4.getHeight();
        }
        this.seaLevel = 0;
        this.voidGen = true;
        integer2 = 0;
        for (final FlatLayerInfo cfw4 : this.layersInfo) {
            for (int integer3 = cfw4.getStart(); integer3 < cfw4.getStart() + cfw4.getHeight(); ++integer3) {
                final BlockState bvt6 = cfw4.getBlockState();
                if (bvt6.getBlock() != Blocks.AIR) {
                    this.voidGen = false;
                    this.layers[integer3] = bvt6;
                }
            }
            if (cfw4.getBlockState().getBlock() == Blocks.AIR) {
                integer2 += cfw4.getHeight();
            }
            else {
                this.seaLevel += cfw4.getHeight() + integer2;
                integer2 = 0;
            }
        }
    }
    
    public String toString() {
        final StringBuilder stringBuilder2 = new StringBuilder();
        for (int integer3 = 0; integer3 < this.layersInfo.size(); ++integer3) {
            if (integer3 > 0) {
                stringBuilder2.append(",");
            }
            stringBuilder2.append(this.layersInfo.get(integer3));
        }
        stringBuilder2.append(";");
        stringBuilder2.append(Registry.BIOME.getKey(this.biome));
        stringBuilder2.append(";");
        if (!this.structuresOptions.isEmpty()) {
            int integer3 = 0;
            for (final Map.Entry<String, Map<String, String>> entry5 : this.structuresOptions.entrySet()) {
                if (integer3++ > 0) {
                    stringBuilder2.append(",");
                }
                stringBuilder2.append(((String)entry5.getKey()).toLowerCase(Locale.ROOT));
                final Map<String, String> map6 = (Map<String, String>)entry5.getValue();
                if (!map6.isEmpty()) {
                    stringBuilder2.append("(");
                    int integer4 = 0;
                    for (final Map.Entry<String, String> entry6 : map6.entrySet()) {
                        if (integer4++ > 0) {
                            stringBuilder2.append(" ");
                        }
                        stringBuilder2.append((String)entry6.getKey());
                        stringBuilder2.append("=");
                        stringBuilder2.append((String)entry6.getValue());
                    }
                    stringBuilder2.append(")");
                }
            }
        }
        return stringBuilder2.toString();
    }
    
    @Nullable
    private static FlatLayerInfo getLayerInfoFromString(final String string, final int integer) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: ldc_w           "\\*"
        //     4: iconst_2       
        //     5: invokevirtual   java/lang/String.split:(Ljava/lang/String;I)[Ljava/lang/String;
        //     8: astore_2        /* arr3 */
        //     9: aload_2         /* arr3 */
        //    10: arraylength    
        //    11: iconst_2       
        //    12: if_icmpne       49
        //    15: aload_2         /* arr3 */
        //    16: iconst_0       
        //    17: aaload         
        //    18: invokestatic    java/lang/Integer.parseInt:(Ljava/lang/String;)I
        //    21: iconst_0       
        //    22: invokestatic    java/lang/Math.max:(II)I
        //    25: istore_3        /* integer4 */
        //    26: goto            51
        //    29: astore          numberFormatException5
        //    31: getstatic       net/minecraft/world/level/levelgen/flat/FlatLevelGeneratorSettings.LOGGER:Lorg/apache/logging/log4j/Logger;
        //    34: ldc_w           "Error while parsing flat world string => {}"
        //    37: aload           numberFormatException5
        //    39: invokevirtual   java/lang/NumberFormatException.getMessage:()Ljava/lang/String;
        //    42: invokeinterface org/apache/logging/log4j/Logger.error:(Ljava/lang/String;Ljava/lang/Object;)V
        //    47: aconst_null    
        //    48: areturn        
        //    49: iconst_1       
        //    50: istore_3        /* integer4 */
        //    51: iload_1         /* integer */
        //    52: iload_3         /* integer4 */
        //    53: iadd           
        //    54: sipush          256
        //    57: invokestatic    java/lang/Math.min:(II)I
        //    60: istore          integer5
        //    62: iload           integer5
        //    64: iload_1         /* integer */
        //    65: isub           
        //    66: istore          integer6
        //    68: aload_2         /* arr3 */
        //    69: aload_2         /* arr3 */
        //    70: arraylength    
        //    71: iconst_1       
        //    72: isub           
        //    73: aaload         
        //    74: invokestatic    net/minecraft/world/level/levelgen/flat/FlatLevelGeneratorSettings.byString:(Ljava/lang/String;)Lnet/minecraft/world/level/block/Block;
        //    77: astore          bmv7
        //    79: goto            102
        //    82: astore          exception8
        //    84: getstatic       net/minecraft/world/level/levelgen/flat/FlatLevelGeneratorSettings.LOGGER:Lorg/apache/logging/log4j/Logger;
        //    87: ldc_w           "Error while parsing flat world string => {}"
        //    90: aload           exception8
        //    92: invokevirtual   java/lang/Exception.getMessage:()Ljava/lang/String;
        //    95: invokeinterface org/apache/logging/log4j/Logger.error:(Ljava/lang/String;Ljava/lang/Object;)V
        //   100: aconst_null    
        //   101: areturn        
        //   102: aload           bmv7
        //   104: ifnonnull       126
        //   107: getstatic       net/minecraft/world/level/levelgen/flat/FlatLevelGeneratorSettings.LOGGER:Lorg/apache/logging/log4j/Logger;
        //   110: ldc_w           "Error while parsing flat world string => Unknown block, {}"
        //   113: aload_2         /* arr3 */
        //   114: aload_2         /* arr3 */
        //   115: arraylength    
        //   116: iconst_1       
        //   117: isub           
        //   118: aaload         
        //   119: invokeinterface org/apache/logging/log4j/Logger.error:(Ljava/lang/String;Ljava/lang/Object;)V
        //   124: aconst_null    
        //   125: areturn        
        //   126: new             Lnet/minecraft/world/level/levelgen/flat/FlatLayerInfo;
        //   129: dup            
        //   130: iload           integer6
        //   132: aload           bmv7
        //   134: invokespecial   net/minecraft/world/level/levelgen/flat/FlatLayerInfo.<init>:(ILnet/minecraft/world/level/block/Block;)V
        //   137: astore          cfw8
        //   139: aload           cfw8
        //   141: iload_1         /* integer */
        //   142: invokevirtual   net/minecraft/world/level/levelgen/flat/FlatLayerInfo.setStart:(I)V
        //   145: aload           cfw8
        //   147: areturn        
        //    MethodParameters:
        //  Name     Flags  
        //  -------  -----
        //  string   
        //  integer  
        //    StackMapTable: 00 06 FF 00 1D 00 00 00 01 07 01 24 FE 00 13 00 01 07 01 43 FC 00 01 01 FF 00 1E 00 00 00 01 07 01 26 FF 00 13 00 07 00 01 07 01 43 00 00 01 07 00 7E 00 00 FF 00 17 00 07 00 01 00 00 00 01 07 00 7E 00 00
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                             
        //  -----  -----  -----  -----  ---------------------------------
        //  15     26     29     49     Ljava/lang/NumberFormatException;
        //  68     79     82     102    Ljava/lang/Exception;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException: Array index out of range: 1
        //     at java.base/java.util.Vector.get(Vector.java:781)
        //     at com.strobel.assembler.metadata.MetadataResolver.resolve(MetadataResolver.java:82)
        //     at com.strobel.assembler.metadata.CoreMetadataFactory$UnresolvedType.resolve(CoreMetadataFactory.java:616)
        //     at com.strobel.assembler.metadata.MetadataHelper$9.visitClassType(MetadataHelper.java:2114)
        //     at com.strobel.assembler.metadata.MetadataHelper$9.visitClassType(MetadataHelper.java:2075)
        //     at com.strobel.assembler.metadata.CoreMetadataFactory$UnresolvedType.accept(CoreMetadataFactory.java:577)
        //     at com.strobel.assembler.metadata.DefaultTypeVisitor.visit(DefaultTypeVisitor.java:25)
        //     at com.strobel.assembler.metadata.DefaultTypeVisitor.visit(DefaultTypeVisitor.java:21)
        //     at com.strobel.assembler.metadata.MetadataHelper.getSuperType(MetadataHelper.java:1264)
        //     at com.strobel.assembler.metadata.MetadataHelper$8.visitClassType(MetadataHelper.java:2011)
        //     at com.strobel.assembler.metadata.MetadataHelper$8.visitClassType(MetadataHelper.java:1994)
        //     at com.strobel.assembler.metadata.CoreMetadataFactory$UnresolvedType.accept(CoreMetadataFactory.java:577)
        //     at com.strobel.assembler.metadata.DefaultTypeVisitor.visit(DefaultTypeVisitor.java:25)
        //     at com.strobel.assembler.metadata.MetadataHelper.asSuper(MetadataHelper.java:727)
        //     at com.strobel.assembler.metadata.MetadataHelper$6.visitClassType(MetadataHelper.java:1853)
        //     at com.strobel.assembler.metadata.MetadataHelper$6.visitClassType(MetadataHelper.java:1815)
        //     at com.strobel.assembler.metadata.CoreMetadataFactory$UnresolvedType.accept(CoreMetadataFactory.java:577)
        //     at com.strobel.assembler.metadata.DefaultTypeVisitor.visit(DefaultTypeVisitor.java:25)
        //     at com.strobel.assembler.metadata.MetadataHelper.isSubType(MetadataHelper.java:1302)
        //     at com.strobel.assembler.metadata.MetadataHelper.isSubType(MetadataHelper.java:568)
        //     at com.strobel.assembler.metadata.MetadataHelper.isSubtypeUncheckedInternal(MetadataHelper.java:540)
        //     at com.strobel.assembler.metadata.MetadataHelper.isSubTypeUnchecked(MetadataHelper.java:520)
        //     at com.strobel.assembler.metadata.MetadataHelper.isConvertible(MetadataHelper.java:507)
        //     at com.strobel.assembler.metadata.MetadataHelper.isConvertible(MetadataHelper.java:488)
        //     at com.strobel.assembler.metadata.MetadataHelper.isAssignableFrom(MetadataHelper.java:557)
        //     at com.strobel.assembler.metadata.MetadataHelper.findCommonSuperTypeCore(MetadataHelper.java:248)
        //     at com.strobel.assembler.metadata.MetadataHelper.findCommonSuperType(MetadataHelper.java:200)
        //     at com.strobel.assembler.ir.Frame.merge(Frame.java:369)
        //     at com.strobel.assembler.ir.Frame.merge(Frame.java:273)
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2206)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:211)
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
    
    private static List<FlatLayerInfo> getLayersInfoFromString(final String string) {
        final List<FlatLayerInfo> list2 = (List<FlatLayerInfo>)Lists.newArrayList();
        final String[] arr3 = string.split(",");
        int integer4 = 0;
        for (final String string2 : arr3) {
            final FlatLayerInfo cfw9 = getLayerInfoFromString(string2, integer4);
            if (cfw9 == null) {
                return (List<FlatLayerInfo>)Collections.emptyList();
            }
            list2.add(cfw9);
            integer4 += cfw9.getHeight();
        }
        return list2;
    }
    
    public <T> Dynamic<T> toObject(final DynamicOps<T> dynamicOps) {
        final T object3 = (T)dynamicOps.createList(this.layersInfo.stream().map(cfw -> dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("height"), dynamicOps.createInt(cfw.getHeight()), dynamicOps.createString("block"), dynamicOps.createString(Registry.BLOCK.getKey(cfw.getBlockState().getBlock()).toString())))));
        final T object4 = (T)dynamicOps.createMap((Map)this.structuresOptions.entrySet().stream().map(entry -> Pair.of(dynamicOps.createString(((String)entry.getKey()).toLowerCase(Locale.ROOT)), dynamicOps.createMap((Map)((Map)entry.getValue()).entrySet().stream().map(entry -> Pair.of(dynamicOps.createString((String)entry.getKey()), dynamicOps.createString((String)entry.getValue()))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("layers"), object3, dynamicOps.createString("biome"), dynamicOps.createString(Registry.BIOME.getKey(this.biome).toString()), dynamicOps.createString("structures"), object4)));
    }
    
    public static FlatLevelGeneratorSettings fromObject(final Dynamic<?> dynamic) {
        final FlatLevelGeneratorSettings cfx2 = ChunkGeneratorType.FLAT.createSettings();
        final List<Pair<Integer, Block>> list3 = (List<Pair<Integer, Block>>)dynamic.get("layers").asList(dynamic -> Pair.of(dynamic.get("height").asInt(1), byString(dynamic.get("block").asString(""))));
        if (list3.stream().anyMatch(pair -> pair.getSecond() == null)) {
            return getDefault();
        }
        final List<FlatLayerInfo> list4 = (List<FlatLayerInfo>)list3.stream().map(pair -> new FlatLayerInfo((int)pair.getFirst(), (Block)pair.getSecond())).collect(Collectors.toList());
        if (list4.isEmpty()) {
            return getDefault();
        }
        cfx2.getLayersInfo().addAll((Collection)list4);
        cfx2.updateLayers();
        cfx2.setBiome(Registry.BIOME.get(new ResourceLocation(dynamic.get("biome").asString(""))));
        dynamic.get("structures").flatMap(Dynamic::getMapValues).ifPresent(map -> map.keySet().forEach(dynamic -> dynamic.asString().map(string -> (Map)cfx2.getStructuresOptions().put(string, Maps.newHashMap()))));
        return cfx2;
    }
    
    public static FlatLevelGeneratorSettings fromString(final String string) {
        final Iterator<String> iterator2 = (Iterator<String>)Splitter.on(';').split((CharSequence)string).iterator();
        if (!iterator2.hasNext()) {
            return getDefault();
        }
        final FlatLevelGeneratorSettings cfx3 = ChunkGeneratorType.FLAT.createSettings();
        final List<FlatLayerInfo> list4 = getLayersInfoFromString((String)iterator2.next());
        if (list4.isEmpty()) {
            return getDefault();
        }
        cfx3.getLayersInfo().addAll((Collection)list4);
        cfx3.updateLayers();
        final Biome bio5 = iterator2.hasNext() ? Registry.BIOME.get(new ResourceLocation((String)iterator2.next())) : null;
        cfx3.setBiome((bio5 == null) ? Biomes.PLAINS : bio5);
        if (iterator2.hasNext()) {
            final String[] split;
            final String[] arr6 = split = ((String)iterator2.next()).toLowerCase(Locale.ROOT).split(",");
            for (final String string2 : split) {
                final String[] arr7 = string2.split("\\(", 2);
                if (!arr7[0].isEmpty()) {
                    cfx3.addStructure(arr7[0]);
                    if (arr7.length > 1 && arr7[1].endsWith(")") && arr7[1].length() > 1) {
                        final String[] split2;
                        final String[] arr8 = split2 = arr7[1].substring(0, arr7[1].length() - 1).split(" ");
                        for (final String string3 : split2) {
                            final String[] arr9 = string3.split("=", 2);
                            if (arr9.length == 2) {
                                cfx3.addStructureOption(arr7[0], arr9[0], arr9[1]);
                            }
                        }
                    }
                }
            }
        }
        else {
            cfx3.getStructuresOptions().put("village", Maps.newHashMap());
        }
        return cfx3;
    }
    
    private void addStructure(final String string) {
        final Map<String, String> map3 = (Map<String, String>)Maps.newHashMap();
        this.structuresOptions.put(string, map3);
    }
    
    private void addStructureOption(final String string1, final String string2, final String string3) {
        ((Map)this.structuresOptions.get(string1)).put(string2, string3);
        if ("village".equals(string1) && "distance".equals(string2)) {
            this.villagesSpacing = Mth.getInt(string3, this.villagesSpacing, 9);
        }
        if ("biome_1".equals(string1) && "distance".equals(string2)) {
            this.templesSpacing = Mth.getInt(string3, this.templesSpacing, 9);
        }
        if ("stronghold".equals(string1)) {
            if ("distance".equals(string2)) {
                this.strongholdsDistance = Mth.getInt(string3, this.strongholdsDistance, 1);
            }
            else if ("count".equals(string2)) {
                this.strongholdsCount = Mth.getInt(string3, this.strongholdsCount, 1);
            }
            else if ("spread".equals(string2)) {
                this.strongholdsSpread = Mth.getInt(string3, this.strongholdsSpread, 1);
            }
        }
        if ("oceanmonument".equals(string1)) {
            if ("separation".equals(string2)) {
                this.monumentsSeparation = Mth.getInt(string3, this.monumentsSeparation, 1);
            }
            else if ("spacing".equals(string2)) {
                this.monumentsSpacing = Mth.getInt(string3, this.monumentsSpacing, 1);
            }
        }
        if ("endcity".equals(string1) && "distance".equals(string2)) {
            this.endCitySpacing = Mth.getInt(string3, this.endCitySpacing, 1);
        }
        if ("mansion".equals(string1) && "distance".equals(string2)) {
            this.woodlandMansionSpacing = Mth.getInt(string3, this.woodlandMansionSpacing, 1);
        }
    }
    
    public static FlatLevelGeneratorSettings getDefault() {
        final FlatLevelGeneratorSettings cfx1 = ChunkGeneratorType.FLAT.createSettings();
        cfx1.setBiome(Biomes.PLAINS);
        cfx1.getLayersInfo().add(new FlatLayerInfo(1, Blocks.BEDROCK));
        cfx1.getLayersInfo().add(new FlatLayerInfo(2, Blocks.DIRT));
        cfx1.getLayersInfo().add(new FlatLayerInfo(1, Blocks.GRASS_BLOCK));
        cfx1.updateLayers();
        cfx1.getStructuresOptions().put("village", Maps.newHashMap());
        return cfx1;
    }
    
    public boolean isVoidGen() {
        return this.voidGen;
    }
    
    public BlockState[] getLayers() {
        return this.layers;
    }
    
    public void deleteLayer(final int integer) {
        this.layers[integer] = null;
    }
    
    static {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: putstatic       net/minecraft/world/level/levelgen/flat/FlatLevelGeneratorSettings.LOGGER:Lorg/apache/logging/log4j/Logger;
        //     6: getstatic       net/minecraft/world/level/levelgen/feature/Feature.MINESHAFT:Lnet/minecraft/world/level/levelgen/feature/StructureFeature;
        //     9: new             Lnet/minecraft/world/level/levelgen/feature/MineshaftConfiguration;
        //    12: dup            
        //    13: ldc2_w          0.004
        //    16: getstatic       net/minecraft/world/level/levelgen/feature/MineshaftFeature$Type.NORMAL:Lnet/minecraft/world/level/levelgen/feature/MineshaftFeature$Type;
        //    19: invokespecial   net/minecraft/world/level/levelgen/feature/MineshaftConfiguration.<init>:(DLnet/minecraft/world/level/levelgen/feature/MineshaftFeature$Type;)V
        //    22: getstatic       net/minecraft/world/level/levelgen/placement/FeatureDecorator.NOPE:Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;
        //    25: getstatic       net/minecraft/world/level/levelgen/feature/DecoratorConfiguration.NONE:Lnet/minecraft/world/level/levelgen/feature/NoneDecoratorConfiguration;
        //    28: invokestatic    net/minecraft/world/level/biome/Biome.makeComposite:(Lnet/minecraft/world/level/levelgen/feature/Feature;Lnet/minecraft/world/level/levelgen/feature/FeatureConfiguration;Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;Lnet/minecraft/world/level/levelgen/feature/DecoratorConfiguration;)Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //    31: putstatic       net/minecraft/world/level/levelgen/flat/FlatLevelGeneratorSettings.MINESHAFT_COMPOSITE_FEATURE:Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //    34: getstatic       net/minecraft/world/level/levelgen/feature/Feature.VILLAGE:Lnet/minecraft/world/level/levelgen/feature/StructureFeature;
        //    37: new             Lnet/minecraft/world/level/levelgen/feature/VillageConfiguration;
        //    40: dup            
        //    41: ldc_w           "village/plains/town_centers"
        //    44: bipush          6
        //    46: invokespecial   net/minecraft/world/level/levelgen/feature/VillageConfiguration.<init>:(Ljava/lang/String;I)V
        //    49: getstatic       net/minecraft/world/level/levelgen/placement/FeatureDecorator.NOPE:Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;
        //    52: getstatic       net/minecraft/world/level/levelgen/feature/DecoratorConfiguration.NONE:Lnet/minecraft/world/level/levelgen/feature/NoneDecoratorConfiguration;
        //    55: invokestatic    net/minecraft/world/level/biome/Biome.makeComposite:(Lnet/minecraft/world/level/levelgen/feature/Feature;Lnet/minecraft/world/level/levelgen/feature/FeatureConfiguration;Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;Lnet/minecraft/world/level/levelgen/feature/DecoratorConfiguration;)Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //    58: putstatic       net/minecraft/world/level/levelgen/flat/FlatLevelGeneratorSettings.VILLAGE_COMPOSITE_FEATURE:Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //    61: getstatic       net/minecraft/world/level/levelgen/feature/Feature.STRONGHOLD:Lnet/minecraft/world/level/levelgen/feature/StructureFeature;
        //    64: getstatic       net/minecraft/world/level/levelgen/feature/FeatureConfiguration.NONE:Lnet/minecraft/world/level/levelgen/feature/NoneFeatureConfiguration;
        //    67: getstatic       net/minecraft/world/level/levelgen/placement/FeatureDecorator.NOPE:Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;
        //    70: getstatic       net/minecraft/world/level/levelgen/feature/DecoratorConfiguration.NONE:Lnet/minecraft/world/level/levelgen/feature/NoneDecoratorConfiguration;
        //    73: invokestatic    net/minecraft/world/level/biome/Biome.makeComposite:(Lnet/minecraft/world/level/levelgen/feature/Feature;Lnet/minecraft/world/level/levelgen/feature/FeatureConfiguration;Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;Lnet/minecraft/world/level/levelgen/feature/DecoratorConfiguration;)Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //    76: putstatic       net/minecraft/world/level/levelgen/flat/FlatLevelGeneratorSettings.STRONGHOLD_COMPOSITE_FEATURE:Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //    79: getstatic       net/minecraft/world/level/levelgen/feature/Feature.SWAMP_HUT:Lnet/minecraft/world/level/levelgen/feature/SwamplandHutFeature;
        //    82: getstatic       net/minecraft/world/level/levelgen/feature/FeatureConfiguration.NONE:Lnet/minecraft/world/level/levelgen/feature/NoneFeatureConfiguration;
        //    85: getstatic       net/minecraft/world/level/levelgen/placement/FeatureDecorator.NOPE:Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;
        //    88: getstatic       net/minecraft/world/level/levelgen/feature/DecoratorConfiguration.NONE:Lnet/minecraft/world/level/levelgen/feature/NoneDecoratorConfiguration;
        //    91: invokestatic    net/minecraft/world/level/biome/Biome.makeComposite:(Lnet/minecraft/world/level/levelgen/feature/Feature;Lnet/minecraft/world/level/levelgen/feature/FeatureConfiguration;Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;Lnet/minecraft/world/level/levelgen/feature/DecoratorConfiguration;)Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //    94: putstatic       net/minecraft/world/level/levelgen/flat/FlatLevelGeneratorSettings.SWAMPHUT_COMPOSITE_FEATURE:Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //    97: getstatic       net/minecraft/world/level/levelgen/feature/Feature.DESERT_PYRAMID:Lnet/minecraft/world/level/levelgen/feature/StructureFeature;
        //   100: getstatic       net/minecraft/world/level/levelgen/feature/FeatureConfiguration.NONE:Lnet/minecraft/world/level/levelgen/feature/NoneFeatureConfiguration;
        //   103: getstatic       net/minecraft/world/level/levelgen/placement/FeatureDecorator.NOPE:Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;
        //   106: getstatic       net/minecraft/world/level/levelgen/feature/DecoratorConfiguration.NONE:Lnet/minecraft/world/level/levelgen/feature/NoneDecoratorConfiguration;
        //   109: invokestatic    net/minecraft/world/level/biome/Biome.makeComposite:(Lnet/minecraft/world/level/levelgen/feature/Feature;Lnet/minecraft/world/level/levelgen/feature/FeatureConfiguration;Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;Lnet/minecraft/world/level/levelgen/feature/DecoratorConfiguration;)Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //   112: putstatic       net/minecraft/world/level/levelgen/flat/FlatLevelGeneratorSettings.DESERT_PYRAMID_COMPOSITE_FEATURE:Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //   115: getstatic       net/minecraft/world/level/levelgen/feature/Feature.JUNGLE_TEMPLE:Lnet/minecraft/world/level/levelgen/feature/StructureFeature;
        //   118: getstatic       net/minecraft/world/level/levelgen/feature/FeatureConfiguration.NONE:Lnet/minecraft/world/level/levelgen/feature/NoneFeatureConfiguration;
        //   121: getstatic       net/minecraft/world/level/levelgen/placement/FeatureDecorator.NOPE:Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;
        //   124: getstatic       net/minecraft/world/level/levelgen/feature/DecoratorConfiguration.NONE:Lnet/minecraft/world/level/levelgen/feature/NoneDecoratorConfiguration;
        //   127: invokestatic    net/minecraft/world/level/biome/Biome.makeComposite:(Lnet/minecraft/world/level/levelgen/feature/Feature;Lnet/minecraft/world/level/levelgen/feature/FeatureConfiguration;Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;Lnet/minecraft/world/level/levelgen/feature/DecoratorConfiguration;)Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //   130: putstatic       net/minecraft/world/level/levelgen/flat/FlatLevelGeneratorSettings.JUNGLE_PYRAMID_COMPOSITE_FEATURE:Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //   133: getstatic       net/minecraft/world/level/levelgen/feature/Feature.IGLOO:Lnet/minecraft/world/level/levelgen/feature/StructureFeature;
        //   136: getstatic       net/minecraft/world/level/levelgen/feature/FeatureConfiguration.NONE:Lnet/minecraft/world/level/levelgen/feature/NoneFeatureConfiguration;
        //   139: getstatic       net/minecraft/world/level/levelgen/placement/FeatureDecorator.NOPE:Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;
        //   142: getstatic       net/minecraft/world/level/levelgen/feature/DecoratorConfiguration.NONE:Lnet/minecraft/world/level/levelgen/feature/NoneDecoratorConfiguration;
        //   145: invokestatic    net/minecraft/world/level/biome/Biome.makeComposite:(Lnet/minecraft/world/level/levelgen/feature/Feature;Lnet/minecraft/world/level/levelgen/feature/FeatureConfiguration;Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;Lnet/minecraft/world/level/levelgen/feature/DecoratorConfiguration;)Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //   148: putstatic       net/minecraft/world/level/levelgen/flat/FlatLevelGeneratorSettings.IGLOO_COMPOSITE_FEATURE:Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //   151: getstatic       net/minecraft/world/level/levelgen/feature/Feature.SHIPWRECK:Lnet/minecraft/world/level/levelgen/feature/StructureFeature;
        //   154: new             Lnet/minecraft/world/level/levelgen/feature/ShipwreckConfiguration;
        //   157: dup            
        //   158: iconst_0       
        //   159: invokespecial   net/minecraft/world/level/levelgen/feature/ShipwreckConfiguration.<init>:(Z)V
        //   162: getstatic       net/minecraft/world/level/levelgen/placement/FeatureDecorator.NOPE:Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;
        //   165: getstatic       net/minecraft/world/level/levelgen/feature/DecoratorConfiguration.NONE:Lnet/minecraft/world/level/levelgen/feature/NoneDecoratorConfiguration;
        //   168: invokestatic    net/minecraft/world/level/biome/Biome.makeComposite:(Lnet/minecraft/world/level/levelgen/feature/Feature;Lnet/minecraft/world/level/levelgen/feature/FeatureConfiguration;Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;Lnet/minecraft/world/level/levelgen/feature/DecoratorConfiguration;)Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //   171: putstatic       net/minecraft/world/level/levelgen/flat/FlatLevelGeneratorSettings.SHIPWRECK_COMPOSITE_FEATURE:Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //   174: getstatic       net/minecraft/world/level/levelgen/feature/Feature.OCEAN_MONUMENT:Lnet/minecraft/world/level/levelgen/feature/StructureFeature;
        //   177: getstatic       net/minecraft/world/level/levelgen/feature/FeatureConfiguration.NONE:Lnet/minecraft/world/level/levelgen/feature/NoneFeatureConfiguration;
        //   180: getstatic       net/minecraft/world/level/levelgen/placement/FeatureDecorator.NOPE:Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;
        //   183: getstatic       net/minecraft/world/level/levelgen/feature/DecoratorConfiguration.NONE:Lnet/minecraft/world/level/levelgen/feature/NoneDecoratorConfiguration;
        //   186: invokestatic    net/minecraft/world/level/biome/Biome.makeComposite:(Lnet/minecraft/world/level/levelgen/feature/Feature;Lnet/minecraft/world/level/levelgen/feature/FeatureConfiguration;Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;Lnet/minecraft/world/level/levelgen/feature/DecoratorConfiguration;)Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //   189: putstatic       net/minecraft/world/level/levelgen/flat/FlatLevelGeneratorSettings.OCEAN_MONUMENT_COMPOSITE_FEATURE:Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //   192: getstatic       net/minecraft/world/level/levelgen/feature/Feature.LAKE:Lnet/minecraft/world/level/levelgen/feature/Feature;
        //   195: new             Lnet/minecraft/world/level/levelgen/feature/LakeConfiguration;
        //   198: dup            
        //   199: getstatic       net/minecraft/world/level/block/Blocks.WATER:Lnet/minecraft/world/level/block/Block;
        //   202: invokevirtual   net/minecraft/world/level/block/Block.defaultBlockState:()Lnet/minecraft/world/level/block/state/BlockState;
        //   205: invokespecial   net/minecraft/world/level/levelgen/feature/LakeConfiguration.<init>:(Lnet/minecraft/world/level/block/state/BlockState;)V
        //   208: getstatic       net/minecraft/world/level/levelgen/placement/FeatureDecorator.WATER_LAKE:Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;
        //   211: new             Lnet/minecraft/world/level/levelgen/placement/LakeChanceDecoratorConfig;
        //   214: dup            
        //   215: iconst_4       
        //   216: invokespecial   net/minecraft/world/level/levelgen/placement/LakeChanceDecoratorConfig.<init>:(I)V
        //   219: invokestatic    net/minecraft/world/level/biome/Biome.makeComposite:(Lnet/minecraft/world/level/levelgen/feature/Feature;Lnet/minecraft/world/level/levelgen/feature/FeatureConfiguration;Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;Lnet/minecraft/world/level/levelgen/feature/DecoratorConfiguration;)Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //   222: putstatic       net/minecraft/world/level/levelgen/flat/FlatLevelGeneratorSettings.WATER_LAKE_COMPOSITE_FEATURE:Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //   225: getstatic       net/minecraft/world/level/levelgen/feature/Feature.LAKE:Lnet/minecraft/world/level/levelgen/feature/Feature;
        //   228: new             Lnet/minecraft/world/level/levelgen/feature/LakeConfiguration;
        //   231: dup            
        //   232: getstatic       net/minecraft/world/level/block/Blocks.LAVA:Lnet/minecraft/world/level/block/Block;
        //   235: invokevirtual   net/minecraft/world/level/block/Block.defaultBlockState:()Lnet/minecraft/world/level/block/state/BlockState;
        //   238: invokespecial   net/minecraft/world/level/levelgen/feature/LakeConfiguration.<init>:(Lnet/minecraft/world/level/block/state/BlockState;)V
        //   241: getstatic       net/minecraft/world/level/levelgen/placement/FeatureDecorator.LAVA_LAKE:Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;
        //   244: new             Lnet/minecraft/world/level/levelgen/placement/LakeChanceDecoratorConfig;
        //   247: dup            
        //   248: bipush          80
        //   250: invokespecial   net/minecraft/world/level/levelgen/placement/LakeChanceDecoratorConfig.<init>:(I)V
        //   253: invokestatic    net/minecraft/world/level/biome/Biome.makeComposite:(Lnet/minecraft/world/level/levelgen/feature/Feature;Lnet/minecraft/world/level/levelgen/feature/FeatureConfiguration;Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;Lnet/minecraft/world/level/levelgen/feature/DecoratorConfiguration;)Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //   256: putstatic       net/minecraft/world/level/levelgen/flat/FlatLevelGeneratorSettings.LAVA_LAKE_COMPOSITE_FEATURE:Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //   259: getstatic       net/minecraft/world/level/levelgen/feature/Feature.END_CITY:Lnet/minecraft/world/level/levelgen/feature/StructureFeature;
        //   262: getstatic       net/minecraft/world/level/levelgen/feature/FeatureConfiguration.NONE:Lnet/minecraft/world/level/levelgen/feature/NoneFeatureConfiguration;
        //   265: getstatic       net/minecraft/world/level/levelgen/placement/FeatureDecorator.NOPE:Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;
        //   268: getstatic       net/minecraft/world/level/levelgen/feature/DecoratorConfiguration.NONE:Lnet/minecraft/world/level/levelgen/feature/NoneDecoratorConfiguration;
        //   271: invokestatic    net/minecraft/world/level/biome/Biome.makeComposite:(Lnet/minecraft/world/level/levelgen/feature/Feature;Lnet/minecraft/world/level/levelgen/feature/FeatureConfiguration;Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;Lnet/minecraft/world/level/levelgen/feature/DecoratorConfiguration;)Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //   274: putstatic       net/minecraft/world/level/levelgen/flat/FlatLevelGeneratorSettings.ENDCITY_COMPOSITE_FEATURE:Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //   277: getstatic       net/minecraft/world/level/levelgen/feature/Feature.WOODLAND_MANSION:Lnet/minecraft/world/level/levelgen/feature/StructureFeature;
        //   280: getstatic       net/minecraft/world/level/levelgen/feature/FeatureConfiguration.NONE:Lnet/minecraft/world/level/levelgen/feature/NoneFeatureConfiguration;
        //   283: getstatic       net/minecraft/world/level/levelgen/placement/FeatureDecorator.NOPE:Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;
        //   286: getstatic       net/minecraft/world/level/levelgen/feature/DecoratorConfiguration.NONE:Lnet/minecraft/world/level/levelgen/feature/NoneDecoratorConfiguration;
        //   289: invokestatic    net/minecraft/world/level/biome/Biome.makeComposite:(Lnet/minecraft/world/level/levelgen/feature/Feature;Lnet/minecraft/world/level/levelgen/feature/FeatureConfiguration;Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;Lnet/minecraft/world/level/levelgen/feature/DecoratorConfiguration;)Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //   292: putstatic       net/minecraft/world/level/levelgen/flat/FlatLevelGeneratorSettings.WOOLAND_MANSION_COMPOSITE_FEATURE:Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //   295: getstatic       net/minecraft/world/level/levelgen/feature/Feature.NETHER_BRIDGE:Lnet/minecraft/world/level/levelgen/feature/StructureFeature;
        //   298: getstatic       net/minecraft/world/level/levelgen/feature/FeatureConfiguration.NONE:Lnet/minecraft/world/level/levelgen/feature/NoneFeatureConfiguration;
        //   301: getstatic       net/minecraft/world/level/levelgen/placement/FeatureDecorator.NOPE:Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;
        //   304: getstatic       net/minecraft/world/level/levelgen/feature/DecoratorConfiguration.NONE:Lnet/minecraft/world/level/levelgen/feature/NoneDecoratorConfiguration;
        //   307: invokestatic    net/minecraft/world/level/biome/Biome.makeComposite:(Lnet/minecraft/world/level/levelgen/feature/Feature;Lnet/minecraft/world/level/levelgen/feature/FeatureConfiguration;Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;Lnet/minecraft/world/level/levelgen/feature/DecoratorConfiguration;)Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //   310: putstatic       net/minecraft/world/level/levelgen/flat/FlatLevelGeneratorSettings.FORTRESS_COMPOSITE_FEATURE:Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //   313: getstatic       net/minecraft/world/level/levelgen/feature/Feature.OCEAN_RUIN:Lnet/minecraft/world/level/levelgen/feature/StructureFeature;
        //   316: new             Lnet/minecraft/world/level/levelgen/feature/OceanRuinConfiguration;
        //   319: dup            
        //   320: getstatic       net/minecraft/world/level/levelgen/structure/OceanRuinFeature$Type.COLD:Lnet/minecraft/world/level/levelgen/structure/OceanRuinFeature$Type;
        //   323: ldc_w           0.3
        //   326: ldc_w           0.1
        //   329: invokespecial   net/minecraft/world/level/levelgen/feature/OceanRuinConfiguration.<init>:(Lnet/minecraft/world/level/levelgen/structure/OceanRuinFeature$Type;FF)V
        //   332: getstatic       net/minecraft/world/level/levelgen/placement/FeatureDecorator.NOPE:Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;
        //   335: getstatic       net/minecraft/world/level/levelgen/feature/DecoratorConfiguration.NONE:Lnet/minecraft/world/level/levelgen/feature/NoneDecoratorConfiguration;
        //   338: invokestatic    net/minecraft/world/level/biome/Biome.makeComposite:(Lnet/minecraft/world/level/levelgen/feature/Feature;Lnet/minecraft/world/level/levelgen/feature/FeatureConfiguration;Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;Lnet/minecraft/world/level/levelgen/feature/DecoratorConfiguration;)Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //   341: putstatic       net/minecraft/world/level/levelgen/flat/FlatLevelGeneratorSettings.OCEAN_RUIN_COMPOSITE_FEATURE:Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //   344: getstatic       net/minecraft/world/level/levelgen/feature/Feature.PILLAGER_OUTPOST:Lnet/minecraft/world/level/levelgen/feature/StructureFeature;
        //   347: new             Lnet/minecraft/world/level/levelgen/feature/PillagerOutpostConfiguration;
        //   350: dup            
        //   351: ldc2_w          0.004
        //   354: invokespecial   net/minecraft/world/level/levelgen/feature/PillagerOutpostConfiguration.<init>:(D)V
        //   357: getstatic       net/minecraft/world/level/levelgen/placement/FeatureDecorator.NOPE:Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;
        //   360: getstatic       net/minecraft/world/level/levelgen/feature/DecoratorConfiguration.NONE:Lnet/minecraft/world/level/levelgen/feature/NoneDecoratorConfiguration;
        //   363: invokestatic    net/minecraft/world/level/biome/Biome.makeComposite:(Lnet/minecraft/world/level/levelgen/feature/Feature;Lnet/minecraft/world/level/levelgen/feature/FeatureConfiguration;Lnet/minecraft/world/level/levelgen/placement/FeatureDecorator;Lnet/minecraft/world/level/levelgen/feature/DecoratorConfiguration;)Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //   366: putstatic       net/minecraft/world/level/levelgen/flat/FlatLevelGeneratorSettings.PILLAGER_OUTPOST_COMPOSITE_FEATURE:Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;
        //   369: invokestatic    com/google/common/collect/Maps.newHashMap:()Ljava/util/HashMap;
        //   372: invokedynamic   BootstrapMethod #12, accept:()Ljava/util/function/Consumer;
        //   377: invokestatic    net/minecraft/Util.make:(Ljava/lang/Object;Ljava/util/function/Consumer;)Ljava/lang/Object;
        //   380: checkcast       Ljava/util/Map;
        //   383: putstatic       net/minecraft/world/level/levelgen/flat/FlatLevelGeneratorSettings.STRUCTURE_FEATURES_STEP:Ljava/util/Map;
        //   386: invokestatic    com/google/common/collect/Maps.newHashMap:()Ljava/util/HashMap;
        //   389: invokedynamic   BootstrapMethod #13, accept:()Ljava/util/function/Consumer;
        //   394: invokestatic    net/minecraft/Util.make:(Ljava/lang/Object;Ljava/util/function/Consumer;)Ljava/lang/Object;
        //   397: checkcast       Ljava/util/Map;
        //   400: putstatic       net/minecraft/world/level/levelgen/flat/FlatLevelGeneratorSettings.STRUCTURE_FEATURES:Ljava/util/Map;
        //   403: invokestatic    com/google/common/collect/Maps.newHashMap:()Ljava/util/HashMap;
        //   406: invokedynamic   BootstrapMethod #14, accept:()Ljava/util/function/Consumer;
        //   411: invokestatic    net/minecraft/Util.make:(Ljava/lang/Object;Ljava/util/function/Consumer;)Ljava/lang/Object;
        //   414: checkcast       Ljava/util/Map;
        //   417: putstatic       net/minecraft/world/level/levelgen/flat/FlatLevelGeneratorSettings.STRUCTURE_FEATURES_DEFAULT:Ljava/util/Map;
        //   420: return         
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException: Array index out of range: 2
        //     at java.base/java.util.Vector.get(Vector.java:781)
        //     at com.strobel.assembler.metadata.MetadataResolver.resolve(MetadataResolver.java:82)
        //     at com.strobel.assembler.metadata.CoreMetadataFactory$UnresolvedType.resolve(CoreMetadataFactory.java:616)
        //     at com.strobel.assembler.metadata.MetadataResolver.resolve(MetadataResolver.java:111)
        //     at com.strobel.assembler.metadata.CoreMetadataFactory$UnresolvedType.resolve(CoreMetadataFactory.java:621)
        //     at com.strobel.assembler.metadata.FieldReference.resolve(FieldReference.java:61)
        //     at com.strobel.decompiler.languages.java.ast.JavaResolver$ResolveVisitor.visitMemberReferenceExpression(JavaResolver.java:219)
        //     at com.strobel.decompiler.languages.java.ast.JavaResolver$ResolveVisitor.visitMemberReferenceExpression(JavaResolver.java:40)
        //     at com.strobel.decompiler.languages.java.ast.MemberReferenceExpression.acceptVisitor(MemberReferenceExpression.java:120)
        //     at com.strobel.decompiler.languages.java.ast.JavaResolver.apply(JavaResolver.java:37)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.adjustArgumentsForMethodCallCore(AstMethodBodyBuilder.java:1304)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.adjustArgumentsForMethodCall(AstMethodBodyBuilder.java:1286)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformCall(AstMethodBodyBuilder.java:1197)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:715)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:540)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:554)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:540)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:392)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:333)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:294)
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
}
