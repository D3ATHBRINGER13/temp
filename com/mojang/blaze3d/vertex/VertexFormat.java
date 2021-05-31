package com.mojang.blaze3d.vertex;

import org.apache.logging.log4j.LogManager;
import com.google.common.collect.Lists;
import java.util.List;
import org.apache.logging.log4j.Logger;

public class VertexFormat {
    private static final Logger LOGGER;
    private final List<VertexFormatElement> elements;
    private final List<Integer> offsets;
    private int vertexSize;
    private int colorOffset;
    private final List<Integer> texOffset;
    private int normalOffset;
    
    public VertexFormat(final VertexFormat cvc) {
        this();
        for (int integer3 = 0; integer3 < cvc.getElementCount(); ++integer3) {
            this.addElement(cvc.getElement(integer3));
        }
        this.vertexSize = cvc.getVertexSize();
    }
    
    public VertexFormat() {
        this.elements = (List<VertexFormatElement>)Lists.newArrayList();
        this.offsets = (List<Integer>)Lists.newArrayList();
        this.colorOffset = -1;
        this.texOffset = (List<Integer>)Lists.newArrayList();
        this.normalOffset = -1;
    }
    
    public void clear() {
        this.elements.clear();
        this.offsets.clear();
        this.colorOffset = -1;
        this.texOffset.clear();
        this.normalOffset = -1;
        this.vertexSize = 0;
    }
    
    public VertexFormat addElement(final VertexFormatElement cvd) {
        if (cvd.isPosition() && this.hasPositionElement()) {
            VertexFormat.LOGGER.warn("VertexFormat error: Trying to add a position VertexFormatElement when one already exists, ignoring.");
            return this;
        }
        this.elements.add(cvd);
        this.offsets.add(this.vertexSize);
        switch (cvd.getUsage()) {
            case NORMAL: {
                this.normalOffset = this.vertexSize;
                break;
            }
            case COLOR: {
                this.colorOffset = this.vertexSize;
                break;
            }
            case UV: {
                this.texOffset.add(cvd.getIndex(), this.vertexSize);
                break;
            }
        }
        this.vertexSize += cvd.getByteSize();
        return this;
    }
    
    public boolean hasNormal() {
        return this.normalOffset >= 0;
    }
    
    public int getNormalOffset() {
        return this.normalOffset;
    }
    
    public boolean hasColor() {
        return this.colorOffset >= 0;
    }
    
    public int getColorOffset() {
        return this.colorOffset;
    }
    
    public boolean hasUv(final int integer) {
        return this.texOffset.size() - 1 >= integer;
    }
    
    public int getUvOffset(final int integer) {
        return (int)this.texOffset.get(integer);
    }
    
    public String toString() {
        String string2 = new StringBuilder().append("format: ").append(this.elements.size()).append(" elements: ").toString();
        for (int integer3 = 0; integer3 < this.elements.size(); ++integer3) {
            string2 += ((VertexFormatElement)this.elements.get(integer3)).toString();
            if (integer3 != this.elements.size() - 1) {
                string2 += " ";
            }
        }
        return string2;
    }
    
    private boolean hasPositionElement() {
        for (int integer2 = 0, integer3 = this.elements.size(); integer2 < integer3; ++integer2) {
            final VertexFormatElement cvd4 = (VertexFormatElement)this.elements.get(integer2);
            if (cvd4.isPosition()) {
                return true;
            }
        }
        return false;
    }
    
    public int getIntegerSize() {
        return this.getVertexSize() / 4;
    }
    
    public int getVertexSize() {
        return this.vertexSize;
    }
    
    public List<VertexFormatElement> getElements() {
        return this.elements;
    }
    
    public int getElementCount() {
        return this.elements.size();
    }
    
    public VertexFormatElement getElement(final int integer) {
        return (VertexFormatElement)this.elements.get(integer);
    }
    
    public int getOffset(final int integer) {
        return (int)this.offsets.get(integer);
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        final VertexFormat cvc3 = (VertexFormat)object;
        return this.vertexSize == cvc3.vertexSize && this.elements.equals(cvc3.elements) && this.offsets.equals(cvc3.offsets);
    }
    
    public int hashCode() {
        int integer2 = this.elements.hashCode();
        integer2 = 31 * integer2 + this.offsets.hashCode();
        integer2 = 31 * integer2 + this.vertexSize;
        return integer2;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
