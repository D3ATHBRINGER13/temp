package net.minecraft.world.entity;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;

public enum HumanoidArm {
    LEFT((Component)new TranslatableComponent("options.mainHand.left", new Object[0])), 
    RIGHT((Component)new TranslatableComponent("options.mainHand.right", new Object[0]));
    
    private final Component name;
    
    private HumanoidArm(final Component jo) {
        this.name = jo;
    }
    
    public HumanoidArm getOpposite() {
        if (this == HumanoidArm.LEFT) {
            return HumanoidArm.RIGHT;
        }
        return HumanoidArm.LEFT;
    }
    
    public String toString() {
        return this.name.getString();
    }
}
