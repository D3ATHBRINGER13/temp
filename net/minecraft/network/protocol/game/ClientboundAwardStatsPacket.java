package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.util.Map;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import net.minecraft.core.Registry;
import net.minecraft.stats.StatType;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.stats.Stat;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.network.protocol.Packet;

public class ClientboundAwardStatsPacket implements Packet<ClientGamePacketListener> {
    private Object2IntMap<Stat<?>> stats;
    
    public ClientboundAwardStatsPacket() {
    }
    
    public ClientboundAwardStatsPacket(final Object2IntMap<Stat<?>> object2IntMap) {
        this.stats = object2IntMap;
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleAwardStats(this);
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        final int integer3 = je.readVarInt();
        this.stats = (Object2IntMap<Stat<?>>)new Object2IntOpenHashMap(integer3);
        for (int integer4 = 0; integer4 < integer3; ++integer4) {
            this.readStat(Registry.STAT_TYPE.byId(je.readVarInt()), je);
        }
    }
    
    private <T> void readStat(final StatType<T> yx, final FriendlyByteBuf je) {
        final int integer4 = je.readVarInt();
        final int integer5 = je.readVarInt();
        this.stats.put(yx.get(yx.getRegistry().byId(integer4)), integer5);
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.stats.size());
        for (final Object2IntMap.Entry<Stat<?>> entry4 : this.stats.object2IntEntrySet()) {
            final Stat<?> yv5 = entry4.getKey();
            je.writeVarInt(Registry.STAT_TYPE.getId(yv5.getType()));
            je.writeVarInt(this.getId(yv5));
            je.writeVarInt(entry4.getIntValue());
        }
    }
    
    private <T> int getId(final Stat<T> yv) {
        return yv.getType().getRegistry().getId(yv.getValue());
    }
    
    public Map<Stat<?>, Integer> getStats() {
        return (Map<Stat<?>, Integer>)this.stats;
    }
}
