package net.minecraft.client.renderer;

import java.util.Objects;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Items;
import com.google.common.base.MoreObjects;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.MapItem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.client.player.AbstractClientPlayer;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.core.BlockPos;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockLayer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class ItemInHandRenderer {
    private static final ResourceLocation MAP_BACKGROUND_LOCATION;
    private static final ResourceLocation UNDERWATER_LOCATION;
    private final Minecraft minecraft;
    private ItemStack mainHandItem;
    private ItemStack offHandItem;
    private float mainHandHeight;
    private float oMainHandHeight;
    private float offHandHeight;
    private float oOffHandHeight;
    private final EntityRenderDispatcher entityRenderDispatcher;
    private final ItemRenderer itemRenderer;
    
    public ItemInHandRenderer(final Minecraft cyc) {
        this.mainHandItem = ItemStack.EMPTY;
        this.offHandItem = ItemStack.EMPTY;
        this.minecraft = cyc;
        this.entityRenderDispatcher = cyc.getEntityRenderDispatcher();
        this.itemRenderer = cyc.getItemRenderer();
    }
    
    public void renderItem(final LivingEntity aix, final ItemStack bcj, final ItemTransforms.TransformType b) {
        this.renderItem(aix, bcj, b, false);
    }
    
    public void renderItem(final LivingEntity aix, final ItemStack bcj, final ItemTransforms.TransformType b, final boolean boolean4) {
        if (bcj.isEmpty()) {
            return;
        }
        final Item bce6 = bcj.getItem();
        final Block bmv7 = Block.byItem(bce6);
        GlStateManager.pushMatrix();
        final boolean boolean5 = this.itemRenderer.isGui3d(bcj) && bmv7.getRenderLayer() == BlockLayer.TRANSLUCENT;
        if (boolean5) {
            GlStateManager.depthMask(false);
        }
        this.itemRenderer.renderWithMobState(bcj, aix, b, boolean4);
        if (boolean5) {
            GlStateManager.depthMask(true);
        }
        GlStateManager.popMatrix();
    }
    
    private void enableLight(final float float1, final float float2) {
        GlStateManager.pushMatrix();
        GlStateManager.rotatef(float1, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(float2, 0.0f, 1.0f, 0.0f);
        Lighting.turnOn();
        GlStateManager.popMatrix();
    }
    
    private void setLightValue() {
        final AbstractClientPlayer dmm2 = this.minecraft.player;
        final int integer3 = this.minecraft.level.getLightColor(new BlockPos(dmm2.x, dmm2.y + dmm2.getEyeHeight(), dmm2.z), 0);
        final float float4 = (float)(integer3 & 0xFFFF);
        final float float5 = (float)(integer3 >> 16);
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, float4, float5);
    }
    
    private void setPlayerBob(final float float1) {
        final LocalPlayer dmp3 = this.minecraft.player;
        final float float2 = Mth.lerp(float1, dmp3.xBobO, dmp3.xBob);
        final float float3 = Mth.lerp(float1, dmp3.yBobO, dmp3.yBob);
        GlStateManager.rotatef((dmp3.getViewXRot(float1) - float2) * 0.1f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef((dmp3.getViewYRot(float1) - float3) * 0.1f, 0.0f, 1.0f, 0.0f);
    }
    
    private float calculateMapTilt(final float float1) {
        float float2 = 1.0f - float1 / 45.0f + 0.1f;
        float2 = Mth.clamp(float2, 0.0f, 1.0f);
        float2 = -Mth.cos(float2 * 3.1415927f) * 0.5f + 0.5f;
        return float2;
    }
    
    private void renderMapHands() {
        if (this.minecraft.player.isInvisible()) {
            return;
        }
        GlStateManager.disableCull();
        GlStateManager.pushMatrix();
        GlStateManager.rotatef(90.0f, 0.0f, 1.0f, 0.0f);
        this.renderMapHand(HumanoidArm.RIGHT);
        this.renderMapHand(HumanoidArm.LEFT);
        GlStateManager.popMatrix();
        GlStateManager.enableCull();
    }
    
    private void renderMapHand(final HumanoidArm aiw) {
        this.minecraft.getTextureManager().bind(this.minecraft.player.getSkinTextureLocation());
        final EntityRenderer<AbstractClientPlayer> dsb3 = this.entityRenderDispatcher.<LocalPlayer, EntityRenderer<AbstractClientPlayer>>getRenderer(this.minecraft.player);
        final PlayerRenderer dwn4 = (PlayerRenderer)dsb3;
        GlStateManager.pushMatrix();
        final float float5 = (aiw == HumanoidArm.RIGHT) ? 1.0f : -1.0f;
        GlStateManager.rotatef(92.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(45.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(float5 * -41.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.translatef(float5 * 0.3f, -1.1f, 0.45f);
        if (aiw == HumanoidArm.RIGHT) {
            dwn4.renderRightHand(this.minecraft.player);
        }
        else {
            dwn4.renderLeftHand(this.minecraft.player);
        }
        GlStateManager.popMatrix();
    }
    
    private void renderOneHandedMap(final float float1, final HumanoidArm aiw, final float float3, final ItemStack bcj) {
        final float float4 = (aiw == HumanoidArm.RIGHT) ? 1.0f : -1.0f;
        GlStateManager.translatef(float4 * 0.125f, -0.125f, 0.0f);
        if (!this.minecraft.player.isInvisible()) {
            GlStateManager.pushMatrix();
            GlStateManager.rotatef(float4 * 10.0f, 0.0f, 0.0f, 1.0f);
            this.renderPlayerArm(float1, float3, aiw);
            GlStateManager.popMatrix();
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef(float4 * 0.51f, -0.08f + float1 * -1.2f, -0.75f);
        final float float5 = Mth.sqrt(float3);
        final float float6 = Mth.sin(float5 * 3.1415927f);
        final float float7 = -0.5f * float6;
        final float float8 = 0.4f * Mth.sin(float5 * 6.2831855f);
        final float float9 = -0.3f * Mth.sin(float3 * 3.1415927f);
        GlStateManager.translatef(float4 * float7, float8 - 0.3f * float6, float9);
        GlStateManager.rotatef(float6 * -45.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(float4 * float6 * -30.0f, 0.0f, 1.0f, 0.0f);
        this.renderMap(bcj);
        GlStateManager.popMatrix();
    }
    
    private void renderTwoHandedMap(final float float1, final float float2, final float float3) {
        final float float4 = Mth.sqrt(float3);
        final float float5 = -0.2f * Mth.sin(float3 * 3.1415927f);
        final float float6 = -0.4f * Mth.sin(float4 * 3.1415927f);
        GlStateManager.translatef(0.0f, -float5 / 2.0f, float6);
        final float float7 = this.calculateMapTilt(float1);
        GlStateManager.translatef(0.0f, 0.04f + float2 * -1.2f + float7 * -0.5f, -0.72f);
        GlStateManager.rotatef(float7 * -85.0f, 1.0f, 0.0f, 0.0f);
        this.renderMapHands();
        final float float8 = Mth.sin(float4 * 3.1415927f);
        GlStateManager.rotatef(float8 * 20.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.scalef(2.0f, 2.0f, 2.0f);
        this.renderMap(this.mainHandItem);
    }
    
    private void renderMap(final ItemStack bcj) {
        GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.scalef(0.38f, 0.38f, 0.38f);
        GlStateManager.disableLighting();
        this.minecraft.getTextureManager().bind(ItemInHandRenderer.MAP_BACKGROUND_LOCATION);
        final Tesselator cuz3 = Tesselator.getInstance();
        final BufferBuilder cuw4 = cuz3.getBuilder();
        GlStateManager.translatef(-0.5f, -0.5f, 0.0f);
        GlStateManager.scalef(0.0078125f, 0.0078125f, 0.0078125f);
        cuw4.begin(7, DefaultVertexFormat.POSITION_TEX);
        cuw4.vertex(-7.0, 135.0, 0.0).uv(0.0, 1.0).endVertex();
        cuw4.vertex(135.0, 135.0, 0.0).uv(1.0, 1.0).endVertex();
        cuw4.vertex(135.0, -7.0, 0.0).uv(1.0, 0.0).endVertex();
        cuw4.vertex(-7.0, -7.0, 0.0).uv(0.0, 0.0).endVertex();
        cuz3.end();
        final MapItemSavedData coh5 = MapItem.getOrCreateSavedData(bcj, this.minecraft.level);
        if (coh5 != null) {
            this.minecraft.gameRenderer.getMapRenderer().render(coh5, false);
        }
        GlStateManager.enableLighting();
    }
    
    private void renderPlayerArm(final float float1, final float float2, final HumanoidArm aiw) {
        final boolean boolean5 = aiw != HumanoidArm.LEFT;
        final float float3 = boolean5 ? 1.0f : -1.0f;
        final float float4 = Mth.sqrt(float2);
        final float float5 = -0.3f * Mth.sin(float4 * 3.1415927f);
        final float float6 = 0.4f * Mth.sin(float4 * 6.2831855f);
        final float float7 = -0.4f * Mth.sin(float2 * 3.1415927f);
        GlStateManager.translatef(float3 * (float5 + 0.64000005f), float6 - 0.6f + float1 * -0.6f, float7 - 0.71999997f);
        GlStateManager.rotatef(float3 * 45.0f, 0.0f, 1.0f, 0.0f);
        final float float8 = Mth.sin(float2 * float2 * 3.1415927f);
        final float float9 = Mth.sin(float4 * 3.1415927f);
        GlStateManager.rotatef(float3 * float9 * 70.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(float3 * float8 * -20.0f, 0.0f, 0.0f, 1.0f);
        final AbstractClientPlayer dmm13 = this.minecraft.player;
        this.minecraft.getTextureManager().bind(dmm13.getSkinTextureLocation());
        GlStateManager.translatef(float3 * -1.0f, 3.6f, 3.5f);
        GlStateManager.rotatef(float3 * 120.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotatef(200.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(float3 * -135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(float3 * 5.6f, 0.0f, 0.0f);
        final PlayerRenderer dwn14 = this.entityRenderDispatcher.<AbstractClientPlayer, PlayerRenderer>getRenderer(dmm13);
        GlStateManager.disableCull();
        if (boolean5) {
            dwn14.renderRightHand(dmm13);
        }
        else {
            dwn14.renderLeftHand(dmm13);
        }
        GlStateManager.enableCull();
    }
    
    private void applyEatTransform(final float float1, final HumanoidArm aiw, final ItemStack bcj) {
        final float float2 = this.minecraft.player.getUseItemRemainingTicks() - float1 + 1.0f;
        final float float3 = float2 / bcj.getUseDuration();
        if (float3 < 0.8f) {
            final float float4 = Mth.abs(Mth.cos(float2 / 4.0f * 3.1415927f) * 0.1f);
            GlStateManager.translatef(0.0f, float4, 0.0f);
        }
        final float float4 = 1.0f - (float)Math.pow((double)float3, 27.0);
        final int integer8 = (aiw == HumanoidArm.RIGHT) ? 1 : -1;
        GlStateManager.translatef(float4 * 0.6f * integer8, float4 * -0.5f, float4 * 0.0f);
        GlStateManager.rotatef(integer8 * float4 * 90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(float4 * 10.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(integer8 * float4 * 30.0f, 0.0f, 0.0f, 1.0f);
    }
    
    private void applyItemArmAttackTransform(final HumanoidArm aiw, final float float2) {
        final int integer4 = (aiw == HumanoidArm.RIGHT) ? 1 : -1;
        final float float3 = Mth.sin(float2 * float2 * 3.1415927f);
        GlStateManager.rotatef(integer4 * (45.0f + float3 * -20.0f), 0.0f, 1.0f, 0.0f);
        final float float4 = Mth.sin(Mth.sqrt(float2) * 3.1415927f);
        GlStateManager.rotatef(integer4 * float4 * -20.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotatef(float4 * -80.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(integer4 * -45.0f, 0.0f, 1.0f, 0.0f);
    }
    
    private void applyItemArmTransform(final HumanoidArm aiw, final float float2) {
        final int integer4 = (aiw == HumanoidArm.RIGHT) ? 1 : -1;
        GlStateManager.translatef(integer4 * 0.56f, -0.52f + float2 * -0.6f, -0.72f);
    }
    
    public void render(final float float1) {
        final AbstractClientPlayer dmm3 = this.minecraft.player;
        final float float2 = dmm3.getAttackAnim(float1);
        final InteractionHand ahi5 = (InteractionHand)MoreObjects.firstNonNull(dmm3.swingingArm, InteractionHand.MAIN_HAND);
        final float float3 = Mth.lerp(float1, dmm3.xRotO, dmm3.xRot);
        final float float4 = Mth.lerp(float1, dmm3.yRotO, dmm3.yRot);
        boolean boolean8 = true;
        boolean boolean9 = true;
        if (dmm3.isUsingItem()) {
            final ItemStack bcj10 = dmm3.getUseItem();
            if (bcj10.getItem() == Items.BOW || bcj10.getItem() == Items.CROSSBOW) {
                boolean8 = (dmm3.getUsedItemHand() == InteractionHand.MAIN_HAND);
                boolean9 = !boolean8;
            }
            final InteractionHand ahi6 = dmm3.getUsedItemHand();
            if (ahi6 == InteractionHand.MAIN_HAND) {
                final ItemStack bcj11 = dmm3.getOffhandItem();
                if (bcj11.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(bcj11)) {
                    boolean9 = false;
                }
            }
        }
        else {
            final ItemStack bcj10 = dmm3.getMainHandItem();
            final ItemStack bcj12 = dmm3.getOffhandItem();
            if (bcj10.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(bcj10)) {
                boolean9 = !boolean8;
            }
            if (bcj12.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(bcj12)) {
                boolean8 = !bcj10.isEmpty();
                boolean9 = !boolean8;
            }
        }
        this.enableLight(float3, float4);
        this.setLightValue();
        this.setPlayerBob(float1);
        GlStateManager.enableRescaleNormal();
        if (boolean8) {
            final float float5 = (ahi5 == InteractionHand.MAIN_HAND) ? float2 : 0.0f;
            final float float6 = 1.0f - Mth.lerp(float1, this.oMainHandHeight, this.mainHandHeight);
            this.renderArmWithItem(dmm3, float1, float3, InteractionHand.MAIN_HAND, float5, this.mainHandItem, float6);
        }
        if (boolean9) {
            final float float5 = (ahi5 == InteractionHand.OFF_HAND) ? float2 : 0.0f;
            final float float6 = 1.0f - Mth.lerp(float1, this.oOffHandHeight, this.offHandHeight);
            this.renderArmWithItem(dmm3, float1, float3, InteractionHand.OFF_HAND, float5, this.offHandItem, float6);
        }
        GlStateManager.disableRescaleNormal();
        Lighting.turnOff();
    }
    
    public void renderArmWithItem(final AbstractClientPlayer dmm, final float float2, final float float3, final InteractionHand ahi, final float float5, final ItemStack bcj, final float float7) {
        final boolean boolean9 = ahi == InteractionHand.MAIN_HAND;
        final HumanoidArm aiw10 = boolean9 ? dmm.getMainArm() : dmm.getMainArm().getOpposite();
        GlStateManager.pushMatrix();
        if (bcj.isEmpty()) {
            if (boolean9 && !dmm.isInvisible()) {
                this.renderPlayerArm(float7, float5, aiw10);
            }
        }
        else if (bcj.getItem() == Items.FILLED_MAP) {
            if (boolean9 && this.offHandItem.isEmpty()) {
                this.renderTwoHandedMap(float3, float7, float5);
            }
            else {
                this.renderOneHandedMap(float7, aiw10, float5, bcj);
            }
        }
        else if (bcj.getItem() == Items.CROSSBOW) {
            final boolean boolean10 = CrossbowItem.isCharged(bcj);
            final boolean boolean11 = aiw10 == HumanoidArm.RIGHT;
            final int integer13 = boolean11 ? 1 : -1;
            if (dmm.isUsingItem() && dmm.getUseItemRemainingTicks() > 0 && dmm.getUsedItemHand() == ahi) {
                this.applyItemArmTransform(aiw10, float7);
                GlStateManager.translatef(integer13 * -0.4785682f, -0.094387f, 0.05731531f);
                GlStateManager.rotatef(-11.935f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotatef(integer13 * 65.3f, 0.0f, 1.0f, 0.0f);
                GlStateManager.rotatef(integer13 * -9.785f, 0.0f, 0.0f, 1.0f);
                final float float8 = bcj.getUseDuration() - (this.minecraft.player.getUseItemRemainingTicks() - float2 + 1.0f);
                float float9 = float8 / CrossbowItem.getChargeDuration(bcj);
                if (float9 > 1.0f) {
                    float9 = 1.0f;
                }
                if (float9 > 0.1f) {
                    final float float10 = Mth.sin((float8 - 0.1f) * 1.3f);
                    final float float11 = float9 - 0.1f;
                    final float float12 = float10 * float11;
                    GlStateManager.translatef(float12 * 0.0f, float12 * 0.004f, float12 * 0.0f);
                }
                GlStateManager.translatef(float9 * 0.0f, float9 * 0.0f, float9 * 0.04f);
                GlStateManager.scalef(1.0f, 1.0f, 1.0f + float9 * 0.2f);
                GlStateManager.rotatef(integer13 * 45.0f, 0.0f, -1.0f, 0.0f);
            }
            else {
                final float float8 = -0.4f * Mth.sin(Mth.sqrt(float5) * 3.1415927f);
                final float float9 = 0.2f * Mth.sin(Mth.sqrt(float5) * 6.2831855f);
                final float float10 = -0.2f * Mth.sin(float5 * 3.1415927f);
                GlStateManager.translatef(integer13 * float8, float9, float10);
                this.applyItemArmTransform(aiw10, float7);
                this.applyItemArmAttackTransform(aiw10, float5);
                if (boolean10 && float5 < 0.001f) {
                    GlStateManager.translatef(integer13 * -0.641864f, 0.0f, 0.0f);
                    GlStateManager.rotatef(integer13 * 10.0f, 0.0f, 1.0f, 0.0f);
                }
            }
            this.renderItem(dmm, bcj, boolean11 ? ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !boolean11);
        }
        else {
            final boolean boolean10 = aiw10 == HumanoidArm.RIGHT;
            if (dmm.isUsingItem() && dmm.getUseItemRemainingTicks() > 0 && dmm.getUsedItemHand() == ahi) {
                final int integer14 = boolean10 ? 1 : -1;
                switch (bcj.getUseAnimation()) {
                    case NONE: {
                        this.applyItemArmTransform(aiw10, float7);
                        break;
                    }
                    case EAT:
                    case DRINK: {
                        this.applyEatTransform(float2, aiw10, bcj);
                        this.applyItemArmTransform(aiw10, float7);
                        break;
                    }
                    case BLOCK: {
                        this.applyItemArmTransform(aiw10, float7);
                        break;
                    }
                    case BOW: {
                        this.applyItemArmTransform(aiw10, float7);
                        GlStateManager.translatef(integer14 * -0.2785682f, 0.18344387f, 0.15731531f);
                        GlStateManager.rotatef(-13.935f, 1.0f, 0.0f, 0.0f);
                        GlStateManager.rotatef(integer14 * 35.3f, 0.0f, 1.0f, 0.0f);
                        GlStateManager.rotatef(integer14 * -9.785f, 0.0f, 0.0f, 1.0f);
                        final float float13 = bcj.getUseDuration() - (this.minecraft.player.getUseItemRemainingTicks() - float2 + 1.0f);
                        float float8 = float13 / 20.0f;
                        float8 = (float8 * float8 + float8 * 2.0f) / 3.0f;
                        if (float8 > 1.0f) {
                            float8 = 1.0f;
                        }
                        if (float8 > 0.1f) {
                            final float float9 = Mth.sin((float13 - 0.1f) * 1.3f);
                            final float float10 = float8 - 0.1f;
                            final float float11 = float9 * float10;
                            GlStateManager.translatef(float11 * 0.0f, float11 * 0.004f, float11 * 0.0f);
                        }
                        GlStateManager.translatef(float8 * 0.0f, float8 * 0.0f, float8 * 0.04f);
                        GlStateManager.scalef(1.0f, 1.0f, 1.0f + float8 * 0.2f);
                        GlStateManager.rotatef(integer14 * 45.0f, 0.0f, -1.0f, 0.0f);
                        break;
                    }
                    case SPEAR: {
                        this.applyItemArmTransform(aiw10, float7);
                        GlStateManager.translatef(integer14 * -0.5f, 0.7f, 0.1f);
                        GlStateManager.rotatef(-55.0f, 1.0f, 0.0f, 0.0f);
                        GlStateManager.rotatef(integer14 * 35.3f, 0.0f, 1.0f, 0.0f);
                        GlStateManager.rotatef(integer14 * -9.785f, 0.0f, 0.0f, 1.0f);
                        final float float13 = bcj.getUseDuration() - (this.minecraft.player.getUseItemRemainingTicks() - float2 + 1.0f);
                        float float8 = float13 / 10.0f;
                        if (float8 > 1.0f) {
                            float8 = 1.0f;
                        }
                        if (float8 > 0.1f) {
                            final float float9 = Mth.sin((float13 - 0.1f) * 1.3f);
                            final float float10 = float8 - 0.1f;
                            final float float11 = float9 * float10;
                            GlStateManager.translatef(float11 * 0.0f, float11 * 0.004f, float11 * 0.0f);
                        }
                        GlStateManager.translatef(0.0f, 0.0f, float8 * 0.2f);
                        GlStateManager.scalef(1.0f, 1.0f, 1.0f + float8 * 0.2f);
                        GlStateManager.rotatef(integer14 * 45.0f, 0.0f, -1.0f, 0.0f);
                        break;
                    }
                }
            }
            else if (dmm.isAutoSpinAttack()) {
                this.applyItemArmTransform(aiw10, float7);
                final int integer14 = boolean10 ? 1 : -1;
                GlStateManager.translatef(integer14 * -0.4f, 0.8f, 0.3f);
                GlStateManager.rotatef(integer14 * 65.0f, 0.0f, 1.0f, 0.0f);
                GlStateManager.rotatef(integer14 * -85.0f, 0.0f, 0.0f, 1.0f);
            }
            else {
                final float float14 = -0.4f * Mth.sin(Mth.sqrt(float5) * 3.1415927f);
                final float float13 = 0.2f * Mth.sin(Mth.sqrt(float5) * 6.2831855f);
                final float float8 = -0.2f * Mth.sin(float5 * 3.1415927f);
                final int integer15 = boolean10 ? 1 : -1;
                GlStateManager.translatef(integer15 * float14, float13, float8);
                this.applyItemArmTransform(aiw10, float7);
                this.applyItemArmAttackTransform(aiw10, float5);
            }
            this.renderItem(dmm, bcj, boolean10 ? ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !boolean10);
        }
        GlStateManager.popMatrix();
    }
    
    public void renderScreenEffect(final float float1) {
        GlStateManager.disableAlphaTest();
        if (this.minecraft.player.isInWall()) {
            BlockState bvt3 = this.minecraft.level.getBlockState(new BlockPos(this.minecraft.player));
            final Player awg4 = this.minecraft.player;
            for (int integer5 = 0; integer5 < 8; ++integer5) {
                final double double6 = awg4.x + ((integer5 >> 0) % 2 - 0.5f) * awg4.getBbWidth() * 0.8f;
                final double double7 = awg4.y + ((integer5 >> 1) % 2 - 0.5f) * 0.1f;
                final double double8 = awg4.z + ((integer5 >> 2) % 2 - 0.5f) * awg4.getBbWidth() * 0.8f;
                final BlockPos ew12 = new BlockPos(double6, double7 + awg4.getEyeHeight(), double8);
                final BlockState bvt4 = this.minecraft.level.getBlockState(ew12);
                if (bvt4.isViewBlocking(this.minecraft.level, ew12)) {
                    bvt3 = bvt4;
                }
            }
            if (bvt3.getRenderShape() != RenderShape.INVISIBLE) {
                this.renderTex(this.minecraft.getBlockRenderer().getBlockModelShaper().getParticleIcon(bvt3));
            }
        }
        if (!this.minecraft.player.isSpectator()) {
            if (this.minecraft.player.isUnderLiquid(FluidTags.WATER)) {
                this.renderWater(float1);
            }
            if (this.minecraft.player.isOnFire()) {
                this.renderFire();
            }
        }
        GlStateManager.enableAlphaTest();
    }
    
    private void renderTex(final TextureAtlasSprite dxb) {
        this.minecraft.getTextureManager().bind(TextureAtlas.LOCATION_BLOCKS);
        final Tesselator cuz3 = Tesselator.getInstance();
        final BufferBuilder cuw4 = cuz3.getBuilder();
        final float float5 = 0.1f;
        GlStateManager.color4f(0.1f, 0.1f, 0.1f, 0.5f);
        GlStateManager.pushMatrix();
        final float float6 = -1.0f;
        final float float7 = 1.0f;
        final float float8 = -1.0f;
        final float float9 = 1.0f;
        final float float10 = -0.5f;
        final float float11 = dxb.getU0();
        final float float12 = dxb.getU1();
        final float float13 = dxb.getV0();
        final float float14 = dxb.getV1();
        cuw4.begin(7, DefaultVertexFormat.POSITION_TEX);
        cuw4.vertex(-1.0, -1.0, -0.5).uv(float12, float14).endVertex();
        cuw4.vertex(1.0, -1.0, -0.5).uv(float11, float14).endVertex();
        cuw4.vertex(1.0, 1.0, -0.5).uv(float11, float13).endVertex();
        cuw4.vertex(-1.0, 1.0, -0.5).uv(float12, float13).endVertex();
        cuz3.end();
        GlStateManager.popMatrix();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    private void renderWater(final float float1) {
        this.minecraft.getTextureManager().bind(ItemInHandRenderer.UNDERWATER_LOCATION);
        final Tesselator cuz3 = Tesselator.getInstance();
        final BufferBuilder cuw4 = cuz3.getBuilder();
        final float float2 = this.minecraft.player.getBrightness();
        GlStateManager.color4f(float2, float2, float2, 0.1f);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        final float float3 = 4.0f;
        final float float4 = -1.0f;
        final float float5 = 1.0f;
        final float float6 = -1.0f;
        final float float7 = 1.0f;
        final float float8 = -0.5f;
        final float float9 = -this.minecraft.player.yRot / 64.0f;
        final float float10 = this.minecraft.player.xRot / 64.0f;
        cuw4.begin(7, DefaultVertexFormat.POSITION_TEX);
        cuw4.vertex(-1.0, -1.0, -0.5).uv(4.0f + float9, 4.0f + float10).endVertex();
        cuw4.vertex(1.0, -1.0, -0.5).uv(0.0f + float9, 4.0f + float10).endVertex();
        cuw4.vertex(1.0, 1.0, -0.5).uv(0.0f + float9, 0.0f + float10).endVertex();
        cuw4.vertex(-1.0, 1.0, -0.5).uv(4.0f + float9, 0.0f + float10).endVertex();
        cuz3.end();
        GlStateManager.popMatrix();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
    }
    
    private void renderFire() {
        final Tesselator cuz2 = Tesselator.getInstance();
        final BufferBuilder cuw3 = cuz2.getBuilder();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 0.9f);
        GlStateManager.depthFunc(519);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        final float float4 = 1.0f;
        for (int integer5 = 0; integer5 < 2; ++integer5) {
            GlStateManager.pushMatrix();
            final TextureAtlasSprite dxb6 = this.minecraft.getTextureAtlas().getSprite(ModelBakery.FIRE_1);
            this.minecraft.getTextureManager().bind(TextureAtlas.LOCATION_BLOCKS);
            final float float5 = dxb6.getU0();
            final float float6 = dxb6.getU1();
            final float float7 = dxb6.getV0();
            final float float8 = dxb6.getV1();
            final float float9 = -0.5f;
            final float float10 = 0.5f;
            final float float11 = -0.5f;
            final float float12 = 0.5f;
            final float float13 = -0.5f;
            GlStateManager.translatef(-(integer5 * 2 - 1) * 0.24f, -0.3f, 0.0f);
            GlStateManager.rotatef((integer5 * 2 - 1) * 10.0f, 0.0f, 1.0f, 0.0f);
            cuw3.begin(7, DefaultVertexFormat.POSITION_TEX);
            cuw3.vertex(-0.5, -0.5, -0.5).uv(float6, float8).endVertex();
            cuw3.vertex(0.5, -0.5, -0.5).uv(float5, float8).endVertex();
            cuw3.vertex(0.5, 0.5, -0.5).uv(float5, float7).endVertex();
            cuw3.vertex(-0.5, 0.5, -0.5).uv(float6, float7).endVertex();
            cuz2.end();
            GlStateManager.popMatrix();
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
    }
    
    public void tick() {
        this.oMainHandHeight = this.mainHandHeight;
        this.oOffHandHeight = this.offHandHeight;
        final LocalPlayer dmp2 = this.minecraft.player;
        final ItemStack bcj3 = dmp2.getMainHandItem();
        final ItemStack bcj4 = dmp2.getOffhandItem();
        if (dmp2.isHandsBusy()) {
            this.mainHandHeight = Mth.clamp(this.mainHandHeight - 0.4f, 0.0f, 1.0f);
            this.offHandHeight = Mth.clamp(this.offHandHeight - 0.4f, 0.0f, 1.0f);
        }
        else {
            final float float5 = dmp2.getAttackStrengthScale(1.0f);
            this.mainHandHeight += Mth.clamp((Objects.equals(this.mainHandItem, bcj3) ? (float5 * float5 * float5) : 0.0f) - this.mainHandHeight, -0.4f, 0.4f);
            this.offHandHeight += Mth.clamp((float)(Objects.equals(this.offHandItem, bcj4) ? 1 : 0) - this.offHandHeight, -0.4f, 0.4f);
        }
        if (this.mainHandHeight < 0.1f) {
            this.mainHandItem = bcj3;
        }
        if (this.offHandHeight < 0.1f) {
            this.offHandItem = bcj4;
        }
    }
    
    public void itemUsed(final InteractionHand ahi) {
        if (ahi == InteractionHand.MAIN_HAND) {
            this.mainHandHeight = 0.0f;
        }
        else {
            this.offHandHeight = 0.0f;
        }
    }
    
    static {
        MAP_BACKGROUND_LOCATION = new ResourceLocation("textures/map/map_background.png");
        UNDERWATER_LOCATION = new ResourceLocation("textures/misc/underwater.png");
    }
}
