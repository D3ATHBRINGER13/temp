package net.minecraft.client.renderer.chunk;

import java.util.Iterator;
import net.minecraft.core.Direction;
import java.util.Set;
import java.util.BitSet;

public class VisibilitySet {
    private static final int FACINGS;
    private final BitSet data;
    
    public VisibilitySet() {
        this.data = new BitSet(VisibilitySet.FACINGS * VisibilitySet.FACINGS);
    }
    
    public void add(final Set<Direction> set) {
        for (final Direction fb4 : set) {
            for (final Direction fb5 : set) {
                this.set(fb4, fb5, true);
            }
        }
    }
    
    public void set(final Direction fb1, final Direction fb2, final boolean boolean3) {
        this.data.set(fb1.ordinal() + fb2.ordinal() * VisibilitySet.FACINGS, boolean3);
        this.data.set(fb2.ordinal() + fb1.ordinal() * VisibilitySet.FACINGS, boolean3);
    }
    
    public void setAll(final boolean boolean1) {
        this.data.set(0, this.data.size(), boolean1);
    }
    
    public boolean visibilityBetween(final Direction fb1, final Direction fb2) {
        return this.data.get(fb1.ordinal() + fb2.ordinal() * VisibilitySet.FACINGS);
    }
    
    public String toString() {
        final StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(' ');
        for (final Direction fb6 : Direction.values()) {
            stringBuilder2.append(' ').append(fb6.toString().toUpperCase().charAt(0));
        }
        stringBuilder2.append('\n');
        for (final Direction fb6 : Direction.values()) {
            stringBuilder2.append(fb6.toString().toUpperCase().charAt(0));
            for (final Direction fb7 : Direction.values()) {
                if (fb6 == fb7) {
                    stringBuilder2.append("  ");
                }
                else {
                    final boolean boolean11 = this.visibilityBetween(fb6, fb7);
                    stringBuilder2.append(' ').append(boolean11 ? 'Y' : 'n');
                }
            }
            stringBuilder2.append('\n');
        }
        return stringBuilder2.toString();
    }
    
    static {
        FACINGS = Direction.values().length;
    }
}
