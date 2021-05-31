package net.minecraft.data.tags;

import net.minecraft.tags.TagCollection;
import java.nio.file.Path;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.entity.EntityType;

public class EntityTypeTagsProvider extends TagsProvider<EntityType<?>> {
    public EntityTypeTagsProvider(final DataGenerator gk) {
        super(gk, Registry.ENTITY_TYPE);
    }
    
    @Override
    protected void addTags() {
        this.tag(EntityTypeTags.SKELETONS).add(EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON);
        this.tag(EntityTypeTags.RAIDERS).add(EntityType.EVOKER, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.VINDICATOR, EntityType.ILLUSIONER, EntityType.WITCH);
    }
    
    @Override
    protected Path getPath(final ResourceLocation qv) {
        return this.generator.getOutputFolder().resolve("data/" + qv.getNamespace() + "/tags/entity_types/" + qv.getPath() + ".json");
    }
    
    public String getName() {
        return "Entity Type Tags";
    }
    
    @Override
    protected void useTags(final TagCollection<EntityType<?>> zh) {
        EntityTypeTags.reset(zh);
    }
}
