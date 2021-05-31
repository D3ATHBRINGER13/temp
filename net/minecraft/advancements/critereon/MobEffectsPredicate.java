package net.minecraft.advancements.critereon;

import java.util.Collections;
import com.google.gson.JsonSyntaxException;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import java.util.Iterator;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import com.google.common.collect.Maps;
import net.minecraft.world.effect.MobEffect;
import java.util.Map;

public class MobEffectsPredicate {
    public static final MobEffectsPredicate ANY;
    private final Map<MobEffect, MobEffectInstancePredicate> effects;
    
    public MobEffectsPredicate(final Map<MobEffect, MobEffectInstancePredicate> map) {
        this.effects = map;
    }
    
    public static MobEffectsPredicate effects() {
        return new MobEffectsPredicate((Map<MobEffect, MobEffectInstancePredicate>)Maps.newHashMap());
    }
    
    public MobEffectsPredicate and(final MobEffect aig) {
        this.effects.put(aig, new MobEffectInstancePredicate());
        return this;
    }
    
    public boolean matches(final Entity aio) {
        return this == MobEffectsPredicate.ANY || (aio instanceof LivingEntity && this.matches(((LivingEntity)aio).getActiveEffectsMap()));
    }
    
    public boolean matches(final LivingEntity aix) {
        return this == MobEffectsPredicate.ANY || this.matches(aix.getActiveEffectsMap());
    }
    
    public boolean matches(final Map<MobEffect, MobEffectInstance> map) {
        if (this == MobEffectsPredicate.ANY) {
            return true;
        }
        for (final Map.Entry<MobEffect, MobEffectInstancePredicate> entry4 : this.effects.entrySet()) {
            final MobEffectInstance aii5 = (MobEffectInstance)map.get(entry4.getKey());
            if (!((MobEffectInstancePredicate)entry4.getValue()).matches(aii5)) {
                return false;
            }
        }
        return true;
    }
    
    public static MobEffectsPredicate fromJson(@Nullable final JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return MobEffectsPredicate.ANY;
        }
        final JsonObject jsonObject2 = GsonHelper.convertToJsonObject(jsonElement, "effects");
        final Map<MobEffect, MobEffectInstancePredicate> map3 = (Map<MobEffect, MobEffectInstancePredicate>)Maps.newHashMap();
        for (final Map.Entry<String, JsonElement> entry5 : jsonObject2.entrySet()) {
            final ResourceLocation qv6 = new ResourceLocation((String)entry5.getKey());
            final MobEffect aig7 = (MobEffect)Registry.MOB_EFFECT.getOptional(qv6).orElseThrow(() -> new JsonSyntaxException(new StringBuilder().append("Unknown effect '").append(qv6).append("'").toString()));
            final MobEffectInstancePredicate a8 = MobEffectInstancePredicate.fromJson(GsonHelper.convertToJsonObject((JsonElement)entry5.getValue(), (String)entry5.getKey()));
            map3.put(aig7, a8);
        }
        return new MobEffectsPredicate(map3);
    }
    
    public JsonElement serializeToJson() {
        if (this == MobEffectsPredicate.ANY) {
            return (JsonElement)JsonNull.INSTANCE;
        }
        final JsonObject jsonObject2 = new JsonObject();
        for (final Map.Entry<MobEffect, MobEffectInstancePredicate> entry4 : this.effects.entrySet()) {
            jsonObject2.add(Registry.MOB_EFFECT.getKey((MobEffect)entry4.getKey()).toString(), ((MobEffectInstancePredicate)entry4.getValue()).serializeToJson());
        }
        return (JsonElement)jsonObject2;
    }
    
    static {
        ANY = new MobEffectsPredicate((Map<MobEffect, MobEffectInstancePredicate>)Collections.emptyMap());
    }
    
    public static class MobEffectInstancePredicate {
        private final MinMaxBounds.Ints amplifier;
        private final MinMaxBounds.Ints duration;
        @Nullable
        private final Boolean ambient;
        @Nullable
        private final Boolean visible;
        
        public MobEffectInstancePredicate(final MinMaxBounds.Ints d1, final MinMaxBounds.Ints d2, @Nullable final Boolean boolean3, @Nullable final Boolean boolean4) {
            this.amplifier = d1;
            this.duration = d2;
            this.ambient = boolean3;
            this.visible = boolean4;
        }
        
        public MobEffectInstancePredicate() {
            this(MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, null, null);
        }
        
        public boolean matches(@Nullable final MobEffectInstance aii) {
            return aii != null && this.amplifier.matches(aii.getAmplifier()) && this.duration.matches(aii.getDuration()) && (this.ambient == null || this.ambient == aii.isAmbient()) && (this.visible == null || this.visible == aii.isVisible());
        }
        
        public JsonElement serializeToJson() {
            final JsonObject jsonObject2 = new JsonObject();
            jsonObject2.add("amplifier", this.amplifier.serializeToJson());
            jsonObject2.add("duration", this.duration.serializeToJson());
            jsonObject2.addProperty("ambient", this.ambient);
            jsonObject2.addProperty("visible", this.visible);
            return (JsonElement)jsonObject2;
        }
        
        public static MobEffectInstancePredicate fromJson(final JsonObject jsonObject) {
            final MinMaxBounds.Ints d2 = MinMaxBounds.Ints.fromJson(jsonObject.get("amplifier"));
            final MinMaxBounds.Ints d3 = MinMaxBounds.Ints.fromJson(jsonObject.get("duration"));
            final Boolean boolean4 = jsonObject.has("ambient") ? Boolean.valueOf(GsonHelper.getAsBoolean(jsonObject, "ambient")) : null;
            final Boolean boolean5 = jsonObject.has("visible") ? Boolean.valueOf(GsonHelper.getAsBoolean(jsonObject, "visible")) : null;
            return new MobEffectInstancePredicate(d2, d3, boolean4, boolean5);
        }
    }
}
