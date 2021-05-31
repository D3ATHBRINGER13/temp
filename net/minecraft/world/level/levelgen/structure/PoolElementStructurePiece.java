package net.minecraft.world.level.levelgen.structure;

import net.minecraft.world.level.ChunkPos;
import java.util.Random;
import net.minecraft.world.level.LevelAccessor;
import java.util.Iterator;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElementType;
import net.minecraft.util.Deserializer;
import net.minecraft.core.Registry;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.Dynamic;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.levelgen.feature.structures.EmptyPoolElement;
import net.minecraft.nbt.CompoundTag;
import com.google.common.collect.Lists;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.feature.structures.JigsawJunction;
import java.util.List;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;

public abstract class PoolElementStructurePiece extends StructurePiece {
    protected final StructurePoolElement element;
    protected BlockPos position;
    private final int groundLevelDelta;
    protected final Rotation rotation;
    private final List<JigsawJunction> junctions;
    private final StructureManager structureManager;
    
    public PoolElementStructurePiece(final StructurePieceType cev, final StructureManager cjp, final StructurePoolElement cfr, final BlockPos ew, final int integer, final Rotation brg, final BoundingBox cic) {
        super(cev, 0);
        this.junctions = (List<JigsawJunction>)Lists.newArrayList();
        this.structureManager = cjp;
        this.element = cfr;
        this.position = ew;
        this.groundLevelDelta = integer;
        this.rotation = brg;
        this.boundingBox = cic;
    }
    
    public PoolElementStructurePiece(final StructureManager cjp, final CompoundTag id, final StructurePieceType cev) {
        super(cev, id);
        this.junctions = (List<JigsawJunction>)Lists.newArrayList();
        this.structureManager = cjp;
        this.position = new BlockPos(id.getInt("PosX"), id.getInt("PosY"), id.getInt("PosZ"));
        this.groundLevelDelta = id.getInt("ground_level_delta");
        this.element = Deserializer.<Object, EmptyPoolElement, StructurePoolElementType>deserialize((com.mojang.datafixers.Dynamic<Object>)new Dynamic((DynamicOps)NbtOps.INSTANCE, id.getCompound("pool_element")), Registry.STRUCTURE_POOL_ELEMENT, "element_type", EmptyPoolElement.INSTANCE);
        this.rotation = Rotation.valueOf(id.getString("rotation"));
        this.boundingBox = this.element.getBoundingBox(cjp, this.position, this.rotation);
        final ListTag ik5 = id.getList("junctions", 10);
        this.junctions.clear();
        ik5.forEach(iu -> this.junctions.add(JigsawJunction.deserialize((com.mojang.datafixers.Dynamic<Object>)new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)iu))));
    }
    
    @Override
    protected void addAdditionalSaveData(final CompoundTag id) {
        id.putInt("PosX", this.position.getX());
        id.putInt("PosY", this.position.getY());
        id.putInt("PosZ", this.position.getZ());
        id.putInt("ground_level_delta", this.groundLevelDelta);
        id.put("pool_element", (Tag)this.element.serialize((com.mojang.datafixers.types.DynamicOps<Object>)NbtOps.INSTANCE).getValue());
        id.putString("rotation", this.rotation.name());
        final ListTag ik3 = new ListTag();
        for (final JigsawJunction cfn5 : this.junctions) {
            ik3.add(cfn5.serialize((com.mojang.datafixers.types.DynamicOps<Object>)NbtOps.INSTANCE).getValue());
        }
        id.put("junctions", (Tag)ik3);
    }
    
    @Override
    public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
        return this.element.place(this.structureManager, bhs, this.position, this.rotation, cic, random);
    }
    
    @Override
    public void move(final int integer1, final int integer2, final int integer3) {
        super.move(integer1, integer2, integer3);
        this.position = this.position.offset(integer1, integer2, integer3);
    }
    
    @Override
    public Rotation getRotation() {
        return this.rotation;
    }
    
    public String toString() {
        return String.format("<%s | %s | %s | %s>", new Object[] { this.getClass().getSimpleName(), this.position, this.rotation, this.element });
    }
    
    public StructurePoolElement getElement() {
        return this.element;
    }
    
    public BlockPos getPosition() {
        return this.position;
    }
    
    public int getGroundLevelDelta() {
        return this.groundLevelDelta;
    }
    
    public void addJunction(final JigsawJunction cfn) {
        this.junctions.add(cfn);
    }
    
    public List<JigsawJunction> getJunctions() {
        return this.junctions;
    }
}
