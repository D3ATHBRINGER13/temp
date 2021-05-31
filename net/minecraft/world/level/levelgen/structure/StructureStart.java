package net.minecraft.world.level.levelgen.structure;

import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import java.util.Iterator;
import net.minecraft.world.level.ChunkPos;
import java.util.Random;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import com.google.common.collect.Lists;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.biome.Biome;
import java.util.List;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

public abstract class StructureStart {
    public static final StructureStart INVALID_START;
    private final StructureFeature<?> feature;
    protected final List<StructurePiece> pieces;
    protected BoundingBox boundingBox;
    private final int chunkX;
    private final int chunkZ;
    private final Biome biome;
    private int references;
    protected final WorldgenRandom random;
    
    public StructureStart(final StructureFeature<?> ceu, final int integer2, final int integer3, final Biome bio, final BoundingBox cic, final int integer6, final long long7) {
        this.pieces = (List<StructurePiece>)Lists.newArrayList();
        this.feature = ceu;
        this.chunkX = integer2;
        this.chunkZ = integer3;
        this.references = integer6;
        this.biome = bio;
        (this.random = new WorldgenRandom()).setLargeFeatureSeed(long7, integer2, integer3);
        this.boundingBox = cic;
    }
    
    public abstract void generatePieces(final ChunkGenerator<?> bxi, final StructureManager cjp, final int integer3, final int integer4, final Biome bio);
    
    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }
    
    public List<StructurePiece> getPieces() {
        return this.pieces;
    }
    
    public void postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
        synchronized (this.pieces) {
            final Iterator<StructurePiece> iterator7 = (Iterator<StructurePiece>)this.pieces.iterator();
            while (iterator7.hasNext()) {
                final StructurePiece civ8 = (StructurePiece)iterator7.next();
                if (civ8.getBoundingBox().intersects(cic) && !civ8.postProcess(bhs, random, cic, bhd)) {
                    iterator7.remove();
                }
            }
            this.calculateBoundingBox();
        }
    }
    
    protected void calculateBoundingBox() {
        this.boundingBox = BoundingBox.getUnknownBox();
        for (final StructurePiece civ3 : this.pieces) {
            this.boundingBox.expand(civ3.getBoundingBox());
        }
    }
    
    public CompoundTag createTag(final int integer1, final int integer2) {
        final CompoundTag id4 = new CompoundTag();
        if (this.isValid()) {
            id4.putString("id", Registry.STRUCTURE_FEATURE.getKey(this.getFeature()).toString());
            id4.putString("biome", Registry.BIOME.getKey(this.biome).toString());
            id4.putInt("ChunkX", integer1);
            id4.putInt("ChunkZ", integer2);
            id4.putInt("references", this.references);
            id4.put("BB", (Tag)this.boundingBox.createTag());
            final ListTag ik5 = new ListTag();
            synchronized (this.pieces) {
                for (final StructurePiece civ8 : this.pieces) {
                    ik5.add(civ8.createTag());
                }
            }
            id4.put("Children", (Tag)ik5);
            return id4;
        }
        id4.putString("id", "INVALID");
        return id4;
    }
    
    protected void moveBelowSeaLevel(final int integer1, final Random random, final int integer3) {
        final int integer4 = integer1 - integer3;
        int integer5 = this.boundingBox.getYSpan() + 1;
        if (integer5 < integer4) {
            integer5 += random.nextInt(integer4 - integer5);
        }
        final int integer6 = integer5 - this.boundingBox.y1;
        this.boundingBox.move(0, integer6, 0);
        for (final StructurePiece civ9 : this.pieces) {
            civ9.move(0, integer6, 0);
        }
    }
    
    protected void moveInsideHeights(final Random random, final int integer2, final int integer3) {
        final int integer4 = integer3 - integer2 + 1 - this.boundingBox.getYSpan();
        int integer5;
        if (integer4 > 1) {
            integer5 = integer2 + random.nextInt(integer4);
        }
        else {
            integer5 = integer2;
        }
        final int integer6 = integer5 - this.boundingBox.y0;
        this.boundingBox.move(0, integer6, 0);
        for (final StructurePiece civ9 : this.pieces) {
            civ9.move(0, integer6, 0);
        }
    }
    
    public boolean isValid() {
        return !this.pieces.isEmpty();
    }
    
    public int getChunkX() {
        return this.chunkX;
    }
    
    public int getChunkZ() {
        return this.chunkZ;
    }
    
    public BlockPos getLocatePos() {
        return new BlockPos(this.chunkX << 4, 0, this.chunkZ << 4);
    }
    
    public boolean canBeReferenced() {
        return this.references < this.getMaxReferences();
    }
    
    public void addReference() {
        ++this.references;
    }
    
    protected int getMaxReferences() {
        return 1;
    }
    
    public StructureFeature<?> getFeature() {
        return this.feature;
    }
    
    static {
        INVALID_START = new StructureStart(Feature.MINESHAFT, 0, 0, Biomes.PLAINS, BoundingBox.getUnknownBox(), 0, 0L) {
            @Override
            public void generatePieces(final ChunkGenerator<?> bxi, final StructureManager cjp, final int integer3, final int integer4, final Biome bio) {
            }
        };
    }
}
