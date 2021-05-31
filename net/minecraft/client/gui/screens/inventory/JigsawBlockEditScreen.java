package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import java.util.function.Consumer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSetJigsawBlockPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.client.gui.screens.Screen;

public class JigsawBlockEditScreen extends Screen {
    private final JigsawBlockEntity jigsawEntity;
    private EditBox attachementTypeEdit;
    private EditBox targetPoolEdit;
    private EditBox finalStateEdit;
    private Button doneButton;
    
    public JigsawBlockEditScreen(final JigsawBlockEntity bum) {
        super(NarratorChatListener.NO_TITLE);
        this.jigsawEntity = bum;
    }
    
    @Override
    public void tick() {
        this.attachementTypeEdit.tick();
        this.targetPoolEdit.tick();
        this.finalStateEdit.tick();
    }
    
    private void onDone() {
        this.sendToServer();
        this.minecraft.setScreen(null);
    }
    
    private void onCancel() {
        this.minecraft.setScreen(null);
    }
    
    private void sendToServer() {
        this.minecraft.getConnection().send(new ServerboundSetJigsawBlockPacket(this.jigsawEntity.getBlockPos(), new ResourceLocation(this.attachementTypeEdit.getValue()), new ResourceLocation(this.targetPoolEdit.getValue()), this.finalStateEdit.getValue()));
    }
    
    @Override
    public void onClose() {
        this.onCancel();
    }
    
    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.doneButton = this.<Button>addButton(new Button(this.width / 2 - 4 - 150, 210, 150, 20, I18n.get("gui.done"), czi -> this.onDone()));
        this.<Button>addButton(new Button(this.width / 2 + 4, 210, 150, 20, I18n.get("gui.cancel"), czi -> this.onCancel()));
        (this.targetPoolEdit = new EditBox(this.font, this.width / 2 - 152, 40, 300, 20, I18n.get("jigsaw_block.target_pool"))).setMaxLength(128);
        this.targetPoolEdit.setValue(this.jigsawEntity.getTargetPool().toString());
        this.targetPoolEdit.setResponder((Consumer<String>)(string -> this.updateValidity()));
        this.children.add(this.targetPoolEdit);
        (this.attachementTypeEdit = new EditBox(this.font, this.width / 2 - 152, 80, 300, 20, I18n.get("jigsaw_block.attachement_type"))).setMaxLength(128);
        this.attachementTypeEdit.setValue(this.jigsawEntity.getAttachementType().toString());
        this.attachementTypeEdit.setResponder((Consumer<String>)(string -> this.updateValidity()));
        this.children.add(this.attachementTypeEdit);
        (this.finalStateEdit = new EditBox(this.font, this.width / 2 - 152, 120, 300, 20, I18n.get("jigsaw_block.final_state"))).setMaxLength(256);
        this.finalStateEdit.setValue(this.jigsawEntity.getFinalState());
        this.children.add(this.finalStateEdit);
        this.setInitialFocus(this.targetPoolEdit);
        this.updateValidity();
    }
    
    protected void updateValidity() {
        this.doneButton.active = (ResourceLocation.isValidResourceLocation(this.attachementTypeEdit.getValue()) & ResourceLocation.isValidResourceLocation(this.targetPoolEdit.getValue()));
    }
    
    @Override
    public void resize(final Minecraft cyc, final int integer2, final int integer3) {
        final String string5 = this.attachementTypeEdit.getValue();
        final String string6 = this.targetPoolEdit.getValue();
        final String string7 = this.finalStateEdit.getValue();
        this.init(cyc, integer2, integer3);
        this.attachementTypeEdit.setValue(string5);
        this.targetPoolEdit.setValue(string6);
        this.finalStateEdit.setValue(string7);
    }
    
    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (super.keyPressed(integer1, integer2, integer3)) {
            return true;
        }
        if (this.doneButton.active && (integer1 == 257 || integer1 == 335)) {
            this.onDone();
            return true;
        }
        return false;
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.drawString(this.font, I18n.get("jigsaw_block.target_pool"), this.width / 2 - 153, 30, 10526880);
        this.targetPoolEdit.render(integer1, integer2, float3);
        this.drawString(this.font, I18n.get("jigsaw_block.attachement_type"), this.width / 2 - 153, 70, 10526880);
        this.attachementTypeEdit.render(integer1, integer2, float3);
        this.drawString(this.font, I18n.get("jigsaw_block.final_state"), this.width / 2 - 153, 110, 10526880);
        this.finalStateEdit.render(integer1, integer2, float3);
        super.render(integer1, integer2, float3);
    }
}
