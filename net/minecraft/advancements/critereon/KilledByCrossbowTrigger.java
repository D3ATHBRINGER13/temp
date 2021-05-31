package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import net.minecraft.world.entity.EntityType;
import java.util.Set;
import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Sets;
import com.google.common.collect.Lists;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.world.entity.Entity;
import java.util.Collection;
import net.minecraft.server.level.ServerPlayer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.server.PlayerAdvancements;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.advancements.CriterionTrigger;

public class KilledByCrossbowTrigger implements CriterionTrigger<TriggerInstance> {
    private static final ResourceLocation ID;
    private final Map<PlayerAdvancements, PlayerListeners> players;
    
    public KilledByCrossbowTrigger() {
        this.players = (Map<PlayerAdvancements, PlayerListeners>)Maps.newHashMap();
    }
    
    public ResourceLocation getId() {
        return KilledByCrossbowTrigger.ID;
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
        final EntityPredicate[] arr4 = EntityPredicate.fromJsonArray(jsonObject.get("victims"));
        final MinMaxBounds.Ints d5 = MinMaxBounds.Ints.fromJson(jsonObject.get("unique_entity_types"));
        return new TriggerInstance(arr4, d5);
    }
    
    public void trigger(final ServerPlayer vl, final Collection<Entity> collection, final int integer) {
        final PlayerListeners a5 = (PlayerListeners)this.players.get(vl.getAdvancements());
        if (a5 != null) {
            a5.trigger(vl, collection, integer);
        }
    }
    
    static {
        ID = new ResourceLocation("killed_by_crossbow");
    }
    
    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final EntityPredicate[] victims;
        private final MinMaxBounds.Ints uniqueEntityTypes;
        
        public TriggerInstance(final EntityPredicate[] arr, final MinMaxBounds.Ints d) {
            super(KilledByCrossbowTrigger.ID);
            this.victims = arr;
            this.uniqueEntityTypes = d;
        }
        
        public static TriggerInstance crossbowKilled(final EntityPredicate.Builder... arr) {
            final EntityPredicate[] arr2 = new EntityPredicate[arr.length];
            for (int integer3 = 0; integer3 < arr.length; ++integer3) {
                final EntityPredicate.Builder a4 = arr[integer3];
                arr2[integer3] = a4.build();
            }
            return new TriggerInstance(arr2, MinMaxBounds.Ints.ANY);
        }
        
        public static TriggerInstance crossbowKilled(final MinMaxBounds.Ints d) {
            final EntityPredicate[] arr2 = new EntityPredicate[0];
            return new TriggerInstance(arr2, d);
        }
        
        public boolean matches(final ServerPlayer vl, final Collection<Entity> collection, final int integer) {
            if (this.victims.length > 0) {
                final List<Entity> list5 = (List<Entity>)Lists.newArrayList((Iterable)collection);
                for (final EntityPredicate av9 : this.victims) {
                    boolean boolean10 = false;
                    final Iterator<Entity> iterator11 = (Iterator<Entity>)list5.iterator();
                    while (iterator11.hasNext()) {
                        final Entity aio12 = (Entity)iterator11.next();
                        if (av9.matches(vl, aio12)) {
                            iterator11.remove();
                            boolean10 = true;
                            break;
                        }
                    }
                    if (!boolean10) {
                        return false;
                    }
                }
            }
            if (this.uniqueEntityTypes != MinMaxBounds.Ints.ANY) {
                final Set<EntityType<?>> set5 = (Set<EntityType<?>>)Sets.newHashSet();
                for (final Entity aio13 : collection) {
                    set5.add(aio13.getType());
                }
                return this.uniqueEntityTypes.matches(set5.size()) && this.uniqueEntityTypes.matches(integer);
            }
            return true;
        }
        
        public JsonElement serializeToJson() {
            final JsonObject jsonObject2 = new JsonObject();
            jsonObject2.add("victims", EntityPredicate.serializeArrayToJson(this.victims));
            jsonObject2.add("unique_entity_types", this.uniqueEntityTypes.serializeToJson());
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
        
        public void trigger(final ServerPlayer vl, final Collection<Entity> collection, final int integer) {
            List<Listener<TriggerInstance>> list5 = null;
            for (final Listener<TriggerInstance> a7 : this.listeners) {
                if (a7.getTriggerInstance().matches(vl, collection, integer)) {
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
