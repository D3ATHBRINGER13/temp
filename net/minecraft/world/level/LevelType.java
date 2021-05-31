package net.minecraft.world.level;

import javax.annotation.Nullable;

public class LevelType {
    public static final LevelType[] LEVEL_TYPES;
    public static final LevelType NORMAL;
    public static final LevelType FLAT;
    public static final LevelType LARGE_BIOMES;
    public static final LevelType AMPLIFIED;
    public static final LevelType CUSTOMIZED;
    public static final LevelType BUFFET;
    public static final LevelType DEBUG_ALL_BLOCK_STATES;
    public static final LevelType NORMAL_1_1;
    private final int id;
    private final String generatorName;
    private final String generatorSerialization;
    private final int version;
    private boolean selectable;
    private boolean replacement;
    private boolean hasHelpText;
    private boolean hasCustomOptions;
    
    private LevelType(final int integer, final String string) {
        this(integer, string, string, 0);
    }
    
    private LevelType(final int integer1, final String string, final int integer3) {
        this(integer1, string, string, integer3);
    }
    
    private LevelType(final int integer1, final String string2, final String string3, final int integer4) {
        this.generatorName = string2;
        this.generatorSerialization = string3;
        this.version = integer4;
        this.selectable = true;
        this.id = integer1;
        LevelType.LEVEL_TYPES[integer1] = this;
    }
    
    public String getName() {
        return this.generatorName;
    }
    
    public String getSerialization() {
        return this.generatorSerialization;
    }
    
    public String getDescriptionId() {
        return "generator." + this.generatorName;
    }
    
    public String getHelpTextId() {
        return this.getDescriptionId() + ".info";
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public LevelType getReplacementForVersion(final int integer) {
        if (this == LevelType.NORMAL && integer == 0) {
            return LevelType.NORMAL_1_1;
        }
        return this;
    }
    
    public boolean hasCustomOptions() {
        return this.hasCustomOptions;
    }
    
    public LevelType setCustomOptions(final boolean boolean1) {
        this.hasCustomOptions = boolean1;
        return this;
    }
    
    private LevelType setSelectableByUser(final boolean boolean1) {
        this.selectable = boolean1;
        return this;
    }
    
    public boolean isSelectable() {
        return this.selectable;
    }
    
    private LevelType setHasReplacement() {
        this.replacement = true;
        return this;
    }
    
    public boolean hasReplacement() {
        return this.replacement;
    }
    
    @Nullable
    public static LevelType getLevelType(final String string) {
        for (final LevelType bhy5 : LevelType.LEVEL_TYPES) {
            if (bhy5 != null && bhy5.generatorName.equalsIgnoreCase(string)) {
                return bhy5;
            }
        }
        return null;
    }
    
    public int getId() {
        return this.id;
    }
    
    public boolean hasHelpText() {
        return this.hasHelpText;
    }
    
    private LevelType setHasHelpText() {
        this.hasHelpText = true;
        return this;
    }
    
    static {
        LEVEL_TYPES = new LevelType[16];
        NORMAL = new LevelType(0, "default", 1).setHasReplacement();
        FLAT = new LevelType(1, "flat").setCustomOptions(true);
        LARGE_BIOMES = new LevelType(2, "largeBiomes");
        AMPLIFIED = new LevelType(3, "amplified").setHasHelpText();
        CUSTOMIZED = new LevelType(4, "customized", "normal", 0).setCustomOptions(true).setSelectableByUser(false);
        BUFFET = new LevelType(5, "buffet").setCustomOptions(true);
        DEBUG_ALL_BLOCK_STATES = new LevelType(6, "debug_all_block_states");
        NORMAL_1_1 = new LevelType(8, "default_1_1", 0).setSelectableByUser(false);
    }
}
