package net.minecraft.client.resources.model;

import net.minecraft.client.renderer.block.model.multipart.Selector;
import java.util.stream.Stream;
import net.minecraft.client.renderer.block.BlockModelShaper;
import java.io.InputStream;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.server.packs.resources.Resource;
import java.io.Reader;
import java.io.Closeable;
import org.apache.commons.io.IOUtils;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.StringReader;
import java.io.FileNotFoundException;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.multipart.MultiPart;
import java.util.function.Supplier;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import com.google.common.collect.ImmutableList;
import java.util.Optional;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.properties.Property;
import java.util.function.Predicate;
import java.util.Iterator;
import java.util.Collection;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import java.io.IOException;
import java.util.function.Consumer;
import net.minecraft.Util;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.util.profiling.ProfilerFiller;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.apache.commons.lang3.tuple.Triple;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.client.renderer.block.model.BlockModel;
import com.google.common.base.Splitter;
import java.util.Map;
import com.google.common.annotations.VisibleForTesting;
import org.apache.logging.log4j.Logger;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public class ModelBakery {
    public static final ResourceLocation FIRE_0;
    public static final ResourceLocation FIRE_1;
    public static final ResourceLocation LAVA_FLOW;
    public static final ResourceLocation WATER_FLOW;
    public static final ResourceLocation WATER_OVERLAY;
    public static final ResourceLocation DESTROY_STAGE_0;
    public static final ResourceLocation DESTROY_STAGE_1;
    public static final ResourceLocation DESTROY_STAGE_2;
    public static final ResourceLocation DESTROY_STAGE_3;
    public static final ResourceLocation DESTROY_STAGE_4;
    public static final ResourceLocation DESTROY_STAGE_5;
    public static final ResourceLocation DESTROY_STAGE_6;
    public static final ResourceLocation DESTROY_STAGE_7;
    public static final ResourceLocation DESTROY_STAGE_8;
    public static final ResourceLocation DESTROY_STAGE_9;
    private static final Set<ResourceLocation> UNREFERENCED_TEXTURES;
    private static final Logger LOGGER;
    public static final ModelResourceLocation MISSING_MODEL_LOCATION;
    @VisibleForTesting
    public static final String MISSING_MODEL_MESH;
    private static final Map<String, String> BUILTIN_MODELS;
    private static final Splitter COMMA_SPLITTER;
    private static final Splitter EQUAL_SPLITTER;
    public static final BlockModel GENERATION_MARKER;
    public static final BlockModel BLOCK_ENTITY_MARKER;
    private static final StateDefinition<Block, BlockState> ITEM_FRAME_FAKE_DEFINITION;
    private static final ItemModelGenerator ITEM_MODEL_GENERATOR;
    private static final Map<ResourceLocation, StateDefinition<Block, BlockState>> STATIC_DEFINITIONS;
    private final ResourceManager resourceManager;
    private final TextureAtlas blockAtlas;
    private final BlockColors blockColors;
    private final Set<ResourceLocation> loadingStack;
    private final BlockModelDefinition.Context context;
    private final Map<ResourceLocation, UnbakedModel> unbakedCache;
    private final Map<Triple<ResourceLocation, BlockModelRotation, Boolean>, BakedModel> bakedCache;
    private final Map<ResourceLocation, UnbakedModel> topLevelModels;
    private final Map<ResourceLocation, BakedModel> bakedTopLevelModels;
    private final TextureAtlas.Preparations atlasPreparations;
    private int nextModelGroup;
    private final Object2IntMap<BlockState> modelGroups;
    
    public ModelBakery(final ResourceManager xi, final TextureAtlas dxa, final BlockColors cyp, final ProfilerFiller agn) {
        this.loadingStack = (Set<ResourceLocation>)Sets.newHashSet();
        this.context = new BlockModelDefinition.Context();
        this.unbakedCache = (Map<ResourceLocation, UnbakedModel>)Maps.newHashMap();
        this.bakedCache = (Map<Triple<ResourceLocation, BlockModelRotation, Boolean>, BakedModel>)Maps.newHashMap();
        this.topLevelModels = (Map<ResourceLocation, UnbakedModel>)Maps.newHashMap();
        this.bakedTopLevelModels = (Map<ResourceLocation, BakedModel>)Maps.newHashMap();
        this.nextModelGroup = 1;
        this.modelGroups = Util.make((Object2IntMap)new Object2IntOpenHashMap(), (java.util.function.Consumer<Object2IntMap>)(object2IntOpenHashMap -> object2IntOpenHashMap.defaultReturnValue(-1)));
        this.resourceManager = xi;
        this.blockAtlas = dxa;
        this.blockColors = cyp;
        agn.push("missing_model");
        try {
            this.unbakedCache.put(ModelBakery.MISSING_MODEL_LOCATION, this.loadBlockModel(ModelBakery.MISSING_MODEL_LOCATION));
            this.loadTopLevel(ModelBakery.MISSING_MODEL_LOCATION);
        }
        catch (IOException iOException6) {
            ModelBakery.LOGGER.error("Error loading missing model, should never happen :(", (Throwable)iOException6);
            throw new RuntimeException((Throwable)iOException6);
        }
        agn.popPush("static_definitions");
        ModelBakery.STATIC_DEFINITIONS.forEach((qv, bvu) -> bvu.getPossibleStates().forEach(bvt -> this.loadTopLevel(BlockModelShaper.stateToModelLocation(qv, bvt))));
        agn.popPush("blocks");
        for (final Block bmv7 : Registry.BLOCK) {
            bmv7.getStateDefinition().getPossibleStates().forEach(bvt -> this.loadTopLevel(BlockModelShaper.stateToModelLocation(bvt)));
        }
        agn.popPush("items");
        for (final ResourceLocation qv7 : Registry.ITEM.keySet()) {
            this.loadTopLevel(new ModelResourceLocation(qv7, "inventory"));
        }
        agn.popPush("special");
        this.loadTopLevel(new ModelResourceLocation("minecraft:trident_in_hand#inventory"));
        agn.popPush("textures");
        final Set<String> set6 = (Set<String>)Sets.newLinkedHashSet();
        final Set<ResourceLocation> set7 = (Set<ResourceLocation>)this.topLevelModels.values().stream().flatMap(dyy -> dyy.getTextures((Function<ResourceLocation, UnbakedModel>)this::getModel, set6).stream()).collect(Collectors.toSet());
        set7.addAll((Collection)ModelBakery.UNREFERENCED_TEXTURES);
        set6.forEach(string -> ModelBakery.LOGGER.warn("Unable to resolve texture reference: {}", string));
        agn.popPush("stitching");
        this.atlasPreparations = this.blockAtlas.prepareToStitch(this.resourceManager, (Iterable<ResourceLocation>)set7, agn);
        agn.pop();
    }
    
    public void uploadTextures(final ProfilerFiller agn) {
        agn.push("atlas");
        this.blockAtlas.reload(this.atlasPreparations);
        agn.popPush("baking");
        this.topLevelModels.keySet().forEach(qv -> {
            BakedModel dyp3 = null;
            try {
                dyp3 = this.bake(qv, BlockModelRotation.X0_Y0);
            }
            catch (Exception exception4) {
                ModelBakery.LOGGER.warn("Unable to bake model: '{}': {}", qv, exception4);
            }
            if (dyp3 != null) {
                this.bakedTopLevelModels.put(qv, dyp3);
            }
        });
        agn.pop();
    }
    
    private static Predicate<BlockState> predicate(final StateDefinition<Block, BlockState> bvu, final String string) {
        final Map<Property<?>, Comparable<?>> map3 = (Map<Property<?>, Comparable<?>>)Maps.newHashMap();
        for (final String string2 : ModelBakery.COMMA_SPLITTER.split((CharSequence)string)) {
            final Iterator<String> iterator6 = (Iterator<String>)ModelBakery.EQUAL_SPLITTER.split((CharSequence)string2).iterator();
            if (iterator6.hasNext()) {
                final String string3 = (String)iterator6.next();
                final Property<?> bww8 = bvu.getProperty(string3);
                if (bww8 != null && iterator6.hasNext()) {
                    final String string4 = (String)iterator6.next();
                    final Comparable<?> comparable10 = ModelBakery.<Comparable<?>>getValueHelper(bww8, string4);
                    if (comparable10 == null) {
                        throw new RuntimeException("Unknown value: '" + string4 + "' for blockstate property: '" + string3 + "' " + bww8.getPossibleValues());
                    }
                    map3.put(bww8, comparable10);
                }
                else {
                    if (!string3.isEmpty()) {
                        throw new RuntimeException("Unknown blockstate property: '" + string3 + "'");
                    }
                    continue;
                }
            }
        }
        final Block bmv4 = bvu.getOwner();
        return (Predicate<BlockState>)(bvt -> {
            if (bvt == null || bmv4 != bvt.getBlock()) {
                return false;
            }
            for (final Map.Entry<Property<?>, Comparable<?>> entry5 : map3.entrySet()) {
                if (!Objects.equals(bvt.getValue((Property<Object>)entry5.getKey()), entry5.getValue())) {
                    return false;
                }
            }
            return true;
        });
    }
    
    @Nullable
    static <T extends Comparable<T>> T getValueHelper(final Property<T> bww, final String string) {
        return (T)bww.getValue(string).orElse(null);
    }
    
    public UnbakedModel getModel(final ResourceLocation qv) {
        if (this.unbakedCache.containsKey(qv)) {
            return (UnbakedModel)this.unbakedCache.get(qv);
        }
        if (this.loadingStack.contains(qv)) {
            throw new IllegalStateException(new StringBuilder().append("Circular reference while loading ").append(qv).toString());
        }
        this.loadingStack.add(qv);
        final UnbakedModel dyy3 = (UnbakedModel)this.unbakedCache.get(ModelBakery.MISSING_MODEL_LOCATION);
        while (!this.loadingStack.isEmpty()) {
            final ResourceLocation qv2 = (ResourceLocation)this.loadingStack.iterator().next();
            try {
                if (!this.unbakedCache.containsKey(qv2)) {
                    this.loadModel(qv2);
                }
            }
            catch (BlockStateDefinitionException a5) {
                ModelBakery.LOGGER.warn(a5.getMessage());
                this.unbakedCache.put(qv2, dyy3);
            }
            catch (Exception exception5) {
                ModelBakery.LOGGER.warn("Unable to load model: '{}' referenced from: {}: {}", qv2, qv, exception5);
                this.unbakedCache.put(qv2, dyy3);
            }
            finally {
                this.loadingStack.remove(qv2);
            }
        }
        return (UnbakedModel)this.unbakedCache.getOrDefault(qv, dyy3);
    }
    
    private void loadModel(final ResourceLocation qv) throws Exception {
        if (!(qv instanceof ModelResourceLocation)) {
            this.cacheAndQueueDependencies(qv, this.loadBlockModel(qv));
            return;
        }
        final ModelResourceLocation dyu3 = (ModelResourceLocation)qv;
        if (Objects.equals(dyu3.getVariant(), "inventory")) {
            final ResourceLocation qv2 = new ResourceLocation(qv.getNamespace(), "item/" + qv.getPath());
            final BlockModel doe5 = this.loadBlockModel(qv2);
            this.cacheAndQueueDependencies(dyu3, doe5);
            this.unbakedCache.put(qv2, doe5);
        }
        else {
            final ResourceLocation qv2 = new ResourceLocation(qv.getNamespace(), qv.getPath());
            final StateDefinition<Block, BlockState> bvu5 = (StateDefinition<Block, BlockState>)Optional.ofNullable(ModelBakery.STATIC_DEFINITIONS.get(qv2)).orElseGet(() -> Registry.BLOCK.get(qv2).getStateDefinition());
            this.context.setDefinition(bvu5);
            final List<Property<?>> list6 = (List<Property<?>>)ImmutableList.copyOf((Collection)this.blockColors.getColoringProperties(bvu5.getOwner()));
            final ImmutableList<BlockState> immutableList7 = bvu5.getPossibleStates();
            final Map<ModelResourceLocation, BlockState> map8 = (Map<ModelResourceLocation, BlockState>)Maps.newHashMap();
            immutableList7.forEach(bvt -> {
                final BlockState blockState = (BlockState)map8.put(BlockModelShaper.stateToModelLocation(qv2, bvt), bvt);
            });
            final Map<BlockState, Pair<UnbakedModel, Supplier<ModelGroupKey>>> map9 = (Map<BlockState, Pair<UnbakedModel, Supplier<ModelGroupKey>>>)Maps.newHashMap();
            final ResourceLocation qv3 = new ResourceLocation(qv.getNamespace(), "blockstates/" + qv.getPath() + ".json");
            final UnbakedModel dyy11 = (UnbakedModel)this.unbakedCache.get(ModelBakery.MISSING_MODEL_LOCATION);
            final ModelGroupKey b12 = new ModelGroupKey((List<UnbakedModel>)ImmutableList.of(dyy11), (List<Object>)ImmutableList.of());
            final Pair<UnbakedModel, Supplier<ModelGroupKey>> pair13 = (Pair<UnbakedModel, Supplier<ModelGroupKey>>)Pair.of(dyy11, (() -> b12));
            try {
                List<Pair<String, BlockModelDefinition>> list7;
                try {
                    list7 = (List<Pair<String, BlockModelDefinition>>)this.resourceManager.getResources(qv3).stream().map(xh -> {
                        try (final InputStream inputStream3 = xh.getInputStream()) {
                            return Pair.of(xh.getSourceName(), BlockModelDefinition.fromStream(this.context, (Reader)new InputStreamReader(inputStream3, StandardCharsets.UTF_8)));
                        }
                        catch (Exception exception3) {
                            throw new BlockStateDefinitionException(String.format("Exception loading blockstate definition: '%s' in resourcepack: '%s': %s", new Object[] { xh.getLocation(), xh.getSourceName(), exception3.getMessage() }));
                        }
                    }).collect(Collectors.toList());
                }
                catch (IOException iOException15) {
                    ModelBakery.LOGGER.warn("Exception loading blockstate definition: {}: {}", qv3, iOException15);
                    return;
                }
                for (final Pair<String, BlockModelDefinition> pair14 : list7) {
                    final BlockModelDefinition dof17 = (BlockModelDefinition)pair14.getSecond();
                    final Map<BlockState, Pair<UnbakedModel, Supplier<ModelGroupKey>>> map10 = (Map<BlockState, Pair<UnbakedModel, Supplier<ModelGroupKey>>>)Maps.newIdentityHashMap();
                    MultiPart dos19;
                    if (dof17.isMultiPart()) {
                        dos19 = dof17.getMultiPart();
                        immutableList7.forEach(bvt -> {
                            final Pair pair = (Pair)map10.put(bvt, Pair.of((Object)dos19, (Object)(() -> ModelGroupKey.create(bvt, dos19, (Collection<Property<?>>)list6))));
                        });
                    }
                    else {
                        dos19 = null;
                    }
                    dof17.getVariants().forEach((string, don) -> {
                        try {
                            immutableList7.stream().filter((Predicate)predicate(bvu5, string)).forEach(bvt -> {
                                final Pair<UnbakedModel, Supplier<ModelGroupKey>> pair2 = (Pair<UnbakedModel, Supplier<ModelGroupKey>>)map10.put(bvt, Pair.of((Object)don, (Object)(() -> ModelGroupKey.create(bvt, don, (Collection<Property<?>>)list6))));
                                if (pair2 != null && pair2.getFirst() != dos19) {
                                    map10.put(bvt, pair13);
                                    throw new RuntimeException("Overlapping definition with: " + (String)((Map.Entry)dof17.getVariants().entrySet().stream().filter(entry -> entry.getValue() == pair2.getFirst()).findFirst().get()).getKey());
                                }
                            });
                        }
                        catch (Exception exception12) {
                            ModelBakery.LOGGER.warn("Exception loading blockstate definition: '{}' in resourcepack: '{}' for variant: '{}': {}", qv3, pair14.getFirst(), string, exception12.getMessage());
                        }
                    });
                    map9.putAll((Map)map10);
                }
            }
            catch (BlockStateDefinitionException a14) {
                throw a14;
            }
            catch (Exception exception14) {
                throw new BlockStateDefinitionException(String.format("Exception loading blockstate definition: '%s': %s", new Object[] { qv3, exception14 }));
            }
            finally {
                final Map<ModelGroupKey, Set<BlockState>> map11 = (Map<ModelGroupKey, Set<BlockState>>)Maps.newHashMap();
                map8.forEach((dyu, bvt) -> {
                    Pair<UnbakedModel, Supplier<ModelGroupKey>> pair2 = (Pair<UnbakedModel, Supplier<ModelGroupKey>>)map9.get(bvt);
                    if (pair2 == null) {
                        ModelBakery.LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", qv3, dyu);
                        pair2 = pair13;
                    }
                    this.cacheAndQueueDependencies(dyu, (UnbakedModel)pair2.getFirst());
                    try {
                        final ModelGroupKey b9 = (ModelGroupKey)((Supplier)pair2.getSecond()).get();
                        ((Set)map11.computeIfAbsent(b9, b -> Sets.newIdentityHashSet())).add(bvt);
                    }
                    catch (Exception exception9) {
                        ModelBakery.LOGGER.warn("Exception evaluating model definition: '{}'", dyu, exception9);
                    }
                });
                map11.forEach((b, set) -> {
                    final Iterator<BlockState> iterator4 = (Iterator<BlockState>)set.iterator();
                    while (iterator4.hasNext()) {
                        final BlockState bvt5 = (BlockState)iterator4.next();
                        if (bvt5.getRenderShape() != RenderShape.MODEL) {
                            iterator4.remove();
                            this.modelGroups.put(bvt5, 0);
                        }
                    }
                    if (set.size() > 1) {
                        this.registerModelGroup((Iterable<BlockState>)set);
                    }
                });
            }
        }
    }
    
    private void cacheAndQueueDependencies(final ResourceLocation qv, final UnbakedModel dyy) {
        this.unbakedCache.put(qv, dyy);
        this.loadingStack.addAll((Collection)dyy.getDependencies());
    }
    
    private void loadTopLevel(final ModelResourceLocation dyu) {
        final UnbakedModel dyy3 = this.getModel(dyu);
        this.unbakedCache.put(dyu, dyy3);
        this.topLevelModels.put(dyu, dyy3);
    }
    
    private void registerModelGroup(final Iterable<BlockState> iterable) {
        final int integer3 = this.nextModelGroup++;
        iterable.forEach(bvt -> this.modelGroups.put(bvt, integer3));
    }
    
    @Nullable
    public BakedModel bake(final ResourceLocation qv, final ModelState dyv) {
        final Triple<ResourceLocation, BlockModelRotation, Boolean> triple4 = (Triple<ResourceLocation, BlockModelRotation, Boolean>)Triple.of(qv, dyv.getRotation(), dyv.isUvLocked());
        if (this.bakedCache.containsKey(triple4)) {
            return (BakedModel)this.bakedCache.get(triple4);
        }
        final UnbakedModel dyy5 = this.getModel(qv);
        if (dyy5 instanceof BlockModel) {
            final BlockModel doe6 = (BlockModel)dyy5;
            if (doe6.getRootModel() == ModelBakery.GENERATION_MARKER) {
                return ModelBakery.ITEM_MODEL_GENERATOR.generateBlockModel((Function<ResourceLocation, TextureAtlasSprite>)this.blockAtlas::getSprite, doe6).bake(this, doe6, (Function<ResourceLocation, TextureAtlasSprite>)this.blockAtlas::getSprite, dyv);
            }
        }
        final BakedModel dyp6 = dyy5.bake(this, (Function<ResourceLocation, TextureAtlasSprite>)this.blockAtlas::getSprite, dyv);
        this.bakedCache.put(triple4, dyp6);
        return dyp6;
    }
    
    private BlockModel loadBlockModel(final ResourceLocation qv) throws IOException {
        Reader reader3 = null;
        Resource xh4 = null;
        try {
            final String string5 = qv.getPath();
            if ("builtin/generated".equals(string5)) {
                return ModelBakery.GENERATION_MARKER;
            }
            if ("builtin/entity".equals(string5)) {
                return ModelBakery.BLOCK_ENTITY_MARKER;
            }
            if (string5.startsWith("builtin/")) {
                final String string6 = string5.substring("builtin/".length());
                final String string7 = (String)ModelBakery.BUILTIN_MODELS.get(string6);
                if (string7 == null) {
                    throw new FileNotFoundException(qv.toString());
                }
                reader3 = (Reader)new StringReader(string7);
            }
            else {
                xh4 = this.resourceManager.getResource(new ResourceLocation(qv.getNamespace(), "models/" + qv.getPath() + ".json"));
                reader3 = (Reader)new InputStreamReader(xh4.getInputStream(), StandardCharsets.UTF_8);
            }
            final BlockModel doe6 = BlockModel.fromStream(reader3);
            doe6.name = qv.toString();
            return doe6;
        }
        finally {
            IOUtils.closeQuietly(reader3);
            IOUtils.closeQuietly((Closeable)xh4);
        }
    }
    
    public Map<ResourceLocation, BakedModel> getBakedTopLevelModels() {
        return this.bakedTopLevelModels;
    }
    
    public Object2IntMap<BlockState> getModelGroups() {
        return this.modelGroups;
    }
    
    static {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: dup            
        //     4: ldc_w           "block/fire_0"
        //     7: invokespecial   net/minecraft/resources/ResourceLocation.<init>:(Ljava/lang/String;)V
        //    10: putstatic       net/minecraft/client/resources/model/ModelBakery.FIRE_0:Lnet/minecraft/resources/ResourceLocation;
        //    13: new             Lnet/minecraft/resources/ResourceLocation;
        //    16: dup            
        //    17: ldc_w           "block/fire_1"
        //    20: invokespecial   net/minecraft/resources/ResourceLocation.<init>:(Ljava/lang/String;)V
        //    23: putstatic       net/minecraft/client/resources/model/ModelBakery.FIRE_1:Lnet/minecraft/resources/ResourceLocation;
        //    26: new             Lnet/minecraft/resources/ResourceLocation;
        //    29: dup            
        //    30: ldc_w           "block/lava_flow"
        //    33: invokespecial   net/minecraft/resources/ResourceLocation.<init>:(Ljava/lang/String;)V
        //    36: putstatic       net/minecraft/client/resources/model/ModelBakery.LAVA_FLOW:Lnet/minecraft/resources/ResourceLocation;
        //    39: new             Lnet/minecraft/resources/ResourceLocation;
        //    42: dup            
        //    43: ldc_w           "block/water_flow"
        //    46: invokespecial   net/minecraft/resources/ResourceLocation.<init>:(Ljava/lang/String;)V
        //    49: putstatic       net/minecraft/client/resources/model/ModelBakery.WATER_FLOW:Lnet/minecraft/resources/ResourceLocation;
        //    52: new             Lnet/minecraft/resources/ResourceLocation;
        //    55: dup            
        //    56: ldc_w           "block/water_overlay"
        //    59: invokespecial   net/minecraft/resources/ResourceLocation.<init>:(Ljava/lang/String;)V
        //    62: putstatic       net/minecraft/client/resources/model/ModelBakery.WATER_OVERLAY:Lnet/minecraft/resources/ResourceLocation;
        //    65: new             Lnet/minecraft/resources/ResourceLocation;
        //    68: dup            
        //    69: ldc_w           "block/destroy_stage_0"
        //    72: invokespecial   net/minecraft/resources/ResourceLocation.<init>:(Ljava/lang/String;)V
        //    75: putstatic       net/minecraft/client/resources/model/ModelBakery.DESTROY_STAGE_0:Lnet/minecraft/resources/ResourceLocation;
        //    78: new             Lnet/minecraft/resources/ResourceLocation;
        //    81: dup            
        //    82: ldc_w           "block/destroy_stage_1"
        //    85: invokespecial   net/minecraft/resources/ResourceLocation.<init>:(Ljava/lang/String;)V
        //    88: putstatic       net/minecraft/client/resources/model/ModelBakery.DESTROY_STAGE_1:Lnet/minecraft/resources/ResourceLocation;
        //    91: new             Lnet/minecraft/resources/ResourceLocation;
        //    94: dup            
        //    95: ldc_w           "block/destroy_stage_2"
        //    98: invokespecial   net/minecraft/resources/ResourceLocation.<init>:(Ljava/lang/String;)V
        //   101: putstatic       net/minecraft/client/resources/model/ModelBakery.DESTROY_STAGE_2:Lnet/minecraft/resources/ResourceLocation;
        //   104: new             Lnet/minecraft/resources/ResourceLocation;
        //   107: dup            
        //   108: ldc_w           "block/destroy_stage_3"
        //   111: invokespecial   net/minecraft/resources/ResourceLocation.<init>:(Ljava/lang/String;)V
        //   114: putstatic       net/minecraft/client/resources/model/ModelBakery.DESTROY_STAGE_3:Lnet/minecraft/resources/ResourceLocation;
        //   117: new             Lnet/minecraft/resources/ResourceLocation;
        //   120: dup            
        //   121: ldc_w           "block/destroy_stage_4"
        //   124: invokespecial   net/minecraft/resources/ResourceLocation.<init>:(Ljava/lang/String;)V
        //   127: putstatic       net/minecraft/client/resources/model/ModelBakery.DESTROY_STAGE_4:Lnet/minecraft/resources/ResourceLocation;
        //   130: new             Lnet/minecraft/resources/ResourceLocation;
        //   133: dup            
        //   134: ldc_w           "block/destroy_stage_5"
        //   137: invokespecial   net/minecraft/resources/ResourceLocation.<init>:(Ljava/lang/String;)V
        //   140: putstatic       net/minecraft/client/resources/model/ModelBakery.DESTROY_STAGE_5:Lnet/minecraft/resources/ResourceLocation;
        //   143: new             Lnet/minecraft/resources/ResourceLocation;
        //   146: dup            
        //   147: ldc_w           "block/destroy_stage_6"
        //   150: invokespecial   net/minecraft/resources/ResourceLocation.<init>:(Ljava/lang/String;)V
        //   153: putstatic       net/minecraft/client/resources/model/ModelBakery.DESTROY_STAGE_6:Lnet/minecraft/resources/ResourceLocation;
        //   156: new             Lnet/minecraft/resources/ResourceLocation;
        //   159: dup            
        //   160: ldc_w           "block/destroy_stage_7"
        //   163: invokespecial   net/minecraft/resources/ResourceLocation.<init>:(Ljava/lang/String;)V
        //   166: putstatic       net/minecraft/client/resources/model/ModelBakery.DESTROY_STAGE_7:Lnet/minecraft/resources/ResourceLocation;
        //   169: new             Lnet/minecraft/resources/ResourceLocation;
        //   172: dup            
        //   173: ldc_w           "block/destroy_stage_8"
        //   176: invokespecial   net/minecraft/resources/ResourceLocation.<init>:(Ljava/lang/String;)V
        //   179: putstatic       net/minecraft/client/resources/model/ModelBakery.DESTROY_STAGE_8:Lnet/minecraft/resources/ResourceLocation;
        //   182: new             Lnet/minecraft/resources/ResourceLocation;
        //   185: dup            
        //   186: ldc_w           "block/destroy_stage_9"
        //   189: invokespecial   net/minecraft/resources/ResourceLocation.<init>:(Ljava/lang/String;)V
        //   192: putstatic       net/minecraft/client/resources/model/ModelBakery.DESTROY_STAGE_9:Lnet/minecraft/resources/ResourceLocation;
        //   195: bipush          20
        //   197: anewarray       Lnet/minecraft/resources/ResourceLocation;
        //   200: dup            
        //   201: iconst_0       
        //   202: getstatic       net/minecraft/client/resources/model/ModelBakery.WATER_FLOW:Lnet/minecraft/resources/ResourceLocation;
        //   205: aastore        
        //   206: dup            
        //   207: iconst_1       
        //   208: getstatic       net/minecraft/client/resources/model/ModelBakery.LAVA_FLOW:Lnet/minecraft/resources/ResourceLocation;
        //   211: aastore        
        //   212: dup            
        //   213: iconst_2       
        //   214: getstatic       net/minecraft/client/resources/model/ModelBakery.WATER_OVERLAY:Lnet/minecraft/resources/ResourceLocation;
        //   217: aastore        
        //   218: dup            
        //   219: iconst_3       
        //   220: getstatic       net/minecraft/client/resources/model/ModelBakery.FIRE_0:Lnet/minecraft/resources/ResourceLocation;
        //   223: aastore        
        //   224: dup            
        //   225: iconst_4       
        //   226: getstatic       net/minecraft/client/resources/model/ModelBakery.FIRE_1:Lnet/minecraft/resources/ResourceLocation;
        //   229: aastore        
        //   230: dup            
        //   231: iconst_5       
        //   232: getstatic       net/minecraft/client/resources/model/ModelBakery.DESTROY_STAGE_0:Lnet/minecraft/resources/ResourceLocation;
        //   235: aastore        
        //   236: dup            
        //   237: bipush          6
        //   239: getstatic       net/minecraft/client/resources/model/ModelBakery.DESTROY_STAGE_1:Lnet/minecraft/resources/ResourceLocation;
        //   242: aastore        
        //   243: dup            
        //   244: bipush          7
        //   246: getstatic       net/minecraft/client/resources/model/ModelBakery.DESTROY_STAGE_2:Lnet/minecraft/resources/ResourceLocation;
        //   249: aastore        
        //   250: dup            
        //   251: bipush          8
        //   253: getstatic       net/minecraft/client/resources/model/ModelBakery.DESTROY_STAGE_3:Lnet/minecraft/resources/ResourceLocation;
        //   256: aastore        
        //   257: dup            
        //   258: bipush          9
        //   260: getstatic       net/minecraft/client/resources/model/ModelBakery.DESTROY_STAGE_4:Lnet/minecraft/resources/ResourceLocation;
        //   263: aastore        
        //   264: dup            
        //   265: bipush          10
        //   267: getstatic       net/minecraft/client/resources/model/ModelBakery.DESTROY_STAGE_5:Lnet/minecraft/resources/ResourceLocation;
        //   270: aastore        
        //   271: dup            
        //   272: bipush          11
        //   274: getstatic       net/minecraft/client/resources/model/ModelBakery.DESTROY_STAGE_6:Lnet/minecraft/resources/ResourceLocation;
        //   277: aastore        
        //   278: dup            
        //   279: bipush          12
        //   281: getstatic       net/minecraft/client/resources/model/ModelBakery.DESTROY_STAGE_7:Lnet/minecraft/resources/ResourceLocation;
        //   284: aastore        
        //   285: dup            
        //   286: bipush          13
        //   288: getstatic       net/minecraft/client/resources/model/ModelBakery.DESTROY_STAGE_8:Lnet/minecraft/resources/ResourceLocation;
        //   291: aastore        
        //   292: dup            
        //   293: bipush          14
        //   295: getstatic       net/minecraft/client/resources/model/ModelBakery.DESTROY_STAGE_9:Lnet/minecraft/resources/ResourceLocation;
        //   298: aastore        
        //   299: dup            
        //   300: bipush          15
        //   302: new             Lnet/minecraft/resources/ResourceLocation;
        //   305: dup            
        //   306: ldc_w           "item/empty_armor_slot_helmet"
        //   309: invokespecial   net/minecraft/resources/ResourceLocation.<init>:(Ljava/lang/String;)V
        //   312: aastore        
        //   313: dup            
        //   314: bipush          16
        //   316: new             Lnet/minecraft/resources/ResourceLocation;
        //   319: dup            
        //   320: ldc_w           "item/empty_armor_slot_chestplate"
        //   323: invokespecial   net/minecraft/resources/ResourceLocation.<init>:(Ljava/lang/String;)V
        //   326: aastore        
        //   327: dup            
        //   328: bipush          17
        //   330: new             Lnet/minecraft/resources/ResourceLocation;
        //   333: dup            
        //   334: ldc_w           "item/empty_armor_slot_leggings"
        //   337: invokespecial   net/minecraft/resources/ResourceLocation.<init>:(Ljava/lang/String;)V
        //   340: aastore        
        //   341: dup            
        //   342: bipush          18
        //   344: new             Lnet/minecraft/resources/ResourceLocation;
        //   347: dup            
        //   348: ldc_w           "item/empty_armor_slot_boots"
        //   351: invokespecial   net/minecraft/resources/ResourceLocation.<init>:(Ljava/lang/String;)V
        //   354: aastore        
        //   355: dup            
        //   356: bipush          19
        //   358: new             Lnet/minecraft/resources/ResourceLocation;
        //   361: dup            
        //   362: ldc_w           "item/empty_armor_slot_shield"
        //   365: invokespecial   net/minecraft/resources/ResourceLocation.<init>:(Ljava/lang/String;)V
        //   368: aastore        
        //   369: invokestatic    com/google/common/collect/Sets.newHashSet:([Ljava/lang/Object;)Ljava/util/HashSet;
        //   372: putstatic       net/minecraft/client/resources/model/ModelBakery.UNREFERENCED_TEXTURES:Ljava/util/Set;
        //   375: invokestatic    org/apache/logging/log4j/LogManager.getLogger:()Lorg/apache/logging/log4j/Logger;
        //   378: putstatic       net/minecraft/client/resources/model/ModelBakery.LOGGER:Lorg/apache/logging/log4j/Logger;
        //   381: new             Lnet/minecraft/client/resources/model/ModelResourceLocation;
        //   384: dup            
        //   385: ldc_w           "builtin/missing"
        //   388: ldc_w           "missing"
        //   391: invokespecial   net/minecraft/client/resources/model/ModelResourceLocation.<init>:(Ljava/lang/String;Ljava/lang/String;)V
        //   394: putstatic       net/minecraft/client/resources/model/ModelBakery.MISSING_MODEL_LOCATION:Lnet/minecraft/client/resources/model/ModelResourceLocation;
        //   397: new             Ljava/lang/StringBuilder;
        //   400: dup            
        //   401: invokespecial   java/lang/StringBuilder.<init>:()V
        //   404: ldc_w           "{    'textures': {       'particle': '"
        //   407: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   410: invokestatic    net/minecraft/client/renderer/texture/MissingTextureAtlasSprite.getLocation:()Lnet/minecraft/resources/ResourceLocation;
        //   413: invokevirtual   net/minecraft/resources/ResourceLocation.getPath:()Ljava/lang/String;
        //   416: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   419: ldc_w           "',       'missingno': '"
        //   422: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   425: invokestatic    net/minecraft/client/renderer/texture/MissingTextureAtlasSprite.getLocation:()Lnet/minecraft/resources/ResourceLocation;
        //   428: invokevirtual   net/minecraft/resources/ResourceLocation.getPath:()Ljava/lang/String;
        //   431: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   434: ldc_w           "'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}"
        //   437: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   440: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   443: bipush          39
        //   445: bipush          34
        //   447: invokevirtual   java/lang/String.replace:(CC)Ljava/lang/String;
        //   450: putstatic       net/minecraft/client/resources/model/ModelBakery.MISSING_MODEL_MESH:Ljava/lang/String;
        //   453: ldc_w           "missing"
        //   456: getstatic       net/minecraft/client/resources/model/ModelBakery.MISSING_MODEL_MESH:Ljava/lang/String;
        //   459: invokestatic    com/google/common/collect/ImmutableMap.of:(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap;
        //   462: invokestatic    com/google/common/collect/Maps.newHashMap:(Ljava/util/Map;)Ljava/util/HashMap;
        //   465: putstatic       net/minecraft/client/resources/model/ModelBakery.BUILTIN_MODELS:Ljava/util/Map;
        //   468: bipush          44
        //   470: invokestatic    com/google/common/base/Splitter.on:(C)Lcom/google/common/base/Splitter;
        //   473: putstatic       net/minecraft/client/resources/model/ModelBakery.COMMA_SPLITTER:Lcom/google/common/base/Splitter;
        //   476: bipush          61
        //   478: invokestatic    com/google/common/base/Splitter.on:(C)Lcom/google/common/base/Splitter;
        //   481: iconst_2       
        //   482: invokevirtual   com/google/common/base/Splitter.limit:(I)Lcom/google/common/base/Splitter;
        //   485: putstatic       net/minecraft/client/resources/model/ModelBakery.EQUAL_SPLITTER:Lcom/google/common/base/Splitter;
        //   488: ldc_w           "{}"
        //   491: invokestatic    net/minecraft/client/renderer/block/model/BlockModel.fromString:(Ljava/lang/String;)Lnet/minecraft/client/renderer/block/model/BlockModel;
        //   494: invokedynamic   BootstrapMethod #24, accept:()Ljava/util/function/Consumer;
        //   499: invokestatic    net/minecraft/Util.make:(Ljava/lang/Object;Ljava/util/function/Consumer;)Ljava/lang/Object;
        //   502: checkcast       Lnet/minecraft/client/renderer/block/model/BlockModel;
        //   505: putstatic       net/minecraft/client/resources/model/ModelBakery.GENERATION_MARKER:Lnet/minecraft/client/renderer/block/model/BlockModel;
        //   508: ldc_w           "{}"
        //   511: invokestatic    net/minecraft/client/renderer/block/model/BlockModel.fromString:(Ljava/lang/String;)Lnet/minecraft/client/renderer/block/model/BlockModel;
        //   514: invokedynamic   BootstrapMethod #25, accept:()Ljava/util/function/Consumer;
        //   519: invokestatic    net/minecraft/Util.make:(Ljava/lang/Object;Ljava/util/function/Consumer;)Ljava/lang/Object;
        //   522: checkcast       Lnet/minecraft/client/renderer/block/model/BlockModel;
        //   525: putstatic       net/minecraft/client/resources/model/ModelBakery.BLOCK_ENTITY_MARKER:Lnet/minecraft/client/renderer/block/model/BlockModel;
        //   528: new             Lnet/minecraft/world/level/block/state/StateDefinition$Builder;
        //   531: dup            
        //   532: getstatic       net/minecraft/world/level/block/Blocks.AIR:Lnet/minecraft/world/level/block/Block;
        //   535: invokespecial   net/minecraft/world/level/block/state/StateDefinition$Builder.<init>:(Ljava/lang/Object;)V
        //   538: iconst_1       
        //   539: anewarray       Lnet/minecraft/world/level/block/state/properties/Property;
        //   542: dup            
        //   543: iconst_0       
        //   544: ldc_w           "map"
        //   547: invokestatic    net/minecraft/world/level/block/state/properties/BooleanProperty.create:(Ljava/lang/String;)Lnet/minecraft/world/level/block/state/properties/BooleanProperty;
        //   550: aastore        
        //   551: invokevirtual   net/minecraft/world/level/block/state/StateDefinition$Builder.add:([Lnet/minecraft/world/level/block/state/properties/Property;)Lnet/minecraft/world/level/block/state/StateDefinition$Builder;
        //   554: invokedynamic   BootstrapMethod #26, create:()Lnet/minecraft/world/level/block/state/StateDefinition$Factory;
        //   559: invokevirtual   net/minecraft/world/level/block/state/StateDefinition$Builder.create:(Lnet/minecraft/world/level/block/state/StateDefinition$Factory;)Lnet/minecraft/world/level/block/state/StateDefinition;
        //   562: putstatic       net/minecraft/client/resources/model/ModelBakery.ITEM_FRAME_FAKE_DEFINITION:Lnet/minecraft/world/level/block/state/StateDefinition;
        //   565: new             Lnet/minecraft/client/renderer/block/model/ItemModelGenerator;
        //   568: dup            
        //   569: invokespecial   net/minecraft/client/renderer/block/model/ItemModelGenerator.<init>:()V
        //   572: putstatic       net/minecraft/client/resources/model/ModelBakery.ITEM_MODEL_GENERATOR:Lnet/minecraft/client/renderer/block/model/ItemModelGenerator;
        //   575: new             Lnet/minecraft/resources/ResourceLocation;
        //   578: dup            
        //   579: ldc_w           "item_frame"
        //   582: invokespecial   net/minecraft/resources/ResourceLocation.<init>:(Ljava/lang/String;)V
        //   585: getstatic       net/minecraft/client/resources/model/ModelBakery.ITEM_FRAME_FAKE_DEFINITION:Lnet/minecraft/world/level/block/state/StateDefinition;
        //   588: invokestatic    com/google/common/collect/ImmutableMap.of:(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap;
        //   591: putstatic       net/minecraft/client/resources/model/ModelBakery.STATIC_DEFINITIONS:Ljava/util/Map;
        //   594: return         
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
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:173)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:39)
        //     at com.strobel.assembler.metadata.MetadataHelper.substituteGenericArguments(MetadataHelper.java:1100)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2676)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:778)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2669)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:770)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:766)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1072)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:672)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:655)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:365)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:96)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:344)
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
    
    static class BlockStateDefinitionException extends RuntimeException {
        public BlockStateDefinitionException(final String string) {
            super(string);
        }
    }
    
    static class ModelGroupKey {
        private final List<UnbakedModel> models;
        private final List<Object> coloringValues;
        
        public ModelGroupKey(final List<UnbakedModel> list1, final List<Object> list2) {
            this.models = list1;
            this.coloringValues = list2;
        }
        
        public boolean equals(final Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof ModelGroupKey) {
                final ModelGroupKey b3 = (ModelGroupKey)object;
                return Objects.equals(this.models, b3.models) && Objects.equals(this.coloringValues, b3.coloringValues);
            }
            return false;
        }
        
        public int hashCode() {
            return 31 * this.models.hashCode() + this.coloringValues.hashCode();
        }
        
        public static ModelGroupKey create(final BlockState bvt, final MultiPart dos, final Collection<Property<?>> collection) {
            final StateDefinition<Block, BlockState> bvu4 = bvt.getBlock().getStateDefinition();
            final List<UnbakedModel> list5 = (List<UnbakedModel>)dos.getSelectors().stream().filter(dou -> dou.getPredicate(bvu4).test(bvt)).map(Selector::getVariant).collect(ImmutableList.toImmutableList());
            final List<Object> list6 = getColoringValues(bvt, collection);
            return new ModelGroupKey(list5, list6);
        }
        
        public static ModelGroupKey create(final BlockState bvt, final UnbakedModel dyy, final Collection<Property<?>> collection) {
            final List<Object> list4 = getColoringValues(bvt, collection);
            return new ModelGroupKey((List<UnbakedModel>)ImmutableList.of(dyy), list4);
        }
        
        private static List<Object> getColoringValues(final BlockState bvt, final Collection<Property<?>> collection) {
            return (List<Object>)collection.stream().map(bvt::getValue).collect(ImmutableList.toImmutableList());
        }
    }
}
