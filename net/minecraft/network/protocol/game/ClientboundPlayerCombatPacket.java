package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundPlayerCombatPacket implements Packet<ClientGamePacketListener> {
    public Event event;
    public int playerId;
    public int killerId;
    public int duration;
    public Component message;
    
    public ClientboundPlayerCombatPacket() {
    }
    
    public ClientboundPlayerCombatPacket(final CombatTracker ahw, final Event a) {
        this(ahw, a, new TextComponent(""));
    }
    
    public ClientboundPlayerCombatPacket(final CombatTracker ahw, final Event a, final Component jo) {
        this.event = a;
        final LivingEntity aix5 = ahw.getKiller();
        switch (a) {
            case END_COMBAT: {
                this.duration = ahw.getCombatDuration();
                this.killerId = ((aix5 == null) ? -1 : aix5.getId());
                break;
            }
            case ENTITY_DIED: {
                this.playerId = ahw.getMob().getId();
                this.killerId = ((aix5 == null) ? -1 : aix5.getId());
                this.message = jo;
                break;
            }
        }
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.event = je.<Event>readEnum(Event.class);
        if (this.event == Event.END_COMBAT) {
            this.duration = je.readVarInt();
            this.killerId = je.readInt();
        }
        else if (this.event == Event.ENTITY_DIED) {
            this.playerId = je.readVarInt();
            this.killerId = je.readInt();
            this.message = je.readComponent();
        }
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeEnum(this.event);
        if (this.event == Event.END_COMBAT) {
            je.writeVarInt(this.duration);
            je.writeInt(this.killerId);
        }
        else if (this.event == Event.ENTITY_DIED) {
            je.writeVarInt(this.playerId);
            je.writeInt(this.killerId);
            je.writeComponent(this.message);
        }
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handlePlayerCombat(this);
    }
    
    public boolean isSkippable() {
        return this.event == Event.ENTITY_DIED;
    }
    
    public enum Event {
        ENTER_COMBAT, 
        END_COMBAT, 
        ENTITY_DIED;
    }
}
