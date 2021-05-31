package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.network.protocol.Packet;

public class ClientboundRemoveMobEffectPacket implements Packet<ClientGamePacketListener> {
    private int entityId;
    private MobEffect effect;
    
    public ClientboundRemoveMobEffectPacket() {
    }
    
    public ClientboundRemoveMobEffectPacket(final int integer, final MobEffect aig) {
        this.entityId = integer;
        this.effect = aig;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.entityId = je.readVarInt();
        this.effect = MobEffect.byId(je.readUnsignedByte());
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.entityId);
        je.writeByte(MobEffect.getId(this.effect));
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleRemoveMobEffect(this);
    }
    
    @Nullable
    public Entity getEntity(final Level bhr) {
        return bhr.getEntity(this.entityId);
    }
    
    @Nullable
    public MobEffect getEffect() {
        return this.effect;
    }
}
