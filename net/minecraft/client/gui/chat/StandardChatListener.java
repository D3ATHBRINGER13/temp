package net.minecraft.client.gui.chat;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ChatType;
import net.minecraft.client.Minecraft;

public class StandardChatListener implements ChatListener {
    private final Minecraft minecraft;
    
    public StandardChatListener(final Minecraft cyc) {
        this.minecraft = cyc;
    }
    
    public void handle(final ChatType jm, final Component jo) {
        this.minecraft.gui.getChat().addMessage(jo);
    }
}
