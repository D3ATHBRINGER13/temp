package net.minecraft.network.chat;

import java.util.Objects;
import com.google.common.collect.Streams;
import java.util.stream.Stream;
import java.util.Iterator;
import com.google.common.collect.Lists;
import java.util.List;

public abstract class BaseComponent implements Component {
    protected final List<Component> siblings;
    private Style style;
    
    public BaseComponent() {
        this.siblings = (List<Component>)Lists.newArrayList();
    }
    
    public Component append(final Component jo) {
        jo.getStyle().inheritFrom(this.getStyle());
        this.siblings.add(jo);
        return this;
    }
    
    public List<Component> getSiblings() {
        return this.siblings;
    }
    
    public Component setStyle(final Style jw) {
        this.style = jw;
        for (final Component jo4 : this.siblings) {
            jo4.getStyle().inheritFrom(this.getStyle());
        }
        return this;
    }
    
    public Style getStyle() {
        if (this.style == null) {
            this.style = new Style();
            for (final Component jo3 : this.siblings) {
                jo3.getStyle().inheritFrom(this.style);
            }
        }
        return this.style;
    }
    
    public Stream<Component> stream() {
        return (Stream<Component>)Streams.concat(new Stream[] { Stream.of(this), this.siblings.stream().flatMap(Component::stream) });
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof BaseComponent) {
            final BaseComponent jl3 = (BaseComponent)object;
            return this.siblings.equals(jl3.siblings) && this.getStyle().equals(jl3.getStyle());
        }
        return false;
    }
    
    public int hashCode() {
        return Objects.hash(new Object[] { this.getStyle(), this.siblings });
    }
    
    public String toString() {
        return new StringBuilder().append("BaseComponent{style=").append(this.style).append(", siblings=").append(this.siblings).append('}').toString();
    }
}
