package net.minecraft.advancements.critereon;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.advancements.CriterionTriggerInstance;

public class AbstractCriterionTriggerInstance implements CriterionTriggerInstance {
    private final ResourceLocation criterion;
    
    public AbstractCriterionTriggerInstance(final ResourceLocation qv) {
        this.criterion = qv;
    }
    
    public ResourceLocation getCriterion() {
        return this.criterion;
    }
    
    public String toString() {
        return new StringBuilder().append("AbstractCriterionInstance{criterion=").append(this.criterion).append('}').toString();
    }
}
