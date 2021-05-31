package net.minecraft.client;

import java.util.Comparator;
import java.util.Arrays;
import net.minecraft.util.Mth;

public enum NarratorStatus {
    OFF(0, "options.narrator.off"), 
    ALL(1, "options.narrator.all"), 
    CHAT(2, "options.narrator.chat"), 
    SYSTEM(3, "options.narrator.system");
    
    private static final NarratorStatus[] BY_ID;
    private final int id;
    private final String key;
    
    private NarratorStatus(final int integer3, final String string4) {
        this.id = integer3;
        this.key = string4;
    }
    
    public int getId() {
        return this.id;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public static NarratorStatus byId(final int integer) {
        return NarratorStatus.BY_ID[Mth.positiveModulo(integer, NarratorStatus.BY_ID.length)];
    }
    
    static {
        BY_ID = (NarratorStatus[])Arrays.stream((Object[])values()).sorted(Comparator.comparingInt(NarratorStatus::getId)).toArray(NarratorStatus[]::new);
    }
}
