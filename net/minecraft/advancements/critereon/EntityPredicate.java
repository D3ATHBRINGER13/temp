package net.minecraft.advancements.critereon;

import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;
import com.google.gson.JsonNull;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonElement;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public class EntityPredicate {
    public static final EntityPredicate ANY;
    public static final EntityPredicate[] ANY_ARRAY;
    private final EntityTypePredicate entityType;
    private final DistancePredicate distanceToPlayer;
    private final LocationPredicate location;
    private final MobEffectsPredicate effects;
    private final NbtPredicate nbt;
    private final EntityFlagsPredicate flags;
    private final EntityEquipmentPredicate equipment;
    private final ResourceLocation catType;
    
    private EntityPredicate(final EntityTypePredicate aw, final DistancePredicate an, final LocationPredicate bg, final MobEffectsPredicate bj, final NbtPredicate bk, final EntityFlagsPredicate at, final EntityEquipmentPredicate as, @Nullable final ResourceLocation qv) {
        this.entityType = aw;
        this.distanceToPlayer = an;
        this.location = bg;
        this.effects = bj;
        this.nbt = bk;
        this.flags = at;
        this.equipment = as;
        this.catType = qv;
    }
    
    public boolean matches(final ServerPlayer vl, @Nullable final Entity aio) {
        return this.matches(vl.getLevel(), new Vec3(vl.x, vl.y, vl.z), aio);
    }
    
    public boolean matches(final ServerLevel vk, final Vec3 csi, @Nullable final Entity aio) {
        return this == EntityPredicate.ANY || (aio != null && this.entityType.matches(aio.getType()) && this.distanceToPlayer.matches(csi.x, csi.y, csi.z, aio.x, aio.y, aio.z) && this.location.matches(vk, aio.x, aio.y, aio.z) && this.effects.matches(aio) && this.nbt.matches(aio) && this.flags.matches(aio) && this.equipment.matches(aio) && (this.catType == null || (aio instanceof Cat && ((Cat)aio).getResourceLocation().equals(this.catType))));
    }
    
    public static EntityPredicate fromJson(@Nullable final JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return EntityPredicate.ANY;
        }
        final JsonObject jsonObject2 = GsonHelper.convertToJsonObject(jsonElement, "entity");
        final EntityTypePredicate aw3 = EntityTypePredicate.fromJson(jsonObject2.get("type"));
        final DistancePredicate an4 = DistancePredicate.fromJson(jsonObject2.get("distance"));
        final LocationPredicate bg5 = LocationPredicate.fromJson(jsonObject2.get("location"));
        final MobEffectsPredicate bj6 = MobEffectsPredicate.fromJson(jsonObject2.get("effects"));
        final NbtPredicate bk7 = NbtPredicate.fromJson(jsonObject2.get("nbt"));
        final EntityFlagsPredicate at8 = EntityFlagsPredicate.fromJson(jsonObject2.get("flags"));
        final EntityEquipmentPredicate as9 = EntityEquipmentPredicate.fromJson(jsonObject2.get("equipment"));
        final ResourceLocation qv10 = jsonObject2.has("catType") ? new ResourceLocation(GsonHelper.getAsString(jsonObject2, "catType")) : null;
        return new Builder().entityType(aw3).distance(an4).located(bg5).effects(bj6).nbt(bk7).flags(at8).equipment(as9).catType(qv10).build();
    }
    
    public static EntityPredicate[] fromJsonArray(@Nullable final JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return EntityPredicate.ANY_ARRAY;
        }
        final JsonArray jsonArray2 = GsonHelper.convertToJsonArray(jsonElement, "entities");
        final EntityPredicate[] arr3 = new EntityPredicate[jsonArray2.size()];
        for (int integer4 = 0; integer4 < jsonArray2.size(); ++integer4) {
            arr3[integer4] = fromJson(jsonArray2.get(integer4));
        }
        return arr3;
    }
    
    public JsonElement serializeToJson() {
        if (this == EntityPredicate.ANY) {
            return (JsonElement)JsonNull.INSTANCE;
        }
        final JsonObject jsonObject2 = new JsonObject();
        jsonObject2.add("type", this.entityType.serializeToJson());
        jsonObject2.add("distance", this.distanceToPlayer.serializeToJson());
        jsonObject2.add("location", this.location.serializeToJson());
        jsonObject2.add("effects", this.effects.serializeToJson());
        jsonObject2.add("nbt", this.nbt.serializeToJson());
        jsonObject2.add("flags", this.flags.serializeToJson());
        jsonObject2.add("equipment", this.equipment.serializeToJson());
        if (this.catType != null) {
            jsonObject2.addProperty("catType", this.catType.toString());
        }
        return (JsonElement)jsonObject2;
    }
    
    public static JsonElement serializeArrayToJson(final EntityPredicate[] arr) {
        if (arr == EntityPredicate.ANY_ARRAY) {
            return (JsonElement)JsonNull.INSTANCE;
        }
        final JsonArray jsonArray2 = new JsonArray();
        for (final EntityPredicate av6 : arr) {
            final JsonElement jsonElement7 = av6.serializeToJson();
            if (!jsonElement7.isJsonNull()) {
                jsonArray2.add(jsonElement7);
            }
        }
        return (JsonElement)jsonArray2;
    }
    
    static {
        ANY = new EntityPredicate(EntityTypePredicate.ANY, DistancePredicate.ANY, LocationPredicate.ANY, MobEffectsPredicate.ANY, NbtPredicate.ANY, EntityFlagsPredicate.ANY, EntityEquipmentPredicate.ANY, null);
        ANY_ARRAY = new EntityPredicate[0];
    }
    
    public static class Builder {
        private EntityTypePredicate entityType;
        private DistancePredicate distanceToPlayer;
        private LocationPredicate location;
        private MobEffectsPredicate effects;
        private NbtPredicate nbt;
        private EntityFlagsPredicate flags;
        private EntityEquipmentPredicate equipment;
        @Nullable
        private ResourceLocation catType;
        
        public Builder() {
            this.entityType = EntityTypePredicate.ANY;
            this.distanceToPlayer = DistancePredicate.ANY;
            this.location = LocationPredicate.ANY;
            this.effects = MobEffectsPredicate.ANY;
            this.nbt = NbtPredicate.ANY;
            this.flags = EntityFlagsPredicate.ANY;
            this.equipment = EntityEquipmentPredicate.ANY;
        }
        
        public static Builder entity() {
            return new Builder();
        }
        
        public Builder of(final EntityType<?> ais) {
            this.entityType = EntityTypePredicate.of(ais);
            return this;
        }
        
        public Builder of(final Tag<EntityType<?>> zg) {
            this.entityType = EntityTypePredicate.of(zg);
            return this;
        }
        
        public Builder of(final ResourceLocation qv) {
            this.catType = qv;
            return this;
        }
        
        public Builder entityType(final EntityTypePredicate aw) {
            this.entityType = aw;
            return this;
        }
        
        public Builder distance(final DistancePredicate an) {
            this.distanceToPlayer = an;
            return this;
        }
        
        public Builder located(final LocationPredicate bg) {
            this.location = bg;
            return this;
        }
        
        public Builder effects(final MobEffectsPredicate bj) {
            this.effects = bj;
            return this;
        }
        
        public Builder nbt(final NbtPredicate bk) {
            this.nbt = bk;
            return this;
        }
        
        public Builder flags(final EntityFlagsPredicate at) {
            this.flags = at;
            return this;
        }
        
        public Builder equipment(final EntityEquipmentPredicate as) {
            this.equipment = as;
            return this;
        }
        
        public Builder catType(@Nullable final ResourceLocation qv) {
            this.catType = qv;
            return this;
        }
        
        public EntityPredicate build() {
            return new EntityPredicate(this.entityType, this.distanceToPlayer, this.location, this.effects, this.nbt, this.flags, this.equipment, this.catType, null);
        }
    }
}
