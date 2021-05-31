package net.minecraft.client.gui.screens.inventory;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSetCommandBlockPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.level.block.entity.CommandBlockEntity;

public class CommandBlockEditScreen extends AbstractCommandBlockEditScreen {
    private final CommandBlockEntity autoCommandBlock;
    private Button modeButton;
    private Button conditionalButton;
    private Button autoexecButton;
    private CommandBlockEntity.Mode mode;
    private boolean conditional;
    private boolean autoexec;
    
    public CommandBlockEditScreen(final CommandBlockEntity bub) {
        this.mode = CommandBlockEntity.Mode.REDSTONE;
        this.autoCommandBlock = bub;
    }
    
    @Override
    BaseCommandBlock getCommandBlock() {
        return this.autoCommandBlock.getCommandBlock();
    }
    
    @Override
    int getPreviousY() {
        return 135;
    }
    
    @Override
    protected void init() {
        super.init();
        this.modeButton = this.<Button>addButton(new Button(this.width / 2 - 50 - 100 - 4, 165, 100, 20, I18n.get("advMode.mode.sequence"), czi -> {
            this.nextMode();
            this.updateMode();
            return;
        }));
        this.conditionalButton = this.<Button>addButton(new Button(this.width / 2 - 50, 165, 100, 20, I18n.get("advMode.mode.unconditional"), czi -> {
            this.conditional = !this.conditional;
            this.updateConditional();
            return;
        }));
        this.autoexecButton = this.<Button>addButton(new Button(this.width / 2 + 50 + 4, 165, 100, 20, I18n.get("advMode.mode.redstoneTriggered"), czi -> {
            this.autoexec = !this.autoexec;
            this.updateAutoexec();
            return;
        }));
        this.doneButton.active = false;
        this.outputButton.active = false;
        this.modeButton.active = false;
        this.conditionalButton.active = false;
        this.autoexecButton.active = false;
    }
    
    public void updateGui() {
        final BaseCommandBlock bgx2 = this.autoCommandBlock.getCommandBlock();
        this.commandEdit.setValue(bgx2.getCommand());
        this.trackOutput = bgx2.isTrackOutput();
        this.mode = this.autoCommandBlock.getMode();
        this.conditional = this.autoCommandBlock.isConditional();
        this.autoexec = this.autoCommandBlock.isAutomatic();
        this.updateCommandOutput();
        this.updateMode();
        this.updateConditional();
        this.updateAutoexec();
        this.doneButton.active = true;
        this.outputButton.active = true;
        this.modeButton.active = true;
        this.conditionalButton.active = true;
        this.autoexecButton.active = true;
    }
    
    @Override
    public void resize(final Minecraft cyc, final int integer2, final int integer3) {
        super.resize(cyc, integer2, integer3);
        this.updateCommandOutput();
        this.updateMode();
        this.updateConditional();
        this.updateAutoexec();
        this.doneButton.active = true;
        this.outputButton.active = true;
        this.modeButton.active = true;
        this.conditionalButton.active = true;
        this.autoexecButton.active = true;
    }
    
    @Override
    protected void populateAndSendPacket(final BaseCommandBlock bgx) {
        this.minecraft.getConnection().send(new ServerboundSetCommandBlockPacket(new BlockPos(bgx.getPosition()), this.commandEdit.getValue(), this.mode, bgx.isTrackOutput(), this.conditional, this.autoexec));
    }
    
    private void updateMode() {
        switch (this.mode) {
            case SEQUENCE: {
                this.modeButton.setMessage(I18n.get("advMode.mode.sequence"));
                break;
            }
            case AUTO: {
                this.modeButton.setMessage(I18n.get("advMode.mode.auto"));
                break;
            }
            case REDSTONE: {
                this.modeButton.setMessage(I18n.get("advMode.mode.redstone"));
                break;
            }
        }
    }
    
    private void nextMode() {
        switch (this.mode) {
            case SEQUENCE: {
                this.mode = CommandBlockEntity.Mode.AUTO;
                break;
            }
            case AUTO: {
                this.mode = CommandBlockEntity.Mode.REDSTONE;
                break;
            }
            case REDSTONE: {
                this.mode = CommandBlockEntity.Mode.SEQUENCE;
                break;
            }
        }
    }
    
    private void updateConditional() {
        if (this.conditional) {
            this.conditionalButton.setMessage(I18n.get("advMode.mode.conditional"));
        }
        else {
            this.conditionalButton.setMessage(I18n.get("advMode.mode.unconditional"));
        }
    }
    
    private void updateAutoexec() {
        if (this.autoexec) {
            this.autoexecButton.setMessage(I18n.get("advMode.mode.autoexec.bat"));
        }
        else {
            this.autoexecButton.setMessage(I18n.get("advMode.mode.redstoneTriggered"));
        }
    }
}
