package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.HumanoidArm;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.fishing.FishingHook;

public class FishingHookRenderer extends EntityRenderer<FishingHook> {
    private static final ResourceLocation TEXTURE_LOCATION;
    
    public FishingHookRenderer(final EntityRenderDispatcher dsa) {
        super(dsa);
    }
    
    @Override
    public void render(final FishingHook ats, final double double2, final double double3, final double double4, final float float5, final float float6) {
        final Player awg11 = ats.getOwner();
        if (awg11 == null || this.solidRender) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)double2, (float)double3, (float)double4);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scalef(0.5f, 0.5f, 0.5f);
        this.bindTexture(ats);
        final Tesselator cuz12 = Tesselator.getInstance();
        final BufferBuilder cuw13 = cuz12.getBuilder();
        final float float7 = 1.0f;
        final float float8 = 0.5f;
        final float float9 = 0.5f;
        GlStateManager.rotatef(180.0f - this.entityRenderDispatcher.playerRotY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(((this.entityRenderDispatcher.options.thirdPersonView == 2) ? -1 : 1) * -this.entityRenderDispatcher.playerRotX, 1.0f, 0.0f, 0.0f);
        if (this.solidRender) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(ats));
        }
        cuw13.begin(7, DefaultVertexFormat.POSITION_TEX_NORMAL);
        cuw13.vertex(-0.5, -0.5, 0.0).uv(0.0, 1.0).normal(0.0f, 1.0f, 0.0f).endVertex();
        cuw13.vertex(0.5, -0.5, 0.0).uv(1.0, 1.0).normal(0.0f, 1.0f, 0.0f).endVertex();
        cuw13.vertex(0.5, 0.5, 0.0).uv(1.0, 0.0).normal(0.0f, 1.0f, 0.0f).endVertex();
        cuw13.vertex(-0.5, 0.5, 0.0).uv(0.0, 0.0).normal(0.0f, 1.0f, 0.0f).endVertex();
        cuz12.end();
        if (this.solidRender) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        int integer17 = (awg11.getMainArm() == HumanoidArm.RIGHT) ? 1 : -1;
        final ItemStack bcj18 = awg11.getMainHandItem();
        if (bcj18.getItem() != Items.FISHING_ROD) {
            integer17 = -integer17;
        }
        final float float10 = awg11.getAttackAnim(float6);
        final float float11 = Mth.sin(Mth.sqrt(float10) * 3.1415927f);
        final float float12 = Mth.lerp(float6, awg11.yBodyRotO, awg11.yBodyRot) * 0.017453292f;
        final double double5 = Mth.sin(float12);
        final double double6 = Mth.cos(float12);
        final double double7 = integer17 * 0.35;
        final double double8 = 0.8;
        double double9;
        double double10;
        double double11;
        double double12;
        if ((this.entityRenderDispatcher.options != null && this.entityRenderDispatcher.options.thirdPersonView > 0) || awg11 != Minecraft.getInstance().player) {
            double9 = Mth.lerp(float6, awg11.xo, awg11.x) - double6 * double7 - double5 * 0.8;
            double10 = awg11.yo + awg11.getEyeHeight() + (awg11.y - awg11.yo) * float6 - 0.45;
            double11 = Mth.lerp(float6, awg11.zo, awg11.z) - double5 * double7 + double6 * 0.8;
            double12 = (awg11.isVisuallySneaking() ? -0.1875 : 0.0);
        }
        else {
            double double13 = this.entityRenderDispatcher.options.fov;
            double13 /= 100.0;
            Vec3 csi40 = new Vec3(integer17 * -0.36 * double13, -0.045 * double13, 0.4);
            csi40 = csi40.xRot(-Mth.lerp(float6, awg11.xRotO, awg11.xRot) * 0.017453292f);
            csi40 = csi40.yRot(-Mth.lerp(float6, awg11.yRotO, awg11.yRot) * 0.017453292f);
            csi40 = csi40.yRot(float11 * 0.5f);
            csi40 = csi40.xRot(-float11 * 0.7f);
            double9 = Mth.lerp(float6, awg11.xo, awg11.x) + csi40.x;
            double10 = Mth.lerp(float6, awg11.yo, awg11.y) + csi40.y;
            double11 = Mth.lerp(float6, awg11.zo, awg11.z) + csi40.z;
            double12 = awg11.getEyeHeight();
        }
        double double13 = Mth.lerp(float6, ats.xo, ats.x);
        final double double14 = Mth.lerp(float6, ats.yo, ats.y) + 0.25;
        final double double15 = Mth.lerp(float6, ats.zo, ats.z);
        final double double16 = (float)(double9 - double13);
        final double double17 = (float)(double10 - double14) + double12;
        final double double18 = (float)(double11 - double15);
        GlStateManager.disableTexture();
        GlStateManager.disableLighting();
        cuw13.begin(3, DefaultVertexFormat.POSITION_COLOR);
        final int integer18 = 16;
        for (int integer19 = 0; integer19 <= 16; ++integer19) {
            final float float13 = integer19 / 16.0f;
            cuw13.vertex(double2 + double16 * float13, double3 + double17 * (float13 * float13 + float13) * 0.5 + 0.25, double4 + double18 * float13).color(0, 0, 0, 255).endVertex();
        }
        cuz12.end();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture();
        super.render(ats, double2, double3, double4, float5, float6);
    }
    
    @Override
    protected ResourceLocation getTextureLocation(final FishingHook ats) {
        return FishingHookRenderer.TEXTURE_LOCATION;
    }
    
    static {
        TEXTURE_LOCATION = new ResourceLocation("textures/entity/fishing_hook.png");
    }
}
