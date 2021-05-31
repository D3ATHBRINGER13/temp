package net.minecraft.world.entity.schedule;

import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.List;
import net.minecraft.core.Registry;
import com.google.common.collect.Maps;
import java.util.Map;

public class Schedule {
    public static final Schedule EMPTY;
    public static final Schedule SIMPLE;
    public static final Schedule VILLAGER_BABY;
    public static final Schedule VILLAGER_DEFAULT;
    private final Map<Activity, Timeline> timelines;
    
    public Schedule() {
        this.timelines = (Map<Activity, Timeline>)Maps.newHashMap();
    }
    
    protected static ScheduleBuilder register(final String string) {
        final Schedule axq2 = Registry.<Schedule>register(Registry.SCHEDULE, string, new Schedule());
        return new ScheduleBuilder(axq2);
    }
    
    protected void ensureTimelineExistsFor(final Activity axo) {
        if (!this.timelines.containsKey(axo)) {
            this.timelines.put(axo, new Timeline());
        }
    }
    
    protected Timeline getTimelineFor(final Activity axo) {
        return (Timeline)this.timelines.get(axo);
    }
    
    protected List<Timeline> getAllTimelinesExceptFor(final Activity axo) {
        return (List<Timeline>)this.timelines.entrySet().stream().filter(entry -> entry.getKey() != axo).map(Map.Entry::getValue).collect(Collectors.toList());
    }
    
    public Activity getActivityAt(final int integer) {
        return (Activity)this.timelines.entrySet().stream().max(Comparator.comparingDouble(entry -> ((Timeline)entry.getValue()).getValueAt(integer))).map(Map.Entry::getKey).orElse(Activity.IDLE);
    }
    
    static {
        EMPTY = register("empty").changeActivityAt(0, Activity.IDLE).build();
        SIMPLE = register("simple").changeActivityAt(5000, Activity.WORK).changeActivityAt(11000, Activity.REST).build();
        VILLAGER_BABY = register("villager_baby").changeActivityAt(10, Activity.IDLE).changeActivityAt(3000, Activity.PLAY).changeActivityAt(6000, Activity.IDLE).changeActivityAt(10000, Activity.PLAY).changeActivityAt(12000, Activity.REST).build();
        VILLAGER_DEFAULT = register("villager_default").changeActivityAt(10, Activity.IDLE).changeActivityAt(2000, Activity.WORK).changeActivityAt(9000, Activity.MEET).changeActivityAt(11000, Activity.IDLE).changeActivityAt(12000, Activity.REST).build();
    }
}
