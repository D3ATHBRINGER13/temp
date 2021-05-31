package net.minecraft.client.multiplayer;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.SharedConstants;

public class ServerData {
    public String name;
    public String ip;
    public String status;
    public String motd;
    public long ping;
    public int protocol;
    public String version;
    public boolean pinged;
    public String playerList;
    private ServerPackStatus packStatus;
    private String iconB64;
    private boolean lan;
    
    public ServerData(final String string1, final String string2, final boolean boolean3) {
        this.protocol = SharedConstants.getCurrentVersion().getProtocolVersion();
        this.version = SharedConstants.getCurrentVersion().getName();
        this.packStatus = ServerPackStatus.PROMPT;
        this.name = string1;
        this.ip = string2;
        this.lan = boolean3;
    }
    
    public CompoundTag write() {
        final CompoundTag id2 = new CompoundTag();
        id2.putString("name", this.name);
        id2.putString("ip", this.ip);
        if (this.iconB64 != null) {
            id2.putString("icon", this.iconB64);
        }
        if (this.packStatus == ServerPackStatus.ENABLED) {
            id2.putBoolean("acceptTextures", true);
        }
        else if (this.packStatus == ServerPackStatus.DISABLED) {
            id2.putBoolean("acceptTextures", false);
        }
        return id2;
    }
    
    public ServerPackStatus getResourcePackStatus() {
        return this.packStatus;
    }
    
    public void setResourcePackStatus(final ServerPackStatus a) {
        this.packStatus = a;
    }
    
    public static ServerData read(final CompoundTag id) {
        final ServerData dki2 = new ServerData(id.getString("name"), id.getString("ip"), false);
        if (id.contains("icon", 8)) {
            dki2.setIconB64(id.getString("icon"));
        }
        if (id.contains("acceptTextures", 1)) {
            if (id.getBoolean("acceptTextures")) {
                dki2.setResourcePackStatus(ServerPackStatus.ENABLED);
            }
            else {
                dki2.setResourcePackStatus(ServerPackStatus.DISABLED);
            }
        }
        else {
            dki2.setResourcePackStatus(ServerPackStatus.PROMPT);
        }
        return dki2;
    }
    
    @Nullable
    public String getIconB64() {
        return this.iconB64;
    }
    
    public void setIconB64(@Nullable final String string) {
        this.iconB64 = string;
    }
    
    public boolean isLan() {
        return this.lan;
    }
    
    public void copyFrom(final ServerData dki) {
        this.ip = dki.ip;
        this.name = dki.name;
        this.setResourcePackStatus(dki.getResourcePackStatus());
        this.iconB64 = dki.iconB64;
        this.lan = dki.lan;
    }
    
    public enum ServerPackStatus {
        ENABLED("enabled"), 
        DISABLED("disabled"), 
        PROMPT("prompt");
        
        private final Component name;
        
        private ServerPackStatus(final String string3) {
            this.name = new TranslatableComponent("addServer.resourcePack." + string3, new Object[0]);
        }
        
        public Component getName() {
            return this.name;
        }
    }
}
