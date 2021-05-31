package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.TurtleModel;
import net.minecraft.world.entity.animal.Turtle;

public class TurtleRenderer extends MobRenderer<Turtle, TurtleModel<Turtle>> {
    private static final ResourceLocation TURTLE_LOCATION;
    
    public TurtleRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new TurtleModel(0.0f), 0.7f);
    }
    
    @Override
    public void render(final Turtle arx, final double double2, final double double3, final double double4, final float float5, final float float6) {
        if (arx.isBaby()) {
            this.shadowRadius *= 0.5f;
        }
        super.render(arx, double2, double3, double4, float5, float6);
    }
    
    @Nullable
    protected ResourceLocation getTextureLocation(final Turtle arx) {
        return TurtleRenderer.TURTLE_LOCATION;
    }
    
    static {
        TURTLE_LOCATION = new ResourceLocation("textures/entity/turtle/big_sea_turtle.png");
    }
}
