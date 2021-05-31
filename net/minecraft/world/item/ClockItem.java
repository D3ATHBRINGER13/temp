package net.minecraft.world.item;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import javax.annotation.Nullable;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;

public class ClockItem extends Item {
    public ClockItem(final Properties a) {
        super(a);
        this.addProperty(new ResourceLocation("time"), new ItemPropertyFunction() {
            private double rotation;
            private double rota;
            private long lastUpdateTick;
            
            public float call(final ItemStack bcj, @Nullable Level bhr, @Nullable final LivingEntity aix) {
                final boolean boolean5 = aix != null;
                final Entity aio6 = boolean5 ? aix : bcj.getFrame();
                if (bhr == null && aio6 != null) {
                    bhr = aio6.level;
                }
                if (bhr == null) {
                    return 0.0f;
                }
                double double7;
                if (bhr.dimension.isNaturalDimension()) {
                    double7 = bhr.getTimeOfDay(1.0f);
                }
                else {
                    double7 = Math.random();
                }
                double7 = this.wobble(bhr, double7);
                return (float)double7;
            }
            
            private double wobble(final Level bhr, final double double2) {
                if (bhr.getGameTime() != this.lastUpdateTick) {
                    this.lastUpdateTick = bhr.getGameTime();
                    double double3 = double2 - this.rotation;
                    double3 = Mth.positiveModulo(double3 + 0.5, 1.0) - 0.5;
                    this.rota += double3 * 0.1;
                    this.rota *= 0.9;
                    this.rotation = Mth.positiveModulo(this.rotation + this.rota, 1.0);
                }
                return this.rotation;
            }
        });
    }
}
