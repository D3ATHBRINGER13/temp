package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.PacketListener;
import com.google.common.base.MoreObjects;
import java.io.IOException;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameType;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.FriendlyByteBuf;
import java.util.Iterator;
import net.minecraft.server.level.ServerPlayer;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.network.protocol.Packet;

public class ClientboundPlayerInfoPacket implements Packet<ClientGamePacketListener> {
    private Action action;
    private final List<PlayerUpdate> entries;
    
    public ClientboundPlayerInfoPacket() {
        this.entries = (List<PlayerUpdate>)Lists.newArrayList();
    }
    
    public ClientboundPlayerInfoPacket(final Action a, final ServerPlayer... arr) {
        this.entries = (List<PlayerUpdate>)Lists.newArrayList();
        this.action = a;
        for (final ServerPlayer vl7 : arr) {
            this.entries.add(new PlayerUpdate(vl7.getGameProfile(), vl7.latency, vl7.gameMode.getGameModeForPlayer(), vl7.getTabListDisplayName()));
        }
    }
    
    public ClientboundPlayerInfoPacket(final Action a, final Iterable<ServerPlayer> iterable) {
        this.entries = (List<PlayerUpdate>)Lists.newArrayList();
        this.action = a;
        for (final ServerPlayer vl5 : iterable) {
            this.entries.add(new PlayerUpdate(vl5.getGameProfile(), vl5.latency, vl5.gameMode.getGameModeForPlayer(), vl5.getTabListDisplayName()));
        }
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.action = je.<Action>readEnum(Action.class);
        for (int integer3 = je.readVarInt(), integer4 = 0; integer4 < integer3; ++integer4) {
            GameProfile gameProfile5 = null;
            int integer5 = 0;
            GameType bho7 = null;
            Component jo8 = null;
            switch (this.action) {
                case ADD_PLAYER: {
                    gameProfile5 = new GameProfile(je.readUUID(), je.readUtf(16));
                    for (int integer6 = je.readVarInt(), integer7 = 0; integer7 < integer6; ++integer7) {
                        final String string11 = je.readUtf(32767);
                        final String string12 = je.readUtf(32767);
                        if (je.readBoolean()) {
                            gameProfile5.getProperties().put(string11, new Property(string11, string12, je.readUtf(32767)));
                        }
                        else {
                            gameProfile5.getProperties().put(string11, new Property(string11, string12));
                        }
                    }
                    bho7 = GameType.byId(je.readVarInt());
                    integer5 = je.readVarInt();
                    if (je.readBoolean()) {
                        jo8 = je.readComponent();
                        break;
                    }
                    break;
                }
                case UPDATE_GAME_MODE: {
                    gameProfile5 = new GameProfile(je.readUUID(), (String)null);
                    bho7 = GameType.byId(je.readVarInt());
                    break;
                }
                case UPDATE_LATENCY: {
                    gameProfile5 = new GameProfile(je.readUUID(), (String)null);
                    integer5 = je.readVarInt();
                    break;
                }
                case UPDATE_DISPLAY_NAME: {
                    gameProfile5 = new GameProfile(je.readUUID(), (String)null);
                    if (je.readBoolean()) {
                        jo8 = je.readComponent();
                        break;
                    }
                    break;
                }
                case REMOVE_PLAYER: {
                    gameProfile5 = new GameProfile(je.readUUID(), (String)null);
                    break;
                }
            }
            this.entries.add(new PlayerUpdate(gameProfile5, integer5, bho7, jo8));
        }
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeEnum(this.action);
        je.writeVarInt(this.entries.size());
        for (final PlayerUpdate b4 : this.entries) {
            switch (this.action) {
                case ADD_PLAYER: {
                    je.writeUUID(b4.getProfile().getId());
                    je.writeUtf(b4.getProfile().getName());
                    je.writeVarInt(b4.getProfile().getProperties().size());
                    for (final Property property6 : b4.getProfile().getProperties().values()) {
                        je.writeUtf(property6.getName());
                        je.writeUtf(property6.getValue());
                        if (property6.hasSignature()) {
                            je.writeBoolean(true);
                            je.writeUtf(property6.getSignature());
                        }
                        else {
                            je.writeBoolean(false);
                        }
                    }
                    je.writeVarInt(b4.getGameMode().getId());
                    je.writeVarInt(b4.getLatency());
                    if (b4.getDisplayName() == null) {
                        je.writeBoolean(false);
                        continue;
                    }
                    je.writeBoolean(true);
                    je.writeComponent(b4.getDisplayName());
                    continue;
                }
                case UPDATE_GAME_MODE: {
                    je.writeUUID(b4.getProfile().getId());
                    je.writeVarInt(b4.getGameMode().getId());
                    continue;
                }
                case UPDATE_LATENCY: {
                    je.writeUUID(b4.getProfile().getId());
                    je.writeVarInt(b4.getLatency());
                    continue;
                }
                case UPDATE_DISPLAY_NAME: {
                    je.writeUUID(b4.getProfile().getId());
                    if (b4.getDisplayName() == null) {
                        je.writeBoolean(false);
                        continue;
                    }
                    je.writeBoolean(true);
                    je.writeComponent(b4.getDisplayName());
                    continue;
                }
                case REMOVE_PLAYER: {
                    je.writeUUID(b4.getProfile().getId());
                    continue;
                }
            }
        }
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handlePlayerInfo(this);
    }
    
    public List<PlayerUpdate> getEntries() {
        return this.entries;
    }
    
    public Action getAction() {
        return this.action;
    }
    
    public String toString() {
        return MoreObjects.toStringHelper(this).add("action", this.action).add("entries", this.entries).toString();
    }
    
    public enum Action {
        ADD_PLAYER, 
        UPDATE_GAME_MODE, 
        UPDATE_LATENCY, 
        UPDATE_DISPLAY_NAME, 
        REMOVE_PLAYER;
    }
    
    public class PlayerUpdate {
        private final int latency;
        private final GameType gameMode;
        private final GameProfile profile;
        private final Component displayName;
        
        public PlayerUpdate(final GameProfile gameProfile, final int integer, @Nullable final GameType bho, @Nullable final Component jo) {
            this.profile = gameProfile;
            this.latency = integer;
            this.gameMode = bho;
            this.displayName = jo;
        }
        
        public GameProfile getProfile() {
            return this.profile;
        }
        
        public int getLatency() {
            return this.latency;
        }
        
        public GameType getGameMode() {
            return this.gameMode;
        }
        
        @Nullable
        public Component getDisplayName() {
            return this.displayName;
        }
        
        public String toString() {
            return MoreObjects.toStringHelper(this).add("latency", this.latency).add("gameMode", this.gameMode).add("profile", this.profile).add("displayName", ((this.displayName == null) ? null : Component.Serializer.toJson(this.displayName))).toString();
        }
    }
}
