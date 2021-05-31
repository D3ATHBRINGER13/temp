package net.minecraft.client.renderer.blockentity;

import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.CrashReport;
import net.minecraft.core.BlockPos;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.Lighting;
import javax.annotation.Nullable;
import java.util.Iterator;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import com.google.common.collect.Maps;
import net.minecraft.world.phys.HitResult;
import net.minecraft.client.Camera;
import net.minecraft.world.level.Level;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.gui.Font;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.Map;

public class BlockEntityRenderDispatcher {
    private final Map<Class<? extends BlockEntity>, BlockEntityRenderer<? extends BlockEntity>> renderers;
    public static final BlockEntityRenderDispatcher instance;
    private Font font;
    public static double xOff;
    public static double yOff;
    public static double zOff;
    public TextureManager textureManager;
    public Level level;
    public Camera camera;
    public HitResult cameraHitResult;
    
    private BlockEntityRenderDispatcher() {
        (this.renderers = (Map<Class<? extends BlockEntity>, BlockEntityRenderer<? extends BlockEntity>>)Maps.newHashMap()).put(SignBlockEntity.class, new SignRenderer());
        this.renderers.put(SpawnerBlockEntity.class, new SpawnerRenderer());
        this.renderers.put(PistonMovingBlockEntity.class, new PistonHeadRenderer());
        this.renderers.put(ChestBlockEntity.class, new ChestRenderer());
        this.renderers.put(EnderChestBlockEntity.class, new ChestRenderer());
        this.renderers.put(EnchantmentTableBlockEntity.class, new EnchantTableRenderer());
        this.renderers.put(LecternBlockEntity.class, new LecternRenderer());
        this.renderers.put(TheEndPortalBlockEntity.class, new TheEndPortalRenderer());
        this.renderers.put(TheEndGatewayBlockEntity.class, new TheEndGatewayRenderer());
        this.renderers.put(BeaconBlockEntity.class, new BeaconRenderer());
        this.renderers.put(SkullBlockEntity.class, new SkullBlockRenderer());
        this.renderers.put(BannerBlockEntity.class, new BannerRenderer());
        this.renderers.put(StructureBlockEntity.class, new StructureBlockRenderer());
        this.renderers.put(ShulkerBoxBlockEntity.class, new ShulkerBoxRenderer(new ShulkerModel<>()));
        this.renderers.put(BedBlockEntity.class, new BedRenderer());
        this.renderers.put(ConduitBlockEntity.class, new ConduitRenderer());
        this.renderers.put(BellBlockEntity.class, new BellRenderer());
        this.renderers.put(CampfireBlockEntity.class, new CampfireRenderer());
        for (final BlockEntityRenderer<?> dpe3 : this.renderers.values()) {
            dpe3.init(this);
        }
    }
    
    public <T extends BlockEntity> BlockEntityRenderer<T> getRenderer(final Class<? extends BlockEntity> class1) {
        BlockEntityRenderer<? extends BlockEntity> dpe3 = this.renderers.get(class1);
        if (dpe3 == null && class1 != BlockEntity.class) {
            dpe3 = this.getRenderer(class1.getSuperclass());
            this.renderers.put(class1, dpe3);
        }
        return (BlockEntityRenderer<T>)dpe3;
    }
    
    @Nullable
    public <T extends BlockEntity> BlockEntityRenderer<T> getRenderer(@Nullable final BlockEntity btw) {
        if (btw == null) {
            return null;
        }
        return this.<T>getRenderer(btw.getClass());
    }
    
    public void prepare(final Level bhr, final TextureManager dxc, final Font cyu, final Camera cxq, final HitResult csf) {
        if (this.level != bhr) {
            this.setLevel(bhr);
        }
        this.textureManager = dxc;
        this.camera = cxq;
        this.font = cyu;
        this.cameraHitResult = csf;
    }
    
    public void render(final BlockEntity btw, final float float2, final int integer) {
        if (btw.distanceToSqr(this.camera.getPosition().x, this.camera.getPosition().y, this.camera.getPosition().z) < btw.getViewDistance()) {
            Lighting.turnOn();
            final int integer2 = this.level.getLightColor(btw.getBlockPos(), 0);
            final int integer3 = integer2 % 65536;
            final int integer4 = integer2 / 65536;
            GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)integer3, (float)integer4);
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            final BlockPos ew8 = btw.getBlockPos();
            this.render(btw, ew8.getX() - BlockEntityRenderDispatcher.xOff, ew8.getY() - BlockEntityRenderDispatcher.yOff, ew8.getZ() - BlockEntityRenderDispatcher.zOff, float2, integer, false);
        }
    }
    
    public void render(final BlockEntity btw, final double double2, final double double3, final double double4, final float float5) {
        this.render(btw, double2, double3, double4, float5, -1, false);
    }
    
    public void renderItem(final BlockEntity btw) {
        this.render(btw, 0.0, 0.0, 0.0, 0.0f, -1, true);
    }
    
    public void render(final BlockEntity btw, final double double2, final double double3, final double double4, final float float5, final int integer, final boolean boolean7) {
        final BlockEntityRenderer<BlockEntity> dpe12 = this.<BlockEntity>getRenderer(btw);
        if (dpe12 != null) {
            try {
                if (boolean7 || (btw.hasLevel() && btw.getType().isValid(btw.getBlockState().getBlock()))) {
                    dpe12.render(btw, double2, double3, double4, float5, integer);
                }
            }
            catch (Throwable throwable13) {
                final CrashReport d14 = CrashReport.forThrowable(throwable13, "Rendering Block Entity");
                final CrashReportCategory e15 = d14.addCategory("Block Entity Details");
                btw.fillCrashReportCategory(e15);
                throw new ReportedException(d14);
            }
        }
    }
    
    public void setLevel(@Nullable final Level bhr) {
        this.level = bhr;
        if (bhr == null) {
            this.camera = null;
        }
    }
    
    public Font getFont() {
        return this.font;
    }
    
    static {
        instance = new BlockEntityRenderDispatcher();
    }
}
