package net.minecraft.world.scores;

import javax.annotation.Nullable;
import java.util.Comparator;

public class Score {
    public static final Comparator<Score> SCORE_COMPARATOR;
    private final Scoreboard scoreboard;
    @Nullable
    private final Objective objective;
    private final String owner;
    private int count;
    private boolean locked;
    private boolean forceUpdate;
    
    public Score(final Scoreboard cti, final Objective ctf, final String string) {
        this.scoreboard = cti;
        this.objective = ctf;
        this.owner = string;
        this.locked = true;
        this.forceUpdate = true;
    }
    
    public void add(final int integer) {
        if (this.objective.getCriteria().isReadOnly()) {
            throw new IllegalStateException("Cannot modify read-only score");
        }
        this.setScore(this.getScore() + integer);
    }
    
    public void increment() {
        this.add(1);
    }
    
    public int getScore() {
        return this.count;
    }
    
    public void reset() {
        this.setScore(0);
    }
    
    public void setScore(final int integer) {
        final int integer2 = this.count;
        this.count = integer;
        if (integer2 != integer || this.forceUpdate) {
            this.forceUpdate = false;
            this.getScoreboard().onScoreChanged(this);
        }
    }
    
    @Nullable
    public Objective getObjective() {
        return this.objective;
    }
    
    public String getOwner() {
        return this.owner;
    }
    
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }
    
    public boolean isLocked() {
        return this.locked;
    }
    
    public void setLocked(final boolean boolean1) {
        this.locked = boolean1;
    }
    
    static {
        SCORE_COMPARATOR = ((cth1, cth2) -> {
            if (cth1.getScore() > cth2.getScore()) {
                return 1;
            }
            if (cth1.getScore() < cth2.getScore()) {
                return -1;
            }
            return cth2.getOwner().compareToIgnoreCase(cth1.getOwner());
        });
    }
}
