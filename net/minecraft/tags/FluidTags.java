package net.minecraft.tags;

import java.util.Collection;
import java.util.function.Function;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

public class FluidTags {
    private static TagCollection<Fluid> source;
    private static int resetCount;
    public static final Tag<Fluid> WATER;
    public static final Tag<Fluid> LAVA;
    
    public static void reset(final TagCollection<Fluid> zh) {
        FluidTags.source = zh;
        ++FluidTags.resetCount;
    }
    
    private static Tag<Fluid> bind(final String string) {
        return new Wrapper(new ResourceLocation(string));
    }
    
    static {
        FluidTags.source = new TagCollection<Fluid>((java.util.function.Function<ResourceLocation, java.util.Optional<Fluid>>)(qv -> Optional.empty()), "", false, "");
        WATER = bind("water");
        LAVA = bind("lava");
    }
    
    public static class Wrapper extends Tag<Fluid> {
        private int check;
        private Tag<Fluid> actual;
        
        public Wrapper(final ResourceLocation qv) {
            super(qv);
            this.check = -1;
        }
        
        @Override
        public boolean contains(final Fluid clj) {
            if (this.check != FluidTags.resetCount) {
                this.actual = FluidTags.source.getTagOrEmpty(this.getId());
                this.check = FluidTags.resetCount;
            }
            return this.actual.contains(clj);
        }
        
        @Override
        public Collection<Fluid> getValues() {
            if (this.check != FluidTags.resetCount) {
                this.actual = FluidTags.source.getTagOrEmpty(this.getId());
                this.check = FluidTags.resetCount;
            }
            return this.actual.getValues();
        }
        
        @Override
        public Collection<Entry<Fluid>> getSource() {
            if (this.check != FluidTags.resetCount) {
                this.actual = FluidTags.source.getTagOrEmpty(this.getId());
                this.check = FluidTags.resetCount;
            }
            return this.actual.getSource();
        }
    }
}
