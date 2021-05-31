package net.minecraft.advancements.critereon;

import com.google.gson.JsonSyntaxException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.Feature;
import com.google.gson.JsonObject;
import com.google.gson.JsonNull;
import com.google.gson.JsonElement;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import javax.annotation.Nullable;
import net.minecraft.world.level.biome.Biome;

public class LocationPredicate {
    public static final LocationPredicate ANY;
    private final MinMaxBounds.Floats x;
    private final MinMaxBounds.Floats y;
    private final MinMaxBounds.Floats z;
    @Nullable
    private final Biome biome;
    @Nullable
    private final StructureFeature<?> feature;
    @Nullable
    private final DimensionType dimension;
    
    public LocationPredicate(final MinMaxBounds.Floats c1, final MinMaxBounds.Floats c2, final MinMaxBounds.Floats c3, @Nullable final Biome bio, @Nullable final StructureFeature<?> ceu, @Nullable final DimensionType byn) {
        this.x = c1;
        this.y = c2;
        this.z = c3;
        this.biome = bio;
        this.feature = ceu;
        this.dimension = byn;
    }
    
    public static LocationPredicate inBiome(final Biome bio) {
        return new LocationPredicate(MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, bio, null, null);
    }
    
    public static LocationPredicate inDimension(final DimensionType byn) {
        return new LocationPredicate(MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, null, null, byn);
    }
    
    public static LocationPredicate inFeature(final StructureFeature<?> ceu) {
        return new LocationPredicate(MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, null, ceu, null);
    }
    
    public boolean matches(final ServerLevel vk, final double double2, final double double3, final double double4) {
        return this.matches(vk, (float)double2, (float)double3, (float)double4);
    }
    
    public boolean matches(final ServerLevel vk, final float float2, final float float3, final float float4) {
        if (!this.x.matches(float2)) {
            return false;
        }
        if (!this.y.matches(float3)) {
            return false;
        }
        if (!this.z.matches(float4)) {
            return false;
        }
        if (this.dimension != null && this.dimension != vk.dimension.getType()) {
            return false;
        }
        final BlockPos ew6 = new BlockPos(float2, float3, float4);
        return (this.biome == null || this.biome == vk.getBiome(ew6)) && (this.feature == null || this.feature.isInsideFeature(vk, ew6));
    }
    
    public JsonElement serializeToJson() {
        if (this == LocationPredicate.ANY) {
            return (JsonElement)JsonNull.INSTANCE;
        }
        final JsonObject jsonObject2 = new JsonObject();
        if (!this.x.isAny() || !this.y.isAny() || !this.z.isAny()) {
            final JsonObject jsonObject3 = new JsonObject();
            jsonObject3.add("x", this.x.serializeToJson());
            jsonObject3.add("y", this.y.serializeToJson());
            jsonObject3.add("z", this.z.serializeToJson());
            jsonObject2.add("position", (JsonElement)jsonObject3);
        }
        if (this.dimension != null) {
            jsonObject2.addProperty("dimension", DimensionType.getName(this.dimension).toString());
        }
        if (this.feature != null) {
            jsonObject2.addProperty("feature", (String)Feature.STRUCTURES_REGISTRY.inverse().get(this.feature));
        }
        if (this.biome != null) {
            jsonObject2.addProperty("biome", Registry.BIOME.getKey(this.biome).toString());
        }
        return (JsonElement)jsonObject2;
    }
    
    public static LocationPredicate fromJson(@Nullable final JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return LocationPredicate.ANY;
        }
        final JsonObject jsonObject2 = GsonHelper.convertToJsonObject(jsonElement, "location");
        final JsonObject jsonObject3 = GsonHelper.getAsJsonObject(jsonObject2, "position", new JsonObject());
        final MinMaxBounds.Floats c4 = MinMaxBounds.Floats.fromJson(jsonObject3.get("x"));
        final MinMaxBounds.Floats c5 = MinMaxBounds.Floats.fromJson(jsonObject3.get("y"));
        final MinMaxBounds.Floats c6 = MinMaxBounds.Floats.fromJson(jsonObject3.get("z"));
        final DimensionType byn7 = jsonObject2.has("dimension") ? DimensionType.getByName(new ResourceLocation(GsonHelper.getAsString(jsonObject2, "dimension"))) : null;
        final StructureFeature<?> ceu8 = (jsonObject2.has("feature") ? ((StructureFeature)Feature.STRUCTURES_REGISTRY.get(GsonHelper.getAsString(jsonObject2, "feature"))) : null);
        Biome bio9 = null;
        if (jsonObject2.has("biome")) {
            final ResourceLocation qv10 = new ResourceLocation(GsonHelper.getAsString(jsonObject2, "biome"));
            bio9 = (Biome)Registry.BIOME.getOptional(qv10).orElseThrow(() -> new JsonSyntaxException(new StringBuilder().append("Unknown biome '").append(qv10).append("'").toString()));
        }
        return new LocationPredicate(c4, c5, c6, bio9, ceu8, byn7);
    }
    
    static {
        ANY = new LocationPredicate(MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, null, null, null);
    }
    
    public static class Builder {
        private MinMaxBounds.Floats x;
        private MinMaxBounds.Floats y;
        private MinMaxBounds.Floats z;
        @Nullable
        private Biome biome;
        @Nullable
        private StructureFeature<?> feature;
        @Nullable
        private DimensionType dimension;
        
        public Builder() {
            this.x = MinMaxBounds.Floats.ANY;
            this.y = MinMaxBounds.Floats.ANY;
            this.z = MinMaxBounds.Floats.ANY;
        }
        
        public Builder setBiome(@Nullable final Biome bio) {
            this.biome = bio;
            return this;
        }
        
        public LocationPredicate build() {
            return new LocationPredicate(this.x, this.y, this.z, this.biome, this.feature, this.dimension);
        }
    }
}
