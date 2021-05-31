package net.minecraft.network.chat;

import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Map;

public class HoverEvent {
    private final Action action;
    private final Component value;
    
    public HoverEvent(final Action a, final Component jo) {
        this.action = a;
        this.value = jo;
    }
    
    public Action getAction() {
        return this.action;
    }
    
    public Component getValue() {
        return this.value;
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        final HoverEvent jr3 = (HoverEvent)object;
        if (this.action != jr3.action) {
            return false;
        }
        if (this.value != null) {
            if (this.value.equals(jr3.value)) {
                return true;
            }
        }
        else if (jr3.value == null) {
            return true;
        }
        return false;
    }
    
    public String toString() {
        return new StringBuilder().append("HoverEvent{action=").append(this.action).append(", value='").append(this.value).append('\'').append('}').toString();
    }
    
    public int hashCode() {
        int integer2 = this.action.hashCode();
        integer2 = 31 * integer2 + ((this.value != null) ? this.value.hashCode() : 0);
        return integer2;
    }
    
    public enum Action {
        SHOW_TEXT("show_text", true), 
        SHOW_ITEM("show_item", true), 
        SHOW_ENTITY("show_entity", true);
        
        private static final Map<String, Action> LOOKUP;
        private final boolean allowFromServer;
        private final String name;
        
        private Action(final String string3, final boolean boolean4) {
            this.name = string3;
            this.allowFromServer = boolean4;
        }
        
        public boolean isAllowedFromServer() {
            return this.allowFromServer;
        }
        
        public String getName() {
            return this.name;
        }
        
        public static Action getByName(final String string) {
            return (Action)Action.LOOKUP.get(string);
        }
        
        static {
            LOOKUP = (Map)Arrays.stream((Object[])values()).collect(Collectors.toMap(Action::getName, a -> a));
        }
    }
}
