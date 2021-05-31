package net.minecraft.world.level.levelgen.feature.structures;

import net.minecraft.util.Deserializer;
import net.minecraft.core.Registry;
import java.util.stream.Collectors;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.LevelAccessor;
import java.util.Iterator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import java.util.Random;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import com.mojang.datafixers.Dynamic;
import java.util.List;

public class ListPoolElement extends StructurePoolElement {
    private final List<StructurePoolElement> elements;
    
    @Deprecated
    public ListPoolElement(final List<StructurePoolElement> list) {
        this(list, StructureTemplatePool.Projection.RIGID);
    }
    
    public ListPoolElement(final List<StructurePoolElement> list, final StructureTemplatePool.Projection a) {
        super(a);
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Elements are empty");
        }
        this.elements = list;
        this.setProjectionOnEachElement(a);
    }
    
    public ListPoolElement(final Dynamic<?> dynamic) {
        super(dynamic);
        final List<StructurePoolElement> list3 = (List<StructurePoolElement>)dynamic.get("elements").asList(dynamic -> Deserializer.<Object, EmptyPoolElement, StructurePoolElementType>deserialize((com.mojang.datafixers.Dynamic<Object>)dynamic, Registry.STRUCTURE_POOL_ELEMENT, "element_type", EmptyPoolElement.INSTANCE));
        if (list3.isEmpty()) {
            throw new IllegalArgumentException("Elements are empty");
        }
        this.elements = list3;
    }
    
    @Override
    public List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(final StructureManager cjp, final BlockPos ew, final Rotation brg, final Random random) {
        return ((StructurePoolElement)this.elements.get(0)).getShuffledJigsawBlocks(cjp, ew, brg, random);
    }
    
    @Override
    public BoundingBox getBoundingBox(final StructureManager cjp, final BlockPos ew, final Rotation brg) {
        final BoundingBox cic5 = BoundingBox.getUnknownBox();
        for (final StructurePoolElement cfr7 : this.elements) {
            final BoundingBox cic6 = cfr7.getBoundingBox(cjp, ew, brg);
            cic5.expand(cic6);
        }
        return cic5;
    }
    
    @Override
    public boolean place(final StructureManager cjp, final LevelAccessor bhs, final BlockPos ew, final Rotation brg, final BoundingBox cic, final Random random) {
        for (final StructurePoolElement cfr9 : this.elements) {
            if (!cfr9.place(cjp, bhs, ew, brg, cic, random)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public StructurePoolElementType getType() {
        return StructurePoolElementType.LIST;
    }
    
    @Override
    public StructurePoolElement setProjection(final StructureTemplatePool.Projection a) {
        super.setProjection(a);
        this.setProjectionOnEachElement(a);
        return this;
    }
    
    public <T> Dynamic<T> getDynamic(final DynamicOps<T> dynamicOps) {
        final T object3 = (T)dynamicOps.createList(this.elements.stream().map(cfr -> cfr.serialize((com.mojang.datafixers.types.DynamicOps<Object>)dynamicOps).getValue()));
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("elements"), object3)));
    }
    
    public String toString() {
        return "List[" + (String)this.elements.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
    }
    
    private void setProjectionOnEachElement(final StructureTemplatePool.Projection a) {
        this.elements.forEach(cfr -> cfr.setProjection(a));
    }
}
