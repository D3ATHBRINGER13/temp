package net.minecraft.advancements.critereon;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonElement;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerPlayer;
import javax.annotation.Nullable;

public class DamageSourcePredicate {
    public static final DamageSourcePredicate ANY;
    private final Boolean isProjectile;
    private final Boolean isExplosion;
    private final Boolean bypassesArmor;
    private final Boolean bypassesInvulnerability;
    private final Boolean bypassesMagic;
    private final Boolean isFire;
    private final Boolean isMagic;
    private final Boolean isLightning;
    private final EntityPredicate directEntity;
    private final EntityPredicate sourceEntity;
    
    public DamageSourcePredicate(@Nullable final Boolean boolean1, @Nullable final Boolean boolean2, @Nullable final Boolean boolean3, @Nullable final Boolean boolean4, @Nullable final Boolean boolean5, @Nullable final Boolean boolean6, @Nullable final Boolean boolean7, @Nullable final Boolean boolean8, final EntityPredicate av9, final EntityPredicate av10) {
        this.isProjectile = boolean1;
        this.isExplosion = boolean2;
        this.bypassesArmor = boolean3;
        this.bypassesInvulnerability = boolean4;
        this.bypassesMagic = boolean5;
        this.isFire = boolean6;
        this.isMagic = boolean7;
        this.isLightning = boolean8;
        this.directEntity = av9;
        this.sourceEntity = av10;
    }
    
    public boolean matches(final ServerPlayer vl, final DamageSource ahx) {
        return this.matches(vl.getLevel(), new Vec3(vl.x, vl.y, vl.z), ahx);
    }
    
    public boolean matches(final ServerLevel vk, final Vec3 csi, final DamageSource ahx) {
        return this == DamageSourcePredicate.ANY || ((this.isProjectile == null || this.isProjectile == ahx.isProjectile()) && (this.isExplosion == null || this.isExplosion == ahx.isExplosion()) && (this.bypassesArmor == null || this.bypassesArmor == ahx.isBypassArmor()) && (this.bypassesInvulnerability == null || this.bypassesInvulnerability == ahx.isBypassInvul()) && (this.bypassesMagic == null || this.bypassesMagic == ahx.isBypassMagic()) && (this.isFire == null || this.isFire == ahx.isFire()) && (this.isMagic == null || this.isMagic == ahx.isMagic()) && (this.isLightning == null || this.isLightning == (ahx == DamageSource.LIGHTNING_BOLT)) && this.directEntity.matches(vk, csi, ahx.getDirectEntity()) && this.sourceEntity.matches(vk, csi, ahx.getEntity()));
    }
    
    public static DamageSourcePredicate fromJson(@Nullable final JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return DamageSourcePredicate.ANY;
        }
        final JsonObject jsonObject2 = GsonHelper.convertToJsonObject(jsonElement, "damage type");
        final Boolean boolean3 = getOptionalBoolean(jsonObject2, "is_projectile");
        final Boolean boolean4 = getOptionalBoolean(jsonObject2, "is_explosion");
        final Boolean boolean5 = getOptionalBoolean(jsonObject2, "bypasses_armor");
        final Boolean boolean6 = getOptionalBoolean(jsonObject2, "bypasses_invulnerability");
        final Boolean boolean7 = getOptionalBoolean(jsonObject2, "bypasses_magic");
        final Boolean boolean8 = getOptionalBoolean(jsonObject2, "is_fire");
        final Boolean boolean9 = getOptionalBoolean(jsonObject2, "is_magic");
        final Boolean boolean10 = getOptionalBoolean(jsonObject2, "is_lightning");
        final EntityPredicate av11 = EntityPredicate.fromJson(jsonObject2.get("direct_entity"));
        final EntityPredicate av12 = EntityPredicate.fromJson(jsonObject2.get("source_entity"));
        return new DamageSourcePredicate(boolean3, boolean4, boolean5, boolean6, boolean7, boolean8, boolean9, boolean10, av11, av12);
    }
    
    @Nullable
    private static Boolean getOptionalBoolean(final JsonObject jsonObject, final String string) {
        return jsonObject.has(string) ? Boolean.valueOf(GsonHelper.getAsBoolean(jsonObject, string)) : null;
    }
    
    public JsonElement serializeToJson() {
        if (this == DamageSourcePredicate.ANY) {
            return (JsonElement)JsonNull.INSTANCE;
        }
        final JsonObject jsonObject2 = new JsonObject();
        this.addOptionally(jsonObject2, "is_projectile", this.isProjectile);
        this.addOptionally(jsonObject2, "is_explosion", this.isExplosion);
        this.addOptionally(jsonObject2, "bypasses_armor", this.bypassesArmor);
        this.addOptionally(jsonObject2, "bypasses_invulnerability", this.bypassesInvulnerability);
        this.addOptionally(jsonObject2, "bypasses_magic", this.bypassesMagic);
        this.addOptionally(jsonObject2, "is_fire", this.isFire);
        this.addOptionally(jsonObject2, "is_magic", this.isMagic);
        this.addOptionally(jsonObject2, "is_lightning", this.isLightning);
        jsonObject2.add("direct_entity", this.directEntity.serializeToJson());
        jsonObject2.add("source_entity", this.sourceEntity.serializeToJson());
        return (JsonElement)jsonObject2;
    }
    
    private void addOptionally(final JsonObject jsonObject, final String string, @Nullable final Boolean boolean3) {
        if (boolean3 != null) {
            jsonObject.addProperty(string, boolean3);
        }
    }
    
    static {
        ANY = Builder.damageType().build();
    }
    
    public static class Builder {
        private Boolean isProjectile;
        private Boolean isExplosion;
        private Boolean bypassesArmor;
        private Boolean bypassesInvulnerability;
        private Boolean bypassesMagic;
        private Boolean isFire;
        private Boolean isMagic;
        private Boolean isLightning;
        private EntityPredicate directEntity;
        private EntityPredicate sourceEntity;
        
        public Builder() {
            this.directEntity = EntityPredicate.ANY;
            this.sourceEntity = EntityPredicate.ANY;
        }
        
        public static Builder damageType() {
            return new Builder();
        }
        
        public Builder isProjectile(final Boolean boolean1) {
            this.isProjectile = boolean1;
            return this;
        }
        
        public Builder isLightning(final Boolean boolean1) {
            this.isLightning = boolean1;
            return this;
        }
        
        public Builder direct(final EntityPredicate.Builder a) {
            this.directEntity = a.build();
            return this;
        }
        
        public DamageSourcePredicate build() {
            return new DamageSourcePredicate(this.isProjectile, this.isExplosion, this.bypassesArmor, this.bypassesInvulnerability, this.bypassesMagic, this.isFire, this.isMagic, this.isLightning, this.directEntity, this.sourceEntity);
        }
    }
}
