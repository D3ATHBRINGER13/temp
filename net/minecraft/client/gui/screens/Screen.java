package net.minecraft.client.gui.screens;

import com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.CrashReportDetail;
import net.minecraft.CrashReport;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.Util;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.io.File;
import java.util.Locale;
import java.net.URISyntaxException;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.nbt.Tag;
import com.google.gson.JsonSyntaxException;
import net.minecraft.ChatFormatting;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.HoverEvent;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Arrays;
import java.util.Iterator;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import com.google.common.collect.Lists;
import java.net.URI;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.entity.ItemRenderer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import java.util.List;
import net.minecraft.network.chat.Component;
import java.util.Set;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;

public abstract class Screen extends AbstractContainerEventHandler implements Widget {
    private static final Logger LOGGER;
    private static final Set<String> ALLOWED_PROTOCOLS;
    protected final Component title;
    protected final List<GuiEventListener> children;
    @Nullable
    protected Minecraft minecraft;
    protected ItemRenderer itemRenderer;
    public int width;
    public int height;
    protected final List<AbstractWidget> buttons;
    public boolean passEvents;
    protected Font font;
    private URI clickedLink;
    
    protected Screen(final Component jo) {
        this.children = (List<GuiEventListener>)Lists.newArrayList();
        this.buttons = (List<AbstractWidget>)Lists.newArrayList();
        this.title = jo;
    }
    
    public Component getTitle() {
        return this.title;
    }
    
    public String getNarrationMessage() {
        return this.getTitle().getString();
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        for (int integer3 = 0; integer3 < this.buttons.size(); ++integer3) {
            ((AbstractWidget)this.buttons.get(integer3)).render(integer1, integer2, float3);
        }
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (integer1 == 256 && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        }
        if (integer1 == 258) {
            final boolean boolean5 = !hasShiftDown();
            if (!this.changeFocus(boolean5)) {
                this.changeFocus(boolean5);
            }
            return true;
        }
        return super.keyPressed(integer1, integer2, integer3);
    }
    
    public boolean shouldCloseOnEsc() {
        return true;
    }
    
    public void onClose() {
        this.minecraft.setScreen(null);
    }
    
    protected <T extends AbstractWidget> T addButton(final T czg) {
        this.buttons.add(czg);
        this.children.add(czg);
        return czg;
    }
    
    protected void renderTooltip(final ItemStack bcj, final int integer2, final int integer3) {
        this.renderTooltip(this.getTooltipFromItem(bcj), integer2, integer3);
    }
    
    public List<String> getTooltipFromItem(final ItemStack bcj) {
        final List<Component> list3 = bcj.getTooltipLines(this.minecraft.player, this.minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
        final List<String> list4 = (List<String>)Lists.newArrayList();
        for (final Component jo6 : list3) {
            list4.add(jo6.getColoredString());
        }
        return list4;
    }
    
    public void renderTooltip(final String string, final int integer2, final int integer3) {
        this.renderTooltip((List<String>)Arrays.asList((Object[])new String[] { string }), integer2, integer3);
    }
    
    public void renderTooltip(final List<String> list, final int integer2, final int integer3) {
        if (list.isEmpty()) {
            return;
        }
        GlStateManager.disableRescaleNormal();
        Lighting.turnOff();
        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();
        int integer4 = 0;
        for (final String string7 : list) {
            final int integer5 = this.font.width(string7);
            if (integer5 > integer4) {
                integer4 = integer5;
            }
        }
        int integer6 = integer2 + 12;
        int integer7 = integer3 - 12;
        final int integer5 = integer4;
        int integer8 = 8;
        if (list.size() > 1) {
            integer8 += 2 + (list.size() - 1) * 10;
        }
        if (integer6 + integer4 > this.width) {
            integer6 -= 28 + integer4;
        }
        if (integer7 + integer8 + 6 > this.height) {
            integer7 = this.height - integer8 - 6;
        }
        this.blitOffset = 300;
        this.itemRenderer.blitOffset = 300.0f;
        final int integer9 = -267386864;
        this.fillGradient(integer6 - 3, integer7 - 4, integer6 + integer5 + 3, integer7 - 3, -267386864, -267386864);
        this.fillGradient(integer6 - 3, integer7 + integer8 + 3, integer6 + integer5 + 3, integer7 + integer8 + 4, -267386864, -267386864);
        this.fillGradient(integer6 - 3, integer7 - 3, integer6 + integer5 + 3, integer7 + integer8 + 3, -267386864, -267386864);
        this.fillGradient(integer6 - 4, integer7 - 3, integer6 - 3, integer7 + integer8 + 3, -267386864, -267386864);
        this.fillGradient(integer6 + integer5 + 3, integer7 - 3, integer6 + integer5 + 4, integer7 + integer8 + 3, -267386864, -267386864);
        final int integer10 = 1347420415;
        final int integer11 = 1344798847;
        this.fillGradient(integer6 - 3, integer7 - 3 + 1, integer6 - 3 + 1, integer7 + integer8 + 3 - 1, 1347420415, 1344798847);
        this.fillGradient(integer6 + integer5 + 2, integer7 - 3 + 1, integer6 + integer5 + 3, integer7 + integer8 + 3 - 1, 1347420415, 1344798847);
        this.fillGradient(integer6 - 3, integer7 - 3, integer6 + integer5 + 3, integer7 - 3 + 1, 1347420415, 1347420415);
        this.fillGradient(integer6 - 3, integer7 + integer8 + 2, integer6 + integer5 + 3, integer7 + integer8 + 3, 1344798847, 1344798847);
        for (int integer12 = 0; integer12 < list.size(); ++integer12) {
            final String string8 = (String)list.get(integer12);
            this.font.drawShadow(string8, (float)integer6, (float)integer7, -1);
            if (integer12 == 0) {
                integer7 += 2;
            }
            integer7 += 10;
        }
        this.blitOffset = 0;
        this.itemRenderer.blitOffset = 0.0f;
        GlStateManager.enableLighting();
        GlStateManager.enableDepthTest();
        Lighting.turnOn();
        GlStateManager.enableRescaleNormal();
    }
    
    protected void renderComponentHoverEffect(final Component jo, final int integer2, final int integer3) {
        if (jo == null || jo.getStyle().getHoverEvent() == null) {
            return;
        }
        final HoverEvent jr5 = jo.getStyle().getHoverEvent();
        if (jr5.getAction() == HoverEvent.Action.SHOW_ITEM) {
            ItemStack bcj6 = ItemStack.EMPTY;
            try {
                final Tag iu7 = TagParser.parseTag(jr5.getValue().getString());
                if (iu7 instanceof CompoundTag) {
                    bcj6 = ItemStack.of((CompoundTag)iu7);
                }
            }
            catch (CommandSyntaxException ex2) {}
            if (bcj6.isEmpty()) {
                this.renderTooltip(new StringBuilder().append(ChatFormatting.RED).append("Invalid Item!").toString(), integer2, integer3);
            }
            else {
                this.renderTooltip(bcj6, integer2, integer3);
            }
        }
        else if (jr5.getAction() == HoverEvent.Action.SHOW_ENTITY) {
            if (this.minecraft.options.advancedItemTooltips) {
                try {
                    final CompoundTag id6 = TagParser.parseTag(jr5.getValue().getString());
                    final List<String> list7 = (List<String>)Lists.newArrayList();
                    final Component jo2 = Component.Serializer.fromJson(id6.getString("name"));
                    if (jo2 != null) {
                        list7.add(jo2.getColoredString());
                    }
                    if (id6.contains("type", 8)) {
                        final String string9 = id6.getString("type");
                        list7.add(("Type: " + string9));
                    }
                    list7.add(id6.getString("id"));
                    this.renderTooltip(list7, integer2, integer3);
                }
                catch (JsonSyntaxException | CommandSyntaxException ex3) {
                    final Exception ex;
                    final Exception exception6 = ex;
                    this.renderTooltip(new StringBuilder().append(ChatFormatting.RED).append("Invalid Entity!").toString(), integer2, integer3);
                }
            }
        }
        else if (jr5.getAction() == HoverEvent.Action.SHOW_TEXT) {
            this.renderTooltip(this.minecraft.font.split(jr5.getValue().getColoredString(), Math.max(this.width / 2, 200)), integer2, integer3);
        }
        GlStateManager.disableLighting();
    }
    
    protected void insertText(final String string, final boolean boolean2) {
    }
    
    public boolean handleComponentClicked(final Component jo) {
        if (jo == null) {
            return false;
        }
        final ClickEvent jn3 = jo.getStyle().getClickEvent();
        if (hasShiftDown()) {
            if (jo.getStyle().getInsertion() != null) {
                this.insertText(jo.getStyle().getInsertion(), false);
            }
        }
        else if (jn3 != null) {
            if (jn3.getAction() == ClickEvent.Action.OPEN_URL) {
                if (!this.minecraft.options.chatLinks) {
                    return false;
                }
                try {
                    final URI uRI4 = new URI(jn3.getValue());
                    final String string5 = uRI4.getScheme();
                    if (string5 == null) {
                        throw new URISyntaxException(jn3.getValue(), "Missing protocol");
                    }
                    if (!Screen.ALLOWED_PROTOCOLS.contains(string5.toLowerCase(Locale.ROOT))) {
                        throw new URISyntaxException(jn3.getValue(), "Unsupported protocol: " + string5.toLowerCase(Locale.ROOT));
                    }
                    if (this.minecraft.options.chatLinksPrompt) {
                        this.clickedLink = uRI4;
                        this.minecraft.setScreen(new ConfirmLinkScreen(this::confirmLink, jn3.getValue(), false));
                    }
                    else {
                        this.openLink(uRI4);
                    }
                }
                catch (URISyntaxException uRISyntaxException4) {
                    Screen.LOGGER.error("Can't open url for {}", jn3, uRISyntaxException4);
                }
            }
            else if (jn3.getAction() == ClickEvent.Action.OPEN_FILE) {
                final URI uRI4 = new File(jn3.getValue()).toURI();
                this.openLink(uRI4);
            }
            else if (jn3.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
                this.insertText(jn3.getValue(), true);
            }
            else if (jn3.getAction() == ClickEvent.Action.RUN_COMMAND) {
                this.sendMessage(jn3.getValue(), false);
            }
            else {
                Screen.LOGGER.error("Don't know how to handle {}", jn3);
            }
            return true;
        }
        return false;
    }
    
    public void sendMessage(final String string) {
        this.sendMessage(string, true);
    }
    
    public void sendMessage(final String string, final boolean boolean2) {
        if (boolean2) {
            this.minecraft.gui.getChat().addRecentChat(string);
        }
        this.minecraft.player.chat(string);
    }
    
    public void init(final Minecraft cyc, final int integer2, final int integer3) {
        this.minecraft = cyc;
        this.itemRenderer = cyc.getItemRenderer();
        this.font = cyc.font;
        this.width = integer2;
        this.height = integer3;
        this.buttons.clear();
        this.children.clear();
        this.setFocused(null);
        this.init();
    }
    
    public void setSize(final int integer1, final int integer2) {
        this.width = integer1;
        this.height = integer2;
    }
    
    @Override
    public List<? extends GuiEventListener> children() {
        return this.children;
    }
    
    protected void init() {
    }
    
    public void tick() {
    }
    
    public void removed() {
    }
    
    public void renderBackground() {
        this.renderBackground(0);
    }
    
    public void renderBackground(final int integer) {
        if (this.minecraft.level != null) {
            this.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        }
        else {
            this.renderDirtBackground(integer);
        }
    }
    
    public void renderDirtBackground(final int integer) {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        final Tesselator cuz3 = Tesselator.getInstance();
        final BufferBuilder cuw4 = cuz3.getBuilder();
        this.minecraft.getTextureManager().bind(Screen.BACKGROUND_LOCATION);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final float float5 = 32.0f;
        cuw4.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
        cuw4.vertex(0.0, this.height, 0.0).uv(0.0, this.height / 32.0f + integer).color(64, 64, 64, 255).endVertex();
        cuw4.vertex(this.width, this.height, 0.0).uv(this.width / 32.0f, this.height / 32.0f + integer).color(64, 64, 64, 255).endVertex();
        cuw4.vertex(this.width, 0.0, 0.0).uv(this.width / 32.0f, integer).color(64, 64, 64, 255).endVertex();
        cuw4.vertex(0.0, 0.0, 0.0).uv(0.0, integer).color(64, 64, 64, 255).endVertex();
        cuz3.end();
    }
    
    public boolean isPauseScreen() {
        return true;
    }
    
    private void confirmLink(final boolean boolean1) {
        if (boolean1) {
            this.openLink(this.clickedLink);
        }
        this.clickedLink = null;
        this.minecraft.setScreen(this);
    }
    
    private void openLink(final URI uRI) {
        Util.getPlatform().openUri(uRI);
    }
    
    public static boolean hasControlDown() {
        if (Minecraft.ON_OSX) {
            return InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 343) || InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 347);
        }
        return InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 341) || InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 345);
    }
    
    public static boolean hasShiftDown() {
        return InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 344);
    }
    
    public static boolean hasAltDown() {
        return InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 342) || InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 346);
    }
    
    public static boolean isCut(final int integer) {
        return integer == 88 && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }
    
    public static boolean isPaste(final int integer) {
        return integer == 86 && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }
    
    public static boolean isCopy(final int integer) {
        return integer == 67 && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }
    
    public static boolean isSelectAll(final int integer) {
        return integer == 65 && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }
    
    public void resize(final Minecraft cyc, final int integer2, final int integer3) {
        this.init(cyc, integer2, integer3);
    }
    
    public static void wrapScreenError(final Runnable runnable, final String string2, final String string3) {
        try {
            runnable.run();
        }
        catch (Throwable throwable4) {
            final CrashReport d5 = CrashReport.forThrowable(throwable4, string2);
            final CrashReportCategory e6 = d5.addCategory("Affected screen");
            e6.setDetail("Screen name", (CrashReportDetail<String>)(() -> string3));
            throw new ReportedException(d5);
        }
    }
    
    protected boolean isValidCharacterForName(final String string, final char character, final int integer) {
        final int integer2 = string.indexOf(58);
        final int integer3 = string.indexOf(47);
        if (character == ':') {
            return (integer3 == -1 || integer <= integer3) && integer2 == -1;
        }
        if (character == '/') {
            return integer > integer2;
        }
        return character == '_' || character == '-' || (character >= 'a' && character <= 'z') || (character >= '0' && character <= '9') || character == '.';
    }
    
    public boolean isMouseOver(final double double1, final double double2) {
        return true;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        ALLOWED_PROTOCOLS = (Set)Sets.newHashSet((Object[])new String[] { "http", "https" });
    }
}
