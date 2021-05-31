package net.minecraft.client.renderer.blockentity;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Calendar;
import net.minecraft.client.model.LargeChestModel;
import net.minecraft.client.model.ChestModel;
import net.minecraft.resources.ResourceLocation;

public class ChestRenderer<T extends BlockEntity> extends BlockEntityRenderer<T> {
    private static final ResourceLocation CHEST_LARGE_TRAP_LOCATION;
    private static final ResourceLocation CHEST_LARGE_XMAS_LOCATION;
    private static final ResourceLocation CHEST_LARGE_LOCATION;
    private static final ResourceLocation CHEST_TRAP_LOCATION;
    private static final ResourceLocation CHEST_XMAS_LOCATION;
    private static final ResourceLocation CHEST_LOCATION;
    private static final ResourceLocation ENDER_CHEST_LOCATION;
    private final ChestModel chestModel;
    private final ChestModel largeChestModel;
    private boolean xmasTextures;
    
    public ChestRenderer() {
        this.chestModel = new ChestModel();
        this.largeChestModel = new LargeChestModel();
        final Calendar calendar2 = Calendar.getInstance();
        if (calendar2.get(2) + 1 == 12 && calendar2.get(5) >= 24 && calendar2.get(5) <= 26) {
            this.xmasTextures = true;
        }
    }
    
    @Override
    public void render(final T btw, final double double2, final double double3, final double double4, final float float5, final int integer) {
        GlStateManager.enableDepthTest();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        final BlockState bvt11 = ((BlockEntity)btw).hasLevel() ? ((BlockEntity)btw).getBlockState() : ((AbstractStateHolder<O, BlockState>)Blocks.CHEST.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)ChestBlock.FACING, Direction.SOUTH);
        final ChestType bwm12 = bvt11.<ChestType>hasProperty(ChestBlock.TYPE) ? bvt11.<ChestType>getValue(ChestBlock.TYPE) : ChestType.SINGLE;
        if (bwm12 == ChestType.LEFT) {
            return;
        }
        final boolean boolean13 = bwm12 != ChestType.SINGLE;
        final ChestModel dgx14 = this.getChestModelAndBindTexture(btw, integer, boolean13);
        if (integer >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(boolean13 ? 8.0f : 4.0f, 4.0f, 1.0f);
            GlStateManager.translatef(0.0625f, 0.0625f, 0.0625f);
            GlStateManager.matrixMode(5888);
        }
        else {
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.translatef((float)double2, (float)double3 + 1.0f, (float)double4 + 1.0f);
        GlStateManager.scalef(1.0f, -1.0f, -1.0f);
        final float float6 = bvt11.<Direction>getValue((Property<Direction>)ChestBlock.FACING).toYRot();
        if (Math.abs(float6) > 1.0E-5) {
            GlStateManager.translatef(0.5f, 0.5f, 0.5f);
            GlStateManager.rotatef(float6, 0.0f, 1.0f, 0.0f);
            GlStateManager.translatef(-0.5f, -0.5f, -0.5f);
        }
        this.rotateLid(btw, float5, dgx14);
        dgx14.render();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (integer >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }
    
    private ChestModel getChestModelAndBindTexture(final T btw, final int integer, final boolean boolean3) {
        ResourceLocation qv5;
        if (integer >= 0) {
            qv5 = ChestRenderer.BREAKING_LOCATIONS[integer];
        }
        else if (this.xmasTextures) {
            qv5 = (boolean3 ? ChestRenderer.CHEST_LARGE_XMAS_LOCATION : ChestRenderer.CHEST_XMAS_LOCATION);
        }
        else if (btw instanceof TrappedChestBlockEntity) {
            qv5 = (boolean3 ? ChestRenderer.CHEST_LARGE_TRAP_LOCATION : ChestRenderer.CHEST_TRAP_LOCATION);
        }
        else if (btw instanceof EnderChestBlockEntity) {
            qv5 = ChestRenderer.ENDER_CHEST_LOCATION;
        }
        else {
            qv5 = (boolean3 ? ChestRenderer.CHEST_LARGE_LOCATION : ChestRenderer.CHEST_LOCATION);
        }
        this.bindTexture(qv5);
        return boolean3 ? this.largeChestModel : this.chestModel;
    }
    
    private void rotateLid(final T btw, final float float2, final ChestModel dgx) {
        float float3 = ((LidBlockEntity)btw).getOpenNess(float2);
        float3 = 1.0f - float3;
        float3 = 1.0f - float3 * float3 * float3;
        dgx.getLid().xRot = -(float3 * 1.5707964f);
    }
    
    static {
        CHEST_LARGE_TRAP_LOCATION = new ResourceLocation("textures/entity/chest/trapped_double.png");
        CHEST_LARGE_XMAS_LOCATION = new ResourceLocation("textures/entity/chest/christmas_double.png");
        CHEST_LARGE_LOCATION = new ResourceLocation("textures/entity/chest/normal_double.png");
        CHEST_TRAP_LOCATION = new ResourceLocation("textures/entity/chest/trapped.png");
        CHEST_XMAS_LOCATION = new ResourceLocation("textures/entity/chest/christmas.png");
        CHEST_LOCATION = new ResourceLocation("textures/entity/chest/normal.png");
        ENDER_CHEST_LOCATION = new ResourceLocation("textures/entity/chest/ender.png");
    }
}
