package net.minecraft.realms;

import java.util.Iterator;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.google.common.collect.Lists;
import java.util.List;
import com.mojang.blaze3d.vertex.VertexFormat;

public class RealmsVertexFormat {
    private VertexFormat v;
    
    public RealmsVertexFormat(final VertexFormat cvc) {
        this.v = cvc;
    }
    
    public RealmsVertexFormat from(final VertexFormat cvc) {
        this.v = cvc;
        return this;
    }
    
    public VertexFormat getVertexFormat() {
        return this.v;
    }
    
    public void clear() {
        this.v.clear();
    }
    
    public int getUvOffset(final int integer) {
        return this.v.getUvOffset(integer);
    }
    
    public int getElementCount() {
        return this.v.getElementCount();
    }
    
    public boolean hasColor() {
        return this.v.hasColor();
    }
    
    public boolean hasUv(final int integer) {
        return this.v.hasUv(integer);
    }
    
    public RealmsVertexFormatElement getElement(final int integer) {
        return new RealmsVertexFormatElement(this.v.getElement(integer));
    }
    
    public RealmsVertexFormat addElement(final RealmsVertexFormatElement realmsVertexFormatElement) {
        return this.from(this.v.addElement(realmsVertexFormatElement.getVertexFormatElement()));
    }
    
    public int getColorOffset() {
        return this.v.getColorOffset();
    }
    
    public List<RealmsVertexFormatElement> getElements() {
        final List<RealmsVertexFormatElement> list2 = (List<RealmsVertexFormatElement>)Lists.newArrayList();
        for (final VertexFormatElement cvd4 : this.v.getElements()) {
            list2.add(new RealmsVertexFormatElement(cvd4));
        }
        return list2;
    }
    
    public boolean hasNormal() {
        return this.v.hasNormal();
    }
    
    public int getVertexSize() {
        return this.v.getVertexSize();
    }
    
    public int getOffset(final int integer) {
        return this.v.getOffset(integer);
    }
    
    public int getNormalOffset() {
        return this.v.getNormalOffset();
    }
    
    public int getIntegerSize() {
        return this.v.getIntegerSize();
    }
    
    public boolean equals(final Object object) {
        return this.v.equals(object);
    }
    
    public int hashCode() {
        return this.v.hashCode();
    }
    
    public String toString() {
        return this.v.toString();
    }
}
