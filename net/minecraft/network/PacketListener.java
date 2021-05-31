package net.minecraft.network;

import net.minecraft.network.chat.Component;

public interface PacketListener {
    void onDisconnect(final Component jo);
    
    Connection getConnection();
}
