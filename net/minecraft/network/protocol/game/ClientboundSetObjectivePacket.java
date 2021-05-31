package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetObjectivePacket implements Packet<ClientGamePacketListener> {
    private String objectiveName;
    private Component displayName;
    private ObjectiveCriteria.RenderType renderType;
    private int method;
    
    public ClientboundSetObjectivePacket() {
    }
    
    public ClientboundSetObjectivePacket(final Objective ctf, final int integer) {
        this.objectiveName = ctf.getName();
        this.displayName = ctf.getDisplayName();
        this.renderType = ctf.getRenderType();
        this.method = integer;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.objectiveName = je.readUtf(16);
        this.method = je.readByte();
        if (this.method == 0 || this.method == 2) {
            this.displayName = je.readComponent();
            this.renderType = je.<ObjectiveCriteria.RenderType>readEnum(ObjectiveCriteria.RenderType.class);
        }
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeUtf(this.objectiveName);
        je.writeByte(this.method);
        if (this.method == 0 || this.method == 2) {
            je.writeComponent(this.displayName);
            je.writeEnum(this.renderType);
        }
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleAddObjective(this);
    }
    
    public String getObjectiveName() {
        return this.objectiveName;
    }
    
    public Component getDisplayName() {
        return this.displayName;
    }
    
    public int getMethod() {
        return this.method;
    }
    
    public ObjectiveCriteria.RenderType getRenderType() {
        return this.renderType;
    }
}
