package net.minecraft.advancements;

import net.minecraft.ChatFormatting;

public enum FrameType {
    TASK("task", 0, ChatFormatting.GREEN), 
    CHALLENGE("challenge", 26, ChatFormatting.DARK_PURPLE), 
    GOAL("goal", 52, ChatFormatting.GREEN);
    
    private final String name;
    private final int texture;
    private final ChatFormatting chatColor;
    
    private FrameType(final String string3, final int integer4, final ChatFormatting c) {
        this.name = string3;
        this.texture = integer4;
        this.chatColor = c;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getTexture() {
        return this.texture;
    }
    
    public static FrameType byName(final String string) {
        for (final FrameType aa5 : values()) {
            if (aa5.name.equals(string)) {
                return aa5;
            }
        }
        throw new IllegalArgumentException("Unknown frame type '" + string + "'");
    }
    
    public ChatFormatting getChatColor() {
        return this.chatColor;
    }
}
