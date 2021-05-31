package net.minecraft.world.level.levelgen.feature.structures;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import java.util.Collections;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import java.util.List;
import java.util.Random;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class EmptyPoolElement extends StructurePoolElement {
    public static final EmptyPoolElement INSTANCE;
    
    private EmptyPoolElement() {
        super(StructureTemplatePool.Projection.TERRAIN_MATCHING);
    }
    
    @Override
    public List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(final StructureManager cjp, final BlockPos ew, final Rotation brg, final Random random) {
        return (List<StructureTemplate.StructureBlockInfo>)Collections.emptyList();
    }
    
    @Override
    public BoundingBox getBoundingBox(final StructureManager cjp, final BlockPos ew, final Rotation brg) {
        return BoundingBox.getUnknownBox();
    }
    
    @Override
    public boolean place(final StructureManager cjp, final LevelAccessor bhs, final BlockPos ew, final Rotation brg, final BoundingBox cic, final Random random) {
        return true;
    }
    
    @Override
    public StructurePoolElementType getType() {
        return StructurePoolElementType.EMPTY;
    }
    
    public <T> Dynamic<T> getDynamic(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.emptyMap());
    }
    
    public String toString() {
        return "Empty";
    }
    
    static {
        INSTANCE = new EmptyPoolElement();
    }
}
