package net.minecraft.advancements.critereon;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.gson.JsonElement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerPlayer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.server.PlayerAdvancements;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.advancements.CriterionTrigger;

public class EntityHurtPlayerTrigger implements CriterionTrigger<TriggerInstance> {
    private static final ResourceLocation ID;
    private final Map<PlayerAdvancements, PlayerListeners> players;
    
    public EntityHurtPlayerTrigger() {
        this.players = (Map<PlayerAdvancements, PlayerListeners>)Maps.newHashMap();
    }
    
    public ResourceLocation getId() {
        return EntityHurtPlayerTrigger.ID;
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
        return new TriggerInstance(al4);
    }
    
    public void trigger(final ServerPlayer vl, final DamageSource ahx, final float float3, final float float4, final boolean boolean5) {
        final PlayerListeners a7 = (PlayerListeners)this.players.get(vl.getAdvancements());
        if (a7 != null) {
            a7.trigger(vl, ahx, float3, float4, boolean5);
        }
    }
    
    static {
        ID = new ResourceLocation("entity_hurt_player");
    }
    
    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final DamagePredicate damage;
        
        public TriggerInstance(final DamagePredicate al) {
            super(EntityHurtPlayerTrigger.ID);
            this.damage = al;
        }
        
        public static TriggerInstance entityHurtPlayer(final DamagePredicate.Builder a) {
            return new TriggerInstance(a.build());
        }
        
        public boolean matches(final ServerPlayer vl, final DamageSource ahx, final float float3, final float float4, final boolean boolean5) {
            return this.damage.matches(vl, ahx, float3, float4, boolean5);
        }
        
        public JsonElement serializeToJson() {
            final JsonObject jsonObject2 = new JsonObject();
            jsonObject2.add("damage", this.damage.serializeToJson());
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
        
        public void trigger(final ServerPlayer vl, final DamageSource ahx, final float float3, final float float4, final boolean boolean5) {
            List<Listener<TriggerInstance>> list7 = null;
            for (final Listener<TriggerInstance> a9 : this.listeners) {
                if (a9.getTriggerInstance().matches(vl, ahx, float3, float4, boolean5)) {
                    if (list7 == null) {
                        list7 = (List<Listener<TriggerInstance>>)Lists.newArrayList();
                    }
                    list7.add(a9);
                }
            }
            if (list7 != null) {
                for (final Listener<TriggerInstance> a9 : list7) {
                    a9.run(this.player);
                }
            }
        }
    }
}
