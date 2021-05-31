package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.util.Iterator;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.scores.PlayerTeam;
import com.google.common.collect.Lists;
import net.minecraft.world.scores.Team;
import net.minecraft.network.chat.TextComponent;
import java.util.Collection;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetPlayerTeamPacket implements Packet<ClientGamePacketListener> {
    private String name;
    private Component displayName;
    private Component playerPrefix;
    private Component playerSuffix;
    private String nametagVisibility;
    private String collisionRule;
    private ChatFormatting color;
    private final Collection<String> players;
    private int method;
    private int options;
    
    public ClientboundSetPlayerTeamPacket() {
        this.name = "";
        this.displayName = new TextComponent("");
        this.playerPrefix = new TextComponent("");
        this.playerSuffix = new TextComponent("");
        this.nametagVisibility = Team.Visibility.ALWAYS.name;
        this.collisionRule = Team.CollisionRule.ALWAYS.name;
        this.color = ChatFormatting.RESET;
        this.players = (Collection<String>)Lists.newArrayList();
    }
    
    public ClientboundSetPlayerTeamPacket(final PlayerTeam ctg, final int integer) {
        this.name = "";
        this.displayName = new TextComponent("");
        this.playerPrefix = new TextComponent("");
        this.playerSuffix = new TextComponent("");
        this.nametagVisibility = Team.Visibility.ALWAYS.name;
        this.collisionRule = Team.CollisionRule.ALWAYS.name;
        this.color = ChatFormatting.RESET;
        this.players = (Collection<String>)Lists.newArrayList();
        this.name = ctg.getName();
        this.method = integer;
        if (integer == 0 || integer == 2) {
            this.displayName = ctg.getDisplayName();
            this.options = ctg.packOptions();
            this.nametagVisibility = ctg.getNameTagVisibility().name;
            this.collisionRule = ctg.getCollisionRule().name;
            this.color = ctg.getColor();
            this.playerPrefix = ctg.getPlayerPrefix();
            this.playerSuffix = ctg.getPlayerSuffix();
        }
        if (integer == 0) {
            this.players.addAll((Collection)ctg.getPlayers());
        }
    }
    
    public ClientboundSetPlayerTeamPacket(final PlayerTeam ctg, final Collection<String> collection, final int integer) {
        this.name = "";
        this.displayName = new TextComponent("");
        this.playerPrefix = new TextComponent("");
        this.playerSuffix = new TextComponent("");
        this.nametagVisibility = Team.Visibility.ALWAYS.name;
        this.collisionRule = Team.CollisionRule.ALWAYS.name;
        this.color = ChatFormatting.RESET;
        this.players = (Collection<String>)Lists.newArrayList();
        if (integer != 3 && integer != 4) {
            throw new IllegalArgumentException("Method must be join or leave for player constructor");
        }
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException("Players cannot be null/empty");
        }
        this.method = integer;
        this.name = ctg.getName();
        this.players.addAll((Collection)collection);
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.name = je.readUtf(16);
        this.method = je.readByte();
        if (this.method == 0 || this.method == 2) {
            this.displayName = je.readComponent();
            this.options = je.readByte();
            this.nametagVisibility = je.readUtf(40);
            this.collisionRule = je.readUtf(40);
            this.color = je.<ChatFormatting>readEnum(ChatFormatting.class);
            this.playerPrefix = je.readComponent();
            this.playerSuffix = je.readComponent();
        }
        if (this.method == 0 || this.method == 3 || this.method == 4) {
            for (int integer3 = je.readVarInt(), integer4 = 0; integer4 < integer3; ++integer4) {
                this.players.add(je.readUtf(40));
            }
        }
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeUtf(this.name);
        je.writeByte(this.method);
        if (this.method == 0 || this.method == 2) {
            je.writeComponent(this.displayName);
            je.writeByte(this.options);
            je.writeUtf(this.nametagVisibility);
            je.writeUtf(this.collisionRule);
            je.writeEnum(this.color);
            je.writeComponent(this.playerPrefix);
            je.writeComponent(this.playerSuffix);
        }
        if (this.method == 0 || this.method == 3 || this.method == 4) {
            je.writeVarInt(this.players.size());
            for (final String string4 : this.players) {
                je.writeUtf(string4);
            }
        }
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleSetPlayerTeamPacket(this);
    }
    
    public String getName() {
        return this.name;
    }
    
    public Component getDisplayName() {
        return this.displayName;
    }
    
    public Collection<String> getPlayers() {
        return this.players;
    }
    
    public int getMethod() {
        return this.method;
    }
    
    public int getOptions() {
        return this.options;
    }
    
    public ChatFormatting getColor() {
        return this.color;
    }
    
    public String getNametagVisibility() {
        return this.nametagVisibility;
    }
    
    public String getCollisionRule() {
        return this.collisionRule;
    }
    
    public Component getPlayerPrefix() {
        return this.playerPrefix;
    }
    
    public Component getPlayerSuffix() {
        return this.playerSuffix;
    }
}
