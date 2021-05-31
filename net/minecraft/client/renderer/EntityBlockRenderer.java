package net.minecraft.client.renderer;

import java.util.Comparator;
import java.util.Arrays;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import java.util.UUID;
import com.mojang.authlib.GameProfile;
import org.apache.commons.lang3.StringUtils;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.AbstractSkullBlock;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.banner.BannerTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;

public class EntityBlockRenderer {
    private static final ShulkerBoxBlockEntity[] SHULKER_BOXES;
    private static final ShulkerBoxBlockEntity DEFAULT_SHULKER_BOX;
    public static final EntityBlockRenderer instance;
    private final ChestBlockEntity chest;
    private final ChestBlockEntity trappedChest;
    private final EnderChestBlockEntity enderChest;
    private final BannerBlockEntity banner;
    private final BedBlockEntity bed;
    private final SkullBlockEntity skull;
    private final ConduitBlockEntity conduit;
    private final ShieldModel shieldModel;
    private final TridentModel tridentModel;
    
    public EntityBlockRenderer() {
        this.chest = new ChestBlockEntity();
        this.trappedChest = new TrappedChestBlockEntity();
        this.enderChest = new EnderChestBlockEntity();
        this.banner = new BannerBlockEntity();
        this.bed = new BedBlockEntity();
        this.skull = new SkullBlockEntity();
        this.conduit = new ConduitBlockEntity();
        this.shieldModel = new ShieldModel();
        this.tridentModel = new TridentModel();
    }
    
    public void renderByItem(final ItemStack bcj) {
        final Item bce3 = bcj.getItem();
        if (bce3 instanceof BannerItem) {
            this.banner.fromItem(bcj, ((BannerItem)bce3).getColor());
            BlockEntityRenderDispatcher.instance.renderItem(this.banner);
        }
        else if (bce3 instanceof BlockItem && ((BlockItem)bce3).getBlock() instanceof BedBlock) {
            this.bed.setColor(((BedBlock)((BlockItem)bce3).getBlock()).getColor());
            BlockEntityRenderDispatcher.instance.renderItem(this.bed);
        }
        else if (bce3 == Items.SHIELD) {
            if (bcj.getTagElement("BlockEntityTag") != null) {
                this.banner.fromItem(bcj, ShieldItem.getColor(bcj));
                Minecraft.getInstance().getTextureManager().bind(BannerTextures.SHIELD_CACHE.getTextureLocation(this.banner.getTextureHashName(), this.banner.getPatterns(), this.banner.getColors()));
            }
            else {
                Minecraft.getInstance().getTextureManager().bind(BannerTextures.NO_PATTERN_SHIELD);
            }
            GlStateManager.pushMatrix();
            GlStateManager.scalef(1.0f, -1.0f, -1.0f);
            this.shieldModel.render();
            if (bcj.hasFoil()) {
                this.renderFoil(this.shieldModel::render);
            }
            GlStateManager.popMatrix();
        }
        else if (bce3 instanceof BlockItem && ((BlockItem)bce3).getBlock() instanceof AbstractSkullBlock) {
            GameProfile gameProfile4 = null;
            if (bcj.hasTag()) {
                final CompoundTag id5 = bcj.getTag();
                if (id5.contains("SkullOwner", 10)) {
                    gameProfile4 = NbtUtils.readGameProfile(id5.getCompound("SkullOwner"));
                }
                else if (id5.contains("SkullOwner", 8) && !StringUtils.isBlank((CharSequence)id5.getString("SkullOwner"))) {
                    gameProfile4 = new GameProfile((UUID)null, id5.getString("SkullOwner"));
                    gameProfile4 = SkullBlockEntity.updateGameprofile(gameProfile4);
                    id5.remove("SkullOwner");
                    id5.put("SkullOwner", (Tag)NbtUtils.writeGameProfile(new CompoundTag(), gameProfile4));
                }
            }
            if (SkullBlockRenderer.instance != null) {
                GlStateManager.pushMatrix();
                GlStateManager.disableCull();
                SkullBlockRenderer.instance.renderSkull(0.0f, 0.0f, 0.0f, null, 180.0f, ((AbstractSkullBlock)((BlockItem)bce3).getBlock()).getType(), gameProfile4, -1, 0.0f);
                GlStateManager.enableCull();
                GlStateManager.popMatrix();
            }
        }
        else if (bce3 == Items.TRIDENT) {
            Minecraft.getInstance().getTextureManager().bind(TridentModel.TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(1.0f, -1.0f, -1.0f);
            this.tridentModel.render();
            if (bcj.hasFoil()) {
                this.renderFoil(this.tridentModel::render);
            }
            GlStateManager.popMatrix();
        }
        else if (bce3 instanceof BlockItem && ((BlockItem)bce3).getBlock() == Blocks.CONDUIT) {
            BlockEntityRenderDispatcher.instance.renderItem(this.conduit);
        }
        else if (bce3 == Blocks.ENDER_CHEST.asItem()) {
            BlockEntityRenderDispatcher.instance.renderItem(this.enderChest);
        }
        else if (bce3 == Blocks.TRAPPED_CHEST.asItem()) {
            BlockEntityRenderDispatcher.instance.renderItem(this.trappedChest);
        }
        else if (Block.byItem(bce3) instanceof ShulkerBoxBlock) {
            final DyeColor bbg4 = ShulkerBoxBlock.getColorFromItem(bce3);
            if (bbg4 == null) {
                BlockEntityRenderDispatcher.instance.renderItem(EntityBlockRenderer.DEFAULT_SHULKER_BOX);
            }
            else {
                BlockEntityRenderDispatcher.instance.renderItem(EntityBlockRenderer.SHULKER_BOXES[bbg4.getId()]);
            }
        }
        else {
            BlockEntityRenderDispatcher.instance.renderItem(this.chest);
        }
    }
    
    private void renderFoil(final Runnable runnable) {
        GlStateManager.color3f(0.5019608f, 0.2509804f, 0.8f);
        Minecraft.getInstance().getTextureManager().bind(ItemRenderer.ENCHANT_GLINT_LOCATION);
        ItemRenderer.renderFoilLayer(Minecraft.getInstance().getTextureManager(), runnable, 1);
    }
    
    static {
        SHULKER_BOXES = (ShulkerBoxBlockEntity[])Arrays.stream((Object[])DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(ShulkerBoxBlockEntity::new).toArray(ShulkerBoxBlockEntity[]::new);
        DEFAULT_SHULKER_BOX = new ShulkerBoxBlockEntity((DyeColor)null);
        instance = new EntityBlockRenderer();
    }
}
