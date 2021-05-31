package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import com.google.gson.JsonObject;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializer;
import java.lang.reflect.Type;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import net.minecraft.core.Direction;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.client.resources.model.BuiltInModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import java.util.Objects;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import java.util.stream.Collectors;
import java.util.Iterator;
import java.util.Set;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.function.Function;
import net.minecraft.client.resources.model.ModelBakery;
import java.io.StringReader;
import net.minecraft.util.GsonHelper;
import java.io.Reader;
import net.minecraft.resources.ResourceLocation;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.List;
import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.resources.model.UnbakedModel;

public class BlockModel implements UnbakedModel {
    private static final Logger LOGGER;
    private static final FaceBakery FACE_BAKERY;
    @VisibleForTesting
    static final Gson GSON;
    private final List<BlockElement> elements;
    private final boolean isGui3d;
    private final boolean hasAmbientOcclusion;
    private final ItemTransforms transforms;
    private final List<ItemOverride> overrides;
    public String name;
    @VisibleForTesting
    protected final Map<String, String> textureMap;
    @Nullable
    protected BlockModel parent;
    @Nullable
    protected ResourceLocation parentLocation;
    
    public static BlockModel fromStream(final Reader reader) {
        return GsonHelper.<BlockModel>fromJson(BlockModel.GSON, reader, BlockModel.class);
    }
    
    public static BlockModel fromString(final String string) {
        return fromStream((Reader)new StringReader(string));
    }
    
    public BlockModel(@Nullable final ResourceLocation qv, final List<BlockElement> list2, final Map<String, String> map, final boolean boolean4, final boolean boolean5, final ItemTransforms dom, final List<ItemOverride> list7) {
        this.name = "";
        this.elements = list2;
        this.hasAmbientOcclusion = boolean4;
        this.isGui3d = boolean5;
        this.textureMap = map;
        this.parentLocation = qv;
        this.transforms = dom;
        this.overrides = list7;
    }
    
    public List<BlockElement> getElements() {
        if (this.elements.isEmpty() && this.parent != null) {
            return this.parent.getElements();
        }
        return this.elements;
    }
    
    public boolean hasAmbientOcclusion() {
        if (this.parent != null) {
            return this.parent.hasAmbientOcclusion();
        }
        return this.hasAmbientOcclusion;
    }
    
    public boolean isGui3d() {
        return this.isGui3d;
    }
    
    public List<ItemOverride> getOverrides() {
        return this.overrides;
    }
    
    private ItemOverrides getItemOverrides(final ModelBakery dys, final BlockModel doe) {
        if (this.overrides.isEmpty()) {
            return ItemOverrides.EMPTY;
        }
        return new ItemOverrides(dys, doe, (Function<ResourceLocation, UnbakedModel>)dys::getModel, this.overrides);
    }
    
    public Collection<ResourceLocation> getDependencies() {
        final Set<ResourceLocation> set2 = (Set<ResourceLocation>)Sets.newHashSet();
        for (final ItemOverride doj4 : this.overrides) {
            set2.add(doj4.getModel());
        }
        if (this.parentLocation != null) {
            set2.add(this.parentLocation);
        }
        return (Collection<ResourceLocation>)set2;
    }
    
    public Collection<ResourceLocation> getTextures(final Function<ResourceLocation, UnbakedModel> function, final Set<String> set) {
        final Set<UnbakedModel> set2 = (Set<UnbakedModel>)Sets.newLinkedHashSet();
        for (BlockModel doe5 = this; doe5.parentLocation != null && doe5.parent == null; doe5 = doe5.parent) {
            set2.add(doe5);
            UnbakedModel dyy6 = (UnbakedModel)function.apply(doe5.parentLocation);
            if (dyy6 == null) {
                BlockModel.LOGGER.warn("No parent '{}' while loading model '{}'", this.parentLocation, doe5);
            }
            if (set2.contains(dyy6)) {
                BlockModel.LOGGER.warn("Found 'parent' loop while loading model '{}' in chain: {} -> {}", doe5, set2.stream().map(Object::toString).collect(Collectors.joining(" -> ")), this.parentLocation);
                dyy6 = null;
            }
            if (dyy6 == null) {
                doe5.parentLocation = ModelBakery.MISSING_MODEL_LOCATION;
                dyy6 = (UnbakedModel)function.apply(doe5.parentLocation);
            }
            if (!(dyy6 instanceof BlockModel)) {
                throw new IllegalStateException("BlockModel parent has to be a block model.");
            }
            doe5.parent = (BlockModel)dyy6;
        }
        final Set<ResourceLocation> set3 = (Set<ResourceLocation>)Sets.newHashSet((Object[])new ResourceLocation[] { new ResourceLocation(this.getTexture("particle")) });
        for (final BlockElement doa8 : this.getElements()) {
            for (final BlockElementFace dob10 : doa8.faces.values()) {
                final String string11 = this.getTexture(dob10.texture);
                if (Objects.equals(string11, MissingTextureAtlasSprite.getLocation().toString())) {
                    set.add(String.format("%s in %s", new Object[] { dob10.texture, this.name }));
                }
                set3.add(new ResourceLocation(string11));
            }
        }
        this.overrides.forEach(doj -> {
            final UnbakedModel dyy6 = (UnbakedModel)function.apply(doj.getModel());
            if (Objects.equals(dyy6, this)) {
                return;
            }
            set3.addAll((Collection)dyy6.getTextures(function, set));
        });
        if (this.getRootModel() == ModelBakery.GENERATION_MARKER) {
            ItemModelGenerator.LAYERS.forEach(string -> set3.add(new ResourceLocation(this.getTexture(string))));
        }
        return (Collection<ResourceLocation>)set3;
    }
    
    public BakedModel bake(final ModelBakery dys, final Function<ResourceLocation, TextureAtlasSprite> function, final ModelState dyv) {
        return this.bake(dys, this, function, dyv);
    }
    
    public BakedModel bake(final ModelBakery dys, final BlockModel doe, final Function<ResourceLocation, TextureAtlasSprite> function, final ModelState dyv) {
        final TextureAtlasSprite dxb6 = (TextureAtlasSprite)function.apply(new ResourceLocation(this.getTexture("particle")));
        if (this.getRootModel() == ModelBakery.BLOCK_ENTITY_MARKER) {
            return new BuiltInModel(this.getTransforms(), this.getItemOverrides(dys, doe), dxb6);
        }
        final SimpleBakedModel.Builder a7 = new SimpleBakedModel.Builder(this, this.getItemOverrides(dys, doe)).particle(dxb6);
        for (final BlockElement doa9 : this.getElements()) {
            for (final Direction fb11 : doa9.faces.keySet()) {
                final BlockElementFace dob12 = (BlockElementFace)doa9.faces.get(fb11);
                final TextureAtlasSprite dxb7 = (TextureAtlasSprite)function.apply(new ResourceLocation(this.getTexture(dob12.texture)));
                if (dob12.cullForDirection == null) {
                    a7.addUnculledFace(bakeFace(doa9, dob12, dxb7, fb11, dyv));
                }
                else {
                    a7.addCulledFace(dyv.getRotation().rotate(dob12.cullForDirection), bakeFace(doa9, dob12, dxb7, fb11, dyv));
                }
            }
        }
        return a7.build();
    }
    
    private static BakedQuad bakeFace(final BlockElement doa, final BlockElementFace dob, final TextureAtlasSprite dxb, final Direction fb, final ModelState dyv) {
        return BlockModel.FACE_BAKERY.bakeQuad(doa.from, doa.to, dob, dxb, fb, dyv, doa.rotation, doa.shade);
    }
    
    public boolean hasTexture(final String string) {
        return !MissingTextureAtlasSprite.getLocation().toString().equals(this.getTexture(string));
    }
    
    public String getTexture(String string) {
        if (!this.isTextureReference(string)) {
            string = '#' + string;
        }
        return this.getTexture(string, new Bookkeep(this));
    }
    
    private String getTexture(final String string, final Bookkeep a) {
        if (!this.isTextureReference(string)) {
            return string;
        }
        if (this == a.maxDepth) {
            BlockModel.LOGGER.warn("Unable to resolve texture due to upward reference: {} in {}", string, this.name);
            return MissingTextureAtlasSprite.getLocation().toString();
        }
        String string2 = (String)this.textureMap.get(string.substring(1));
        if (string2 == null && this.parent != null) {
            string2 = this.parent.getTexture(string, a);
        }
        a.maxDepth = this;
        if (string2 != null && this.isTextureReference(string2)) {
            string2 = a.root.getTexture(string2, a);
        }
        if (string2 == null || this.isTextureReference(string2)) {
            return MissingTextureAtlasSprite.getLocation().toString();
        }
        return string2;
    }
    
    private boolean isTextureReference(final String string) {
        return string.charAt(0) == '#';
    }
    
    public BlockModel getRootModel() {
        return (this.parent == null) ? this : this.parent.getRootModel();
    }
    
    public ItemTransforms getTransforms() {
        final ItemTransform dol2 = this.getTransform(ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND);
        final ItemTransform dol3 = this.getTransform(ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
        final ItemTransform dol4 = this.getTransform(ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND);
        final ItemTransform dol5 = this.getTransform(ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
        final ItemTransform dol6 = this.getTransform(ItemTransforms.TransformType.HEAD);
        final ItemTransform dol7 = this.getTransform(ItemTransforms.TransformType.GUI);
        final ItemTransform dol8 = this.getTransform(ItemTransforms.TransformType.GROUND);
        final ItemTransform dol9 = this.getTransform(ItemTransforms.TransformType.FIXED);
        return new ItemTransforms(dol2, dol3, dol4, dol5, dol6, dol7, dol8, dol9);
    }
    
    private ItemTransform getTransform(final ItemTransforms.TransformType b) {
        if (this.parent != null && !this.transforms.hasTransform(b)) {
            return this.parent.getTransform(b);
        }
        return this.transforms.getTransform(b);
    }
    
    public String toString() {
        return this.name;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        FACE_BAKERY = new FaceBakery();
        GSON = new GsonBuilder().registerTypeAdapter((Type)BlockModel.class, new Deserializer()).registerTypeAdapter((Type)BlockElement.class, new BlockElement.Deserializer()).registerTypeAdapter((Type)BlockElementFace.class, new BlockElementFace.Deserializer()).registerTypeAdapter((Type)BlockFaceUV.class, new BlockFaceUV.Deserializer()).registerTypeAdapter((Type)ItemTransform.class, new ItemTransform.Deserializer()).registerTypeAdapter((Type)ItemTransforms.class, new ItemTransforms.Deserializer()).registerTypeAdapter((Type)ItemOverride.class, new ItemOverride.Deserializer()).create();
    }
    
    static final class Bookkeep {
        public final BlockModel root;
        public BlockModel maxDepth;
        
        private Bookkeep(final BlockModel doe) {
            this.root = doe;
        }
    }
    
    public static class Deserializer implements JsonDeserializer<BlockModel> {
        public BlockModel deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            final JsonObject jsonObject5 = jsonElement.getAsJsonObject();
            final List<BlockElement> list6 = this.getElements(jsonDeserializationContext, jsonObject5);
            final String string7 = this.getParentName(jsonObject5);
            final Map<String, String> map8 = this.getTextureMap(jsonObject5);
            final boolean boolean9 = this.getAmbientOcclusion(jsonObject5);
            ItemTransforms dom10 = ItemTransforms.NO_TRANSFORMS;
            if (jsonObject5.has("display")) {
                final JsonObject jsonObject6 = GsonHelper.getAsJsonObject(jsonObject5, "display");
                dom10 = (ItemTransforms)jsonDeserializationContext.deserialize((JsonElement)jsonObject6, (Type)ItemTransforms.class);
            }
            final List<ItemOverride> list7 = this.getOverrides(jsonDeserializationContext, jsonObject5);
            final ResourceLocation qv12 = string7.isEmpty() ? null : new ResourceLocation(string7);
            return new BlockModel(qv12, list6, map8, boolean9, true, dom10, list7);
        }
        
        protected List<ItemOverride> getOverrides(final JsonDeserializationContext jsonDeserializationContext, final JsonObject jsonObject) {
            final List<ItemOverride> list4 = (List<ItemOverride>)Lists.newArrayList();
            if (jsonObject.has("overrides")) {
                final JsonArray jsonArray5 = GsonHelper.getAsJsonArray(jsonObject, "overrides");
                for (final JsonElement jsonElement7 : jsonArray5) {
                    list4.add(jsonDeserializationContext.deserialize(jsonElement7, (Type)ItemOverride.class));
                }
            }
            return list4;
        }
        
        private Map<String, String> getTextureMap(final JsonObject jsonObject) {
            final Map<String, String> map3 = (Map<String, String>)Maps.newHashMap();
            if (jsonObject.has("textures")) {
                final JsonObject jsonObject2 = GsonHelper.getAsJsonObject(jsonObject, "textures");
                for (final Map.Entry<String, JsonElement> entry6 : jsonObject2.entrySet()) {
                    map3.put(entry6.getKey(), ((JsonElement)entry6.getValue()).getAsString());
                }
            }
            return map3;
        }
        
        private String getParentName(final JsonObject jsonObject) {
            return GsonHelper.getAsString(jsonObject, "parent", "");
        }
        
        protected boolean getAmbientOcclusion(final JsonObject jsonObject) {
            return GsonHelper.getAsBoolean(jsonObject, "ambientocclusion", true);
        }
        
        protected List<BlockElement> getElements(final JsonDeserializationContext jsonDeserializationContext, final JsonObject jsonObject) {
            final List<BlockElement> list4 = (List<BlockElement>)Lists.newArrayList();
            if (jsonObject.has("elements")) {
                for (final JsonElement jsonElement6 : GsonHelper.getAsJsonArray(jsonObject, "elements")) {
                    list4.add(jsonDeserializationContext.deserialize(jsonElement6, (Type)BlockElement.class));
                }
            }
            return list4;
        }
    }
}
