package net.minecraft.world.level.levelgen.structure.templatesystem;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import com.mojang.datafixers.Dynamic;
import net.minecraft.world.level.levelgen.Heightmap;

public class GravityProcessor extends StructureProcessor {
    private final Heightmap.Types heightmap;
    private final int offset;
    
    public GravityProcessor(final Heightmap.Types a, final int integer) {
        this.heightmap = a;
        this.offset = integer;
    }
    
    public GravityProcessor(final Dynamic<?> dynamic) {
        this(Heightmap.Types.getFromKey(dynamic.get("heightmap").asString(Heightmap.Types.WORLD_SURFACE_WG.getSerializationKey())), dynamic.get("offset").asInt(0));
    }
    
    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(final LevelReader bhu, final BlockPos ew, final StructureTemplate.StructureBlockInfo b3, final StructureTemplate.StructureBlockInfo b4, final StructurePlaceSettings cjq) {
        final int integer7 = bhu.getHeight(this.heightmap, b4.pos.getX(), b4.pos.getZ()) + this.offset;
        final int integer8 = b3.pos.getY();
        return new StructureTemplate.StructureBlockInfo(new BlockPos(b4.pos.getX(), integer7 + integer8, b4.pos.getZ()), b4.state, b4.nbt);
    }
    
    @Override
    protected StructureProcessorType getType() {
        return StructureProcessorType.GRAVITY;
    }
    
    @Override
    protected <T> Dynamic<T> getDynamic(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("heightmap"), dynamicOps.createString(this.heightmap.getSerializationKey()), dynamicOps.createString("offset"), dynamicOps.createInt(this.offset))));
    }
}
