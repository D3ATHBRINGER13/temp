package net.minecraft.advancements.critereon;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.gson.JsonElement;
import net.minecraft.world.entity.Entity;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.server.level.ServerPlayer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.server.PlayerAdvancements;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.advancements.CriterionTrigger;

public class TradeTrigger implements CriterionTrigger<TriggerInstance> {
    private static final ResourceLocation ID;
    private final Map<PlayerAdvancements, PlayerListeners> players;
    
    public TradeTrigger() {
        this.players = (Map<PlayerAdvancements, PlayerListeners>)Maps.newHashMap();
    }
    
    public ResourceLocation getId() {
        return TradeTrigger.ID;
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
        final EntityPredicate av4 = EntityPredicate.fromJson(jsonObject.get("villager"));
        final ItemPredicate bc5 = ItemPredicate.fromJson(jsonObject.get("item"));
        return new TriggerInstance(av4, bc5);
    }
    
    public void trigger(final ServerPlayer vl, final AbstractVillager avp, final ItemStack bcj) {
        final PlayerListeners a5 = (PlayerListeners)this.players.get(vl.getAdvancements());
        if (a5 != null) {
            a5.trigger(vl, avp, bcj);
        }
    }
    
    static {
        ID = new ResourceLocation("villager_trade");
    }
    
    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final EntityPredicate villager;
        private final ItemPredicate item;
        
        public TriggerInstance(final EntityPredicate av, final ItemPredicate bc) {
            super(TradeTrigger.ID);
            this.villager = av;
            this.item = bc;
        }
        
        public static TriggerInstance tradedWithVillager() {
            return new TriggerInstance(EntityPredicate.ANY, ItemPredicate.ANY);
        }
        
        public boolean matches(final ServerPlayer vl, final AbstractVillager avp, final ItemStack bcj) {
            return this.villager.matches(vl, avp) && this.item.matches(bcj);
        }
        
        public JsonElement serializeToJson() {
            final JsonObject jsonObject2 = new JsonObject();
            jsonObject2.add("item", this.item.serializeToJson());
            jsonObject2.add("villager", this.villager.serializeToJson());
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
        
        public void trigger(final ServerPlayer vl, final AbstractVillager avp, final ItemStack bcj) {
            List<Listener<TriggerInstance>> list5 = null;
            for (final Listener<TriggerInstance> a7 : this.listeners) {
                if (a7.getTriggerInstance().matches(vl, avp, bcj)) {
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
