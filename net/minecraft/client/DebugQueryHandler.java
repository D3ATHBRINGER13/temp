package net.minecraft.client;

import net.minecraft.network.protocol.game.ServerboundBlockEntityTagQuery;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundEntityTagQuery;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import java.util.function.Consumer;
import net.minecraft.client.multiplayer.ClientPacketListener;

public class DebugQueryHandler {
    private final ClientPacketListener connection;
    private int transactionId;
    @Nullable
    private Consumer<CompoundTag> callback;
    
    public DebugQueryHandler(final ClientPacketListener dkc) {
        this.transactionId = -1;
        this.connection = dkc;
    }
    
    public boolean handleResponse(final int integer, @Nullable final CompoundTag id) {
        if (this.transactionId == integer && this.callback != null) {
            this.callback.accept(id);
            this.callback = null;
            return true;
        }
        return false;
    }
    
    private int startTransaction(final Consumer<CompoundTag> consumer) {
        this.callback = consumer;
        return ++this.transactionId;
    }
    
    public void queryEntityTag(final int integer, final Consumer<CompoundTag> consumer) {
        final int integer2 = this.startTransaction(consumer);
        this.connection.send(new ServerboundEntityTagQuery(integer2, integer));
    }
    
    public void queryBlockEntityTag(final BlockPos ew, final Consumer<CompoundTag> consumer) {
        final int integer4 = this.startTransaction(consumer);
        this.connection.send(new ServerboundBlockEntityTagQuery(integer4, ew));
    }
}
