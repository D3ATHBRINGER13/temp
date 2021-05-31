package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ZombieModel;
import net.minecraft.world.entity.monster.Zombie;

public class ZombieRenderer extends AbstractZombieRenderer<Zombie, ZombieModel<Zombie>> {
    public ZombieRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new ZombieModel(), new ZombieModel(0.5f, true), new ZombieModel(1.0f, true));
    }
}
