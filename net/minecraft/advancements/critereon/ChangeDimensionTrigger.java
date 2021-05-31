package net.minecraft.advancements.critereon;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.gson.JsonElement;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.server.PlayerAdvancements;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.advancements.CriterionTrigger;

public class ChangeDimensionTrigger implements CriterionTrigger<TriggerInstance> {
    private static final ResourceLocation ID;
    private final Map<PlayerAdvancements, PlayerListeners> players;
    
    public ChangeDimensionTrigger() {
        this.players = (Map<PlayerAdvancements, PlayerListeners>)Maps.newHashMap();
    }
    
    public ResourceLocation getId() {
        return ChangeDimensionTrigger.ID;
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
        final DimensionType byn4 = jsonObject.has("from") ? DimensionType.getByName(new ResourceLocation(GsonHelper.getAsString(jsonObject, "from"))) : null;
        final DimensionType byn5 = jsonObject.has("to") ? DimensionType.getByName(new ResourceLocation(GsonHelper.getAsString(jsonObject, "to"))) : null;
        return new TriggerInstance(byn4, byn5);
    }
    
    public void trigger(final ServerPlayer vl, final DimensionType byn2, final DimensionType byn3) {
        final PlayerListeners a5 = (PlayerListeners)this.players.get(vl.getAdvancements());
        if (a5 != null) {
            a5.trigger(byn2, byn3);
        }
    }
    
    static {
        ID = new ResourceLocation("changed_dimension");
    }
    
    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        @Nullable
        private final DimensionType from;
        @Nullable
        private final DimensionType to;
        
        public TriggerInstance(@Nullable final DimensionType byn1, @Nullable final DimensionType byn2) {
            super(ChangeDimensionTrigger.ID);
            this.from = byn1;
            this.to = byn2;
        }
        
        public static TriggerInstance changedDimensionTo(final DimensionType byn) {
            return new TriggerInstance(null, byn);
        }
        
        public boolean matches(final DimensionType byn1, final DimensionType byn2) {
            return (this.from == null || this.from == byn1) && (this.to == null || this.to == byn2);
        }
        
        public JsonElement serializeToJson() {
            final JsonObject jsonObject2 = new JsonObject();
            if (this.from != null) {
                jsonObject2.addProperty("from", DimensionType.getName(this.from).toString());
            }
            if (this.to != null) {
                jsonObject2.addProperty("to", DimensionType.getName(this.to).toString());
            }
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
        
        public void trigger(final DimensionType byn1, final DimensionType byn2) {
            List<Listener<TriggerInstance>> list4 = null;
            for (final Listener<TriggerInstance> a6 : this.listeners) {
                if (a6.getTriggerInstance().matches(byn1, byn2)) {
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
