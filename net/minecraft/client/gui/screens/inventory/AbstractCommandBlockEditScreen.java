package net.minecraft.client.gui.screens.inventory;

import com.mojang.brigadier.Message;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.world.phys.Vec2;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.gui.Font;
import javax.annotation.Nullable;
import com.mojang.brigadier.suggestion.Suggestion;
import net.minecraft.client.gui.GuiComponent;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import java.util.Collection;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.client.gui.screens.ChatScreen;
import com.mojang.brigadier.tree.CommandNode;
import java.util.Iterator;
import net.minecraft.ChatFormatting;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Map;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import java.util.function.Consumer;
import java.util.function.BiFunction;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.BaseCommandBlock;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.chat.NarratorChatListener;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.SharedSuggestionProvider;
import com.mojang.brigadier.ParseResults;
import java.util.List;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;

public abstract class AbstractCommandBlockEditScreen extends Screen {
    protected EditBox commandEdit;
    protected EditBox previousEdit;
    protected Button doneButton;
    protected Button cancelButton;
    protected Button outputButton;
    protected boolean trackOutput;
    protected final List<String> commandUsage;
    protected int commandUsagePosition;
    protected int commandUsageWidth;
    protected ParseResults<SharedSuggestionProvider> currentParse;
    protected CompletableFuture<Suggestions> pendingSuggestions;
    protected SuggestionsList suggestions;
    private boolean keepSuggestions;
    
    public AbstractCommandBlockEditScreen() {
        super(NarratorChatListener.NO_TITLE);
        this.commandUsage = (List<String>)Lists.newArrayList();
    }
    
    @Override
    public void tick() {
        this.commandEdit.tick();
    }
    
    abstract BaseCommandBlock getCommandBlock();
    
    abstract int getPreviousY();
    
    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.doneButton = this.<Button>addButton(new Button(this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, I18n.get("gui.done"), czi -> this.onDone()));
        this.cancelButton = this.<Button>addButton(new Button(this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, I18n.get("gui.cancel"), czi -> this.onClose()));
        final BaseCommandBlock bgx3;
        this.outputButton = this.<Button>addButton(new Button(this.width / 2 + 150 - 20, this.getPreviousY(), 20, 20, "O", czi -> {
            bgx3 = this.getCommandBlock();
            bgx3.setTrackOutput(!bgx3.isTrackOutput());
            this.updateCommandOutput();
            return;
        }));
        (this.commandEdit = new EditBox(this.font, this.width / 2 - 150, 50, 300, 20, I18n.get("advMode.command"))).setMaxLength(32500);
        this.commandEdit.setFormatter((BiFunction<String, Integer, String>)this::formatChat);
        this.commandEdit.setResponder((Consumer<String>)this::onEdited);
        this.children.add(this.commandEdit);
        (this.previousEdit = new EditBox(this.font, this.width / 2 - 150, this.getPreviousY(), 276, 20, I18n.get("advMode.previousOutput"))).setMaxLength(32500);
        this.previousEdit.setEditable(false);
        this.previousEdit.setValue("-");
        this.children.add(this.previousEdit);
        this.setInitialFocus(this.commandEdit);
        this.commandEdit.setFocus(true);
        this.updateCommandInfo();
    }
    
    @Override
    public void resize(final Minecraft cyc, final int integer2, final int integer3) {
        final String string5 = this.commandEdit.getValue();
        this.init(cyc, integer2, integer3);
        this.setChatLine(string5);
        this.updateCommandInfo();
    }
    
    protected void updateCommandOutput() {
        if (this.getCommandBlock().isTrackOutput()) {
            this.outputButton.setMessage("O");
            this.previousEdit.setValue(this.getCommandBlock().getLastOutput().getString());
        }
        else {
            this.outputButton.setMessage("X");
            this.previousEdit.setValue("-");
        }
    }
    
    protected void onDone() {
        final BaseCommandBlock bgx2 = this.getCommandBlock();
        this.populateAndSendPacket(bgx2);
        if (!bgx2.isTrackOutput()) {
            bgx2.setLastOutput(null);
        }
        this.minecraft.setScreen(null);
    }
    
    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }
    
    protected abstract void populateAndSendPacket(final BaseCommandBlock bgx);
    
    @Override
    public void onClose() {
        this.getCommandBlock().setTrackOutput(this.trackOutput);
        this.minecraft.setScreen(null);
    }
    
    private void onEdited(final String string) {
        this.updateCommandInfo();
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (this.suggestions != null && this.suggestions.keyPressed(integer1, integer2, integer3)) {
            return true;
        }
        if (this.getFocused() == this.commandEdit && integer1 == 258) {
            this.showSuggestions();
            return true;
        }
        if (super.keyPressed(integer1, integer2, integer3)) {
            return true;
        }
        if (integer1 == 257 || integer1 == 335) {
            this.onDone();
            return true;
        }
        if (integer1 == 258 && this.getFocused() == this.commandEdit) {
            this.showSuggestions();
        }
        return false;
    }
    
    @Override
    public boolean mouseScrolled(final double double1, final double double2, final double double3) {
        return (this.suggestions != null && this.suggestions.mouseScrolled(Mth.clamp(double3, -1.0, 1.0))) || super.mouseScrolled(double1, double2, double3);
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        return (this.suggestions != null && this.suggestions.mouseClicked((int)double1, (int)double2, integer)) || super.mouseClicked(double1, double2, integer);
    }
    
    protected void updateCommandInfo() {
        final String string2 = this.commandEdit.getValue();
        if (this.currentParse != null && !this.currentParse.getReader().getString().equals(string2)) {
            this.currentParse = null;
        }
        if (!this.keepSuggestions) {
            this.commandEdit.setSuggestion(null);
            this.suggestions = null;
        }
        this.commandUsage.clear();
        final CommandDispatcher<SharedSuggestionProvider> commandDispatcher3 = this.minecraft.player.connection.getCommands();
        final StringReader stringReader4 = new StringReader(string2);
        if (stringReader4.canRead() && stringReader4.peek() == '/') {
            stringReader4.skip();
        }
        final int integer5 = stringReader4.getCursor();
        if (this.currentParse == null) {
            this.currentParse = (ParseResults<SharedSuggestionProvider>)commandDispatcher3.parse(stringReader4, this.minecraft.player.connection.getSuggestionsProvider());
        }
        final int integer6 = this.commandEdit.getCursorPosition();
        if (integer6 >= integer5 && (this.suggestions == null || !this.keepSuggestions)) {
            (this.pendingSuggestions = (CompletableFuture<Suggestions>)commandDispatcher3.getCompletionSuggestions((ParseResults)this.currentParse, integer6)).thenRun(() -> {
                if (!this.pendingSuggestions.isDone()) {
                    return;
                }
                this.updateUsageInfo();
            });
        }
    }
    
    private void updateUsageInfo() {
        if (((Suggestions)this.pendingSuggestions.join()).isEmpty() && !this.currentParse.getExceptions().isEmpty() && this.commandEdit.getCursorPosition() == this.commandEdit.getValue().length()) {
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
        if (this.minecraft.options.autoSuggestions) {
            this.showSuggestions();
        }
    }
    
    private String formatChat(final String string, final int integer) {
        if (this.currentParse != null) {
            return ChatScreen.formatText(this.currentParse, string, integer);
        }
        return string;
    }
    
    private void fillNodeUsage(final ChatFormatting c) {
        final CommandContextBuilder<SharedSuggestionProvider> commandContextBuilder3 = (CommandContextBuilder<SharedSuggestionProvider>)this.currentParse.getContext();
        final SuggestionContext<SharedSuggestionProvider> suggestionContext4 = (SuggestionContext<SharedSuggestionProvider>)commandContextBuilder3.findSuggestionContext(this.commandEdit.getCursorPosition());
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
            this.commandUsagePosition = Mth.clamp(this.commandEdit.getScreenX(suggestionContext4.startPos), 0, this.commandEdit.getScreenX(0) + this.commandEdit.getInnerWidth() - integer7);
            this.commandUsageWidth = integer7;
        }
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.drawCenteredString(this.font, I18n.get("advMode.setCommand"), this.width / 2, 20, 16777215);
        this.drawString(this.font, I18n.get("advMode.command"), this.width / 2 - 150, 40, 10526880);
        this.commandEdit.render(integer1, integer2, float3);
        int integer3 = 75;
        if (!this.previousEdit.getValue().isEmpty()) {
            final int n = integer3;
            final int n2 = 5;
            this.font.getClass();
            integer3 = n + (n2 * 9 + 1 + this.getPreviousY() - 135);
            this.drawString(this.font, I18n.get("advMode.previousOutput"), this.width / 2 - 150, integer3 + 4, 10526880);
            this.previousEdit.render(integer1, integer2, float3);
        }
        super.render(integer1, integer2, float3);
        if (this.suggestions != null) {
            this.suggestions.render(integer1, integer2);
        }
        else {
            integer3 = 0;
            for (final String string7 : this.commandUsage) {
                GuiComponent.fill(this.commandUsagePosition - 1, 72 + 12 * integer3, this.commandUsagePosition + this.commandUsageWidth + 1, 84 + 12 * integer3, Integer.MIN_VALUE);
                this.font.drawShadow(string7, (float)this.commandUsagePosition, (float)(74 + 12 * integer3), -1);
                ++integer3;
            }
        }
    }
    
    public void showSuggestions() {
        if (this.pendingSuggestions != null && this.pendingSuggestions.isDone()) {
            final Suggestions suggestions2 = (Suggestions)this.pendingSuggestions.join();
            if (!suggestions2.isEmpty()) {
                int integer3 = 0;
                for (final Suggestion suggestion5 : suggestions2.getList()) {
                    integer3 = Math.max(integer3, this.font.width(suggestion5.getText()));
                }
                final int integer4 = Mth.clamp(this.commandEdit.getScreenX(suggestions2.getRange().getStart()), 0, this.commandEdit.getScreenX(0) + this.commandEdit.getInnerWidth() - integer3);
                this.suggestions = new SuggestionsList(integer4, 72, integer3, suggestions2);
            }
        }
    }
    
    protected void setChatLine(final String string) {
        this.commandEdit.setValue(string);
    }
    
    @Nullable
    private static String calculateSuggestionSuffix(final String string1, final String string2) {
        if (string2.startsWith(string1)) {
            return string2.substring(string1.length());
        }
        return null;
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
            this.rect = new Rect2i(integer2 - 1, integer3, integer4 + 1, Math.min(suggestions.getList().size(), 7) * 12);
            this.suggestions = suggestions;
            this.originalContents = AbstractCommandBlockEditScreen.this.commandEdit.getValue();
            this.select(0);
        }
        
        public void render(final int integer1, final int integer2) {
            final int integer3 = Math.min(this.suggestions.getList().size(), 7);
            final int integer4 = Integer.MIN_VALUE;
            final int integer5 = -5592406;
            final boolean boolean7 = this.offset > 0;
            final boolean boolean8 = this.suggestions.getList().size() > this.offset + integer3;
            final boolean boolean9 = boolean7 || boolean8;
            final boolean boolean10 = this.lastMouse.x != integer1 || this.lastMouse.y != integer2;
            if (boolean10) {
                this.lastMouse = new Vec2((float)integer1, (float)integer2);
            }
            if (boolean9) {
                GuiComponent.fill(this.rect.getX(), this.rect.getY() - 1, this.rect.getX() + this.rect.getWidth(), this.rect.getY(), Integer.MIN_VALUE);
                GuiComponent.fill(this.rect.getX(), this.rect.getY() + this.rect.getHeight(), this.rect.getX() + this.rect.getWidth(), this.rect.getY() + this.rect.getHeight() + 1, Integer.MIN_VALUE);
                if (boolean7) {
                    for (int integer6 = 0; integer6 < this.rect.getWidth(); ++integer6) {
                        if (integer6 % 2 == 0) {
                            GuiComponent.fill(this.rect.getX() + integer6, this.rect.getY() - 1, this.rect.getX() + integer6 + 1, this.rect.getY(), -1);
                        }
                    }
                }
                if (boolean8) {
                    for (int integer6 = 0; integer6 < this.rect.getWidth(); ++integer6) {
                        if (integer6 % 2 == 0) {
                            GuiComponent.fill(this.rect.getX() + integer6, this.rect.getY() + this.rect.getHeight(), this.rect.getX() + integer6 + 1, this.rect.getY() + this.rect.getHeight() + 1, -1);
                        }
                    }
                }
            }
            boolean boolean11 = false;
            for (int integer7 = 0; integer7 < integer3; ++integer7) {
                final Suggestion suggestion13 = (Suggestion)this.suggestions.getList().get(integer7 + this.offset);
                GuiComponent.fill(this.rect.getX(), this.rect.getY() + 12 * integer7, this.rect.getX() + this.rect.getWidth(), this.rect.getY() + 12 * integer7 + 12, Integer.MIN_VALUE);
                if (integer1 > this.rect.getX() && integer1 < this.rect.getX() + this.rect.getWidth() && integer2 > this.rect.getY() + 12 * integer7 && integer2 < this.rect.getY() + 12 * integer7 + 12) {
                    if (boolean10) {
                        this.select(integer7 + this.offset);
                    }
                    boolean11 = true;
                }
                AbstractCommandBlockEditScreen.this.font.drawShadow(suggestion13.getText(), (float)(this.rect.getX() + 1), (float)(this.rect.getY() + 2 + 12 * integer7), (integer7 + this.offset == this.current) ? -256 : -5592406);
            }
            if (boolean11) {
                final Message message12 = ((Suggestion)this.suggestions.getList().get(this.current)).getTooltip();
                if (message12 != null) {
                    AbstractCommandBlockEditScreen.this.renderTooltip(ComponentUtils.fromMessage(message12).getColoredString(), integer1, integer2);
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
            final int integer4 = (int)(AbstractCommandBlockEditScreen.this.minecraft.mouseHandler.xpos() * AbstractCommandBlockEditScreen.this.minecraft.window.getGuiScaledWidth() / AbstractCommandBlockEditScreen.this.minecraft.window.getScreenWidth());
            final int integer5 = (int)(AbstractCommandBlockEditScreen.this.minecraft.mouseHandler.ypos() * AbstractCommandBlockEditScreen.this.minecraft.window.getGuiScaledHeight() / AbstractCommandBlockEditScreen.this.minecraft.window.getScreenHeight());
            if (this.rect.contains(integer4, integer5)) {
                this.offset = Mth.clamp((int)(this.offset - double1), 0, Math.max(this.suggestions.getList().size() - 7, 0));
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
            final int integer3 = this.offset + 7 - 1;
            if (this.current < integer2) {
                this.offset = Mth.clamp(this.current, 0, Math.max(this.suggestions.getList().size() - 7, 0));
            }
            else if (this.current > integer3) {
                this.offset = Mth.clamp(this.current - 7, 0, Math.max(this.suggestions.getList().size() - 7, 0));
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
            AbstractCommandBlockEditScreen.this.commandEdit.setSuggestion(calculateSuggestionSuffix(AbstractCommandBlockEditScreen.this.commandEdit.getValue(), suggestion3.apply(this.originalContents)));
        }
        
        public void useSuggestion() {
            final Suggestion suggestion2 = (Suggestion)this.suggestions.getList().get(this.current);
            AbstractCommandBlockEditScreen.this.keepSuggestions = true;
            AbstractCommandBlockEditScreen.this.setChatLine(suggestion2.apply(this.originalContents));
            final int integer3 = suggestion2.getRange().getStart() + suggestion2.getText().length();
            AbstractCommandBlockEditScreen.this.commandEdit.setCursorPosition(integer3);
            AbstractCommandBlockEditScreen.this.commandEdit.setHighlightPos(integer3);
            this.select(this.current);
            AbstractCommandBlockEditScreen.this.keepSuggestions = false;
            this.tabCycles = true;
        }
        
        public void hide() {
            AbstractCommandBlockEditScreen.this.suggestions = null;
        }
    }
}
