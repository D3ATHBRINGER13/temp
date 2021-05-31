package net.minecraft.client.gui.chat;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ChatType;

public interface ChatListener {
    void handle(final ChatType jm, final Component jo);
}
