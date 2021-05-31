package net.minecraft.client;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import java.util.UUID;
import com.mojang.bridge.game.GameSession;

public class Session implements GameSession {
    private final int players;
    private final boolean isRemoteServer;
    private final String difficulty;
    private final String gameMode;
    private final UUID id;
    
    public Session(final MultiPlayerLevel dkf, final LocalPlayer dmp, final ClientPacketListener dkc) {
        this.players = dkc.getOnlinePlayers().size();
        this.isRemoteServer = !dkc.getConnection().isMemoryConnection();
        this.difficulty = dkf.getDifficulty().getKey();
        final PlayerInfo dkg5 = dkc.getPlayerInfo(dmp.getUUID());
        if (dkg5 != null) {
            this.gameMode = dkg5.getGameMode().getName();
        }
        else {
            this.gameMode = "unknown";
        }
        this.id = dkc.getId();
    }
    
    public int getPlayerCount() {
        return this.players;
    }
    
    public boolean isRemoteServer() {
        return this.isRemoteServer;
    }
    
    public String getDifficulty() {
        return this.difficulty;
    }
    
    public String getGameMode() {
        return this.gameMode;
    }
    
    public UUID getSessionId() {
        return this.id;
    }
}
