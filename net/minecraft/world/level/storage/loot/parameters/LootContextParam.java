package net.minecraft.world.level.storage.loot.parameters;

import net.minecraft.resources.ResourceLocation;

public class LootContextParam<T> {
    private final ResourceLocation name;
    
    public LootContextParam(final ResourceLocation qv) {
        this.name = qv;
    }
    
    public ResourceLocation getName() {
        return this.name;
    }
    
    public String toString() {
        return new StringBuilder().append("<parameter ").append(this.name).append(">").toString();
    }
}
