package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.Packet;

public class ServerboundEditBookPacket implements Packet<ServerGamePacketListener> {
    private ItemStack book;
    private boolean signing;
    private InteractionHand hand;
    
    public ServerboundEditBookPacket() {
    }
    
    public ServerboundEditBookPacket(final ItemStack bcj, final boolean boolean2, final InteractionHand ahi) {
        this.book = bcj.copy();
        this.signing = boolean2;
        this.hand = ahi;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.book = je.readItem();
        this.signing = je.readBoolean();
        this.hand = je.<InteractionHand>readEnum(InteractionHand.class);
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeItem(this.book);
        je.writeBoolean(this.signing);
        je.writeEnum(this.hand);
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handleEditBook(this);
    }
    
    public ItemStack getBook() {
        return this.book;
    }
    
    public boolean isSigning() {
        return this.signing;
    }
    
    public InteractionHand getHand() {
        return this.hand;
    }
}
