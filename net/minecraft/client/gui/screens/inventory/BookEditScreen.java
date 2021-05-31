package net.minecraft.client.gui.screens.inventory;

import net.minecraft.Util;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.gui.GuiComponent;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.util.Mth;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.StringTag;
import java.util.ListIterator;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.client.gui.components.Button;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.gui.screens.Screen;

public class BookEditScreen extends Screen {
    private final Player owner;
    private final ItemStack book;
    private boolean isModified;
    private boolean isSigning;
    private int frameTick;
    private int currentPage;
    private final List<String> pages;
    private String title;
    private int cursorPos;
    private int selectionPos;
    private long lastClickTime;
    private int lastIndex;
    private PageButton forwardButton;
    private PageButton backButton;
    private Button doneButton;
    private Button signButton;
    private Button finalizeButton;
    private Button cancelButton;
    private final InteractionHand hand;
    
    public BookEditScreen(final Player awg, final ItemStack bcj, final InteractionHand ahi) {
        super(NarratorChatListener.NO_TITLE);
        this.pages = (List<String>)Lists.newArrayList();
        this.title = "";
        this.lastIndex = -1;
        this.owner = awg;
        this.book = bcj;
        this.hand = ahi;
        final CompoundTag id5 = bcj.getTag();
        if (id5 != null) {
            final ListTag ik6 = id5.getList("pages", 8).copy();
            for (int integer7 = 0; integer7 < ik6.size(); ++integer7) {
                this.pages.add(ik6.getString(integer7));
            }
        }
        if (this.pages.isEmpty()) {
            this.pages.add("");
        }
    }
    
    private int getNumPages() {
        return this.pages.size();
    }
    
    @Override
    public void tick() {
        super.tick();
        ++this.frameTick;
    }
    
    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.signButton = this.<Button>addButton(new Button(this.width / 2 - 100, 196, 98, 20, I18n.get("book.signButton"), czi -> {
            this.isSigning = true;
            this.updateButtonVisibility();
            return;
        }));
        this.doneButton = this.<Button>addButton(new Button(this.width / 2 + 2, 196, 98, 20, I18n.get("gui.done"), czi -> {
            this.minecraft.setScreen(null);
            this.saveChanges(false);
            return;
        }));
        this.finalizeButton = this.<Button>addButton(new Button(this.width / 2 - 100, 196, 98, 20, I18n.get("book.finalizeButton"), czi -> {
            if (this.isSigning) {
                this.saveChanges(true);
                this.minecraft.setScreen(null);
            }
            return;
        }));
        this.cancelButton = this.<Button>addButton(new Button(this.width / 2 + 2, 196, 98, 20, I18n.get("gui.cancel"), czi -> {
            if (this.isSigning) {
                this.isSigning = false;
            }
            this.updateButtonVisibility();
            return;
        }));
        final int integer2 = (this.width - 192) / 2;
        final int integer3 = 2;
        this.forwardButton = this.<PageButton>addButton(new PageButton(integer2 + 116, 159, true, czi -> this.pageForward(), true));
        this.backButton = this.<PageButton>addButton(new PageButton(integer2 + 43, 159, false, czi -> this.pageBack(), true));
        this.updateButtonVisibility();
    }
    
    private String filterText(final String string) {
        final StringBuilder stringBuilder3 = new StringBuilder();
        for (final char character7 : string.toCharArray()) {
            if (character7 != '§' && character7 != '\u007f') {
                stringBuilder3.append(character7);
            }
        }
        return stringBuilder3.toString();
    }
    
    private void pageBack() {
        if (this.currentPage > 0) {
            --this.currentPage;
            this.cursorPos = 0;
            this.selectionPos = this.cursorPos;
        }
        this.updateButtonVisibility();
    }
    
    private void pageForward() {
        if (this.currentPage < this.getNumPages() - 1) {
            ++this.currentPage;
            this.cursorPos = 0;
            this.selectionPos = this.cursorPos;
        }
        else {
            this.appendPageToBook();
            if (this.currentPage < this.getNumPages() - 1) {
                ++this.currentPage;
            }
            this.cursorPos = 0;
            this.selectionPos = this.cursorPos;
        }
        this.updateButtonVisibility();
    }
    
    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }
    
    private void updateButtonVisibility() {
        this.backButton.visible = (!this.isSigning && this.currentPage > 0);
        this.forwardButton.visible = !this.isSigning;
        this.doneButton.visible = !this.isSigning;
        this.signButton.visible = !this.isSigning;
        this.cancelButton.visible = this.isSigning;
        this.finalizeButton.visible = this.isSigning;
        this.finalizeButton.active = !this.title.trim().isEmpty();
    }
    
    private void eraseEmptyTrailingPages() {
        final ListIterator<String> listIterator2 = (ListIterator<String>)this.pages.listIterator(this.pages.size());
        while (listIterator2.hasPrevious() && ((String)listIterator2.previous()).isEmpty()) {
            listIterator2.remove();
        }
    }
    
    private void saveChanges(final boolean boolean1) {
        if (!this.isModified) {
            return;
        }
        this.eraseEmptyTrailingPages();
        final ListTag ik3 = new ListTag();
        this.pages.stream().map(StringTag::new).forEach(ik3::add);
        if (!this.pages.isEmpty()) {
            this.book.addTagElement("pages", (Tag)ik3);
        }
        if (boolean1) {
            this.book.addTagElement("author", (Tag)new StringTag(this.owner.getGameProfile().getName()));
            this.book.addTagElement("title", (Tag)new StringTag(this.title.trim()));
        }
        this.minecraft.getConnection().send(new ServerboundEditBookPacket(this.book, boolean1, this.hand));
    }
    
    private void appendPageToBook() {
        if (this.getNumPages() >= 100) {
            return;
        }
        this.pages.add("");
        this.isModified = true;
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (super.keyPressed(integer1, integer2, integer3)) {
            return true;
        }
        if (this.isSigning) {
            return this.titleKeyPressed(integer1, integer2, integer3);
        }
        return this.bookKeyPressed(integer1, integer2, integer3);
    }
    
    @Override
    public boolean charTyped(final char character, final int integer) {
        if (super.charTyped(character, integer)) {
            return true;
        }
        if (this.isSigning) {
            if (this.title.length() < 16 && SharedConstants.isAllowedChatCharacter(character)) {
                this.title += Character.toString(character);
                this.updateButtonVisibility();
                return this.isModified = true;
            }
            return false;
        }
        else {
            if (SharedConstants.isAllowedChatCharacter(character)) {
                this.insertText(Character.toString(character));
                return true;
            }
            return false;
        }
    }
    
    private boolean bookKeyPressed(final int integer1, final int integer2, final int integer3) {
        final String string5 = this.getCurrentPageText();
        if (Screen.isSelectAll(integer1)) {
            this.selectionPos = 0;
            this.cursorPos = string5.length();
            return true;
        }
        if (Screen.isCopy(integer1)) {
            this.minecraft.keyboardHandler.setClipboard(this.getSelected());
            return true;
        }
        if (Screen.isPaste(integer1)) {
            this.insertText(this.filterText(ChatFormatting.stripFormatting(this.minecraft.keyboardHandler.getClipboard().replaceAll("\\r", ""))));
            this.selectionPos = this.cursorPos;
            return true;
        }
        if (Screen.isCut(integer1)) {
            this.minecraft.keyboardHandler.setClipboard(this.getSelected());
            this.deleteSelection();
            return true;
        }
        switch (integer1) {
            case 259: {
                this.keyBackspace(string5);
                return true;
            }
            case 261: {
                this.keyDelete(string5);
                return true;
            }
            case 257:
            case 335: {
                this.insertText("\n");
                return true;
            }
            case 263: {
                this.keyLeft(string5);
                return true;
            }
            case 262: {
                this.keyRight(string5);
                return true;
            }
            case 265: {
                this.keyUp(string5);
                return true;
            }
            case 264: {
                this.keyDown(string5);
                return true;
            }
            case 266: {
                this.backButton.onPress();
                return true;
            }
            case 267: {
                this.forwardButton.onPress();
                return true;
            }
            case 268: {
                this.keyHome(string5);
                return true;
            }
            case 269: {
                this.keyEnd(string5);
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private void keyBackspace(final String string) {
        if (!string.isEmpty()) {
            if (this.selectionPos != this.cursorPos) {
                this.deleteSelection();
            }
            else if (this.cursorPos > 0) {
                final String string2 = new StringBuilder(string).deleteCharAt(Math.max(0, this.cursorPos - 1)).toString();
                this.setCurrentPageText(string2);
                this.cursorPos = Math.max(0, this.cursorPos - 1);
                this.selectionPos = this.cursorPos;
            }
        }
    }
    
    private void keyDelete(final String string) {
        if (!string.isEmpty()) {
            if (this.selectionPos != this.cursorPos) {
                this.deleteSelection();
            }
            else if (this.cursorPos < string.length()) {
                final String string2 = new StringBuilder(string).deleteCharAt(Math.max(0, this.cursorPos)).toString();
                this.setCurrentPageText(string2);
            }
        }
    }
    
    private void keyLeft(final String string) {
        final int integer3 = this.font.isBidirectional() ? 1 : -1;
        if (Screen.hasControlDown()) {
            this.cursorPos = this.font.getWordPosition(string, integer3, this.cursorPos, true);
        }
        else {
            this.cursorPos = Math.max(0, this.cursorPos + integer3);
        }
        if (!Screen.hasShiftDown()) {
            this.selectionPos = this.cursorPos;
        }
    }
    
    private void keyRight(final String string) {
        final int integer3 = this.font.isBidirectional() ? -1 : 1;
        if (Screen.hasControlDown()) {
            this.cursorPos = this.font.getWordPosition(string, integer3, this.cursorPos, true);
        }
        else {
            this.cursorPos = Math.min(string.length(), this.cursorPos + integer3);
        }
        if (!Screen.hasShiftDown()) {
            this.selectionPos = this.cursorPos;
        }
    }
    
    private void keyUp(final String string) {
        if (!string.isEmpty()) {
            final Pos2i a3 = this.getPositionAtIndex(string, this.cursorPos);
            if (a3.y == 0) {
                this.cursorPos = 0;
                if (!Screen.hasShiftDown()) {
                    this.selectionPos = this.cursorPos;
                }
            }
            else {
                final int integer5 = a3.x + this.getWidthAt(string, this.cursorPos) / 3;
                final int access$000 = a3.y;
                this.font.getClass();
                final int integer4 = this.getIndexAtPosition(string, new Pos2i(integer5, access$000 - 9));
                if (integer4 >= 0) {
                    this.cursorPos = integer4;
                    if (!Screen.hasShiftDown()) {
                        this.selectionPos = this.cursorPos;
                    }
                }
            }
        }
    }
    
    private void keyDown(final String string) {
        if (!string.isEmpty()) {
            final Pos2i a3 = this.getPositionAtIndex(string, this.cursorPos);
            final int integer4 = this.font.wordWrapHeight(string + "" + ChatFormatting.BLACK + "_", 114);
            final int access$000 = a3.y;
            this.font.getClass();
            if (access$000 + 9 == integer4) {
                this.cursorPos = string.length();
                if (!Screen.hasShiftDown()) {
                    this.selectionPos = this.cursorPos;
                }
            }
            else {
                final int integer6 = a3.x + this.getWidthAt(string, this.cursorPos) / 3;
                final int access$2 = a3.y;
                this.font.getClass();
                final int integer5 = this.getIndexAtPosition(string, new Pos2i(integer6, access$2 + 9));
                if (integer5 >= 0) {
                    this.cursorPos = integer5;
                    if (!Screen.hasShiftDown()) {
                        this.selectionPos = this.cursorPos;
                    }
                }
            }
        }
    }
    
    private void keyHome(final String string) {
        this.cursorPos = this.getIndexAtPosition(string, new Pos2i(0, this.getPositionAtIndex(string, this.cursorPos).y));
        if (!Screen.hasShiftDown()) {
            this.selectionPos = this.cursorPos;
        }
    }
    
    private void keyEnd(final String string) {
        this.cursorPos = this.getIndexAtPosition(string, new Pos2i(113, this.getPositionAtIndex(string, this.cursorPos).y));
        if (!Screen.hasShiftDown()) {
            this.selectionPos = this.cursorPos;
        }
    }
    
    private void deleteSelection() {
        if (this.selectionPos == this.cursorPos) {
            return;
        }
        final String string2 = this.getCurrentPageText();
        final int integer3 = Math.min(this.cursorPos, this.selectionPos);
        final int integer4 = Math.max(this.cursorPos, this.selectionPos);
        final String string3 = string2.substring(0, integer3) + string2.substring(integer4);
        this.cursorPos = integer3;
        this.selectionPos = this.cursorPos;
        this.setCurrentPageText(string3);
    }
    
    private int getWidthAt(final String string, final int integer) {
        return (int)this.font.charWidth(string.charAt(Mth.clamp(integer, 0, string.length() - 1)));
    }
    
    private boolean titleKeyPressed(final int integer1, final int integer2, final int integer3) {
        switch (integer1) {
            case 259: {
                if (!this.title.isEmpty()) {
                    this.title = this.title.substring(0, this.title.length() - 1);
                    this.updateButtonVisibility();
                }
                return true;
            }
            case 257:
            case 335: {
                if (!this.title.isEmpty()) {
                    this.saveChanges(true);
                    this.minecraft.setScreen(null);
                }
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private String getCurrentPageText() {
        if (this.currentPage >= 0 && this.currentPage < this.pages.size()) {
            return (String)this.pages.get(this.currentPage);
        }
        return "";
    }
    
    private void setCurrentPageText(final String string) {
        if (this.currentPage >= 0 && this.currentPage < this.pages.size()) {
            this.pages.set(this.currentPage, string);
            this.isModified = true;
        }
    }
    
    private void insertText(final String string) {
        if (this.selectionPos != this.cursorPos) {
            this.deleteSelection();
        }
        final String string2 = this.getCurrentPageText();
        this.cursorPos = Mth.clamp(this.cursorPos, 0, string2.length());
        final String string3 = new StringBuilder(string2).insert(this.cursorPos, string).toString();
        final int integer5 = this.font.wordWrapHeight(string3 + "" + ChatFormatting.BLACK + "_", 114);
        if (integer5 <= 128 && string3.length() < 1024) {
            this.setCurrentPageText(string3);
            final int min = Math.min(this.getCurrentPageText().length(), this.cursorPos + string.length());
            this.cursorPos = min;
            this.selectionPos = min;
        }
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.setFocused(null);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(BookViewScreen.BOOK_LOCATION);
        final int integer3 = (this.width - 192) / 2;
        final int integer4 = 2;
        this.blit(integer3, 2, 0, 0, 192, 192);
        if (this.isSigning) {
            String string7 = this.title;
            if (this.frameTick / 6 % 2 == 0) {
                string7 = string7 + "" + ChatFormatting.BLACK + "_";
            }
            else {
                string7 = string7 + "" + ChatFormatting.GRAY + "_";
            }
            final String string8 = I18n.get("book.editTitle");
            final int integer5 = this.strWidth(string8);
            this.font.draw(string8, (float)(integer3 + 36 + (114 - integer5) / 2), 34.0f, 0);
            final int integer6 = this.strWidth(string7);
            this.font.draw(string7, (float)(integer3 + 36 + (114 - integer6) / 2), 50.0f, 0);
            final String string9 = I18n.get("book.byAuthor", this.owner.getName().getString());
            final int integer7 = this.strWidth(string9);
            this.font.draw(ChatFormatting.DARK_GRAY + string9, (float)(integer3 + 36 + (114 - integer7) / 2), 60.0f, 0);
            final String string10 = I18n.get("book.finalizeWarning");
            this.font.drawWordWrap(string10, integer3 + 36, 82, 114, 0);
        }
        else {
            final String string7 = I18n.get("book.pageIndicator", this.currentPage + 1, this.getNumPages());
            final String string8 = this.getCurrentPageText();
            final int integer5 = this.strWidth(string7);
            this.font.draw(string7, (float)(integer3 - integer5 + 192 - 44), 18.0f, 0);
            this.font.drawWordWrap(string8, integer3 + 36, 32, 114, 0);
            this.renderSelection(string8);
            if (this.frameTick / 6 % 2 == 0) {
                final Pos2i a10 = this.getPositionAtIndex(string8, this.cursorPos);
                if (this.font.isBidirectional()) {
                    this.handleBidi(a10);
                    a10.x -= 4;
                }
                this.convertLocalToScreen(a10);
                if (this.cursorPos < string8.length()) {
                    final int access$100 = a10.x;
                    final int integer8 = a10.y - 1;
                    final int integer9 = a10.x + 1;
                    final int access$101 = a10.y;
                    this.font.getClass();
                    GuiComponent.fill(access$100, integer8, integer9, access$101 + 9, -16777216);
                }
                else {
                    this.font.draw("_", (float)a10.x, (float)a10.y, 0);
                }
            }
        }
        super.render(integer1, integer2, float3);
    }
    
    private int strWidth(final String string) {
        return this.font.width(this.font.isBidirectional() ? this.font.bidirectionalShaping(string) : string);
    }
    
    private int strIndexAtWidth(final String string, final int integer) {
        return this.font.indexAtWidth(string, integer);
    }
    
    private String getSelected() {
        final String string2 = this.getCurrentPageText();
        final int integer3 = Math.min(this.cursorPos, this.selectionPos);
        final int integer4 = Math.max(this.cursorPos, this.selectionPos);
        return string2.substring(integer3, integer4);
    }
    
    private void renderSelection(final String string) {
        if (this.selectionPos == this.cursorPos) {
            return;
        }
        final int integer3 = Math.min(this.cursorPos, this.selectionPos);
        final int integer4 = Math.max(this.cursorPos, this.selectionPos);
        String string2 = string.substring(integer3, integer4);
        final int integer5 = this.font.getWordPosition(string, 1, integer4, true);
        String string3 = string.substring(integer3, integer5);
        final Pos2i a8 = this.getPositionAtIndex(string, integer3);
        final int access$100 = a8.x;
        final int access$101 = a8.y;
        this.font.getClass();
        final Pos2i a9 = new Pos2i(access$100, access$101 + 9);
        while (!string2.isEmpty()) {
            int integer6 = this.strIndexAtWidth(string3, 114 - a8.x);
            if (string2.length() <= integer6) {
                a9.x = a8.x + this.strWidth(string2);
                this.renderHighlight(a8, a9);
                break;
            }
            integer6 = Math.min(integer6, string2.length() - 1);
            final String string4 = string2.substring(0, integer6);
            final char character12 = string2.charAt(integer6);
            final boolean boolean13 = character12 == ' ' || character12 == '\n';
            string2 = ChatFormatting.getLastColors(string4) + string2.substring(integer6 + (boolean13 ? 1 : 0));
            string3 = ChatFormatting.getLastColors(string4) + string3.substring(integer6 + (boolean13 ? 1 : 0));
            a9.x = a8.x + this.strWidth(string4 + " ");
            this.renderHighlight(a8, a9);
            a8.x = 0;
            final Pos2i a10 = a8;
            final int access$102 = a8.y;
            this.font.getClass();
            a10.y = access$102 + 9;
            final Pos2i a11 = a9;
            final int access$103 = a9.y;
            this.font.getClass();
            a11.y = access$103 + 9;
        }
    }
    
    private void renderHighlight(final Pos2i a1, final Pos2i a2) {
        final Pos2i a3 = new Pos2i(a1.x, a1.y);
        final Pos2i a4 = new Pos2i(a2.x, a2.y);
        if (this.font.isBidirectional()) {
            this.handleBidi(a3);
            this.handleBidi(a4);
            final int integer6 = a4.x;
            a4.x = a3.x;
            a3.x = integer6;
        }
        this.convertLocalToScreen(a3);
        this.convertLocalToScreen(a4);
        final Tesselator cuz6 = Tesselator.getInstance();
        final BufferBuilder cuw7 = cuz6.getBuilder();
        GlStateManager.color4f(0.0f, 0.0f, 255.0f, 255.0f);
        GlStateManager.disableTexture();
        GlStateManager.enableColorLogicOp();
        GlStateManager.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        cuw7.begin(7, DefaultVertexFormat.POSITION);
        cuw7.vertex(a3.x, a4.y, 0.0).endVertex();
        cuw7.vertex(a4.x, a4.y, 0.0).endVertex();
        cuw7.vertex(a4.x, a3.y, 0.0).endVertex();
        cuw7.vertex(a3.x, a3.y, 0.0).endVertex();
        cuz6.end();
        GlStateManager.disableColorLogicOp();
        GlStateManager.enableTexture();
    }
    
    private Pos2i getPositionAtIndex(final String string, final int integer) {
        final Pos2i a4 = new Pos2i();
        int integer2 = 0;
        int integer3 = 0;
        String string2 = string;
        while (!string2.isEmpty()) {
            final int integer4 = this.strIndexAtWidth(string2, 114);
            if (string2.length() <= integer4) {
                final String string3 = string2.substring(0, Math.min(Math.max(integer - integer3, 0), string2.length()));
                a4.x += this.strWidth(string3);
                break;
            }
            final String string3 = string2.substring(0, integer4);
            final char character10 = string2.charAt(integer4);
            final boolean boolean11 = character10 == ' ' || character10 == '\n';
            string2 = ChatFormatting.getLastColors(string3) + string2.substring(integer4 + (boolean11 ? 1 : 0));
            integer2 += string3.length() + (boolean11 ? 1 : 0);
            if (integer2 - 1 >= integer) {
                final String string4 = string3.substring(0, Math.min(Math.max(integer - integer3, 0), string3.length()));
                a4.x += this.strWidth(string4);
                break;
            }
            final Pos2i a5 = a4;
            final int access$000 = a4.y;
            this.font.getClass();
            a5.y = access$000 + 9;
            integer3 = integer2;
        }
        return a4;
    }
    
    private void handleBidi(final Pos2i a) {
        if (this.font.isBidirectional()) {
            a.x = 114 - a.x;
        }
    }
    
    private void convertScreenToLocal(final Pos2i a) {
        a.x = a.x - (this.width - 192) / 2 - 36;
        a.y -= 32;
    }
    
    private void convertLocalToScreen(final Pos2i a) {
        a.x = a.x + (this.width - 192) / 2 + 36;
        a.y += 32;
    }
    
    private int indexInLine(final String string, final int integer) {
        if (integer < 0) {
            return 0;
        }
        float float5 = 0.0f;
        boolean boolean6 = false;
        final String string2 = string + " ";
        for (int integer2 = 0; integer2 < string2.length(); ++integer2) {
            char character9 = string2.charAt(integer2);
            float float6 = this.font.charWidth(character9);
            if (character9 == '§' && integer2 < string2.length() - 1) {
                character9 = string2.charAt(++integer2);
                if (character9 == 'l' || character9 == 'L') {
                    boolean6 = true;
                }
                else if (character9 == 'r' || character9 == 'R') {
                    boolean6 = false;
                }
                float6 = 0.0f;
            }
            final float float7 = float5;
            float5 += float6;
            if (boolean6 && float6 > 0.0f) {
                ++float5;
            }
            if (integer >= float7 && integer < float5) {
                return integer2;
            }
        }
        if (integer >= float5) {
            return string2.length() - 1;
        }
        return -1;
    }
    
    private int getIndexAtPosition(final String string, final Pos2i a) {
        final int n = 16;
        this.font.getClass();
        final int integer4 = n * 9;
        if (a.y > integer4) {
            return -1;
        }
        int integer5 = Integer.MIN_VALUE;
        this.font.getClass();
        int integer6 = 9;
        int integer7 = 0;
        String string2 = string;
        while (!string2.isEmpty() && integer5 < integer4) {
            final int integer8 = this.strIndexAtWidth(string2, 114);
            if (integer8 < string2.length()) {
                final String string3 = string2.substring(0, integer8);
                if (a.y >= integer5 && a.y < integer6) {
                    final int integer9 = this.indexInLine(string3, a.x);
                    return (integer9 < 0) ? -1 : (integer7 + integer9);
                }
                final char character11 = string2.charAt(integer8);
                final boolean boolean12 = character11 == ' ' || character11 == '\n';
                string2 = ChatFormatting.getLastColors(string3) + string2.substring(integer8 + (boolean12 ? 1 : 0));
                integer7 += string3.length() + (boolean12 ? 1 : 0);
            }
            else if (a.y >= integer5 && a.y < integer6) {
                final int integer10 = this.indexInLine(string2, a.x);
                return (integer10 < 0) ? -1 : (integer7 + integer10);
            }
            integer5 = integer6;
            final int n2 = integer6;
            this.font.getClass();
            integer6 = n2 + 9;
        }
        return string.length();
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        if (integer == 0) {
            final long long7 = Util.getMillis();
            final String string9 = this.getCurrentPageText();
            if (!string9.isEmpty()) {
                final Pos2i a10 = new Pos2i((int)double1, (int)double2);
                this.convertScreenToLocal(a10);
                this.handleBidi(a10);
                final int integer2 = this.getIndexAtPosition(string9, a10);
                if (integer2 >= 0) {
                    if (integer2 == this.lastIndex && long7 - this.lastClickTime < 250L) {
                        if (this.selectionPos == this.cursorPos) {
                            this.selectionPos = this.font.getWordPosition(string9, -1, integer2, false);
                            this.cursorPos = this.font.getWordPosition(string9, 1, integer2, false);
                        }
                        else {
                            this.selectionPos = 0;
                            this.cursorPos = this.getCurrentPageText().length();
                        }
                    }
                    else {
                        this.cursorPos = integer2;
                        if (!Screen.hasShiftDown()) {
                            this.selectionPos = this.cursorPos;
                        }
                    }
                }
                this.lastIndex = integer2;
            }
            this.lastClickTime = long7;
        }
        return super.mouseClicked(double1, double2, integer);
    }
    
    @Override
    public boolean mouseDragged(final double double1, final double double2, final int integer, final double double4, final double double5) {
        if (integer == 0 && this.currentPage >= 0 && this.currentPage < this.pages.size()) {
            final String string11 = (String)this.pages.get(this.currentPage);
            final Pos2i a12 = new Pos2i((int)double1, (int)double2);
            this.convertScreenToLocal(a12);
            this.handleBidi(a12);
            final int integer2 = this.getIndexAtPosition(string11, a12);
            if (integer2 >= 0) {
                this.cursorPos = integer2;
            }
        }
        return super.mouseDragged(double1, double2, integer, double4, double5);
    }
    
    class Pos2i {
        private int x;
        private int y;
        
        Pos2i() {
        }
        
        Pos2i(final int integer2, final int integer3) {
            this.x = integer2;
            this.y = integer3;
        }
    }
}
