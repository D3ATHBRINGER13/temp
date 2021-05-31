package net.minecraft.advancements.critereon;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.gson.JsonElement;
import net.minecraft.world.entity.Entity;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.server.level.ServerPlayer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.server.PlayerAdvancements;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.advancements.CriterionTrigger;

public class CuredZombieVillagerTrigger implements CriterionTrigger<TriggerInstance> {
    private static final ResourceLocation ID;
    private final Map<PlayerAdvancements, PlayerListeners> players;
    
    public CuredZombieVillagerTrigger() {
        this.players = (Map<PlayerAdvancements, PlayerListeners>)Maps.newHashMap();
    }
    
    public ResourceLocation getId() {
        return CuredZombieVillagerTrigger.ID;
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
        final EntityPredicate av4 = EntityPredicate.fromJson(jsonObject.get("zombie"));
        final EntityPredicate av5 = EntityPredicate.fromJson(jsonObject.get("villager"));
        return new TriggerInstance(av4, av5);
    }
    
    public void trigger(final ServerPlayer vl, final Zombie avm, final Villager avt) {
        final PlayerListeners a5 = (PlayerListeners)this.players.get(vl.getAdvancements());
        if (a5 != null) {
            a5.trigger(vl, avm, avt);
        }
    }
    
    static {
        ID = new ResourceLocation("cured_zombie_villager");
    }
    
    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final EntityPredicate zombie;
        private final EntityPredicate villager;
        
        public TriggerInstance(final EntityPredicate av1, final EntityPredicate av2) {
            super(CuredZombieVillagerTrigger.ID);
            this.zombie = av1;
            this.villager = av2;
        }
        
        public static TriggerInstance curedZombieVillager() {
            return new TriggerInstance(EntityPredicate.ANY, EntityPredicate.ANY);
        }
        
        public boolean matches(final ServerPlayer vl, final Zombie avm, final Villager avt) {
            return this.zombie.matches(vl, avm) && this.villager.matches(vl, avt);
        }
        
        public JsonElement serializeToJson() {
            final JsonObject jsonObject2 = new JsonObject();
            jsonObject2.add("zombie", this.zombie.serializeToJson());
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
        
        public void trigger(final ServerPlayer vl, final Zombie avm, final Villager avt) {
            List<Listener<TriggerInstance>> list5 = null;
            for (final Listener<TriggerInstance> a7 : this.listeners) {
                if (a7.getTriggerInstance().matches(vl, avm, avt)) {
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
