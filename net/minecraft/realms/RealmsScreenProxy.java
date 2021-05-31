package net.minecraft.realms;

import org.apache.logging.log4j.LogManager;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.events.GuiEventListener;
import java.util.Set;
import com.google.common.collect.Sets;
import java.util.Iterator;
import net.minecraft.client.gui.components.AbstractWidget;
import com.google.common.collect.Lists;
import java.util.stream.Collectors;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.gui.screens.Screen;

public class RealmsScreenProxy extends Screen {
    private final RealmsScreen screen;
    private static final Logger LOGGER;
    
    public RealmsScreenProxy(final RealmsScreen realmsScreen) {
        super(NarratorChatListener.NO_TITLE);
        this.screen = realmsScreen;
    }
    
    public RealmsScreen getScreen() {
        return this.screen;
    }
    
    @Override
    public void init(final Minecraft cyc, final int integer2, final int integer3) {
        this.screen.init(cyc, integer2, integer3);
        super.init(cyc, integer2, integer3);
    }
    
    public void init() {
        this.screen.init();
        super.init();
    }
    
    public void drawCenteredString(final String string, final int integer2, final int integer3, final int integer4) {
        super.drawCenteredString(this.font, string, integer2, integer3, integer4);
    }
    
    public void drawString(final String string, final int integer2, final int integer3, final int integer4, final boolean boolean5) {
        if (boolean5) {
            super.drawString(this.font, string, integer2, integer3, integer4);
        }
        else {
            this.font.draw(string, (float)integer2, (float)integer3, integer4);
        }
    }
    
    @Override
    public void blit(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        this.screen.blit(integer1, integer2, integer3, integer4, integer5, integer6);
        super.blit(integer1, integer2, integer3, integer4, integer5, integer6);
    }
    
    public static void blit(final int integer1, final int integer2, final float float3, final float float4, final int integer5, final int integer6, final int integer7, final int integer8, final int integer9, final int integer10) {
        GuiComponent.blit(integer1, integer2, integer7, integer8, float3, float4, integer5, integer6, integer9, integer10);
    }
    
    public static void blit(final int integer1, final int integer2, final float float3, final float float4, final int integer5, final int integer6, final int integer7, final int integer8) {
        GuiComponent.blit(integer1, integer2, float3, float4, integer5, integer6, integer7, integer8);
    }
    
    public void fillGradient(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        super.fillGradient(integer1, integer2, integer3, integer4, integer5, integer6);
    }
    
    @Override
    public void renderBackground() {
        super.renderBackground();
    }
    
    @Override
    public boolean isPauseScreen() {
        return super.isPauseScreen();
    }
    
    @Override
    public void renderBackground(final int integer) {
        super.renderBackground(integer);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.screen.render(integer1, integer2, float3);
    }
    
    public void renderTooltip(final ItemStack bcj, final int integer2, final int integer3) {
        super.renderTooltip(bcj, integer2, integer3);
    }
    
    @Override
    public void renderTooltip(final String string, final int integer2, final int integer3) {
        super.renderTooltip(string, integer2, integer3);
    }
    
    @Override
    public void renderTooltip(final List<String> list, final int integer2, final int integer3) {
        super.renderTooltip(list, integer2, integer3);
    }
    
    @Override
    public void tick() {
        this.screen.tick();
        super.tick();
    }
    
    public int width() {
        return this.width;
    }
    
    public int height() {
        return this.height;
    }
    
    public int fontLineHeight() {
        this.font.getClass();
        return 9;
    }
    
    public int fontWidth(final String string) {
        return this.font.width(string);
    }
    
    public void fontDrawShadow(final String string, final int integer2, final int integer3, final int integer4) {
        this.font.drawShadow(string, (float)integer2, (float)integer3, integer4);
    }
    
    public List<String> fontSplit(final String string, final int integer) {
        return this.font.split(string, integer);
    }
    
    public void childrenClear() {
        this.children.clear();
    }
    
    public void addWidget(final RealmsGuiEventListener realmsGuiEventListener) {
        if (this.hasWidget(realmsGuiEventListener) || !this.children.add(realmsGuiEventListener.getProxy())) {
            RealmsScreenProxy.LOGGER.error(new StringBuilder().append("Tried to add the same widget multiple times: ").append(realmsGuiEventListener).toString());
        }
    }
    
    public void narrateLabels() {
        final List<String> list2 = (List<String>)this.children.stream().filter(dae -> dae instanceof RealmsLabelProxy).map(dae -> ((RealmsLabelProxy)dae).getLabel().getText()).collect(Collectors.toList());
        Realms.narrateNow((Iterable<String>)list2);
    }
    
    public void removeWidget(final RealmsGuiEventListener realmsGuiEventListener) {
        if (!this.hasWidget(realmsGuiEventListener) || !this.children.remove(realmsGuiEventListener.getProxy())) {
            RealmsScreenProxy.LOGGER.error(new StringBuilder().append("Tried to add the same widget multiple times: ").append(realmsGuiEventListener).toString());
        }
    }
    
    public boolean hasWidget(final RealmsGuiEventListener realmsGuiEventListener) {
        return this.children.contains(realmsGuiEventListener.getProxy());
    }
    
    public void buttonsAdd(final AbstractRealmsButton<?> abstractRealmsButton) {
        this.addButton(abstractRealmsButton.getProxy());
    }
    
    public List<AbstractRealmsButton<?>> buttons() {
        final List<AbstractRealmsButton<?>> list2 = (List<AbstractRealmsButton<?>>)Lists.newArrayListWithExpectedSize(this.buttons.size());
        for (final AbstractWidget czg4 : this.buttons) {
            list2.add(((RealmsAbstractButtonProxy)czg4).getButton());
        }
        return list2;
    }
    
    public void buttonsClear() {
        final Set<GuiEventListener> set2 = (Set<GuiEventListener>)Sets.newHashSet((Iterable)this.buttons);
        this.children.removeIf(set2::contains);
        this.buttons.clear();
    }
    
    public void removeButton(final RealmsButton realmsButton) {
        this.children.remove(realmsButton.getProxy());
        this.buttons.remove(realmsButton.getProxy());
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        return this.screen.mouseClicked(double1, double2, integer) || super.mouseClicked(double1, double2, integer);
    }
    
    @Override
    public boolean mouseReleased(final double double1, final double double2, final int integer) {
        return this.screen.mouseReleased(double1, double2, integer);
    }
    
    @Override
    public boolean mouseDragged(final double double1, final double double2, final int integer, final double double4, final double double5) {
        return this.screen.mouseDragged(double1, double2, integer, double4, double5) || super.mouseDragged(double1, double2, integer, double4, double5);
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        return this.screen.keyPressed(integer1, integer2, integer3) || super.keyPressed(integer1, integer2, integer3);
    }
    
    @Override
    public boolean charTyped(final char character, final int integer) {
        return this.screen.charTyped(character, integer) || super.charTyped(character, integer);
    }
    
    @Override
    public void removed() {
        this.screen.removed();
        super.removed();
    }
    
    public int draw(final String string, final int integer2, final int integer3, final int integer4, final boolean boolean5) {
        if (boolean5) {
            return this.font.drawShadow(string, (float)integer2, (float)integer3, integer4);
        }
        return this.font.draw(string, (float)integer2, (float)integer3, integer4);
    }
    
    public Font getFont() {
        return this.font;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
