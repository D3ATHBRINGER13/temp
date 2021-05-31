package net.minecraft.world.effect;

import net.minecraft.ChatFormatting;

public enum MobEffectCategory {
    BENEFICIAL(ChatFormatting.BLUE), 
    HARMFUL(ChatFormatting.RED), 
    NEUTRAL(ChatFormatting.BLUE);
    
    private final ChatFormatting tooltipFormatting;
    
    private MobEffectCategory(final ChatFormatting c) {
        this.tooltipFormatting = c;
    }
    
    public ChatFormatting getTooltipFormatting() {
        return this.tooltipFormatting;
    }
}
