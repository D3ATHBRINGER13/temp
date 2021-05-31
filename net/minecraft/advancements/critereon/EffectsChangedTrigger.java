package net.minecraft.advancements.critereon;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.gson.JsonElement;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.server.level.ServerPlayer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.server.PlayerAdvancements;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.advancements.CriterionTrigger;

public class EffectsChangedTrigger implements CriterionTrigger<TriggerInstance> {
    private static final ResourceLocation ID;
    private final Map<PlayerAdvancements, PlayerListeners> players;
    
    public EffectsChangedTrigger() {
        this.players = (Map<PlayerAdvancements, PlayerListeners>)Maps.newHashMap();
    }
    
    public ResourceLocation getId() {
        return EffectsChangedTrigger.ID;
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
        final MobEffectsPredicate bj4 = MobEffectsPredicate.fromJson(jsonObject.get("effects"));
        return new TriggerInstance(bj4);
    }
    
    public void trigger(final ServerPlayer vl) {
        final PlayerListeners a3 = (PlayerListeners)this.players.get(vl.getAdvancements());
        if (a3 != null) {
            a3.trigger(vl);
        }
    }
    
    static {
        ID = new ResourceLocation("effects_changed");
    }
    
    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final MobEffectsPredicate effects;
        
        public TriggerInstance(final MobEffectsPredicate bj) {
            super(EffectsChangedTrigger.ID);
            this.effects = bj;
        }
        
        public static TriggerInstance hasEffects(final MobEffectsPredicate bj) {
            return new TriggerInstance(bj);
        }
        
        public boolean matches(final ServerPlayer vl) {
            return this.effects.matches(vl);
        }
        
        public JsonElement serializeToJson() {
            final JsonObject jsonObject2 = new JsonObject();
            jsonObject2.add("effects", this.effects.serializeToJson());
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
        
        public void trigger(final ServerPlayer vl) {
            List<Listener<TriggerInstance>> list3 = null;
            for (final Listener<TriggerInstance> a5 : this.listeners) {
                if (a5.getTriggerInstance().matches(vl)) {
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
