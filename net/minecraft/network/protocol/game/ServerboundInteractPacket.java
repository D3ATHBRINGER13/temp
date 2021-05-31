package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import javax.annotation.Nullable;
import net.minecraft.world.level.Level;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.protocol.Packet;

public class ServerboundInteractPacket implements Packet<ServerGamePacketListener> {
    private int entityId;
    private Action action;
    private Vec3 location;
    private InteractionHand hand;
    
    public ServerboundInteractPacket() {
    }
    
    public ServerboundInteractPacket(final Entity aio) {
        this.entityId = aio.getId();
        this.action = Action.ATTACK;
    }
    
    public ServerboundInteractPacket(final Entity aio, final InteractionHand ahi) {
        this.entityId = aio.getId();
        this.action = Action.INTERACT;
        this.hand = ahi;
    }
    
    public ServerboundInteractPacket(final Entity aio, final InteractionHand ahi, final Vec3 csi) {
        this.entityId = aio.getId();
        this.action = Action.INTERACT_AT;
        this.hand = ahi;
        this.location = csi;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.entityId = je.readVarInt();
        this.action = je.<Action>readEnum(Action.class);
        if (this.action == Action.INTERACT_AT) {
            this.location = new Vec3(je.readFloat(), je.readFloat(), je.readFloat());
        }
        if (this.action == Action.INTERACT || this.action == Action.INTERACT_AT) {
            this.hand = je.<InteractionHand>readEnum(InteractionHand.class);
        }
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.entityId);
        je.writeEnum(this.action);
        if (this.action == Action.INTERACT_AT) {
            je.writeFloat((float)this.location.x);
            je.writeFloat((float)this.location.y);
            je.writeFloat((float)this.location.z);
        }
        if (this.action == Action.INTERACT || this.action == Action.INTERACT_AT) {
            je.writeEnum(this.hand);
        }
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handleInteract(this);
    }
    
    @Nullable
    public Entity getTarget(final Level bhr) {
        return bhr.getEntity(this.entityId);
    }
    
    public Action getAction() {
        return this.action;
    }
    
    public InteractionHand getHand() {
        return this.hand;
    }
    
    public Vec3 getLocation() {
        return this.location;
    }
    
    public enum Action {
        INTERACT, 
        ATTACK, 
        INTERACT_AT;
    }
}
