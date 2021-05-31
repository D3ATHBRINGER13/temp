package net.minecraft.client.gui.screens;

import com.mojang.brigadier.Message;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.world.phys.Vec2;
import net.minecraft.client.renderer.Rect2i;
import javax.annotation.Nullable;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.ChatFormatting;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Map;
import java.util.Collection;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.StringReader;
import java.util.regex.Matcher;
import com.google.common.base.Strings;
import java.util.Iterator;
import net.minecraft.util.Mth;
import com.mojang.brigadier.suggestion.Suggestion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import java.util.function.Consumer;
import java.util.function.BiFunction;
import net.minecraft.client.resources.language.I18n;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.chat.NarratorChatListener;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.SharedSuggestionProvider;
import com.mojang.brigadier.ParseResults;
import java.util.List;
import net.minecraft.client.gui.components.EditBox;
import java.util.regex.Pattern;

public class ChatScreen extends Screen {
    private static final Pattern WHITESPACE_PATTERN;
    private String historyBuffer;
    private int historyPos;
    protected EditBox input;
    private String initial;
    protected final List<String> commandUsage;
    protected int commandUsagePosition;
    protected int commandUsageWidth;
    private ParseResults<SharedSuggestionProvider> currentParse;
    private CompletableFuture<Suggestions> pendingSuggestions;
    private SuggestionsList suggestions;
    private boolean hasEdits;
    private boolean keepSuggestions;
    
    public ChatScreen(final String string) {
        super(NarratorChatListener.NO_TITLE);
        this.historyBuffer = "";
        this.historyPos = -1;
        this.initial = "";
        this.commandUsage = (List<String>)Lists.newArrayList();
        this.initial = string;
    }
    
    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.historyPos = this.minecraft.gui.getChat().getRecentChat().size();
        (this.input = new EditBox(this.font, 4, this.height - 12, this.width - 4, 12, I18n.get("chat.editBox"))).setMaxLength(256);
        this.input.setBordered(false);
        this.input.setValue(this.initial);
        this.input.setFormatter((BiFunction<String, Integer, String>)this::formatChat);
        this.input.setResponder((Consumer<String>)this::onEdited);
        this.children.add(this.input);
        this.updateCommandInfo();
        this.setInitialFocus(this.input);
    }
    
    @Override
    public void resize(final Minecraft cyc, final int integer2, final int integer3) {
        final String string5 = this.input.getValue();
        this.init(cyc, integer2, integer3);
        this.setChatLine(string5);
        this.updateCommandInfo();
    }
    
    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
        this.minecraft.gui.getChat().resetChatScroll();
    }
    
    @Override
    public void tick() {
        this.input.tick();
    }
    
    private void onEdited(final String string) {
        final String string2 = this.input.getValue();
        this.hasEdits = !string2.equals(this.initial);
        this.updateCommandInfo();
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (this.suggestions != null && this.suggestions.keyPressed(integer1, integer2, integer3)) {
            return true;
        }
        if (integer1 == 258) {
            this.hasEdits = true;
            this.showSuggestions();
        }
        if (super.keyPressed(integer1, integer2, integer3)) {
            return true;
        }
        if (integer1 == 256) {
            this.minecraft.setScreen(null);
            return true;
        }
        if (integer1 == 257 || integer1 == 335) {
            final String string5 = this.input.getValue().trim();
            if (!string5.isEmpty()) {
                this.sendMessage(string5);
            }
            this.minecraft.setScreen(null);
            return true;
        }
        if (integer1 == 265) {
            this.moveInHistory(-1);
            return true;
        }
        if (integer1 == 264) {
            this.moveInHistory(1);
            return true;
        }
        if (integer1 == 266) {
            this.minecraft.gui.getChat().scrollChat(this.minecraft.gui.getChat().getLinesPerPage() - 1);
            return true;
        }
        if (integer1 == 267) {
            this.minecraft.gui.getChat().scrollChat(-this.minecraft.gui.getChat().getLinesPerPage() + 1);
            return true;
        }
        return false;
    }
    
    public void showSuggestions() {
        if (this.pendingSuggestions != null && this.pendingSuggestions.isDone()) {
            int integer2 = 0;
            final Suggestions suggestions3 = (Suggestions)this.pendingSuggestions.join();
            if (!suggestions3.getList().isEmpty()) {
                for (final Suggestion suggestion5 : suggestions3.getList()) {
                    integer2 = Math.max(integer2, this.font.width(suggestion5.getText()));
                }
                final int integer3 = Mth.clamp(this.input.getScreenX(suggestions3.getRange().getStart()), 0, this.width - integer2);
                this.suggestions = new SuggestionsList(integer3, this.height - 12, integer2, suggestions3);
            }
        }
    }
    
    private static int getLastWordIndex(final String string) {
        if (Strings.isNullOrEmpty(string)) {
            return 0;
        }
        int integer2 = 0;
        final Matcher matcher3 = ChatScreen.WHITESPACE_PATTERN.matcher((CharSequence)string);
        while (matcher3.find()) {
            integer2 = matcher3.end();
        }
        return integer2;
    }
    
    private void updateCommandInfo() {
        final String string2 = this.input.getValue();
        if (this.currentParse != null && !this.currentParse.getReader().getString().equals(string2)) {
            this.currentParse = null;
        }
        if (!this.keepSuggestions) {
            this.input.setSuggestion(null);
            this.suggestions = null;
        }
        this.commandUsage.clear();
        final StringReader stringReader3 = new StringReader(string2);
        if (stringReader3.canRead() && stringReader3.peek() == '/') {
            stringReader3.skip();
            final CommandDispatcher<SharedSuggestionProvider> commandDispatcher4 = this.minecraft.player.connection.getCommands();
            if (this.currentParse == null) {
                this.currentParse = (ParseResults<SharedSuggestionProvider>)commandDispatcher4.parse(stringReader3, this.minecraft.player.connection.getSuggestionsProvider());
            }
            final int integer5 = this.input.getCursorPosition();
            if (integer5 >= 1 && (this.suggestions == null || !this.keepSuggestions)) {
                (this.pendingSuggestions = (CompletableFuture<Suggestions>)commandDispatcher4.getCompletionSuggestions((ParseResults)this.currentParse, integer5)).thenRun(() -> {
                    if (!this.pendingSuggestions.isDone()) {
                        return;
                    }
                    this.updateUsageInfo();
                });
            }
        }
        else {
            final String string3 = string2;
            final int integer5 = getLastWordIndex(string3);
            final Collection<String> collection6 = this.minecraft.player.connection.getSuggestionsProvider().getOnlinePlayerNames();
            this.pendingSuggestions = SharedSuggestionProvider.suggest((Iterable<String>)collection6, new SuggestionsBuilder(string3, integer5));
        }
    }
    
    private void updateUsageInfo() {
        if (((Suggestions)this.pendingSuggestions.join()).isEmpty() && !this.currentParse.getExceptions().isEmpty() && this.input.getCursorPosition() == this.input.getValue().length()) {
            int integer2 = 0;
            for (final Map.Entry<CommandNode<SharedSuggestionProvider>, CommandSyntaxException> entry4 : this.currentParse.getExceptions().entrySet()) {
                final CommandSyntaxException commandSyntaxException5 = (CommandSyntaxException)entry4.getValue();
                if (commandSyntaxException5.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
                    ++integer2;
                }
                else {
                    this.commandUsage.add(commandSyntaxException5.getMessage());
                }
            }
            if (integer2 > 0) {
                this.commandUsage.add(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create().getMessage());
            }
        }
        this.commandUsagePosition = 0;
        this.commandUsageWidth = this.width;
        if (this.commandUsage.isEmpty()) {
            this.fillNodeUsage(ChatFormatting.GRAY);
        }
        this.suggestions = null;
        if (this.hasEdits && this.minecraft.options.autoSuggestions) {
            this.showSuggestions();
        }
    }
    
    private String formatChat(final String string, final int integer) {
        if (this.currentParse != null) {
            return formatText(this.currentParse, string, integer);
        }
        return string;
    }
    
    public static String formatText(final ParseResults<SharedSuggestionProvider> parseResults, final String string, final int integer) {
        final ChatFormatting[] arr4 = { ChatFormatting.AQUA, ChatFormatting.YELLOW, ChatFormatting.GREEN, ChatFormatting.LIGHT_PURPLE, ChatFormatting.GOLD };
        final String string2 = ChatFormatting.GRAY.toString();
        final StringBuilder stringBuilder6 = new StringBuilder(string2);
        int integer2 = 0;
        int integer3 = -1;
        final CommandContextBuilder<SharedSuggestionProvider> commandContextBuilder9 = (CommandContextBuilder<SharedSuggestionProvider>)parseResults.getContext().getLastChild();
        for (final ParsedArgument<SharedSuggestionProvider, ?> parsedArgument11 : commandContextBuilder9.getArguments().values()) {
            if (++integer3 >= arr4.length) {
                integer3 = 0;
            }
            final int integer4 = Math.max(parsedArgument11.getRange().getStart() - integer, 0);
            if (integer4 >= string.length()) {
                break;
            }
            final int integer5 = Math.min(parsedArgument11.getRange().getEnd() - integer, string.length());
            if (integer5 <= 0) {
                continue;
            }
            stringBuilder6.append((CharSequence)string, integer2, integer4);
            stringBuilder6.append(arr4[integer3]);
            stringBuilder6.append((CharSequence)string, integer4, integer5);
            stringBuilder6.append(string2);
            integer2 = integer5;
        }
        if (parseResults.getReader().canRead()) {
            final int integer6 = Math.max(parseResults.getReader().getCursor() - integer, 0);
            if (integer6 < string.length()) {
                final int integer7 = Math.min(integer6 + parseResults.getReader().getRemainingLength(), string.length());
                stringBuilder6.append((CharSequence)string, integer2, integer6);
                stringBuilder6.append(ChatFormatting.RED);
                stringBuilder6.append((CharSequence)string, integer6, integer7);
                integer2 = integer7;
            }
        }
        stringBuilder6.append((CharSequence)string, integer2, string.length());
        return stringBuilder6.toString();
    }
    
    @Override
    public boolean mouseScrolled(final double double1, final double double2, double double3) {
        if (double3 > 1.0) {
            double3 = 1.0;
        }
        if (double3 < -1.0) {
            double3 = -1.0;
        }
        if (this.suggestions != null && this.suggestions.mouseScrolled(double3)) {
            return true;
        }
        if (!Screen.hasShiftDown()) {
            double3 *= 7.0;
        }
        this.minecraft.gui.getChat().scrollChat(double3);
        return true;
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        if (this.suggestions != null && this.suggestions.mouseClicked((int)double1, (int)double2, integer)) {
            return true;
        }
        if (integer == 0) {
            final Component jo7 = this.minecraft.gui.getChat().getClickedComponentAt(double1, double2);
            if (jo7 != null && this.handleComponentClicked(jo7)) {
                return true;
            }
        }
        return this.input.mouseClicked(double1, double2, integer) || super.mouseClicked(double1, double2, integer);
    }
    
    @Override
    protected void insertText(final String string, final boolean boolean2) {
        if (boolean2) {
            this.input.setValue(string);
        }
        else {
            this.input.insertText(string);
        }
    }
    
    public void moveInHistory(final int integer) {
        int integer2 = this.historyPos + integer;
        final int integer3 = this.minecraft.gui.getChat().getRecentChat().size();
        integer2 = Mth.clamp(integer2, 0, integer3);
        if (integer2 == this.historyPos) {
            return;
        }
        if (integer2 == integer3) {
            this.historyPos = integer3;
            this.input.setValue(this.historyBuffer);
            return;
        }
        if (this.historyPos == integer3) {
            this.historyBuffer = this.input.getValue();
        }
        this.input.setValue((String)this.minecraft.gui.getChat().getRecentChat().get(integer2));
        this.suggestions = null;
        this.historyPos = integer2;
        this.hasEdits = false;
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.setFocused(this.input);
        this.input.setFocus(true);
        GuiComponent.fill(2, this.height - 14, this.width - 2, this.height - 2, this.minecraft.options.getBackgroundColor(Integer.MIN_VALUE));
        this.input.render(integer1, integer2, float3);
        if (this.suggestions != null) {
            this.suggestions.render(integer1, integer2);
        }
        else {
            int integer3 = 0;
            for (final String string7 : this.commandUsage) {
                GuiComponent.fill(this.commandUsagePosition - 1, this.height - 14 - 13 - 12 * integer3, this.commandUsagePosition + this.commandUsageWidth + 1, this.height - 2 - 13 - 12 * integer3, -16777216);
                this.font.drawShadow(string7, (float)this.commandUsagePosition, (float)(this.height - 14 - 13 + 2 - 12 * integer3), -1);
                ++integer3;
            }
        }
        final Component jo5 = this.minecraft.gui.getChat().getClickedComponentAt(integer1, integer2);
        if (jo5 != null && jo5.getStyle().getHoverEvent() != null) {
            this.renderComponentHoverEffect(jo5, integer1, integer2);
        }
        super.render(integer1, integer2, float3);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    private void fillNodeUsage(final ChatFormatting c) {
        final CommandContextBuilder<SharedSuggestionProvider> commandContextBuilder3 = (CommandContextBuilder<SharedSuggestionProvider>)this.currentParse.getContext();
        final SuggestionContext<SharedSuggestionProvider> suggestionContext4 = (SuggestionContext<SharedSuggestionProvider>)commandContextBuilder3.findSuggestionContext(this.input.getCursorPosition());
        final Map<CommandNode<SharedSuggestionProvider>, String> map5 = (Map<CommandNode<SharedSuggestionProvider>, String>)this.minecraft.player.connection.getCommands().getSmartUsage(suggestionContext4.parent, this.minecraft.player.connection.getSuggestionsProvider());
        final List<String> list6 = (List<String>)Lists.newArrayList();
        int integer7 = 0;
        for (final Map.Entry<CommandNode<SharedSuggestionProvider>, String> entry9 : map5.entrySet()) {
            if (!(entry9.getKey() instanceof LiteralCommandNode)) {
                list6.add(((Object)c + (String)entry9.getValue()));
                integer7 = Math.max(integer7, this.font.width((String)entry9.getValue()));
            }
        }
        if (!list6.isEmpty()) {
            this.commandUsage.addAll((Collection)list6);
            this.commandUsagePosition = Mth.clamp(this.input.getScreenX(suggestionContext4.startPos), 0, this.width - integer7);
            this.commandUsageWidth = integer7;
        }
    }
    
    @Nullable
    private static String calculateSuggestionSuffix(final String string1, final String string2) {
        if (string2.startsWith(string1)) {
            return string2.substring(string1.length());
        }
        return null;
    }
    
    private void setChatLine(final String string) {
        this.input.setValue(string);
    }
    
    static {
        WHITESPACE_PATTERN = Pattern.compile("(\\s+)");
    }
    
    class SuggestionsList {
        private final Rect2i rect;
        private final Suggestions suggestions;
        private final String originalContents;
        private int offset;
        private int current;
        private Vec2 lastMouse;
        private boolean tabCycles;
        
        private SuggestionsList(final int integer2, final int integer3, final int integer4, final Suggestions suggestions) {
            this.lastMouse = Vec2.ZERO;
            this.rect = new Rect2i(integer2 - 1, integer3 - 3 - Math.min(suggestions.getList().size(), 10) * 12, integer4 + 1, Math.min(suggestions.getList().size(), 10) * 12);
            this.suggestions = suggestions;
            this.originalContents = ChatScreen.this.input.getValue();
            this.select(0);
        }
        
        public void render(final int integer1, final int integer2) {
            final int integer3 = Math.min(this.suggestions.getList().size(), 10);
            final int integer4 = -5592406;
            final boolean boolean6 = this.offset > 0;
            final boolean boolean7 = this.suggestions.getList().size() > this.offset + integer3;
            final boolean boolean8 = boolean6 || boolean7;
            final boolean boolean9 = this.lastMouse.x != integer1 || this.lastMouse.y != integer2;
            if (boolean9) {
                this.lastMouse = new Vec2((float)integer1, (float)integer2);
            }
            if (boolean8) {
                GuiComponent.fill(this.rect.getX(), this.rect.getY() - 1, this.rect.getX() + this.rect.getWidth(), this.rect.getY(), -805306368);
                GuiComponent.fill(this.rect.getX(), this.rect.getY() + this.rect.getHeight(), this.rect.getX() + this.rect.getWidth(), this.rect.getY() + this.rect.getHeight() + 1, -805306368);
                if (boolean6) {
                    for (int integer5 = 0; integer5 < this.rect.getWidth(); ++integer5) {
                        if (integer5 % 2 == 0) {
                            GuiComponent.fill(this.rect.getX() + integer5, this.rect.getY() - 1, this.rect.getX() + integer5 + 1, this.rect.getY(), -1);
                        }
                    }
                }
                if (boolean7) {
                    for (int integer5 = 0; integer5 < this.rect.getWidth(); ++integer5) {
                        if (integer5 % 2 == 0) {
                            GuiComponent.fill(this.rect.getX() + integer5, this.rect.getY() + this.rect.getHeight(), this.rect.getX() + integer5 + 1, this.rect.getY() + this.rect.getHeight() + 1, -1);
                        }
                    }
                }
            }
            boolean boolean10 = false;
            for (int integer6 = 0; integer6 < integer3; ++integer6) {
                final Suggestion suggestion12 = (Suggestion)this.suggestions.getList().get(integer6 + this.offset);
                GuiComponent.fill(this.rect.getX(), this.rect.getY() + 12 * integer6, this.rect.getX() + this.rect.getWidth(), this.rect.getY() + 12 * integer6 + 12, -805306368);
                if (integer1 > this.rect.getX() && integer1 < this.rect.getX() + this.rect.getWidth() && integer2 > this.rect.getY() + 12 * integer6 && integer2 < this.rect.getY() + 12 * integer6 + 12) {
                    if (boolean9) {
                        this.select(integer6 + this.offset);
                    }
                    boolean10 = true;
                }
                ChatScreen.this.font.drawShadow(suggestion12.getText(), (float)(this.rect.getX() + 1), (float)(this.rect.getY() + 2 + 12 * integer6), (integer6 + this.offset == this.current) ? -256 : -5592406);
            }
            if (boolean10) {
                final Message message11 = ((Suggestion)this.suggestions.getList().get(this.current)).getTooltip();
                if (message11 != null) {
                    ChatScreen.this.renderTooltip(ComponentUtils.fromMessage(message11).getColoredString(), integer1, integer2);
                }
            }
        }
        
        public boolean mouseClicked(final int integer1, final int integer2, final int integer3) {
            if (!this.rect.contains(integer1, integer2)) {
                return false;
            }
            final int integer4 = (integer2 - this.rect.getY()) / 12 + this.offset;
            if (integer4 >= 0 && integer4 < this.suggestions.getList().size()) {
                this.select(integer4);
                this.useSuggestion();
            }
            return true;
        }
        
        public boolean mouseScrolled(final double double1) {
            final int integer4 = (int)(ChatScreen.this.minecraft.mouseHandler.xpos() * ChatScreen.this.minecraft.window.getGuiScaledWidth() / ChatScreen.this.minecraft.window.getScreenWidth());
            final int integer5 = (int)(ChatScreen.this.minecraft.mouseHandler.ypos() * ChatScreen.this.minecraft.window.getGuiScaledHeight() / ChatScreen.this.minecraft.window.getScreenHeight());
            if (this.rect.contains(integer4, integer5)) {
                this.offset = Mth.clamp((int)(this.offset - double1), 0, Math.max(this.suggestions.getList().size() - 10, 0));
                return true;
            }
            return false;
        }
        
        public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
            if (integer1 == 265) {
                this.cycle(-1);
                this.tabCycles = false;
                return true;
            }
            if (integer1 == 264) {
                this.cycle(1);
                this.tabCycles = false;
                return true;
            }
            if (integer1 == 258) {
                if (this.tabCycles) {
                    this.cycle(Screen.hasShiftDown() ? -1 : 1);
                }
                this.useSuggestion();
                return true;
            }
            if (integer1 == 256) {
                this.hide();
                return true;
            }
            return false;
        }
        
        public void cycle(final int integer) {
            this.select(this.current + integer);
            final int integer2 = this.offset;
            final int integer3 = this.offset + 10 - 1;
            if (this.current < integer2) {
                this.offset = Mth.clamp(this.current, 0, Math.max(this.suggestions.getList().size() - 10, 0));
            }
            else if (this.current > integer3) {
                this.offset = Mth.clamp(this.current + 1 - 10, 0, Math.max(this.suggestions.getList().size() - 10, 0));
            }
        }
        
        public void select(final int integer) {
            this.current = integer;
            if (this.current < 0) {
                this.current += this.suggestions.getList().size();
            }
            if (this.current >= this.suggestions.getList().size()) {
                this.current -= this.suggestions.getList().size();
            }
            final Suggestion suggestion3 = (Suggestion)this.suggestions.getList().get(this.current);
            ChatScreen.this.input.setSuggestion(calculateSuggestionSuffix(ChatScreen.this.input.getValue(), suggestion3.apply(this.originalContents)));
        }
        
        public void useSuggestion() {
            final Suggestion suggestion2 = (Suggestion)this.suggestions.getList().get(this.current);
            ChatScreen.this.keepSuggestions = true;
            ChatScreen.this.setChatLine(suggestion2.apply(this.originalContents));
            final int integer3 = suggestion2.getRange().getStart() + suggestion2.getText().length();
            ChatScreen.this.input.setCursorPosition(integer3);
            ChatScreen.this.input.setHighlightPos(integer3);
            this.select(this.current);
            ChatScreen.this.keepSuggestions = false;
            this.tabCycles = true;
        }
        
        public void hide() {
            ChatScreen.this.suggestions = null;
        }
    }
}
