package net.minecraft.world.effect;

import org.apache.logging.log4j.LogManager;
import com.google.common.collect.ComparisonChain;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.apache.logging.log4j.Logger;

public class MobEffectInstance implements Comparable<MobEffectInstance> {
    private static final Logger LOGGER;
    private final MobEffect effect;
    private int duration;
    private int amplifier;
    private boolean splash;
    private boolean ambient;
    private boolean noCounter;
    private boolean visible;
    private boolean showIcon;
    
    public MobEffectInstance(final MobEffect aig) {
        this(aig, 0, 0);
    }
    
    public MobEffectInstance(final MobEffect aig, final int integer) {
        this(aig, integer, 0);
    }
    
    public MobEffectInstance(final MobEffect aig, final int integer2, final int integer3) {
        this(aig, integer2, integer3, false, true);
    }
    
    public MobEffectInstance(final MobEffect aig, final int integer2, final int integer3, final boolean boolean4, final boolean boolean5) {
        this(aig, integer2, integer3, boolean4, boolean5, boolean5);
    }
    
    public MobEffectInstance(final MobEffect aig, final int integer2, final int integer3, final boolean boolean4, final boolean boolean5, final boolean boolean6) {
        this.effect = aig;
        this.duration = integer2;
        this.amplifier = integer3;
        this.ambient = boolean4;
        this.visible = boolean5;
        this.showIcon = boolean6;
    }
    
    public MobEffectInstance(final MobEffectInstance aii) {
        this.effect = aii.effect;
        this.duration = aii.duration;
        this.amplifier = aii.amplifier;
        this.ambient = aii.ambient;
        this.visible = aii.visible;
        this.showIcon = aii.showIcon;
    }
    
    public boolean update(final MobEffectInstance aii) {
        if (this.effect != aii.effect) {
            MobEffectInstance.LOGGER.warn("This method should only be called for matching effects!");
        }
        boolean boolean3 = false;
        if (aii.amplifier > this.amplifier) {
            this.amplifier = aii.amplifier;
            this.duration = aii.duration;
            boolean3 = true;
        }
        else if (aii.amplifier == this.amplifier && this.duration < aii.duration) {
            this.duration = aii.duration;
            boolean3 = true;
        }
        if ((!aii.ambient && this.ambient) || boolean3) {
            this.ambient = aii.ambient;
            boolean3 = true;
        }
        if (aii.visible != this.visible) {
            this.visible = aii.visible;
            boolean3 = true;
        }
        if (aii.showIcon != this.showIcon) {
            this.showIcon = aii.showIcon;
            boolean3 = true;
        }
        return boolean3;
    }
    
    public MobEffect getEffect() {
        return this.effect;
    }
    
    public int getDuration() {
        return this.duration;
    }
    
    public int getAmplifier() {
        return this.amplifier;
    }
    
    public boolean isAmbient() {
        return this.ambient;
    }
    
    public boolean isVisible() {
        return this.visible;
    }
    
    public boolean showIcon() {
        return this.showIcon;
    }
    
    public boolean tick(final LivingEntity aix) {
        if (this.duration > 0) {
            if (this.effect.isDurationEffectTick(this.duration, this.amplifier)) {
                this.applyEffect(aix);
            }
            this.tickDownDuration();
        }
        return this.duration > 0;
    }
    
    private int tickDownDuration() {
        return --this.duration;
    }
    
    public void applyEffect(final LivingEntity aix) {
        if (this.duration > 0) {
            this.effect.applyEffectTick(aix, this.amplifier);
        }
    }
    
    public String getDescriptionId() {
        return this.effect.getDescriptionId();
    }
    
    public String toString() {
        String string2;
        if (this.amplifier > 0) {
            string2 = this.getDescriptionId() + " x " + (this.amplifier + 1) + ", Duration: " + this.duration;
        }
        else {
            string2 = this.getDescriptionId() + ", Duration: " + this.duration;
        }
        if (this.splash) {
            string2 += ", Splash: true";
        }
        if (!this.visible) {
            string2 += ", Particles: false";
        }
        if (!this.showIcon) {
            string2 += ", Show Icon: false";
        }
        return string2;
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof MobEffectInstance) {
            final MobEffectInstance aii3 = (MobEffectInstance)object;
            return this.duration == aii3.duration && this.amplifier == aii3.amplifier && this.splash == aii3.splash && this.ambient == aii3.ambient && this.effect.equals(aii3.effect);
        }
        return false;
    }
    
    public int hashCode() {
        int integer2 = this.effect.hashCode();
        integer2 = 31 * integer2 + this.duration;
        integer2 = 31 * integer2 + this.amplifier;
        integer2 = 31 * integer2 + (this.splash ? 1 : 0);
        integer2 = 31 * integer2 + (this.ambient ? 1 : 0);
        return integer2;
    }
    
    public CompoundTag save(final CompoundTag id) {
        id.putByte("Id", (byte)MobEffect.getId(this.getEffect()));
        id.putByte("Amplifier", (byte)this.getAmplifier());
        id.putInt("Duration", this.getDuration());
        id.putBoolean("Ambient", this.isAmbient());
        id.putBoolean("ShowParticles", this.isVisible());
        id.putBoolean("ShowIcon", this.showIcon());
        return id;
    }
    
    public static MobEffectInstance load(final CompoundTag id) {
        final int integer2 = id.getByte("Id");
        final MobEffect aig3 = MobEffect.byId(integer2);
        if (aig3 == null) {
            return null;
        }
        final int integer3 = id.getByte("Amplifier");
        final int integer4 = id.getInt("Duration");
        final boolean boolean6 = id.getBoolean("Ambient");
        boolean boolean7 = true;
        if (id.contains("ShowParticles", 1)) {
            boolean7 = id.getBoolean("ShowParticles");
        }
        boolean boolean8 = boolean7;
        if (id.contains("ShowIcon", 1)) {
            boolean8 = id.getBoolean("ShowIcon");
        }
        return new MobEffectInstance(aig3, integer4, (integer3 < 0) ? 0 : integer3, boolean6, boolean7, boolean8);
    }
    
    public void setNoCounter(final boolean boolean1) {
        this.noCounter = boolean1;
    }
    
    public boolean isNoCounter() {
        return this.noCounter;
    }
    
    public int compareTo(final MobEffectInstance aii) {
        final int integer3 = 32147;
        if ((this.getDuration() > 32147 && aii.getDuration() > 32147) || (this.isAmbient() && aii.isAmbient())) {
            return ComparisonChain.start().compare(Boolean.valueOf(this.isAmbient()), Boolean.valueOf(aii.isAmbient())).compare(this.getEffect().getColor(), aii.getEffect().getColor()).result();
        }
        return ComparisonChain.start().compare(Boolean.valueOf(this.isAmbient()), Boolean.valueOf(aii.isAmbient())).compare(this.getDuration(), aii.getDuration()).compare(this.getEffect().getColor(), aii.getEffect().getColor()).result();
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
