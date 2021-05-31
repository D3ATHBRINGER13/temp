package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.Maps;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.scores.Objective;
import java.util.Iterator;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.entity.Entity;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import java.util.Set;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.RandomValueBounds;
import java.util.Map;

public class EntityHasScoreCondition implements LootItemCondition {
    private final Map<String, RandomValueBounds> scores;
    private final LootContext.EntityTarget entityTarget;
    
    private EntityHasScoreCondition(final Map<String, RandomValueBounds> map, final LootContext.EntityTarget c) {
        this.scores = (Map<String, RandomValueBounds>)ImmutableMap.copyOf((Map)map);
        this.entityTarget = c;
    }
    
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return (Set<LootContextParam<?>>)ImmutableSet.of(this.entityTarget.getParam());
    }
    
    public boolean test(final LootContext coy) {
        final Entity aio3 = coy.<Entity>getParamOrNull(this.entityTarget.getParam());
        if (aio3 == null) {
            return false;
        }
        final Scoreboard cti4 = aio3.level.getScoreboard();
        for (final Map.Entry<String, RandomValueBounds> entry6 : this.scores.entrySet()) {
            if (!this.hasScore(aio3, cti4, (String)entry6.getKey(), (RandomValueBounds)entry6.getValue())) {
                return false;
            }
        }
        return true;
    }
    
    protected boolean hasScore(final Entity aio, final Scoreboard cti, final String string, final RandomValueBounds cpg) {
        final Objective ctf6 = cti.getObjective(string);
        if (ctf6 == null) {
            return false;
        }
        final String string2 = aio.getScoreboardName();
        return cti.hasPlayerScore(string2, ctf6) && cpg.matchesValue(cti.getOrCreatePlayerScore(string2, ctf6).getScore());
    }
    
    public static class Serializer extends LootItemCondition.Serializer<EntityHasScoreCondition> {
        protected Serializer() {
            super(new ResourceLocation("entity_scores"), EntityHasScoreCondition.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final EntityHasScoreCondition crf, final JsonSerializationContext jsonSerializationContext) {
            final JsonObject jsonObject2 = new JsonObject();
            for (final Map.Entry<String, RandomValueBounds> entry7 : crf.scores.entrySet()) {
                jsonObject2.add((String)entry7.getKey(), jsonSerializationContext.serialize(entry7.getValue()));
            }
            jsonObject.add("scores", (JsonElement)jsonObject2);
            jsonObject.add("entity", jsonSerializationContext.serialize(crf.entityTarget));
        }
        
        @Override
        public EntityHasScoreCondition deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext) {
            final Set<Map.Entry<String, JsonElement>> set4 = (Set<Map.Entry<String, JsonElement>>)GsonHelper.getAsJsonObject(jsonObject, "scores").entrySet();
            final Map<String, RandomValueBounds> map5 = (Map<String, RandomValueBounds>)Maps.newLinkedHashMap();
            for (final Map.Entry<String, JsonElement> entry7 : set4) {
                map5.put(entry7.getKey(), GsonHelper.convertToObject((JsonElement)entry7.getValue(), "score", jsonDeserializationContext, (java.lang.Class<?>)RandomValueBounds.class));
            }
            return new EntityHasScoreCondition(map5, GsonHelper.<LootContext.EntityTarget>getAsObject(jsonObject, "entity", jsonDeserializationContext, (java.lang.Class<? extends LootContext.EntityTarget>)LootContext.EntityTarget.class), null);
        }
    }
}
