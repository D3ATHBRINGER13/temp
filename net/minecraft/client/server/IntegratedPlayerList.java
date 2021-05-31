package net.minecraft.client.server;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.players.PlayerList;

public class IntegratedPlayerList extends PlayerList {
    private CompoundTag playerData;
    
    public IntegratedPlayerList(final IntegratedServer eac) {
        super(eac, 8);
        this.setViewDistance(10);
    }
    
    @Override
    protected void save(final ServerPlayer vl) {
        if (vl.getName().getString().equals(this.getServer().getSingleplayerName())) {
            this.playerData = vl.saveWithoutId(new CompoundTag());
        }
        super.save(vl);
    }
    
    @Override
    public Component canPlayerLogin(final SocketAddress socketAddress, final GameProfile gameProfile) {
        if (gameProfile.getName().equalsIgnoreCase(this.getServer().getSingleplayerName()) && this.getPlayerByName(gameProfile.getName()) != null) {
            return new TranslatableComponent("multiplayer.disconnect.name_taken", new Object[0]);
        }
        return super.canPlayerLogin(socketAddress, gameProfile);
    }
    
    @Override
    public IntegratedServer getServer() {
        return (IntegratedServer)super.getServer();
    }
    
    @Override
    public CompoundTag getSingleplayerData() {
        return this.playerData;
    }
}
