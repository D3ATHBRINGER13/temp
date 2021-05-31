package net.minecraft.advancements.critereon;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.server.level.ServerPlayer;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.server.PlayerAdvancements;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.advancements.CriterionTrigger;

public class LocationTrigger implements CriterionTrigger<TriggerInstance> {
    private final ResourceLocation id;
    private final Map<PlayerAdvancements, PlayerListeners> players;
    
    public LocationTrigger(final ResourceLocation qv) {
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
        final LocationPredicate bg4 = LocationPredicate.fromJson((JsonElement)jsonObject);
        return new TriggerInstance(this.id, bg4);
    }
    
    public void trigger(final ServerPlayer vl) {
        final PlayerListeners a3 = (PlayerListeners)this.players.get(vl.getAdvancements());
        if (a3 != null) {
            a3.trigger(vl.getLevel(), vl.x, vl.y, vl.z);
        }
    }
    
    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final LocationPredicate location;
        
        public TriggerInstance(final ResourceLocation qv, final LocationPredicate bg) {
            super(qv);
            this.location = bg;
        }
        
        public static TriggerInstance located(final LocationPredicate bg) {
            return new TriggerInstance(CriteriaTriggers.LOCATION.id, bg);
        }
        
        public static TriggerInstance sleptInBed() {
            return new TriggerInstance(CriteriaTriggers.SLEPT_IN_BED.id, LocationPredicate.ANY);
        }
        
        public static TriggerInstance raidWon() {
            return new TriggerInstance(CriteriaTriggers.RAID_WIN.id, LocationPredicate.ANY);
        }
        
        public boolean matches(final ServerLevel vk, final double double2, final double double3, final double double4) {
            return this.location.matches(vk, double2, double3, double4);
        }
        
        public JsonElement serializeToJson() {
            return this.location.serializeToJson();
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
        
        public void trigger(final ServerLevel vk, final double double2, final double double3, final double double4) {
            List<Listener<TriggerInstance>> list9 = null;
            for (final Listener<TriggerInstance> a11 : this.listeners) {
                if (a11.getTriggerInstance().matches(vk, double2, double3, double4)) {
                    if (list9 == null) {
                        list9 = (List<Listener<TriggerInstance>>)Lists.newArrayList();
                    }
                    list9.add(a11);
                }
            }
            if (list9 != null) {
                for (final Listener<TriggerInstance> a11 : list9) {
                    a11.run(this.player);
                }
            }
        }
    }
}
