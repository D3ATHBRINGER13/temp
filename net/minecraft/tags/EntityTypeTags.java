package net.minecraft.tags;

import java.util.Collection;
import java.util.function.Function;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class EntityTypeTags {
    private static TagCollection<EntityType<?>> source;
    private static int resetCount;
    public static final Tag<EntityType<?>> SKELETONS;
    public static final Tag<EntityType<?>> RAIDERS;
    
    public static void reset(final TagCollection<EntityType<?>> zh) {
        EntityTypeTags.source = zh;
        ++EntityTypeTags.resetCount;
    }
    
    public static TagCollection<EntityType<?>> getAllTags() {
        return EntityTypeTags.source;
    }
    
    private static Tag<EntityType<?>> bind(final String string) {
        return new Wrapper(new ResourceLocation(string));
    }
    
    static {
        EntityTypeTags.source = new TagCollection<EntityType<?>>((java.util.function.Function<ResourceLocation, java.util.Optional<EntityType<?>>>)(qv -> Optional.empty()), "", false, "");
        SKELETONS = bind("skeletons");
        RAIDERS = bind("raiders");
    }
    
    public static class Wrapper extends Tag<EntityType<?>> {
        private int check;
        private Tag<EntityType<?>> actual;
        
        public Wrapper(final ResourceLocation qv) {
            super(qv);
            this.check = -1;
        }
        
        @Override
        public boolean contains(final EntityType<?> ais) {
            if (this.check != EntityTypeTags.resetCount) {
                this.actual = EntityTypeTags.source.getTagOrEmpty(this.getId());
                this.check = EntityTypeTags.resetCount;
            }
            return this.actual.contains(ais);
        }
        
        @Override
        public Collection<EntityType<?>> getValues() {
            if (this.check != EntityTypeTags.resetCount) {
                this.actual = EntityTypeTags.source.getTagOrEmpty(this.getId());
                this.check = EntityTypeTags.resetCount;
            }
            return this.actual.getValues();
        }
        
        @Override
        public Collection<Entry<EntityType<?>>> getSource() {
            if (this.check != EntityTypeTags.resetCount) {
                this.actual = EntityTypeTags.source.getTagOrEmpty(this.getId());
                this.check = EntityTypeTags.resetCount;
            }
            return this.actual.getSource();
        }
    }
}
