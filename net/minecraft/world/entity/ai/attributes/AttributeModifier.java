package net.minecraft.world.entity.ai.attributes;

import java.util.Objects;
import java.util.Random;
import net.minecraft.util.Mth;
import io.netty.util.internal.ThreadLocalRandom;
import java.util.UUID;
import java.util.function.Supplier;

public class AttributeModifier {
    private final double amount;
    private final Operation operation;
    private final Supplier<String> nameGetter;
    private final UUID id;
    private boolean serialize;
    
    public AttributeModifier(final String string, final double double2, final Operation a) {
        this(Mth.createInsecureUUID((Random)ThreadLocalRandom.current()), (Supplier<String>)(() -> string), double2, a);
    }
    
    public AttributeModifier(final UUID uUID, final String string, final double double3, final Operation a) {
        this(uUID, (Supplier<String>)(() -> string), double3, a);
    }
    
    public AttributeModifier(final UUID uUID, final Supplier<String> supplier, final double double3, final Operation a) {
        this.serialize = true;
        this.id = uUID;
        this.nameGetter = supplier;
        this.amount = double3;
        this.operation = a;
    }
    
    public UUID getId() {
        return this.id;
    }
    
    public String getName() {
        return (String)this.nameGetter.get();
    }
    
    public Operation getOperation() {
        return this.operation;
    }
    
    public double getAmount() {
        return this.amount;
    }
    
    public boolean isSerializable() {
        return this.serialize;
    }
    
    public AttributeModifier setSerialize(final boolean boolean1) {
        this.serialize = boolean1;
        return this;
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        final AttributeModifier ajp3 = (AttributeModifier)object;
        return Objects.equals(this.id, ajp3.id);
    }
    
    public int hashCode() {
        return (this.id != null) ? this.id.hashCode() : 0;
    }
    
    public String toString() {
        return new StringBuilder().append("AttributeModifier{amount=").append(this.amount).append(", operation=").append(this.operation).append(", name='").append((String)this.nameGetter.get()).append('\'').append(", id=").append(this.id).append(", serialize=").append(this.serialize).append('}').toString();
    }
    
    public enum Operation {
        ADDITION(0), 
        MULTIPLY_BASE(1), 
        MULTIPLY_TOTAL(2);
        
        private static final Operation[] OPERATIONS;
        private final int value;
        
        private Operation(final int integer3) {
            this.value = integer3;
        }
        
        public int toValue() {
            return this.value;
        }
        
        public static Operation fromValue(final int integer) {
            if (integer < 0 || integer >= Operation.OPERATIONS.length) {
                throw new IllegalArgumentException(new StringBuilder().append("No operation with value ").append(integer).toString());
            }
            return Operation.OPERATIONS[integer];
        }
        
        static {
            OPERATIONS = new Operation[] { Operation.ADDITION, Operation.MULTIPLY_BASE, Operation.MULTIPLY_TOTAL };
        }
    }
}
