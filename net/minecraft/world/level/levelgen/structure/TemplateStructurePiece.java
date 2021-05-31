package net.minecraft.world.level.levelgen.structure;

import org.apache.logging.log4j.LogManager;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Iterator;
import java.util.List;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import com.mojang.brigadier.StringReader;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.ChunkPos;
import java.util.Random;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.logging.log4j.Logger;

public abstract class TemplateStructurePiece extends StructurePiece {
    private static final Logger LOGGER;
    protected StructureTemplate template;
    protected StructurePlaceSettings placeSettings;
    protected BlockPos templatePosition;
    
    public TemplateStructurePiece(final StructurePieceType cev, final int integer) {
        super(cev, integer);
    }
    
    public TemplateStructurePiece(final StructurePieceType cev, final CompoundTag id) {
        super(cev, id);
        this.templatePosition = new BlockPos(id.getInt("TPX"), id.getInt("TPY"), id.getInt("TPZ"));
    }
    
    protected void setup(final StructureTemplate cjt, final BlockPos ew, final StructurePlaceSettings cjq) {
        this.template = cjt;
        this.setOrientation(Direction.NORTH);
        this.templatePosition = ew;
        this.placeSettings = cjq;
        this.boundingBox = cjt.getBoundingBox(cjq, ew);
    }
    
    @Override
    protected void addAdditionalSaveData(final CompoundTag id) {
        id.putInt("TPX", this.templatePosition.getX());
        id.putInt("TPY", this.templatePosition.getY());
        id.putInt("TPZ", this.templatePosition.getZ());
    }
    
    @Override
    public boolean postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
        this.placeSettings.setBoundingBox(cic);
        this.boundingBox = this.template.getBoundingBox(this.placeSettings, this.templatePosition);
        if (this.template.placeInWorld(bhs, this.templatePosition, this.placeSettings, 2)) {
            final List<StructureTemplate.StructureBlockInfo> list6 = this.template.filterBlocks(this.templatePosition, this.placeSettings, Blocks.STRUCTURE_BLOCK);
            for (final StructureTemplate.StructureBlockInfo b8 : list6) {
                if (b8.nbt == null) {
                    continue;
                }
                final StructureMode bxb9 = StructureMode.valueOf(b8.nbt.getString("mode"));
                if (bxb9 != StructureMode.DATA) {
                    continue;
                }
                this.handleDataMarker(b8.nbt.getString("metadata"), b8.pos, bhs, random, cic);
            }
            final List<StructureTemplate.StructureBlockInfo> list7 = this.template.filterBlocks(this.templatePosition, this.placeSettings, Blocks.JIGSAW_BLOCK);
            for (final StructureTemplate.StructureBlockInfo b9 : list7) {
                if (b9.nbt == null) {
                    continue;
                }
                final String string10 = b9.nbt.getString("final_state");
                final BlockStateParser dh11 = new BlockStateParser(new StringReader(string10), false);
                BlockState bvt12 = Blocks.AIR.defaultBlockState();
                try {
                    dh11.parse(true);
                    final BlockState bvt13 = dh11.getState();
                    if (bvt13 != null) {
                        bvt12 = bvt13;
                    }
                    else {
                        TemplateStructurePiece.LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", string10, b9.pos);
                    }
                }
                catch (CommandSyntaxException commandSyntaxException13) {
                    TemplateStructurePiece.LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", string10, b9.pos);
                }
                bhs.setBlock(b9.pos, bvt12, 3);
            }
        }
        return true;
    }
    
    protected abstract void handleDataMarker(final String string, final BlockPos ew, final LevelAccessor bhs, final Random random, final BoundingBox cic);
    
    @Override
    public void move(final int integer1, final int integer2, final int integer3) {
        super.move(integer1, integer2, integer3);
        this.templatePosition = this.templatePosition.offset(integer1, integer2, integer3);
    }
    
    @Override
    public Rotation getRotation() {
        return this.placeSettings.getRotation();
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
