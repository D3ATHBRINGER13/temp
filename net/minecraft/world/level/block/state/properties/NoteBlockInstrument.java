package net.minecraft.world.level.block.state.properties;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.StringRepresentable;

public enum NoteBlockInstrument implements StringRepresentable {
    HARP("harp", SoundEvents.NOTE_BLOCK_HARP), 
    BASEDRUM("basedrum", SoundEvents.NOTE_BLOCK_BASEDRUM), 
    SNARE("snare", SoundEvents.NOTE_BLOCK_SNARE), 
    HAT("hat", SoundEvents.NOTE_BLOCK_HAT), 
    BASS("bass", SoundEvents.NOTE_BLOCK_BASS), 
    FLUTE("flute", SoundEvents.NOTE_BLOCK_FLUTE), 
    BELL("bell", SoundEvents.NOTE_BLOCK_BELL), 
    GUITAR("guitar", SoundEvents.NOTE_BLOCK_GUITAR), 
    CHIME("chime", SoundEvents.NOTE_BLOCK_CHIME), 
    XYLOPHONE("xylophone", SoundEvents.NOTE_BLOCK_XYLOPHONE), 
    IRON_XYLOPHONE("iron_xylophone", SoundEvents.NOTE_BLOCK_IRON_XYLOPHONE), 
    COW_BELL("cow_bell", SoundEvents.NOTE_BLOCK_COW_BELL), 
    DIDGERIDOO("didgeridoo", SoundEvents.NOTE_BLOCK_DIDGERIDOO), 
    BIT("bit", SoundEvents.NOTE_BLOCK_BIT), 
    BANJO("banjo", SoundEvents.NOTE_BLOCK_BANJO), 
    PLING("pling", SoundEvents.NOTE_BLOCK_PLING);
    
    private final String name;
    private final SoundEvent soundEvent;
    
    private NoteBlockInstrument(final String string3, final SoundEvent yo) {
        this.name = string3;
        this.soundEvent = yo;
    }
    
    public String getSerializedName() {
        return this.name;
    }
    
    public SoundEvent getSoundEvent() {
        return this.soundEvent;
    }
    
    public static NoteBlockInstrument byState(final BlockState bvt) {
        final Block bmv2 = bvt.getBlock();
        if (bmv2 == Blocks.CLAY) {
            return NoteBlockInstrument.FLUTE;
        }
        if (bmv2 == Blocks.GOLD_BLOCK) {
            return NoteBlockInstrument.BELL;
        }
        if (bmv2.is(BlockTags.WOOL)) {
            return NoteBlockInstrument.GUITAR;
        }
        if (bmv2 == Blocks.PACKED_ICE) {
            return NoteBlockInstrument.CHIME;
        }
        if (bmv2 == Blocks.BONE_BLOCK) {
            return NoteBlockInstrument.XYLOPHONE;
        }
        if (bmv2 == Blocks.IRON_BLOCK) {
            return NoteBlockInstrument.IRON_XYLOPHONE;
        }
        if (bmv2 == Blocks.SOUL_SAND) {
            return NoteBlockInstrument.COW_BELL;
        }
        if (bmv2 == Blocks.PUMPKIN) {
            return NoteBlockInstrument.DIDGERIDOO;
        }
        if (bmv2 == Blocks.EMERALD_BLOCK) {
            return NoteBlockInstrument.BIT;
        }
        if (bmv2 == Blocks.HAY_BLOCK) {
            return NoteBlockInstrument.BANJO;
        }
        if (bmv2 == Blocks.GLOWSTONE) {
            return NoteBlockInstrument.PLING;
        }
        final Material clo3 = bvt.getMaterial();
        if (clo3 == Material.STONE) {
            return NoteBlockInstrument.BASEDRUM;
        }
        if (clo3 == Material.SAND) {
            return NoteBlockInstrument.SNARE;
        }
        if (clo3 == Material.GLASS) {
            return NoteBlockInstrument.HAT;
        }
        if (clo3 == Material.WOOD) {
            return NoteBlockInstrument.BASS;
        }
        return NoteBlockInstrument.HARP;
    }
}
