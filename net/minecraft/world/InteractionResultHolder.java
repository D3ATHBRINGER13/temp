package net.minecraft.world;

public class InteractionResultHolder<T> {
    private final InteractionResult result;
    private final T object;
    
    public InteractionResultHolder(final InteractionResult ahj, final T object) {
        this.result = ahj;
        this.object = object;
    }
    
    public InteractionResult getResult() {
        return this.result;
    }
    
    public T getObject() {
        return this.object;
    }
}
