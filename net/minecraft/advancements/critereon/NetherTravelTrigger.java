package net.minecraft.advancements.critereon;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.gson.JsonElement;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerPlayer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.server.PlayerAdvancements;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.advancements.CriterionTrigger;

public class NetherTravelTrigger implements CriterionTrigger<TriggerInstance> {
    private static final ResourceLocation ID;
    private final Map<PlayerAdvancements, PlayerListeners> players;
    
    public NetherTravelTrigger() {
        this.players = (Map<PlayerAdvancements, PlayerListeners>)Maps.newHashMap();
    }
    
    public ResourceLocation getId() {
        return NetherTravelTrigger.ID;
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
        final LocationPredicate bg4 = LocationPredicate.fromJson(jsonObject.get("entered"));
        final LocationPredicate bg5 = LocationPredicate.fromJson(jsonObject.get("exited"));
        final DistancePredicate an6 = DistancePredicate.fromJson(jsonObject.get("distance"));
        return new TriggerInstance(bg4, bg5, an6);
    }
    
    public void trigger(final ServerPlayer vl, final Vec3 csi) {
        final PlayerListeners a4 = (PlayerListeners)this.players.get(vl.getAdvancements());
        if (a4 != null) {
            a4.trigger(vl.getLevel(), csi, vl.x, vl.y, vl.z);
        }
    }
    
    static {
        ID = new ResourceLocation("nether_travel");
    }
    
    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final LocationPredicate entered;
        private final LocationPredicate exited;
        private final DistancePredicate distance;
        
        public TriggerInstance(final LocationPredicate bg1, final LocationPredicate bg2, final DistancePredicate an) {
            super(NetherTravelTrigger.ID);
            this.entered = bg1;
            this.exited = bg2;
            this.distance = an;
        }
        
        public static TriggerInstance travelledThroughNether(final DistancePredicate an) {
            return new TriggerInstance(LocationPredicate.ANY, LocationPredicate.ANY, an);
        }
        
        public boolean matches(final ServerLevel vk, final Vec3 csi, final double double3, final double double4, final double double5) {
            return this.entered.matches(vk, csi.x, csi.y, csi.z) && this.exited.matches(vk, double3, double4, double5) && this.distance.matches(csi.x, csi.y, csi.z, double3, double4, double5);
        }
        
        public JsonElement serializeToJson() {
            final JsonObject jsonObject2 = new JsonObject();
            jsonObject2.add("entered", this.entered.serializeToJson());
            jsonObject2.add("exited", this.exited.serializeToJson());
            jsonObject2.add("distance", this.distance.serializeToJson());
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
        
        public void trigger(final ServerLevel vk, final Vec3 csi, final double double3, final double double4, final double double5) {
            List<Listener<TriggerInstance>> list10 = null;
            for (final Listener<TriggerInstance> a12 : this.listeners) {
                if (a12.getTriggerInstance().matches(vk, csi, double3, double4, double5)) {
                    if (list10 == null) {
                        list10 = (List<Listener<TriggerInstance>>)Lists.newArrayList();
                    }
                    list10.add(a12);
                }
            }
            if (list10 != null) {
                for (final Listener<TriggerInstance> a12 : list10) {
                    a12.run(this.player);
                }
            }
        }
    }
}
