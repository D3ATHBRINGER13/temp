package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

public class MinecartRenderer<T extends AbstractMinecart> extends EntityRenderer<T> {
    private static final ResourceLocation MINECART_LOCATION;
    protected final EntityModel<T> model;
    
    public MinecartRenderer(final EntityRenderDispatcher dsa) {
        super(dsa);
        this.model = new MinecartModel<T>();
        this.shadowRadius = 0.7f;
    }
    
    @Override
    public void render(final T axu, double double2, double double3, double double4, float float5, final float float6) {
        GlStateManager.pushMatrix();
        this.bindTexture(axu);
        long long11 = axu.getId() * 493286711L;
        long11 = long11 * long11 * 4392167121L + long11 * 98761L;
        final float float7 = (((long11 >> 16 & 0x7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        final float float8 = (((long11 >> 20 & 0x7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        final float float9 = (((long11 >> 24 & 0x7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        GlStateManager.translatef(float7, float8, float9);
        final double double5 = Mth.lerp(float6, axu.xOld, axu.x);
        final double double6 = Mth.lerp(float6, axu.yOld, axu.y);
        final double double7 = Mth.lerp(float6, axu.zOld, axu.z);
        final double double8 = 0.30000001192092896;
        final Vec3 csi24 = axu.getPos(double5, double6, double7);
        float float10 = Mth.lerp(float6, axu.xRotO, axu.xRot);
        if (csi24 != null) {
            Vec3 csi25 = axu.getPosOffs(double5, double6, double7, 0.30000001192092896);
            Vec3 csi26 = axu.getPosOffs(double5, double6, double7, -0.30000001192092896);
            if (csi25 == null) {
                csi25 = csi24;
            }
            if (csi26 == null) {
                csi26 = csi24;
            }
            double2 += csi24.x - double5;
            double3 += (csi25.y + csi26.y) / 2.0 - double6;
            double4 += csi24.z - double7;
            Vec3 csi27 = csi26.add(-csi25.x, -csi25.y, -csi25.z);
            if (csi27.length() != 0.0) {
                csi27 = csi27.normalize();
                float5 = (float)(Math.atan2(csi27.z, csi27.x) * 180.0 / 3.141592653589793);
                float10 = (float)(Math.atan(csi27.y) * 73.0);
            }
        }
        GlStateManager.translatef((float)double2, (float)double3 + 0.375f, (float)double4);
        GlStateManager.rotatef(180.0f - float5, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(-float10, 0.0f, 0.0f, 1.0f);
        final float float11 = axu.getHurtTime() - float6;
        float float12 = axu.getDamage() - float6;
        if (float12 < 0.0f) {
            float12 = 0.0f;
        }
        if (float11 > 0.0f) {
            GlStateManager.rotatef(Mth.sin(float11) * float11 * float12 / 10.0f * axu.getHurtDir(), 1.0f, 0.0f, 0.0f);
        }
        final int integer28 = axu.getDisplayOffset();
        if (this.solidRender) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(axu));
        }
        final BlockState bvt29 = axu.getDisplayBlockState();
        if (bvt29.getRenderShape() != RenderShape.INVISIBLE) {
            GlStateManager.pushMatrix();
            this.bindTexture(TextureAtlas.LOCATION_BLOCKS);
            final float float13 = 0.75f;
            GlStateManager.scalef(0.75f, 0.75f, 0.75f);
            GlStateManager.translatef(-0.5f, (integer28 - 8) / 16.0f, 0.5f);
            this.renderMinecartContents(axu, float6, bvt29);
            GlStateManager.popMatrix();
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.bindTexture(axu);
        }
        GlStateManager.scalef(-1.0f, -1.0f, 1.0f);
        this.model.render(axu, 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
        GlStateManager.popMatrix();
        if (this.solidRender) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        super.render(axu, double2, double3, double4, float5, float6);
    }
    
    @Override
    protected ResourceLocation getTextureLocation(final T axu) {
        return MinecartRenderer.MINECART_LOCATION;
    }
    
    protected void renderMinecartContents(final T axu, final float float2, final BlockState bvt) {
        GlStateManager.pushMatrix();
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(bvt, axu.getBrightness());
        GlStateManager.popMatrix();
    }
    
    static {
        MINECART_LOCATION = new ResourceLocation("textures/entity/minecart.png");
    }
}
