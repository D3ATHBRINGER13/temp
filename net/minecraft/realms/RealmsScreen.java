package net.minecraft.realms;

import net.minecraft.client.gui.components.events.GuiEventListener;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.resources.language.I18n;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.Minecraft;

public abstract class RealmsScreen extends RealmsGuiEventListener implements RealmsConfirmResultListener {
    public static final int SKIN_HEAD_U = 8;
    public static final int SKIN_HEAD_V = 8;
    public static final int SKIN_HEAD_WIDTH = 8;
    public static final int SKIN_HEAD_HEIGHT = 8;
    public static final int SKIN_HAT_U = 40;
    public static final int SKIN_HAT_V = 8;
    public static final int SKIN_HAT_WIDTH = 8;
    public static final int SKIN_HAT_HEIGHT = 8;
    public static final int SKIN_TEX_WIDTH = 64;
    public static final int SKIN_TEX_HEIGHT = 64;
    private Minecraft minecraft;
    public int width;
    public int height;
    private final RealmsScreenProxy proxy;
    
    public RealmsScreen() {
        this.proxy = new RealmsScreenProxy(this);
    }
    
    @Override
    public RealmsScreenProxy getProxy() {
        return this.proxy;
    }
    
    public void init() {
    }
    
    public void init(final Minecraft cyc, final int integer2, final int integer3) {
        this.minecraft = cyc;
    }
    
    public void drawCenteredString(final String string, final int integer2, final int integer3, final int integer4) {
        this.proxy.drawCenteredString(string, integer2, integer3, integer4);
    }
    
    public int draw(final String string, final int integer2, final int integer3, final int integer4, final boolean boolean5) {
        return this.proxy.draw(string, integer2, integer3, integer4, boolean5);
    }
    
    public void drawString(final String string, final int integer2, final int integer3, final int integer4) {
        this.drawString(string, integer2, integer3, integer4, true);
    }
    
    public void drawString(final String string, final int integer2, final int integer3, final int integer4, final boolean boolean5) {
        this.proxy.drawString(string, integer2, integer3, integer4, false);
    }
    
    public void blit(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        this.proxy.blit(integer1, integer2, integer3, integer4, integer5, integer6);
    }
    
    public static void blit(final int integer1, final int integer2, final float float3, final float float4, final int integer5, final int integer6, final int integer7, final int integer8, final int integer9, final int integer10) {
        GuiComponent.blit(integer1, integer2, integer7, integer8, float3, float4, integer5, integer6, integer9, integer10);
    }
    
    public static void blit(final int integer1, final int integer2, final float float3, final float float4, final int integer5, final int integer6, final int integer7, final int integer8) {
        GuiComponent.blit(integer1, integer2, float3, float4, integer5, integer6, integer7, integer8);
    }
    
    public void fillGradient(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        this.proxy.fillGradient(integer1, integer2, integer3, integer4, integer5, integer6);
    }
    
    public void renderBackground() {
        this.proxy.renderBackground();
    }
    
    public boolean isPauseScreen() {
        return this.proxy.isPauseScreen();
    }
    
    public void renderBackground(final int integer) {
        this.proxy.renderBackground(integer);
    }
    
    public void render(final int integer1, final int integer2, final float float3) {
        for (int integer3 = 0; integer3 < this.proxy.buttons().size(); ++integer3) {
            ((AbstractRealmsButton)this.proxy.buttons().get(integer3)).render(integer1, integer2, float3);
        }
    }
    
    public void renderTooltip(final ItemStack bcj, final int integer2, final int integer3) {
        this.proxy.renderTooltip(bcj, integer2, integer3);
    }
    
    public void renderTooltip(final String string, final int integer2, final int integer3) {
        this.proxy.renderTooltip(string, integer2, integer3);
    }
    
    public void renderTooltip(final List<String> list, final int integer2, final int integer3) {
        this.proxy.renderTooltip(list, integer2, integer3);
    }
    
    public static void bind(final String string) {
        Realms.bind(string);
    }
    
    public void tick() {
        this.tickButtons();
    }
    
    protected void tickButtons() {
        for (final AbstractRealmsButton<?> abstractRealmsButton3 : this.buttons()) {
            abstractRealmsButton3.tick();
        }
    }
    
    public int width() {
        return this.proxy.width;
    }
    
    public int height() {
        return this.proxy.height;
    }
    
    public int fontLineHeight() {
        return this.proxy.fontLineHeight();
    }
    
    public int fontWidth(final String string) {
        return this.proxy.fontWidth(string);
    }
    
    public void fontDrawShadow(final String string, final int integer2, final int integer3, final int integer4) {
        this.proxy.fontDrawShadow(string, integer2, integer3, integer4);
    }
    
    public List<String> fontSplit(final String string, final int integer) {
        return this.proxy.fontSplit(string, integer);
    }
    
    public void childrenClear() {
        this.proxy.childrenClear();
    }
    
    public void addWidget(final RealmsGuiEventListener realmsGuiEventListener) {
        this.proxy.addWidget(realmsGuiEventListener);
    }
    
    public void removeWidget(final RealmsGuiEventListener realmsGuiEventListener) {
        this.proxy.removeWidget(realmsGuiEventListener);
    }
    
    public boolean hasWidget(final RealmsGuiEventListener realmsGuiEventListener) {
        return this.proxy.hasWidget(realmsGuiEventListener);
    }
    
    public void buttonsAdd(final AbstractRealmsButton<?> abstractRealmsButton) {
        this.proxy.buttonsAdd(abstractRealmsButton);
    }
    
    public List<AbstractRealmsButton<?>> buttons() {
        return this.proxy.buttons();
    }
    
    protected void buttonsClear() {
        this.proxy.buttonsClear();
    }
    
    protected void focusOn(final RealmsGuiEventListener realmsGuiEventListener) {
        this.proxy.magicalSpecialHackyFocus(realmsGuiEventListener.getProxy());
    }
    
    public RealmsEditBox newEditBox(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5) {
        return this.newEditBox(integer1, integer2, integer3, integer4, integer5, "");
    }
    
    public RealmsEditBox newEditBox(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final String string) {
        return new RealmsEditBox(integer1, integer2, integer3, integer4, integer5, string);
    }
    
    @Override
    public void confirmResult(final boolean boolean1, final int integer) {
    }
    
    public static String getLocalizedString(final String string) {
        return Realms.getLocalizedString(string);
    }
    
    public static String getLocalizedString(final String string, final Object... arr) {
        return Realms.getLocalizedString(string, arr);
    }
    
    public List<String> getLocalizedStringWithLineWidth(final String string, final int integer) {
        return this.minecraft.font.split(I18n.get(string), integer);
    }
    
    public RealmsAnvilLevelStorageSource getLevelStorageSource() {
        return new RealmsAnvilLevelStorageSource(Minecraft.getInstance().getLevelSource());
    }
    
    public void removed() {
    }
    
    protected void removeButton(final RealmsButton realmsButton) {
        this.proxy.removeButton(realmsButton);
    }
    
    protected void setKeyboardHandlerSendRepeatsToGui(final boolean boolean1) {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(boolean1);
    }
    
    protected boolean isKeyDown(final int integer) {
        return InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), integer);
    }
    
    protected void narrateLabels() {
        this.getProxy().narrateLabels();
    }
    
    public boolean isFocused(final RealmsGuiEventListener realmsGuiEventListener) {
        return this.getProxy().getFocused() == realmsGuiEventListener.getProxy();
    }
}
