package net.minecraft.advancements.critereon;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.gson.JsonElement;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import java.util.Map;
import net.minecraft.advancements.CriterionTrigger;

public class KilledTrigger implements CriterionTrigger<TriggerInstance> {
    private final Map<PlayerAdvancements, PlayerListeners> players;
    private final ResourceLocation id;
    
    public KilledTrigger(final ResourceLocation qv) {
        this.players = (Map<PlayerAdvancements, PlayerListeners>)Maps.newHashMap();
        this.id = qv;
    }
    
    public ResourceLocation getId() {
        return this.id;
    }
    
    public void addPlayerListener(final PlayerAdvancements re, final Listener<TriggerInstance> a) {
        PlayerListeners a2 = (PlayerListeners)this.players.get(re);
        if (a2 == null) {
            a2 = new PlayerListeners(re);
            this.players.put(re, a2);
        }
        a2.addListener(a);
    }
    
    public void removePlayerListener(final PlayerAdvancements re, final Listener<TriggerInstance> a) {
        final PlayerListeners a2 = (PlayerListeners)this.players.get(re);
        if (a2 != null) {
            a2.removeListener(a);
            if (a2.isEmpty()) {
                this.players.remove(re);
            }
        }
    }
    
    public void removePlayerListeners(final PlayerAdvancements re) {
        this.players.remove(re);
    }
    
    public TriggerInstance createInstance(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext) {
        return new TriggerInstance(this.id, EntityPredicate.fromJson(jsonObject.get("entity")), DamageSourcePredicate.fromJson(jsonObject.get("killing_blow")));
    }
    
    public void trigger(final ServerPlayer vl, final Entity aio, final DamageSource ahx) {
        final PlayerListeners a5 = (PlayerListeners)this.players.get(vl.getAdvancements());
        if (a5 != null) {
            a5.trigger(vl, aio, ahx);
        }
    }
    
    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final EntityPredicate entityPredicate;
        private final DamageSourcePredicate killingBlow;
        
        public TriggerInstance(final ResourceLocation qv, final EntityPredicate av, final DamageSourcePredicate am) {
            super(qv);
            this.entityPredicate = av;
            this.killingBlow = am;
        }
        
        public static TriggerInstance playerKilledEntity(final EntityPredicate.Builder a) {
            return new TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, a.build(), DamageSourcePredicate.ANY);
        }
        
        public static TriggerInstance playerKilledEntity() {
            return new TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, EntityPredicate.ANY, DamageSourcePredicate.ANY);
        }
        
        public static TriggerInstance playerKilledEntity(final EntityPredicate.Builder a, final DamageSourcePredicate.Builder a) {
            return new TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, a.build(), a.build());
        }
        
        public static TriggerInstance entityKilledPlayer() {
            return new TriggerInstance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, EntityPredicate.ANY, DamageSourcePredicate.ANY);
        }
        
        public boolean matches(final ServerPlayer vl, final Entity aio, final DamageSource ahx) {
            return this.killingBlow.matches(vl, ahx) && this.entityPredicate.matches(vl, aio);
        }
        
        public JsonElement serializeToJson() {
            final JsonObject jsonObject2 = new JsonObject();
            jsonObject2.add("entity", this.entityPredicate.serializeToJson());
            jsonObject2.add("killing_blow", this.killingBlow.serializeToJson());
            return (JsonElement)jsonObject2;
        }
    }
    
    static class PlayerListeners {
        private final PlayerAdvancements player;
        private final Set<Listener<TriggerInstance>> listeners;
        
        public PlayerListeners(final PlayerAdvancements re) {
            this.listeners = (Set<Listener<TriggerInstance>>)Sets.newHashSet();
            this.player = re;
        }
        
        public boolean isEmpty() {
            return this.listeners.isEmpty();
        }
        
        public void addListener(final Listener<TriggerInstance> a) {
            this.listeners.add(a);
        }
        
        public void removeListener(final Listener<TriggerInstance> a) {
            this.listeners.remove(a);
        }
        
        public void trigger(final ServerPlayer vl, final Entity aio, final DamageSource ahx) {
            List<Listener<TriggerInstance>> list5 = null;
            for (final Listener<TriggerInstance> a7 : this.listeners) {
                if (a7.getTriggerInstance().matches(vl, aio, ahx)) {
                    if (list5 == null) {
                        list5 = (List<Listener<TriggerInstance>>)Lists.newArrayList();
                    }
                    list5.add(a7);
                }
            }
            if (list5 != null) {
                for (final Listener<TriggerInstance> a7 : list5) {
                    a7.run(this.player);
                }
            }
        }
    }
}
