package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import com.google.common.collect.Sets;
import net.minecraft.network.FriendlyByteBuf;
import java.util.Iterator;
import com.google.common.collect.Maps;
import java.util.Collection;
import net.minecraft.advancements.AdvancementProgress;
import java.util.Set;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;
import net.minecraft.network.protocol.Packet;

public class ClientboundUpdateAdvancementsPacket implements Packet<ClientGamePacketListener> {
    private boolean reset;
    private Map<ResourceLocation, Advancement.Builder> added;
    private Set<ResourceLocation> removed;
    private Map<ResourceLocation, AdvancementProgress> progress;
    
    public ClientboundUpdateAdvancementsPacket() {
    }
    
    public ClientboundUpdateAdvancementsPacket(final boolean boolean1, final Collection<Advancement> collection, final Set<ResourceLocation> set, final Map<ResourceLocation, AdvancementProgress> map) {
        this.reset = boolean1;
        this.added = (Map<ResourceLocation, Advancement.Builder>)Maps.newHashMap();
        for (final Advancement q7 : collection) {
            this.added.put(q7.getId(), q7.deconstruct());
        }
        this.removed = set;
        this.progress = (Map<ResourceLocation, AdvancementProgress>)Maps.newHashMap((Map)map);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleUpdateAdvancementsPacket(this);
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.reset = je.readBoolean();
        this.added = (Map<ResourceLocation, Advancement.Builder>)Maps.newHashMap();
        this.removed = (Set<ResourceLocation>)Sets.newLinkedHashSet();
        this.progress = (Map<ResourceLocation, AdvancementProgress>)Maps.newHashMap();
        for (int integer3 = je.readVarInt(), integer4 = 0; integer4 < integer3; ++integer4) {
            final ResourceLocation qv5 = je.readResourceLocation();
            final Advancement.Builder a6 = Advancement.Builder.fromNetwork(je);
            this.added.put(qv5, a6);
        }
        for (int integer3 = je.readVarInt(), integer4 = 0; integer4 < integer3; ++integer4) {
            final ResourceLocation qv5 = je.readResourceLocation();
            this.removed.add(qv5);
        }
        for (int integer3 = je.readVarInt(), integer4 = 0; integer4 < integer3; ++integer4) {
            final ResourceLocation qv5 = je.readResourceLocation();
            this.progress.put(qv5, AdvancementProgress.fromNetwork(je));
        }
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeBoolean(this.reset);
        je.writeVarInt(this.added.size());
        for (final Map.Entry<ResourceLocation, Advancement.Builder> entry4 : this.added.entrySet()) {
            final ResourceLocation qv5 = (ResourceLocation)entry4.getKey();
            final Advancement.Builder a6 = (Advancement.Builder)entry4.getValue();
            je.writeResourceLocation(qv5);
            a6.serializeToNetwork(je);
        }
        je.writeVarInt(this.removed.size());
        for (final ResourceLocation qv6 : this.removed) {
            je.writeResourceLocation(qv6);
        }
        je.writeVarInt(this.progress.size());
        for (final Map.Entry<ResourceLocation, AdvancementProgress> entry5 : this.progress.entrySet()) {
            je.writeResourceLocation((ResourceLocation)entry5.getKey());
            ((AdvancementProgress)entry5.getValue()).serializeToNetwork(je);
        }
    }
    
    public Map<ResourceLocation, Advancement.Builder> getAdded() {
        return this.added;
    }
    
    public Set<ResourceLocation> getRemoved() {
        return this.removed;
    }
    
    public Map<ResourceLocation, AdvancementProgress> getProgress() {
        return this.progress;
    }
    
    public boolean shouldReset() {
        return this.reset;
    }
}
