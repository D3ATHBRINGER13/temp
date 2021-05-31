package net.minecraft.world.level.levelgen.feature.structures;

import net.minecraft.core.Registry;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import java.util.List;
import java.util.Random;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import com.mojang.datafixers.Dynamic;
import javax.annotation.Nullable;

public abstract class StructurePoolElement {
    @Nullable
    private volatile StructureTemplatePool.Projection projection;
    
    protected StructurePoolElement(final StructureTemplatePool.Projection a) {
        this.projection = a;
    }
    
    protected StructurePoolElement(final Dynamic<?> dynamic) {
        this.projection = StructureTemplatePool.Projection.byName(dynamic.get("projection").asString(StructureTemplatePool.Projection.RIGID.getName()));
    }
    
    public abstract List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(final StructureManager cjp, final BlockPos ew, final Rotation brg, final Random random);
    
    public abstract BoundingBox getBoundingBox(final StructureManager cjp, final BlockPos ew, final Rotation brg);
    
    public abstract boolean place(final StructureManager cjp, final LevelAccessor bhs, final BlockPos ew, final Rotation brg, final BoundingBox cic, final Random random);
    
    public abstract StructurePoolElementType getType();
    
    public void handleDataMarker(final LevelAccessor bhs, final StructureTemplate.StructureBlockInfo b, final BlockPos ew, final Rotation brg, final Random random, final BoundingBox cic) {
    }
    
    public StructurePoolElement setProjection(final StructureTemplatePool.Projection a) {
        this.projection = a;
        return this;
    }
    
    public StructureTemplatePool.Projection getProjection() {
        final StructureTemplatePool.Projection a2 = this.projection;
        if (a2 == null) {
            throw new IllegalStateException();
        }
        return a2;
    }
    
    protected abstract <T> Dynamic<T> getDynamic(final DynamicOps<T> dynamicOps);
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        final T object3 = (T)this.getDynamic((com.mojang.datafixers.types.DynamicOps<Object>)dynamicOps).getValue();
        final T object4 = (T)dynamicOps.mergeInto(object3, dynamicOps.createString("element_type"), dynamicOps.createString(Registry.STRUCTURE_POOL_ELEMENT.getKey(this.getType()).toString()));
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.mergeInto(object4, dynamicOps.createString("projection"), dynamicOps.createString(this.projection.getName())));
    }
    
    public int getGroundLevelDelta() {
        return 1;
    }
}
