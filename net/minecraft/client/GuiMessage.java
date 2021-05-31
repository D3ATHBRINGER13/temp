package net.minecraft.client;

import net.minecraft.network.chat.Component;

public class GuiMessage {
    private final int addedTime;
    private final Component message;
    private final int id;
    
    public GuiMessage(final int integer1, final Component jo, final int integer3) {
        this.message = jo;
        this.addedTime = integer1;
        this.id = integer3;
    }
    
    public Component getMessage() {
        return this.message;
    }
    
    public int getAddedTime() {
        return this.addedTime;
    }
    
    public int getId() {
        return this.id;
    }
}
