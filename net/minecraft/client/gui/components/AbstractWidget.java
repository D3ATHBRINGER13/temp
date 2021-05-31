package net.minecraft.client.gui.components;

import java.util.Objects;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.gui.Font;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.GuiComponent;

public abstract class AbstractWidget extends GuiComponent implements Widget, GuiEventListener {
    public static final ResourceLocation WIDGETS_LOCATION;
    private static final int NARRATE_DELAY_MOUSE = 750;
    private static final int NARRATE_DELAY_FOCUS = 200;
    protected int width;
    protected int height;
    public int x;
    public int y;
    private String message;
    private boolean wasHovered;
    protected boolean isHovered;
    public boolean active;
    public boolean visible;
    protected float alpha;
    protected long nextNarration;
    private boolean focused;
    
    public AbstractWidget(final int integer1, final int integer2, final String string) {
        this(integer1, integer2, 200, 20, string);
    }
    
    public AbstractWidget(final int integer1, final int integer2, final int integer3, final int integer4, final String string) {
        this.active = true;
        this.visible = true;
        this.alpha = 1.0f;
        this.nextNarration = Long.MAX_VALUE;
        this.x = integer1;
        this.y = integer2;
        this.width = integer3;
        this.height = integer4;
        this.message = string;
    }
    
    protected int getYImage(final boolean boolean1) {
        int integer3 = 1;
        if (!this.active) {
            integer3 = 0;
        }
        else if (boolean1) {
            integer3 = 2;
        }
        return integer3;
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        if (!this.visible) {
            return;
        }
        this.isHovered = (integer1 >= this.x && integer2 >= this.y && integer1 < this.x + this.width && integer2 < this.y + this.height);
        if (this.wasHovered != this.isHovered()) {
            if (this.isHovered()) {
                if (this.focused) {
                    this.nextNarration = Util.getMillis() + 200L;
                }
                else {
                    this.nextNarration = Util.getMillis() + 750L;
                }
            }
            else {
                this.nextNarration = Long.MAX_VALUE;
            }
        }
        if (this.visible) {
            this.renderButton(integer1, integer2, float3);
        }
        this.narrate();
        this.wasHovered = this.isHovered();
    }
    
    protected void narrate() {
        if (this.active && this.isHovered() && Util.getMillis() > this.nextNarration) {
            final String string2 = this.getNarrationMessage();
            if (!string2.isEmpty()) {
                NarratorChatListener.INSTANCE.sayNow(string2);
                this.nextNarration = Long.MAX_VALUE;
            }
        }
    }
    
    protected String getNarrationMessage() {
        if (this.message.isEmpty()) {
            return "";
        }
        return I18n.get("gui.narrate.button", this.getMessage());
    }
    
    public void renderButton(final int integer1, final int integer2, final float float3) {
        final Minecraft cyc5 = Minecraft.getInstance();
        final Font cyu6 = cyc5.font;
        cyc5.getTextureManager().bind(AbstractWidget.WIDGETS_LOCATION);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, this.alpha);
        final int integer3 = this.getYImage(this.isHovered());
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        this.blit(this.x, this.y, 0, 46 + integer3 * 20, this.width / 2, this.height);
        this.blit(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + integer3 * 20, this.width / 2, this.height);
        this.renderBg(cyc5, integer1, integer2);
        int integer4 = 14737632;
        if (!this.active) {
            integer4 = 10526880;
        }
        else if (this.isHovered()) {
            integer4 = 16777120;
        }
        this.drawCenteredString(cyu6, this.message, this.x + this.width / 2, this.y + (this.height - 8) / 2, integer4 | Mth.ceil(this.alpha * 255.0f) << 24);
    }
    
    protected void renderBg(final Minecraft cyc, final int integer2, final int integer3) {
    }
    
    public void onClick(final double double1, final double double2) {
    }
    
    public void onRelease(final double double1, final double double2) {
    }
    
    protected void onDrag(final double double1, final double double2, final double double3, final double double4) {
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        if (!this.active || !this.visible) {
            return false;
        }
        if (this.isValidClickButton(integer)) {
            final boolean boolean7 = this.clicked(double1, double2);
            if (boolean7) {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                this.onClick(double1, double2);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean mouseReleased(final double double1, final double double2, final int integer) {
        if (this.isValidClickButton(integer)) {
            this.onRelease(double1, double2);
            return true;
        }
        return false;
    }
    
    protected boolean isValidClickButton(final int integer) {
        return integer == 0;
    }
    
    @Override
    public boolean mouseDragged(final double double1, final double double2, final int integer, final double double4, final double double5) {
        if (this.isValidClickButton(integer)) {
            this.onDrag(double1, double2, double4, double5);
            return true;
        }
        return false;
    }
    
    protected boolean clicked(final double double1, final double double2) {
        return this.active && this.visible && double1 >= this.x && double2 >= this.y && double1 < this.x + this.width && double2 < this.y + this.height;
    }
    
    public boolean isHovered() {
        return this.isHovered || this.focused;
    }
    
    @Override
    public boolean changeFocus(final boolean boolean1) {
        if (!this.active || !this.visible) {
            return false;
        }
        this.onFocusedChanged(this.focused = !this.focused);
        return this.focused;
    }
    
    protected void onFocusedChanged(final boolean boolean1) {
    }
    
    @Override
    public boolean isMouseOver(final double double1, final double double2) {
        return this.active && this.visible && double1 >= this.x && double2 >= this.y && double1 < this.x + this.width && double2 < this.y + this.height;
    }
    
    public void renderToolTip(final int integer1, final int integer2) {
    }
    
    public void playDownSound(final SoundManager eap) {
        eap.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public void setWidth(final int integer) {
        this.width = integer;
    }
    
    public void setAlpha(final float float1) {
        this.alpha = float1;
    }
    
    public void setMessage(final String string) {
        if (!Objects.equals(string, this.message)) {
            this.nextNarration = Util.getMillis() + 250L;
        }
        this.message = string;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public boolean isFocused() {
        return this.focused;
    }
    
    protected void setFocused(final boolean boolean1) {
        this.focused = boolean1;
    }
    
    static {
        WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
    }
}
