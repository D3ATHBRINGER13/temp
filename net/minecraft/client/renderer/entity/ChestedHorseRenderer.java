package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.animal.horse.Mule;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;
import net.minecraft.client.model.ChestedHorseModel;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;

public class ChestedHorseRenderer<T extends AbstractChestedHorse> extends AbstractHorseRenderer<T, ChestedHorseModel<T>> {
    private static final Map<Class<?>, ResourceLocation> MAP;
    
    public ChestedHorseRenderer(final EntityRenderDispatcher dsa, final float float2) {
        super(dsa, new ChestedHorseModel(0.0f), float2);
    }
    
    protected ResourceLocation getTextureLocation(final T asa) {
        return (ResourceLocation)ChestedHorseRenderer.MAP.get(asa.getClass());
    }
    
    static {
        MAP = (Map)Maps.newHashMap((Map)ImmutableMap.of(Donkey.class, new ResourceLocation("textures/entity/horse/donkey.png"), Mule.class, new ResourceLocation("textures/entity/horse/mule.png")));
    }
}
