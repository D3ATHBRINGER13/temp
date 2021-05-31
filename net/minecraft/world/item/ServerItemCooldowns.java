package net.minecraft.world.item;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCooldownPacket;
import net.minecraft.server.level.ServerPlayer;

public class ServerItemCooldowns extends ItemCooldowns {
    private final ServerPlayer player;
    
    public ServerItemCooldowns(final ServerPlayer vl) {
        this.player = vl;
    }
    
    @Override
    protected void onCooldownStarted(final Item bce, final int integer) {
        super.onCooldownStarted(bce, integer);
        this.player.connection.send(new ClientboundCooldownPacket(bce, integer));
    }
    
    @Override
    protected void onCooldownEnded(final Item bce) {
        super.onCooldownEnded(bce);
        this.player.connection.send(new ClientboundCooldownPacket(bce, 0));
    }
}
