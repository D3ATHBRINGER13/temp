package net.minecraft.world.entity.schedule;

import net.minecraft.core.Registry;

public class Activity {
    public static final Activity CORE;
    public static final Activity IDLE;
    public static final Activity WORK;
    public static final Activity PLAY;
    public static final Activity REST;
    public static final Activity MEET;
    public static final Activity PANIC;
    public static final Activity RAID;
    public static final Activity PRE_RAID;
    public static final Activity HIDE;
    private final String name;
    
    private Activity(final String string) {
        this.name = string;
    }
    
    public String getName() {
        return this.name;
    }
    
    private static Activity register(final String string) {
        return Registry.<Activity>register(Registry.ACTIVITY, string, new Activity(string));
    }
    
    public String toString() {
        return this.getName();
    }
    
    static {
        CORE = register("core");
        IDLE = register("idle");
        WORK = register("work");
        PLAY = register("play");
        REST = register("rest");
        MEET = register("meet");
        PANIC = register("panic");
        RAID = register("raid");
        PRE_RAID = register("pre_raid");
        HIDE = register("hide");
    }
}
