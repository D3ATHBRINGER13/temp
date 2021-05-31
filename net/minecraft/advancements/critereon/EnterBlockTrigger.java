package net.minecraft.advancements.critereon;

import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.Util;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.server.level.ServerPlayer;
import java.util.Optional;
import java.util.Iterator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.server.PlayerAdvancements;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.advancements.CriterionTrigger;

public class EnterBlockTrigger implements CriterionTrigger<TriggerInstance> {
    private static final ResourceLocation ID;
    private final Map<PlayerAdvancements, PlayerListeners> players;
    
    public EnterBlockTrigger() {
        this.players = (Map<PlayerAdvancements, PlayerListeners>)Maps.newHashMap();
    }
    
    public ResourceLocation getId() {
        return EnterBlockTrigger.ID;
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
        Block bmv4 = null;
        if (jsonObject.has("block")) {
            final ResourceLocation qv5 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "block"));
            bmv4 = (Block)Registry.BLOCK.getOptional(qv5).orElseThrow(() -> new JsonSyntaxException(new StringBuilder().append("Unknown block type '").append(qv5).append("'").toString()));
        }
        Map<Property<?>, Object> map5 = null;
        if (jsonObject.has("state")) {
            if (bmv4 == null) {
                throw new JsonSyntaxException("Can't define block state without a specific block type");
            }
            final StateDefinition<Block, BlockState> bvu6 = bmv4.getStateDefinition();
            for (final Map.Entry<String, JsonElement> entry8 : GsonHelper.getAsJsonObject(jsonObject, "state").entrySet()) {
                final Property<?> bww9 = bvu6.getProperty((String)entry8.getKey());
                if (bww9 == null) {
                    throw new JsonSyntaxException("Unknown block state property '" + (String)entry8.getKey() + "' for block '" + Registry.BLOCK.getKey(bmv4) + "'");
                }
                final String string10 = GsonHelper.convertToString((JsonElement)entry8.getValue(), (String)entry8.getKey());
                final Optional<?> optional11 = bww9.getValue(string10);
                if (!optional11.isPresent()) {
                    throw new JsonSyntaxException("Invalid block state value '" + string10 + "' for property '" + (String)entry8.getKey() + "' on block '" + Registry.BLOCK.getKey(bmv4) + "'");
                }
                if (map5 == null) {
                    map5 = (Map<Property<?>, Object>)Maps.newHashMap();
                }
                map5.put(bww9, optional11.get());
            }
        }
        return new TriggerInstance(bmv4, map5);
    }
    
    public void trigger(final ServerPlayer vl, final BlockState bvt) {
        final PlayerListeners a4 = (PlayerListeners)this.players.get(vl.getAdvancements());
        if (a4 != null) {
            a4.trigger(bvt);
        }
    }
    
    static {
        ID = new ResourceLocation("enter_block");
    }
    
    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final Block block;
        private final Map<Property<?>, Object> state;
        
        public TriggerInstance(@Nullable final Block bmv, @Nullable final Map<Property<?>, Object> map) {
            super(EnterBlockTrigger.ID);
            this.block = bmv;
            this.state = map;
        }
        
        public static TriggerInstance entersBlock(final Block bmv) {
            return new TriggerInstance(bmv, null);
        }
        
        public JsonElement serializeToJson() {
            final JsonObject jsonObject2 = new JsonObject();
            if (this.block != null) {
                jsonObject2.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
                if (this.state != null && !this.state.isEmpty()) {
                    final JsonObject jsonObject3 = new JsonObject();
                    for (final Map.Entry<Property<?>, ?> entry5 : this.state.entrySet()) {
                        jsonObject3.addProperty(((Property)entry5.getKey()).getName(), Util.<Comparable>getPropertyName((Property<Comparable>)entry5.getKey(), entry5.getValue()));
                    }
                    jsonObject2.add("state", (JsonElement)jsonObject3);
                }
            }
            return (JsonElement)jsonObject2;
        }
        
        public boolean matches(final BlockState bvt) {
            if (this.block != null && bvt.getBlock() != this.block) {
                return false;
            }
            if (this.state != null) {
                for (final Map.Entry<Property<?>, Object> entry4 : this.state.entrySet()) {
                    if (bvt.<Comparable>getValue((Property<Comparable>)entry4.getKey()) != entry4.getValue()) {
                        return false;
                    }
                }
            }
            return true;
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
        
        public void trigger(final BlockState bvt) {
            List<Listener<TriggerInstance>> list3 = null;
            for (final Listener<TriggerInstance> a5 : this.listeners) {
                if (a5.getTriggerInstance().matches(bvt)) {
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
