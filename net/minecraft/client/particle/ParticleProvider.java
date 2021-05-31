package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleOptions;

public interface ParticleProvider<T extends ParticleOptions> {
    @Nullable
    Particle createParticle(final T gf, final Level bhr, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8);
}
