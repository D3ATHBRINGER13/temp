package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.components.AbstractWidget;
import java.util.Iterator;
import java.util.Collection;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.network.chat.Component;

public class AlertScreen extends Screen {
    private final Runnable callback;
    protected final Component text;
    private final List<String> lines;
    protected final String okButton;
    private int delayTicker;
    
    public AlertScreen(final Runnable runnable, final Component jo2, final Component jo3) {
        this(runnable, jo2, jo3, "gui.back");
    }
    
    public AlertScreen(final Runnable runnable, final Component jo2, final Component jo3, final String string) {
        super(jo2);
        this.lines = (List<String>)Lists.newArrayList();
        this.callback = runnable;
        this.text = jo3;
        this.okButton = I18n.get(string);
    }
    
    @Override
    protected void init() {
        super.init();
        this.<Button>addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, this.okButton, czi -> this.callback.run()));
        this.lines.clear();
        this.lines.addAll((Collection)this.font.split(this.text.getColoredString(), this.width - 50));
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 70, 16777215);
        int integer3 = 90;
        for (final String string7 : this.lines) {
            this.drawCenteredString(this.font, string7, this.width / 2, integer3, 16777215);
            final int n = integer3;
            this.font.getClass();
            integer3 = n + 9;
        }
        super.render(integer1, integer2, float3);
    }
    
    @Override
    public void tick() {
        super.tick();
        final int delayTicker = this.delayTicker - 1;
        this.delayTicker = delayTicker;
        if (delayTicker == 0) {
            for (final AbstractWidget czg3 : this.buttons) {
                czg3.active = true;
            }
        }
    }
}
