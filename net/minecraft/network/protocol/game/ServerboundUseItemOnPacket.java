package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.protocol.Packet;

public class ServerboundUseItemOnPacket implements Packet<ServerGamePacketListener> {
    private BlockHitResult blockHit;
    private InteractionHand hand;
    
    public ServerboundUseItemOnPacket() {
    }
    
    public ServerboundUseItemOnPacket(final InteractionHand ahi, final BlockHitResult csd) {
        this.hand = ahi;
        this.blockHit = csd;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.hand = je.<InteractionHand>readEnum(InteractionHand.class);
        this.blockHit = je.readBlockHitResult();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeEnum(this.hand);
        je.writeBlockHitResult(this.blockHit);
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handleUseItemOn(this);
    }
    
    public InteractionHand getHand() {
        return this.hand;
    }
    
    public BlockHitResult getHitResult() {
        return this.blockHit;
    }
}
