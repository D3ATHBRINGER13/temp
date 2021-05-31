package net.minecraft.server.level;

import java.util.Objects;

public final class Ticket<T> implements Comparable<Ticket<?>> {
    private final TicketType<T> type;
    private final int ticketLevel;
    private final T key;
    private final long createdTick;
    
    protected Ticket(final TicketType<T> vp, final int integer, final T object, final long long4) {
        this.type = vp;
        this.ticketLevel = integer;
        this.key = object;
        this.createdTick = long4;
    }
    
    public int compareTo(final Ticket<?> vo) {
        final int integer3 = Integer.compare(this.ticketLevel, vo.ticketLevel);
        if (integer3 != 0) {
            return integer3;
        }
        final int integer4 = Integer.compare(System.identityHashCode(this.type), System.identityHashCode(vo.type));
        if (integer4 != 0) {
            return integer4;
        }
        return this.type.getComparator().compare(this.key, vo.key);
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Ticket)) {
            return false;
        }
        final Ticket<?> vo3 = object;
        return this.ticketLevel == vo3.ticketLevel && Objects.equals(this.type, vo3.type) && Objects.equals(this.key, vo3.key);
    }
    
    public int hashCode() {
        return Objects.hash(new Object[] { this.type, this.ticketLevel, this.key });
    }
    
    public String toString() {
        return new StringBuilder().append("Ticket[").append(this.type).append(" ").append(this.ticketLevel).append(" (").append(this.key).append(")] at ").append(this.createdTick).toString();
    }
    
    public TicketType<T> getType() {
        return this.type;
    }
    
    public int getTicketLevel() {
        return this.ticketLevel;
    }
    
    public boolean timedOut(final long long1) {
        final long long2 = this.type.timeout();
        return long2 != 0L && long1 - this.createdTick > long2;
    }
}
