package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.advancements.Advancement;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.Packet;

public class ServerboundSeenAdvancementsPacket implements Packet<ServerGamePacketListener> {
    private Action action;
    private ResourceLocation tab;
    
    public ServerboundSeenAdvancementsPacket() {
    }
    
    public ServerboundSeenAdvancementsPacket(final Action a, @Nullable final ResourceLocation qv) {
        this.action = a;
        this.tab = qv;
    }
    
    public static ServerboundSeenAdvancementsPacket openedTab(final Advancement q) {
        return new ServerboundSeenAdvancementsPacket(Action.OPENED_TAB, q.getId());
    }
    
    public static ServerboundSeenAdvancementsPacket closedScreen() {
        return new ServerboundSeenAdvancementsPacket(Action.CLOSED_SCREEN, null);
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.action = je.<Action>readEnum(Action.class);
        if (this.action == Action.OPENED_TAB) {
            this.tab = je.readResourceLocation();
        }
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeEnum(this.action);
        if (this.action == Action.OPENED_TAB) {
            je.writeResourceLocation(this.tab);
        }
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handleSeenAdvancements(this);
    }
    
    public Action getAction() {
        return this.action;
    }
    
    public ResourceLocation getTab() {
        return this.tab;
    }
    
    public enum Action {
        OPENED_TAB, 
        CLOSED_SCREEN;
    }
}
