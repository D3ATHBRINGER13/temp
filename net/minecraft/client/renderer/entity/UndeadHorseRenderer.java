package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;
import net.minecraft.client.model.HorseModel;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public class UndeadHorseRenderer extends AbstractHorseRenderer<AbstractHorse, HorseModel<AbstractHorse>> {
    private static final Map<Class<?>, ResourceLocation> MAP;
    
    public UndeadHorseRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new HorseModel(0.0f), 1.0f);
    }
    
    protected ResourceLocation getTextureLocation(final AbstractHorse asb) {
        return (ResourceLocation)UndeadHorseRenderer.MAP.get(asb.getClass());
    }
    
    static {
        MAP = (Map)Maps.newHashMap((Map)ImmutableMap.of(ZombieHorse.class, new ResourceLocation("textures/entity/horse/horse_zombie.png"), SkeletonHorse.class, new ResourceLocation("textures/entity/horse/horse_skeleton.png")));
    }
}
