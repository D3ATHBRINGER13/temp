package net.minecraft.advancements.critereon;

import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.gson.JsonElement;
import java.util.Iterator;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.advancements.CriterionTriggerInstance;
import java.util.Collection;
import net.minecraft.world.entity.fishing.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.server.PlayerAdvancements;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.advancements.CriterionTrigger;

public class FishingRodHookedTrigger implements CriterionTrigger<TriggerInstance> {
    private static final ResourceLocation ID;
    private final Map<PlayerAdvancements, PlayerListeners> players;
    
    public FishingRodHookedTrigger() {
        this.players = (Map<PlayerAdvancements, PlayerListeners>)Maps.newHashMap();
    }
    
    public ResourceLocation getId() {
        return FishingRodHookedTrigger.ID;
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
        final ItemPredicate bc4 = ItemPredicate.fromJson(jsonObject.get("rod"));
        final EntityPredicate av5 = EntityPredicate.fromJson(jsonObject.get("entity"));
        final ItemPredicate bc5 = ItemPredicate.fromJson(jsonObject.get("item"));
        return new TriggerInstance(bc4, av5, bc5);
    }
    
    public void trigger(final ServerPlayer vl, final ItemStack bcj, final FishingHook ats, final Collection<ItemStack> collection) {
        final PlayerListeners a6 = (PlayerListeners)this.players.get(vl.getAdvancements());
        if (a6 != null) {
            a6.trigger(vl, bcj, ats, collection);
        }
    }
    
    static {
        ID = new ResourceLocation("fishing_rod_hooked");
    }
    
    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ItemPredicate rod;
        private final EntityPredicate entity;
        private final ItemPredicate item;
        
        public TriggerInstance(final ItemPredicate bc1, final EntityPredicate av, final ItemPredicate bc3) {
            super(FishingRodHookedTrigger.ID);
            this.rod = bc1;
            this.entity = av;
            this.item = bc3;
        }
        
        public static TriggerInstance fishedItem(final ItemPredicate bc1, final EntityPredicate av, final ItemPredicate bc3) {
            return new TriggerInstance(bc1, av, bc3);
        }
        
        public boolean matches(final ServerPlayer vl, final ItemStack bcj, final FishingHook ats, final Collection<ItemStack> collection) {
            if (!this.rod.matches(bcj)) {
                return false;
            }
            if (!this.entity.matches(vl, ats.hookedIn)) {
                return false;
            }
            if (this.item != ItemPredicate.ANY) {
                boolean boolean6 = false;
                if (ats.hookedIn instanceof ItemEntity) {
                    final ItemEntity atx7 = (ItemEntity)ats.hookedIn;
                    if (this.item.matches(atx7.getItem())) {
                        boolean6 = true;
                    }
                }
                for (final ItemStack bcj2 : collection) {
                    if (this.item.matches(bcj2)) {
                        boolean6 = true;
                        break;
                    }
                }
                if (!boolean6) {
                    return false;
                }
            }
            return true;
        }
        
        public JsonElement serializeToJson() {
            final JsonObject jsonObject2 = new JsonObject();
            jsonObject2.add("rod", this.rod.serializeToJson());
            jsonObject2.add("entity", this.entity.serializeToJson());
            jsonObject2.add("item", this.item.serializeToJson());
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
        
        public void trigger(final ServerPlayer vl, final ItemStack bcj, final FishingHook ats, final Collection<ItemStack> collection) {
            List<Listener<TriggerInstance>> list6 = null;
            for (final Listener<TriggerInstance> a8 : this.listeners) {
                if (a8.getTriggerInstance().matches(vl, bcj, ats, collection)) {
                    if (list6 == null) {
                        list6 = (List<Listener<TriggerInstance>>)Lists.newArrayList();
                    }
                    list6.add(a8);
                }
            }
            if (list6 != null) {
                for (final Listener<TriggerInstance> a8 : list6) {
                    a8.run(this.player);
                }
            }
        }
    }
}
