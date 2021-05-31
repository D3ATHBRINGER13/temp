package net.minecraft.client.gui.screens.inventory;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.StandingSignBlock;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screens.Screen;

public class SignEditScreen extends Screen {
    private final SignBlockEntity sign;
    private int frame;
    private int line;
    private TextFieldHelper signField;
    
    public SignEditScreen(final SignBlockEntity bus) {
        super(new TranslatableComponent("sign.edit", new Object[0]));
        this.sign = bus;
    }
    
    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.<Button>addButton(new Button(this.width / 2 - 100, this.height / 4 + 120, 200, 20, I18n.get("gui.done"), czi -> this.onDone()));
        this.sign.setEditable(false);
        this.signField = new TextFieldHelper(this.minecraft, (Supplier<String>)(() -> this.sign.getMessage(this.line).getString()), (Consumer<String>)(string -> this.sign.setMessage(this.line, new TextComponent(string))), 90);
    }
    
    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
        final ClientPacketListener dkc2 = this.minecraft.getConnection();
        if (dkc2 != null) {
            dkc2.send(new ServerboundSignUpdatePacket(this.sign.getBlockPos(), this.sign.getMessage(0), this.sign.getMessage(1), this.sign.getMessage(2), this.sign.getMessage(3)));
        }
        this.sign.setEditable(true);
    }
    
    @Override
    public void tick() {
        ++this.frame;
        if (!this.sign.getType().isValid(this.sign.getBlockState().getBlock())) {
            this.onDone();
        }
    }
    
    private void onDone() {
        this.sign.setChanged();
        this.minecraft.setScreen(null);
    }
    
    @Override
    public boolean charTyped(final char character, final int integer) {
        this.signField.charTyped(character);
        return true;
    }
    
    @Override
    public void onClose() {
        this.onDone();
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (integer1 == 265) {
            this.line = (this.line - 1 & 0x3);
            this.signField.setEnd();
            return true;
        }
        if (integer1 == 264 || integer1 == 257 || integer1 == 335) {
            this.line = (this.line + 1 & 0x3);
            this.signField.setEnd();
            return true;
        }
        return this.signField.keyPressed(integer1) || super.keyPressed(integer1, integer2, integer3);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 40, 16777215);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)(this.width / 2), 0.0f, 50.0f);
        final float float4 = 93.75f;
        GlStateManager.scalef(-93.75f, -93.75f, -93.75f);
        GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
        final BlockState bvt6 = this.sign.getBlockState();
        float float5;
        if (bvt6.getBlock() instanceof StandingSignBlock) {
            float5 = bvt6.<Integer>getValue((Property<Integer>)StandingSignBlock.ROTATION) * 360 / 16.0f;
        }
        else {
            float5 = bvt6.<Direction>getValue((Property<Direction>)WallSignBlock.FACING).toYRot();
        }
        GlStateManager.rotatef(float5, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(0.0f, -1.0625f, 0.0f);
        this.sign.setCursorInfo(this.line, this.signField.getCursorPos(), this.signField.getSelectionPos(), this.frame / 6 % 2 == 0);
        BlockEntityRenderDispatcher.instance.render(this.sign, -0.5, -0.75, -0.5, 0.0f);
        this.sign.resetCursorInfo();
        GlStateManager.popMatrix();
        super.render(integer1, integer2, float3);
    }
}
