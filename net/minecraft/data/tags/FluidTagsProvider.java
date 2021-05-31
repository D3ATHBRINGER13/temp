package net.minecraft.data.tags;

import net.minecraft.tags.TagCollection;
import java.nio.file.Path;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.material.Fluid;

public class FluidTagsProvider extends TagsProvider<Fluid> {
    public FluidTagsProvider(final DataGenerator gk) {
        super(gk, Registry.FLUID);
    }
    
    @Override
    protected void addTags() {
        this.tag(FluidTags.WATER).add(Fluids.WATER, Fluids.FLOWING_WATER);
        this.tag(FluidTags.LAVA).add(Fluids.LAVA, Fluids.FLOWING_LAVA);
    }
    
    @Override
    protected Path getPath(final ResourceLocation qv) {
        return this.generator.getOutputFolder().resolve("data/" + qv.getNamespace() + "/tags/fluids/" + qv.getPath() + ".json");
    }
    
    public String getName() {
        return "Fluid Tags";
    }
    
    @Override
    protected void useTags(final TagCollection<Fluid> zh) {
        FluidTags.reset(zh);
    }
}
