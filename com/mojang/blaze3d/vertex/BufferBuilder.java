package com.mojang.blaze3d.vertex;

import org.apache.logging.log4j.LogManager;
import com.google.common.primitives.Floats;
import java.nio.ByteOrder;
import java.util.BitSet;
import java.util.Arrays;
import com.mojang.blaze3d.platform.MemoryTracker;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.nio.IntBuffer;
import java.nio.ByteBuffer;
import org.apache.logging.log4j.Logger;

public class BufferBuilder {
    private static final Logger LOGGER;
    private ByteBuffer buffer;
    private IntBuffer intBuffer;
    private ShortBuffer shortBuffer;
    private FloatBuffer floatBuffer;
    private int vertices;
    private VertexFormatElement currentElement;
    private int elementIndex;
    private boolean noColor;
    private int mode;
    private double xo;
    private double yo;
    private double zo;
    private VertexFormat format;
    private boolean building;
    
    public BufferBuilder(final int integer) {
        this.buffer = MemoryTracker.createByteBuffer(integer * 4);
        this.intBuffer = this.buffer.asIntBuffer();
        this.shortBuffer = this.buffer.asShortBuffer();
        this.floatBuffer = this.buffer.asFloatBuffer();
    }
    
    private void ensureCapacity(final int integer) {
        if (this.vertices * this.format.getVertexSize() + integer <= this.buffer.capacity()) {
            return;
        }
        final int integer2 = this.buffer.capacity();
        final int integer3 = integer2 + roundUp(integer);
        BufferBuilder.LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", integer2, integer3);
        final int integer4 = this.intBuffer.position();
        final ByteBuffer byteBuffer6 = MemoryTracker.createByteBuffer(integer3);
        this.buffer.position(0);
        byteBuffer6.put(this.buffer);
        byteBuffer6.rewind();
        this.buffer = byteBuffer6;
        this.floatBuffer = this.buffer.asFloatBuffer().asReadOnlyBuffer();
        (this.intBuffer = this.buffer.asIntBuffer()).position(integer4);
        (this.shortBuffer = this.buffer.asShortBuffer()).position(integer4 << 1);
    }
    
    private static int roundUp(final int integer) {
        int integer2 = 2097152;
        if (integer == 0) {
            return integer2;
        }
        if (integer < 0) {
            integer2 *= -1;
        }
        final int integer3 = integer % integer2;
        if (integer3 == 0) {
            return integer;
        }
        return integer + integer2 - integer3;
    }
    
    public void sortQuads(final float float1, final float float2, final float float3) {
        final int integer5 = this.vertices / 4;
        final float[] arr6 = new float[integer5];
        for (int integer6 = 0; integer6 < integer5; ++integer6) {
            arr6[integer6] = getQuadDistanceFromPlayer(this.floatBuffer, (float)(float1 + this.xo), (float)(float2 + this.yo), (float)(float3 + this.zo), this.format.getIntegerSize(), integer6 * this.format.getVertexSize());
        }
        final Integer[] arr7 = new Integer[integer5];
        for (int integer7 = 0; integer7 < arr7.length; ++integer7) {
            arr7[integer7] = integer7;
        }
        Arrays.sort((Object[])arr7, (integer2, integer3) -> Floats.compare(arr6[integer3], arr6[integer2]));
        final BitSet bitSet8 = new BitSet();
        final int integer8 = this.format.getVertexSize();
        final int[] arr8 = new int[integer8];
        for (int integer9 = bitSet8.nextClearBit(0); integer9 < arr7.length; integer9 = bitSet8.nextClearBit(integer9 + 1)) {
            final int integer10 = arr7[integer9];
            if (integer10 != integer9) {
                this.intBuffer.limit(integer10 * integer8 + integer8);
                this.intBuffer.position(integer10 * integer8);
                this.intBuffer.get(arr8);
                for (int integer11 = integer10, integer12 = arr7[integer11]; integer11 != integer9; integer11 = integer12, integer12 = arr7[integer11]) {
                    this.intBuffer.limit(integer12 * integer8 + integer8);
                    this.intBuffer.position(integer12 * integer8);
                    final IntBuffer intBuffer15 = this.intBuffer.slice();
                    this.intBuffer.limit(integer11 * integer8 + integer8);
                    this.intBuffer.position(integer11 * integer8);
                    this.intBuffer.put(intBuffer15);
                    bitSet8.set(integer11);
                }
                this.intBuffer.limit(integer9 * integer8 + integer8);
                this.intBuffer.position(integer9 * integer8);
                this.intBuffer.put(arr8);
            }
            bitSet8.set(integer9);
        }
    }
    
    public State getState() {
        this.intBuffer.rewind();
        final int integer2 = this.getBufferIndex();
        this.intBuffer.limit(integer2);
        final int[] arr3 = new int[integer2];
        this.intBuffer.get(arr3);
        this.intBuffer.limit(this.intBuffer.capacity());
        this.intBuffer.position(integer2);
        return new State(arr3, new VertexFormat(this.format));
    }
    
    private int getBufferIndex() {
        return this.vertices * this.format.getIntegerSize();
    }
    
    private static float getQuadDistanceFromPlayer(final FloatBuffer floatBuffer, final float float2, final float float3, final float float4, final int integer5, final int integer6) {
        final float float5 = floatBuffer.get(integer6 + integer5 * 0 + 0);
        final float float6 = floatBuffer.get(integer6 + integer5 * 0 + 1);
        final float float7 = floatBuffer.get(integer6 + integer5 * 0 + 2);
        final float float8 = floatBuffer.get(integer6 + integer5 * 1 + 0);
        final float float9 = floatBuffer.get(integer6 + integer5 * 1 + 1);
        final float float10 = floatBuffer.get(integer6 + integer5 * 1 + 2);
        final float float11 = floatBuffer.get(integer6 + integer5 * 2 + 0);
        final float float12 = floatBuffer.get(integer6 + integer5 * 2 + 1);
        final float float13 = floatBuffer.get(integer6 + integer5 * 2 + 2);
        final float float14 = floatBuffer.get(integer6 + integer5 * 3 + 0);
        final float float15 = floatBuffer.get(integer6 + integer5 * 3 + 1);
        final float float16 = floatBuffer.get(integer6 + integer5 * 3 + 2);
        final float float17 = (float5 + float8 + float11 + float14) * 0.25f - float2;
        final float float18 = (float6 + float9 + float12 + float15) * 0.25f - float3;
        final float float19 = (float7 + float10 + float13 + float16) * 0.25f - float4;
        return float17 * float17 + float18 * float18 + float19 * float19;
    }
    
    public void restoreState(final State a) {
        this.intBuffer.clear();
        this.ensureCapacity(a.array().length * 4);
        this.intBuffer.put(a.array());
        this.vertices = a.vertices();
        this.format = new VertexFormat(a.getFormat());
    }
    
    public void clear() {
        this.vertices = 0;
        this.currentElement = null;
        this.elementIndex = 0;
    }
    
    public void begin(final int integer, final VertexFormat cvc) {
        if (this.building) {
            throw new IllegalStateException("Already building!");
        }
        this.building = true;
        this.clear();
        this.mode = integer;
        this.format = cvc;
        this.currentElement = cvc.getElement(this.elementIndex);
        this.noColor = false;
        this.buffer.limit(this.buffer.capacity());
    }
    
    public BufferBuilder uv(final double double1, final double double2) {
        final int integer6 = this.vertices * this.format.getVertexSize() + this.format.getOffset(this.elementIndex);
        switch (this.currentElement.getType()) {
            case FLOAT: {
                this.buffer.putFloat(integer6, (float)double1);
                this.buffer.putFloat(integer6 + 4, (float)double2);
                break;
            }
            case UINT:
            case INT: {
                this.buffer.putInt(integer6, (int)double1);
                this.buffer.putInt(integer6 + 4, (int)double2);
                break;
            }
            case USHORT:
            case SHORT: {
                this.buffer.putShort(integer6, (short)double2);
                this.buffer.putShort(integer6 + 2, (short)double1);
                break;
            }
            case UBYTE:
            case BYTE: {
                this.buffer.put(integer6, (byte)double2);
                this.buffer.put(integer6 + 1, (byte)double1);
                break;
            }
        }
        this.nextElement();
        return this;
    }
    
    public BufferBuilder uv2(final int integer1, final int integer2) {
        final int integer3 = this.vertices * this.format.getVertexSize() + this.format.getOffset(this.elementIndex);
        switch (this.currentElement.getType()) {
            case FLOAT: {
                this.buffer.putFloat(integer3, (float)integer1);
                this.buffer.putFloat(integer3 + 4, (float)integer2);
                break;
            }
            case UINT:
            case INT: {
                this.buffer.putInt(integer3, integer1);
                this.buffer.putInt(integer3 + 4, integer2);
                break;
            }
            case USHORT:
            case SHORT: {
                this.buffer.putShort(integer3, (short)integer2);
                this.buffer.putShort(integer3 + 2, (short)integer1);
                break;
            }
            case UBYTE:
            case BYTE: {
                this.buffer.put(integer3, (byte)integer2);
                this.buffer.put(integer3 + 1, (byte)integer1);
                break;
            }
        }
        this.nextElement();
        return this;
    }
    
    public void faceTex2(final int integer1, final int integer2, final int integer3, final int integer4) {
        final int integer5 = (this.vertices - 4) * this.format.getIntegerSize() + this.format.getUvOffset(1) / 4;
        final int integer6 = this.format.getVertexSize() >> 2;
        this.intBuffer.put(integer5, integer1);
        this.intBuffer.put(integer5 + integer6, integer2);
        this.intBuffer.put(integer5 + integer6 * 2, integer3);
        this.intBuffer.put(integer5 + integer6 * 3, integer4);
    }
    
    public void postProcessFacePosition(final double double1, final double double2, final double double3) {
        final int integer8 = this.format.getIntegerSize();
        final int integer9 = (this.vertices - 4) * integer8;
        for (int integer10 = 0; integer10 < 4; ++integer10) {
            final int integer11 = integer9 + integer10 * integer8;
            final int integer12 = integer11 + 1;
            final int integer13 = integer12 + 1;
            this.intBuffer.put(integer11, Float.floatToRawIntBits((float)(double1 + this.xo) + Float.intBitsToFloat(this.intBuffer.get(integer11))));
            this.intBuffer.put(integer12, Float.floatToRawIntBits((float)(double2 + this.yo) + Float.intBitsToFloat(this.intBuffer.get(integer12))));
            this.intBuffer.put(integer13, Float.floatToRawIntBits((float)(double3 + this.zo) + Float.intBitsToFloat(this.intBuffer.get(integer13))));
        }
    }
    
    private int getStartingColorIndex(final int integer) {
        return ((this.vertices - integer) * this.format.getVertexSize() + this.format.getColorOffset()) / 4;
    }
    
    public void faceTint(final float float1, final float float2, final float float3, final int integer) {
        final int integer2 = this.getStartingColorIndex(integer);
        int integer3 = -1;
        if (!this.noColor) {
            integer3 = this.intBuffer.get(integer2);
            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                final int integer4 = (int)((integer3 & 0xFF) * float1);
                final int integer5 = (int)((integer3 >> 8 & 0xFF) * float2);
                final int integer6 = (int)((integer3 >> 16 & 0xFF) * float3);
                integer3 &= 0xFF000000;
                integer3 |= (integer6 << 16 | integer5 << 8 | integer4);
            }
            else {
                final int integer4 = (int)((integer3 >> 24 & 0xFF) * float1);
                final int integer5 = (int)((integer3 >> 16 & 0xFF) * float2);
                final int integer6 = (int)((integer3 >> 8 & 0xFF) * float3);
                integer3 &= 0xFF;
                integer3 |= (integer4 << 24 | integer5 << 16 | integer6 << 8);
            }
        }
        this.intBuffer.put(integer2, integer3);
    }
    
    private void fixupVertexColor(final int integer1, final int integer2) {
        final int integer3 = this.getStartingColorIndex(integer2);
        final int integer4 = integer1 >> 16 & 0xFF;
        final int integer5 = integer1 >> 8 & 0xFF;
        final int integer6 = integer1 & 0xFF;
        this.putColor(integer3, integer4, integer5, integer6);
    }
    
    public void fixupVertexColor(final float float1, final float float2, final float float3, final int integer) {
        final int integer2 = this.getStartingColorIndex(integer);
        final int integer3 = clamp((int)(float1 * 255.0f), 0, 255);
        final int integer4 = clamp((int)(float2 * 255.0f), 0, 255);
        final int integer5 = clamp((int)(float3 * 255.0f), 0, 255);
        this.putColor(integer2, integer3, integer4, integer5);
    }
    
    private static int clamp(final int integer1, final int integer2, final int integer3) {
        if (integer1 < integer2) {
            return integer2;
        }
        if (integer1 > integer3) {
            return integer3;
        }
        return integer1;
    }
    
    private void putColor(final int integer1, final int integer2, final int integer3, final int integer4) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            this.intBuffer.put(integer1, 0xFF000000 | integer4 << 16 | integer3 << 8 | integer2);
        }
        else {
            this.intBuffer.put(integer1, integer2 << 24 | integer3 << 16 | integer4 << 8 | 0xFF);
        }
    }
    
    public void noColor() {
        this.noColor = true;
    }
    
    public BufferBuilder color(final float float1, final float float2, final float float3, final float float4) {
        return this.color((int)(float1 * 255.0f), (int)(float2 * 255.0f), (int)(float3 * 255.0f), (int)(float4 * 255.0f));
    }
    
    public BufferBuilder color(final int integer1, final int integer2, final int integer3, final int integer4) {
        if (this.noColor) {
            return this;
        }
        final int integer5 = this.vertices * this.format.getVertexSize() + this.format.getOffset(this.elementIndex);
        switch (this.currentElement.getType()) {
            case FLOAT: {
                this.buffer.putFloat(integer5, integer1 / 255.0f);
                this.buffer.putFloat(integer5 + 4, integer2 / 255.0f);
                this.buffer.putFloat(integer5 + 8, integer3 / 255.0f);
                this.buffer.putFloat(integer5 + 12, integer4 / 255.0f);
                break;
            }
            case UINT:
            case INT: {
                this.buffer.putFloat(integer5, (float)integer1);
                this.buffer.putFloat(integer5 + 4, (float)integer2);
                this.buffer.putFloat(integer5 + 8, (float)integer3);
                this.buffer.putFloat(integer5 + 12, (float)integer4);
                break;
            }
            case USHORT:
            case SHORT: {
                this.buffer.putShort(integer5, (short)integer1);
                this.buffer.putShort(integer5 + 2, (short)integer2);
                this.buffer.putShort(integer5 + 4, (short)integer3);
                this.buffer.putShort(integer5 + 6, (short)integer4);
                break;
            }
            case UBYTE:
            case BYTE: {
                if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                    this.buffer.put(integer5, (byte)integer1);
                    this.buffer.put(integer5 + 1, (byte)integer2);
                    this.buffer.put(integer5 + 2, (byte)integer3);
                    this.buffer.put(integer5 + 3, (byte)integer4);
                    break;
                }
                this.buffer.put(integer5, (byte)integer4);
                this.buffer.put(integer5 + 1, (byte)integer3);
                this.buffer.put(integer5 + 2, (byte)integer2);
                this.buffer.put(integer5 + 3, (byte)integer1);
                break;
            }
        }
        this.nextElement();
        return this;
    }
    
    public void putBulkData(final int[] arr) {
        this.ensureCapacity(arr.length * 4 + this.format.getVertexSize());
        this.intBuffer.position(this.getBufferIndex());
        this.intBuffer.put(arr);
        this.vertices += arr.length / this.format.getIntegerSize();
    }
    
    public void endVertex() {
        ++this.vertices;
        this.ensureCapacity(this.format.getVertexSize());
    }
    
    public BufferBuilder vertex(final double double1, final double double2, final double double3) {
        final int integer8 = this.vertices * this.format.getVertexSize() + this.format.getOffset(this.elementIndex);
        switch (this.currentElement.getType()) {
            case FLOAT: {
                this.buffer.putFloat(integer8, (float)(double1 + this.xo));
                this.buffer.putFloat(integer8 + 4, (float)(double2 + this.yo));
                this.buffer.putFloat(integer8 + 8, (float)(double3 + this.zo));
                break;
            }
            case UINT:
            case INT: {
                this.buffer.putInt(integer8, Float.floatToRawIntBits((float)(double1 + this.xo)));
                this.buffer.putInt(integer8 + 4, Float.floatToRawIntBits((float)(double2 + this.yo)));
                this.buffer.putInt(integer8 + 8, Float.floatToRawIntBits((float)(double3 + this.zo)));
                break;
            }
            case USHORT:
            case SHORT: {
                this.buffer.putShort(integer8, (short)(double1 + this.xo));
                this.buffer.putShort(integer8 + 2, (short)(double2 + this.yo));
                this.buffer.putShort(integer8 + 4, (short)(double3 + this.zo));
                break;
            }
            case UBYTE:
            case BYTE: {
                this.buffer.put(integer8, (byte)(double1 + this.xo));
                this.buffer.put(integer8 + 1, (byte)(double2 + this.yo));
                this.buffer.put(integer8 + 2, (byte)(double3 + this.zo));
                break;
            }
        }
        this.nextElement();
        return this;
    }
    
    public void postNormal(final float float1, final float float2, final float float3) {
        final int integer5 = (byte)(float1 * 127.0f) & 0xFF;
        final int integer6 = (byte)(float2 * 127.0f) & 0xFF;
        final int integer7 = (byte)(float3 * 127.0f) & 0xFF;
        final int integer8 = integer5 | integer6 << 8 | integer7 << 16;
        final int integer9 = this.format.getVertexSize() >> 2;
        final int integer10 = (this.vertices - 4) * integer9 + this.format.getNormalOffset() / 4;
        this.intBuffer.put(integer10, integer8);
        this.intBuffer.put(integer10 + integer9, integer8);
        this.intBuffer.put(integer10 + integer9 * 2, integer8);
        this.intBuffer.put(integer10 + integer9 * 3, integer8);
    }
    
    private void nextElement() {
        ++this.elementIndex;
        this.elementIndex %= this.format.getElementCount();
        this.currentElement = this.format.getElement(this.elementIndex);
        if (this.currentElement.getUsage() == VertexFormatElement.Usage.PADDING) {
            this.nextElement();
        }
    }
    
    public BufferBuilder normal(final float float1, final float float2, final float float3) {
        final int integer5 = this.vertices * this.format.getVertexSize() + this.format.getOffset(this.elementIndex);
        switch (this.currentElement.getType()) {
            case FLOAT: {
                this.buffer.putFloat(integer5, float1);
                this.buffer.putFloat(integer5 + 4, float2);
                this.buffer.putFloat(integer5 + 8, float3);
                break;
            }
            case UINT:
            case INT: {
                this.buffer.putInt(integer5, (int)float1);
                this.buffer.putInt(integer5 + 4, (int)float2);
                this.buffer.putInt(integer5 + 8, (int)float3);
                break;
            }
            case USHORT:
            case SHORT: {
                this.buffer.putShort(integer5, (short)((int)float1 * 32767 & 0xFFFF));
                this.buffer.putShort(integer5 + 2, (short)((int)float2 * 32767 & 0xFFFF));
                this.buffer.putShort(integer5 + 4, (short)((int)float3 * 32767 & 0xFFFF));
                break;
            }
            case UBYTE:
            case BYTE: {
                this.buffer.put(integer5, (byte)((int)float1 * 127 & 0xFF));
                this.buffer.put(integer5 + 1, (byte)((int)float2 * 127 & 0xFF));
                this.buffer.put(integer5 + 2, (byte)((int)float3 * 127 & 0xFF));
                break;
            }
        }
        this.nextElement();
        return this;
    }
    
    public void offset(final double double1, final double double2, final double double3) {
        this.xo = double1;
        this.yo = double2;
        this.zo = double3;
    }
    
    public void end() {
        if (!this.building) {
            throw new IllegalStateException("Not building!");
        }
        this.building = false;
        this.buffer.position(0);
        this.buffer.limit(this.getBufferIndex() * 4);
    }
    
    public ByteBuffer getBuffer() {
        return this.buffer;
    }
    
    public VertexFormat getVertexFormat() {
        return this.format;
    }
    
    public int getVertexCount() {
        return this.vertices;
    }
    
    public int getDrawMode() {
        return this.mode;
    }
    
    public void fixupQuadColor(final int integer) {
        for (int integer2 = 0; integer2 < 4; ++integer2) {
            this.fixupVertexColor(integer, integer2 + 1);
        }
    }
    
    public void fixupQuadColor(final float float1, final float float2, final float float3) {
        for (int integer5 = 0; integer5 < 4; ++integer5) {
            this.fixupVertexColor(float1, float2, float3, integer5 + 1);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public class State {
        private final int[] array;
        private final VertexFormat format;
        
        public State(final int[] arr, final VertexFormat cvc) {
            this.array = arr;
            this.format = cvc;
        }
        
        public int[] array() {
            return this.array;
        }
        
        public int vertices() {
            return this.array.length / this.format.getIntegerSize();
        }
        
        public VertexFormat getFormat() {
            return this.format;
        }
    }
}
