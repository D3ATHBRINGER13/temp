package net.minecraft.advancements.critereon;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.gson.JsonElement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.server.PlayerAdvancements;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.advancements.CriterionTrigger;

public class RecipeUnlockedTrigger implements CriterionTrigger<TriggerInstance> {
    private static final ResourceLocation ID;
    private final Map<PlayerAdvancements, PlayerListeners> players;
    
    public RecipeUnlockedTrigger() {
        this.players = (Map<PlayerAdvancements, PlayerListeners>)Maps.newHashMap();
    }
    
    public ResourceLocation getId() {
        return RecipeUnlockedTrigger.ID;
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
        final ResourceLocation qv4 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "recipe"));
        return new TriggerInstance(qv4);
    }
    
    public void trigger(final ServerPlayer vl, final Recipe<?> ber) {
        final PlayerListeners a4 = (PlayerListeners)this.players.get(vl.getAdvancements());
        if (a4 != null) {
            a4.trigger(ber);
        }
    }
    
    static {
        ID = new ResourceLocation("recipe_unlocked");
    }
    
    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ResourceLocation recipe;
        
        public TriggerInstance(final ResourceLocation qv) {
            super(RecipeUnlockedTrigger.ID);
            this.recipe = qv;
        }
        
        public JsonElement serializeToJson() {
            final JsonObject jsonObject2 = new JsonObject();
            jsonObject2.addProperty("recipe", this.recipe.toString());
            return (JsonElement)jsonObject2;
        }
        
        public boolean matches(final Recipe<?> ber) {
            return this.recipe.equals(ber.getId());
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
        
        public void trigger(final Recipe<?> ber) {
            List<Listener<TriggerInstance>> list3 = null;
            for (final Listener<TriggerInstance> a5 : this.listeners) {
                if (a5.getTriggerInstance().matches(ber)) {
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
