package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.components.AbstractWidget;
import java.util.Iterator;
import java.util.Collection;
import net.minecraft.client.gui.components.Button;
import com.google.common.collect.Lists;
import net.minecraft.client.resources.language.I18n;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import net.minecraft.network.chat.Component;

public class ConfirmScreen extends Screen {
    private final Component title2;
    private final List<String> lines;
    protected String yesButton;
    protected String noButton;
    private int delayTicker;
    protected final BooleanConsumer callback;
    
    public ConfirmScreen(final BooleanConsumer booleanConsumer, final Component jo2, final Component jo3) {
        this(booleanConsumer, jo2, jo3, I18n.get("gui.yes"), I18n.get("gui.no"));
    }
    
    public ConfirmScreen(final BooleanConsumer booleanConsumer, final Component jo2, final Component jo3, final String string4, final String string5) {
        super(jo2);
        this.lines = (List<String>)Lists.newArrayList();
        this.callback = booleanConsumer;
        this.title2 = jo3;
        this.yesButton = string4;
        this.noButton = string5;
    }
    
    @Override
    public String getNarrationMessage() {
        return super.getNarrationMessage() + ". " + this.title2.getString();
    }
    
    @Override
    protected void init() {
        super.init();
        this.<Button>addButton(new Button(this.width / 2 - 155, this.height / 6 + 96, 150, 20, this.yesButton, czi -> this.callback.accept(true)));
        this.<Button>addButton(new Button(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20, this.noButton, czi -> this.callback.accept(false)));
        this.lines.clear();
        this.lines.addAll((Collection)this.font.split(this.title2.getColoredString(), this.width - 50));
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
    
    public void setDelay(final int integer) {
        this.delayTicker = integer;
        for (final AbstractWidget czg4 : this.buttons) {
            czg4.active = false;
        }
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
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (integer1 == 256) {
            this.callback.accept(false);
            return true;
        }
        return super.keyPressed(integer1, integer2, integer3);
    }
}
