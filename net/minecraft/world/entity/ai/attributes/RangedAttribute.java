package net.minecraft.world.entity.ai.attributes;

import net.minecraft.util.Mth;
import javax.annotation.Nullable;

public class RangedAttribute extends BaseAttribute {
    private final double minValue;
    private final double maxValue;
    private String importLegacyName;
    
    public RangedAttribute(@Nullable final Attribute ajn, final String string, final double double3, final double double4, final double double5) {
        super(ajn, string, double3);
        this.minValue = double4;
        this.maxValue = double5;
        if (double4 > double5) {
            throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
        }
        if (double3 < double4) {
            throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
        }
        if (double3 > double5) {
            throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
        }
    }
    
    public RangedAttribute importLegacyName(final String string) {
        this.importLegacyName = string;
        return this;
    }
    
    public String getImportLegacyName() {
        return this.importLegacyName;
    }
    
    public double sanitizeValue(double double1) {
        double1 = Mth.clamp(double1, this.minValue, this.maxValue);
        return double1;
    }
}
