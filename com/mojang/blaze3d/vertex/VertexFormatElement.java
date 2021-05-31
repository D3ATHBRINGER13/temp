package com.mojang.blaze3d.vertex;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VertexFormatElement {
    private static final Logger LOGGER;
    private final Type type;
    private final Usage usage;
    private final int index;
    private final int count;
    
    public VertexFormatElement(final int integer1, final Type a, final Usage b, final int integer4) {
        if (this.supportsUsage(integer1, b)) {
            this.usage = b;
        }
        else {
            VertexFormatElement.LOGGER.warn("Multiple vertex elements of the same type other than UVs are not supported. Forcing type to UV.");
            this.usage = Usage.UV;
        }
        this.type = a;
        this.index = integer1;
        this.count = integer4;
    }
    
    private final boolean supportsUsage(final int integer, final Usage b) {
        return integer == 0 || b == Usage.UV;
    }
    
    public final Type getType() {
        return this.type;
    }
    
    public final Usage getUsage() {
        return this.usage;
    }
    
    public final int getCount() {
        return this.count;
    }
    
    public final int getIndex() {
        return this.index;
    }
    
    public String toString() {
        return new StringBuilder().append(this.count).append(",").append(this.usage.getName()).append(",").append(this.type.getName()).toString();
    }
    
    public final int getByteSize() {
        return this.type.getSize() * this.count;
    }
    
    public final boolean isPosition() {
        return this.usage == Usage.POSITION;
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        final VertexFormatElement cvd3 = (VertexFormatElement)object;
        return this.count == cvd3.count && this.index == cvd3.index && this.type == cvd3.type && this.usage == cvd3.usage;
    }
    
    public int hashCode() {
        int integer2 = this.type.hashCode();
        integer2 = 31 * integer2 + this.usage.hashCode();
        integer2 = 31 * integer2 + this.index;
        integer2 = 31 * integer2 + this.count;
        return integer2;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public enum Usage {
        POSITION("Position"), 
        NORMAL("Normal"), 
        COLOR("Vertex Color"), 
        UV("UV"), 
        MATRIX("Bone Matrix"), 
        BLEND_WEIGHT("Blend Weight"), 
        PADDING("Padding");
        
        private final String name;
        
        private Usage(final String string3) {
            this.name = string3;
        }
        
        public String getName() {
            return this.name;
        }
    }
    
    public enum Type {
        FLOAT(4, "Float", 5126), 
        UBYTE(1, "Unsigned Byte", 5121), 
        BYTE(1, "Byte", 5120), 
        USHORT(2, "Unsigned Short", 5123), 
        SHORT(2, "Short", 5122), 
        UINT(4, "Unsigned Int", 5125), 
        INT(4, "Int", 5124);
        
        private final int size;
        private final String name;
        private final int glType;
        
        private Type(final int integer3, final String string4, final int integer5) {
            this.size = integer3;
            this.name = string4;
            this.glType = integer5;
        }
        
        public int getSize() {
            return this.size;
        }
        
        public String getName() {
            return this.name;
        }
        
        public int getGlType() {
            return this.glType;
        }
    }
}
