package net.minecraft.world.level.block;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;

public class SoundType {
    public static final SoundType WOOD;
    public static final SoundType GRAVEL;
    public static final SoundType GRASS;
    public static final SoundType STONE;
    public static final SoundType METAL;
    public static final SoundType GLASS;
    public static final SoundType WOOL;
    public static final SoundType SAND;
    public static final SoundType SNOW;
    public static final SoundType LADDER;
    public static final SoundType ANVIL;
    public static final SoundType SLIME_BLOCK;
    public static final SoundType WET_GRASS;
    public static final SoundType CORAL_BLOCK;
    public static final SoundType BAMBOO;
    public static final SoundType BAMBOO_SAPLING;
    public static final SoundType SCAFFOLDING;
    public static final SoundType SWEET_BERRY_BUSH;
    public static final SoundType CROP;
    public static final SoundType HARD_CROP;
    public static final SoundType NETHER_WART;
    public static final SoundType LANTERN;
    public final float volume;
    public final float pitch;
    private final SoundEvent breakSound;
    private final SoundEvent stepSound;
    private final SoundEvent placeSound;
    private final SoundEvent hitSound;
    private final SoundEvent fallSound;
    
    public SoundType(final float float1, final float float2, final SoundEvent yo3, final SoundEvent yo4, final SoundEvent yo5, final SoundEvent yo6, final SoundEvent yo7) {
        this.volume = float1;
        this.pitch = float2;
        this.breakSound = yo3;
        this.stepSound = yo4;
        this.placeSound = yo5;
        this.hitSound = yo6;
        this.fallSound = yo7;
    }
    
    public float getVolume() {
        return this.volume;
    }
    
    public float getPitch() {
        return this.pitch;
    }
    
    public SoundEvent getBreakSound() {
        return this.breakSound;
    }
    
    public SoundEvent getStepSound() {
        return this.stepSound;
    }
    
    public SoundEvent getPlaceSound() {
        return this.placeSound;
    }
    
    public SoundEvent getHitSound() {
        return this.hitSound;
    }
    
    public SoundEvent getFallSound() {
        return this.fallSound;
    }
    
    static {
        WOOD = new SoundType(1.0f, 1.0f, SoundEvents.WOOD_BREAK, SoundEvents.WOOD_STEP, SoundEvents.WOOD_PLACE, SoundEvents.WOOD_HIT, SoundEvents.WOOD_FALL);
        GRAVEL = new SoundType(1.0f, 1.0f, SoundEvents.GRAVEL_BREAK, SoundEvents.GRAVEL_STEP, SoundEvents.GRAVEL_PLACE, SoundEvents.GRAVEL_HIT, SoundEvents.GRAVEL_FALL);
        GRASS = new SoundType(1.0f, 1.0f, SoundEvents.GRASS_BREAK, SoundEvents.GRASS_STEP, SoundEvents.GRASS_PLACE, SoundEvents.GRASS_HIT, SoundEvents.GRASS_FALL);
        STONE = new SoundType(1.0f, 1.0f, SoundEvents.STONE_BREAK, SoundEvents.STONE_STEP, SoundEvents.STONE_PLACE, SoundEvents.STONE_HIT, SoundEvents.STONE_FALL);
        METAL = new SoundType(1.0f, 1.5f, SoundEvents.METAL_BREAK, SoundEvents.METAL_STEP, SoundEvents.METAL_PLACE, SoundEvents.METAL_HIT, SoundEvents.METAL_FALL);
        GLASS = new SoundType(1.0f, 1.0f, SoundEvents.GLASS_BREAK, SoundEvents.GLASS_STEP, SoundEvents.GLASS_PLACE, SoundEvents.GLASS_HIT, SoundEvents.GLASS_FALL);
        WOOL = new SoundType(1.0f, 1.0f, SoundEvents.WOOL_BREAK, SoundEvents.WOOL_STEP, SoundEvents.WOOL_PLACE, SoundEvents.WOOL_HIT, SoundEvents.WOOL_FALL);
        SAND = new SoundType(1.0f, 1.0f, SoundEvents.SAND_BREAK, SoundEvents.SAND_STEP, SoundEvents.SAND_PLACE, SoundEvents.SAND_HIT, SoundEvents.SAND_FALL);
        SNOW = new SoundType(1.0f, 1.0f, SoundEvents.SNOW_BREAK, SoundEvents.SNOW_STEP, SoundEvents.SNOW_PLACE, SoundEvents.SNOW_HIT, SoundEvents.SNOW_FALL);
        LADDER = new SoundType(1.0f, 1.0f, SoundEvents.LADDER_BREAK, SoundEvents.LADDER_STEP, SoundEvents.LADDER_PLACE, SoundEvents.LADDER_HIT, SoundEvents.LADDER_FALL);
        ANVIL = new SoundType(0.3f, 1.0f, SoundEvents.ANVIL_BREAK, SoundEvents.ANVIL_STEP, SoundEvents.ANVIL_PLACE, SoundEvents.ANVIL_HIT, SoundEvents.ANVIL_FALL);
        SLIME_BLOCK = new SoundType(1.0f, 1.0f, SoundEvents.SLIME_BLOCK_BREAK, SoundEvents.SLIME_BLOCK_STEP, SoundEvents.SLIME_BLOCK_PLACE, SoundEvents.SLIME_BLOCK_HIT, SoundEvents.SLIME_BLOCK_FALL);
        WET_GRASS = new SoundType(1.0f, 1.0f, SoundEvents.WET_GRASS_BREAK, SoundEvents.WET_GRASS_STEP, SoundEvents.WET_GRASS_PLACE, SoundEvents.WET_GRASS_HIT, SoundEvents.WET_GRASS_FALL);
        CORAL_BLOCK = new SoundType(1.0f, 1.0f, SoundEvents.CORAL_BLOCK_BREAK, SoundEvents.CORAL_BLOCK_STEP, SoundEvents.CORAL_BLOCK_PLACE, SoundEvents.CORAL_BLOCK_HIT, SoundEvents.CORAL_BLOCK_FALL);
        BAMBOO = new SoundType(1.0f, 1.0f, SoundEvents.BAMBOO_BREAK, SoundEvents.BAMBOO_STEP, SoundEvents.BAMBOO_PLACE, SoundEvents.BAMBOO_HIT, SoundEvents.BAMBOO_FALL);
        BAMBOO_SAPLING = new SoundType(1.0f, 1.0f, SoundEvents.BAMBOO_SAPLING_BREAK, SoundEvents.BAMBOO_STEP, SoundEvents.BAMBOO_SAPLING_PLACE, SoundEvents.BAMBOO_SAPLING_HIT, SoundEvents.BAMBOO_FALL);
        SCAFFOLDING = new SoundType(1.0f, 1.0f, SoundEvents.SCAFFOLDING_BREAK, SoundEvents.SCAFFOLDING_STEP, SoundEvents.SCAFFOLDING_PLACE, SoundEvents.SCAFFOLDING_HIT, SoundEvents.SCAFFOLDING_FALL);
        SWEET_BERRY_BUSH = new SoundType(1.0f, 1.0f, SoundEvents.SWEET_BERRY_BUSH_BREAK, SoundEvents.GRASS_STEP, SoundEvents.SWEET_BERRY_BUSH_PLACE, SoundEvents.GRASS_HIT, SoundEvents.GRASS_FALL);
        CROP = new SoundType(1.0f, 1.0f, SoundEvents.CROP_BREAK, SoundEvents.GRASS_STEP, SoundEvents.CROP_PLANTED, SoundEvents.GRASS_HIT, SoundEvents.GRASS_FALL);
        HARD_CROP = new SoundType(1.0f, 1.0f, SoundEvents.WOOD_BREAK, SoundEvents.WOOD_STEP, SoundEvents.CROP_PLANTED, SoundEvents.WOOD_HIT, SoundEvents.WOOD_FALL);
        NETHER_WART = new SoundType(1.0f, 1.0f, SoundEvents.NETHER_WART_BREAK, SoundEvents.STONE_STEP, SoundEvents.NETHER_WART_PLANTED, SoundEvents.STONE_HIT, SoundEvents.STONE_FALL);
        LANTERN = new SoundType(1.0f, 1.0f, SoundEvents.LANTERN_BREAK, SoundEvents.LANTERN_STEP, SoundEvents.LANTERN_PLACE, SoundEvents.LANTERN_HIT, SoundEvents.LANTERN_FALL);
    }
}
