package net.minecraft.world.level.block.entity;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import java.util.Objects;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.Util;
import java.util.Random;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.ResourceLocationException;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Vec3i;
import java.util.Iterator;
import com.google.common.collect.Lists;
import java.util.stream.Collectors;
import java.util.function.Predicate;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Player;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.StructureBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.util.Mth;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class StructureBlockEntity extends BlockEntity {
    private ResourceLocation structureName;
    private String author;
    private String metaData;
    private BlockPos structurePos;
    private BlockPos structureSize;
    private Mirror mirror;
    private Rotation rotation;
    private StructureMode mode;
    private boolean ignoreEntities;
    private boolean powered;
    private boolean showAir;
    private boolean showBoundingBox;
    private float integrity;
    private long seed;
    
    public StructureBlockEntity() {
        super(BlockEntityType.STRUCTURE_BLOCK);
        this.author = "";
        this.metaData = "";
        this.structurePos = new BlockPos(0, 1, 0);
        this.structureSize = BlockPos.ZERO;
        this.mirror = Mirror.NONE;
        this.rotation = Rotation.NONE;
        this.mode = StructureMode.DATA;
        this.ignoreEntities = true;
        this.showBoundingBox = true;
        this.integrity = 1.0f;
    }
    
    @Override
    public CompoundTag save(final CompoundTag id) {
        super.save(id);
        id.putString("name", this.getStructureName());
        id.putString("author", this.author);
        id.putString("metadata", this.metaData);
        id.putInt("posX", this.structurePos.getX());
        id.putInt("posY", this.structurePos.getY());
        id.putInt("posZ", this.structurePos.getZ());
        id.putInt("sizeX", this.structureSize.getX());
        id.putInt("sizeY", this.structureSize.getY());
        id.putInt("sizeZ", this.structureSize.getZ());
        id.putString("rotation", this.rotation.toString());
        id.putString("mirror", this.mirror.toString());
        id.putString("mode", this.mode.toString());
        id.putBoolean("ignoreEntities", this.ignoreEntities);
        id.putBoolean("powered", this.powered);
        id.putBoolean("showair", this.showAir);
        id.putBoolean("showboundingbox", this.showBoundingBox);
        id.putFloat("integrity", this.integrity);
        id.putLong("seed", this.seed);
        return id;
    }
    
    @Override
    public void load(final CompoundTag id) {
        super.load(id);
        this.setStructureName(id.getString("name"));
        this.author = id.getString("author");
        this.metaData = id.getString("metadata");
        final int integer3 = Mth.clamp(id.getInt("posX"), -32, 32);
        final int integer4 = Mth.clamp(id.getInt("posY"), -32, 32);
        final int integer5 = Mth.clamp(id.getInt("posZ"), -32, 32);
        this.structurePos = new BlockPos(integer3, integer4, integer5);
        final int integer6 = Mth.clamp(id.getInt("sizeX"), 0, 32);
        final int integer7 = Mth.clamp(id.getInt("sizeY"), 0, 32);
        final int integer8 = Mth.clamp(id.getInt("sizeZ"), 0, 32);
        this.structureSize = new BlockPos(integer6, integer7, integer8);
        try {
            this.rotation = Rotation.valueOf(id.getString("rotation"));
        }
        catch (IllegalArgumentException illegalArgumentException9) {
            this.rotation = Rotation.NONE;
        }
        try {
            this.mirror = Mirror.valueOf(id.getString("mirror"));
        }
        catch (IllegalArgumentException illegalArgumentException9) {
            this.mirror = Mirror.NONE;
        }
        try {
            this.mode = StructureMode.valueOf(id.getString("mode"));
        }
        catch (IllegalArgumentException illegalArgumentException9) {
            this.mode = StructureMode.DATA;
        }
        this.ignoreEntities = id.getBoolean("ignoreEntities");
        this.powered = id.getBoolean("powered");
        this.showAir = id.getBoolean("showair");
        this.showBoundingBox = id.getBoolean("showboundingbox");
        if (id.contains("integrity")) {
            this.integrity = id.getFloat("integrity");
        }
        else {
            this.integrity = 1.0f;
        }
        this.seed = id.getLong("seed");
        this.updateBlockState();
    }
    
    private void updateBlockState() {
        if (this.level == null) {
            return;
        }
        final BlockPos ew2 = this.getBlockPos();
        final BlockState bvt3 = this.level.getBlockState(ew2);
        if (bvt3.getBlock() == Blocks.STRUCTURE_BLOCK) {
            this.level.setBlock(ew2, ((AbstractStateHolder<O, BlockState>)bvt3).<StructureMode, StructureMode>setValue(StructureBlock.MODE, this.mode), 2);
        }
    }
    
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 7, this.getUpdateTag());
    }
    
    @Override
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }
    
    public boolean usedBy(final Player awg) {
        if (!awg.canUseGameMasterBlocks()) {
            return false;
        }
        if (awg.getCommandSenderWorld().isClientSide) {
            awg.openStructureBlock(this);
        }
        return true;
    }
    
    public String getStructureName() {
        return (this.structureName == null) ? "" : this.structureName.toString();
    }
    
    public boolean hasStructureName() {
        return this.structureName != null;
    }
    
    public void setStructureName(@Nullable final String string) {
        this.setStructureName(StringUtil.isNullOrEmpty(string) ? null : ResourceLocation.tryParse(string));
    }
    
    public void setStructureName(@Nullable final ResourceLocation qv) {
        this.structureName = qv;
    }
    
    public void createdBy(final LivingEntity aix) {
        this.author = aix.getName().getString();
    }
    
    public BlockPos getStructurePos() {
        return this.structurePos;
    }
    
    public void setStructurePos(final BlockPos ew) {
        this.structurePos = ew;
    }
    
    public BlockPos getStructureSize() {
        return this.structureSize;
    }
    
    public void setStructureSize(final BlockPos ew) {
        this.structureSize = ew;
    }
    
    public Mirror getMirror() {
        return this.mirror;
    }
    
    public void setMirror(final Mirror bqg) {
        this.mirror = bqg;
    }
    
    public Rotation getRotation() {
        return this.rotation;
    }
    
    public void setRotation(final Rotation brg) {
        this.rotation = brg;
    }
    
    public String getMetaData() {
        return this.metaData;
    }
    
    public void setMetaData(final String string) {
        this.metaData = string;
    }
    
    public StructureMode getMode() {
        return this.mode;
    }
    
    public void setMode(final StructureMode bxb) {
        this.mode = bxb;
        final BlockState bvt3 = this.level.getBlockState(this.getBlockPos());
        if (bvt3.getBlock() == Blocks.STRUCTURE_BLOCK) {
            this.level.setBlock(this.getBlockPos(), ((AbstractStateHolder<O, BlockState>)bvt3).<StructureMode, StructureMode>setValue(StructureBlock.MODE, bxb), 2);
        }
    }
    
    public void nextMode() {
        switch (this.getMode()) {
            case SAVE: {
                this.setMode(StructureMode.LOAD);
                break;
            }
            case LOAD: {
                this.setMode(StructureMode.CORNER);
                break;
            }
            case CORNER: {
                this.setMode(StructureMode.DATA);
                break;
            }
            case DATA: {
                this.setMode(StructureMode.SAVE);
                break;
            }
        }
    }
    
    public boolean isIgnoreEntities() {
        return this.ignoreEntities;
    }
    
    public void setIgnoreEntities(final boolean boolean1) {
        this.ignoreEntities = boolean1;
    }
    
    public float getIntegrity() {
        return this.integrity;
    }
    
    public void setIntegrity(final float float1) {
        this.integrity = float1;
    }
    
    public long getSeed() {
        return this.seed;
    }
    
    public void setSeed(final long long1) {
        this.seed = long1;
    }
    
    public boolean detectSize() {
        if (this.mode != StructureMode.SAVE) {
            return false;
        }
        final BlockPos ew2 = this.getBlockPos();
        final int integer3 = 80;
        final BlockPos ew3 = new BlockPos(ew2.getX() - 80, 0, ew2.getZ() - 80);
        final BlockPos ew4 = new BlockPos(ew2.getX() + 80, 255, ew2.getZ() + 80);
        final List<StructureBlockEntity> list6 = this.getNearbyCornerBlocks(ew3, ew4);
        final List<StructureBlockEntity> list7 = this.filterRelatedCornerBlocks(list6);
        if (list7.size() < 1) {
            return false;
        }
        final BoundingBox cic8 = this.calculateEnclosingBoundingBox(ew2, list7);
        if (cic8.x1 - cic8.x0 > 1 && cic8.y1 - cic8.y0 > 1 && cic8.z1 - cic8.z0 > 1) {
            this.structurePos = new BlockPos(cic8.x0 - ew2.getX() + 1, cic8.y0 - ew2.getY() + 1, cic8.z0 - ew2.getZ() + 1);
            this.structureSize = new BlockPos(cic8.x1 - cic8.x0 - 1, cic8.y1 - cic8.y0 - 1, cic8.z1 - cic8.z0 - 1);
            this.setChanged();
            final BlockState bvt9 = this.level.getBlockState(ew2);
            this.level.sendBlockUpdated(ew2, bvt9, bvt9, 3);
            return true;
        }
        return false;
    }
    
    private List<StructureBlockEntity> filterRelatedCornerBlocks(final List<StructureBlockEntity> list) {
        final Predicate<StructureBlockEntity> predicate3 = (Predicate<StructureBlockEntity>)(buw -> buw.mode == StructureMode.CORNER && Objects.equals(this.structureName, buw.structureName));
        return (List<StructureBlockEntity>)list.stream().filter((Predicate)predicate3).collect(Collectors.toList());
    }
    
    private List<StructureBlockEntity> getNearbyCornerBlocks(final BlockPos ew1, final BlockPos ew2) {
        final List<StructureBlockEntity> list4 = (List<StructureBlockEntity>)Lists.newArrayList();
        for (final BlockPos ew3 : BlockPos.betweenClosed(ew1, ew2)) {
            final BlockState bvt7 = this.level.getBlockState(ew3);
            if (bvt7.getBlock() != Blocks.STRUCTURE_BLOCK) {
                continue;
            }
            final BlockEntity btw8 = this.level.getBlockEntity(ew3);
            if (btw8 == null || !(btw8 instanceof StructureBlockEntity)) {
                continue;
            }
            list4.add(btw8);
        }
        return list4;
    }
    
    private BoundingBox calculateEnclosingBoundingBox(final BlockPos ew, final List<StructureBlockEntity> list) {
        BoundingBox cic4;
        if (list.size() > 1) {
            final BlockPos ew2 = ((StructureBlockEntity)list.get(0)).getBlockPos();
            cic4 = new BoundingBox(ew2, ew2);
        }
        else {
            cic4 = new BoundingBox(ew, ew);
        }
        for (final StructureBlockEntity buw6 : list) {
            final BlockPos ew3 = buw6.getBlockPos();
            if (ew3.getX() < cic4.x0) {
                cic4.x0 = ew3.getX();
            }
            else if (ew3.getX() > cic4.x1) {
                cic4.x1 = ew3.getX();
            }
            if (ew3.getY() < cic4.y0) {
                cic4.y0 = ew3.getY();
            }
            else if (ew3.getY() > cic4.y1) {
                cic4.y1 = ew3.getY();
            }
            if (ew3.getZ() < cic4.z0) {
                cic4.z0 = ew3.getZ();
            }
            else {
                if (ew3.getZ() <= cic4.z1) {
                    continue;
                }
                cic4.z1 = ew3.getZ();
            }
        }
        return cic4;
    }
    
    public boolean saveStructure() {
        return this.saveStructure(true);
    }
    
    public boolean saveStructure(final boolean boolean1) {
        if (this.mode != StructureMode.SAVE || this.level.isClientSide || this.structureName == null) {
            return false;
        }
        final BlockPos ew3 = this.getBlockPos().offset(this.structurePos);
        final ServerLevel vk4 = (ServerLevel)this.level;
        final StructureManager cjp5 = vk4.getStructureManager();
        StructureTemplate cjt6;
        try {
            cjt6 = cjp5.getOrCreate(this.structureName);
        }
        catch (ResourceLocationException n7) {
            return false;
        }
        cjt6.fillFromWorld(this.level, ew3, this.structureSize, !this.ignoreEntities, Blocks.STRUCTURE_VOID);
        cjt6.setAuthor(this.author);
        if (boolean1) {
            try {
                return cjp5.save(this.structureName);
            }
            catch (ResourceLocationException n7) {
                return false;
            }
        }
        return true;
    }
    
    public boolean loadStructure() {
        return this.loadStructure(true);
    }
    
    private static Random createRandom(final long long1) {
        if (long1 == 0L) {
            return new Random(Util.getMillis());
        }
        return new Random(long1);
    }
    
    public boolean loadStructure(final boolean boolean1) {
        if (this.mode != StructureMode.LOAD || this.level.isClientSide || this.structureName == null) {
            return false;
        }
        final BlockPos ew3 = this.getBlockPos();
        final BlockPos ew4 = ew3.offset(this.structurePos);
        final ServerLevel vk5 = (ServerLevel)this.level;
        final StructureManager cjp6 = vk5.getStructureManager();
        StructureTemplate cjt7;
        try {
            cjt7 = cjp6.get(this.structureName);
        }
        catch (ResourceLocationException n8) {
            return false;
        }
        if (cjt7 == null) {
            return false;
        }
        if (!StringUtil.isNullOrEmpty(cjt7.getAuthor())) {
            this.author = cjt7.getAuthor();
        }
        final BlockPos ew5 = cjt7.getSize();
        final boolean boolean2 = this.structureSize.equals(ew5);
        if (!boolean2) {
            this.structureSize = ew5;
            this.setChanged();
            final BlockState bvt10 = this.level.getBlockState(ew3);
            this.level.sendBlockUpdated(ew3, bvt10, bvt10, 3);
        }
        if (!boolean1 || boolean2) {
            final StructurePlaceSettings cjq10 = new StructurePlaceSettings().setMirror(this.mirror).setRotation(this.rotation).setIgnoreEntities(this.ignoreEntities).setChunkPos(null);
            if (this.integrity < 1.0f) {
                cjq10.clearProcessors().addProcessor(new BlockRotProcessor(Mth.clamp(this.integrity, 0.0f, 1.0f))).setRandom(createRandom(this.seed));
            }
            cjt7.placeInWorldChunk(this.level, ew4, cjq10);
            return true;
        }
        return false;
    }
    
    public void unloadStructure() {
        if (this.structureName == null) {
            return;
        }
        final ServerLevel vk2 = (ServerLevel)this.level;
        final StructureManager cjp3 = vk2.getStructureManager();
        cjp3.remove(this.structureName);
    }
    
    public boolean isStructureLoadable() {
        if (this.mode != StructureMode.LOAD || this.level.isClientSide || this.structureName == null) {
            return false;
        }
        final ServerLevel vk2 = (ServerLevel)this.level;
        final StructureManager cjp3 = vk2.getStructureManager();
        try {
            return cjp3.get(this.structureName) != null;
        }
        catch (ResourceLocationException n4) {
            return false;
        }
    }
    
    public boolean isPowered() {
        return this.powered;
    }
    
    public void setPowered(final boolean boolean1) {
        this.powered = boolean1;
    }
    
    public boolean getShowAir() {
        return this.showAir;
    }
    
    public void setShowAir(final boolean boolean1) {
        this.showAir = boolean1;
    }
    
    public boolean getShowBoundingBox() {
        return this.showBoundingBox;
    }
    
    public void setShowBoundingBox(final boolean boolean1) {
        this.showBoundingBox = boolean1;
    }
    
    public enum UpdateType {
        UPDATE_DATA, 
        SAVE_AREA, 
        LOAD_AREA, 
        SCAN_AREA;
    }
}
