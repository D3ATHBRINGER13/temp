package net.minecraft.advancements.critereon;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.gson.JsonElement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.server.PlayerAdvancements;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.advancements.CriterionTrigger;

public class PlayerHurtEntityTrigger implements CriterionTrigger<TriggerInstance> {
    private static final ResourceLocation ID;
    private final Map<PlayerAdvancements, PlayerListeners> players;
    
    public PlayerHurtEntityTrigger() {
        this.players = (Map<PlayerAdvancements, PlayerListeners>)Maps.newHashMap();
    }
    
    public ResourceLocation getId() {
        return PlayerHurtEntityTrigger.ID;
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
        final DamagePredicate al4 = DamagePredicate.fromJson(jsonObject.get("damage"));
        final EntityPredicate av5 = EntityPredicate.fromJson(jsonObject.get("entity"));
        return new TriggerInstance(al4, av5);
    }
    
    public void trigger(final ServerPlayer vl, final Entity aio, final DamageSource ahx, final float float4, final float float5, final boolean boolean6) {
        final PlayerListeners a8 = (PlayerListeners)this.players.get(vl.getAdvancements());
        if (a8 != null) {
            a8.trigger(vl, aio, ahx, float4, float5, boolean6);
        }
    }
    
    static {
        ID = new ResourceLocation("player_hurt_entity");
    }
    
    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final DamagePredicate damage;
        private final EntityPredicate entity;
        
        public TriggerInstance(final DamagePredicate al, final EntityPredicate av) {
            super(PlayerHurtEntityTrigger.ID);
            this.damage = al;
            this.entity = av;
        }
        
        public static TriggerInstance playerHurtEntity(final DamagePredicate.Builder a) {
            return new TriggerInstance(a.build(), EntityPredicate.ANY);
        }
        
        public boolean matches(final ServerPlayer vl, final Entity aio, final DamageSource ahx, final float float4, final float float5, final boolean boolean6) {
            return this.damage.matches(vl, ahx, float4, float5, boolean6) && this.entity.matches(vl, aio);
        }
        
        public JsonElement serializeToJson() {
            final JsonObject jsonObject2 = new JsonObject();
            jsonObject2.add("damage", this.damage.serializeToJson());
            jsonObject2.add("entity", this.entity.serializeToJson());
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
        
        public void trigger(final ServerPlayer vl, final Entity aio, final DamageSource ahx, final float float4, final float float5, final boolean boolean6) {
            List<Listener<TriggerInstance>> list8 = null;
            for (final Listener<TriggerInstance> a10 : this.listeners) {
                if (a10.getTriggerInstance().matches(vl, aio, ahx, float4, float5, boolean6)) {
                    if (list8 == null) {
                        list8 = (List<Listener<TriggerInstance>>)Lists.newArrayList();
                    }
                    list8.add(a10);
                }
            }
            if (list8 != null) {
                for (final Listener<TriggerInstance> a10 : list8) {
                    a10.run(this.player);
                }
            }
        }
    }
}
