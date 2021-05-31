package net.minecraft.server.level;

import java.util.stream.Stream;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;

public final class PlayerMap {
    private final Object2BooleanMap<ServerPlayer> players;
    
    public PlayerMap() {
        this.players = (Object2BooleanMap<ServerPlayer>)new Object2BooleanOpenHashMap();
    }
    
    public Stream<ServerPlayer> getPlayers(final long long1) {
        return (Stream<ServerPlayer>)this.players.keySet().stream();
    }
    
    public void addPlayer(final long long1, final ServerPlayer vl, final boolean boolean3) {
        this.players.put(vl, boolean3);
    }
    
    public void removePlayer(final long long1, final ServerPlayer vl) {
        this.players.removeBoolean(vl);
    }
    
    public void ignorePlayer(final ServerPlayer vl) {
        this.players.replace(vl, true);
    }
    
    public void unIgnorePlayer(final ServerPlayer vl) {
        this.players.replace(vl, false);
    }
    
    public boolean ignoredOrUnknown(final ServerPlayer vl) {
        return this.players.getOrDefault(vl, true);
    }
    
    public boolean ignored(final ServerPlayer vl) {
        return this.players.getBoolean(vl);
    }
    
    public void updatePlayer(final long long1, final long long2, final ServerPlayer vl) {
    }
}
