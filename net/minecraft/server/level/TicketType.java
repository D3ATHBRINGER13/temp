package net.minecraft.server.level;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.util.Unit;
import java.util.Comparator;

public class TicketType<T> {
    private final String name;
    private final Comparator<T> comparator;
    private final long timeout;
    public static final TicketType<Unit> START;
    public static final TicketType<Unit> DRAGON;
    public static final TicketType<ChunkPos> PLAYER;
    public static final TicketType<ChunkPos> FORCED;
    public static final TicketType<ChunkPos> LIGHT;
    public static final TicketType<ColumnPos> PORTAL;
    public static final TicketType<Integer> POST_TELEPORT;
    public static final TicketType<ChunkPos> UNKNOWN;
    
    public static <T> TicketType<T> create(final String string, final Comparator<T> comparator) {
        return new TicketType<T>(string, comparator, 0L);
    }
    
    public static <T> TicketType<T> create(final String string, final Comparator<T> comparator, final int integer) {
        return new TicketType<T>(string, comparator, integer);
    }
    
    protected TicketType(final String string, final Comparator<T> comparator, final long long3) {
        this.name = string;
        this.comparator = comparator;
        this.timeout = long3;
    }
    
    public String toString() {
        return this.name;
    }
    
    public Comparator<T> getComparator() {
        return this.comparator;
    }
    
    public long timeout() {
        return this.timeout;
    }
    
    static {
        START = TicketType.<Unit>create("start", (java.util.Comparator<Unit>)((aag1, aag2) -> 0));
        DRAGON = TicketType.<Unit>create("dragon", (java.util.Comparator<Unit>)((aag1, aag2) -> 0));
        PLAYER = TicketType.<ChunkPos>create("player", (java.util.Comparator<ChunkPos>)Comparator.comparingLong(ChunkPos::toLong));
        FORCED = TicketType.<ChunkPos>create("forced", (java.util.Comparator<ChunkPos>)Comparator.comparingLong(ChunkPos::toLong));
        LIGHT = TicketType.<ChunkPos>create("light", (java.util.Comparator<ChunkPos>)Comparator.comparingLong(ChunkPos::toLong));
        PORTAL = TicketType.<ColumnPos>create("portal", (java.util.Comparator<ColumnPos>)Comparator.comparingLong(ColumnPos::toLong));
        POST_TELEPORT = TicketType.<Integer>create("post_teleport", (java.util.Comparator<Integer>)Integer::compareTo, 5);
        UNKNOWN = TicketType.<ChunkPos>create("unknown", (java.util.Comparator<ChunkPos>)Comparator.comparingLong(ChunkPos::toLong), 1);
    }
}
