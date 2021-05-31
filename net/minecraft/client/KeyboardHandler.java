package net.minecraft.client;

import net.minecraft.ReportedException;
import net.minecraft.CrashReport;
import com.mojang.blaze3d.Blaze3D;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.screens.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.ChatOptionsScreen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Registry;
import net.minecraft.world.phys.EntityHitResult;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.world.level.dimension.DimensionType;
import java.util.Locale;
import net.minecraft.util.Mth;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.TextComponent;
import com.mojang.blaze3d.platform.ClipboardManager;

public class KeyboardHandler {
    private final Minecraft minecraft;
    private boolean sendRepeatsToGui;
    private final ClipboardManager clipboardManager;
    private long debugCrashKeyTime;
    private long debugCrashKeyReportedTime;
    private long debugCrashKeyReportedCount;
    private boolean handledDebugKey;
    
    public KeyboardHandler(final Minecraft cyc) {
        this.clipboardManager = new ClipboardManager();
        this.debugCrashKeyTime = -1L;
        this.debugCrashKeyReportedTime = -1L;
        this.debugCrashKeyReportedCount = -1L;
        this.minecraft = cyc;
    }
    
    private void debugFeedbackTranslated(final String string, final Object... arr) {
        this.minecraft.gui.getChat().addMessage(new TextComponent("").append(new TranslatableComponent("debug.prefix", new Object[0]).withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD)).append(" ").append(new TranslatableComponent(string, arr)));
    }
    
    private void debugWarningTranslated(final String string, final Object... arr) {
        this.minecraft.gui.getChat().addMessage(new TextComponent("").append(new TranslatableComponent("debug.prefix", new Object[0]).withStyle(ChatFormatting.RED, ChatFormatting.BOLD)).append(" ").append(new TranslatableComponent(string, arr)));
    }
    
    private boolean handleDebugKeys(final int integer) {
        if (this.debugCrashKeyTime > 0L && this.debugCrashKeyTime < Util.getMillis() - 100L) {
            return true;
        }
        switch (integer) {
            case 65: {
                this.minecraft.levelRenderer.allChanged();
                this.debugFeedbackTranslated("debug.reload_chunks.message");
                return true;
            }
            case 66: {
                final boolean boolean3 = !this.minecraft.getEntityRenderDispatcher().shouldRenderHitBoxes();
                this.minecraft.getEntityRenderDispatcher().setRenderHitBoxes(boolean3);
                this.debugFeedbackTranslated(boolean3 ? "debug.show_hitboxes.on" : "debug.show_hitboxes.off");
                return true;
            }
            case 68: {
                if (this.minecraft.gui != null) {
                    this.minecraft.gui.getChat().clearMessages(false);
                }
                return true;
            }
            case 70: {
                Option.RENDER_DISTANCE.set(this.minecraft.options, Mth.clamp(this.minecraft.options.renderDistance + (Screen.hasShiftDown() ? -1 : 1), Option.RENDER_DISTANCE.getMinValue(), Option.RENDER_DISTANCE.getMaxValue()));
                this.debugFeedbackTranslated("debug.cycle_renderdistance.message", this.minecraft.options.renderDistance);
                return true;
            }
            case 71: {
                final boolean boolean4 = this.minecraft.debugRenderer.switchRenderChunkborder();
                this.debugFeedbackTranslated(boolean4 ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off");
                return true;
            }
            case 72: {
                this.minecraft.options.advancedItemTooltips = !this.minecraft.options.advancedItemTooltips;
                this.debugFeedbackTranslated(this.minecraft.options.advancedItemTooltips ? "debug.advanced_tooltips.on" : "debug.advanced_tooltips.off");
                this.minecraft.options.save();
                return true;
            }
            case 73: {
                if (!this.minecraft.player.isReducedDebugInfo()) {
                    this.copyRecreateCommand(this.minecraft.player.hasPermissions(2), !Screen.hasShiftDown());
                }
                return true;
            }
            case 78: {
                if (!this.minecraft.player.hasPermissions(2)) {
                    this.debugFeedbackTranslated("debug.creative_spectator.error");
                }
                else if (this.minecraft.player.isCreative()) {
                    this.minecraft.player.chat("/gamemode spectator");
                }
                else {
                    this.minecraft.player.chat("/gamemode creative");
                }
                return true;
            }
            case 80: {
                this.minecraft.options.pauseOnLostFocus = !this.minecraft.options.pauseOnLostFocus;
                this.minecraft.options.save();
                this.debugFeedbackTranslated(this.minecraft.options.pauseOnLostFocus ? "debug.pause_focus.on" : "debug.pause_focus.off");
                return true;
            }
            case 81: {
                this.debugFeedbackTranslated("debug.help.message");
                final ChatComponent czj5 = this.minecraft.gui.getChat();
                czj5.addMessage(new TranslatableComponent("debug.reload_chunks.help", new Object[0]));
                czj5.addMessage(new TranslatableComponent("debug.show_hitboxes.help", new Object[0]));
                czj5.addMessage(new TranslatableComponent("debug.copy_location.help", new Object[0]));
                czj5.addMessage(new TranslatableComponent("debug.clear_chat.help", new Object[0]));
                czj5.addMessage(new TranslatableComponent("debug.cycle_renderdistance.help", new Object[0]));
                czj5.addMessage(new TranslatableComponent("debug.chunk_boundaries.help", new Object[0]));
                czj5.addMessage(new TranslatableComponent("debug.advanced_tooltips.help", new Object[0]));
                czj5.addMessage(new TranslatableComponent("debug.inspect.help", new Object[0]));
                czj5.addMessage(new TranslatableComponent("debug.creative_spectator.help", new Object[0]));
                czj5.addMessage(new TranslatableComponent("debug.pause_focus.help", new Object[0]));
                czj5.addMessage(new TranslatableComponent("debug.help.help", new Object[0]));
                czj5.addMessage(new TranslatableComponent("debug.reload_resourcepacks.help", new Object[0]));
                czj5.addMessage(new TranslatableComponent("debug.pause.help", new Object[0]));
                return true;
            }
            case 84: {
                this.debugFeedbackTranslated("debug.reload_resourcepacks.message");
                this.minecraft.reloadResourcePacks();
                return true;
            }
            case 67: {
                if (this.minecraft.player.isReducedDebugInfo()) {
                    return false;
                }
                this.debugFeedbackTranslated("debug.copy_location.message");
                this.setClipboard(String.format(Locale.ROOT, "/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f", new Object[] { DimensionType.getName(this.minecraft.player.level.dimension.getType()), this.minecraft.player.x, this.minecraft.player.y, this.minecraft.player.z, this.minecraft.player.yRot, this.minecraft.player.xRot }));
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private void copyRecreateCommand(final boolean boolean1, final boolean boolean2) {
        final HitResult csf4 = this.minecraft.hitResult;
        if (csf4 == null) {
            return;
        }
        switch (csf4.getType()) {
            case BLOCK: {
                final BlockPos ew5 = ((BlockHitResult)csf4).getBlockPos();
                final BlockState bvt6 = this.minecraft.player.level.getBlockState(ew5);
                if (!boolean1) {
                    this.copyCreateBlockCommand(bvt6, ew5, null);
                    this.debugFeedbackTranslated("debug.inspect.client.block");
                    break;
                }
                if (boolean2) {
                    this.minecraft.player.connection.getDebugQueryHandler().queryBlockEntityTag(ew5, (Consumer<CompoundTag>)(id -> {
                        this.copyCreateBlockCommand(bvt6, ew5, id);
                        this.debugFeedbackTranslated("debug.inspect.server.block");
                    }));
                    break;
                }
                final BlockEntity btw7 = this.minecraft.player.level.getBlockEntity(ew5);
                final CompoundTag id8 = (btw7 != null) ? btw7.save(new CompoundTag()) : null;
                this.copyCreateBlockCommand(bvt6, ew5, id8);
                this.debugFeedbackTranslated("debug.inspect.client.block");
                break;
            }
            case ENTITY: {
                final Entity aio5 = ((EntityHitResult)csf4).getEntity();
                final ResourceLocation qv6 = Registry.ENTITY_TYPE.getKey(aio5.getType());
                final Vec3 csi7 = new Vec3(aio5.x, aio5.y, aio5.z);
                if (!boolean1) {
                    this.copyCreateEntityCommand(qv6, csi7, null);
                    this.debugFeedbackTranslated("debug.inspect.client.entity");
                    break;
                }
                if (boolean2) {
                    this.minecraft.player.connection.getDebugQueryHandler().queryEntityTag(aio5.getId(), (Consumer<CompoundTag>)(id -> {
                        this.copyCreateEntityCommand(qv6, csi7, id);
                        this.debugFeedbackTranslated("debug.inspect.server.entity");
                    }));
                    break;
                }
                final CompoundTag id8 = aio5.saveWithoutId(new CompoundTag());
                this.copyCreateEntityCommand(qv6, csi7, id8);
                this.debugFeedbackTranslated("debug.inspect.client.entity");
                break;
            }
        }
    }
    
    private void copyCreateBlockCommand(final BlockState bvt, final BlockPos ew, @Nullable final CompoundTag id) {
        if (id != null) {
            id.remove("x");
            id.remove("y");
            id.remove("z");
            id.remove("id");
        }
        final StringBuilder stringBuilder5 = new StringBuilder(BlockStateParser.serialize(bvt));
        if (id != null) {
            stringBuilder5.append(id);
        }
        final String string6 = String.format(Locale.ROOT, "/setblock %d %d %d %s", new Object[] { ew.getX(), ew.getY(), ew.getZ(), stringBuilder5 });
        this.setClipboard(string6);
    }
    
    private void copyCreateEntityCommand(final ResourceLocation qv, final Vec3 csi, @Nullable final CompoundTag id) {
        String string7;
        if (id != null) {
            id.remove("UUIDMost");
            id.remove("UUIDLeast");
            id.remove("Pos");
            id.remove("Dimension");
            final String string6 = id.getPrettyDisplay().getString();
            string7 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f %s", new Object[] { qv.toString(), csi.x, csi.y, csi.z, string6 });
        }
        else {
            string7 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f", new Object[] { qv.toString(), csi.x, csi.y, csi.z });
        }
        this.setClipboard(string7);
    }
    
    public void keyPress(final long long1, final int integer2, final int integer3, final int integer4, final int integer5) {
        if (long1 != this.minecraft.window.getWindow()) {
            return;
        }
        if (this.debugCrashKeyTime > 0L) {
            if (!InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 67) || !InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 292)) {
                this.debugCrashKeyTime = -1L;
            }
        }
        else if (InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 67) && InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 292)) {
            this.handledDebugKey = true;
            this.debugCrashKeyTime = Util.getMillis();
            this.debugCrashKeyReportedTime = Util.getMillis();
            this.debugCrashKeyReportedCount = 0L;
        }
        final ContainerEventHandler dad8 = this.minecraft.screen;
        if (integer4 == 1 && (!(this.minecraft.screen instanceof ControlsScreen) || ((ControlsScreen)dad8).lastKeySelection <= Util.getMillis() - 20L)) {
            if (this.minecraft.options.keyFullscreen.matches(integer2, integer3)) {
                this.minecraft.window.toggleFullScreen();
                this.minecraft.options.fullscreen = this.minecraft.window.isFullscreen();
                return;
            }
            if (this.minecraft.options.keyScreenshot.matches(integer2, integer3)) {
                if (Screen.hasControlDown()) {}
                Screenshot.grab(this.minecraft.gameDirectory, this.minecraft.window.getWidth(), this.minecraft.window.getHeight(), this.minecraft.getMainRenderTarget(), (Consumer<Component>)(jo -> this.minecraft.execute(() -> this.minecraft.gui.getChat().addMessage(jo))));
                return;
            }
        }
        final boolean boolean9 = dad8 == null || !(dad8.getFocused() instanceof EditBox) || !((EditBox)dad8.getFocused()).canConsumeInput();
        if (integer4 != 0 && integer2 == 66 && Screen.hasControlDown() && boolean9) {
            Option.NARRATOR.toggle(this.minecraft.options, 1);
            if (dad8 instanceof ChatOptionsScreen) {
                ((ChatOptionsScreen)dad8).updateNarratorButton();
            }
            if (dad8 instanceof AccessibilityOptionsScreen) {
                ((AccessibilityOptionsScreen)dad8).updateNarratorButton();
            }
        }
        if (dad8 != null) {
            final boolean[] arr10 = { false };
            Screen.wrapScreenError(() -> {
                if (integer4 == 1 || (integer4 == 2 && this.sendRepeatsToGui)) {
                    arr10[0] = dad8.keyPressed(integer2, integer3, integer5);
                }
                else if (integer4 == 0) {
                    arr10[0] = dad8.keyReleased(integer2, integer3, integer5);
                }
            }, "keyPressed event handler", dad8.getClass().getCanonicalName());
            if (arr10[0]) {
                return;
            }
        }
        if (this.minecraft.screen == null || this.minecraft.screen.passEvents) {
            final InputConstants.Key a10 = InputConstants.getKey(integer2, integer3);
            if (integer4 == 0) {
                KeyMapping.set(a10, false);
                if (integer2 == 292) {
                    if (this.handledDebugKey) {
                        this.handledDebugKey = false;
                    }
                    else {
                        this.minecraft.options.renderDebug = !this.minecraft.options.renderDebug;
                        this.minecraft.options.renderDebugCharts = (this.minecraft.options.renderDebug && Screen.hasShiftDown());
                        this.minecraft.options.renderFpsChart = (this.minecraft.options.renderDebug && Screen.hasAltDown());
                    }
                }
            }
            else {
                if (integer2 == 293 && this.minecraft.gameRenderer != null) {
                    this.minecraft.gameRenderer.togglePostEffect();
                }
                boolean boolean10 = false;
                if (this.minecraft.screen == null) {
                    if (integer2 == 256) {
                        final boolean boolean11 = InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 292);
                        this.minecraft.pauseGame(boolean11);
                    }
                    boolean10 = (InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 292) && this.handleDebugKeys(integer2));
                    this.handledDebugKey |= boolean10;
                    if (integer2 == 290) {
                        this.minecraft.options.hideGui = !this.minecraft.options.hideGui;
                    }
                }
                if (boolean10) {
                    KeyMapping.set(a10, false);
                }
                else {
                    KeyMapping.set(a10, true);
                    KeyMapping.click(a10);
                }
                if (this.minecraft.options.renderDebugCharts) {
                    if (integer2 == 48) {
                        this.minecraft.debugFpsMeterKeyPress(0);
                    }
                    for (int integer6 = 0; integer6 < 9; ++integer6) {
                        if (integer2 == 49 + integer6) {
                            this.minecraft.debugFpsMeterKeyPress(integer6 + 1);
                        }
                    }
                }
            }
        }
    }
    
    private void charTyped(final long long1, final int integer2, final int integer3) {
        if (long1 != this.minecraft.window.getWindow()) {
            return;
        }
        final GuiEventListener dae6 = this.minecraft.screen;
        if (dae6 == null || this.minecraft.getOverlay() != null) {
            return;
        }
        if (Character.charCount(integer2) == 1) {
            Screen.wrapScreenError(() -> dae6.charTyped((char)integer2, integer3), "charTyped event handler", dae6.getClass().getCanonicalName());
        }
        else {
            for (final char character10 : Character.toChars(integer2)) {
                Screen.wrapScreenError(() -> dae6.charTyped(character10, integer3), "charTyped event handler", dae6.getClass().getCanonicalName());
            }
        }
    }
    
    public void setSendRepeatsToGui(final boolean boolean1) {
        this.sendRepeatsToGui = boolean1;
    }
    
    public void setup(final long long1) {
        InputConstants.setupKeyboardCallbacks(long1, this::keyPress, this::charTyped);
    }
    
    public String getClipboard() {
        return this.clipboardManager.getClipboard(this.minecraft.window.getWindow(), (integer, long2) -> {
            if (integer != 65545) {
                this.minecraft.window.defaultErrorCallback(integer, long2);
            }
        });
    }
    
    public void setClipboard(final String string) {
        this.clipboardManager.setClipboard(this.minecraft.window.getWindow(), string);
    }
    
    public void tick() {
        if (this.debugCrashKeyTime > 0L) {
            final long long2 = Util.getMillis();
            final long long3 = 10000L - (long2 - this.debugCrashKeyTime);
            final long long4 = long2 - this.debugCrashKeyReportedTime;
            if (long3 < 0L) {
                if (Screen.hasControlDown()) {
                    Blaze3D.youJustLostTheGame();
                }
                throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
            }
            if (long4 >= 1000L) {
                if (this.debugCrashKeyReportedCount == 0L) {
                    this.debugFeedbackTranslated("debug.crash.message");
                }
                else {
                    this.debugWarningTranslated("debug.crash.warning", Mth.ceil(long3 / 1000.0f));
                }
                this.debugCrashKeyReportedTime = long2;
                ++this.debugCrashKeyReportedCount;
            }
        }
    }
}
