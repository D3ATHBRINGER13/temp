package net.minecraft.client.renderer.entity.player;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.PlayerModelPart;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.Deadmau5EarsLayer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;

public class PlayerRenderer extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public PlayerRenderer(final EntityRenderDispatcher dsa) {
        this(dsa, false);
    }
    
    public PlayerRenderer(final EntityRenderDispatcher dsa, final boolean boolean2) {
        super(dsa, new PlayerModel(0.0f, boolean2), 0.5f);
        this.addLayer((RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>)new HumanoidArmorLayer((RenderLayerParent<LivingEntity, HumanoidModel>)this, new HumanoidModel(0.5f), new HumanoidModel(1.0f)));
        this.addLayer(new ItemInHandLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>(this));
        this.addLayer(new ArrowLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>(this));
        this.addLayer(new Deadmau5EarsLayer(this));
        this.addLayer(new CapeLayer(this));
        this.addLayer(new CustomHeadLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>(this));
        this.addLayer(new ElytraLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>(this));
        this.addLayer((RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>)new ParrotOnShoulderLayer((RenderLayerParent<Player, PlayerModel<Player>>)this));
        this.addLayer((RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>)new SpinAttackEffectLayer((RenderLayerParent<LivingEntity, PlayerModel<LivingEntity>>)this));
    }
    
    @Override
    public void render(final AbstractClientPlayer dmm, final double double2, final double double3, final double double4, final float float5, final float float6) {
        if (dmm.isLocalPlayer() && this.entityRenderDispatcher.camera.getEntity() != dmm) {
            return;
        }
        double double5 = double3;
        if (dmm.isVisuallySneaking()) {
            double5 -= 0.125;
        }
        this.setModelProperties(dmm);
        GlStateManager.setProfile(GlStateManager.Profile.PLAYER_SKIN);
        super.render(dmm, double2, double5, double4, float5, float6);
        GlStateManager.unsetProfile(GlStateManager.Profile.PLAYER_SKIN);
    }
    
    private void setModelProperties(final AbstractClientPlayer dmm) {
        final PlayerModel<AbstractClientPlayer> dif3 = ((LivingEntityRenderer<T, PlayerModel<AbstractClientPlayer>>)this).getModel();
        if (dmm.isSpectator()) {
            dif3.setAllVisible(false);
            dif3.head.visible = true;
            dif3.hat.visible = true;
        }
        else {
            final ItemStack bcj4 = dmm.getMainHandItem();
            final ItemStack bcj5 = dmm.getOffhandItem();
            dif3.setAllVisible(true);
            dif3.hat.visible = dmm.isModelPartShown(PlayerModelPart.HAT);
            dif3.jacket.visible = dmm.isModelPartShown(PlayerModelPart.JACKET);
            dif3.leftPants.visible = dmm.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
            dif3.rightPants.visible = dmm.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
            dif3.leftSleeve.visible = dmm.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
            dif3.rightSleeve.visible = dmm.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
            dif3.sneaking = dmm.isVisuallySneaking();
            final HumanoidModel.ArmPose a6 = this.getArmPose(dmm, bcj4, bcj5, InteractionHand.MAIN_HAND);
            final HumanoidModel.ArmPose a7 = this.getArmPose(dmm, bcj4, bcj5, InteractionHand.OFF_HAND);
            if (dmm.getMainArm() == HumanoidArm.RIGHT) {
                dif3.rightArmPose = a6;
                dif3.leftArmPose = a7;
            }
            else {
                dif3.rightArmPose = a7;
                dif3.leftArmPose = a6;
            }
        }
    }
    
    private HumanoidModel.ArmPose getArmPose(final AbstractClientPlayer dmm, final ItemStack bcj2, final ItemStack bcj3, final InteractionHand ahi) {
        HumanoidModel.ArmPose a6 = HumanoidModel.ArmPose.EMPTY;
        final ItemStack bcj4 = (ahi == InteractionHand.MAIN_HAND) ? bcj2 : bcj3;
        if (!bcj4.isEmpty()) {
            a6 = HumanoidModel.ArmPose.ITEM;
            if (dmm.getUseItemRemainingTicks() > 0) {
                final UseAnim bdt8 = bcj4.getUseAnimation();
                if (bdt8 == UseAnim.BLOCK) {
                    a6 = HumanoidModel.ArmPose.BLOCK;
                }
                else if (bdt8 == UseAnim.BOW) {
                    a6 = HumanoidModel.ArmPose.BOW_AND_ARROW;
                }
                else if (bdt8 == UseAnim.SPEAR) {
                    a6 = HumanoidModel.ArmPose.THROW_SPEAR;
                }
                else if (bdt8 == UseAnim.CROSSBOW && ahi == dmm.getUsedItemHand()) {
                    a6 = HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                }
            }
            else {
                final boolean boolean8 = bcj2.getItem() == Items.CROSSBOW;
                final boolean boolean9 = CrossbowItem.isCharged(bcj2);
                final boolean boolean10 = bcj3.getItem() == Items.CROSSBOW;
                final boolean boolean11 = CrossbowItem.isCharged(bcj3);
                if (boolean8 && boolean9) {
                    a6 = HumanoidModel.ArmPose.CROSSBOW_HOLD;
                }
                if (boolean10 && boolean11 && bcj2.getItem().getUseAnimation(bcj2) == UseAnim.NONE) {
                    a6 = HumanoidModel.ArmPose.CROSSBOW_HOLD;
                }
            }
        }
        return a6;
    }
    
    public ResourceLocation getTextureLocation(final AbstractClientPlayer dmm) {
        return dmm.getSkinTextureLocation();
    }
    
    @Override
    protected void scale(final AbstractClientPlayer dmm, final float float2) {
        final float float3 = 0.9375f;
        GlStateManager.scalef(0.9375f, 0.9375f, 0.9375f);
    }
    
    protected void renderNameTags(final AbstractClientPlayer dmm, final double double2, double double3, final double double4, final String string, final double double6) {
        if (double6 < 100.0) {
            final Scoreboard cti12 = dmm.getScoreboard();
            final Objective ctf13 = cti12.getDisplayObjective(2);
            if (ctf13 != null) {
                final Score cth14 = cti12.getOrCreatePlayerScore(dmm.getScoreboardName(), ctf13);
                this.renderNameTag((T)dmm, new StringBuilder().append(cth14.getScore()).append(" ").append(ctf13.getDisplayName().getColoredString()).toString(), double2, double3, double4, 64);
                final double n = double3;
                this.getFont().getClass();
                double3 = n + 9.0f * 1.15f * 0.025f;
            }
        }
        super.renderNameTags((T)dmm, double2, double3, double4, string, double6);
    }
    
    public void renderRightHand(final AbstractClientPlayer dmm) {
        final float float3 = 1.0f;
        GlStateManager.color3f(1.0f, 1.0f, 1.0f);
        final float float4 = 0.0625f;
        final PlayerModel<AbstractClientPlayer> dif5 = ((LivingEntityRenderer<T, PlayerModel<AbstractClientPlayer>>)this).getModel();
        this.setModelProperties(dmm);
        GlStateManager.enableBlend();
        dif5.attackTime = 0.0f;
        dif5.sneaking = false;
        dif5.setupAnim(dmm, dif5.swimAmount = 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        dif5.rightArm.xRot = 0.0f;
        dif5.rightArm.render(0.0625f);
        dif5.rightSleeve.xRot = 0.0f;
        dif5.rightSleeve.render(0.0625f);
        GlStateManager.disableBlend();
    }
    
    public void renderLeftHand(final AbstractClientPlayer dmm) {
        final float float3 = 1.0f;
        GlStateManager.color3f(1.0f, 1.0f, 1.0f);
        final float float4 = 0.0625f;
        final PlayerModel<AbstractClientPlayer> dif5 = ((LivingEntityRenderer<T, PlayerModel<AbstractClientPlayer>>)this).getModel();
        this.setModelProperties(dmm);
        GlStateManager.enableBlend();
        dif5.sneaking = false;
        dif5.attackTime = 0.0f;
        dif5.setupAnim(dmm, dif5.swimAmount = 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        dif5.leftArm.xRot = 0.0f;
        dif5.leftArm.render(0.0625f);
        dif5.leftSleeve.xRot = 0.0f;
        dif5.leftSleeve.render(0.0625f);
        GlStateManager.disableBlend();
    }
    
    @Override
    protected void setupRotations(final AbstractClientPlayer dmm, final float float2, final float float3, final float float4) {
        final float float5 = dmm.getSwimAmount(float4);
        if (dmm.isFallFlying()) {
            super.setupRotations(dmm, float2, float3, float4);
            final float float6 = dmm.getFallFlyingTicks() + float4;
            final float float7 = Mth.clamp(float6 * float6 / 100.0f, 0.0f, 1.0f);
            if (!dmm.isAutoSpinAttack()) {
                GlStateManager.rotatef(float7 * (-90.0f - dmm.xRot), 1.0f, 0.0f, 0.0f);
            }
            final Vec3 csi9 = dmm.getViewVector(float4);
            final Vec3 csi10 = dmm.getDeltaMovement();
            final double double11 = Entity.getHorizontalDistanceSqr(csi10);
            final double double12 = Entity.getHorizontalDistanceSqr(csi9);
            if (double11 > 0.0 && double12 > 0.0) {
                final double double13 = (csi10.x * csi9.x + csi10.z * csi9.z) / (Math.sqrt(double11) * Math.sqrt(double12));
                final double double14 = csi10.x * csi9.z - csi10.z * csi9.x;
                GlStateManager.rotatef((float)(Math.signum(double14) * Math.acos(double13)) * 180.0f / 3.1415927f, 0.0f, 1.0f, 0.0f);
            }
        }
        else if (float5 > 0.0f) {
            super.setupRotations(dmm, float2, float3, float4);
            final float float6 = dmm.isInWater() ? (-90.0f - dmm.xRot) : -90.0f;
            final float float7 = Mth.lerp(float5, 0.0f, float6);
            GlStateManager.rotatef(float7, 1.0f, 0.0f, 0.0f);
            if (dmm.isVisuallySwimming()) {
                GlStateManager.translatef(0.0f, -1.0f, 0.3f);
            }
        }
        else {
            super.setupRotations(dmm, float2, float3, float4);
        }
    }
}
