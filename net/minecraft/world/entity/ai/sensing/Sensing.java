package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.Lists;
import net.minecraft.world.entity.Entity;
import java.util.List;
import net.minecraft.world.entity.Mob;

public class Sensing {
    private final Mob mob;
    private final List<Entity> seen;
    private final List<Entity> unseen;
    
    public Sensing(final Mob aiy) {
        this.seen = (List<Entity>)Lists.newArrayList();
        this.unseen = (List<Entity>)Lists.newArrayList();
        this.mob = aiy;
    }
    
    public void tick() {
        this.seen.clear();
        this.unseen.clear();
    }
    
    public boolean canSee(final Entity aio) {
        if (this.seen.contains(aio)) {
            return true;
        }
        if (this.unseen.contains(aio)) {
            return false;
        }
        this.mob.level.getProfiler().push("canSee");
        final boolean boolean3 = this.mob.canSee(aio);
        this.mob.level.getProfiler().pop();
        if (boolean3) {
            this.seen.add(aio);
        }
        else {
            this.unseen.add(aio);
        }
        return boolean3;
    }
}
