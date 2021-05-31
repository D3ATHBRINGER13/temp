package net.minecraft.realms;

import com.mojang.blaze3d.vertex.VertexFormatElement;

public class RealmsVertexFormatElement {
    private final VertexFormatElement v;
    
    public RealmsVertexFormatElement(final VertexFormatElement cvd) {
        this.v = cvd;
    }
    
    public VertexFormatElement getVertexFormatElement() {
        return this.v;
    }
    
    public boolean isPosition() {
        return this.v.isPosition();
    }
    
    public int getIndex() {
        return this.v.getIndex();
    }
    
    public int getByteSize() {
        return this.v.getByteSize();
    }
    
    public int getCount() {
        return this.v.getCount();
    }
    
    public int hashCode() {
        return this.v.hashCode();
    }
    
    public boolean equals(final Object object) {
        return this.v.equals(object);
    }
    
    public String toString() {
        return this.v.toString();
    }
}
