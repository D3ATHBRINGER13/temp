package net.minecraft.world.level.levelgen.feature.structures;

import java.util.stream.Collectors;
import java.util.Arrays;
import net.minecraft.world.level.levelgen.structure.templatesystem.GravityProcessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import java.util.Map;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.Random;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import java.util.Iterator;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import com.mojang.datafixers.util.Pair;
import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceLocation;

public class StructureTemplatePool {
    public static final StructureTemplatePool EMPTY;
    public static final StructureTemplatePool INVALID;
    private final ResourceLocation name;
    private final ImmutableList<Pair<StructurePoolElement, Integer>> rawTemplates;
    private final List<StructurePoolElement> templates;
    private final ResourceLocation fallback;
    private final Projection projection;
    private int maxSize;
    
    public StructureTemplatePool(final ResourceLocation qv1, final ResourceLocation qv2, final List<Pair<StructurePoolElement, Integer>> list, final Projection a) {
        this.maxSize = Integer.MIN_VALUE;
        this.name = qv1;
        this.rawTemplates = (ImmutableList<Pair<StructurePoolElement, Integer>>)ImmutableList.copyOf((Collection)list);
        this.templates = (List<StructurePoolElement>)Lists.newArrayList();
        for (final Pair<StructurePoolElement, Integer> pair7 : list) {
            for (Integer integer8 = 0; integer8 < (int)pair7.getSecond(); ++integer8) {
                this.templates.add(((StructurePoolElement)pair7.getFirst()).setProjection(a));
            }
        }
        this.fallback = qv2;
        this.projection = a;
    }
    
    public int getMaxSize(final StructureManager cjp) {
        if (this.maxSize == Integer.MIN_VALUE) {
            this.maxSize = this.templates.stream().mapToInt(cfr -> cfr.getBoundingBox(cjp, BlockPos.ZERO, Rotation.NONE).getYSpan()).max().orElse(0);
        }
        return this.maxSize;
    }
    
    public ResourceLocation getFallback() {
        return this.fallback;
    }
    
    public StructurePoolElement getRandomTemplate(final Random random) {
        return (StructurePoolElement)this.templates.get(random.nextInt(this.templates.size()));
    }
    
    public List<StructurePoolElement> getShuffledTemplates(final Random random) {
        return (List<StructurePoolElement>)ImmutableList.copyOf(ObjectArrays.shuffle(this.templates.toArray((Object[])new StructurePoolElement[0]), random));
    }
    
    public ResourceLocation getName() {
        return this.name;
    }
    
    public int size() {
        return this.templates.size();
    }
    
    static {
        EMPTY = new StructureTemplatePool(new ResourceLocation("empty"), new ResourceLocation("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of(), Projection.RIGID);
        INVALID = new StructureTemplatePool(new ResourceLocation("invalid"), new ResourceLocation("invalid"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of(), Projection.RIGID);
    }
    
    public enum Projection {
        TERRAIN_MATCHING("terrain_matching", (ImmutableList<StructureProcessor>)ImmutableList.of(new GravityProcessor(Heightmap.Types.WORLD_SURFACE_WG, -1))), 
        RIGID("rigid", (ImmutableList<StructureProcessor>)ImmutableList.of());
        
        private static final Map<String, Projection> BY_NAME;
        private final String name;
        private final ImmutableList<StructureProcessor> processors;
        
        private Projection(final String string3, final ImmutableList<StructureProcessor> immutableList) {
            this.name = string3;
            this.processors = immutableList;
        }
        
        public String getName() {
            return this.name;
        }
        
        public static Projection byName(final String string) {
            return (Projection)Projection.BY_NAME.get(string);
        }
        
        public ImmutableList<StructureProcessor> getProcessors() {
            return this.processors;
        }
        
        static {
            BY_NAME = (Map)Arrays.stream((Object[])values()).collect(Collectors.toMap(Projection::getName, a -> a));
        }
    }
}
