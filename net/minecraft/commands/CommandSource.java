package net.minecraft.commands;

import net.minecraft.network.chat.Component;

public interface CommandSource {
    public static final CommandSource NULL = new CommandSource() {
        public void sendMessage(final Component jo) {
        }
        
        public boolean acceptsSuccess() {
            return false;
        }
        
        public boolean acceptsFailure() {
            return false;
        }
        
        public boolean shouldInformAdmins() {
            return false;
        }
    };
    
    void sendMessage(final Component jo);
    
    boolean acceptsSuccess();
    
    boolean acceptsFailure();
    
    boolean shouldInformAdmins();
}
