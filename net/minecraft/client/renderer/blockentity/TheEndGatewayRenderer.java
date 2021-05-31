package net.minecraft.client.renderer.blockentity;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.resources.ResourceLocation;

public class TheEndGatewayRenderer extends TheEndPortalRenderer {
    private static final ResourceLocation BEAM_LOCATION;
    
    @Override
    public void render(final TheEndPortalBlockEntity buy, final double double2, final double double3, final double double4, final float float5, final int integer) {
        GlStateManager.disableFog();
        final TheEndGatewayBlockEntity bux11 = (TheEndGatewayBlockEntity)buy;
        if (bux11.isSpawning() || bux11.isCoolingDown()) {
            GlStateManager.alphaFunc(516, 0.1f);
            this.bindTexture(TheEndGatewayRenderer.BEAM_LOCATION);
            float float6 = bux11.isSpawning() ? bux11.getSpawnPercent(float5) : bux11.getCooldownPercent(float5);
            final double double5 = bux11.isSpawning() ? (256.0 - double3) : 50.0;
            float6 = Mth.sin(float6 * 3.1415927f);
            final int integer2 = Mth.floor(float6 * double5);
            final float[] arr16 = bux11.isSpawning() ? DyeColor.MAGENTA.getTextureDiffuseColors() : DyeColor.PURPLE.getTextureDiffuseColors();
            BeaconRenderer.renderBeaconBeam(double2, double3, double4, float5, float6, bux11.getLevel().getGameTime(), 0, integer2, arr16, 0.15, 0.175);
            BeaconRenderer.renderBeaconBeam(double2, double3, double4, float5, float6, bux11.getLevel().getGameTime(), 0, -integer2, arr16, 0.15, 0.175);
        }
        super.render(buy, double2, double3, double4, float5, integer);
        GlStateManager.enableFog();
    }
    
    @Override
    protected int getPasses(final double double1) {
        return super.getPasses(double1) + 1;
    }
    
    @Override
    protected float getOffset() {
        return 1.0f;
    }
    
    static {
        BEAM_LOCATION = new ResourceLocation("textures/entity/end_gateway_beam.png");
    }
}
