package net.minecraft.advancements.critereon;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.gson.JsonElement;
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

public class LevitationTrigger implements CriterionTrigger<TriggerInstance> {
    private static final ResourceLocation ID;
    private final Map<PlayerAdvancements, PlayerListeners> players;
    
    public LevitationTrigger() {
        this.players = (Map<PlayerAdvancements, PlayerListeners>)Maps.newHashMap();
    }
    
    public ResourceLocation getId() {
        return LevitationTrigger.ID;
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
        final DistancePredicate an4 = DistancePredicate.fromJson(jsonObject.get("distance"));
        final MinMaxBounds.Ints d5 = MinMaxBounds.Ints.fromJson(jsonObject.get("duration"));
        return new TriggerInstance(an4, d5);
    }
    
    public void trigger(final ServerPlayer vl, final Vec3 csi, final int integer) {
        final PlayerListeners a5 = (PlayerListeners)this.players.get(vl.getAdvancements());
        if (a5 != null) {
            a5.trigger(vl, csi, integer);
        }
    }
    
    static {
        ID = new ResourceLocation("levitation");
    }
    
    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final DistancePredicate distance;
        private final MinMaxBounds.Ints duration;
        
        public TriggerInstance(final DistancePredicate an, final MinMaxBounds.Ints d) {
            super(LevitationTrigger.ID);
            this.distance = an;
            this.duration = d;
        }
        
        public static TriggerInstance levitated(final DistancePredicate an) {
            return new TriggerInstance(an, MinMaxBounds.Ints.ANY);
        }
        
        public boolean matches(final ServerPlayer vl, final Vec3 csi, final int integer) {
            return this.distance.matches(csi.x, csi.y, csi.z, vl.x, vl.y, vl.z) && this.duration.matches(integer);
        }
        
        public JsonElement serializeToJson() {
            final JsonObject jsonObject2 = new JsonObject();
            jsonObject2.add("distance", this.distance.serializeToJson());
            jsonObject2.add("duration", this.duration.serializeToJson());
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
        
        public void trigger(final ServerPlayer vl, final Vec3 csi, final int integer) {
            List<Listener<TriggerInstance>> list5 = null;
            for (final Listener<TriggerInstance> a7 : this.listeners) {
                if (a7.getTriggerInstance().matches(vl, csi, integer)) {
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
