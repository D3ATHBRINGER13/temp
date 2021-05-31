package net.minecraft.client.renderer.blockentity;

import net.minecraft.world.level.block.entity.BlockEntity;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;

public class BeaconRenderer extends BlockEntityRenderer<BeaconBlockEntity> {
    private static final ResourceLocation BEAM_LOCATION;
    
    @Override
    public void render(final BeaconBlockEntity bts, final double double2, final double double3, final double double4, final float float5, final int integer) {
        this.renderBeaconBeam(double2, double3, double4, float5, bts.getBeamSections(), bts.getLevel().getGameTime());
    }
    
    private void renderBeaconBeam(final double double1, final double double2, final double double3, final double double4, final List<BeaconBlockEntity.BeaconBeamSection> list, final long long6) {
        GlStateManager.alphaFunc(516, 0.1f);
        this.bindTexture(BeaconRenderer.BEAM_LOCATION);
        GlStateManager.disableFog();
        int integer13 = 0;
        for (int integer14 = 0; integer14 < list.size(); ++integer14) {
            final BeaconBlockEntity.BeaconBeamSection a15 = (BeaconBlockEntity.BeaconBeamSection)list.get(integer14);
            renderBeaconBeam(double1, double2, double3, double4, long6, integer13, (integer14 == list.size() - 1) ? 1024 : a15.getHeight(), a15.getColor());
            integer13 += a15.getHeight();
        }
        GlStateManager.enableFog();
    }
    
    private static void renderBeaconBeam(final double double1, final double double2, final double double3, final double double4, final long long5, final int integer6, final int integer7, final float[] arr) {
        renderBeaconBeam(double1, double2, double3, double4, 1.0, long5, integer6, integer7, arr, 0.2, 0.25);
    }
    
    public static void renderBeaconBeam(final double double1, final double double2, final double double3, final double double4, final double double5, final long long6, final int integer7, final int integer8, final float[] arr, final double double10, final double double11) {
        final int integer9 = integer7 + integer8;
        GlStateManager.texParameter(3553, 10242, 10497);
        GlStateManager.texParameter(3553, 10243, 10497);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        GlStateManager.translated(double1 + 0.5, double2, double3 + 0.5);
        final Tesselator cuz21 = Tesselator.getInstance();
        final BufferBuilder cuw22 = cuz21.getBuilder();
        final double double12 = Math.floorMod(long6, 40L) + double4;
        final double double13 = (integer8 < 0) ? double12 : (-double12);
        final double double14 = Mth.frac(double13 * 0.2 - Mth.floor(double13 * 0.1));
        final float float29 = arr[0];
        final float float30 = arr[1];
        final float float31 = arr[2];
        GlStateManager.pushMatrix();
        GlStateManager.rotated(double12 * 2.25 - 45.0, 0.0, 1.0, 0.0);
        double double15 = 0.0;
        double double16 = double10;
        double double17 = double10;
        double double18 = 0.0;
        double double19 = -double10;
        double double20 = 0.0;
        double double21 = 0.0;
        double double22 = -double10;
        double double23 = 0.0;
        double double24 = 1.0;
        double double25 = -1.0 + double14;
        double double26 = integer8 * double5 * (0.5 / double10) + double25;
        cuw22.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
        cuw22.vertex(0.0, integer9, double16).uv(1.0, double26).color(float29, float30, float31, 1.0f).endVertex();
        cuw22.vertex(0.0, integer7, double16).uv(1.0, double25).color(float29, float30, float31, 1.0f).endVertex();
        cuw22.vertex(double17, integer7, 0.0).uv(0.0, double25).color(float29, float30, float31, 1.0f).endVertex();
        cuw22.vertex(double17, integer9, 0.0).uv(0.0, double26).color(float29, float30, float31, 1.0f).endVertex();
        cuw22.vertex(0.0, integer9, double22).uv(1.0, double26).color(float29, float30, float31, 1.0f).endVertex();
        cuw22.vertex(0.0, integer7, double22).uv(1.0, double25).color(float29, float30, float31, 1.0f).endVertex();
        cuw22.vertex(double19, integer7, 0.0).uv(0.0, double25).color(float29, float30, float31, 1.0f).endVertex();
        cuw22.vertex(double19, integer9, 0.0).uv(0.0, double26).color(float29, float30, float31, 1.0f).endVertex();
        cuw22.vertex(double17, integer9, 0.0).uv(1.0, double26).color(float29, float30, float31, 1.0f).endVertex();
        cuw22.vertex(double17, integer7, 0.0).uv(1.0, double25).color(float29, float30, float31, 1.0f).endVertex();
        cuw22.vertex(0.0, integer7, double22).uv(0.0, double25).color(float29, float30, float31, 1.0f).endVertex();
        cuw22.vertex(0.0, integer9, double22).uv(0.0, double26).color(float29, float30, float31, 1.0f).endVertex();
        cuw22.vertex(double19, integer9, 0.0).uv(1.0, double26).color(float29, float30, float31, 1.0f).endVertex();
        cuw22.vertex(double19, integer7, 0.0).uv(1.0, double25).color(float29, float30, float31, 1.0f).endVertex();
        cuw22.vertex(0.0, integer7, double16).uv(0.0, double25).color(float29, float30, float31, 1.0f).endVertex();
        cuw22.vertex(0.0, integer9, double16).uv(0.0, double26).color(float29, float30, float31, 1.0f).endVertex();
        cuz21.end();
        GlStateManager.popMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.depthMask(false);
        double15 = -double11;
        double16 = -double11;
        double17 = double11;
        double18 = -double11;
        double19 = -double11;
        double20 = double11;
        double21 = double11;
        double22 = double11;
        double23 = 0.0;
        double24 = 1.0;
        double25 = -1.0 + double14;
        double26 = integer8 * double5 + double25;
        cuw22.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
        cuw22.vertex(double15, integer9, double16).uv(1.0, double26).color(float29, float30, float31, 0.125f).endVertex();
        cuw22.vertex(double15, integer7, double16).uv(1.0, double25).color(float29, float30, float31, 0.125f).endVertex();
        cuw22.vertex(double17, integer7, double18).uv(0.0, double25).color(float29, float30, float31, 0.125f).endVertex();
        cuw22.vertex(double17, integer9, double18).uv(0.0, double26).color(float29, float30, float31, 0.125f).endVertex();
        cuw22.vertex(double21, integer9, double22).uv(1.0, double26).color(float29, float30, float31, 0.125f).endVertex();
        cuw22.vertex(double21, integer7, double22).uv(1.0, double25).color(float29, float30, float31, 0.125f).endVertex();
        cuw22.vertex(double19, integer7, double20).uv(0.0, double25).color(float29, float30, float31, 0.125f).endVertex();
        cuw22.vertex(double19, integer9, double20).uv(0.0, double26).color(float29, float30, float31, 0.125f).endVertex();
        cuw22.vertex(double17, integer9, double18).uv(1.0, double26).color(float29, float30, float31, 0.125f).endVertex();
        cuw22.vertex(double17, integer7, double18).uv(1.0, double25).color(float29, float30, float31, 0.125f).endVertex();
        cuw22.vertex(double21, integer7, double22).uv(0.0, double25).color(float29, float30, float31, 0.125f).endVertex();
        cuw22.vertex(double21, integer9, double22).uv(0.0, double26).color(float29, float30, float31, 0.125f).endVertex();
        cuw22.vertex(double19, integer9, double20).uv(1.0, double26).color(float29, float30, float31, 0.125f).endVertex();
        cuw22.vertex(double19, integer7, double20).uv(1.0, double25).color(float29, float30, float31, 0.125f).endVertex();
        cuw22.vertex(double15, integer7, double16).uv(0.0, double25).color(float29, float30, float31, 0.125f).endVertex();
        cuw22.vertex(double15, integer9, double16).uv(0.0, double26).color(float29, float30, float31, 0.125f).endVertex();
        cuz21.end();
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture();
        GlStateManager.depthMask(true);
    }
    
    @Override
    public boolean shouldRenderOffScreen(final BeaconBlockEntity bts) {
        return true;
    }
    
    static {
        BEAM_LOCATION = new ResourceLocation("textures/entity/beacon_beam.png");
    }
}
