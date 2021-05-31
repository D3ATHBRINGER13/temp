package net.minecraft.world.entity.schedule;

import java.util.stream.Collectors;
import java.util.Set;
import com.google.common.collect.Lists;
import java.util.List;

public class ScheduleBuilder {
    private final Schedule schedule;
    private final List<ActivityTransition> transitions;
    
    public ScheduleBuilder(final Schedule axq) {
        this.transitions = (List<ActivityTransition>)Lists.newArrayList();
        this.schedule = axq;
    }
    
    public ScheduleBuilder changeActivityAt(final int integer, final Activity axo) {
        this.transitions.add(new ActivityTransition(integer, axo));
        return this;
    }
    
    public Schedule build() {
        ((Set)this.transitions.stream().map(ActivityTransition::getActivity).collect(Collectors.toSet())).forEach(this.schedule::ensureTimelineExistsFor);
        this.transitions.forEach(a -> {
            final Activity axo3 = a.getActivity();
            this.schedule.getAllTimelinesExceptFor(axo3).forEach(axs -> axs.addKeyframe(a.getTime(), 0.0f));
            this.schedule.getTimelineFor(axo3).addKeyframe(a.getTime(), 1.0f);
        });
        return this.schedule;
    }
    
    static class ActivityTransition {
        private final int time;
        private final Activity activity;
        
        public ActivityTransition(final int integer, final Activity axo) {
            this.time = integer;
            this.activity = axo;
        }
        
        public int getTime() {
            return this.time;
        }
        
        public Activity getActivity() {
            return this.activity;
        }
    }
}
