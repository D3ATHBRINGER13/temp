package net.minecraft.server.bossevents;

import net.minecraft.server.level.ServerPlayer;
import java.util.Iterator;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import java.util.Collection;
import net.minecraft.network.chat.Component;
import javax.annotation.Nullable;
import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;
import net.minecraft.server.MinecraftServer;

public class CustomBossEvents {
    private final MinecraftServer server;
    private final Map<ResourceLocation, CustomBossEvent> events;
    
    public CustomBossEvents(final MinecraftServer minecraftServer) {
        this.events = (Map<ResourceLocation, CustomBossEvent>)Maps.newHashMap();
        this.server = minecraftServer;
    }
    
    @Nullable
    public CustomBossEvent get(final ResourceLocation qv) {
        return (CustomBossEvent)this.events.get(qv);
    }
    
    public CustomBossEvent create(final ResourceLocation qv, final Component jo) {
        final CustomBossEvent rl4 = new CustomBossEvent(qv, jo);
        this.events.put(qv, rl4);
        return rl4;
    }
    
    public void remove(final CustomBossEvent rl) {
        this.events.remove(rl.getTextId());
    }
    
    public Collection<ResourceLocation> getIds() {
        return (Collection<ResourceLocation>)this.events.keySet();
    }
    
    public Collection<CustomBossEvent> getEvents() {
        return (Collection<CustomBossEvent>)this.events.values();
    }
    
    public CompoundTag save() {
        final CompoundTag id2 = new CompoundTag();
        for (final CustomBossEvent rl4 : this.events.values()) {
            id2.put(rl4.getTextId().toString(), rl4.save());
        }
        return id2;
    }
    
    public void load(final CompoundTag id) {
        for (final String string4 : id.getAllKeys()) {
            final ResourceLocation qv5 = new ResourceLocation(string4);
            this.events.put(qv5, CustomBossEvent.load(id.getCompound(string4), qv5));
        }
    }
    
    public void onPlayerConnect(final ServerPlayer vl) {
        for (final CustomBossEvent rl4 : this.events.values()) {
            rl4.onPlayerConnect(vl);
        }
    }
    
    public void onPlayerDisconnect(final ServerPlayer vl) {
        for (final CustomBossEvent rl4 : this.events.values()) {
            rl4.onPlayerDisconnect(vl);
        }
    }
}
