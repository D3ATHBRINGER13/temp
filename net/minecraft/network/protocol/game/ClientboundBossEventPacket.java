package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.BossEvent;
import net.minecraft.network.chat.Component;
import java.util.UUID;
import net.minecraft.network.protocol.Packet;

public class ClientboundBossEventPacket implements Packet<ClientGamePacketListener> {
    private UUID id;
    private Operation operation;
    private Component name;
    private float pct;
    private BossEvent.BossBarColor color;
    private BossEvent.BossBarOverlay overlay;
    private boolean darkenScreen;
    private boolean playMusic;
    private boolean createWorldFog;
    
    public ClientboundBossEventPacket() {
    }
    
    public ClientboundBossEventPacket(final Operation a, final BossEvent agz) {
        this.operation = a;
        this.id = agz.getId();
        this.name = agz.getName();
        this.pct = agz.getPercent();
        this.color = agz.getColor();
        this.overlay = agz.getOverlay();
        this.darkenScreen = agz.shouldDarkenScreen();
        this.playMusic = agz.shouldPlayBossMusic();
        this.createWorldFog = agz.shouldCreateWorldFog();
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.id = je.readUUID();
        this.operation = je.<Operation>readEnum(Operation.class);
        switch (this.operation) {
            case ADD: {
                this.name = je.readComponent();
                this.pct = je.readFloat();
                this.color = je.<BossEvent.BossBarColor>readEnum(BossEvent.BossBarColor.class);
                this.overlay = je.<BossEvent.BossBarOverlay>readEnum(BossEvent.BossBarOverlay.class);
                this.decodeProperties(je.readUnsignedByte());
            }
            case UPDATE_PCT: {
                this.pct = je.readFloat();
                break;
            }
            case UPDATE_NAME: {
                this.name = je.readComponent();
                break;
            }
            case UPDATE_STYLE: {
                this.color = je.<BossEvent.BossBarColor>readEnum(BossEvent.BossBarColor.class);
                this.overlay = je.<BossEvent.BossBarOverlay>readEnum(BossEvent.BossBarOverlay.class);
                break;
            }
            case UPDATE_PROPERTIES: {
                this.decodeProperties(je.readUnsignedByte());
                break;
            }
        }
    }
    
    private void decodeProperties(final int integer) {
        this.darkenScreen = ((integer & 0x1) > 0);
        this.playMusic = ((integer & 0x2) > 0);
        this.createWorldFog = ((integer & 0x4) > 0);
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeUUID(this.id);
        je.writeEnum(this.operation);
        switch (this.operation) {
            case ADD: {
                je.writeComponent(this.name);
                je.writeFloat(this.pct);
                je.writeEnum(this.color);
                je.writeEnum(this.overlay);
                je.writeByte(this.encodeProperties());
            }
            case UPDATE_PCT: {
                je.writeFloat(this.pct);
                break;
            }
            case UPDATE_NAME: {
                je.writeComponent(this.name);
                break;
            }
            case UPDATE_STYLE: {
                je.writeEnum(this.color);
                je.writeEnum(this.overlay);
                break;
            }
            case UPDATE_PROPERTIES: {
                je.writeByte(this.encodeProperties());
                break;
            }
        }
    }
    
    private int encodeProperties() {
        int integer2 = 0;
        if (this.darkenScreen) {
            integer2 |= 0x1;
        }
        if (this.playMusic) {
            integer2 |= 0x2;
        }
        if (this.createWorldFog) {
            integer2 |= 0x4;
        }
        return integer2;
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleBossUpdate(this);
    }
    
    public UUID getId() {
        return this.id;
    }
    
    public Operation getOperation() {
        return this.operation;
    }
    
    public Component getName() {
        return this.name;
    }
    
    public float getPercent() {
        return this.pct;
    }
    
    public BossEvent.BossBarColor getColor() {
        return this.color;
    }
    
    public BossEvent.BossBarOverlay getOverlay() {
        return this.overlay;
    }
    
    public boolean shouldDarkenScreen() {
        return this.darkenScreen;
    }
    
    public boolean shouldPlayMusic() {
        return this.playMusic;
    }
    
    public boolean shouldCreateWorldFog() {
        return this.createWorldFog;
    }
    
    public enum Operation {
        ADD, 
        REMOVE, 
        UPDATE_PCT, 
        UPDATE_NAME, 
        UPDATE_STYLE, 
        UPDATE_PROPERTIES;
    }
}
