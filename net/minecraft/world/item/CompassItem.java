package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.LivingEntity;
import javax.annotation.Nullable;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;

public class CompassItem extends Item {
    public CompassItem(final Properties a) {
        super(a);
        this.addProperty(new ResourceLocation("angle"), new ItemPropertyFunction() {
            private double rotation;
            private double rota;
            private long lastUpdateTick;
            
            public float call(final ItemStack bcj, @Nullable Level bhr, @Nullable final LivingEntity aix) {
                if (aix == null && !bcj.isFramed()) {
                    return 0.0f;
                }
                final boolean boolean5 = aix != null;
                final Entity aio6 = boolean5 ? aix : bcj.getFrame();
                if (bhr == null) {
                    bhr = aio6.level;
                }
                double double11;
                if (bhr.dimension.isNaturalDimension()) {
                    double double9 = boolean5 ? aio6.yRot : this.getFrameRotation((ItemFrame)aio6);
                    double9 = Mth.positiveModulo(double9 / 360.0, 1.0);
                    final double double10 = this.getSpawnToAngle(bhr, aio6) / 6.2831854820251465;
                    double11 = 0.5 - (double9 - 0.25 - double10);
                }
                else {
                    double11 = Math.random();
                }
                if (boolean5) {
                    double11 = this.wobble(bhr, double11);
                }
                return Mth.positiveModulo((float)double11, 1.0f);
            }
            
            private double wobble(final Level bhr, final double double2) {
                if (bhr.getGameTime() != this.lastUpdateTick) {
                    this.lastUpdateTick = bhr.getGameTime();
                    double double3 = double2 - this.rotation;
                    double3 = Mth.positiveModulo(double3 + 0.5, 1.0) - 0.5;
                    this.rota += double3 * 0.1;
                    this.rota *= 0.8;
                    this.rotation = Mth.positiveModulo(this.rotation + this.rota, 1.0);
                }
                return this.rotation;
            }
            
            private double getFrameRotation(final ItemFrame atn) {
                return Mth.wrapDegrees(180 + atn.getDirection().get2DDataValue() * 90);
            }
            
            private double getSpawnToAngle(final LevelAccessor bhs, final Entity aio) {
                final BlockPos ew4 = bhs.getSharedSpawnPos();
                return Math.atan2(ew4.getZ() - aio.z, ew4.getX() - aio.x);
            }
        });
    }
}
