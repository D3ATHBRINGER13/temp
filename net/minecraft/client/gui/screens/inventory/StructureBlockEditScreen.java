package net.minecraft.client.gui.screens.inventory;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.Blocks;
import java.text.DecimalFormat;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.client.gui.screens.Screen;

public class StructureBlockEditScreen extends Screen {
    private final StructureBlockEntity structure;
    private Mirror initialMirror;
    private Rotation initialRotation;
    private StructureMode initialMode;
    private boolean initialEntityIgnoring;
    private boolean initialShowAir;
    private boolean initialShowBoundingBox;
    private EditBox nameEdit;
    private EditBox posXEdit;
    private EditBox posYEdit;
    private EditBox posZEdit;
    private EditBox sizeXEdit;
    private EditBox sizeYEdit;
    private EditBox sizeZEdit;
    private EditBox integrityEdit;
    private EditBox seedEdit;
    private EditBox dataEdit;
    private Button doneButton;
    private Button cancelButton;
    private Button saveButton;
    private Button loadButton;
    private Button rot0Button;
    private Button rot90Button;
    private Button rot180Button;
    private Button rot270Button;
    private Button modeButton;
    private Button detectButton;
    private Button entitiesButton;
    private Button mirrorButton;
    private Button toggleAirButton;
    private Button toggleBoundingBox;
    private final DecimalFormat decimalFormat;
    
    public StructureBlockEditScreen(final StructureBlockEntity buw) {
        super(new TranslatableComponent(Blocks.STRUCTURE_BLOCK.getDescriptionId(), new Object[0]));
        this.initialMirror = Mirror.NONE;
        this.initialRotation = Rotation.NONE;
        this.initialMode = StructureMode.DATA;
        this.decimalFormat = new DecimalFormat("0.0###");
        this.structure = buw;
        this.decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
    }
    
    @Override
    public void tick() {
        this.nameEdit.tick();
        this.posXEdit.tick();
        this.posYEdit.tick();
        this.posZEdit.tick();
        this.sizeXEdit.tick();
        this.sizeYEdit.tick();
        this.sizeZEdit.tick();
        this.integrityEdit.tick();
        this.seedEdit.tick();
        this.dataEdit.tick();
    }
    
    private void onDone() {
        if (this.sendToServer(StructureBlockEntity.UpdateType.UPDATE_DATA)) {
            this.minecraft.setScreen(null);
        }
    }
    
    private void onCancel() {
        this.structure.setMirror(this.initialMirror);
        this.structure.setRotation(this.initialRotation);
        this.structure.setMode(this.initialMode);
        this.structure.setIgnoreEntities(this.initialEntityIgnoring);
        this.structure.setShowAir(this.initialShowAir);
        this.structure.setShowBoundingBox(this.initialShowBoundingBox);
        this.minecraft.setScreen(null);
    }
    
    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.doneButton = this.<Button>addButton(new Button(this.width / 2 - 4 - 150, 210, 150, 20, I18n.get("gui.done"), czi -> this.onDone()));
        this.cancelButton = this.<Button>addButton(new Button(this.width / 2 + 4, 210, 150, 20, I18n.get("gui.cancel"), czi -> this.onCancel()));
        this.saveButton = this.<Button>addButton(new Button(this.width / 2 + 4 + 100, 185, 50, 20, I18n.get("structure_block.button.save"), czi -> {
            if (this.structure.getMode() == StructureMode.SAVE) {
                this.sendToServer(StructureBlockEntity.UpdateType.SAVE_AREA);
                this.minecraft.setScreen(null);
            }
            return;
        }));
        this.loadButton = this.<Button>addButton(new Button(this.width / 2 + 4 + 100, 185, 50, 20, I18n.get("structure_block.button.load"), czi -> {
            if (this.structure.getMode() == StructureMode.LOAD) {
                this.sendToServer(StructureBlockEntity.UpdateType.LOAD_AREA);
                this.minecraft.setScreen(null);
            }
            return;
        }));
        this.modeButton = this.<Button>addButton(new Button(this.width / 2 - 4 - 150, 185, 50, 20, "MODE", czi -> {
            this.structure.nextMode();
            this.updateMode();
            return;
        }));
        this.detectButton = this.<Button>addButton(new Button(this.width / 2 + 4 + 100, 120, 50, 20, I18n.get("structure_block.button.detect_size"), czi -> {
            if (this.structure.getMode() == StructureMode.SAVE) {
                this.sendToServer(StructureBlockEntity.UpdateType.SCAN_AREA);
                this.minecraft.setScreen(null);
            }
            return;
        }));
        this.entitiesButton = this.<Button>addButton(new Button(this.width / 2 + 4 + 100, 160, 50, 20, "ENTITIES", czi -> {
            this.structure.setIgnoreEntities(!this.structure.isIgnoreEntities());
            this.updateEntitiesButton();
            return;
        }));
        this.mirrorButton = this.<Button>addButton(new Button(this.width / 2 - 20, 185, 40, 20, "MIRROR", czi -> {
            switch (this.structure.getMirror()) {
                case NONE: {
                    this.structure.setMirror(Mirror.LEFT_RIGHT);
                    break;
                }
                case LEFT_RIGHT: {
                    this.structure.setMirror(Mirror.FRONT_BACK);
                    break;
                }
                case FRONT_BACK: {
                    this.structure.setMirror(Mirror.NONE);
                    break;
                }
            }
            this.updateMirrorButton();
            return;
        }));
        this.toggleAirButton = this.<Button>addButton(new Button(this.width / 2 + 4 + 100, 80, 50, 20, "SHOWAIR", czi -> {
            this.structure.setShowAir(!this.structure.getShowAir());
            this.updateToggleAirButton();
            return;
        }));
        this.toggleBoundingBox = this.<Button>addButton(new Button(this.width / 2 + 4 + 100, 80, 50, 20, "SHOWBB", czi -> {
            this.structure.setShowBoundingBox(!this.structure.getShowBoundingBox());
            this.updateToggleBoundingBox();
            return;
        }));
        this.rot0Button = this.<Button>addButton(new Button(this.width / 2 - 1 - 40 - 1 - 40 - 20, 185, 40, 20, "0", czi -> {
            this.structure.setRotation(Rotation.NONE);
            this.updateDirectionButtons();
            return;
        }));
        this.rot90Button = this.<Button>addButton(new Button(this.width / 2 - 1 - 40 - 20, 185, 40, 20, "90", czi -> {
            this.structure.setRotation(Rotation.CLOCKWISE_90);
            this.updateDirectionButtons();
            return;
        }));
        this.rot180Button = this.<Button>addButton(new Button(this.width / 2 + 1 + 20, 185, 40, 20, "180", czi -> {
            this.structure.setRotation(Rotation.CLOCKWISE_180);
            this.updateDirectionButtons();
            return;
        }));
        this.rot270Button = this.<Button>addButton(new Button(this.width / 2 + 1 + 40 + 1 + 20, 185, 40, 20, "270", czi -> {
            this.structure.setRotation(Rotation.COUNTERCLOCKWISE_90);
            this.updateDirectionButtons();
            return;
        }));
        (this.nameEdit = new EditBox(this.font, this.width / 2 - 152, 40, 300, 20, I18n.get("structure_block.structure_name")) {
            @Override
            public boolean charTyped(final char character, final int integer) {
                return Screen.this.isValidCharacterForName(this.getValue(), character, this.getCursorPosition()) && super.charTyped(character, integer);
            }
        }).setMaxLength(64);
        this.nameEdit.setValue(this.structure.getStructureName());
        this.children.add(this.nameEdit);
        final BlockPos ew2 = this.structure.getStructurePos();
        (this.posXEdit = new EditBox(this.font, this.width / 2 - 152, 80, 80, 20, I18n.get("structure_block.position.x"))).setMaxLength(15);
        this.posXEdit.setValue(Integer.toString(ew2.getX()));
        this.children.add(this.posXEdit);
        (this.posYEdit = new EditBox(this.font, this.width / 2 - 72, 80, 80, 20, I18n.get("structure_block.position.y"))).setMaxLength(15);
        this.posYEdit.setValue(Integer.toString(ew2.getY()));
        this.children.add(this.posYEdit);
        (this.posZEdit = new EditBox(this.font, this.width / 2 + 8, 80, 80, 20, I18n.get("structure_block.position.z"))).setMaxLength(15);
        this.posZEdit.setValue(Integer.toString(ew2.getZ()));
        this.children.add(this.posZEdit);
        final BlockPos ew3 = this.structure.getStructureSize();
        (this.sizeXEdit = new EditBox(this.font, this.width / 2 - 152, 120, 80, 20, I18n.get("structure_block.size.x"))).setMaxLength(15);
        this.sizeXEdit.setValue(Integer.toString(ew3.getX()));
        this.children.add(this.sizeXEdit);
        (this.sizeYEdit = new EditBox(this.font, this.width / 2 - 72, 120, 80, 20, I18n.get("structure_block.size.y"))).setMaxLength(15);
        this.sizeYEdit.setValue(Integer.toString(ew3.getY()));
        this.children.add(this.sizeYEdit);
        (this.sizeZEdit = new EditBox(this.font, this.width / 2 + 8, 120, 80, 20, I18n.get("structure_block.size.z"))).setMaxLength(15);
        this.sizeZEdit.setValue(Integer.toString(ew3.getZ()));
        this.children.add(this.sizeZEdit);
        (this.integrityEdit = new EditBox(this.font, this.width / 2 - 152, 120, 80, 20, I18n.get("structure_block.integrity.integrity"))).setMaxLength(15);
        this.integrityEdit.setValue(this.decimalFormat.format((double)this.structure.getIntegrity()));
        this.children.add(this.integrityEdit);
        (this.seedEdit = new EditBox(this.font, this.width / 2 - 72, 120, 80, 20, I18n.get("structure_block.integrity.seed"))).setMaxLength(31);
        this.seedEdit.setValue(Long.toString(this.structure.getSeed()));
        this.children.add(this.seedEdit);
        (this.dataEdit = new EditBox(this.font, this.width / 2 - 152, 120, 240, 20, I18n.get("structure_block.custom_data"))).setMaxLength(128);
        this.dataEdit.setValue(this.structure.getMetaData());
        this.children.add(this.dataEdit);
        this.initialMirror = this.structure.getMirror();
        this.updateMirrorButton();
        this.initialRotation = this.structure.getRotation();
        this.updateDirectionButtons();
        this.initialMode = this.structure.getMode();
        this.updateMode();
        this.initialEntityIgnoring = this.structure.isIgnoreEntities();
        this.updateEntitiesButton();
        this.initialShowAir = this.structure.getShowAir();
        this.updateToggleAirButton();
        this.initialShowBoundingBox = this.structure.getShowBoundingBox();
        this.updateToggleBoundingBox();
        this.setInitialFocus(this.nameEdit);
    }
    
    @Override
    public void resize(final Minecraft cyc, final int integer2, final int integer3) {
        final String string5 = this.nameEdit.getValue();
        final String string6 = this.posXEdit.getValue();
        final String string7 = this.posYEdit.getValue();
        final String string8 = this.posZEdit.getValue();
        final String string9 = this.sizeXEdit.getValue();
        final String string10 = this.sizeYEdit.getValue();
        final String string11 = this.sizeZEdit.getValue();
        final String string12 = this.integrityEdit.getValue();
        final String string13 = this.seedEdit.getValue();
        final String string14 = this.dataEdit.getValue();
        this.init(cyc, integer2, integer3);
        this.nameEdit.setValue(string5);
        this.posXEdit.setValue(string6);
        this.posYEdit.setValue(string7);
        this.posZEdit.setValue(string8);
        this.sizeXEdit.setValue(string9);
        this.sizeYEdit.setValue(string10);
        this.sizeZEdit.setValue(string11);
        this.integrityEdit.setValue(string12);
        this.seedEdit.setValue(string13);
        this.dataEdit.setValue(string14);
    }
    
    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }
    
    private void updateEntitiesButton() {
        final boolean boolean2 = !this.structure.isIgnoreEntities();
        if (boolean2) {
            this.entitiesButton.setMessage(I18n.get("options.on"));
        }
        else {
            this.entitiesButton.setMessage(I18n.get("options.off"));
        }
    }
    
    private void updateToggleAirButton() {
        final boolean boolean2 = this.structure.getShowAir();
        if (boolean2) {
            this.toggleAirButton.setMessage(I18n.get("options.on"));
        }
        else {
            this.toggleAirButton.setMessage(I18n.get("options.off"));
        }
    }
    
    private void updateToggleBoundingBox() {
        final boolean boolean2 = this.structure.getShowBoundingBox();
        if (boolean2) {
            this.toggleBoundingBox.setMessage(I18n.get("options.on"));
        }
        else {
            this.toggleBoundingBox.setMessage(I18n.get("options.off"));
        }
    }
    
    private void updateMirrorButton() {
        final Mirror bqg2 = this.structure.getMirror();
        switch (bqg2) {
            case NONE: {
                this.mirrorButton.setMessage("|");
                break;
            }
            case LEFT_RIGHT: {
                this.mirrorButton.setMessage("< >");
                break;
            }
            case FRONT_BACK: {
                this.mirrorButton.setMessage("^ v");
                break;
            }
        }
    }
    
    private void updateDirectionButtons() {
        this.rot0Button.active = true;
        this.rot90Button.active = true;
        this.rot180Button.active = true;
        this.rot270Button.active = true;
        switch (this.structure.getRotation()) {
            case NONE: {
                this.rot0Button.active = false;
                break;
            }
            case CLOCKWISE_180: {
                this.rot180Button.active = false;
                break;
            }
            case COUNTERCLOCKWISE_90: {
                this.rot270Button.active = false;
                break;
            }
            case CLOCKWISE_90: {
                this.rot90Button.active = false;
                break;
            }
        }
    }
    
    private void updateMode() {
        this.nameEdit.setVisible(false);
        this.posXEdit.setVisible(false);
        this.posYEdit.setVisible(false);
        this.posZEdit.setVisible(false);
        this.sizeXEdit.setVisible(false);
        this.sizeYEdit.setVisible(false);
        this.sizeZEdit.setVisible(false);
        this.integrityEdit.setVisible(false);
        this.seedEdit.setVisible(false);
        this.dataEdit.setVisible(false);
        this.saveButton.visible = false;
        this.loadButton.visible = false;
        this.detectButton.visible = false;
        this.entitiesButton.visible = false;
        this.mirrorButton.visible = false;
        this.rot0Button.visible = false;
        this.rot90Button.visible = false;
        this.rot180Button.visible = false;
        this.rot270Button.visible = false;
        this.toggleAirButton.visible = false;
        this.toggleBoundingBox.visible = false;
        switch (this.structure.getMode()) {
            case SAVE: {
                this.nameEdit.setVisible(true);
                this.posXEdit.setVisible(true);
                this.posYEdit.setVisible(true);
                this.posZEdit.setVisible(true);
                this.sizeXEdit.setVisible(true);
                this.sizeYEdit.setVisible(true);
                this.sizeZEdit.setVisible(true);
                this.saveButton.visible = true;
                this.detectButton.visible = true;
                this.entitiesButton.visible = true;
                this.toggleAirButton.visible = true;
                break;
            }
            case LOAD: {
                this.nameEdit.setVisible(true);
                this.posXEdit.setVisible(true);
                this.posYEdit.setVisible(true);
                this.posZEdit.setVisible(true);
                this.integrityEdit.setVisible(true);
                this.seedEdit.setVisible(true);
                this.loadButton.visible = true;
                this.entitiesButton.visible = true;
                this.mirrorButton.visible = true;
                this.rot0Button.visible = true;
                this.rot90Button.visible = true;
                this.rot180Button.visible = true;
                this.rot270Button.visible = true;
                this.toggleBoundingBox.visible = true;
                this.updateDirectionButtons();
                break;
            }
            case CORNER: {
                this.nameEdit.setVisible(true);
                break;
            }
            case DATA: {
                this.dataEdit.setVisible(true);
                break;
            }
        }
        this.modeButton.setMessage(I18n.get("structure_block.mode." + this.structure.getMode().getSerializedName()));
    }
    
    private boolean sendToServer(final StructureBlockEntity.UpdateType a) {
        final BlockPos ew3 = new BlockPos(this.parseCoordinate(this.posXEdit.getValue()), this.parseCoordinate(this.posYEdit.getValue()), this.parseCoordinate(this.posZEdit.getValue()));
        final BlockPos ew4 = new BlockPos(this.parseCoordinate(this.sizeXEdit.getValue()), this.parseCoordinate(this.sizeYEdit.getValue()), this.parseCoordinate(this.sizeZEdit.getValue()));
        final float float5 = this.parseIntegrity(this.integrityEdit.getValue());
        final long long6 = this.parseSeed(this.seedEdit.getValue());
        this.minecraft.getConnection().send(new ServerboundSetStructureBlockPacket(this.structure.getBlockPos(), a, this.structure.getMode(), this.nameEdit.getValue(), ew3, ew4, this.structure.getMirror(), this.structure.getRotation(), this.dataEdit.getValue(), this.structure.isIgnoreEntities(), this.structure.getShowAir(), this.structure.getShowBoundingBox(), float5, long6));
        return true;
    }
    
    private long parseSeed(final String string) {
        try {
            return Long.valueOf(string);
        }
        catch (NumberFormatException numberFormatException3) {
            return 0L;
        }
    }
    
    private float parseIntegrity(final String string) {
        try {
            return Float.valueOf(string);
        }
        catch (NumberFormatException numberFormatException3) {
            return 1.0f;
        }
    }
    
    private int parseCoordinate(final String string) {
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException numberFormatException3) {
            return 0;
        }
    }
    
    @Override
    public void onClose() {
        this.onCancel();
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (super.keyPressed(integer1, integer2, integer3)) {
            return true;
        }
        if (integer1 == 257 || integer1 == 335) {
            this.onDone();
            return true;
        }
        return false;
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        final StructureMode bxb5 = this.structure.getMode();
        this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 10, 16777215);
        if (bxb5 != StructureMode.DATA) {
            this.drawString(this.font, I18n.get("structure_block.structure_name"), this.width / 2 - 153, 30, 10526880);
            this.nameEdit.render(integer1, integer2, float3);
        }
        if (bxb5 == StructureMode.LOAD || bxb5 == StructureMode.SAVE) {
            this.drawString(this.font, I18n.get("structure_block.position"), this.width / 2 - 153, 70, 10526880);
            this.posXEdit.render(integer1, integer2, float3);
            this.posYEdit.render(integer1, integer2, float3);
            this.posZEdit.render(integer1, integer2, float3);
            final String string6 = I18n.get("structure_block.include_entities");
            final int integer3 = this.font.width(string6);
            this.drawString(this.font, string6, this.width / 2 + 154 - integer3, 150, 10526880);
        }
        if (bxb5 == StructureMode.SAVE) {
            this.drawString(this.font, I18n.get("structure_block.size"), this.width / 2 - 153, 110, 10526880);
            this.sizeXEdit.render(integer1, integer2, float3);
            this.sizeYEdit.render(integer1, integer2, float3);
            this.sizeZEdit.render(integer1, integer2, float3);
            final String string6 = I18n.get("structure_block.detect_size");
            final int integer3 = this.font.width(string6);
            this.drawString(this.font, string6, this.width / 2 + 154 - integer3, 110, 10526880);
            final String string7 = I18n.get("structure_block.show_air");
            final int integer4 = this.font.width(string7);
            this.drawString(this.font, string7, this.width / 2 + 154 - integer4, 70, 10526880);
        }
        if (bxb5 == StructureMode.LOAD) {
            this.drawString(this.font, I18n.get("structure_block.integrity"), this.width / 2 - 153, 110, 10526880);
            this.integrityEdit.render(integer1, integer2, float3);
            this.seedEdit.render(integer1, integer2, float3);
            final String string6 = I18n.get("structure_block.show_boundingbox");
            final int integer3 = this.font.width(string6);
            this.drawString(this.font, string6, this.width / 2 + 154 - integer3, 70, 10526880);
        }
        if (bxb5 == StructureMode.DATA) {
            this.drawString(this.font, I18n.get("structure_block.custom_data"), this.width / 2 - 153, 110, 10526880);
            this.dataEdit.render(integer1, integer2, float3);
        }
        final String string6 = "structure_block.mode_info." + bxb5.getSerializedName();
        this.drawString(this.font, I18n.get(string6), this.width / 2 - 153, 174, 10526880);
        super.render(integer1, integer2, float3);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
