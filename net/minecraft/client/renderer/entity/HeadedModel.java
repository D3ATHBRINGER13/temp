package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelPart;

public interface HeadedModel {
    ModelPart getHead();
    
    default void translateToHead(final float float1) {
        this.getHead().translateTo(float1);
    }
}
