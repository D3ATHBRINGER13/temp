package net.minecraft.client.renderer.blockentity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.Model;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;

public class ConduitRenderer extends BlockEntityRenderer<ConduitBlockEntity> {
    private static final ResourceLocation SHELL_TEXTURE;
    private static final ResourceLocation ACTIVE_SHELL_TEXTURE;
    private static final ResourceLocation WIND_TEXTURE;
    private static final ResourceLocation VERTICAL_WIND_TEXTURE;
    private static final ResourceLocation OPEN_EYE_TEXTURE;
    private static final ResourceLocation CLOSED_EYE_TEXTURE;
    private final ShellModel shellModel;
    private final CageModel cageModel;
    private final WindModel windModel;
    private final EyeModel eyeModel;
    
    public ConduitRenderer() {
        this.shellModel = new ShellModel();
        this.cageModel = new CageModel();
        this.windModel = new WindModel();
        this.eyeModel = new EyeModel();
    }
    
    @Override
    public void render(final ConduitBlockEntity bud, final double double2, final double double3, final double double4, final float float5, final int integer) {
        final float float6 = bud.tickCount + float5;
        if (!bud.isActive()) {
            final float float7 = bud.getActiveRotation(0.0f);
            this.bindTexture(ConduitRenderer.SHELL_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)double2 + 0.5f, (float)double3 + 0.5f, (float)double4 + 0.5f);
            GlStateManager.rotatef(float7, 0.0f, 1.0f, 0.0f);
            this.shellModel.render(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
            GlStateManager.popMatrix();
        }
        else if (bud.isActive()) {
            final float float7 = bud.getActiveRotation(float5) * 57.295776f;
            float float8 = Mth.sin(float6 * 0.1f) / 2.0f + 0.5f;
            float8 += float8 * float8;
            this.bindTexture(ConduitRenderer.ACTIVE_SHELL_TEXTURE);
            GlStateManager.disableCull();
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)double2 + 0.5f, (float)double3 + 0.3f + float8 * 0.2f, (float)double4 + 0.5f);
            GlStateManager.rotatef(float7, 0.5f, 1.0f, 0.5f);
            this.cageModel.render(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
            GlStateManager.popMatrix();
            final int integer2 = 3;
            final int integer3 = bud.tickCount / 3 % 22;
            this.windModel.setActiveAnim(integer3);
            final int integer4 = bud.tickCount / 66 % 3;
            switch (integer4) {
                case 0: {
                    this.bindTexture(ConduitRenderer.WIND_TEXTURE);
                    GlStateManager.pushMatrix();
                    GlStateManager.translatef((float)double2 + 0.5f, (float)double3 + 0.5f, (float)double4 + 0.5f);
                    this.windModel.render(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                    GlStateManager.popMatrix();
                    GlStateManager.pushMatrix();
                    GlStateManager.translatef((float)double2 + 0.5f, (float)double3 + 0.5f, (float)double4 + 0.5f);
                    GlStateManager.scalef(0.875f, 0.875f, 0.875f);
                    GlStateManager.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
                    GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
                    this.windModel.render(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                    GlStateManager.popMatrix();
                    break;
                }
                case 1: {
                    this.bindTexture(ConduitRenderer.VERTICAL_WIND_TEXTURE);
                    GlStateManager.pushMatrix();
                    GlStateManager.translatef((float)double2 + 0.5f, (float)double3 + 0.5f, (float)double4 + 0.5f);
                    GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                    this.windModel.render(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                    GlStateManager.popMatrix();
                    GlStateManager.pushMatrix();
                    GlStateManager.translatef((float)double2 + 0.5f, (float)double3 + 0.5f, (float)double4 + 0.5f);
                    GlStateManager.scalef(0.875f, 0.875f, 0.875f);
                    GlStateManager.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
                    GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
                    this.windModel.render(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                    GlStateManager.popMatrix();
                    break;
                }
                case 2: {
                    this.bindTexture(ConduitRenderer.WIND_TEXTURE);
                    GlStateManager.pushMatrix();
                    GlStateManager.translatef((float)double2 + 0.5f, (float)double3 + 0.5f, (float)double4 + 0.5f);
                    GlStateManager.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
                    this.windModel.render(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                    GlStateManager.popMatrix();
                    GlStateManager.pushMatrix();
                    GlStateManager.translatef((float)double2 + 0.5f, (float)double3 + 0.5f, (float)double4 + 0.5f);
                    GlStateManager.scalef(0.875f, 0.875f, 0.875f);
                    GlStateManager.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
                    GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
                    this.windModel.render(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                    GlStateManager.popMatrix();
                    break;
                }
            }
            final Camera cxq17 = this.blockEntityRenderDispatcher.camera;
            if (bud.isHunting()) {
                this.bindTexture(ConduitRenderer.OPEN_EYE_TEXTURE);
            }
            else {
                this.bindTexture(ConduitRenderer.CLOSED_EYE_TEXTURE);
            }
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)double2 + 0.5f, (float)double3 + 0.3f + float8 * 0.2f, (float)double4 + 0.5f);
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.rotatef(-cxq17.getYRot(), 0.0f, 1.0f, 0.0f);
            GlStateManager.rotatef(cxq17.getXRot(), 1.0f, 0.0f, 0.0f);
            GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
            this.eyeModel.render(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.083333336f);
            GlStateManager.popMatrix();
        }
        super.render(bud, double2, double3, double4, float5, integer);
    }
    
    static {
        SHELL_TEXTURE = new ResourceLocation("textures/entity/conduit/base.png");
        ACTIVE_SHELL_TEXTURE = new ResourceLocation("textures/entity/conduit/cage.png");
        WIND_TEXTURE = new ResourceLocation("textures/entity/conduit/wind.png");
        VERTICAL_WIND_TEXTURE = new ResourceLocation("textures/entity/conduit/wind_vertical.png");
        OPEN_EYE_TEXTURE = new ResourceLocation("textures/entity/conduit/open_eye.png");
        CLOSED_EYE_TEXTURE = new ResourceLocation("textures/entity/conduit/closed_eye.png");
    }
    
    static class ShellModel extends Model {
        private final ModelPart box;
        
        public ShellModel() {
            this.texWidth = 32;
            this.texHeight = 16;
            (this.box = new ModelPart(this, 0, 0)).addBox(-3.0f, -3.0f, -3.0f, 6, 6, 6);
        }
        
        public void render(final float float1, final float float2, final float float3, final float float4, final float float5, final float float6) {
            this.box.render(float6);
        }
    }
    
    static class CageModel extends Model {
        private final ModelPart box;
        
        public CageModel() {
            this.texWidth = 32;
            this.texHeight = 16;
            (this.box = new ModelPart(this, 0, 0)).addBox(-4.0f, -4.0f, -4.0f, 8, 8, 8);
        }
        
        public void render(final float float1, final float float2, final float float3, final float float4, final float float5, final float float6) {
            this.box.render(float6);
        }
    }
    
    static class WindModel extends Model {
        private final ModelPart[] box;
        private int activeAnim;
        
        public WindModel() {
            this.box = new ModelPart[22];
            this.texWidth = 64;
            this.texHeight = 1024;
            for (int integer2 = 0; integer2 < 22; ++integer2) {
                (this.box[integer2] = new ModelPart(this, 0, 32 * integer2)).addBox(-8.0f, -8.0f, -8.0f, 16, 16, 16);
            }
        }
        
        public void render(final float float1, final float float2, final float float3, final float float4, final float float5, final float float6) {
            this.box[this.activeAnim].render(float6);
        }
        
        public void setActiveAnim(final int integer) {
            this.activeAnim = integer;
        }
    }
    
    static class EyeModel extends Model {
        private final ModelPart eye;
        
        public EyeModel() {
            this.texWidth = 8;
            this.texHeight = 8;
            (this.eye = new ModelPart(this, 0, 0)).addBox(-4.0f, -4.0f, 0.0f, 8, 8, 0, 0.01f);
        }
        
        public void render(final float float1, final float float2, final float float3, final float float4, final float float5, final float float6) {
            this.eye.render(float6);
        }
    }
}
