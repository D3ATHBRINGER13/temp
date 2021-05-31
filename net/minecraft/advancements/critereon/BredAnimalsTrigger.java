package net.minecraft.advancements.critereon;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.gson.JsonElement;
import net.minecraft.world.entity.Entity;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.world.entity.AgableMob;
import javax.annotation.Nullable;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.server.level.ServerPlayer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.server.PlayerAdvancements;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.advancements.CriterionTrigger;

public class BredAnimalsTrigger implements CriterionTrigger<TriggerInstance> {
    private static final ResourceLocation ID;
    private final Map<PlayerAdvancements, PlayerListeners> players;
    
    public BredAnimalsTrigger() {
        this.players = (Map<PlayerAdvancements, PlayerListeners>)Maps.newHashMap();
    }
    
    public ResourceLocation getId() {
        return BredAnimalsTrigger.ID;
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
        final EntityPredicate av4 = EntityPredicate.fromJson(jsonObject.get("parent"));
        final EntityPredicate av5 = EntityPredicate.fromJson(jsonObject.get("partner"));
        final EntityPredicate av6 = EntityPredicate.fromJson(jsonObject.get("child"));
        return new TriggerInstance(av4, av5, av6);
    }
    
    public void trigger(final ServerPlayer vl, final Animal ara2, @Nullable final Animal ara3, @Nullable final AgableMob aim) {
        final PlayerListeners a6 = (PlayerListeners)this.players.get(vl.getAdvancements());
        if (a6 != null) {
            a6.trigger(vl, ara2, ara3, aim);
        }
    }
    
    static {
        ID = new ResourceLocation("bred_animals");
    }
    
    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final EntityPredicate parent;
        private final EntityPredicate partner;
        private final EntityPredicate child;
        
        public TriggerInstance(final EntityPredicate av1, final EntityPredicate av2, final EntityPredicate av3) {
            super(BredAnimalsTrigger.ID);
            this.parent = av1;
            this.partner = av2;
            this.child = av3;
        }
        
        public static TriggerInstance bredAnimals() {
            return new TriggerInstance(EntityPredicate.ANY, EntityPredicate.ANY, EntityPredicate.ANY);
        }
        
        public static TriggerInstance bredAnimals(final EntityPredicate.Builder a) {
            return new TriggerInstance(a.build(), EntityPredicate.ANY, EntityPredicate.ANY);
        }
        
        public boolean matches(final ServerPlayer vl, final Animal ara2, @Nullable final Animal ara3, @Nullable final AgableMob aim) {
            return this.child.matches(vl, aim) && ((this.parent.matches(vl, ara2) && this.partner.matches(vl, ara3)) || (this.parent.matches(vl, ara3) && this.partner.matches(vl, ara2)));
        }
        
        public JsonElement serializeToJson() {
            final JsonObject jsonObject2 = new JsonObject();
            jsonObject2.add("parent", this.parent.serializeToJson());
            jsonObject2.add("partner", this.partner.serializeToJson());
            jsonObject2.add("child", this.child.serializeToJson());
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
        
        public void trigger(final ServerPlayer vl, final Animal ara2, @Nullable final Animal ara3, @Nullable final AgableMob aim) {
            List<Listener<TriggerInstance>> list6 = null;
            for (final Listener<TriggerInstance> a8 : this.listeners) {
                if (a8.getTriggerInstance().matches(vl, ara2, ara3, aim)) {
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
