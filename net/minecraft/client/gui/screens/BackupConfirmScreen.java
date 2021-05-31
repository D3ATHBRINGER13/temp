package net.minecraft.client.gui.screens;

import java.util.Iterator;
import net.minecraft.client.gui.components.Button;
import java.util.Collection;
import net.minecraft.client.resources.language.I18n;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.components.Checkbox;
import java.util.List;
import net.minecraft.network.chat.Component;

public class BackupConfirmScreen extends Screen {
    private final Screen lastScreen;
    protected final Listener listener;
    private final Component description;
    private final boolean promptForCacheErase;
    private final List<String> lines;
    private final String eraseCacheText;
    private final String backupButton;
    private final String continueButton;
    private final String cancelButton;
    private Checkbox eraseCache;
    
    public BackupConfirmScreen(final Screen dcl, final Listener a, final Component jo3, final Component jo4, final boolean boolean5) {
        super(jo3);
        this.lines = (List<String>)Lists.newArrayList();
        this.lastScreen = dcl;
        this.listener = a;
        this.description = jo4;
        this.promptForCacheErase = boolean5;
        this.eraseCacheText = I18n.get("selectWorld.backupEraseCache");
        this.backupButton = I18n.get("selectWorld.backupJoinConfirmButton");
        this.continueButton = I18n.get("selectWorld.backupJoinSkipButton");
        this.cancelButton = I18n.get("gui.cancel");
    }
    
    @Override
    protected void init() {
        super.init();
        this.lines.clear();
        this.lines.addAll((Collection)this.font.split(this.description.getColoredString(), this.width - 50));
        final int n = this.lines.size() + 1;
        this.font.getClass();
        final int integer2 = n * 9;
        this.<Button>addButton(new Button(this.width / 2 - 155, 100 + integer2, 150, 20, this.backupButton, czi -> this.listener.proceed(true, this.eraseCache.selected())));
        this.<Button>addButton(new Button(this.width / 2 - 155 + 160, 100 + integer2, 150, 20, this.continueButton, czi -> this.listener.proceed(false, this.eraseCache.selected())));
        this.<Button>addButton(new Button(this.width / 2 - 155 + 80, 124 + integer2, 150, 20, this.cancelButton, czi -> this.minecraft.setScreen(this.lastScreen)));
        this.eraseCache = new Checkbox(this.width / 2 - 155 + 80, 76 + integer2, 150, 20, this.eraseCacheText, false);
        if (this.promptForCacheErase) {
            this.<Checkbox>addButton(this.eraseCache);
        }
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 50, 16777215);
        int integer3 = 70;
        for (final String string7 : this.lines) {
            this.drawCenteredString(this.font, string7, this.width / 2, integer3, 16777215);
            final int n = integer3;
            this.font.getClass();
            integer3 = n + 9;
        }
        super.render(integer1, integer2, float3);
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (integer1 == 256) {
            this.minecraft.setScreen(this.lastScreen);
            return true;
        }
        return super.keyPressed(integer1, integer2, integer3);
    }
    
    public interface Listener {
        void proceed(final boolean boolean1, final boolean boolean2);
    }
}
