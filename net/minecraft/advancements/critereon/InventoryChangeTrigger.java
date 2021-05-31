package net.minecraft.advancements.critereon;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.Iterator;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.Item;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.ItemLike;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.server.PlayerAdvancements;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.advancements.CriterionTrigger;

public class InventoryChangeTrigger implements CriterionTrigger<TriggerInstance> {
    private static final ResourceLocation ID;
    private final Map<PlayerAdvancements, PlayerListeners> players;
    
    public InventoryChangeTrigger() {
        this.players = (Map<PlayerAdvancements, PlayerListeners>)Maps.newHashMap();
    }
    
    public ResourceLocation getId() {
        return InventoryChangeTrigger.ID;
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
        final JsonObject jsonObject2 = GsonHelper.getAsJsonObject(jsonObject, "slots", new JsonObject());
        final MinMaxBounds.Ints d5 = MinMaxBounds.Ints.fromJson(jsonObject2.get("occupied"));
        final MinMaxBounds.Ints d6 = MinMaxBounds.Ints.fromJson(jsonObject2.get("full"));
        final MinMaxBounds.Ints d7 = MinMaxBounds.Ints.fromJson(jsonObject2.get("empty"));
        final ItemPredicate[] arr8 = ItemPredicate.fromJsonArray(jsonObject.get("items"));
        return new TriggerInstance(d5, d6, d7, arr8);
    }
    
    public void trigger(final ServerPlayer vl, final Inventory awf) {
        final PlayerListeners a4 = (PlayerListeners)this.players.get(vl.getAdvancements());
        if (a4 != null) {
            a4.trigger(awf);
        }
    }
    
    static {
        ID = new ResourceLocation("inventory_changed");
    }
    
    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final MinMaxBounds.Ints slotsOccupied;
        private final MinMaxBounds.Ints slotsFull;
        private final MinMaxBounds.Ints slotsEmpty;
        private final ItemPredicate[] predicates;
        
        public TriggerInstance(final MinMaxBounds.Ints d1, final MinMaxBounds.Ints d2, final MinMaxBounds.Ints d3, final ItemPredicate[] arr) {
            super(InventoryChangeTrigger.ID);
            this.slotsOccupied = d1;
            this.slotsFull = d2;
            this.slotsEmpty = d3;
            this.predicates = arr;
        }
        
        public static TriggerInstance hasItem(final ItemPredicate... arr) {
            return new TriggerInstance(MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, arr);
        }
        
        public static TriggerInstance hasItem(final ItemLike... arr) {
            final ItemPredicate[] arr2 = new ItemPredicate[arr.length];
            for (int integer3 = 0; integer3 < arr.length; ++integer3) {
                arr2[integer3] = new ItemPredicate(null, arr[integer3].asItem(), MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, new EnchantmentPredicate[0], null, NbtPredicate.ANY);
            }
            return hasItem(arr2);
        }
        
        public JsonElement serializeToJson() {
            final JsonObject jsonObject2 = new JsonObject();
            if (!this.slotsOccupied.isAny() || !this.slotsFull.isAny() || !this.slotsEmpty.isAny()) {
                final JsonObject jsonObject3 = new JsonObject();
                jsonObject3.add("occupied", this.slotsOccupied.serializeToJson());
                jsonObject3.add("full", this.slotsFull.serializeToJson());
                jsonObject3.add("empty", this.slotsEmpty.serializeToJson());
                jsonObject2.add("slots", (JsonElement)jsonObject3);
            }
            if (this.predicates.length > 0) {
                final JsonArray jsonArray3 = new JsonArray();
                for (final ItemPredicate bc7 : this.predicates) {
                    jsonArray3.add(bc7.serializeToJson());
                }
                jsonObject2.add("items", (JsonElement)jsonArray3);
            }
            return (JsonElement)jsonObject2;
        }
        
        public boolean matches(final Inventory awf) {
            int integer3 = 0;
            int integer4 = 0;
            int integer5 = 0;
            final List<ItemPredicate> list6 = (List<ItemPredicate>)Lists.newArrayList((Object[])this.predicates);
            for (int integer6 = 0; integer6 < awf.getContainerSize(); ++integer6) {
                final ItemStack bcj8 = awf.getItem(integer6);
                if (bcj8.isEmpty()) {
                    ++integer4;
                }
                else {
                    ++integer5;
                    if (bcj8.getCount() >= bcj8.getMaxStackSize()) {
                        ++integer3;
                    }
                    final Iterator<ItemPredicate> iterator9 = (Iterator<ItemPredicate>)list6.iterator();
                    while (iterator9.hasNext()) {
                        final ItemPredicate bc10 = (ItemPredicate)iterator9.next();
                        if (bc10.matches(bcj8)) {
                            iterator9.remove();
                        }
                    }
                }
            }
            return this.slotsFull.matches(integer3) && this.slotsEmpty.matches(integer4) && this.slotsOccupied.matches(integer5) && list6.isEmpty();
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
        
        public void trigger(final Inventory awf) {
            List<Listener<TriggerInstance>> list3 = null;
            for (final Listener<TriggerInstance> a5 : this.listeners) {
                if (a5.getTriggerInstance().matches(awf)) {
                    if (list3 == null) {
                        list3 = (List<Listener<TriggerInstance>>)Lists.newArrayList();
                    }
                    list3.add(a5);
                }
            }
            if (list3 != null) {
                for (final Listener<TriggerInstance> a5 : list3) {
                    a5.run(this.player);
                }
            }
        }
    }
}
