package net.minecraft.client.gui.screens;

import java.util.Objects;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.util.ProgressListener;

public class ProgressScreen extends Screen implements ProgressListener {
    private String title;
    private String stage;
    private int progress;
    private boolean stop;
    
    public ProgressScreen() {
        super(NarratorChatListener.NO_TITLE);
        this.title = "";
        this.stage = "";
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
    
    @Override
    public void progressStartNoAbort(final Component jo) {
        this.progressStart(jo);
    }
    
    @Override
    public void progressStart(final Component jo) {
        this.title = jo.getColoredString();
        this.progressStage(new TranslatableComponent("progress.working", new Object[0]));
    }
    
    @Override
    public void progressStage(final Component jo) {
        this.stage = jo.getColoredString();
        this.progressStagePercentage(0);
    }
    
    @Override
    public void progressStagePercentage(final int integer) {
        this.progress = integer;
    }
    
    @Override
    public void stop() {
        this.stop = true;
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        if (this.stop) {
            if (!this.minecraft.isConnectedToRealms()) {
                this.minecraft.setScreen(null);
            }
            return;
        }
        this.renderBackground();
        this.drawCenteredString(this.font, this.title, this.width / 2, 70, 16777215);
        if (!Objects.equals(this.stage, "") && this.progress != 0) {
            this.drawCenteredString(this.font, this.stage + " " + this.progress + "%", this.width / 2, 90, 16777215);
        }
        super.render(integer1, integer2, float3);
    }
}
