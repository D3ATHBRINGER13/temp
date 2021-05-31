package net.minecraft.client.gui.components;

import org.apache.logging.log4j.LogManager;
import net.minecraft.client.gui.screens.ChatScreen;
import javax.annotation.Nullable;
import net.minecraft.network.chat.TextComponent;
import java.util.Iterator;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.ChatVisiblity;
import com.google.common.collect.Lists;
import net.minecraft.client.GuiMessage;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.gui.GuiComponent;

public class ChatComponent extends GuiComponent {
    private static final Logger LOGGER;
    private final Minecraft minecraft;
    private final List<String> recentChat;
    private final List<GuiMessage> allMessages;
    private final List<GuiMessage> trimmedMessages;
    private int chatScrollbarPos;
    private boolean newMessageSinceScroll;
    
    public ChatComponent(final Minecraft cyc) {
        this.recentChat = (List<String>)Lists.newArrayList();
        this.allMessages = (List<GuiMessage>)Lists.newArrayList();
        this.trimmedMessages = (List<GuiMessage>)Lists.newArrayList();
        this.minecraft = cyc;
    }
    
    public void render(final int integer) {
        if (this.minecraft.options.chatVisibility == ChatVisiblity.HIDDEN) {
            return;
        }
        final int integer2 = this.getLinesPerPage();
        final int integer3 = this.trimmedMessages.size();
        if (integer3 <= 0) {
            return;
        }
        boolean boolean5 = false;
        if (this.isChatFocused()) {
            boolean5 = true;
        }
        final double double6 = this.getScale();
        final int integer4 = Mth.ceil(this.getWidth() / double6);
        GlStateManager.pushMatrix();
        GlStateManager.translatef(2.0f, 8.0f, 0.0f);
        GlStateManager.scaled(double6, double6, 1.0);
        final double double7 = this.minecraft.options.chatOpacity * 0.8999999761581421 + 0.10000000149011612;
        final double double8 = this.minecraft.options.textBackgroundOpacity;
        int integer5 = 0;
        for (int integer6 = 0; integer6 + this.chatScrollbarPos < this.trimmedMessages.size() && integer6 < integer2; ++integer6) {
            final GuiMessage cxx15 = (GuiMessage)this.trimmedMessages.get(integer6 + this.chatScrollbarPos);
            if (cxx15 != null) {
                final int integer7 = integer - cxx15.getAddedTime();
                if (integer7 < 200 || boolean5) {
                    final double double9 = boolean5 ? 1.0 : getTimeFactor(integer7);
                    final int integer8 = (int)(255.0 * double9 * double7);
                    final int integer9 = (int)(255.0 * double9 * double8);
                    ++integer5;
                    if (integer8 > 3) {
                        final int integer10 = 0;
                        final int integer11 = -integer6 * 9;
                        GuiComponent.fill(-2, integer11 - 9, 0 + integer4 + 4, integer11, integer9 << 24);
                        final String string23 = cxx15.getMessage().getColoredString();
                        GlStateManager.enableBlend();
                        this.minecraft.font.drawShadow(string23, 0.0f, (float)(integer11 - 8), 16777215 + (integer8 << 24));
                        GlStateManager.disableAlphaTest();
                        GlStateManager.disableBlend();
                    }
                }
            }
        }
        if (boolean5) {
            this.minecraft.font.getClass();
            final int integer6 = 9;
            GlStateManager.translatef(-3.0f, 0.0f, 0.0f);
            final int integer12 = integer3 * integer6 + integer3;
            final int integer7 = integer5 * integer6 + integer5;
            final int integer13 = this.chatScrollbarPos * integer7 / integer3;
            final int integer14 = integer7 * integer7 / integer12;
            if (integer12 != integer7) {
                final int integer8 = (integer13 > 0) ? 170 : 96;
                final int integer9 = this.newMessageSinceScroll ? 13382451 : 3355562;
                GuiComponent.fill(0, -integer13, 2, -integer13 - integer14, integer9 + (integer8 << 24));
                GuiComponent.fill(2, -integer13, 1, -integer13 - integer14, 13421772 + (integer8 << 24));
            }
        }
        GlStateManager.popMatrix();
    }
    
    private static double getTimeFactor(final int integer) {
        double double2 = integer / 200.0;
        double2 = 1.0 - double2;
        double2 *= 10.0;
        double2 = Mth.clamp(double2, 0.0, 1.0);
        double2 *= double2;
        return double2;
    }
    
    public void clearMessages(final boolean boolean1) {
        this.trimmedMessages.clear();
        this.allMessages.clear();
        if (boolean1) {
            this.recentChat.clear();
        }
    }
    
    public void addMessage(final Component jo) {
        this.addMessage(jo, 0);
    }
    
    public void addMessage(final Component jo, final int integer) {
        this.addMessage(jo, integer, this.minecraft.gui.getGuiTicks(), false);
        ChatComponent.LOGGER.info("[CHAT] {}", jo.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
    }
    
    private void addMessage(final Component jo, final int integer2, final int integer3, final boolean boolean4) {
        if (integer2 != 0) {
            this.removeById(integer2);
        }
        final int integer4 = Mth.floor(this.getWidth() / this.getScale());
        final List<Component> list7 = ComponentRenderUtils.wrapComponents(jo, integer4, this.minecraft.font, false, false);
        final boolean boolean5 = this.isChatFocused();
        for (final Component jo2 : list7) {
            if (boolean5 && this.chatScrollbarPos > 0) {
                this.newMessageSinceScroll = true;
                this.scrollChat(1.0);
            }
            this.trimmedMessages.add(0, new GuiMessage(integer3, jo2, integer2));
        }
        while (this.trimmedMessages.size() > 100) {
            this.trimmedMessages.remove(this.trimmedMessages.size() - 1);
        }
        if (!boolean4) {
            this.allMessages.add(0, new GuiMessage(integer3, jo, integer2));
            while (this.allMessages.size() > 100) {
                this.allMessages.remove(this.allMessages.size() - 1);
            }
        }
    }
    
    public void rescaleChat() {
        this.trimmedMessages.clear();
        this.resetChatScroll();
        for (int integer2 = this.allMessages.size() - 1; integer2 >= 0; --integer2) {
            final GuiMessage cxx3 = (GuiMessage)this.allMessages.get(integer2);
            this.addMessage(cxx3.getMessage(), cxx3.getId(), cxx3.getAddedTime(), true);
        }
    }
    
    public List<String> getRecentChat() {
        return this.recentChat;
    }
    
    public void addRecentChat(final String string) {
        if (this.recentChat.isEmpty() || !((String)this.recentChat.get(this.recentChat.size() - 1)).equals(string)) {
            this.recentChat.add(string);
        }
    }
    
    public void resetChatScroll() {
        this.chatScrollbarPos = 0;
        this.newMessageSinceScroll = false;
    }
    
    public void scrollChat(final double double1) {
        this.chatScrollbarPos += (int)double1;
        final int integer4 = this.trimmedMessages.size();
        if (this.chatScrollbarPos > integer4 - this.getLinesPerPage()) {
            this.chatScrollbarPos = integer4 - this.getLinesPerPage();
        }
        if (this.chatScrollbarPos <= 0) {
            this.chatScrollbarPos = 0;
            this.newMessageSinceScroll = false;
        }
    }
    
    @Nullable
    public Component getClickedComponentAt(final double double1, final double double2) {
        if (!this.isChatFocused()) {
            return null;
        }
        final double double3 = this.getScale();
        double double4 = double1 - 2.0;
        double double5 = this.minecraft.window.getGuiScaledHeight() - double2 - 40.0;
        double4 = Mth.floor(double4 / double3);
        double5 = Mth.floor(double5 / double3);
        if (double4 < 0.0 || double5 < 0.0) {
            return null;
        }
        final int integer12 = Math.min(this.getLinesPerPage(), this.trimmedMessages.size());
        if (double4 <= Mth.floor(this.getWidth() / this.getScale())) {
            final double n = double5;
            this.minecraft.font.getClass();
            if (n < 9 * integer12 + integer12) {
                final double n2 = double5;
                this.minecraft.font.getClass();
                final int integer13 = (int)(n2 / 9.0 + this.chatScrollbarPos);
                if (integer13 >= 0 && integer13 < this.trimmedMessages.size()) {
                    final GuiMessage cxx14 = (GuiMessage)this.trimmedMessages.get(integer13);
                    int integer14 = 0;
                    for (final Component jo17 : cxx14.getMessage()) {
                        if (jo17 instanceof TextComponent) {
                            integer14 += this.minecraft.font.width(ComponentRenderUtils.stripColor(((TextComponent)jo17).getText(), false));
                            if (integer14 > double4) {
                                return jo17;
                            }
                            continue;
                        }
                    }
                }
                return null;
            }
        }
        return null;
    }
    
    public boolean isChatFocused() {
        return this.minecraft.screen instanceof ChatScreen;
    }
    
    public void removeById(final int integer) {
        Iterator<GuiMessage> iterator3 = (Iterator<GuiMessage>)this.trimmedMessages.iterator();
        while (iterator3.hasNext()) {
            final GuiMessage cxx4 = (GuiMessage)iterator3.next();
            if (cxx4.getId() == integer) {
                iterator3.remove();
            }
        }
        iterator3 = (Iterator<GuiMessage>)this.allMessages.iterator();
        while (iterator3.hasNext()) {
            final GuiMessage cxx4 = (GuiMessage)iterator3.next();
            if (cxx4.getId() == integer) {
                iterator3.remove();
                break;
            }
        }
    }
    
    public int getWidth() {
        return getWidth(this.minecraft.options.chatWidth);
    }
    
    public int getHeight() {
        return getHeight(this.isChatFocused() ? this.minecraft.options.chatHeightFocused : this.minecraft.options.chatHeightUnfocused);
    }
    
    public double getScale() {
        return this.minecraft.options.chatScale;
    }
    
    public static int getWidth(final double double1) {
        final int integer3 = 320;
        final int integer4 = 40;
        return Mth.floor(double1 * 280.0 + 40.0);
    }
    
    public static int getHeight(final double double1) {
        final int integer3 = 180;
        final int integer4 = 20;
        return Mth.floor(double1 * 160.0 + 20.0);
    }
    
    public int getLinesPerPage() {
        return this.getHeight() / 9;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
