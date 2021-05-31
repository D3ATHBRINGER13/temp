package net.minecraft.util;

import java.util.function.IntConsumer;
import org.apache.commons.lang3.Validate;

public class BitStorage {
    private final long[] data;
    private final int bits;
    private final long mask;
    private final int size;
    
    public BitStorage(final int integer1, final int integer2) {
        this(integer1, integer2, new long[Mth.roundUp(integer2 * integer1, 64) / 64]);
    }
    
    public BitStorage(final int integer1, final int integer2, final long[] arr) {
        Validate.inclusiveBetween(1L, 32L, (long)integer1);
        this.size = integer2;
        this.bits = integer1;
        this.data = arr;
        this.mask = (1L << integer1) - 1L;
        final int integer3 = Mth.roundUp(integer2 * integer1, 64) / 64;
        if (arr.length != integer3) {
            throw new RuntimeException(new StringBuilder().append("Invalid length given for storage, got: ").append(arr.length).append(" but expected: ").append(integer3).toString());
        }
    }
    
    public int getAndSet(final int integer1, final int integer2) {
        Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)integer1);
        Validate.inclusiveBetween(0L, this.mask, (long)integer2);
        final int integer3 = integer1 * this.bits;
        final int integer4 = integer3 >> 6;
        final int integer5 = (integer1 + 1) * this.bits - 1 >> 6;
        final int integer6 = integer3 ^ integer4 << 6;
        int integer7 = 0;
        integer7 |= (int)(this.data[integer4] >>> integer6 & this.mask);
        this.data[integer4] = ((this.data[integer4] & ~(this.mask << integer6)) | ((long)integer2 & this.mask) << integer6);
        if (integer4 != integer5) {
            final int integer8 = 64 - integer6;
            final int integer9 = this.bits - integer8;
            integer7 |= (int)(this.data[integer5] << integer8 & this.mask);
            this.data[integer5] = (this.data[integer5] >>> integer9 << integer9 | ((long)integer2 & this.mask) >> integer8);
        }
        return integer7;
    }
    
    public void set(final int integer1, final int integer2) {
        Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)integer1);
        Validate.inclusiveBetween(0L, this.mask, (long)integer2);
        final int integer3 = integer1 * this.bits;
        final int integer4 = integer3 >> 6;
        final int integer5 = (integer1 + 1) * this.bits - 1 >> 6;
        final int integer6 = integer3 ^ integer4 << 6;
        this.data[integer4] = ((this.data[integer4] & ~(this.mask << integer6)) | ((long)integer2 & this.mask) << integer6);
        if (integer4 != integer5) {
            final int integer7 = 64 - integer6;
            final int integer8 = this.bits - integer7;
            this.data[integer5] = (this.data[integer5] >>> integer8 << integer8 | ((long)integer2 & this.mask) >> integer7);
        }
    }
    
    public int get(final int integer) {
        Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)integer);
        final int integer2 = integer * this.bits;
        final int integer3 = integer2 >> 6;
        final int integer4 = (integer + 1) * this.bits - 1 >> 6;
        final int integer5 = integer2 ^ integer3 << 6;
        if (integer3 == integer4) {
            return (int)(this.data[integer3] >>> integer5 & this.mask);
        }
        final int integer6 = 64 - integer5;
        return (int)((this.data[integer3] >>> integer5 | this.data[integer4] << integer6) & this.mask);
    }
    
    public long[] getRaw() {
        return this.data;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public int getBits() {
        return this.bits;
    }
    
    public void getAll(final IntConsumer intConsumer) {
        final int integer3 = this.data.length;
        if (integer3 == 0) {
            return;
        }
        int integer4 = 0;
        long long5 = this.data[0];
        long long6 = (integer3 > 1) ? this.data[1] : 0L;
        for (int integer5 = 0; integer5 < this.size; ++integer5) {
            final int integer6 = integer5 * this.bits;
            final int integer7 = integer6 >> 6;
            final int integer8 = (integer5 + 1) * this.bits - 1 >> 6;
            final int integer9 = integer6 ^ integer7 << 6;
            if (integer7 != integer4) {
                long5 = long6;
                long6 = ((integer7 + 1 < integer3) ? this.data[integer7 + 1] : 0L);
                integer4 = integer7;
            }
            if (integer7 == integer8) {
                intConsumer.accept((int)(long5 >>> integer9 & this.mask));
            }
            else {
                final int integer10 = 64 - integer9;
                intConsumer.accept((int)((long5 >>> integer9 | long6 << integer10) & this.mask));
            }
        }
    }
}
