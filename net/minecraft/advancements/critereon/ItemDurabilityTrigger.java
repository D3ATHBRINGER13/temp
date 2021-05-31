package net.minecraft.advancements.critereon;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.gson.JsonElement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.server.PlayerAdvancements;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.advancements.CriterionTrigger;

public class ItemDurabilityTrigger implements CriterionTrigger<TriggerInstance> {
    private static final ResourceLocation ID;
    private final Map<PlayerAdvancements, PlayerListeners> players;
    
    public ItemDurabilityTrigger() {
        this.players = (Map<PlayerAdvancements, PlayerListeners>)Maps.newHashMap();
    }
    
    public ResourceLocation getId() {
        return ItemDurabilityTrigger.ID;
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
        final ItemPredicate bc4 = ItemPredicate.fromJson(jsonObject.get("item"));
        final MinMaxBounds.Ints d5 = MinMaxBounds.Ints.fromJson(jsonObject.get("durability"));
        final MinMaxBounds.Ints d6 = MinMaxBounds.Ints.fromJson(jsonObject.get("delta"));
        return new TriggerInstance(bc4, d5, d6);
    }
    
    public void trigger(final ServerPlayer vl, final ItemStack bcj, final int integer) {
        final PlayerListeners a5 = (PlayerListeners)this.players.get(vl.getAdvancements());
        if (a5 != null) {
            a5.trigger(bcj, integer);
        }
    }
    
    static {
        ID = new ResourceLocation("item_durability_changed");
    }
    
    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ItemPredicate item;
        private final MinMaxBounds.Ints durability;
        private final MinMaxBounds.Ints delta;
        
        public TriggerInstance(final ItemPredicate bc, final MinMaxBounds.Ints d2, final MinMaxBounds.Ints d3) {
            super(ItemDurabilityTrigger.ID);
            this.item = bc;
            this.durability = d2;
            this.delta = d3;
        }
        
        public static TriggerInstance changedDurability(final ItemPredicate bc, final MinMaxBounds.Ints d) {
            return new TriggerInstance(bc, d, MinMaxBounds.Ints.ANY);
        }
        
        public boolean matches(final ItemStack bcj, final int integer) {
            return this.item.matches(bcj) && this.durability.matches(bcj.getMaxDamage() - integer) && this.delta.matches(bcj.getDamageValue() - integer);
        }
        
        public JsonElement serializeToJson() {
            final JsonObject jsonObject2 = new JsonObject();
            jsonObject2.add("item", this.item.serializeToJson());
            jsonObject2.add("durability", this.durability.serializeToJson());
            jsonObject2.add("delta", this.delta.serializeToJson());
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
        
        public void trigger(final ItemStack bcj, final int integer) {
            List<Listener<TriggerInstance>> list4 = null;
            for (final Listener<TriggerInstance> a6 : this.listeners) {
                if (a6.getTriggerInstance().matches(bcj, integer)) {
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
