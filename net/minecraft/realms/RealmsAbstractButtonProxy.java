package net.minecraft.realms;

public interface RealmsAbstractButtonProxy<T extends AbstractRealmsButton<?>> {
    T getButton();
    
    boolean active();
    
    void active(final boolean boolean1);
    
    boolean isVisible();
    
    void setVisible(final boolean boolean1);
}
