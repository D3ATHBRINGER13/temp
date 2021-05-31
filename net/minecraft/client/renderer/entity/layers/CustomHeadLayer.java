package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import java.util.UUID;
import com.mojang.authlib.GameProfile;
import org.apache.commons.lang3.StringUtils;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.client.renderer.entity.HeadedModel;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;

public class CustomHeadLayer<T extends LivingEntity, M extends EntityModel> extends RenderLayer<T, M> {
    public CustomHeadLayer(final RenderLayerParent<T, M> dtr) {
        super(dtr);
    }
    
    @Override
    public void render(final T aix, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        final ItemStack bcj10 = aix.getItemBySlot(EquipmentSlot.HEAD);
        if (bcj10.isEmpty()) {
            return;
        }
        final Item bce11 = bcj10.getItem();
        GlStateManager.pushMatrix();
        if (aix.isVisuallySneaking()) {
            GlStateManager.translatef(0.0f, 0.2f, 0.0f);
        }
        final boolean boolean12 = aix instanceof Villager || aix instanceof ZombieVillager;
        if (aix.isBaby() && !(aix instanceof Villager)) {
            final float float9 = 2.0f;
            final float float10 = 1.4f;
            GlStateManager.translatef(0.0f, 0.5f * float8, 0.0f);
            GlStateManager.scalef(0.7f, 0.7f, 0.7f);
            GlStateManager.translatef(0.0f, 16.0f * float8, 0.0f);
        }
        this.getParentModel().translateToHead(0.0625f);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (bce11 instanceof BlockItem && ((BlockItem)bce11).getBlock() instanceof AbstractSkullBlock) {
            final float float9 = 1.1875f;
            GlStateManager.scalef(1.1875f, -1.1875f, -1.1875f);
            if (boolean12) {
                GlStateManager.translatef(0.0f, 0.0625f, 0.0f);
            }
            GameProfile gameProfile14 = null;
            if (bcj10.hasTag()) {
                final CompoundTag id15 = bcj10.getTag();
                if (id15.contains("SkullOwner", 10)) {
                    gameProfile14 = NbtUtils.readGameProfile(id15.getCompound("SkullOwner"));
                }
                else if (id15.contains("SkullOwner", 8)) {
                    final String string16 = id15.getString("SkullOwner");
                    if (!StringUtils.isBlank((CharSequence)string16)) {
                        gameProfile14 = SkullBlockEntity.updateGameprofile(new GameProfile((UUID)null, string16));
                        id15.put("SkullOwner", (Tag)NbtUtils.writeGameProfile(new CompoundTag(), gameProfile14));
                    }
                }
            }
            SkullBlockRenderer.instance.renderSkull(-0.5f, 0.0f, -0.5f, null, 180.0f, ((AbstractSkullBlock)((BlockItem)bce11).getBlock()).getType(), gameProfile14, -1, float2);
        }
        else if (!(bce11 instanceof ArmorItem) || ((ArmorItem)bce11).getSlot() != EquipmentSlot.HEAD) {
            final float float9 = 0.625f;
            GlStateManager.translatef(0.0f, -0.25f, 0.0f);
            GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
            GlStateManager.scalef(0.625f, -0.625f, -0.625f);
            if (boolean12) {
                GlStateManager.translatef(0.0f, 0.1875f, 0.0f);
            }
            Minecraft.getInstance().getItemInHandRenderer().renderItem(aix, bcj10, ItemTransforms.TransformType.HEAD);
        }
        GlStateManager.popMatrix();
    }
    
    @Override
    public boolean colorsOnDamage() {
        return false;
    }
}
