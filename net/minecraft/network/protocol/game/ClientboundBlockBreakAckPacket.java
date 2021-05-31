package net.minecraft.network.protocol.game;

import org.apache.logging.log4j.LogManager;
import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.world.level.block.Block;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import org.apache.logging.log4j.Logger;
import net.minecraft.network.protocol.Packet;

public class ClientboundBlockBreakAckPacket implements Packet<ClientGamePacketListener> {
    private static final Logger LOGGER;
    private BlockPos pos;
    private BlockState state;
    ServerboundPlayerActionPacket.Action action;
    private boolean allGood;
    
    public ClientboundBlockBreakAckPacket() {
    }
    
    public ClientboundBlockBreakAckPacket(final BlockPos ew, final BlockState bvt, final ServerboundPlayerActionPacket.Action a, final boolean boolean4) {
        this.pos = ew.immutable();
        this.state = bvt;
        this.action = a;
        this.allGood = boolean4;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.pos = je.readBlockPos();
        this.state = Block.BLOCK_STATE_REGISTRY.byId(je.readVarInt());
        this.action = je.<ServerboundPlayerActionPacket.Action>readEnum(ServerboundPlayerActionPacket.Action.class);
        this.allGood = je.readBoolean();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeBlockPos(this.pos);
        je.writeVarInt(Block.getId(this.state));
        je.writeEnum(this.action);
        je.writeBoolean(this.allGood);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleBlockBreakAck(this);
    }
    
    public BlockState getState() {
        return this.state;
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public boolean allGood() {
        return this.allGood;
    }
    
    public ServerboundPlayerActionPacket.Action action() {
        return this.action;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
