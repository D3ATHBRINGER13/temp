package net.minecraft.world.level.levelgen.feature.structures;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.util.Deserializer;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.templatesystem.NopProcessor;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.structure.templatesystem.JigsawReplacementProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import java.util.Collections;
import java.util.Random;
import java.util.Iterator;
import net.minecraft.world.level.block.state.properties.StructureMode;
import com.google.common.collect.Lists;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import com.mojang.datafixers.Dynamic;
import java.util.Collection;
import java.util.List;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceLocation;

public class SinglePoolElement extends StructurePoolElement {
    protected final ResourceLocation location;
    protected final ImmutableList<StructureProcessor> processors;
    
    @Deprecated
    public SinglePoolElement(final String string, final List<StructureProcessor> list) {
        this(string, list, StructureTemplatePool.Projection.RIGID);
    }
    
    public SinglePoolElement(final String string, final List<StructureProcessor> list, final StructureTemplatePool.Projection a) {
        super(a);
        this.location = new ResourceLocation(string);
        this.processors = (ImmutableList<StructureProcessor>)ImmutableList.copyOf((Collection)list);
    }
    
    @Deprecated
    public SinglePoolElement(final String string) {
        this(string, (List<StructureProcessor>)ImmutableList.of());
    }
    
    public SinglePoolElement(final Dynamic<?> dynamic) {
        super(dynamic);
        this.location = new ResourceLocation(dynamic.get("location").asString(""));
        this.processors = (ImmutableList<StructureProcessor>)ImmutableList.copyOf((Collection)dynamic.get("processors").asList(dynamic -> Deserializer.<Object, NopProcessor, StructureProcessorType>deserialize((com.mojang.datafixers.Dynamic<Object>)dynamic, Registry.STRUCTURE_PROCESSOR, "processor_type", NopProcessor.INSTANCE)));
    }
    
    public List<StructureTemplate.StructureBlockInfo> getDataMarkers(final StructureManager cjp, final BlockPos ew, final Rotation brg, final boolean boolean4) {
        final StructureTemplate cjt6 = cjp.getOrCreate(this.location);
        final List<StructureTemplate.StructureBlockInfo> list7 = cjt6.filterBlocks(ew, new StructurePlaceSettings().setRotation(brg), Blocks.STRUCTURE_BLOCK, boolean4);
        final List<StructureTemplate.StructureBlockInfo> list8 = (List<StructureTemplate.StructureBlockInfo>)Lists.newArrayList();
        for (final StructureTemplate.StructureBlockInfo b10 : list7) {
            if (b10.nbt == null) {
                continue;
            }
            final StructureMode bxb11 = StructureMode.valueOf(b10.nbt.getString("mode"));
            if (bxb11 != StructureMode.DATA) {
                continue;
            }
            list8.add(b10);
        }
        return list8;
    }
    
    @Override
    public List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(final StructureManager cjp, final BlockPos ew, final Rotation brg, final Random random) {
        final StructureTemplate cjt6 = cjp.getOrCreate(this.location);
        final List<StructureTemplate.StructureBlockInfo> list7 = cjt6.filterBlocks(ew, new StructurePlaceSettings().setRotation(brg), Blocks.JIGSAW_BLOCK, true);
        Collections.shuffle((List)list7, random);
        return list7;
    }
    
    @Override
    public BoundingBox getBoundingBox(final StructureManager cjp, final BlockPos ew, final Rotation brg) {
        final StructureTemplate cjt5 = cjp.getOrCreate(this.location);
        return cjt5.getBoundingBox(new StructurePlaceSettings().setRotation(brg), ew);
    }
    
    @Override
    public boolean place(final StructureManager cjp, final LevelAccessor bhs, final BlockPos ew, final Rotation brg, final BoundingBox cic, final Random random) {
        final StructureTemplate cjt8 = cjp.getOrCreate(this.location);
        final StructurePlaceSettings cjq9 = this.getSettings(brg, cic);
        if (cjt8.placeInWorld(bhs, ew, cjq9, 18)) {
            final List<StructureTemplate.StructureBlockInfo> list10 = StructureTemplate.processBlockInfos(bhs, ew, cjq9, this.getDataMarkers(cjp, ew, brg, false));
            for (final StructureTemplate.StructureBlockInfo b12 : list10) {
                this.handleDataMarker(bhs, b12, ew, brg, random, cic);
            }
            return true;
        }
        return false;
    }
    
    protected StructurePlaceSettings getSettings(final Rotation brg, final BoundingBox cic) {
        final StructurePlaceSettings cjq4 = new StructurePlaceSettings();
        cjq4.setBoundingBox(cic);
        cjq4.setRotation(brg);
        cjq4.setKnownShape(true);
        cjq4.setIgnoreEntities(false);
        cjq4.addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
        cjq4.addProcessor(JigsawReplacementProcessor.INSTANCE);
        this.processors.forEach(cjq4::addProcessor);
        this.getProjection().getProcessors().forEach(cjq4::addProcessor);
        return cjq4;
    }
    
    @Override
    public StructurePoolElementType getType() {
        return StructurePoolElementType.SINGLE;
    }
    
    public <T> Dynamic<T> getDynamic(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("location"), dynamicOps.createString(this.location.toString()), dynamicOps.createString("processors"), dynamicOps.createList(this.processors.stream().map(cjr -> cjr.serialize((com.mojang.datafixers.types.DynamicOps<Object>)dynamicOps).getValue())))));
    }
    
    public String toString() {
        return new StringBuilder().append("Single[").append(this.location).append("]").toString();
    }
}
