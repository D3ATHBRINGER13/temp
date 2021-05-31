package net.minecraft.client.gui.components;

import net.minecraft.client.gui.components.events.GuiEventListener;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Options;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Option;
import net.minecraft.client.Minecraft;

public class OptionsList extends ContainerObjectSelectionList<Entry> {
    public OptionsList(final Minecraft cyc, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        super(cyc, integer2, integer3, integer4, integer5, integer6);
        this.centerListVertically = false;
    }
    
    public int addBig(final Option cyf) {
        return this.addEntry(Entry.big(this.minecraft.options, this.width, cyf));
    }
    
    public void addSmall(final Option cyf1, @Nullable final Option cyf2) {
        this.addEntry(Entry.small(this.minecraft.options, this.width, cyf1, cyf2));
    }
    
    public void addSmall(final Option[] arr) {
        for (int integer3 = 0; integer3 < arr.length; integer3 += 2) {
            this.addSmall(arr[integer3], (integer3 < arr.length - 1) ? arr[integer3 + 1] : null);
        }
    }
    
    @Override
    public int getRowWidth() {
        return 400;
    }
    
    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 32;
    }
    
    public static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
        private final List<AbstractWidget> children;
        
        private Entry(final List<AbstractWidget> list) {
            this.children = list;
        }
        
        public static Entry big(final Options cyg, final int integer, final Option cyf) {
            return new Entry((List<AbstractWidget>)ImmutableList.of(cyf.createButton(cyg, integer / 2 - 155, 0, 310)));
        }
        
        public static Entry small(final Options cyg, final int integer, final Option cyf3, @Nullable final Option cyf4) {
            final AbstractWidget czg5 = cyf3.createButton(cyg, integer / 2 - 155, 0, 150);
            if (cyf4 == null) {
                return new Entry((List<AbstractWidget>)ImmutableList.of(czg5));
            }
            return new Entry((List<AbstractWidget>)ImmutableList.of(czg5, cyf4.createButton(cyg, integer / 2 - 155 + 160, 0, 150)));
        }
        
        @Override
        public void render(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final boolean boolean8, final float float9) {
            this.children.forEach(czg -> {
                czg.y = integer2;
                czg.render(integer6, integer7, float9);
            });
        }
        
        @Override
        public List<? extends GuiEventListener> children() {
            return this.children;
        }
    }
}
