package net.minecraft.client.gui.screens.worldselection;

import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.client.Minecraft;
import java.nio.file.Path;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.client.gui.components.events.GuiEventListener;
import java.util.function.Consumer;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import java.io.IOException;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import net.minecraft.Util;
import org.apache.commons.io.FileUtils;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.client.gui.components.EditBox;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;

public class EditWorldScreen extends Screen {
    private Button renameButton;
    private final BooleanConsumer callback;
    private EditBox nameEdit;
    private final String levelId;
    
    public EditWorldScreen(final BooleanConsumer booleanConsumer, final String string) {
        super(new TranslatableComponent("selectWorld.edit.title", new Object[0]));
        this.callback = booleanConsumer;
        this.levelId = string;
    }
    
    @Override
    public void tick() {
        this.nameEdit.tick();
    }
    
    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        final LevelStorageSource coq3;
        final Button czi2 = this.<Button>addButton(new Button(this.width / 2 - 100, this.height / 4 + 24 + 5, 200, 20, I18n.get("selectWorld.edit.resetIcon"), czi -> {
            coq3 = this.minecraft.getLevelSource();
            FileUtils.deleteQuietly(coq3.getFile(this.levelId, "icon.png"));
            czi.active = false;
            return;
        }));
        final LevelStorageSource coq4;
        this.<Button>addButton(new Button(this.width / 2 - 100, this.height / 4 + 48 + 5, 200, 20, I18n.get("selectWorld.edit.openFolder"), czi -> {
            coq4 = this.minecraft.getLevelSource();
            Util.getPlatform().openFile(coq4.getFile(this.levelId, "icon.png").getParentFile());
            return;
        }));
        final LevelStorageSource coq5;
        this.<Button>addButton(new Button(this.width / 2 - 100, this.height / 4 + 72 + 5, 200, 20, I18n.get("selectWorld.edit.backup"), czi -> {
            coq5 = this.minecraft.getLevelSource();
            makeBackupAndShowToast(coq5, this.levelId);
            this.callback.accept(false);
            return;
        }));
        final LevelStorageSource coq6;
        final Path path4;
        this.<Button>addButton(new Button(this.width / 2 - 100, this.height / 4 + 96 + 5, 200, 20, I18n.get("selectWorld.edit.backupFolder"), czi -> {
            coq6 = this.minecraft.getLevelSource();
            path4 = coq6.getBackupPath();
            try {
                Files.createDirectories(Files.exists(path4, new LinkOption[0]) ? path4.toRealPath(new LinkOption[0]) : path4, new FileAttribute[0]);
            }
            catch (IOException iOException5) {
                throw new RuntimeException((Throwable)iOException5);
            }
            Util.getPlatform().openFile(path4.toFile());
            return;
        }));
        final Minecraft minecraft;
        final BackupConfirmScreen screen;
        this.<Button>addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 5, 200, 20, I18n.get("selectWorld.edit.optimize"), czi -> {
            minecraft = this.minecraft;
            new BackupConfirmScreen(this, (boolean1, boolean2) -> {
                if (boolean1) {
                    makeBackupAndShowToast(this.minecraft.getLevelSource(), this.levelId);
                }
                this.minecraft.setScreen(new OptimizeWorldScreen(this.callback, this.levelId, this.minecraft.getLevelSource(), boolean2));
                return;
            }, new TranslatableComponent("optimizeWorld.confirm.title", new Object[0]), new TranslatableComponent("optimizeWorld.confirm.description", new Object[0]), true);
            minecraft.setScreen(screen);
            return;
        }));
        this.renameButton = this.<Button>addButton(new Button(this.width / 2 - 100, this.height / 4 + 144 + 5, 98, 20, I18n.get("selectWorld.edit.save"), czi -> this.onRename()));
        this.<Button>addButton(new Button(this.width / 2 + 2, this.height / 4 + 144 + 5, 98, 20, I18n.get("gui.cancel"), czi -> this.callback.accept(false)));
        czi2.active = this.minecraft.getLevelSource().getFile(this.levelId, "icon.png").isFile();
        final LevelStorageSource coq7 = this.minecraft.getLevelSource();
        final LevelData com4 = coq7.getDataTagFor(this.levelId);
        final String string5 = (com4 == null) ? "" : com4.getLevelName();
        (this.nameEdit = new EditBox(this.font, this.width / 2 - 100, 53, 200, 20, I18n.get("selectWorld.enterName"))).setValue(string5);
        this.nameEdit.setResponder((Consumer<String>)(string -> this.renameButton.active = !string.trim().isEmpty()));
        this.children.add(this.nameEdit);
        this.setInitialFocus(this.nameEdit);
    }
    
    @Override
    public void resize(final Minecraft cyc, final int integer2, final int integer3) {
        final String string5 = this.nameEdit.getValue();
        this.init(cyc, integer2, integer3);
        this.nameEdit.setValue(string5);
    }
    
    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }
    
    private void onRename() {
        final LevelStorageSource coq2 = this.minecraft.getLevelSource();
        coq2.renameLevel(this.levelId, this.nameEdit.getValue().trim());
        this.callback.accept(true);
    }
    
    public static void makeBackupAndShowToast(final LevelStorageSource coq, final String string) {
        final ToastComponent dan3 = Minecraft.getInstance().getToasts();
        long long4 = 0L;
        IOException iOException6 = null;
        try {
            long4 = coq.makeWorldBackup(string);
        }
        catch (IOException iOException7) {
            iOException6 = iOException7;
        }
        Component jo7;
        Component jo8;
        if (iOException6 != null) {
            jo7 = new TranslatableComponent("selectWorld.edit.backupFailed", new Object[0]);
            jo8 = new TextComponent(iOException6.getMessage());
        }
        else {
            jo7 = new TranslatableComponent("selectWorld.edit.backupCreated", new Object[] { string });
            jo8 = new TranslatableComponent("selectWorld.edit.backupSize", new Object[] { Mth.ceil(long4 / 1048576.0) });
        }
        dan3.addToast(new SystemToast(SystemToast.SystemToastIds.WORLD_BACKUP, jo7, jo8));
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 20, 16777215);
        this.drawString(this.font, I18n.get("selectWorld.enterName"), this.width / 2 - 100, 40, 10526880);
        this.nameEdit.render(integer1, integer2, float3);
        super.render(integer1, integer2, float3);
    }
}
