package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.renderer.texture.TextureObject;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.HorseArmorLayer;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;
import net.minecraft.client.model.HorseModel;
import net.minecraft.world.entity.animal.horse.Horse;

public final class HorseRenderer extends AbstractHorseRenderer<Horse, HorseModel<Horse>> {
    private static final Map<String, ResourceLocation> LAYERED_LOCATION_CACHE;
    
    public HorseRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new HorseModel(0.0f), 1.1f);
        this.addLayer(new HorseArmorLayer(this));
    }
    
    protected ResourceLocation getTextureLocation(final Horse asd) {
        final String string3 = asd.getLayeredTextureHashName();
        ResourceLocation qv4 = (ResourceLocation)HorseRenderer.LAYERED_LOCATION_CACHE.get(string3);
        if (qv4 == null) {
            qv4 = new ResourceLocation(string3);
            Minecraft.getInstance().getTextureManager().register(qv4, new LayeredTexture(asd.getLayeredTextureLayers()));
            HorseRenderer.LAYERED_LOCATION_CACHE.put(string3, qv4);
        }
        return qv4;
    }
    
    static {
        LAYERED_LOCATION_CACHE = (Map)Maps.newHashMap();
    }
}
