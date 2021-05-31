package net.minecraft.world.level.levelgen.feature.structures;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.core.Registry;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import com.google.common.collect.Lists;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import com.mojang.datafixers.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class FeaturePoolElement extends StructurePoolElement {
    private final ConfiguredFeature<?> feature;
    private final CompoundTag defaultJigsawNBT;
    
    @Deprecated
    public FeaturePoolElement(final ConfiguredFeature<?> cal) {
        this(cal, StructureTemplatePool.Projection.RIGID);
    }
    
    public FeaturePoolElement(final ConfiguredFeature<?> cal, final StructureTemplatePool.Projection a) {
        super(a);
        this.feature = cal;
        this.defaultJigsawNBT = this.fillDefaultJigsawNBT();
    }
    
    public <T> FeaturePoolElement(final Dynamic<T> dynamic) {
        super(dynamic);
        this.feature = ConfiguredFeature.deserialize((com.mojang.datafixers.Dynamic<Object>)dynamic.get("feature").orElseEmptyMap());
        this.defaultJigsawNBT = this.fillDefaultJigsawNBT();
    }
    
    public CompoundTag fillDefaultJigsawNBT() {
        final CompoundTag id2 = new CompoundTag();
        id2.putString("target_pool", "minecraft:empty");
        id2.putString("attachement_type", "minecraft:bottom");
        id2.putString("final_state", "minecraft:air");
        return id2;
    }
    
    public BlockPos getSize(final StructureManager cjp, final Rotation brg) {
        return BlockPos.ZERO;
    }
    
    @Override
    public List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(final StructureManager cjp, final BlockPos ew, final Rotation brg, final Random random) {
        final List<StructureTemplate.StructureBlockInfo> list6 = (List<StructureTemplate.StructureBlockInfo>)Lists.newArrayList();
        list6.add(new StructureTemplate.StructureBlockInfo(ew, ((AbstractStateHolder<O, BlockState>)Blocks.JIGSAW_BLOCK.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)JigsawBlock.FACING, Direction.DOWN), this.defaultJigsawNBT));
        return list6;
    }
    
    @Override
    public BoundingBox getBoundingBox(final StructureManager cjp, final BlockPos ew, final Rotation brg) {
        final BlockPos ew2 = this.getSize(cjp, brg);
        return new BoundingBox(ew.getX(), ew.getY(), ew.getZ(), ew.getX() + ew2.getX(), ew.getY() + ew2.getY(), ew.getZ() + ew2.getZ());
    }
    
    @Override
    public boolean place(final StructureManager cjp, final LevelAccessor bhs, final BlockPos ew, final Rotation brg, final BoundingBox cic, final Random random) {
        final ChunkGenerator<?> bxi8 = bhs.getChunkSource().getGenerator();
        return this.feature.place(bhs, bxi8, random, ew);
    }
    
    public <T> Dynamic<T> getDynamic(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("feature"), this.feature.<T>serialize(dynamicOps).getValue())));
    }
    
    @Override
    public StructurePoolElementType getType() {
        return StructurePoolElementType.FEATURE;
    }
    
    public String toString() {
        return new StringBuilder().append("Feature[").append(Registry.FEATURE.getKey(this.feature.feature)).append("]").toString();
    }
}
