package net.minecraft.advancements.critereon;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.server.PlayerAdvancements;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.advancements.CriterionTrigger;

public class UsedEnderEyeTrigger implements CriterionTrigger<TriggerInstance> {
    private static final ResourceLocation ID;
    private final Map<PlayerAdvancements, PlayerListeners> players;
    
    public UsedEnderEyeTrigger() {
        this.players = (Map<PlayerAdvancements, PlayerListeners>)Maps.newHashMap();
    }
    
    public ResourceLocation getId() {
        return UsedEnderEyeTrigger.ID;
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
        final MinMaxBounds.Floats c4 = MinMaxBounds.Floats.fromJson(jsonObject.get("distance"));
        return new TriggerInstance(c4);
    }
    
    public void trigger(final ServerPlayer vl, final BlockPos ew) {
        final PlayerListeners a4 = (PlayerListeners)this.players.get(vl.getAdvancements());
        if (a4 != null) {
            final double double5 = vl.x - ew.getX();
            final double double6 = vl.z - ew.getZ();
            a4.trigger(double5 * double5 + double6 * double6);
        }
    }
    
    static {
        ID = new ResourceLocation("used_ender_eye");
    }
    
    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final MinMaxBounds.Floats level;
        
        public TriggerInstance(final MinMaxBounds.Floats c) {
            super(UsedEnderEyeTrigger.ID);
            this.level = c;
        }
        
        public boolean matches(final double double1) {
            return this.level.matchesSqr(double1);
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
        
        public void trigger(final double double1) {
            List<Listener<TriggerInstance>> list4 = null;
            for (final Listener<TriggerInstance> a6 : this.listeners) {
                if (a6.getTriggerInstance().matches(double1)) {
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
