package net.minecraft.client.gui.font;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import net.minecraft.SharedConstants;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.Font;
import net.minecraft.client.Minecraft;

public class TextFieldHelper {
    private final Minecraft minecraft;
    private final Font font;
    private final Supplier<String> getMessageFn;
    private final Consumer<String> setMessageFn;
    private final int maxWidth;
    private int cursorPos;
    private int selectionPos;
    
    public TextFieldHelper(final Minecraft cyc, final Supplier<String> supplier, final Consumer<String> consumer, final int integer) {
        this.minecraft = cyc;
        this.font = cyc.font;
        this.getMessageFn = supplier;
        this.setMessageFn = consumer;
        this.maxWidth = integer;
        this.setEnd();
    }
    
    public boolean charTyped(final char character) {
        if (SharedConstants.isAllowedChatCharacter(character)) {
            this.insertText(Character.toString(character));
        }
        return true;
    }
    
    private void insertText(final String string) {
        if (this.selectionPos != this.cursorPos) {
            this.deleteSelection();
        }
        final String string2 = (String)this.getMessageFn.get();
        this.cursorPos = Mth.clamp(this.cursorPos, 0, string2.length());
        final String string3 = new StringBuilder(string2).insert(this.cursorPos, string).toString();
        if (this.font.width(string3) <= this.maxWidth) {
            this.setMessageFn.accept(string3);
            final int min = Math.min(string3.length(), this.cursorPos + string.length());
            this.cursorPos = min;
            this.selectionPos = min;
        }
    }
    
    public boolean keyPressed(final int integer) {
        String string3 = (String)this.getMessageFn.get();
        if (Screen.isSelectAll(integer)) {
            this.selectionPos = 0;
            this.cursorPos = string3.length();
            return true;
        }
        if (Screen.isCopy(integer)) {
            this.minecraft.keyboardHandler.setClipboard(this.getSelected());
            return true;
        }
        if (Screen.isPaste(integer)) {
            this.insertText(SharedConstants.filterText(ChatFormatting.stripFormatting(this.minecraft.keyboardHandler.getClipboard().replaceAll("\\r", ""))));
            this.selectionPos = this.cursorPos;
            return true;
        }
        if (Screen.isCut(integer)) {
            this.minecraft.keyboardHandler.setClipboard(this.getSelected());
            this.deleteSelection();
            return true;
        }
        if (integer == 259) {
            if (!string3.isEmpty()) {
                if (this.selectionPos != this.cursorPos) {
                    this.deleteSelection();
                }
                else if (this.cursorPos > 0) {
                    string3 = new StringBuilder(string3).deleteCharAt(Math.max(0, this.cursorPos - 1)).toString();
                    final int max = Math.max(0, this.cursorPos - 1);
                    this.cursorPos = max;
                    this.selectionPos = max;
                    this.setMessageFn.accept(string3);
                }
            }
            return true;
        }
        if (integer == 261) {
            if (!string3.isEmpty()) {
                if (this.selectionPos != this.cursorPos) {
                    this.deleteSelection();
                }
                else if (this.cursorPos < string3.length()) {
                    string3 = new StringBuilder(string3).deleteCharAt(Math.max(0, this.cursorPos)).toString();
                    this.setMessageFn.accept(string3);
                }
            }
            return true;
        }
        if (integer == 263) {
            final int integer2 = this.font.isBidirectional() ? 1 : -1;
            if (Screen.hasControlDown()) {
                this.cursorPos = this.font.getWordPosition(string3, integer2, this.cursorPos, true);
            }
            else {
                this.cursorPos = Math.max(0, Math.min(string3.length(), this.cursorPos + integer2));
            }
            if (!Screen.hasShiftDown()) {
                this.selectionPos = this.cursorPos;
            }
            return true;
        }
        if (integer == 262) {
            final int integer2 = this.font.isBidirectional() ? -1 : 1;
            if (Screen.hasControlDown()) {
                this.cursorPos = this.font.getWordPosition(string3, integer2, this.cursorPos, true);
            }
            else {
                this.cursorPos = Math.max(0, Math.min(string3.length(), this.cursorPos + integer2));
            }
            if (!Screen.hasShiftDown()) {
                this.selectionPos = this.cursorPos;
            }
            return true;
        }
        if (integer == 268) {
            this.cursorPos = 0;
            if (!Screen.hasShiftDown()) {
                this.selectionPos = this.cursorPos;
            }
            return true;
        }
        if (integer == 269) {
            this.cursorPos = ((String)this.getMessageFn.get()).length();
            if (!Screen.hasShiftDown()) {
                this.selectionPos = this.cursorPos;
            }
            return true;
        }
        return false;
    }
    
    private String getSelected() {
        final String string2 = (String)this.getMessageFn.get();
        final int integer3 = Math.min(this.cursorPos, this.selectionPos);
        final int integer4 = Math.max(this.cursorPos, this.selectionPos);
        return string2.substring(integer3, integer4);
    }
    
    private void deleteSelection() {
        if (this.selectionPos == this.cursorPos) {
            return;
        }
        final String string2 = (String)this.getMessageFn.get();
        final int integer3 = Math.min(this.cursorPos, this.selectionPos);
        final int integer4 = Math.max(this.cursorPos, this.selectionPos);
        final String string3 = string2.substring(0, integer3) + string2.substring(integer4);
        this.cursorPos = integer3;
        this.selectionPos = this.cursorPos;
        this.setMessageFn.accept(string3);
    }
    
    public void setEnd() {
        final int length = ((String)this.getMessageFn.get()).length();
        this.cursorPos = length;
        this.selectionPos = length;
    }
    
    public int getCursorPos() {
        return this.cursorPos;
    }
    
    public int getSelectionPos() {
        return this.selectionPos;
    }
}
