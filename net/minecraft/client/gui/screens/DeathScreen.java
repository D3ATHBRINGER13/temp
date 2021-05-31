package net.minecraft.client.gui.screens;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.ChatFormatting;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;

public class DeathScreen extends Screen {
    private int delayTicker;
    private final Component causeOfDeath;
    private final boolean hardcore;
    
    public DeathScreen(@Nullable final Component jo, final boolean boolean2) {
        super(new TranslatableComponent(boolean2 ? "deathScreen.title.hardcore" : "deathScreen.title", new Object[0]));
        this.causeOfDeath = jo;
        this.hardcore = boolean2;
    }
    
    @Override
    protected void init() {
        this.delayTicker = 0;
        String string2;
        String string3;
        if (this.hardcore) {
            string2 = I18n.get("deathScreen.spectate");
            string3 = I18n.get(new StringBuilder().append("deathScreen.").append(this.minecraft.isLocalServer() ? "deleteWorld" : "leaveServer").toString());
        }
        else {
            string2 = I18n.get("deathScreen.respawn");
            string3 = I18n.get("deathScreen.titleScreen");
        }
        this.<Button>addButton(new Button(this.width / 2 - 100, this.height / 4 + 72, 200, 20, string2, czi -> {
            this.minecraft.player.respawn();
            this.minecraft.setScreen(null);
            return;
        }));
        final ConfirmScreen confirmScreen;
        ConfirmScreen dbn3;
        final Button czi4 = this.<Button>addButton(new Button(this.width / 2 - 100, this.height / 4 + 96, 200, 20, string3, czi -> {
            if (this.hardcore) {
                this.minecraft.setScreen(new TitleScreen());
                return;
            }
            else {
                new ConfirmScreen(this::confirmResult, new TranslatableComponent("deathScreen.quit.confirm", new Object[0]), new TextComponent(""), I18n.get("deathScreen.titleScreen"), I18n.get("deathScreen.respawn"));
                dbn3 = confirmScreen;
                this.minecraft.setScreen(dbn3);
                dbn3.setDelay(20);
                return;
            }
        }));
        if (!this.hardcore && this.minecraft.getUser() == null) {
            czi4.active = false;
        }
        for (final AbstractWidget czg6 : this.buttons) {
            czg6.active = false;
        }
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
    
    private void confirmResult(final boolean boolean1) {
        if (boolean1) {
            if (this.minecraft.level != null) {
                this.minecraft.level.disconnect();
            }
            this.minecraft.clearLevel(new GenericDirtMessageScreen(new TranslatableComponent("menu.savingLevel", new Object[0])));
            this.minecraft.setScreen(new TitleScreen());
        }
        else {
            this.minecraft.player.respawn();
            this.minecraft.setScreen(null);
        }
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.fillGradient(0, 0, this.width, this.height, 1615855616, -1602211792);
        GlStateManager.pushMatrix();
        GlStateManager.scalef(2.0f, 2.0f, 2.0f);
        this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2 / 2, 30, 16777215);
        GlStateManager.popMatrix();
        if (this.causeOfDeath != null) {
            this.drawCenteredString(this.font, this.causeOfDeath.getColoredString(), this.width / 2, 85, 16777215);
        }
        this.drawCenteredString(this.font, I18n.get("deathScreen.score") + ": " + ChatFormatting.YELLOW + this.minecraft.player.getScore(), this.width / 2, 100, 16777215);
        if (this.causeOfDeath != null && integer2 > 85) {
            final int n = 85;
            this.font.getClass();
            if (integer2 < n + 9) {
                final Component jo5 = this.getClickedComponentAt(integer1);
                if (jo5 != null && jo5.getStyle().getHoverEvent() != null) {
                    this.renderComponentHoverEffect(jo5, integer1, integer2);
                }
            }
        }
        super.render(integer1, integer2, float3);
    }
    
    @Nullable
    public Component getClickedComponentAt(final int integer) {
        if (this.causeOfDeath == null) {
            return null;
        }
        final int integer2 = this.minecraft.font.width(this.causeOfDeath.getColoredString());
        final int integer3 = this.width / 2 - integer2 / 2;
        final int integer4 = this.width / 2 + integer2 / 2;
        int integer5 = integer3;
        if (integer < integer3 || integer > integer4) {
            return null;
        }
        for (final Component jo8 : this.causeOfDeath) {
            integer5 += this.minecraft.font.width(ComponentRenderUtils.stripColor(jo8.getContents(), false));
            if (integer5 > integer) {
                return jo8;
            }
        }
        return null;
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        if (this.causeOfDeath != null && double2 > 85.0) {
            final int n = 85;
            this.font.getClass();
            if (double2 < n + 9) {
                final Component jo7 = this.getClickedComponentAt((int)double1);
                if (jo7 != null && jo7.getStyle().getClickEvent() != null && jo7.getStyle().getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
                    this.handleComponentClicked(jo7);
                    return false;
                }
            }
        }
        return super.mouseClicked(double1, double2, integer);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    @Override
    public void tick() {
        super.tick();
        ++this.delayTicker;
        if (this.delayTicker == 20) {
            for (final AbstractWidget czg3 : this.buttons) {
                czg3.active = true;
            }
        }
    }
}
