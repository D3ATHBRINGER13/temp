package net.minecraft.world.entity.animal;

import java.util.Locale;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import javax.annotation.Nullable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.EntityDataAccessor;

public class TropicalFish extends AbstractSchoolingFish {
    private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT;
    private static final ResourceLocation[] BASE_TEXTURE_LOCATIONS;
    private static final ResourceLocation[] PATTERN_A_TEXTURE_LOCATIONS;
    private static final ResourceLocation[] PATTERN_B_TEXTURE_LOCATIONS;
    public static final int[] COMMON_VARIANTS;
    private boolean isSchool;
    
    private static int calculateVariant(final Pattern a, final DyeColor bbg2, final DyeColor bbg3) {
        return (a.getBase() & 0xFF) | (a.getIndex() & 0xFF) << 8 | (bbg2.getId() & 0xFF) << 16 | (bbg3.getId() & 0xFF) << 24;
    }
    
    public TropicalFish(final EntityType<? extends TropicalFish> ais, final Level bhr) {
        super(ais, bhr);
        this.isSchool = true;
    }
    
    public static String getPredefinedName(final int integer) {
        return new StringBuilder().append("entity.minecraft.tropical_fish.predefined.").append(integer).toString();
    }
    
    public static DyeColor getBaseColor(final int integer) {
        return DyeColor.byId(getBaseColorIdx(integer));
    }
    
    public static DyeColor getPatternColor(final int integer) {
        return DyeColor.byId(getPatternColorIdx(integer));
    }
    
    public static String getFishTypeName(final int integer) {
        final int integer2 = getBaseVariant(integer);
        final int integer3 = getPatternVariant(integer);
        return "entity.minecraft.tropical_fish.type." + Pattern.getPatternName(integer2, integer3);
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Integer>define(TropicalFish.DATA_ID_TYPE_VARIANT, 0);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putInt("Variant", this.getVariant());
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        this.setVariant(id.getInt("Variant"));
    }
    
    public void setVariant(final int integer) {
        this.entityData.<Integer>set(TropicalFish.DATA_ID_TYPE_VARIANT, integer);
    }
    
    @Override
    public boolean isMaxGroupSizeReached(final int integer) {
        return !this.isSchool;
    }
    
    public int getVariant() {
        return this.entityData.<Integer>get(TropicalFish.DATA_ID_TYPE_VARIANT);
    }
    
    @Override
    protected void saveToBucketTag(final ItemStack bcj) {
        super.saveToBucketTag(bcj);
        final CompoundTag id3 = bcj.getOrCreateTag();
        id3.putInt("BucketVariantTag", this.getVariant());
    }
    
    @Override
    protected ItemStack getBucketItemStack() {
        return new ItemStack(Items.TROPICAL_FISH_BUCKET);
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.TROPICAL_FISH_AMBIENT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.TROPICAL_FISH_DEATH;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.TROPICAL_FISH_HURT;
    }
    
    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.TROPICAL_FISH_FLOP;
    }
    
    private static int getBaseColorIdx(final int integer) {
        return (integer & 0xFF0000) >> 16;
    }
    
    public float[] getBaseColor() {
        return DyeColor.byId(getBaseColorIdx(this.getVariant())).getTextureDiffuseColors();
    }
    
    private static int getPatternColorIdx(final int integer) {
        return (integer & 0xFF000000) >> 24;
    }
    
    public float[] getPatternColor() {
        return DyeColor.byId(getPatternColorIdx(this.getVariant())).getTextureDiffuseColors();
    }
    
    public static int getBaseVariant(final int integer) {
        return Math.min(integer & 0xFF, 1);
    }
    
    public int getBaseVariant() {
        return getBaseVariant(this.getVariant());
    }
    
    private static int getPatternVariant(final int integer) {
        return Math.min((integer & 0xFF00) >> 8, 5);
    }
    
    public ResourceLocation getPatternTextureLocation() {
        if (getBaseVariant(this.getVariant()) == 0) {
            return TropicalFish.PATTERN_A_TEXTURE_LOCATIONS[getPatternVariant(this.getVariant())];
        }
        return TropicalFish.PATTERN_B_TEXTURE_LOCATIONS[getPatternVariant(this.getVariant())];
    }
    
    public ResourceLocation getBaseTextureLocation() {
        return TropicalFish.BASE_TEXTURE_LOCATIONS[getBaseVariant(this.getVariant())];
    }
    
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(final LevelAccessor bhs, final DifficultyInstance ahh, final MobSpawnType aja, @Nullable SpawnGroupData ajj, @Nullable final CompoundTag id) {
        ajj = super.finalizeSpawn(bhs, ahh, aja, ajj, id);
        if (id != null && id.contains("BucketVariantTag", 3)) {
            this.setVariant(id.getInt("BucketVariantTag"));
            return ajj;
        }
        int integer7;
        int integer8;
        int integer9;
        int integer10;
        if (ajj instanceof TropicalFishGroupData) {
            final TropicalFishGroupData b11 = (TropicalFishGroupData)ajj;
            integer7 = b11.base;
            integer8 = b11.pattern;
            integer9 = b11.baseColor;
            integer10 = b11.patternColor;
        }
        else if (this.random.nextFloat() < 0.9) {
            final int integer11 = TropicalFish.COMMON_VARIANTS[this.random.nextInt(TropicalFish.COMMON_VARIANTS.length)];
            integer7 = (integer11 & 0xFF);
            integer8 = (integer11 & 0xFF00) >> 8;
            integer9 = (integer11 & 0xFF0000) >> 16;
            integer10 = (integer11 & 0xFF000000) >> 24;
            ajj = new TropicalFishGroupData(this, integer7, integer8, integer9, integer10);
        }
        else {
            this.isSchool = false;
            integer7 = this.random.nextInt(2);
            integer8 = this.random.nextInt(6);
            integer9 = this.random.nextInt(15);
            integer10 = this.random.nextInt(15);
        }
        this.setVariant(integer7 | integer8 << 8 | integer9 << 16 | integer10 << 24);
        return ajj;
    }
    
    static {
        DATA_ID_TYPE_VARIANT = SynchedEntityData.<Integer>defineId(TropicalFish.class, EntityDataSerializers.INT);
        BASE_TEXTURE_LOCATIONS = new ResourceLocation[] { new ResourceLocation("textures/entity/fish/tropical_a.png"), new ResourceLocation("textures/entity/fish/tropical_b.png") };
        PATTERN_A_TEXTURE_LOCATIONS = new ResourceLocation[] { new ResourceLocation("textures/entity/fish/tropical_a_pattern_1.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_2.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_3.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_4.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_5.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_6.png") };
        PATTERN_B_TEXTURE_LOCATIONS = new ResourceLocation[] { new ResourceLocation("textures/entity/fish/tropical_b_pattern_1.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_2.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_3.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_4.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_5.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_6.png") };
        COMMON_VARIANTS = new int[] { calculateVariant(Pattern.STRIPEY, DyeColor.ORANGE, DyeColor.GRAY), calculateVariant(Pattern.FLOPPER, DyeColor.GRAY, DyeColor.GRAY), calculateVariant(Pattern.FLOPPER, DyeColor.GRAY, DyeColor.BLUE), calculateVariant(Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.GRAY), calculateVariant(Pattern.SUNSTREAK, DyeColor.BLUE, DyeColor.GRAY), calculateVariant(Pattern.KOB, DyeColor.ORANGE, DyeColor.WHITE), calculateVariant(Pattern.SPOTTY, DyeColor.PINK, DyeColor.LIGHT_BLUE), calculateVariant(Pattern.BLOCKFISH, DyeColor.PURPLE, DyeColor.YELLOW), calculateVariant(Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.RED), calculateVariant(Pattern.SPOTTY, DyeColor.WHITE, DyeColor.YELLOW), calculateVariant(Pattern.GLITTER, DyeColor.WHITE, DyeColor.GRAY), calculateVariant(Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.ORANGE), calculateVariant(Pattern.DASHER, DyeColor.CYAN, DyeColor.PINK), calculateVariant(Pattern.BRINELY, DyeColor.LIME, DyeColor.LIGHT_BLUE), calculateVariant(Pattern.BETTY, DyeColor.RED, DyeColor.WHITE), calculateVariant(Pattern.SNOOPER, DyeColor.GRAY, DyeColor.RED), calculateVariant(Pattern.BLOCKFISH, DyeColor.RED, DyeColor.WHITE), calculateVariant(Pattern.FLOPPER, DyeColor.WHITE, DyeColor.YELLOW), calculateVariant(Pattern.KOB, DyeColor.RED, DyeColor.WHITE), calculateVariant(Pattern.SUNSTREAK, DyeColor.GRAY, DyeColor.WHITE), calculateVariant(Pattern.DASHER, DyeColor.CYAN, DyeColor.YELLOW), calculateVariant(Pattern.FLOPPER, DyeColor.YELLOW, DyeColor.YELLOW) };
    }
    
    enum Pattern {
        KOB(0, 0), 
        SUNSTREAK(0, 1), 
        SNOOPER(0, 2), 
        DASHER(0, 3), 
        BRINELY(0, 4), 
        SPOTTY(0, 5), 
        FLOPPER(1, 0), 
        STRIPEY(1, 1), 
        GLITTER(1, 2), 
        BLOCKFISH(1, 3), 
        BETTY(1, 4), 
        CLAYFISH(1, 5);
        
        private final int base;
        private final int index;
        private static final Pattern[] VALUES;
        
        private Pattern(final int integer3, final int integer4) {
            this.base = integer3;
            this.index = integer4;
        }
        
        public int getBase() {
            return this.base;
        }
        
        public int getIndex() {
            return this.index;
        }
        
        public static String getPatternName(final int integer1, final int integer2) {
            return Pattern.VALUES[integer2 + 6 * integer1].getName();
        }
        
        public String getName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
        
        static {
            VALUES = values();
        }
    }
    
    static class TropicalFishGroupData extends SchoolSpawnGroupData {
        private final int base;
        private final int pattern;
        private final int baseColor;
        private final int patternColor;
        
        private TropicalFishGroupData(final TropicalFish arw, final int integer2, final int integer3, final int integer4, final int integer5) {
            super(arw);
            this.base = integer2;
            this.pattern = integer3;
            this.baseColor = integer4;
            this.patternColor = integer5;
        }
    }
}
