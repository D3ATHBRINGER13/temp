package net.minecraft.world.level.block.entity;

import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import javax.annotation.Nullable;
import java.util.function.Function;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;

public class SignBlockEntity extends BlockEntity {
    public final Component[] messages;
    private boolean showCursor;
    private int selectedLine;
    private int cursorPos;
    private int selectionPos;
    private boolean isEditable;
    private Player playerWhoMayEdit;
    private final String[] renderMessages;
    private DyeColor color;
    
    public SignBlockEntity() {
        super(BlockEntityType.SIGN);
        this.messages = new Component[] { new TextComponent(""), new TextComponent(""), new TextComponent(""), new TextComponent("") };
        this.selectedLine = -1;
        this.cursorPos = -1;
        this.selectionPos = -1;
        this.isEditable = true;
        this.renderMessages = new String[4];
        this.color = DyeColor.BLACK;
    }
    
    @Override
    public CompoundTag save(final CompoundTag id) {
        super.save(id);
        for (int integer3 = 0; integer3 < 4; ++integer3) {
            final String string4 = Component.Serializer.toJson(this.messages[integer3]);
            id.putString(new StringBuilder().append("Text").append(integer3 + 1).toString(), string4);
        }
        id.putString("Color", this.color.getName());
        return id;
    }
    
    @Override
    public void load(final CompoundTag id) {
        this.isEditable = false;
        super.load(id);
        this.color = DyeColor.byName(id.getString("Color"), DyeColor.BLACK);
        for (int integer3 = 0; integer3 < 4; ++integer3) {
            final String string4 = id.getString(new StringBuilder().append("Text").append(integer3 + 1).toString());
            final Component jo5 = Component.Serializer.fromJson(string4.isEmpty() ? "\"\"" : string4);
            if (this.level instanceof ServerLevel) {
                try {
                    this.messages[integer3] = ComponentUtils.updateForEntity(this.createCommandSourceStack(null), jo5, null, 0);
                }
                catch (CommandSyntaxException commandSyntaxException6) {
                    this.messages[integer3] = jo5;
                }
            }
            else {
                this.messages[integer3] = jo5;
            }
            this.renderMessages[integer3] = null;
        }
    }
    
    public Component getMessage(final int integer) {
        return this.messages[integer];
    }
    
    public void setMessage(final int integer, final Component jo) {
        this.messages[integer] = jo;
        this.renderMessages[integer] = null;
    }
    
    @Nullable
    public String getRenderMessage(final int integer, final Function<Component, String> function) {
        if (this.renderMessages[integer] == null && this.messages[integer] != null) {
            this.renderMessages[integer] = (String)function.apply(this.messages[integer]);
        }
        return this.renderMessages[integer];
    }
    
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 9, this.getUpdateTag());
    }
    
    @Override
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }
    
    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }
    
    public boolean isEditable() {
        return this.isEditable;
    }
    
    public void setEditable(final boolean boolean1) {
        if (!(this.isEditable = boolean1)) {
            this.playerWhoMayEdit = null;
        }
    }
    
    public void setAllowedPlayerEditor(final Player awg) {
        this.playerWhoMayEdit = awg;
    }
    
    public Player getPlayerWhoMayEdit() {
        return this.playerWhoMayEdit;
    }
    
    public boolean executeClickCommands(final Player awg) {
        for (final Component jo6 : this.messages) {
            final Style jw7 = (jo6 == null) ? null : jo6.getStyle();
            if (jw7 != null) {
                if (jw7.getClickEvent() != null) {
                    final ClickEvent jn8 = jw7.getClickEvent();
                    if (jn8.getAction() == ClickEvent.Action.RUN_COMMAND) {
                        awg.getServer().getCommands().performCommand(this.createCommandSourceStack((ServerPlayer)awg), jn8.getValue());
                    }
                }
            }
        }
        return true;
    }
    
    public CommandSourceStack createCommandSourceStack(@Nullable final ServerPlayer vl) {
        final String string3 = (vl == null) ? "Sign" : vl.getName().getString();
        final Component jo4 = (vl == null) ? new TextComponent("Sign") : vl.getDisplayName();
        return new CommandSourceStack(CommandSource.NULL, new Vec3(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5), Vec2.ZERO, (ServerLevel)this.level, 2, string3, jo4, this.level.getServer(), vl);
    }
    
    public DyeColor getColor() {
        return this.color;
    }
    
    public boolean setColor(final DyeColor bbg) {
        if (bbg != this.getColor()) {
            this.color = bbg;
            this.setChanged();
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
            return true;
        }
        return false;
    }
    
    public void setCursorInfo(final int integer1, final int integer2, final int integer3, final boolean boolean4) {
        this.selectedLine = integer1;
        this.cursorPos = integer2;
        this.selectionPos = integer3;
        this.showCursor = boolean4;
    }
    
    public void resetCursorInfo() {
        this.selectedLine = -1;
        this.cursorPos = -1;
        this.selectionPos = -1;
        this.showCursor = false;
    }
    
    public boolean isShowCursor() {
        return this.showCursor;
    }
    
    public int getSelectedLine() {
        return this.selectedLine;
    }
    
    public int getCursorPos() {
        return this.cursorPos;
    }
    
    public int getSelectionPos() {
        return this.selectionPos;
    }
}
