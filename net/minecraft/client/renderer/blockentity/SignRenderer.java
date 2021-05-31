package net.minecraft.client.renderer.blockentity;

import java.util.List;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.gui.Font;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import java.util.function.Function;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.StandingSignBlock;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.SignModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.SignBlockEntity;

public class SignRenderer extends BlockEntityRenderer<SignBlockEntity> {
    private static final ResourceLocation OAK_TEXTURE;
    private static final ResourceLocation SPRUCE_TEXTURE;
    private static final ResourceLocation BIRCH_TEXTURE;
    private static final ResourceLocation ACACIA_TEXTURE;
    private static final ResourceLocation JUNGLE_TEXTURE;
    private static final ResourceLocation DARK_OAK_TEXTURE;
    private final SignModel signModel;
    
    public SignRenderer() {
        this.signModel = new SignModel();
    }
    
    @Override
    public void render(final SignBlockEntity bus, final double double2, final double double3, final double double4, final float float5, final int integer) {
        final BlockState bvt11 = bus.getBlockState();
        GlStateManager.pushMatrix();
        final float float6 = 0.6666667f;
        if (bvt11.getBlock() instanceof StandingSignBlock) {
            GlStateManager.translatef((float)double2 + 0.5f, (float)double3 + 0.5f, (float)double4 + 0.5f);
            GlStateManager.rotatef(-(bvt11.<Integer>getValue((Property<Integer>)StandingSignBlock.ROTATION) * 360 / 16.0f), 0.0f, 1.0f, 0.0f);
            this.signModel.getStick().visible = true;
        }
        else {
            GlStateManager.translatef((float)double2 + 0.5f, (float)double3 + 0.5f, (float)double4 + 0.5f);
            GlStateManager.rotatef(-bvt11.<Direction>getValue((Property<Direction>)WallSignBlock.FACING).toYRot(), 0.0f, 1.0f, 0.0f);
            GlStateManager.translatef(0.0f, -0.3125f, -0.4375f);
            this.signModel.getStick().visible = false;
        }
        if (integer >= 0) {
            this.bindTexture(SignRenderer.BREAKING_LOCATIONS[integer]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(4.0f, 2.0f, 1.0f);
            GlStateManager.translatef(0.0625f, 0.0625f, 0.0625f);
            GlStateManager.matrixMode(5888);
        }
        else {
            this.bindTexture(this.getTexture(bvt11.getBlock()));
        }
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        GlStateManager.scalef(0.6666667f, -0.6666667f, -0.6666667f);
        this.signModel.render();
        GlStateManager.popMatrix();
        final Font cyu13 = this.getFont();
        final float float7 = 0.010416667f;
        GlStateManager.translatef(0.0f, 0.33333334f, 0.046666667f);
        GlStateManager.scalef(0.010416667f, -0.010416667f, 0.010416667f);
        GlStateManager.normal3f(0.0f, 0.0f, -0.010416667f);
        GlStateManager.depthMask(false);
        final int integer2 = bus.getColor().getTextColor();
        if (integer < 0) {
            for (int integer3 = 0; integer3 < 4; ++integer3) {
                final String string17 = bus.getRenderMessage(integer3, (Function<Component, String>)(jo -> {
                    final List<Component> list3 = ComponentRenderUtils.wrapComponents(jo, 90, cyu13, false, true);
                    return list3.isEmpty() ? "" : ((Component)list3.get(0)).getColoredString();
                }));
                if (string17 != null) {
                    cyu13.draw(string17, (float)(-cyu13.width(string17) / 2), (float)(integer3 * 10 - bus.messages.length * 5), integer2);
                    if (integer3 == bus.getSelectedLine() && bus.getCursorPos() >= 0) {
                        final int integer4 = cyu13.width(string17.substring(0, Math.max(Math.min(bus.getCursorPos(), string17.length()), 0)));
                        final int integer5 = cyu13.isBidirectional() ? -1 : 1;
                        final int integer6 = (integer4 - cyu13.width(string17) / 2) * integer5;
                        final int integer7 = integer3 * 10 - bus.messages.length * 5;
                        if (bus.isShowCursor()) {
                            if (bus.getCursorPos() < string17.length()) {
                                final int integer12 = integer6;
                                final int integer13 = integer7 - 1;
                                final int integer14 = integer6 + 1;
                                final int n = integer7;
                                cyu13.getClass();
                                GuiComponent.fill(integer12, integer13, integer14, n + 9, 0xFF000000 | integer2);
                            }
                            else {
                                cyu13.draw("_", (float)integer6, (float)integer7, integer2);
                            }
                        }
                        if (bus.getSelectionPos() != bus.getCursorPos()) {
                            final int integer8 = Math.min(bus.getCursorPos(), bus.getSelectionPos());
                            final int integer9 = Math.max(bus.getCursorPos(), bus.getSelectionPos());
                            final int integer10 = (cyu13.width(string17.substring(0, integer8)) - cyu13.width(string17) / 2) * integer5;
                            final int integer11 = (cyu13.width(string17.substring(0, integer9)) - cyu13.width(string17) / 2) * integer5;
                            final int min = Math.min(integer10, integer11);
                            final int integer15 = integer7;
                            final int max = Math.max(integer10, integer11);
                            final int n2 = integer7;
                            cyu13.getClass();
                            this.renderHighlight(min, integer15, max, n2 + 9);
                        }
                    }
                }
            }
        }
        GlStateManager.depthMask(true);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
        if (integer >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }
    
    private ResourceLocation getTexture(final Block bmv) {
        if (bmv == Blocks.OAK_SIGN || bmv == Blocks.OAK_WALL_SIGN) {
            return SignRenderer.OAK_TEXTURE;
        }
        if (bmv == Blocks.SPRUCE_SIGN || bmv == Blocks.SPRUCE_WALL_SIGN) {
            return SignRenderer.SPRUCE_TEXTURE;
        }
        if (bmv == Blocks.BIRCH_SIGN || bmv == Blocks.BIRCH_WALL_SIGN) {
            return SignRenderer.BIRCH_TEXTURE;
        }
        if (bmv == Blocks.ACACIA_SIGN || bmv == Blocks.ACACIA_WALL_SIGN) {
            return SignRenderer.ACACIA_TEXTURE;
        }
        if (bmv == Blocks.JUNGLE_SIGN || bmv == Blocks.JUNGLE_WALL_SIGN) {
            return SignRenderer.JUNGLE_TEXTURE;
        }
        if (bmv == Blocks.DARK_OAK_SIGN || bmv == Blocks.DARK_OAK_WALL_SIGN) {
            return SignRenderer.DARK_OAK_TEXTURE;
        }
        return SignRenderer.OAK_TEXTURE;
    }
    
    private void renderHighlight(final int integer1, final int integer2, final int integer3, final int integer4) {
        final Tesselator cuz6 = Tesselator.getInstance();
        final BufferBuilder cuw7 = cuz6.getBuilder();
        GlStateManager.color4f(0.0f, 0.0f, 255.0f, 255.0f);
        GlStateManager.disableTexture();
        GlStateManager.enableColorLogicOp();
        GlStateManager.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        cuw7.begin(7, DefaultVertexFormat.POSITION);
        cuw7.vertex(integer1, integer4, 0.0).endVertex();
        cuw7.vertex(integer3, integer4, 0.0).endVertex();
        cuw7.vertex(integer3, integer2, 0.0).endVertex();
        cuw7.vertex(integer1, integer2, 0.0).endVertex();
        cuz6.end();
        GlStateManager.disableColorLogicOp();
        GlStateManager.enableTexture();
    }
    
    static {
        OAK_TEXTURE = new ResourceLocation("textures/entity/signs/oak.png");
        SPRUCE_TEXTURE = new ResourceLocation("textures/entity/signs/spruce.png");
        BIRCH_TEXTURE = new ResourceLocation("textures/entity/signs/birch.png");
        ACACIA_TEXTURE = new ResourceLocation("textures/entity/signs/acacia.png");
        JUNGLE_TEXTURE = new ResourceLocation("textures/entity/signs/jungle.png");
        DARK_OAK_TEXTURE = new ResourceLocation("textures/entity/signs/dark_oak.png");
    }
}
