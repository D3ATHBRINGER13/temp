package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.player.Player;

public class ParrotOnShoulderLayer<T extends Player> extends RenderLayer<T, PlayerModel<T>> {
    private final ParrotModel model;
    
    public ParrotOnShoulderLayer(final RenderLayerParent<T, PlayerModel<T>> dtr) {
        super(dtr);
        this.model = new ParrotModel();
    }
    
    @Override
    public void render(final T awg, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        GlStateManager.enableRescaleNormal();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.render(awg, float2, float3, float4, float6, float7, float8, true);
        this.render(awg, float2, float3, float4, float6, float7, float8, false);
        GlStateManager.disableRescaleNormal();
    }
    
    private void render(final T awg, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final boolean boolean8) {
        final CompoundTag id10 = boolean8 ? awg.getShoulderEntityLeft() : awg.getShoulderEntityRight();
        EntityType.byString(id10.getString("id")).filter(ais -> ais == EntityType.PARROT).ifPresent(ais -> {
            GlStateManager.pushMatrix();
            GlStateManager.translatef(boolean8 ? 0.4f : -0.4f, awg.isVisuallySneaking() ? -1.3f : -1.5f, 0.0f);
            this.bindTexture(ParrotRenderer.PARROT_LOCATIONS[id10.getInt("Variant")]);
            this.model.renderOnShoulder(float2, float3, float5, float6, float7, awg.tickCount);
            GlStateManager.popMatrix();
        });
    }
    
    @Override
    public boolean colorsOnDamage() {
        return false;
    }
}
