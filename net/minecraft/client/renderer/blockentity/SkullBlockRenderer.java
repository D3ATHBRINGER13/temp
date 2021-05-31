package net.minecraft.client.renderer.blockentity;

import java.util.function.Consumer;
import net.minecraft.Util;
import com.google.common.collect.Maps;
import net.minecraft.client.model.dragon.DragonHeadModel;
import net.minecraft.client.model.HumanoidHeadModel;
import java.util.HashMap;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.world.entity.player.Player;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.SkullModel;
import net.minecraft.world.level.block.SkullBlock;
import java.util.Map;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

public class SkullBlockRenderer extends BlockEntityRenderer<SkullBlockEntity> {
    public static SkullBlockRenderer instance;
    private static final Map<SkullBlock.Type, SkullModel> MODEL_BY_TYPE;
    private static final Map<SkullBlock.Type, ResourceLocation> SKIN_BY_TYPE;
    
    @Override
    public void render(final SkullBlockEntity but, final double double2, final double double3, final double double4, final float float5, final int integer) {
        final float float6 = but.getMouthAnimation(float5);
        final BlockState bvt12 = but.getBlockState();
        final boolean boolean13 = bvt12.getBlock() instanceof WallSkullBlock;
        final Direction fb14 = boolean13 ? bvt12.<Direction>getValue((Property<Direction>)WallSkullBlock.FACING) : null;
        final float float7 = 22.5f * (boolean13 ? ((2 + fb14.get2DDataValue()) * 4) : bvt12.<Integer>getValue((Property<Integer>)SkullBlock.ROTATION));
        this.renderSkull((float)double2, (float)double3, (float)double4, fb14, float7, ((AbstractSkullBlock)bvt12.getBlock()).getType(), but.getOwnerProfile(), integer, float6);
    }
    
    @Override
    public void init(final BlockEntityRenderDispatcher dpd) {
        super.init(dpd);
        SkullBlockRenderer.instance = this;
    }
    
    public void renderSkull(final float float1, final float float2, final float float3, @Nullable final Direction fb, final float float5, final SkullBlock.Type a, @Nullable final GameProfile gameProfile, final int integer, final float float9) {
        final SkullModel dix11 = (SkullModel)SkullBlockRenderer.MODEL_BY_TYPE.get(a);
        if (integer >= 0) {
            this.bindTexture(SkullBlockRenderer.BREAKING_LOCATIONS[integer]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(4.0f, 2.0f, 1.0f);
            GlStateManager.translatef(0.0625f, 0.0625f, 0.0625f);
            GlStateManager.matrixMode(5888);
        }
        else {
            this.bindTexture(this.getLocation(a, gameProfile));
        }
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        if (fb == null) {
            GlStateManager.translatef(float1 + 0.5f, float2, float3 + 0.5f);
        }
        else {
            switch (fb) {
                case NORTH: {
                    GlStateManager.translatef(float1 + 0.5f, float2 + 0.25f, float3 + 0.74f);
                    break;
                }
                case SOUTH: {
                    GlStateManager.translatef(float1 + 0.5f, float2 + 0.25f, float3 + 0.26f);
                    break;
                }
                case WEST: {
                    GlStateManager.translatef(float1 + 0.74f, float2 + 0.25f, float3 + 0.5f);
                    break;
                }
                default: {
                    GlStateManager.translatef(float1 + 0.26f, float2 + 0.25f, float3 + 0.5f);
                    break;
                }
            }
        }
        GlStateManager.enableRescaleNormal();
        GlStateManager.scalef(-1.0f, -1.0f, 1.0f);
        GlStateManager.enableAlphaTest();
        if (a == SkullBlock.Types.PLAYER) {
            GlStateManager.setProfile(GlStateManager.Profile.PLAYER_SKIN);
        }
        dix11.render(float9, 0.0f, 0.0f, float5, 0.0f, 0.0625f);
        GlStateManager.popMatrix();
        if (integer >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }
    
    private ResourceLocation getLocation(final SkullBlock.Type a, @Nullable final GameProfile gameProfile) {
        ResourceLocation qv4 = (ResourceLocation)SkullBlockRenderer.SKIN_BY_TYPE.get(a);
        if (a == SkullBlock.Types.PLAYER && gameProfile != null) {
            final Minecraft cyc5 = Minecraft.getInstance();
            final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map6 = cyc5.getSkinManager().getInsecureSkinInformation(gameProfile);
            if (map6.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                qv4 = cyc5.getSkinManager().registerTexture((MinecraftProfileTexture)map6.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
            }
            else {
                qv4 = DefaultPlayerSkin.getDefaultSkin(Player.createPlayerUUID(gameProfile));
            }
        }
        return qv4;
    }
    
    static {
        MODEL_BY_TYPE = Util.<Map>make((Map)Maps.newHashMap(), (java.util.function.Consumer<Map>)(hashMap -> {
            final SkullModel dix2 = new SkullModel(0, 0, 64, 32);
            final SkullModel dix3 = new HumanoidHeadModel();
            final DragonHeadModel djp4 = new DragonHeadModel(0.0f);
            hashMap.put(SkullBlock.Types.SKELETON, dix2);
            hashMap.put(SkullBlock.Types.WITHER_SKELETON, dix2);
            hashMap.put(SkullBlock.Types.PLAYER, dix3);
            hashMap.put(SkullBlock.Types.ZOMBIE, dix3);
            hashMap.put(SkullBlock.Types.CREEPER, dix2);
            hashMap.put(SkullBlock.Types.DRAGON, djp4);
        }));
        SKIN_BY_TYPE = Util.<Map>make((Map)Maps.newHashMap(), (java.util.function.Consumer<Map>)(hashMap -> {
            hashMap.put(SkullBlock.Types.SKELETON, new ResourceLocation("textures/entity/skeleton/skeleton.png"));
            hashMap.put(SkullBlock.Types.WITHER_SKELETON, new ResourceLocation("textures/entity/skeleton/wither_skeleton.png"));
            hashMap.put(SkullBlock.Types.ZOMBIE, new ResourceLocation("textures/entity/zombie/zombie.png"));
            hashMap.put(SkullBlock.Types.CREEPER, new ResourceLocation("textures/entity/creeper/creeper.png"));
            hashMap.put(SkullBlock.Types.DRAGON, new ResourceLocation("textures/entity/enderdragon/dragon.png"));
            hashMap.put(SkullBlock.Types.PLAYER, DefaultPlayerSkin.getDefaultSkin());
        }));
    }
}
