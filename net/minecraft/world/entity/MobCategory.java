package net.minecraft.world.entity;

import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Map;

public enum MobCategory {
    MONSTER("monster", 70, false, false), 
    CREATURE("creature", 10, true, true), 
    AMBIENT("ambient", 15, true, false), 
    WATER_CREATURE("water_creature", 15, true, false), 
    MISC("misc", 15, true, false);
    
    private static final Map<String, MobCategory> BY_NAME;
    private final int max;
    private final boolean isFriendly;
    private final boolean isPersistent;
    private final String name;
    
    private MobCategory(final String string3, final int integer4, final boolean boolean5, final boolean boolean6) {
        this.name = string3;
        this.max = integer4;
        this.isFriendly = boolean5;
        this.isPersistent = boolean6;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getMaxInstancesPerChunk() {
        return this.max;
    }
    
    public boolean isFriendly() {
        return this.isFriendly;
    }
    
    public boolean isPersistent() {
        return this.isPersistent;
    }
    
    static {
        BY_NAME = (Map)Arrays.stream((Object[])values()).collect(Collectors.toMap(MobCategory::getName, aiz -> aiz));
    }
}
