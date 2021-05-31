package net.minecraft.advancements.critereon;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.gson.JsonElement;
import net.minecraft.world.entity.Entity;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.server.level.ServerPlayer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.server.PlayerAdvancements;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.advancements.CriterionTrigger;

public class TameAnimalTrigger implements CriterionTrigger<TriggerInstance> {
    private static final ResourceLocation ID;
    private final Map<PlayerAdvancements, PlayerListeners> players;
    
    public TameAnimalTrigger() {
        this.players = (Map<PlayerAdvancements, PlayerListeners>)Maps.newHashMap();
    }
    
    public ResourceLocation getId() {
        return TameAnimalTrigger.ID;
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
        final EntityPredicate av4 = EntityPredicate.fromJson(jsonObject.get("entity"));
        return new TriggerInstance(av4);
    }
    
    public void trigger(final ServerPlayer vl, final Animal ara) {
        final PlayerListeners a4 = (PlayerListeners)this.players.get(vl.getAdvancements());
        if (a4 != null) {
            a4.trigger(vl, ara);
        }
    }
    
    static {
        ID = new ResourceLocation("tame_animal");
    }
    
    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final EntityPredicate entity;
        
        public TriggerInstance(final EntityPredicate av) {
            super(TameAnimalTrigger.ID);
            this.entity = av;
        }
        
        public static TriggerInstance tamedAnimal() {
            return new TriggerInstance(EntityPredicate.ANY);
        }
        
        public static TriggerInstance tamedAnimal(final EntityPredicate av) {
            return new TriggerInstance(av);
        }
        
        public boolean matches(final ServerPlayer vl, final Animal ara) {
            return this.entity.matches(vl, ara);
        }
        
        public JsonElement serializeToJson() {
            final JsonObject jsonObject2 = new JsonObject();
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
        
        public void trigger(final ServerPlayer vl, final Animal ara) {
            List<Listener<TriggerInstance>> list4 = null;
            for (final Listener<TriggerInstance> a6 : this.listeners) {
                if (a6.getTriggerInstance().matches(vl, ara)) {
                    if (list4 == null) {
                        list4 = (List<Listener<TriggerInstance>>)Lists.newArrayList();
                    }
                    list4.add(a6);
                }
            }
            if (list4 != null) {
                for (final Listener<TriggerInstance> a6 : list4) {
                    a6.run(this.player);
                }
            }
        }
    }
}
