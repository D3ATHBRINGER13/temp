package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.network.FriendlyByteBuf;
import java.util.Iterator;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import java.util.Collection;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.network.protocol.Packet;

public class ClientboundUpdateAttributesPacket implements Packet<ClientGamePacketListener> {
    private int entityId;
    private final List<AttributeSnapshot> attributes;
    
    public ClientboundUpdateAttributesPacket() {
        this.attributes = (List<AttributeSnapshot>)Lists.newArrayList();
    }
    
    public ClientboundUpdateAttributesPacket(final int integer, final Collection<AttributeInstance> collection) {
        this.attributes = (List<AttributeSnapshot>)Lists.newArrayList();
        this.entityId = integer;
        for (final AttributeInstance ajo5 : collection) {
            this.attributes.add(new AttributeSnapshot(ajo5.getAttribute().getName(), ajo5.getBaseValue(), ajo5.getModifiers()));
        }
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.entityId = je.readVarInt();
        for (int integer3 = je.readInt(), integer4 = 0; integer4 < integer3; ++integer4) {
            final String string5 = je.readUtf(64);
            final double double6 = je.readDouble();
            final List<AttributeModifier> list8 = (List<AttributeModifier>)Lists.newArrayList();
            for (int integer5 = je.readVarInt(), integer6 = 0; integer6 < integer5; ++integer6) {
                final UUID uUID11 = je.readUUID();
                list8.add(new AttributeModifier(uUID11, "Unknown synced attribute modifier", je.readDouble(), AttributeModifier.Operation.fromValue(je.readByte())));
            }
            this.attributes.add(new AttributeSnapshot(string5, double6, (Collection<AttributeModifier>)list8));
        }
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.entityId);
        je.writeInt(this.attributes.size());
        for (final AttributeSnapshot a4 : this.attributes) {
            je.writeUtf(a4.getName());
            je.writeDouble(a4.getBase());
            je.writeVarInt(a4.getModifiers().size());
            for (final AttributeModifier ajp6 : a4.getModifiers()) {
                je.writeUUID(ajp6.getId());
                je.writeDouble(ajp6.getAmount());
                je.writeByte(ajp6.getOperation().toValue());
            }
        }
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleUpdateAttributes(this);
    }
    
    public int getEntityId() {
        return this.entityId;
    }
    
    public List<AttributeSnapshot> getValues() {
        return this.attributes;
    }
    
    public class AttributeSnapshot {
        private final String name;
        private final double base;
        private final Collection<AttributeModifier> modifiers;
        
        public AttributeSnapshot(final String string, final double double3, final Collection<AttributeModifier> collection) {
            this.name = string;
            this.base = double3;
            this.modifiers = collection;
        }
        
        public String getName() {
            return this.name;
        }
        
        public double getBase() {
            return this.base;
        }
        
        public Collection<AttributeModifier> getModifiers() {
            return this.modifiers;
        }
    }
}
