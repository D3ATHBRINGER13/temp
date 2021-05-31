package net.minecraft.world.level.levelgen.structure.templatesystem;

import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.Util;
import java.util.Collection;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import javax.annotation.Nullable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;

public class StructurePlaceSettings {
    private Mirror mirror;
    private Rotation rotation;
    private BlockPos rotationPivot;
    private boolean ignoreEntities;
    @Nullable
    private ChunkPos chunkPos;
    @Nullable
    private BoundingBox boundingBox;
    private boolean keepLiquids;
    @Nullable
    private Random random;
    @Nullable
    private Integer preferredPalette;
    private int palette;
    private final List<StructureProcessor> processors;
    private boolean knownShape;
    
    public StructurePlaceSettings() {
        this.mirror = Mirror.NONE;
        this.rotation = Rotation.NONE;
        this.rotationPivot = BlockPos.ZERO;
        this.keepLiquids = true;
        this.processors = (List<StructureProcessor>)Lists.newArrayList();
    }
    
    public StructurePlaceSettings copy() {
        final StructurePlaceSettings cjq2 = new StructurePlaceSettings();
        cjq2.mirror = this.mirror;
        cjq2.rotation = this.rotation;
        cjq2.rotationPivot = this.rotationPivot;
        cjq2.ignoreEntities = this.ignoreEntities;
        cjq2.chunkPos = this.chunkPos;
        cjq2.boundingBox = this.boundingBox;
        cjq2.keepLiquids = this.keepLiquids;
        cjq2.random = this.random;
        cjq2.preferredPalette = this.preferredPalette;
        cjq2.palette = this.palette;
        cjq2.processors.addAll((Collection)this.processors);
        cjq2.knownShape = this.knownShape;
        return cjq2;
    }
    
    public StructurePlaceSettings setMirror(final Mirror bqg) {
        this.mirror = bqg;
        return this;
    }
    
    public StructurePlaceSettings setRotation(final Rotation brg) {
        this.rotation = brg;
        return this;
    }
    
    public StructurePlaceSettings setRotationPivot(final BlockPos ew) {
        this.rotationPivot = ew;
        return this;
    }
    
    public StructurePlaceSettings setIgnoreEntities(final boolean boolean1) {
        this.ignoreEntities = boolean1;
        return this;
    }
    
    public StructurePlaceSettings setChunkPos(final ChunkPos bhd) {
        this.chunkPos = bhd;
        return this;
    }
    
    public StructurePlaceSettings setBoundingBox(final BoundingBox cic) {
        this.boundingBox = cic;
        return this;
    }
    
    public StructurePlaceSettings setRandom(@Nullable final Random random) {
        this.random = random;
        return this;
    }
    
    public StructurePlaceSettings setKnownShape(final boolean boolean1) {
        this.knownShape = boolean1;
        return this;
    }
    
    public StructurePlaceSettings clearProcessors() {
        this.processors.clear();
        return this;
    }
    
    public StructurePlaceSettings addProcessor(final StructureProcessor cjr) {
        this.processors.add(cjr);
        return this;
    }
    
    public StructurePlaceSettings popProcessor(final StructureProcessor cjr) {
        this.processors.remove(cjr);
        return this;
    }
    
    public Mirror getMirror() {
        return this.mirror;
    }
    
    public Rotation getRotation() {
        return this.rotation;
    }
    
    public BlockPos getRotationPivot() {
        return this.rotationPivot;
    }
    
    public Random getRandom(@Nullable final BlockPos ew) {
        if (this.random != null) {
            return this.random;
        }
        if (ew == null) {
            return new Random(Util.getMillis());
        }
        return new Random(Mth.getSeed(ew));
    }
    
    public boolean isIgnoreEntities() {
        return this.ignoreEntities;
    }
    
    @Nullable
    public BoundingBox getBoundingBox() {
        if (this.boundingBox == null && this.chunkPos != null) {
            this.updateBoundingBoxFromChunkPos();
        }
        return this.boundingBox;
    }
    
    public boolean getKnownShape() {
        return this.knownShape;
    }
    
    public List<StructureProcessor> getProcessors() {
        return this.processors;
    }
    
    void updateBoundingBoxFromChunkPos() {
        if (this.chunkPos != null) {
            this.boundingBox = this.calculateBoundingBox(this.chunkPos);
        }
    }
    
    public boolean shouldKeepLiquids() {
        return this.keepLiquids;
    }
    
    public List<StructureTemplate.StructureBlockInfo> getPalette(final List<List<StructureTemplate.StructureBlockInfo>> list, @Nullable final BlockPos ew) {
        this.preferredPalette = 8;
        if (this.preferredPalette != null && this.preferredPalette >= 0 && this.preferredPalette < list.size()) {
            return (List<StructureTemplate.StructureBlockInfo>)list.get((int)this.preferredPalette);
        }
        this.preferredPalette = this.getRandom(ew).nextInt(list.size());
        return (List<StructureTemplate.StructureBlockInfo>)list.get((int)this.preferredPalette);
    }
    
    @Nullable
    private BoundingBox calculateBoundingBox(@Nullable final ChunkPos bhd) {
        if (bhd == null) {
            return this.boundingBox;
        }
        final int integer3 = bhd.x * 16;
        final int integer4 = bhd.z * 16;
        return new BoundingBox(integer3, 0, integer4, integer3 + 16 - 1, 255, integer4 + 16 - 1);
    }
}
